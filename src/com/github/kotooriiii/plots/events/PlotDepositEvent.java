package com.github.kotooriiii.plots.events;

import com.github.kotooriiii.plots.struct.PlayerPlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlotDepositEvent extends Event implements Cancellable {

    private Player player;
    private PlayerPlot plot;
    private double amount;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public PlotDepositEvent(Player player, PlayerPlot plot, double amount) {
        this.player = player;
        this.plot = plot;
        this.amount = amount;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerPlot getPlot() {
        return plot;
    }

    public double getAmount() {
        return amount;
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
