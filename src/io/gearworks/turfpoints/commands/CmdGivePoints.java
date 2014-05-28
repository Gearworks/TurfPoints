package io.gearworks.turfpoints.commands;

import io.gearworks.turfpoints.TurfPoints;
import io.gearworks.turfpoints.player.LocalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdGivePoints implements CommandExecutor {

    public boolean onCommand (final CommandSender sender, final Command cmd, String label, String [] args){
        if (sender.isOp ()){
            if (args.length != 2){
                sender.sendMessage ("\2474/givepoints [player] [value]");
                return true;
            }

            if (Bukkit.getPlayer (args[0]) != null){
                final LocalPlayer localPlayer = TurfPoints.getPlayerRegistry ().getPlayer (Bukkit.getPlayer (args[0]));
                localPlayer.setPoints (localPlayer.getPoints () + Integer.parseInt (args[1]));
                localPlayer.getBukkitPlayer ().sendMessage (String.format ("\2476You have received %d points! Enjoy!!!", Integer.parseInt (args[1])));
                sender.sendMessage ("Player has received points.");
            }

            TurfPoints.getDatabaseManager ().addPlayerPoints (Integer.parseInt (args[1]), args[0]);
        }

        return true;
    }

}
