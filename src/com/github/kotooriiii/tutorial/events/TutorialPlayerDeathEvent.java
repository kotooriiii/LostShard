package com.github.kotooriiii.tutorial.events;

import com.github.kotooriiii.sorcery.spells.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TutorialPlayerDeathEvent extends Event {
    private Player player;


    private static final HandlerList handlers = new HandlerList();

    public TutorialPlayerDeathEvent(Player player) {
        this.player = player;
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