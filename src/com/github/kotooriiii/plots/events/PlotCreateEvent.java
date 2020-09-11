package com.github.kotooriiii.plots.events;

import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.skills.commands.blacksmithy.BlacksmithyType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlotCreateEvent extends Event implements Cancellable {

    private Player player;
    private PlayerPlot plot;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public PlotCreateEvent(Player player, PlayerPlot plot) {
        this.player = player;
        this.plot = plot;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerPlot getPlot() {
        return plot;
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
