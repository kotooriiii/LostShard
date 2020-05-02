package com.github.kotooriiii.events;

import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeavePlotEvent extends Event implements Cancellable {

    private Plot plot;
    private Player trespasser;

    private static final HandlerList handlers = new HandlerList();
    boolean cancelled;

    public PlayerLeavePlotEvent(Player trespasser, Plot plot) {
        this.trespasser = trespasser;
        this.plot = plot;
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

    public Plot getPlot() {
        return plot;
    }

    public Player getTrespasser() {
        return trespasser;
    }
}