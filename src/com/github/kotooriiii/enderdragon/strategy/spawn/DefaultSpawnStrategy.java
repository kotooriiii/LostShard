package com.github.kotooriiii.enderdragon.strategy.spawn;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.enderdragon.EnderDragonManager;
import com.github.kotooriiii.enderdragon.entity.LSEnderDragon;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

import static org.bukkit.Material.*;
import static org.bukkit.Material.PURPLE_WOOL;

public class DefaultSpawnStrategy extends SpawnStrategy {

    private static final Material[] wools = new Material[]{WHITE_WOOL, RED_WOOL, BLUE_WOOL, ORANGE_WOOL, CYAN_WOOL, BLACK_WOOL, GRAY_WOOL, BROWN_WOOL, LIME_WOOL, GREEN_WOOL, MAGENTA_WOOL, LIGHT_BLUE_WOOL, YELLOW_WOOL, PINK_WOOL, LIGHT_GRAY_WOOL, PURPLE_WOOL};

    public DefaultSpawnStrategy(EnderDragonManager enderDragonManager) {
        super(enderDragonManager);
    }

    protected void spawn() {
        spawnCenterCrystal();
    }

    /**
     * Spawns the center crystals
     */
    private void spawnCenterCrystal() {
        int coordinates[][] = new int[][]{{5, -5}, {-5, -5}, {5, 5}, {-5, 5}};

        ArrayList<EnderCrystal> crystals = new ArrayList<>();

        outer:
        for (int i = 0; i < coordinates.length; i++) {

            int x = coordinates[i][0];
            int z = coordinates[i][1];
            Block block = getSuitableCrystalLocation(x, z);

            if (block == null)
                continue;
            for (Entity entity : block.getLocation().getNearbyEntitiesByType(EnderCrystal.class, 0.75f, 0.75f, 0.75f)) {
                if (entity.getType() == EntityType.ENDER_CRYSTAL) {
                    crystals.add((EnderCrystal) entity);
                    continue outer;
                }
            }

            final EnderCrystal crystal = (EnderCrystal) block.getWorld().spawnEntity(block.getLocation().add(0.5, 0, 0.5), EntityType.ENDER_CRYSTAL, CreatureSpawnEvent.SpawnReason.CUSTOM);
            crystals.add(crystal);

        }

        crystals.forEach(crystal -> {crystal.setBeamTarget(this.manager.getEnderDragonSpawnLocation()); crystal.setInvulnerable(false);});

        new BukkitRunnable() {
            @Override
            public void run() {
                spawnCrystals(crystals);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 5);
    }

    /**
     * Spawns crystals on top of obsidian pillars
     * @param crystals The crystals on the center
     */
    private void spawnCrystals(ArrayList<EnderCrystal> crystals) {
        int coordinates[][] = new int[][]{{42, 0}, {33, 24}, {12, 39}, {-13, 39}, {-34, 24}, {-42, -1}, {-34, -25}, {-13, -40}, {12, -40}, {33, -25}};


        int timer = 20 * 3;
        int offset = 0;


        outer:
        for (int i = 0; i < coordinates.length; i++) {

            int x = coordinates[i][0];
            int z = coordinates[i][1];

            Block block = getSuitableCrystalLocation(x, z);
            if (block == null) {

                // Bukkit.broadcastMessage("Debug: No bedrock found at [" + x + "," + z + "]");
                continue;
            }
            for (Entity entity : block.getLocation().getNearbyEntitiesByType(EnderCrystal.class, 1, 1, 1)) {
                if (entity.getType() == EntityType.ENDER_CRYSTAL)
                    continue outer;
            }

            crystals.forEach(c -> c.setInvulnerable(true));


            new BukkitRunnable() {
                @Override
                public void run() {
                    crystals.forEach(crystal -> crystal.setBeamTarget(block.getLocation().clone().add(0, 1, 0)));
                    block.getWorld().createExplosion(block.getLocation(), 1F, false, true);
                    block.getWorld().spawnEntity(block.getLocation().add(0.5, 0, 0.5), EntityType.ENDER_CRYSTAL, CreatureSpawnEvent.SpawnReason.CUSTOM);
                    surroundWithIron(block.getRelative(BlockFace.DOWN));
                }
            }.runTaskLater(LostShardPlugin.plugin, timer + (offset++ * timer));

        }

        new BukkitRunnable() {
            @Override
            public void run() {
                crystals.forEach(crystal -> {crystal.setBeamTarget(null); crystal.setInvulnerable(false);});
                spawnEnderDragon();
            }
        }.runTaskLater(LostShardPlugin.plugin, timer + (offset++ * timer));
    }

    /**
     * Spawns
     */
    private void spawnEnderDragon() {

        LSEnderDragon enderDragon = new LSEnderDragon(this.manager.getEnderDragonSpawnLocation());
        WorldServer world = ((CraftWorld) LostShardPlugin.getEndWorld()).getHandle(); // Creates and NMS world
        world.addEntity(enderDragon); // Adds the entity to the world

        final CraftEntity bukkitEntity = enderDragon.getBukkitEntity();

        final EnderDragon enderDragonBukkit = (EnderDragon) bukkitEntity;

        AttributeInstance healthAttribute = enderDragonBukkit.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.setBaseValue(500);
        }
        enderDragonBukkit.setHealth(healthAttribute.getBaseValue());
        enderDragonBukkit.setPhase(EnderDragon.Phase.CIRCLING);

        this.enderDragon = enderDragon;
        spawnComplete();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!manager.getCooldownStrategy().isAlive())
                    return;
                final TextComponent textComponent = new TextComponent("The Ender Dragon has fled.");
                textComponent.setColor(ChatColor.RED);
                Bukkit.broadcast(textComponent);

