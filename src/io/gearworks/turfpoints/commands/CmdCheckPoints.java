package io.gearworks.turfpoints.commands;

import io.gearworks.turfpoints.TurfPoints;
import io.gearworks.turfpoints.player.LocalPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdCheckPoints implements CommandExecutor{

    public boolean onCommand (final CommandSender sender, final Command cmd, String label, String [] args){
        if (sender instanceof Player){
            final LocalPlayer localPlayer = TurfPoints.getPlayerRegistry ().getPlayer ((Player) sender);
            sender.sendMessage (String.format ("\2476You have %d points.", localPlayer.getPoints ()));
        }else{
            sender.sendMessage ("\2474You must be logged in to run this command!");
        }

        return true;
    }
}
