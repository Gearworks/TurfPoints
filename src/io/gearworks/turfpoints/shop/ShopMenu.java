package io.gearworks.turfpoints.shop;

import io.gearworks.turfpoints.TurfPoints;
import io.gearworks.turfpoints.player.LocalPlayer;
import io.gearworks.turfpoints.shop.api.OptionClickEvent;
import io.gearworks.turfpoints.shop.api.OptionClickEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class ShopMenu implements Listener {

    private String shopName;
    private int size;
    private OptionClickEventHandler handler;

    private String[] optionNames;
    private ItemStack[] optionIcons;
    private int[] optionPrices;

    public ShopMenu (String shopName, int size, OptionClickEventHandler handler){
        this.shopName = shopName;
        this.size = size;
        this.handler = handler;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
        optionPrices = new int[size];
        TurfPoints.getInstance ().getServer ().getPluginManager ().registerEvents (this, TurfPoints.getInstance ());
    }

    public ShopMenu setOption (int position, ItemStack icon, int price, String name, String... info){
        optionNames[position] = name;
        optionIcons[position] = setItemNameAndLore (icon, name, info);
        optionPrices[position] = price;
        return this;
    }

    public void open (Player player){
        Inventory inventory = Bukkit.createInventory (player, size, shopName);

        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }

        player.openInventory(inventory);
    }

    public void destroy() {
        HandlerList.unregisterAll (this);
        handler = null;
        optionNames = null;
        optionIcons = null;
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onInventoryClick (final InventoryClickEvent event){
        if (event.getInventory ().getTitle ().equals (shopName)){
            event.setCancelled (true);
            int slot = event.getRawSlot ();
            LocalPlayer localPlayer = TurfPoints.getPlayerRegistry ().getPlayer ((Player) event.getWhoClicked ());
            if (slot >= 0 && slot < size && optionNames[slot] != null){
                if (localPlayer.getPoints () >= optionPrices[slot]){
                    OptionClickEvent clickEvent = new OptionClickEvent ((Player) event.getWhoClicked (), slot, optionNames[slot]);
                    handler.onOptionClick (clickEvent);
                    if (clickEvent.willClose ()){
                        final Player player = (Player) event.getWhoClicked ();
                        Bukkit.getScheduler ().scheduleSyncDelayedTask (TurfPoints.getInstance (), new Runnable (){
                            public void run (){
                                player.closeInventory ();
                            }
                        }, 1);
                    }

                    if (clickEvent.willClose ()){
                        destroy ();
                    }
                }else{
                    ((Player) event.getWhoClicked ()).sendMessage ("\2474You do not have enough gearpoints!");
                }
            }
        }
    }

    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList (lore));
        item.setItemMeta(im);
        return item;
    }
}
