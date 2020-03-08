package com.github.kotooriiii.events;

import com.github.kotooriiii.clans.Clan;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LSClanFileUpdateEvent extends Event implements Cancellable {

    private Clan clan;

    private static final HandlerList handlers = new HandlerList();
    boolean cancelled;

    public LSClanFileUpdateEvent(Clan clan)
    {
        this.clan = clan;
    }

    public Clan getClan()
    {
        return this.clan;
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


}