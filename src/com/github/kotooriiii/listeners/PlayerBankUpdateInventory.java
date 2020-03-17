package com.github.kotooriiii.listeners;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.files.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class PlayerBankUpdateInventory implements Listener {
    @EventHandler
    public void onBankInventoryClose(InventoryCloseEvent inventoryCloseEvent) {


        if(inventoryCloseEvent.getView().getTitle().equalsIgnoreCase(ChatColor.GRAY + "Bank"))
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

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        Bank.getBanks().remove(player.getUniqueId());
    }

    @EventHandler
    public void onJoinLoadBank(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        Bank bank = FileManager.readBankFile(player.getUniqueId());
        if(bank==null)
            return;
        Bank.getBanks().put(player.getUniqueId(), bank);
    }

}