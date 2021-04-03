package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.util.HelperMethods;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftWolf;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class EnderDragonLivesListener implements Listener {

    private static boolean isCooldown = false;
    private static UUID lastKillerUUID = null;
    private static ZonedDateTime killDate = null;
    private static ZonedDateTime summonDate = null;
    private static boolean isAlive = false;

    public static ZonedDateTime getKillDate() {
        return killDate;
    }

    public static ZonedDateTime getSummonDate() {
        return summonDate;
    }

    public static boolean isAlive() {
        return isAlive;
    }

    @EventHandler
    public void onEndGateWayTP(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            event.setTo(new Location(event.getTo().getWorld(), 0, 256, 0));
        }
    }

    @EventHandler
    public void onSpawnDragon(EntitySpawnEvent event) {
        if (isCooldown)
            return;
        if (event.getEntity().getWorld().getEnvironment() != World.Environment.THE_END)
            return;
        if (event.getEntity().getType() != EntityType.ENDER_DRAGON)
            return;
        final TextComponent textComponent = new TextComponent("The Ender Dragon is in the process of being summoned again...");
        textComponent.setColor(ChatColor.RED);
        Bukkit.broadcast(textComponent);

        new BukkitRunnable() {
            @Override
            public void run() {
                final TextComponent textComponent = new TextComponent("The Ender Dragon has been summoned.");
                textComponent.setColor(ChatColor.RED);
                Bukkit.broadcast(textComponent);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 30);


    }


    @EventHandler
    public void onDragonKill(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.ENDER_DRAGON)
            return;
        if (event.getEntity().getWorld().getEnvironment() != World.Environment.THE_END)
            return;
        if (isCooldown)
            return;

        boolean hasDragonEgg = false;
        for (ItemStack itemStack : event.getDrops()) {
            if (itemStack.getType() == Material.DRAGON_EGG) {
                hasDragonEgg = true;
                break;
            }
        }


        if (!hasDragonEgg) {
            event.getDrops().add(new ItemStack(Material.DRAGON_EGG, 1));
        }

        if (event.getEntity().getKiller() != null) {
            Player player = HelperMethods.getPlayerDamagerONLY(event.getEntity(), event.getEntity().getKiller());
            lastKillerUUID = player == null ? lastKillerUUID : player.getUniqueId();
        }
        killDate = ZonedDateTime.now(ZoneId.of("America/New_York"));
        isAlive = false;
        final TextComponent textComponent = new TextComponent("The Ender Dragon was slain.");
        textComponent.setColor(ChatColor.RED);
        Bukkit.broadcast(textComponent);
        respawn();
    }

    public static void respawn() {

        isCooldown = true;


        if (lastKillerUUID != null && lastKillerUUID.toString().equals("8bf60cf3-008a-48fb-b3fd-da956a722cff")) {
            int timer = 20 * 5;
            summonDate = ZonedDateTime.now(ZoneId.of("America/New_York")).plusSeconds(5);
            final TextComponent textComponent = new TextComponent("The Ender Dragon will respawn in 5 seconds.");
            textComponent.setColor(ChatColor.RED);
            Bukkit.broadcast(textComponent);
            new BukkitRunnable() {
                @Override
                public void run() {
                    isCooldown = false;
                    isAlive = true;
                    spawn();


                }
            }.runTaskLater(LostShardPlugin.plugin, timer);
        } else {
            int hrs = Bukkit.getOnlinePlayers().size() < 20 ? 12 : 6;
            int timer = 20 * 60 * 60 * hrs;
            summonDate = ZonedDateTime.now(ZoneId.of("America/New_York")).plusHours(hrs);

            final TextComponent textComponent = new TextComponent("The Ender Dragon will respawn in " + (timer == 20 * 60 * 60 * 6 ? "6" : "12") + " hours.");
            textComponent.setColor(ChatColor.RED);
            Bukkit.broadcast(textComponent);
            new BukkitRunnable() {
                @Override
                public void run() {
                    isCooldown = false;
                    isAlive = true;

                    spawn();

                }
            }.runTaskLater(LostShardPlugin.plugin, timer);
        }


    }

    private static void spawn() {
        final World world = Bukkit.getWorld("LSWMAP3_the_end");

        Location enderDragonLocation = new Location(world, 0, 130, 0);

        final Chunk MyChunk = world.getChunkAt(enderDragonLocation);
        if (!MyChunk.isLoaded()) {
            MyChunk.setForceLoaded(true);
        }

        EnderDragon enderDragon = (EnderDragon) world.spawnEntity(enderDragonLocation, EntityType.ENDER_DRAGON);
        enderDragon.getDragonBattle().initiateRespawn();


        //world.spawn(enderDragonLocation, EnderDragon.class, drag -> this.setWerte((EnderDragon) drag));


    }

    private static void setWerte(final EnderDragon drag) {
        drag.setPhase(EnderDragon.Phase.CIRCLING);
    }

    private static Entity create(final EntityType entityType, final Location location) {
        try {
            final Object craftWorldObject = (CraftWorld) location.getWorld();
            final Object entity = craftWorldObject.getClass().getMethod("createEntity", Location.class, Class.class).invoke(craftWorldObject, location, entityType.getEntityClass());
            return (Entity) entity.getClass().getMethod("getBukkitEntity", (Class<?>[]) new Class[0]).invoke(entity, new Object[0]);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException ex2) {
            return null;
        }
    }
}
