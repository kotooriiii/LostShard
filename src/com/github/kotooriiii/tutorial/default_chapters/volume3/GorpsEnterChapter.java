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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
        clan.update(player.getUniqueId(), false);
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


        if (!exists) {

            //kill zombs
            for (Entity entity : event.getPlayer().getWorld().getNearbyEntities(event.getPlayer().getLocation(), 100, 100, 100)) {
                if (!(entity instanceof Zombie))
                    continue;
                if (entity.isDead())
                    continue;
                entity.remove();
            }

            spawnZombies();
            spawnFlies();
        }


        if (!event.getPlayer().getInventory().contains(Material.BOW)) {
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemMeta meta = bow.getItemMeta();
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            bow.setItemMeta(meta);
            event.getPlayer().getInventory().setItem(1, bow);
            event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 1));
        }

        event.setWins(2);
    }

    private void spawnFlies() {

        World world = LostShardPlugin.getTutorialManager().getTutorialWorld();
        final int x = 317, y = 104, z = 924;
        final int delayer = 20 * 1;

        Location[] locations = new Location[]
                {
                        new Location(world, x + 7, y, z - 7),
                        new Location(world, x + 7, y, z + 7),
                        new Location(world, x - 7, y, z + 7),
                        new Location(world, x - 7, y, z - 7)
                };
        final int[] currentIndex = {0};

        new BukkitRunnable() {
            @Override
            public void run() {

                if (currentIndex[0] == locations.length) {
                    this.cancel();
                    return;
                }

                //spawn and then inc by one
                spawnFly(locations[currentIndex[0]++]);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, delayer);
    }

    private void spawnFly(Location locD) {

        Skeleton riderD = (Skeleton) LostShardPlugin.getTutorialManager().getTutorialWorld().spawnEntity(locD, EntityType.SKELETON);
        riderD.setCustomName("[Tutorial] Skelly");
        riderD.setCustomNameVisible(true);
        riderD.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 5, 2));
        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
        riderD.getEquipment().setItemInMainHand(bow);

        Phantom phantomD = (Phantom) LostShardPlugin.getTutorialManager().getTutorialWorld().spawnEntity(locD, EntityType.PHANTOM);
        phantomD.setCustomName("[Tutorial] Phantom");
        phantomD.setCustomNameVisible(true);
        phantomD.addPassenger(riderD);
        phantomD.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 2));
        final Player player = Bukkit.getPlayer(getUUID());
        if (player != null)
            riderD.setTarget(player);
        phantomD.setTarget(player);

        riderD.getWorld().playSound(riderD.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10, 0);
        riderD.getWorld().spawnParticle(Particle.FLASH, riderD.getLocation(), 5, 0, 0, 0);
    }

    private void spawnZombies() {

        Location locA = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 317, 86, 943);
        Location locB = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 298, 86, 924);
        Location locC = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 317, 86, 908);

        spawnZombie(locA);
        spawnZombie(locB);
        spawnZombie(locC);

    }

    private void spawnZombie(Location loc) {
        Zombie zombie = (Zombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 60 * 5, 1, true, true, true));
        zombie.setCustomName("[Tutorial] Zombie");
        zombie.setCustomNameVisible(true);
        Player player = Bukkit.getPlayer(getUUID());
        if (player != null)
            zombie.setTarget(player);
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


        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        sendMessage(event.getPlayer(), "Congratulations! You've captured Gorps! You've been awarded 100 gold for your efforts and your max mana has increased to 115 for 24 hours.");
        event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 100));

        startGame(null);
    }
}
