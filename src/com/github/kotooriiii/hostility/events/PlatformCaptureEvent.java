package com.github.kotooriiii.hostility.events;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlatformCaptureEvent extends Event implements Cancellable {

    private Player player;
    private Clan clan;
    private int wins;

    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public PlatformCaptureEvent(Player player, Clan clan) {
        this.player = player;
        this.clan = clan;
    }

    public Player getPlayer() {
        return player;
    }

    public Clan getClan() {
        return clan;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
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
