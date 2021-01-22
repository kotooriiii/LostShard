package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.hostility.HostilityMatch;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.hostility.events.*;
import com.github.kotooriiii.tutorial.AbstractChapter;
import com.github.kotooriiii.tutorial.events.TutorialPlayerDeathEvent;

import net.minecraft.server.v1_16_R3.EntityPhantom;
import net.minecraft.server.v1_16_R3.EntitySkeleton;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityZombie;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class GorpsEnterChapter extends AbstractChapter {

    private boolean isComplete, isHologramSetup;
    private static BukkitTask task;
    private static HashSet<UUID> uuidsBreakSet = new HashSet<>();
    private int deathCounter = 0;
    private Zone zone;

    public GorpsEnterChapter() {
        this.isComplete = isHologramSetup = false;
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

        sendMessage(player, "You've made it to Gorps!\nHead to the center to capture it.", ChapterMessageType.HOLOGRAM_TO_TEXT);
        player.removePotionEffect(PotionEffectType.SPEED);

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
                        if (match.getCapturingPlayer() != null)
                            if (player != null)
                                sendMessage(player, ERROR_COLOR + "Gorps is currently being played on. You must fight for it or wait your turn.", ChapterMessageType.HELPER);
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
    public void onContest(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        final Player player = event.getPlayer();

        if (uuidsBreakSet.contains(player.getUniqueId()))
            return;

        for (HostilityMatch match : activeHostilityGames) {
            if (match.getPlatform().getName().equalsIgnoreCase("Gorps")) {
                if (match.getCapturingPlayer() == null)
                    return;
                if (match.getCapturingPlayer().getUniqueId().equals(getUUID()))
                    return;
                if (match.getPlatform().contains(event.getTo().getBlock())) {
                    event.getPlayer().sendMessage(ERROR_COLOR + "Gorps is currently being played on. You must fight for it or wait your turn.");

                    uuidsBreakSet.add(player.getUniqueId());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            uuidsBreakSet.remove(player.getUniqueId());
                        }
                    }.runTaskLater(LostShardPlugin.plugin, 20 * 5);

                }
            }
        }
    }

    @EventHandler
    public void onContest(PlayerTeleportEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        final Player player = event.getPlayer();

        if (uuidsBreakSet.contains(player.getUniqueId()))
            return;

        for (HostilityMatch match : activeHostilityGames) {
            if (match.getPlatform().getName().equalsIgnoreCase("Gorps")) {

                if (match.getPlatform().contains(event.getTo().getBlock())) {
                    event.getPlayer().sendMessage(ERROR_COLOR + "Gorps is currently being played on. You must fight for it or wait your turn.");

                    uuidsBreakSet.add(player.getUniqueId());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            uuidsBreakSet.remove(player.getUniqueId());
                        }
                    }.runTaskLater(LostShardPlugin.plugin, 20 * 3);

                }
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
    public void onDie(TutorialPlayerDeathEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        event.getPlayer().getInventory().setItem(2, new ItemStack(Material.MELON_SLICE, 64));
    }


    @EventHandler
    public void onCap(PlatformCaptureEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if (!isHologramSetup) {
            isHologramSetup = true;
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);

        }
        sendMessage(event.getPlayer(), "Stay on the platform without getting knocked off to capture it!", ChapterMessageType.HELPER);


        runLoopSpawn();


        if (!event.getPlayer().getInventory().contains(Material.BOW)) {
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemMeta meta = bow.getItemMeta();
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            bow.setItemMeta(meta);
            event.getPlayer().getInventory().setItem(2, bow);
            event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 1));
        }

        event.setWins(2);
    }

    private void runLoopSpawn() {

        if (task != null) {
            task.cancel();
        }

        task = new BukkitRunnable() {
            @Override
            public void run() {


                boolean isActive = false;

                for (HostilityMatch match : activeHostilityGames) {
                    if (match.getPlatform().getName().equalsIgnoreCase("Gorps")) {
                        if (match.getCapturingPlayer() != null)
                            isActive = true;
                    }
                }

                if (!isActive) {
                    this.cancel();
                    return;
                }
                if (isCancelled())
                    return;
                spawn();
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 20 * 13);
    }

    private void spawn() {
        spawnZombies();
        spawnFlies();
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
                        new Location(world, x - 7, y, z - 7),

                        new Location(world, x + 7, y, z - 7),
                        new Location(world, x - 7, y, z + 7),

                };
        final int[] currentIndex = {0};

        new BukkitRunnable() {
            @Override
            public void run() {

                if (currentIndex[0] == locations.length) {
                    this.cancel();
                    return;
                }


                boolean isActive = false;

                for (HostilityMatch match : activeHostilityGames) {
                    if (match.getPlatform().getName().equalsIgnoreCase("Gorps")) {
                        if (match.getCapturingPlayer() != null)
                            isActive = true;
                    }
                }

                if (!isActive) {
                    this.cancel();
                    return;
                }
                if (isCancelled())
                    return;

                //spawn and then inc by one
                spawnFly(locations[currentIndex[0]++]);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, delayer);
    }

    private void spawnFly(Location loc) {

        CraftWorld craftWorld = ((CraftWorld) loc.getWorld());
        final EntitySkeleton entitySkeleton = new EntitySkeleton(EntityTypes.SKELETON, craftWorld.getHandle());
        entitySkeleton.setPosition(loc.getX(), loc.getY(), loc.getZ());
        Skeleton skelly = (Skeleton) craftWorld.addEntity(entitySkeleton, CreatureSpawnEvent.SpawnReason.CUSTOM);

        skelly.setCustomName("[Tutorial] Skelly");
        skelly.setCustomNameVisible(true);
        skelly.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 5, 2));
        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
        skelly.getEquipment().setItemInMainHand(bow);

        final EntityPhantom entityPhantom = new EntityPhantom(EntityTypes.PHANTOM, craftWorld.getHandle());
        entityPhantom.setPosition(loc.getX(), loc.getY(), loc.getZ());
        Phantom phantomD = (Phantom) craftWorld.addEntity(entityPhantom, CreatureSpawnEvent.SpawnReason.CUSTOM);

        phantomD.setCustomName("[Tutorial] Phantom");
        phantomD.setCustomNameVisible(true);
        phantomD.addPassenger(skelly);
        phantomD.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 * 5, 2));
        final Player player = Bukkit.getPlayer(getUUID());
        if (player != null)
            skelly.setTarget(player);
        phantomD.setTarget(player);

        skelly.getWorld().playSound(skelly.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10, 0);
        skelly.getWorld().spawnParticle(Particle.FLASH, skelly.getLocation(), 5, 0, 0, 0);
    }

    private void spawnZombies() {

        Location locA = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 317, 86, 943);
        Location locB = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 298, 86, 924);
        Location locC = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 317, 86, 908);

        spawnZombie(locA);
        spawnZombie(locB);
        spawnZombie(locC);

        spawnZombie(locA);
        spawnZombie(locB);
        spawnZombie(locC);

    }

    private void spawnZombie(Location loc) {
        CraftWorld craftWorld = ((CraftWorld) loc.getWorld());
        final EntityZombie entityZombie = new EntityZombie(EntityTypes.ZOMBIE, craftWorld.getHandle());
        entityZombie.setPosition(loc.getX(), loc.getY(), loc.getZ());
        Zombie zombie = (Zombie) craftWorld.addEntity(entityZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);

        zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 60 * 5, 1, true, true, true));
        zombie.setCustomName("[Tutorial] Zombie");
        zombie.setCustomNameVisible(true);
        Player player = Bukkit.getPlayer(getUUID());
        if (player != null)
            zombie.setTarget(player);
    }

    @EventHandler
    public void onLoseControl(PlatformLoseEvent event) {

        if (event.getCapturingPlayer() == null || event.getCapturingClan() == null || event.getMatch() == null)
            return;
        if (!event.getCapturingPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!event.getMatch().getPlatform().getName().equalsIgnoreCase("Gorps"))
            return;

        killAll(event.getCapturingPlayer());
    }

    @EventHandler
    public void onDeath(TutorialPlayerDeathEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;


        for (HostilityMatch match : activeHostilityGames) {
            if (match.getPlatform().getName().equalsIgnoreCase("Gorps")) {
                if (match.getCapturingPlayer() != null) {
                    if (event.getPlayer().equals(match.getCapturingPlayer())) {

                        deathCounter++;
                        killAll(event.getPlayer());

                        switch (deathCounter) {
                            case 1:
                                sendMessage(event.getPlayer(), "Nice try! We'll give you an extra shot.", ChapterMessageType.HELPER);
                                break;
                            case 2:
                                sendMessage(event.getPlayer(), "That was close. This is your last shot. We made it easier this time!", ChapterMessageType.HELPER);
                                break;
                            case 3:
                                success(event.getPlayer(), false);
                                match.endGame(false);
                                break;
                        }
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onWin(PlatformVictoryEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;


        success(event.getPlayer(), true);
        event.setMessage("");

    }

    public void success(Player player, boolean isKillAll) {
        LostShardPlugin.getClanManager().removeClan(LostShardPlugin.getClanManager().getClan(player.getUniqueId()));
        setComplete();

        if (isKillAll)
            killAll(player);

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        sendMessage(player, ChatColor.GOLD + "Congratulations! You've captured Gorps! You've been awarded 100 gold for your efforts and your max mana has increased to 115 for 24 hours.", ChapterMessageType.HELPER);
        player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 100));
        startGame(null);

    }

    public void killAll(Player player) {
        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 60, 60, 60)) {
            if (entity instanceof Skeleton || entity instanceof Phantom || entity instanceof Zombie) {
                ((Damageable) entity).damage(1000.0f);

                entity.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, entity.getLocation(), 1);
                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10, 0);

                //entity.getWorld().createExplosion(entity.getLocation(), 4.0f);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, entity.getLocation(), 5, 0, 0, 0);
                    }
                }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);

            }
        }
    }
}
