package com.github.kotooriiii.channels;

import com.github.kotooriiii.guards.ShardBaseNPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ShardChatEvent extends Event implements Cancellable {

    private Player player;
    private String message;
    private static final HandlerList handlers = new HandlerList();
    boolean cancelled;

    public ShardChatEvent(Player player, String message)
    {
        this.player = player;
        this.message = message;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getMessage() {return  message;}

    public Player getPlayer() {
        return player;
    }
}