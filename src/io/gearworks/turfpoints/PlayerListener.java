package io.gearworks.turfpoints;

import com.vexsoftware.votifier.model.VotifierEvent;
import io.gearworks.turfpoints.player.LocalPlayer;
import io.gearworks.turfpoints.utilities.BungeeUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener {

    public PlayerListener (){
        Bukkit.getPluginManager ().registerEvents (this, TurfPoints.getInstance ());
    }

    @EventHandler
    public void onPlayerLogin (final PlayerLoginEvent event){
        final String group = TurfPoints.getPermissions ().getPrimaryGroup (event.getPlayer ());
        if (group.equalsIgnoreCase ("default") || group.equalsIgnoreCase ("guest")){
            if (TurfPoints.getDatabaseManager ().isVerified (event.getPlayer ().getName ())){
                TurfPoints.getPermissions ().playerAddGroup (event.getPlayer (), "Member");
            }
        }
    }

    @EventHandler
    public void onVoteEvent (final VotifierEvent event){
        if (TurfPoints.getPlayerRegistry ().getPlayer (Bukkit.getPlayer (event.getVote ().getUsername ())) != null){
            final LocalPlayer localPlayer = TurfPoints.getPlayerRegistry ().getPlayer (Bukkit.getPlayer (event.getVote ().getUsername ()));
            Bukkit.broadcastMessage (String.format ("\2473\247l%s has voted and earned 250 points for the server", event.getVote ().getUsername ()));
            localPlayer.setPoints (localPlayer.getPoints () + 250);
        }
    }
}
