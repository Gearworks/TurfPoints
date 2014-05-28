package io.gearworks.turfpoints.database.queries;

import io.gearworks.turfpoints.player.LocalPlayer;

public class QueryOffline implements Query {

    private LocalPlayer localPlayer;

    public QueryOffline (final LocalPlayer localPlayer){
        this.localPlayer = localPlayer;
    }

    public String[] getQuery (){
        return new String []{ String.format ("UPDATE credits SET online = 0 WHERE playername = '%s'", localPlayer.getBukkitPlayer ().getName ()) };
    }
}
