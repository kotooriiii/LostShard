package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.SkillPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.awt.event.KeyEvent;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class NoMoreOldEnchantsListener implements Listener {

    public static final String COMBINABLE_PREFIX = ChatColor.GREEN + "Combinable";
    public static final int COMBINABLE_LEVEL = 30;

    @EventHandler
    public void onEnchant(InventoryOpenEvent event) {
        if (!event.getView().getType().equals(InventoryType.ENCHANTING))
            return;

        event.getPlayer().sendMessage(ERROR_COLOR + "You must use the Blacksmithy skill to enchant your items.");
        event.setCancelled(true);
    }

    @EventHandler
    public void onNPC(InventoryOpenEvent event) {
        if (!event.getView().getType().equals(InventoryType.MERCHANT))
            return;

        event.getPlayer().sendMessage(ERROR_COLOR + "Trading with villagers is disabled.");
        event.setCancelled(true);
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {

        Inventory source = event.getInventory();

        if (source == null)
            return;

        if (!hasTwoItems(source)) //only one item, naming?
            return;

        if (!hasCombinable(source))
            return;

        ((AnvilInventory) source).setRepairCost(30);
    }

    @EventHandler
    public void onEnchant(InventoryClickEvent event) {

        Inventory source = event.getClickedInventory();

        if (source == null)
            return;

        if (!source.getType().equals(InventoryType.ANVIL))
            return;

        if (event.getSlot() != 2)
            return;

        if (!hasTwoItems(source)) //only one item, naming?
            return;

        /*
        At this point, guaranteed to be:
        - Existing inventory
        - Anvil inventory
        - Clicking on result item
        - More than one item
         */

        if (!hasCombinable(source)) {
            sendMessage(source, ERROR_COLOR + "You must use a " + ChatColor.GREEN + "Combinable" + ERROR_COLOR + " item.");

//            //DEBUGGING
//            //todo remove this
//            ItemStack fireAspectItemStack = new ItemStack(Material.ENCHANTED_BOOK, 1);
//            EnchantmentStorageMeta fireAspectMeta = (EnchantmentStorageMeta) fireAspectItemStack.getItemMeta();
//            fireAspectMeta.addStoredEnchant(Enchantment.FIRE_ASPECT, 1, true);
//            fireAspectMeta.setLore(Arrays.asList(COMBINABLE_PREFIX));
//            fireAspectItemStack.setItemMeta(fireAspectMeta);
//            event.getWhoClicked().getInventory().addItem(fireAspectItemStack);
//            //REMOVE ^

            event.setCancelled(true);
            return;
        }
               /*
        At this point, guaranteed to be:
        - Existing inventory
        - Anvil inventory
        - Clicking on result item
        - More than one item
        - One item is a Combinable item
         */

        if ((int) LostShardPlugin.getSkillManager().getSkillPlayer(event.getWhoClicked().getUniqueId()).getActiveBuild().getBlacksmithy().getLevel() != 100) {
            sendMessage(source, ERROR_COLOR + "You must be Blacksmithy level 100 to combine.");
            event.setCancelled(true);
            return;
        }

        if (event.getWhoClicked() instanceof Player) {
            Player clickedPlayer = (Player) event.getWhoClicked();
            if (clickedPlayer.getLevel() < COMBINABLE_LEVEL) {
                sendMessage(source, ERROR_COLOR + "Insufficient levels. You must be at least xp level 30 to combine.");
                event.setCancelled(true);
                return;
            }
        }


    }

    private void sendMessage(Inventory source, String message) {
        HumanEntity[] humanEntities = source.getViewers().toArray(new HumanEntity[0]);
        for (HumanEntity humanEntity : humanEntities) {
            humanEntity.closeInventory();
            humanEntity.sendMessage(message);
        }

    }

    private boolean hasTwoItems(Inventory inventory) {
        ItemStack item1 = inventory.getItem(0);
        ItemStack item2 = inventory.getItem(1);
        return item1 != null && item2 != null;
    }

    private boolean hasCombinable(Inventory inventory) {
        ItemStack item1 = inventory.getItem(0);
        ItemStack item2 = inventory.getItem(1);

        List<String> lore1 = item1.getItemMeta().getLore();
        List<String> lore2 = item2.getItemMeta().getLore();

        if (lore2 != null) {
            if (lore2.get(lore2.size() - 1).equals(COMBINABLE_PREFIX))
                return true;
        }

        if (lore1 != null) {
            if (lore1.get(lore1.size() - 1).equals(COMBINABLE_PREFIX))
                return true;
        }

        return false;
    }
}


