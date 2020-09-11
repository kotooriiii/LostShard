package com.github.kotooriiii.bank.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BankDepositEvent extends Event {
    private final double amount;
    private Player player;


    private static final HandlerList handlers = new HandlerList();

    public BankDepositEvent(Player player, double amount) {
        this.player = player;
        this.amount=amount;
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

    public double getAmount() {
        return amount;
    }
}