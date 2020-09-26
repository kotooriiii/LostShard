package com.github.kotooriiii.skills.events;

import com.github.kotooriiii.skills.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerTrackEvent extends Event implements Cancellable {

    private Player player;
    private Player trackedPlayer;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public PlayerTrackEvent(Player player, Player trackedPlayer) {
        this.player = player;
        this.trackedPlayer = trackedPlayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getTrackedPlayer() {
        return trackedPlayer;
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
}