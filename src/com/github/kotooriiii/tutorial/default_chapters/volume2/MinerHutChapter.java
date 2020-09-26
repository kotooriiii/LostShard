package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.commands.blacksmithy.BlacksmithyType;
import com.github.kotooriiii.skills.events.BlacksmithySkillEvent;
import com.github.kotooriiii.skills.events.MiningSkillEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import jdk.nashorn.internal.ir.Block;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class MinerHutChapter extends AbstractChapter {


    private boolean isFirstTime = true;

    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 6));
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        sendMessage(player, "A miner station!\nYour pickaxe is considerably low. You can use the Blacksmithy skill to repair it.\nHold it in your hand and type: /repair");

        boolean exists = false;

        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack itemStack : contents) {
            if (itemStack == null)
                continue;
            if (itemStack.getType() != Material.IRON_PICKAXE)
                continue;
            exists = true;
            ItemMeta meta = itemStack.getItemMeta();
            Damageable dam = (Damageable) meta;

            if (dam.getDamage() == 0) {
                dam.setDamage(50);
                itemStack.setItemMeta(meta);
            }
        }

        if (!exists) {
            ItemStack itemStack = new ItemStack(Material.IRON_PICKAXE, 1);
            itemStack.addEnchantment(Enchantment.DIG_SPEED, 5);
            itemStack.addEnchantment(Enchantment.DURABILITY, 1);
            ItemMeta meta = itemStack.getItemMeta();
            Damageable dam = (Damageable) meta;
            dam.setDamage(50);
            itemStack.setItemMeta(meta);
            player.getInventory().addItem(itemStack);
        }

    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onRepair(BlacksmithySkillEvent event) {

        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if (!event.getType().equals(BlacksmithyType.REPAIR)) {
            sendMessage(event.getPlayer(), "This operation is not supported in the tutorial phase.");
            event.setCancelled(true);
            return;
        }


        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        sendMessage(event.getPlayer(), "You repaired your pickaxe!\nNow keep moving forward!");

        setComplete();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        sendMessage(event.getPlayer(), "You must repair your pickaxe before continuing.");
        event.setCancelled(true);
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
        sendMessage(event.getPlayer(), "The Mining skill awards you extra drops when mining stone.");
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
