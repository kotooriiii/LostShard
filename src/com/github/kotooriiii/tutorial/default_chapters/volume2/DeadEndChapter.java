package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.skills.events.MiningSkillEvent;
import com.github.kotooriiii.skills.events.SkillLevelUpEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DeadEndChapter extends AbstractChapter {

    private Zone zone;
    private boolean isComplete;
    private boolean isFirstTime;

    public DeadEndChapter() {
        isComplete = false;
        isFirstTime = true;
        this.zone = new Zone(393, 400, 40, 37, 723, 718);
    }

    @Override
    public void onBegin() {

        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        sendMessage(player, "Looks like a dead end.\nMine through and see if there's anything on the other side...");
    }

    @Override
    public void onDestroy() {

    }
    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(event.isCancelled())
            return;
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if(itemStack == null || itemStack.getEnchantmentLevel(Enchantment.DIG_SPEED) == 0)
        {
            sendMessage(event.getPlayer(), "You should probably use the pickaxe in the chest...");
            event.setCancelled(true);
        }

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



    @EventHandler
    public void onLevelUp(SkillLevelUpEvent event) {
        if (!event.getUUID().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!isFirstTime)
            return;
        isFirstTime = false;

        Player player = Bukkit.getPlayer(getUUID());
        if(player != null)
        sendMessage(player, "“Your mining level increases as you mine smooth stone...");
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
        setComplete();
    }

}
