package com.github.kotooriiii.enderdragon.strategy.fight;

import com.comphenix.net.sf.cglib.asm.$ByteVector;
import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.enderdragon.EnderDragonManager;
import com.github.kotooriiii.enderdragon.entity.LSEnderDragon;
import com.github.kotooriiii.enderdragon.entity.LSWither;
import com.github.kotooriiii.status.Staff;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DefaultFightStrategy extends FightStrategy {


    boolean firstTroops, secondTroops, thirdTroops;
    private ArrayList<Entity> entities;

    public DefaultFightStrategy(EnderDragonManager manager) {
        super(manager);
        entities = new ArrayList<>();
    }

    @Override
    public void damage(double oldRatio, double newRatio) {

        //todo smthn else
        if (oldRatio > 0.85 && newRatio <= 0.85) {

            if (!firstTroops) {
                firstTroops = true;

                final TextComponent textComponent = new TextComponent("The Ender Dragon has called for allies.");
                textComponent.setColor(ChatColor.RED);
                Bukkit.broadcast(textComponent);
                //spawn 30 phantoms, note: ai
                spawnPhantoms((EnderDragon) this.manager.getSpawnStrategy().getLSEnderDragon().getBukkitEntity(), 30);
            }
        }
        if (oldRatio > 0.65 && newRatio <= 0.65) {


            if (!secondTroops) {
                secondTroops = true;

                final TextComponent textComponent = new TextComponent("The Ender Dragon has called for his stronger allies.");
                textComponent.setColor(ChatColor.RED);
                Bukkit.broadcast(textComponent);

                //spawn 1 withers, same note
                spawnWithers((EnderDragon) this.manager.getSpawnStrategy().getLSEnderDragon().getBukkitEntity(), 1);
                spawnPhantoms((EnderDragon) this.manager.getSpawnStrategy().getLSEnderDragon().getBukkitEntity(), 15);
            }
        }
        if (oldRatio > 0.45 && newRatio <= 0.45) {

            if (!thirdTroops) {
                thirdTroops = true;

                final TextComponent textComponent = new TextComponent("The Ender Dragon has called for his last allies.");
                textComponent.setColor(ChatColor.RED);
                Bukkit.broadcast(textComponent);

                //spawn 2 withers, same note

                spawnWithers((EnderDragon) this.manager.getSpawnStrategy().getLSEnderDragon().getBukkitEntity(), 2);
                spawnPhantoms((EnderDragon) this.manager.getSpawnStrategy().getLSEnderDragon().getBukkitEntity(), 15);
            }
        }

        if (oldRatio > 0.25 && newRatio <= 0.25) {

            final TextComponent textComponent = new TextComponent("The Ender Dragon calls for defensive strategy.");
            textComponent.setColor(ChatColor.RED);
            Bukkit.broadcast(textComponent);

            EnderDragon dw = (EnderDragon) manager.getSpawnStrategy().getLSEnderDragon().getBukkitEntity();
            dw.setPhase(EnderDragon.Phase.LAND_ON_PORTAL);

            for (Entity entity : entities) {
                if (entity.getType() == EntityType.WITHER) {
                    final net.minecraft.server.v1_16_R3.Entity handle = ((CraftEntity) entity).getHandle();
                    if (handle instanceof LSWither) {
                        LSWither wither = (LSWither) handle;
                        wither.setHealingDragon(true);
                        Wither vanillaWither = (Wither) entity;
                        vanillaWither.setTarget((EnderDragon) this.manager.getSpawnStrategy().getLSEnderDragon().getBukkitEntity());
                    }
                }
            }
        }

        if (newRatio <= 0) {
            entities.clear();
        }
    }

    @Override
    public void heal(double oldRatio, double newRatio) {
        if (newRatio >= 1) {
            for (Entity entity : entities) {
                if (entity.getType() == EntityType.WITHER) {
                    final net.minecraft.server.v1_16_R3.Entity handle = ((CraftEntity) entity).getHandle();
                    if (handle instanceof LSWither) {
                        LSWither wither = (LSWither) handle;
                        wither.setHealingDragon(false);
                        Wither vanillaWither = (Wither) entity;
                        vanillaWither.setTarget(targetRandomly((EnderDragon) manager.getSpawnStrategy().getLSEnderDragon().getBukkitEntity()));
                    }
                }
            }
        }
    }

    @Override
    public void end() {
        for (Entity entity : entities) {
            if (entity.getType() == EntityType.WITHER) {
                final net.minecraft.server.v1_16_R3.Entity handle = ((CraftEntity) entity).getHandle();
                if (handle instanceof LSWither) {
                    LSWither wither = (LSWither) handle;
                    wither.setHealingDragon(false);
                    Wither vanillaWither = (Wither) entity;
                    vanillaWither.setTarget(targetRandomly((EnderDragon) manager.getSpawnStrategy().getLSEnderDragon().getBukkitEntity()));
                }
            }
        }
    }

    private void spawnWithers(EnderDragon enderDragon, int count) {
        final Random random = new Random();
        final int[] offset = {0};
        for (final int[] i = {0}; i[0] < count; i[0]++) {
            int x = random.nextInt(20 * 2 + 1) - 20;
            int y = 85;
            int z = random.nextInt(20 * 2 + 1) - 20;

            offset[0] = offset[0] + 20;

            new BukkitRunnable() {
                @Override
                public void run() {

                    Location location = new Location(enderDragon.getWorld(), x, y, z);

                    LSWither lsWither = new LSWither(location);
                    WorldServer world = ((CraftWorld) LostShardPlugin.getEndWorld()).getHandle(); // Creates and NMS world
                    boolean b = world.addEntity(lsWither, CreatureSpawnEvent.SpawnReason.CUSTOM); // Adds the entity to the world.
                    if (!b) {
                        spawnWithers(enderDragon, 1);
                        return;
                    }
                    location.createExplosion(1);


                    final Wither wither = (Wither) lsWither.getBukkitEntity();
                    entities.add(wither);

                    wither.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
                }
            }.runTaskLater(LostShardPlugin.plugin, offset[0]);

        }
    }


    private void spawnPhantoms(EnderDragon enderDragon, int count) {
        Random random = new Random();
        final int[] offset2 = {0};

        final Player player = targetRandomly(enderDragon);
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(20 * 2 + 1) - 20;
            int y = 74;
            int z = random.nextInt(20 * 2 + 1) - 20;

            offset2[0] = offset2[0] + 5;

            new BukkitRunnable() {
                @Override
                public void run() {

                    final Location location = new Location(enderDragon.getWorld(), x, y, z);
                    location.createExplosion(1);
                    final Phantom phantom = (Phantom) location.getWorld().spawnEntity(location, EntityType.PHANTOM);
                    entities.add(phantom);

                    if (player != null)
                        phantom.setTarget(player);

                }
            }.runTaskLater(LostShardPlugin.plugin, offset2[0]);

        }
    }

    private Player targetRandomly(EnderDragon dragon) {

        List<Player> players = new ArrayList<>(dragon.getWorld().getPlayers());

        players.removeIf(p -> p.getGameMode() != GameMode.SURVIVAL);

        if (players.isEmpty())
            return null;

        boolean isFound = false;
        Player foundPlayer = null;
        while (!isFound) {
            final Player player = players.get(new Random().nextInt(players.size()));
            if (player.getGameMode() != GameMode.SURVIVAL)
                continue;
            isFound = true;
            foundPlayer = player;
            break;
        }
        return foundPlayer;
    }

}
