package com.github.kotooriiii.sorcery.events;

import com.github.kotooriiii.plots.struct.PlayerPlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SuccessfulRecallEvent extends Event implements Cancellable {

    private Player player;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public SuccessfulRecallEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
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
