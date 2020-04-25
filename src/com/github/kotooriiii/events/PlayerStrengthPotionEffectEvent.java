package com.github.kotooriiii.events;

import com.github.kotooriiii.plots.Plot;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerStrengthPotionEffectEvent extends Event {
    private Player player;

    private static final HandlerList handlers = new HandlerList();
    boolean cancelled;

    public PlayerStrengthPotionEffectEvent(Player player) {
        this.player = player;
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

    public Player getPlayer() {
        return player;
    }
}
