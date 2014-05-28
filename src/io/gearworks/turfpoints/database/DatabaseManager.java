package io.gearworks.turfpoints.database;

import io.gearworks.turfpoints.TurfPoints;
import io.gearworks.turfpoints.database.queries.QueryAddCredits;
import io.gearworks.turfpoints.database.queries.QueryAddPoints;
import io.gearworks.turfpoints.database.task.Consumer;
import io.gearworks.turfpoints.player.LocalPlayer;
import io.gearworks.turfpoints.utilities.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private ConnectionPool connectionPool;
    private Consumer consumer;

    private FileConfiguration config = TurfPoints.getInstance ().getConfig ();

    private Connection conn;

    public DatabaseManager (){
        try{
            Messaging.info (config.getString ("mysql.host"));

            connectionPool = new ConnectionPool (config.getString ("mysql.host"),
                                                 config.getInt ("mysql.port"),
                                                 config.getString ("mysql.database"),
                                                 config.getString ("mysql.user"),
                                                 config.getString ("mysql.pass"));

            Connection connection = connectionPool.getConnection ();
            if (connection == null){
                Messaging.severe ("Could not create a connection to the MySQL server, stopping plugin...");
                Bukkit.getPluginManager ().disablePlugin (TurfPoints.getInstance ());
            }

            connection.close ();
        } catch (NullPointerException exception){
            Messaging.severe ("Error while initializing: %s", new Object[]{exception.getMessage ()});
            exception.printStackTrace();
        } catch (Exception exception){
            Messaging.severe ("Error while initializing: %s", new Object[]{exception.getMessage ()});
            exception.printStackTrace();
        }

        consumer = new Consumer ();
        conn = getMysqlConnection ();
    }

    /**
     * Made static to be easily accessed across the plugin to allow other classes to queue queries
     *
     * @return consumer
     */
    public Consumer getConsumer (){
        return consumer;
    }

    /**
     *
     * @return the connection to the MySQL server
     */
    private Connection getMysqlConnection (){
        try{
            return connectionPool.getConnection ();
        }catch (SQLException exception){
            Messaging.severe ("SQL Exception while fetching connection: %s", new Object[]{exception.getMessage ()});
            exception.printStackTrace ();
        }

        return null;
    }

    public Connection getConnection (){
        return conn;
    }

    /**
     * Will run a result set to check to see if we get any values back from the credits table
     *
     * @param playerName
     * @return true if the player exists in the database, otherwise false
     */
    public boolean playerExistsCredits (final String playerName) throws SQLException{
        final ResultSet rs = conn.prepareStatement (String.format ("SELECT * FROM credits WHERE playername = '%s'", playerName)).executeQuery ();
        if (rs.next ()){
            return true;
        }
        rs.close ();

        return false;
    }

    /**
     * Will run a result set to check to see if we get any values back from the points table
     *
     * @param playerName
     * @return true if the player exists in the database, otherwise false
     */
    public boolean playerExistsPoints (final String playerName) throws SQLException{
        ResultSet rs = conn.prepareStatement (String.format ("SELECT * FROM points WHERE playername = '%s'", playerName)).executeQuery ();
        if (rs.next ()){
            return true;
        }

        rs.close ();
        return false;
    }

    /**
     * Needs to be done right away in order for the player loading to happen with nothing null
     *
     * @param localPlayer
     */
    public void createPlayerCredits (final String localPlayer) throws SQLException{
        final PreparedStatement ps = conn.prepareStatement (String.format ("INSERT INTO credits (`playername`, `points`, `online`) VALUES ('%s', 0, 1)",
                localPlayer));

        ps.execute ();
    }

    /**
     * Needs to be done right away in order for the player loading to happen with nothing null
     *
     * @param localPlayer
     */
    public void createPlayerPoints (final String localPlayer) throws SQLException{
        final PreparedStatement ps = conn.prepareStatement (String.format ("INSERT INTO points (`playername`, `points`) VALUES ('%s', 0)",
                localPlayer));

        ps.execute ();
    }

    public int getPlayerPoints (final LocalPlayer localPlayer){
        try{
            if (!playerExistsPoints (localPlayer.getBukkitPlayer ().getName ()))
                createPlayerPoints (localPlayer.getBukkitPlayer ().getName ());

            final ResultSet rs = conn.prepareStatement (String.format ("SELECT * FROM points WHERE playername = '%s'", localPlayer.getBukkitPlayer ().getName ())).executeQuery ();
            while (rs.next ()){
                return rs.getInt ("points");
            }
        }catch (SQLException e) {
            e.printStackTrace ();
        }

        return -1;
    }

    public void addPlayerPoints (int points, String name){
        try{
            if (!playerExistsPoints (name))
                createPlayerPoints (name);

            getConsumer ().queueQuery (new QueryAddPoints (points, name));
        }catch(SQLException e){
            e.printStackTrace ();
        }
    }

    public int getPlayerCredits (final LocalPlayer localPlayer){
        try{
            // Need to create the player into the database
            if (!playerExistsCredits (localPlayer.getBukkitPlayer ().getName ()))
                createPlayerCredits (localPlayer.getBukkitPlayer ().getName ());

            final ResultSet rs =  conn.prepareStatement (String.format ("SELECT * FROM `credits` WHERE playername = '%s'", localPlayer.getBukkitPlayer ().getName ())).executeQuery ();
            while (rs.next ()){
                return rs.getInt ("points");
            }
        }catch (SQLException e){
            e.printStackTrace ();
        }

        return -1;
    }

    public void addPlayerCredits (int points, String name){
        try{
            if (!playerExistsCredits (name))
                createPlayerCredits (name);

            getConsumer ().queueQuery (new QueryAddCredits (points, name));
        }catch(SQLException e){
            e.printStackTrace ();
        }
    }

    /**
     *
     * @param name
     * @return true if the player has their name into the vbver table, otherwise false
     */
    public boolean isLinkedAttempted (final String name){
        try{
            final ResultSet rs = conn.prepareStatement (String.format ("SELECT * FROM `vbver` WHERE `mcname` = '%s'", name)).executeQuery ();

            while (rs.next ())
                return true;

        }catch(SQLException e){
            e.printStackTrace ();
        }

        return false;
    }

    /**
     *
     * @param name
     * @param key
     * @return true if the player enters the same key in the database, otherwise false
     */
    public boolean keyMatches (final String name, final String key){
        try{
            final ResultSet rs = conn.prepareStatement (String.format ("SELECT * FROM `vbver` WHERE `mcname` = '%s'", name)).executeQuery ();

            while (rs.next ()){
                if (key.equals (rs.getString ("verkey"))) return true;
            }
        }catch (SQLException e){
            e.printStackTrace ();
        }

        return false;
    }

    /**
     *
     * @param name
     * @return true if the player has already been verified, otherwise false
     */
    public boolean isVerified (final String name){
        try{
            final ResultSet rs = conn.prepareStatement (String.format ("SELECT * FROM `vbver` WHERE `mcname` = '%s'", name)).executeQuery ();

            while (rs.next ()){
                if (rs.getInt ("verified") == 1)
                    return true;
            }
        }catch (SQLException e){
            e.printStackTrace ();
        }

        return false;
    }
}
