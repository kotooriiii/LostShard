package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.HostilityMatch;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.google.common.base.Function;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.activeHostilityGames;


public class WebFieldSpell extends Spell {

    private static HashMap<UUID, Double> webFieldSpellCooldownMap = new HashMap<UUID, Double>();

    public WebFieldSpell() {
        super(SpellType.WEB_FIELD,
                ChatColor.AQUA,
                new ItemStack[]{new ItemStack(Material.STRING, 1)},
                1.0f,
                15,
                true, true, false);
    }

    @Override
    public boolean executeSpell(Player player)
    {
        final int rangeWeb = 32;
        final int despawnDelayWeb = 7;
        // Get blocks in line of sight of player
        List<Block> lineOfSightWeb = player.getLineOfSight(null, rangeWeb);

        // Get target block (last block in line of sight)
        final Location centerBlockWeb = lineOfSightWeb.get(lineOfSightWeb.size() - 1).getLocation();
        //Should make it unable to spawn in the air
        if (centerBlockWeb.getBlock().isEmpty()) {
            player.sendMessage(ERROR_COLOR + "Invalid target.");
            return false;
        }

        Material blockWeb = centerBlockWeb.getBlock().getType();
        if (blockWeb != Material.LEGACY_LONG_GRASS)
            centerBlockWeb.add(0, 1, 0);

        if (!hasWebShape(centerBlockWeb, Material.COBWEB, bottomLeft -> bottomLeft.isEmpty() || bottomLeft.getType() == Material.LEGACY_LONG_GRASS)) {
            player.sendMessage(ERROR_COLOR + "You can not cast Web Field on hostility platforms.");
            return false;
        }

        setWebShape(centerBlockWeb, Material.COBWEB, bottomLeft -> bottomLeft.isEmpty() || bottomLeft.getType() == Material.LEGACY_LONG_GRASS);

        // This runnable will remove the player from cooldown list after a given time
        new BukkitRunnable() {
            @Override
            public void run() {
                setWebShape(centerBlockWeb, Material.AIR, block -> block.getType() == Material.COBWEB);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * despawnDelayWeb);
        return true;
    }

    @Override
    public void updateCooldown(Player player)
    {
        webFieldSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    webFieldSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                webFieldSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (webFieldSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = webFieldSpellCooldownMap.get(player.getUniqueId());
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


    private boolean hasWebShape(Location centerBlock, Material material, Function<Block, Boolean> shouldReplace) {
        Location bottomLeft = centerBlock.clone().add(-2, 0, -2);
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());
                Boolean isEdge = (x == 0 && z == 0) || (x == 0 && z == 4) || (x == 4 && z == 0) || (x == 4 && z == 4);

                if (!isEdge && shouldReplaceBlock) {
                    for (HostilityMatch match : activeHostilityGames) {
                        HostilityPlatform platform = match.getPlatform();
                        if (platform.hasAdjacency(bottomLeft))
                            return false;
                    }
                }
                bottomLeft.add(0, 0, 1);

            }
            bottomLeft.add(1, 0, -5);
        }
        return true;
    }

    private void setWebShape(Location centerBlock, Material material, Function<Block, Boolean> shouldReplace) {
        Location bottomLeft = centerBlock.clone().add(-2, 0, -2);
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());
                Boolean isEdge = (x == 0 && z == 0) || (x == 0 && z == 4) || (x == 4 && z == 0) || (x == 4 && z == 4);

                if (!isEdge && shouldReplaceBlock) {
                    bottomLeft.getBlock().setType(material);
                    if (material.equals(Material.COBWEB))
                        locationSavedForNoDrop.add(bottomLeft.getBlock().getLocation());
                    else if (material.equals(Material.AIR))
                        locationSavedForNoDrop.remove(bottomLeft.getBlock().getLocation());

                }
                bottomLeft.add(0, 0, 1);

            }
            bottomLeft.add(1, 0, -5);
        }
    }


}
