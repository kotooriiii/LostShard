package com.github.kotooriiii.hostility.events;

import com.github.kotooriiii.clans.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlatformVictoryEvent extends Event implements Cancellable {

    private Player player;
    private Clan clan;
    private String platformName;
    private String message;

    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public PlatformVictoryEvent(Player player, Clan clan, String platformName) {
        this.player = player;
        this.clan = clan;
        this.platformName = platformName;
    }

    public Player getPlayer() {
        return player;
    }

    public Clan getClan() {
        return clan;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
