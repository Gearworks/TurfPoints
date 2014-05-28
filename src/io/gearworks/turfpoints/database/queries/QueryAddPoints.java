package io.gearworks.turfpoints.database.queries;

public class QueryAddPoints implements Query {

    private int points;
    private String playerName;

    public QueryAddPoints (int points, String playerName){
        this.points = points;
        this.playerName = playerName;
    }

    public String [] getQuery (){
        return new String[] { String.format ("UPDATE points SET points = points + %d WHERE playername = '%s'", points, playerName) };
    }

}
