package com.github.kotooriiii.sorcery.spells.type.circle1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.hostilityRemoverConfirmation;
import static org.bukkit.block.BlockFace.UP;

public class LightSpell extends Spell {

    private static HashMap<UUID, Double> grassSpellCooldownMap = new HashMap<UUID, Double>();

    private final static HashMap<UUID, Location> map = new HashMap();

    private final static int LIGHT_DURATION = 10;
    private final static int LIGHT_LEVEL = 5;


    private LightSpell() {
        super(SpellType.LIGHT,
                "Creates an aura of light around you for 10 seconds. Great for mining!",
                1,
                ChatColor.WHITE,
                new ItemStack[]{new ItemStack(Material.COAL, 1)},
                LIGHT_DURATION,
                5,
                true, true, false);


    }

    private static LightSpell instance;

    public static LightSpell getInstance() {
        if (instance == null) {
            synchronized (LightSpell.class) {
                if (instance == null)
                    instance = new LightSpell();
            }
        }
        return instance;
    }

    public static int getLightLevel() {
        return LIGHT_LEVEL;
    }

    public static void createLight(Location loc) {
        LightAPI.createLight(loc, LightType.BLOCK, getLightLevel(), true);
        for (ChunkInfo info : LightAPI.collectChunks(loc, LightType.BLOCK, getLightLevel()))
            LightAPI.updateChunk(info, LightType.BLOCK);
    }

    public static void deleteLight(Location loc) {
        LightAPI.deleteLight(loc, LightType.BLOCK, true);

        for (ChunkInfo info : LightAPI.collectChunks(loc, LightType.BLOCK, getLightLevel()))
            LightAPI.updateChunk(info, LightType.BLOCK);
    }

    @Override
    public boolean executeSpell(Player player) {

        Location loc = player.getLocation().clone().add(0, 1, 0);

        Location oldLocation = map.get(player.getUniqueId());
        if (oldLocation != null) {
            deleteLight(oldLocation);
        }


        map.put(player.getUniqueId(), loc);

        createLight(loc);

        player.getWorld().playSound(loc, Sound.BLOCK_FURNACE_FIRE_CRACKLE, 5.0f, 0.0f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 5.0f, 0.0f);

                }
                final Location loc = map.get(player.getUniqueId());
                if (loc != null) {
                    deleteLight(loc);
                    map.remove(player.getUniqueId());
                }
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * LIGHT_DURATION);

        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        grassSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    grassSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                grassSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (grassSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = grassSpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(1, RoundingMode.HALF_UP);
            float value = bd.floatValue();
            if (value == 0)
                value = 0.1f;

            String time = "seconds";
            if (value <= 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }

    public static HashMap<UUID, Location> getMap() {
        return map;
    }
}
