package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class BankChestChapter extends AbstractChapter {
    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if(player==null)
            return;
        LostShardPlugin.getBankManager().wrap(player.getUniqueId()).getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
        sendMessage(player, "To access your bank, type /bank\nNo one else has access to your bank.");
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        if(!(event.getPlayer() instanceof Player))
            return;
        if(!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;
        if(!event.getView().getTitle().equals(Bank.NAME))
            return;
        if(!event.getInventory().contains(Material.DIAMOND)) {
            setComplete();
            return;
        }
        sendMessage((Player) event.getPlayer(), "Don't you want that diamond? It might just come in handy...");
    }
}
