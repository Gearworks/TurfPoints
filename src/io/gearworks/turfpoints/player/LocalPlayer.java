package io.gearworks.turfpoints.player;

import io.gearworks.turfpoints.TurfPoints;
import org.bukkit.entity.Player;

public class LocalPlayer {

    private final Player bukkitPlayer;
    private int credits;
    private int points;

    public LocalPlayer (final Player bukkitPlayer){
        this.bukkitPlayer = bukkitPlayer;

        load ();
    }

    private void load (){
        credits = TurfPoints.getDatabaseManager ().getPlayerCredits (this);
        points = TurfPoints.getDatabaseManager ().getPlayerPoints (this);
    }

    public Player getBukkitPlayer (){
        return bukkitPlayer;
    }

    public int getPoints (){
        return points;
    }

    public void setPoints (int points){
        this.points = points;
    }

    public int getCredits (){
        return credits;
    }

    public void setCredits (int credits){
        this.credits = credits;
    }
}
