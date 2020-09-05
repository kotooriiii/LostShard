package com.github.kotooriiii.events;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.sorcery.spells.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BindEvent extends Event implements Cancellable {

    private Player player;
    private Spell spell;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public BindEvent(Player player, Spell spell) {
        this.player = player;
        this.spell = spell;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Spell getSpell() {
        return spell;
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
