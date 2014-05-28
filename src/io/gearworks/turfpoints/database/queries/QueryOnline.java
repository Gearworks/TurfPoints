package io.gearworks.turfpoints.database.queries;

import io.gearworks.turfpoints.player.LocalPlayer;

public class QueryOnline implements Query {

    private final LocalPlayer localPlayer;

    public QueryOnline (final LocalPlayer localPlayer){
        this.localPlayer = localPlayer;
    }

    public String[] getQuery (){
        return new String[]{ String.format ("UPDATE credits SET online = 1 WHERE playername = '%s'", localPlayer.getBukkitPlayer ().getName ()) };
    }
}
