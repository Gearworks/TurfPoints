package io.gearworks.turfpoints.commands;

import io.gearworks.turfpoints.TurfPoints;
import io.gearworks.turfpoints.player.LocalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdGiveCredits implements CommandExecutor {

    public boolean onCommand (final CommandSender sender, final Command cmd, String label, String [] args){
        if (sender.isOp ()){
            if (args.length != 2){
                sender.sendMessage ("\2474/givecredits [player] [value]");
                return true;
            }

            if (Bukkit.getPlayer (args[0]) != null){
                final LocalPlayer localPlayer = TurfPoints.getPlayerRegistry ().getPlayer (Bukkit.getPlayer (args[0]));
                localPlayer.setCredits (localPlayer.getCredits () + Integer.parseInt (args[1]));
                localPlayer.getBukkitPlayer ().sendMessage (String.format ("\2476You have received %d credits! Enjoy!!!", Integer.parseInt (args[1])));
                sender.sendMessage ("Player has received credits.");
            }
            // The player will still receive his points, even if he is offline
            TurfPoints.getDatabaseManager ().addPlayerCredits (Integer.parseInt (args[1]), args[0]);
        }

        return true;
    }
}
