package io.gearworks.turfpoints;

import io.gearworks.turfpoints.commands.*;
import io.gearworks.turfpoints.database.DatabaseManager;
import io.gearworks.turfpoints.database.queries.QueryOffline;
import io.gearworks.turfpoints.database.queries.QueryOnline;
import io.gearworks.turfpoints.player.LocalPlayer;
import io.gearworks.turfpoints.player.PlayerRegistry;
import io.gearworks.turfpoints.shop.ShopMenu;
import io.gearworks.turfpoints.shop.api.OptionClickEvent;
import io.gearworks.turfpoints.shop.api.OptionClickEventHandler;
import io.gearworks.turfpoints.utilities.Messaging;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TurfPoints extends JavaPlugin implements Listener{

    private static TurfPoints instance;

    public static final String SERVER_STRING = "pvp-turf";

    private DatabaseManager databaseManager;
    private PlayerRegistry playerRegistry;

    private Permission permission;

    public void onLoad (){
        instance = this;

    }

    public void onEnable (){
        databaseManager = new DatabaseManager ();

        if (databaseManager.getConnection () == null){
            Messaging.severe ("Disabling dicks");
            getServer ().getPluginManager ().disablePlugin (this);
        }

        playerRegistry = new PlayerRegistry ();
        new PlayerListener ();

        getServer ().getPluginManager ().registerEvents (this, this);

        getCommand ("givecredits").setExecutor (new CmdGiveCredits ());
        getCommand ("givepoints").setExecutor (new CmdGivePoints ());
        getCommand ("checkmypoints").setExecutor (new CmdCheckPoints ());
        getCommand ("checkmycredits").setExecutor (new CmdCheckCredits ());
        getCommand ("verify").setExecutor (new CmdVerify ());

        Bukkit.getScheduler ().runTaskTimer (this, databaseManager.getConsumer (), 120L, 120L);

        if (!setupPermissions ()){
            Messaging.severe ("Unable to hook into permissions, disabling plugin...");
            Bukkit.getPluginManager ().disablePlugin (this);
        }
    }

    public boolean setupPermissions (){
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);

        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        return (permission != null);
    }

    public void onDisable (){

    }

    public static TurfPoints getInstance (){
        return instance;
    }

    public static DatabaseManager getDatabaseManager (){
        return instance.databaseManager;
    }

    public static PlayerRegistry getPlayerRegistry (){
        return instance.playerRegistry;
    }

    public static Permission getPermissions (){
        return instance.permission;
    }

    @EventHandler
    public void onPlayerJoin (final PlayerJoinEvent event){
        final LocalPlayer localPlayer = playerRegistry.register (event.getPlayer (), new LocalPlayer (event.getPlayer ()));
        databaseManager.getConsumer ().queueQuery (new QueryOnline (localPlayer));
    }

    @EventHandler
    public void onPlayerDisconnect (final PlayerQuitEvent event){
        final LocalPlayer localPlayer = playerRegistry.getPlayer (event.getPlayer ());
        databaseManager.getConsumer ().queueQuery (new QueryOffline (localPlayer));
        playerRegistry.unregister (event.getPlayer (), localPlayer);
    }

    public ShopMenu getPvpTurfShop (){
        ShopMenu toReturn = new ShopMenu ("PvP-Turf Shop", 10, new OptionClickEventHandler () {
            @Override
            public void onOptionClick (OptionClickEvent event) {
                final LocalPlayer localPlayer = getPlayerRegistry ().getPlayer (event.getPlayer ());
                if (localPlayer != null){

                }
            }
        }).setOption (3, new ItemStack (Material.APPLE), 250, "Test");

        return toReturn;
    }
}
