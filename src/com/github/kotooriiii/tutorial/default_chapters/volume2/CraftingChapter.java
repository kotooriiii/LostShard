package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CraftingChapter extends AbstractChapter {

    private Zone zone;

    public CraftingChapter() {
        //todo this.zone =
    }

    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;


    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onCraft(InventoryClickEvent event) {
        //todo implement
        if (event.getSlotType() != InventoryType.SlotType.RESULT)
            return;

        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null)
            return;

        switch (itemStack.getType()) {
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_HELMET:
                break;
            default:
                return;
        }

        List<ItemStack> items = Arrays.asList(player.getInventory().getContents());
        items.add(itemStack);

        boolean[] armor = new boolean[]{false, false, false, false};
        for (ItemStack istack : items) {
            if (istack.getType() == Material.CHAINMAIL_HELMET && !armor[0])
                armor[0] = true;

            if (istack.getType() == Material.CHAINMAIL_CHESTPLATE && !armor[1])
                armor[1] = true;

            if (istack.getType() == Material.CHAINMAIL_LEGGINGS && !armor[2])
                armor[2] = true;

            if (istack.getType() == Material.CHAINMAIL_BOOTS && !armor[3])
                armor[3] = true;
        }

        boolean isUnanimous = true;
        for (boolean checks : armor)
            if (!checks)
                isUnanimous = false;

        if (!isUnanimous)
            return;

        sendMessage(player, "Good job!\nYou're ready to head to Order.");
        setComplete();
    }

    @EventHandler
    public void onLeave(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (zone == null)
            return;

        Location from = event.getFrom();
        Location to = event.getTo();
        if (zone.contains(from) && !zone.contains(to)) {

            sendMessage(event.getPlayer(), "You must craft some chain armor before leaving.");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onEnter(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (zone == null)
            return;
        Location from = event.getFrom();
        Location to = event.getTo();
        if (!zone.contains(from) && zone.contains(to)) {
            sendMessage(event.getPlayer(), "Before you go in, you should craft some Chain Armor.\nChain armor is crafted like any other armor, just using cobblestone!");
            event.getPlayer().getInventory().addItem(new ItemStack(Material.COBBLESTONE, 24));
        }
    }

}
