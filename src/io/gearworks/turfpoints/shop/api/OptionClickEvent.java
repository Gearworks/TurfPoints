package io.gearworks.turfpoints.shop.api;

import org.bukkit.entity.Player;

public class OptionClickEvent {

    private Player player;
    private int position;
    private String name;
    private boolean close;
    private boolean destroy;

    public OptionClickEvent(Player player, int position, String name) {
        this.player = player;
        this.position = position;
        this.name = name;
        this.close = true;
        this.destroy = false;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public boolean willClose() {
        return close;
    }

    public boolean willDestroy() {
        return destroy;
    }

    public void setWillClose(boolean close) {
        this.close = close;
    }

    public void setWillDestroy(boolean destroy) {
        this.destroy = destroy;
    }

}