                LostShardPlugin.getEnderDragonManager().kill();
            }
        }.runTaskLater(LostShardPlugin.plugin, 20*60*60);

    }

    /**
     * Get the crystal location
     * @param x X coord
     * @param z Z coord
     * @return Two blocks above bedrock
     */
    private static Block getSuitableCrystalLocation(int x, int z) {
        for (int y = 0; y < 256; y++) {
            Block block = LostShardPlugin.getEndWorld().getBlockAt(x, y, z);
            if (block.getType() == Material.BEDROCK)
                return block.getRelative(BlockFace.UP).getRelative(BlockFace.UP);
        }
        return null;
    }

    /**
     * Randomizes a color for wool
     * @return
     */
    private static Material woolRandomizer() {


        return wools[new Random().nextInt(wools.length)];
    }

    /**
     * Surrounds the bedrock block with iron bars
     * @param bedrockBlock
     */
    private static void surroundWithIron(Block bedrockBlock) {

        Material wool;

        wool = woolRandomizer();


        for (int x = bedrockBlock.getX() - 2; x <= bedrockBlock.getX() + 2; x++) {
            for (int z = bedrockBlock.getZ() - 2; z <= bedrockBlock.getZ() + 2; z++) {
                bedrockBlock.getWorld().getBlockAt(x, bedrockBlock.getY() + 2, z).setType(Material.IRON_BARS, true);
            }
        }

        for (int x = bedrockBlock.getX() - 2; x <= bedrockBlock.getX() + 2; x++) {
            for (int y = bedrockBlock.getY() - 1; y <= bedrockBlock.getY() + 3; y++) {

                if (x == bedrockBlock.getX() - 2 || x == bedrockBlock.getX() + 2)
                    bedrockBlock.getWorld().getBlockAt(x, y, bedrockBlock.getZ() + 2).setType(wool, true);
                else
                    bedrockBlock.getWorld().getBlockAt(x, y, bedrockBlock.getZ() + 2).setType(Material.IRON_BARS, true);
            }
        }

        for (int x = bedrockBlock.getX() - 2; x <= bedrockBlock.getX() + 2; x++) {
            for (int y = bedrockBlock.getY() - 1; y <= bedrockBlock.getY() + 3; y++) {

                if (x == bedrockBlock.getX() - 2 || x == bedrockBlock.getX() + 2)
                    bedrockBlock.getWorld().getBlockAt(x, y, bedrockBlock.getZ() - 2).setType(wool, true);
                else
                    bedrockBlock.getWorld().getBlockAt(x, y, bedrockBlock.getZ() - 2).setType(Material.IRON_BARS, true);
            }
        }

        for (int z = bedrockBlock.getZ() - 2; z <= bedrockBlock.getZ() + 2; z++) {
            for (int y = bedrockBlock.getY() - 1; y <= bedrockBlock.getY() + 3; y++) {

                if (z == bedrockBlock.getZ() - 2 || z == bedrockBlock.getZ() + 2)
                    bedrockBlock.getWorld().getBlockAt(bedrockBlock.getX() + 2, y, z).setType(wool, true);
                else

                    bedrockBlock.getWorld().getBlockAt(bedrockBlock.getX() + 2, y, z).setType(Material.IRON_BARS, true);
            }
        }

        for (int z = bedrockBlock.getZ() - 2; z <= bedrockBlock.getZ() + 2; z++) {
            for (int y = bedrockBlock.getY() - 1; y <= bedrockBlock.getY() + 3; y++) {
                if (z == bedrockBlock.getZ() - 2 || z == bedrockBlock.getZ() + 2)
                    bedrockBlock.getWorld().getBlockAt(bedrockBlock.getX() - 2, y, z).setType(wool, true);
                else
                    bedrockBlock.getWorld().getBlockAt(bedrockBlock.getX() - 2, y, z).setType(Material.IRON_BARS, true);
            }
        }


    }


}
