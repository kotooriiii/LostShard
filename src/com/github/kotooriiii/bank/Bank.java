package com.github.kotooriiii.bank;

import com.github.kotooriiii.files.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Bank {
    private UUID playerUUID;
    private Inventory inventory;
    private double currency;
    public final static String NAME = ChatColor.GRAY + "Bank";

    public Bank(UUID playerUUID, Inventory inventory, double currency) {
        this.inventory = inventory;
        this.playerUUID = playerUUID;
        this.currency = currency;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Inventory getInventory() {
        return inventory;
    }


    public double getCurrency() {
        return new BigDecimal(currency).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public void setCurrency(double currency) {
        this.currency = currency;
    }

    public void addCurrency(double currency) {
        this.currency += currency;
    }

    public void removeCurrency(double currency) {
        this.currency -= currency;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

}
