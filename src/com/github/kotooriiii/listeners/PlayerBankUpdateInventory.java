package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
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
import org.bukkit.inventory.InventoryHolder;

public class PlayerBankUpdateInventory implements Listener {
    @EventHandler
    public void onBankInventoryClose(InventoryCloseEvent inventoryCloseEvent) {


        if(inventoryCloseEvent.getView().getTitle().equalsIgnoreCase(Bank.NAME))
        {
            HumanEntity humanEntity = inventoryCloseEvent.getPlayer();
            Inventory inventory = inventoryCloseEvent.getInventory();
            InventoryHolder holder = inventory.getHolder();

            if(holder==null)
                return;

            if(humanEntity instanceof Player && holder instanceof Player) {

                Bank bank = LostShardPlugin.getBankManager().wrap(((Player) holder).getUniqueId());
                bank.setInventory(inventory);
                FileManager.write(bank);
            }
            //save inventory
        }


    }



}