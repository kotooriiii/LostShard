package com.github.kotooriiii.events;

import com.github.kotooriiii.guards.ShardNPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeftClickShardNPCEvent extends Event implements Cancellable {

    private ShardNPC rightClicked;
    private Player player;


    private static final HandlerList handlers = new HandlerList();
    boolean cancelled;

    public PlayerLeftClickShardNPCEvent(Player attacker, ShardNPC rightClicked)
    {
        this.player = attacker;
        this.rightClicked = rightClicked;
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

    public ShardNPC getRightClicked() {
        return rightClicked;
    }

    public Player getPlayer() {
        return player;
    }
}