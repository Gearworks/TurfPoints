package io.gearworks.turfpoints.commands;

import io.gearworks.turfpoints.TurfPoints;
import io.gearworks.turfpoints.database.DatabaseManager;
import io.gearworks.turfpoints.database.queries.QueryVerify;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdVerify implements CommandExecutor {

    private DatabaseManager databaseManager = TurfPoints.getDatabaseManager ();

    public boolean onCommand (final CommandSender sender, final Command cmd, String label, String[] args){
        if (sender instanceof Player){
            final String playerName = sender.getName ();

            if (!databaseManager.isLinkedAttempted (playerName)){
                sender.sendMessage ("\2474You have not tried to link your account! Do it now at http://forums.gearworks.io/?pageid=linkaccount");
                return true;
            }

            if (args.length != 1){
                sender.sendMessage ("\2476Usage: /verify [key]");
                return true;
            }

            if (!databaseManager.keyMatches (playerName, args[0])){
                sender.sendMessage ("\2474Keys do not match!");
                return true;
            }

            if (databaseManager.isVerified (playerName)){
                sender.sendMessage ("\2474You have already verified this account!");
                return true;
            }

            sender.sendMessage ("\247aYou have successfully verified your account!");
            sender.sendMessage ("\247aPlease wait a few seconds and then relog to finish the verification process.");
            databaseManager.getConsumer ().queueQuery (new QueryVerify (playerName));

            if (TurfPoints.getPermissions ().getPrimaryGroup ((Player) sender).equalsIgnoreCase ("default")
                    || TurfPoints.getPermissions ().getPrimaryGroup ((Player) sender).equalsIgnoreCase ("guest")){
                TurfPoints.getPermissions ().playerAddGroup ((Player) sender, "Member");
            }
        }else{
            sender.sendMessage ("\2474You must be logged in to use this command");
        }

        return true;
    }


}
