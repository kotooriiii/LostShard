package com.github.kotooriiii.channels.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ShardChatEvent extends Event implements Cancellable {

    private Player player;
    private String message;
    private String formattedMessage;
    private static final HandlerList handlers = new HandlerList();
    boolean cancelled;

    public ShardChatEvent(Player player, String message, String formattedMessage)
    {
        this.player = player;
        this.message = message;
        this.formattedMessage = formattedMessage;
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

    public String getFormattedMessage() {return  formattedMessage;}

    public String getMessage() {return  message;}

    public Player getPlayer() {
        return player;
    }
}