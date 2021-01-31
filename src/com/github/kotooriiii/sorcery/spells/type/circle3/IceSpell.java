package com.github.kotooriiii.sorcery.spells.type.circle3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.HostilityMatch;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import com.google.common.base.Function;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.activeHostilityGames;

public class IceSpell extends Spell {

    private static HashMap<UUID, Double> iceSpellCooldownMap = new HashMap<UUID, Double>();

    private static HashMap<UUID, HashSet<Location>> uuidBlockPlacedMap = new HashMap<>();

    public static HashMap<UUID, HashSet<Location>> getUuidBlockPlacedMap() {
        return uuidBlockPlacedMap;
    }

    private IceSpell() {
        super(SpellType.ICE, "Creates a ball of ice in the direction you are facing. Good for trapping or slowing down enemies.",
                3,
                ChatColor.AQUA,
                new ItemStack[]{new ItemStack(Material.STRING, 1)},
                2.0f,
                15,
                true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.STRAY}, 0.15));


    }

    private  static IceSpell instance;
    public static IceSpell getInstance() {
        if (instance == null) {
            synchronized (IceSpell.class) {
                if (instance == null)
                    instance = new IceSpell();
            }
        }
        return instance;
    }

    @Override
    public boolean executeSpell(Player player) {
        // Get blocks in line of sight of player
        final int rangeIce = 32;
        final int despawnDelayIce = 7;
        List<Block> lineOfSightBlocks = player.getLineOfSight(null, rangeIce);

        // Get target block (last block in line of sight)
        Location centerBlock = lineOfSightBlocks.get(lineOfSightBlocks.size() - 1).getLocation();
        centerBlock.setX(centerBlock.getBlockX());
        centerBlock.setY(centerBlock.getBlockY());
        centerBlock.setZ(centerBlock.getBlockZ());
        centerBlock.setPitch(0);
        centerBlock.setYaw(0);
        //Should make it unable to spawn in the air
        if (centerBlock.getBlock().isEmpty()) {
            player.sendMessage(ERROR_COLOR + "Invalid target.");
            return false;
        }

        Material blockType = centerBlock.getBlock().getType();
        if (blockType != Material.LEGACY_LONG_GRASS)
            centerBlock.add(0, 1, 0);

        if (!hasIceShape(centerBlock, Material.SNOW_BLOCK, block -> block.isEmpty() || block.getType() == Material.LEGACY_LONG_GRASS)) {
            player.sendMessage(ERROR_COLOR + "You can not cast Ice Ball on hostility platforms.");
            return false;
        }
        setIceShape(player.getUniqueId(), centerBlock, Material.SNOW_BLOCK, block -> block.isEmpty() || block.getType() == Material.LEGACY_LONG_GRASS);

        // This runnable will remove the player from cooldown list after a given time
        new BukkitRunnable() {
            @Override
            public void run() {
                setIceShape(player.getUniqueId(), centerBlock, Material.AIR, block -> block.getType() == Material.SNOW_BLOCK);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * despawnDelayIce);
        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        iceSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    iceSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                iceSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (iceSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = iceSpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(0, RoundingMode.UP);
            int value = bd.intValue();
            if (value == 0)
                value = 1;

            String time = "seconds";
            if (value == 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }

    private boolean hasIceShape(Location centerBlock, Material material, Function<Block, Boolean> shouldReplace) {

        //Layer 0 and 2
        Location bottomLeft = centerBlock.clone().add(-1, 0, -1);
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());
                    Boolean isEdge = (x == 0 && z == 0) || (x == 0 && z == 2) || (x == 2 && z == 0) || (x == 2 && z == 2);

                    if (shouldReplaceBlock && !isEdge) {
                        for (HostilityMatch match : activeHostilityGames) {
                            HostilityPlatform platform = match.getPlatform();
                            if (platform.hasAdjacency(bottomLeft))
                                return false;
                        }
                    }
                    bottomLeft.add(0, 0, 1);

                }
                bottomLeft.add(1, 0, -3);
            }
            bottomLeft = centerBlock.clone().add(-1, 2, -1);
        }

        //Layer 1
        bottomLeft = centerBlock.clone().add(-1, 1, -1);
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());

                if (shouldReplaceBlock) {
                    for (HostilityMatch match : activeHostilityGames) {
                        HostilityPlatform platform = match.getPlatform();
                        if (platform.hasAdjacency(bottomLeft))
                            return false;
                    }
                }
                bottomLeft.add(0, 0, 1);

            }
            bottomLeft.add(1, 0, -3);
        }

        return true;
    }

    private void setIceShape(UUID uuid, Location centerBlock, Material material, Function<Block, Boolean> shouldReplace) {

        HashSet<Location> set = new HashSet<>();

        //Layer 0 and 2
        Location bottomLeft = centerBlock.clone().add(-1, 0, -1);
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());
                    Boolean isEdge = (x == 0 && z == 0) || (x == 0 && z == 2) || (x == 2 && z == 0) || (x == 2 && z == 2);

                    if (shouldReplaceBlock && !isEdge) {
                        bottomLeft.getBlock().setType(material);
                        if (material != Material.AIR) {
                            set.add(bottomLeft);
                        }
                    }
                    bottomLeft = bottomLeft.clone().add(0, 0, 1);

                }
                bottomLeft = bottomLeft.clone().add(1, 0, -3);
            }
            bottomLeft = centerBlock.clone().add(-1, 2, -1);
        }

        //Layer 1
        bottomLeft = centerBlock.clone().add(-1, 1, -1);
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());

                if (shouldReplaceBlock) {
                    bottomLeft.getBlock().setType(material);
                    if (material != Material.AIR) {
                        set.add(bottomLeft);
                    }
                }
                bottomLeft = bottomLeft.clone().add(0, 0, 1);

            }
           bottomLeft = bottomLeft.clone().add(1, 0, -3);
        }

        if (material == Material.AIR) {
            uuidBlockPlacedMap.remove(uuid, set);
        } else {
            uuidBlockPlacedMap.put(uuid, set);
        }

    }

}
