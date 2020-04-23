package com.github.kotooriiii.discord.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DiscordBaseCommandEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean isSuccessful;

    public HandlerList getHandlers() {
        return handlers;
    }

    public DiscordBaseCommandEvent()
    {
        isSuccessful =false;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
