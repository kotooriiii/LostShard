package com.github.kotooriiii.bank;

import com.github.kotooriiii.files.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Bank {
    private UUID playerUUID;
    private Inventory inventory;
    private double currency;
    public final static String NAME = ChatColor.GRAY + "Bank";
    private final static HashMap<UUID, Bank> banks = new HashMap<>();

    public Bank(UUID playerUUID, Inventory inventory, double currency)
    {
        this.inventory = inventory;
        this.playerUUID = playerUUID;
        this.currency=currency;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Inventory getInventory() {
        return inventory;
    }



    public double getCurrency()
    {
        return new BigDecimal(currency).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public void setCurrency(double currency) {
        this.currency = currency;
        FileManager.write(this);
    }

    public void add(double currency)
    {
        this.currency += currency;
        FileManager.write(this);
    }

    public void remove(double currency)
    {
        this.currency -= currency;
        FileManager.write(this);
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public static HashMap<UUID, Bank> getBanks() {
        return banks;
    }

    public static Bank wrap(UUID playerUUID)
    {
        return getBanks().get(playerUUID);
    }
}
