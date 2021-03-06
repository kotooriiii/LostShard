package com.github.kotooriiii.plots.events;

import com.github.kotooriiii.plots.struct.PlayerPlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlotExpandEvent extends Event implements Cancellable {

    private Player player;
    private PlayerPlot plot;
    private int radius;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public PlotExpandEvent(Player player, PlayerPlot plot, int newRadius) {
        this.player = player;
        this.plot = plot;
        this.radius = newRadius;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerPlot getPlot() {
        return plot;
    }

    public int getNextRadius() {
        return radius;
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
