package com.github.kotooriiii.tutorial.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.SpellCastEvent;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.newt.TutorialBook;
import com.github.kotooriiii.tutorial.newt.TutorialCompleteType;
import com.github.kotooriiii.tutorial.newt.TutorialManager;
import com.google.common.eventbus.AllowConcurrentEvents;
import jdk.internal.org.objectweb.asm.commons.SerialVersionUIDAdder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import java.util.List;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class TutorialSettingsListener implements Listener {
    @EventHandler
    public void onLogOff(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final TutorialManager tutorialManager = LostShardPlugin.getTutorialManager();
        if (tutorialManager.isRestartWhenLoggedOff())
            tutorialManager.removeTutorial(player.getUniqueId(), TutorialCompleteType.RESET);

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(EntityDamageEvent ev) //Listens to EntityDamageEvent
    {

        if (ev.getEntity() instanceof Player) {
            LivingEntity livingEntity = (LivingEntity) ev.getEntity();
            if (livingEntity.getHealth() - ev.getFinalDamage() <= 0) {
                TutorialBook book = LostShardPlugin.getTutorialManager().wrap(livingEntity.getUniqueId());
                if (book == null)
                    return;
                ev.setCancelled(true);
                livingEntity.teleport(book.getCurrentChapter().getLocation());
                livingEntity.setHealth(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                livingEntity.setFireTicks(0);
                for (PotionEffectType potionEffectType : PotionEffectType.values())
                    livingEntity.removePotionEffect(potionEffectType);
            }
        }
    }

    @EventHandler
    public void onCast(SpellCastEvent event) {
        event.getPlayer().getInventory().setItem(9, new ItemStack(Material.FEATHER, 64));
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Spider && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL)
            return;

        final Zone zone = null;//todo implement the place they can MINE
        final Material type = event.getBlock().getType();

        if (!zone.contains(event.getBlock().getLocation())) {
            event.setCancelled(true);

            if (zone.hasAdjacency(event.getBlock().getLocation()))
                event.getPlayer().sendMessage(ERROR_COLOR + "Don't mine this way! You are going the wrong direction.");
            return;
        }

        if (type != Material.IRON_ORE && type != Material.GOLD_ORE && type != Material.STONE) {
            event.setCancelled(true);
            return;
        }


        new BukkitRunnable() {
            @Override
            public void run() {
                event.getBlock().setType(type);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 15);

        return;


    }

    @EventHandler
    public void onBlockBreak(BlockDropItemEvent event) {
        if (drop(event.getItems(), Material.IRON_ORE, Material.IRON_INGOT) || drop(event.getItems(), Material.GOLD_ORE, Material.GOLD_INGOT))
            event.setCancelled(true);
    }

    private boolean drop(List<Item> droppedItems, Material ore, Material ingot) {

        boolean hasOre = false;
        Location betterLocation = null;

        for (Item item : droppedItems) {
            if (item.getItemStack().getType().equals(ore)) {
                hasOre = true;
                betterLocation = item.getLocation();
                break;
            }
        }

        if (!hasOre)
            return false;

        betterLocation.getWorld().dropItemNaturally(betterLocation, new ItemStack(ingot, 3));
        return true;
    }

    @EventHandler
    public void itemDmg(PlayerItemDamageEvent event) {

        int dmg = event.getDamage() + 15;

        if (event.getItem().getType().getMaxDurability() <= dmg) {
            event.setDamage(0);
        } else
            event.setDamage(dmg);
    }
}
