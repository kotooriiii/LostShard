package com.github.kotooriiii.skills.events;

import com.github.kotooriiii.skills.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class SkillLevelUpEvent extends Event implements Cancellable {

    private UUID uuid;
    private SkillType type;
    private float toLevel;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public SkillLevelUpEvent(UUID uuid, SkillType type, float toLevel) {
        this.uuid = uuid;
        this.type = type;
        this.toLevel = toLevel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public float getToLevel() {
        return toLevel;
    }

    public SkillType getType() {
        return type;
    }

    public UUID getUUID() {
        return uuid;
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