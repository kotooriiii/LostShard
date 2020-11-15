package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.skills.events.MiningSkillEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;

public class FreedomChapter extends AbstractChapter {

    private Zone zone;
    private boolean isComplete,isFirstTime;

    public FreedomChapter() {
        this.zone = new Zone(423, 427, 44, 36, 724, 716);
        isComplete = false;
        isFirstTime = true;
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximity(PlayerMoveEvent event) {
        if (isComplete)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (zone == null)
            return;

        Location to = event.getTo();
        if (!zone.contains(to))
            return;
        isComplete = true;

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);


        sendMessage(event.getPlayer(), "You've made it!\nMake your way to Order straight ahead.", ChapterMessageType.HOLOGRAM_TO_TEXT);

        ItemStack[] contents = event.getPlayer().getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null)
                continue;
            if (contents[i].getType() == Material.IRON_PICKAXE || contents[i].getType() == Material.DIAMOND || contents[i].getType().getKey().getKey().toLowerCase().contains("ore") || contents[i].getType() == Material.STICK || contents[i].getType() == Material.FEATHER)
                event.getPlayer().getInventory().setItem(i, null);
        }

        setComplete();
    }

    @EventHandler
    public void onFirstDrop(MiningSkillEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!isFirstTime)
            return;

        isFirstTime = false;
        sendMessage(event.getPlayer(), "The Mining skill awards you extra drops when mining stone.", ChapterMessageType.HELPER);
    }

    @EventHandler
    public void onListen(InventoryOpenEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if (event.isCancelled())
            return;

        final String name = ChatColor.MAGIC + "" + ChatColor.LIGHT_PURPLE + "l" + ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "Super Iron Pickaxe Holder" + ChatColor.RESET + "" + ChatColor.MAGIC + ChatColor.LIGHT_PURPLE + "l";

        if (event.getView().getTitle().equals(name))
            return;


        event.setCancelled(true);

        Inventory inventory = Bukkit.createInventory(event.getPlayer(), 9, name);
        ItemStack itemStack = new ItemStack(Material.IRON_PICKAXE, 1);
        itemStack.addEnchantment(Enchantment.DIG_SPEED, 5);
        itemStack.addEnchantment(Enchantment.DURABILITY, 1);
        inventory.addItem(itemStack);
        event.getPlayer().openInventory(inventory);
    }
}
