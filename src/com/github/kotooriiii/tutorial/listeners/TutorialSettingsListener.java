package com.github.kotooriiii.tutorial.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.events.TutorialPlayerDeathEvent;
import com.github.kotooriiii.tutorial.TutorialBook;
import com.github.kotooriiii.tutorial.TutorialCompleteType;
import com.github.kotooriiii.tutorial.TutorialManager;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageByEntityEvent ev) //Listens to EntityDamageEvent
    {
        if (ev.getEntity() instanceof Player && ev.getDamager() instanceof Player) {
            ev.setCancelled(true);
            return;
        }

        if (ev.getEntity() instanceof Player && ev.getDamager() instanceof AbstractArrow) {
            if (((AbstractArrow) ev.getDamager()).getShooter() instanceof Player) {
                ev.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageEvent ev) //Listens to EntityDamageEvent
    {

        if (CitizensAPI.getNPCRegistry().isNPC(ev.getEntity()))
            return;

        if (ev.getEntity() instanceof Player)
            return;

        Player player = (Player) ev.getEntity();
        if (player.getHealth() - ev.getFinalDamage() <= 0) {
            TutorialBook book = LostShardPlugin.getTutorialManager().wrap(player.getUniqueId());
            if (book == null)
                return;

            LostShardPlugin.plugin.getServer().getPluginManager().callEvent(new TutorialPlayerDeathEvent(player));

            //Cancel damage event
            ev.setCancelled(true);

            //Set back to living
            player.teleport(book.getCurrentChapter().getLocation());
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFireTicks(0);
            for (PotionEffectType potionEffectType : PotionEffectType.values())
                player.removePotionEffect(potionEffectType);
        }

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

        class Pair {
            private Zone z;
            private Material[] mats;
            private int seconds;

            public Pair(Zone z, Material[] mat, int seconds) {
                this.z = z;
                this.mats = mat;
                this.seconds = seconds;
            }

            public Material[] getMats() {
                return mats;
            }

            public int getSeconds() {
                return seconds;
            }

            public Zone getZ() {
                return z;
            }
        }

        Pair[] pairs = new Pair[]{
                new Pair(new Zone(372, 316, 38, 102, 723, 634), new Material[]{Material.IRON_ORE, Material.GOLD_ORE}, 7),
                new Pair(new Zone(392, 371, 38, 41, 719, 723), new Material[]{Material.IRON_ORE, Material.GOLD_ORE, Material.STONE, Material.ANDESITE}, 30),
                new Pair(new Zone(403, 421, 38, 40, 722, 719), new Material[]{Material.IRON_ORE, Material.GOLD_ORE, Material.STONE, Material.ANDESITE}, 30),
                new Pair(new Zone(746, 691, 48, 75, 1061, 1131), new Material[]{Material.MELON}, 5)
        };

        final Material type = event.getBlock().getType();

        boolean exists = false;
        int seconds = 0;


        out:
        for (Pair pair : pairs) {
            Zone z = pair.getZ();
            Material[] mats = pair.getMats();
            if (z.contains(event.getBlock().getLocation())) {
                for (Material m : mats) {
                    if (m == type) {
                        exists = true;
                        seconds = pair.getSeconds();
                        break out;
                    }
                }
            }

            if (z.hasAdjacency(event.getBlock().getLocation()))
                event.getPlayer().sendMessage(ERROR_COLOR + "Don't mine this way! You are going the wrong direction.");
        }


        if (!exists)
            event.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getBlock().setType(type);
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * seconds);
    }

    @EventHandler
    public void onGrav(EntityChangeBlockEvent event) {
        Block b = event.getBlock();
        final Material mat = event.getTo();
        Entity e = event.getEntity();


        if (!(e instanceof FallingBlock)) {
            return;
        }

        if (mat != Material.SAND && mat != Material.GRAVEL) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getBlock().getType() == Material.AIR) {
                    event.getBlock().setType(mat);
                }
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 15);


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
