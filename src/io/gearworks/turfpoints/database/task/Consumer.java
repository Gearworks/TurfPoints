package io.gearworks.turfpoints.database.task;

import io.gearworks.turfpoints.TurfPoints;
import io.gearworks.turfpoints.database.queries.Query;
import io.gearworks.turfpoints.utilities.Messaging;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Consumer implements Runnable{

    private final Queue<Query> queue = new LinkedBlockingDeque ();

    private final Lock lock = new ReentrantLock ();

    public void queueQuery (Query pQuery) {
        this.queue.add (pQuery);
    }

    public int getQueueSize () {
        return this.queue.size ();
    }

    public void run () {
        if ((this.queue.isEmpty ()) || (!this.lock.tryLock ())) {
            return;
        }

        Connection connection = TurfPoints.getDatabaseManager ().getConnection ();
        Statement statement = null;

        if (this.queue.size () > 1000) {
            Messaging.info ("Queue overloaded. Size: " + this.queue.size (), new Object[0]);
        }

        try {
            if (connection == null) {
                return;
            }

            connection.setAutoCommit (false);

            statement = connection.createStatement ();

            connection.commit ();

            long lStartTime = System.currentTimeMillis ();

            while ((!this.queue.isEmpty ()) && (System.currentTimeMillis () - lStartTime < 100L)) {
                Query query = (Query) this.queue.poll ();
                if (query != null) {
                    for (String sQuery : query.getQuery ()) {
                        try {
                            statement.execute (sQuery);
                        } catch (SQLException exception) {
                            Messaging.severe ("SQL exception on \"%s\": %s", new Object[]{sQuery, exception.getMessage ()});
                            exception.printStackTrace ();
                        }
                    }
                }
            }
            connection.commit ();
        } catch (SQLException exception) {
            Messaging.severe ("SQL exception %s:", new Object[]{exception.getMessage ()});
            exception.printStackTrace ();
        } finally {
            try {
                if (statement != null) {
                    statement.close ();
                }else{
                    Messaging.info ("Can't close statement");
                }

                if (connection != null)
                    connection.close ();
                else
                    Messaging.info ("Can't close connection");
            } catch (SQLException exception) {
                Messaging.severe ("SQL exception on close %s:", new Object[]{exception.getMessage ()});
                exception.printStackTrace ();
            }

            this.lock.unlock ();
        }
    }
}
