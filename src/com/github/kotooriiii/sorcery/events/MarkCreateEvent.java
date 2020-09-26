package com.github.kotooriiii.sorcery.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MarkCreateEvent extends Event implements Cancellable {

    private Player player;
    private Location location;
    private String name;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public MarkCreateEvent(Player player, Location location, String name) {
        this.player = player;
        this.location = location;
        this.name = name;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
