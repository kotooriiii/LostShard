package com.github.kotooriiii.skills.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityTrackEvent extends Event implements Cancellable {

    private Player player;
    private Entity trackedEntity;
    private EntityType type;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public EntityTrackEvent(Player player, Entity trackedEntity, EntityType type) {
        this.player = player;
        this.trackedEntity = trackedEntity;
        this.type = type;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Entity getTrackedEntity() {
        return trackedEntity;
    }

    public EntityType getType() {
        return type;
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