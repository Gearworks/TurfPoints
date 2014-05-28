package io.gearworks.turfpoints.database.queries;

public class QueryAddCredits implements Query {

    private int points;
    private String playerName;

    public QueryAddCredits (int points, String playerName){
        this.points = points;
        this.playerName = playerName;
    }

    public String [] getQuery (){
        return new String[] { String.format ("UPDATE credits SET points = points + %d WHERE playername = '%s'", points, playerName) };
    }
}
