package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.skills.events.MiningSkillEvent;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;

public class IntroMiningChapter extends AbstractChapter {

    private int counter;
    private Zone zone;
    private boolean isFirstTime;

    public IntroMiningChapter() {
        counter = 0;
        isFirstTime = true;
        //todo zone
    }

    @Override
    public void onBegin() {

        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getMining().setLevel(100.0f);

        sendMessage(player, "Wow, this mine has a lot of gold in it. It's probably a good idea to mine it all...\nGrab the pickaxe out of the chest.");

    }

    @EventHandler
    public void onListen(InventoryOpenEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if (event.isCancelled())
            return;

        if (event.getInventory().getType() != InventoryType.CHEST)
            return;


        event.setCancelled(true);

        Inventory inventory = Bukkit.createInventory(event.getPlayer(), 1, ChatColor.GRAY + "Iron Pickaxe Holder");
        inventory.addItem(new ItemStack(Material.IRON_PICKAXE, 1));
        event.getPlayer().openInventory(inventory);
    }


    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if (!event.getBlock().getType().equals(Material.GOLD_ORE))
            return;

        counter++;
        if (counter == 3) {
            sendMessage(event.getPlayer(), "Get the iron as well!");
            return;
        }

        if (counter >= 13) {
            sendMessage(event.getPlayer(), "You won't need all this gold...");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onProximity(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (zone == null)
            return;

        Location to = event.getTo();
        if (!zone.contains(to))
            return;

        setComplete();
    }

    @EventHandler
    public void onFirstDrop(MiningSkillEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(!isFirstTime)
            return;

        isFirstTime= false;
        sendMessage(event.getPlayer(), "The Mining skill awards you extra drops when mining stone and ores.");
    }

    @EventHandler
    public void itemDmg(PlayerItemDamageEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        int dmg = event.getDamage() + 15;

        if (event.getItem().getType().getMaxDurability() <= dmg) {
            sendMessage(event.getPlayer(), "Your pickaxe is getting low. Find a place to fix it!");
        }
    }

    @Override
    public void onDestroy() {

    }
}
