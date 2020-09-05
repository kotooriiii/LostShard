package com.github.kotooriiii.tutorial.default_chapters.volume1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.BindEvent;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import java.awt.*;

public class GrabStickFromChestChapter extends AbstractChapter {
    @Override
    public void onBegin() {

        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        sendMessage(player, "Grab a stick from the chest.\nHold the stick in your hand.\nType: /bind teleport");

    }

    @EventHandler
    public void onListen(InventoryOpenEvent event)
    {
        if(!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;

        if(event.isCancelled())
            return;

        if(event.getInventory().getType() != InventoryType.CHEST)
            return;


        event.setCancelled(true);

        Inventory inventory = Bukkit.createInventory(event.getPlayer(), 1, ChatColor.GRAY + "Stick Holder");
        inventory.addItem(new ItemStack(Material.STICK, 1));
        event.getPlayer().openInventory(inventory);
    }


    @EventHandler
    public void onCommandEvent(BindEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if(event.getSpell().getType() != SpellType.TELEPORT) {
            sendMessage(event.getPlayer(), ChatColor.RED + "You will be able to try out other spells after the tutorial.");
            event.setCancelled(true);
            return;
        }

        setComplete();
    }

    @Override
    public void onDestroy() {

    }
}
