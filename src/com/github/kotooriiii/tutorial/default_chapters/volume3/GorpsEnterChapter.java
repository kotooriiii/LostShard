package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.hostility.HostilityMatch;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.hostility.events.PlatformCaptureEvent;
import com.github.kotooriiii.hostility.events.PlatformStartEvent;
import com.github.kotooriiii.hostility.events.PlatformVictoryEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import static com.github.kotooriiii.data.Maps.*;

public class GorpsEnterChapter extends AbstractChapter {

    private boolean isComplete;
    private Zone zone;

    public GorpsEnterChapter() {
        this.isComplete = false;
        this.zone = new Zone(351, 354, 77, 73, 961, 958);
    }

    @Override
    public void onBegin() {


    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximityA(PlayerMoveEvent event) {
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
        final Player player = event.getPlayer();
        setLocation(event.getTo());
        Clan clan = new Clan(player.getName(), player.getUniqueId());
        LostShardPlugin.getClanManager().addClan(clan, true);

        sendMessage(player, "You've made it to Gorps!\nHead to the center to capture it.");

        new BukkitRunnable() {
            @Override
            public void run() {
                startGame(player);
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);
        //available
    }

    private void startGame(Player player) {
        for (HostilityPlatform platform : platforms) {

            if (platform.getName().equalsIgnoreCase("Gorps")) {

                for (HostilityMatch match : activeHostilityGames) {
                    if (match.getPlatform().getName().equalsIgnoreCase("Gorps")) {
                        if (player != null)
                            sendMessage(player, ERROR_COLOR + "Gorps is currently being played on. You must fight for it or wait your turn.");
                        return;
                    }
                }
                HostilityMatch match = new HostilityMatch(platform);
                match.startGame();
                return;
            }
        }
    }

    @EventHandler
    public void onStart(PlatformStartEvent event) {
        if (!isActive())
            return;

        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        player.sendMessage(ChatColor.GOLD + event.getPlatform().getName() + " is now available for capture.");
    }


    @EventHandler
    public void onCap(PlatformCaptureEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        sendMessage(event.getPlayer(), "Stay on the platform without getting knocked off to capture it!");

        boolean exists = false;
        for (Entity entity : event.getPlayer().getWorld().getNearbyEntities(event.getPlayer().getLocation(), 100, 100, 100)) {
            if (entity instanceof Skeleton || entity instanceof Phantom) {
                exists = true;
                break;
            }
        }

        if(!exists) {
            final int x = 317, y=104, z=924;
            final int delayer = 20*2;

            Location locA = new Location(event.getPlayer().getWorld(), x + 25, y, z - 25);
            Entity riderA = LostShardPlugin.getTutorialManager().getTutorialWorld().spawnEntity(locA, EntityType.SKELETON);
            LostShardPlugin.getTutorialManager().getTutorialWorld().spawnEntity(locA, EntityType.PHANTOM).addPassenger(riderA);
            riderA.getWorld().playSound(riderA.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10, 0);
            riderA.getWorld().spawnParticle(Particle.FLASH, riderA.getLocation(), 5,0,0,0);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Location locB = new Location(event.getPlayer().getWorld(), x + 25, y, z + 25);

                    Entity riderB = LostShardPlugin.getTutorialManager().getTutorialWorld().spawnEntity(locB, EntityType.SKELETON);
                    LostShardPlugin.getTutorialManager().getTutorialWorld().spawnEntity(locB, EntityType.PHANTOM).addPassenger(riderB);
                    riderB.getWorld().playSound(riderB.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10, 0);
                    riderB.getWorld().spawnParticle(Particle.FLASH, riderB.getLocation(), 5,0,0,0);
                    this.cancel();
                }
            }.runTaskLater(LostShardPlugin.plugin, delayer);


            new BukkitRunnable() {
                @Override
                public void run() {
                    Location locC = new Location(event.getPlayer().getWorld(), x - 25, y, z + 25);
                    Entity riderC = LostShardPlugin.getTutorialManager().getTutorialWorld().spawnEntity(locC, EntityType.SKELETON);
                    LostShardPlugin.getTutorialManager().getTutorialWorld().spawnEntity(locC, EntityType.PHANTOM).addPassenger(riderC);
                    riderC.getWorld().playSound(riderC.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10, 0);
                    riderC.getWorld().spawnParticle(Particle.FLASH, riderC.getLocation(), 5,0,0,0);
                    this.cancel();
                }
            }.runTaskLater(LostShardPlugin.plugin, delayer * 2);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Location locD = new Location(event.getPlayer().getWorld(), x - 25, y, z - 25);

                    Entity riderD = LostShardPlugin.getTutorialManager().getTutorialWorld().spawnEntity(locD, EntityType.SKELETON);
                    LostShardPlugin.getTutorialManager().getTutorialWorld().spawnEntity(locD, EntityType.PHANTOM).addPassenger(riderD);
                    riderD.getWorld().playSound(riderD.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10, 0);
                    riderD.getWorld().spawnParticle(Particle.FLASH, riderD.getLocation(), 5,0,0,0);
                    this.cancel();
                }
            }.runTaskLater(LostShardPlugin.plugin, delayer * 3);

        }


        if (!event.getPlayer().getInventory().contains(Material.BOW)) {
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemMeta meta = bow.getItemMeta();
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            bow.setItemMeta(meta);
            event.getPlayer().getInventory().addItem(bow, new ItemStack(Material.ARROW, 1));
        }

        event.setWins(2);
    }

    @EventHandler
    public void onWin(PlatformVictoryEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        LostShardPlugin.getClanManager().removeClan(LostShardPlugin.getClanManager().getClan(event.getPlayer().getUniqueId()));
        setComplete();

        for (Entity entity : event.getPlayer().getWorld().getNearbyEntities(event.getPlayer().getLocation(), 100, 100, 100)) {
            if (entity instanceof Skeleton || entity instanceof Phantom) {
                ((Damageable) entity).damage(1000.0f);
                entity.getWorld().createExplosion(entity.getLocation(), 4.0f);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, entity.getLocation(), 5, 0, 0, 0);
                    }
                }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);

            }
        }


        sendMessage(event.getPlayer(), "Congratulations! You've captured Gorps! You've been awarded 100 gold for your efforts and your max mana has increased to 115 for 24 hours.");
        event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 100));
        startGame(null);
    }
}
