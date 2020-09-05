package com.github.kotooriiii.skills.events;

import com.github.kotooriiii.sorcery.spells.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MiningSkillEvent extends Event implements Cancellable {

    private Player player;
    private ArrayList<ItemStack> drops;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public MiningSkillEvent(Player player, ArrayList<ItemStack> drops) {
        this.player = player;
        this.drops = drops;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ArrayList<ItemStack> getDrops() {
        return drops;
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