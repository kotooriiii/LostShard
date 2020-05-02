package com.github.kotooriiii.listeners;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.ranks.RankPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

public class PlayerBankUpdateInventory implements Listener {
    @EventHandler
    public void onBankInventoryClose(InventoryCloseEvent inventoryCloseEvent) {


        if(inventoryCloseEvent.getView().getTitle().equalsIgnoreCase(Bank.NAME))
        {
            HumanEntity player = inventoryCloseEvent.getPlayer();
            if(player instanceof Player) {
                Inventory inventory = inventoryCloseEvent.getInventory();
                Bank bank = Bank.getBanks().get(player.getUniqueId());
                bank.setInventory(inventory);
                FileManager.write(bank);
            }
            //save inventory
        }
    }



}