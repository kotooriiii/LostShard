package com.github.kotooriiii.discord.events;

import discord4j.core.object.entity.Message;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DiscordMessageCreateEvent extends Event implements Cancellable {
    private Message message;

    private static final HandlerList handlers = new HandlerList();
    boolean cancelled;

    public DiscordMessageCreateEvent(Message message)
    {
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

    public Message getMessage() {return  message;}
}
