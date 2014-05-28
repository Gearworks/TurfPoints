package io.gearworks.turfpoints.database.queries;

public class QueryVerify implements Query {

    private String playerName;

    public QueryVerify (final String playerName){
        this.playerName = playerName;
    }

    public String [] getQuery (){
        return new String [] { String.format ("UPDATE `vbver` SET verified = 1 WHERE mcname = '%s'", playerName) };
    }
}
