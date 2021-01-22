package com.github.kotooriiii.sorcery.spells.type.circle5;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.type.circle4.ScreechSpell;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.UP;

public class FireFieldSpell extends Spell {

    private static HashMap<UUID, Double> fireFieldCooldownMap = new HashMap<UUID, Double>();

    private final static int RANGE = 3, DISTANCE = 32, DURATION = 6;


    private FireFieldSpell() {
        super(SpellType.FIRE_FIELD,
                "Creates a field of fire in the direction you are facing.",
                5,
                ChatColor.RED,
                new ItemStack[]{new ItemStack(Material.WHEAT_SEEDS, 1)},
                1d,
                15,
                true, true, false);
    }

    private  static FireFieldSpell instance;
    public static FireFieldSpell getInstance() {
        if (instance == null) {
            synchronized (FireFieldSpell.class) {
                if (instance == null)
                    instance = new FireFieldSpell();
            }
        }
        return instance;
    }


    public int getY(Location location) {

        Location cloneLoc = location.clone();

        //exclusive. i.e 1 block for feet.
        final int Y_SPACE = 3;

        if (cloneLoc.getBlock().getType().isAir()) {
            for (int y = cloneLoc.getBlockY(); y >= location.getBlockY() - Y_SPACE && y != 0 && !(cloneLoc.getBlock().getType().isAir() && cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()); y--) {
                cloneLoc.setY(y);
            }
        } else {
            for (int y = cloneLoc.getBlockY(); y <= location.getBlockY() + Y_SPACE && y != cloneLoc.getWorld().getMaxHeight() && !(cloneLoc.getBlock().getType().isAir() && cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()); y++) {
                cloneLoc.setY(y);
            }
        }

        if (cloneLoc.getBlockY() == cloneLoc.getWorld().getMaxHeight() || cloneLoc.getBlockY() == 0)
            return -1;
        if (cloneLoc.getBlockY() - 1 < location.getBlockY() - Y_SPACE || cloneLoc.getBlockY() + 1 > location.getBlockY() + Y_SPACE) {
            return -1;
        }


        return cloneLoc.getBlockY();
    }

    @Override
    public boolean executeSpell(Player player) {

        Block block = player.getTargetBlockExact(DISTANCE, FluidCollisionMode.NEVER);

        if (block == null) {
            player.sendMessage(ERROR_COLOR + "You must be looking at a block.");
            return false;
        }



        int castingX = block.getX();
        int castingY = block.getY();
        int castingZ = block.getZ();

        ArrayList<Block> blocks = new ArrayList<>();

        for (int x = castingX - RANGE; x <= castingX + RANGE; x++) {
            for (int z = castingZ - RANGE; z <= castingZ + RANGE; z++) {

                if (x == castingX - RANGE && z == castingZ - RANGE)
                    continue;
                if (x == castingX - RANGE && z == castingZ + RANGE)
                    continue;
                if (x == castingX + RANGE && z == castingZ - RANGE)
                    continue;
                if (x == castingX + RANGE && z == castingZ + RANGE)
                    continue;



                int y = getY(new Location(player.getWorld(), x, castingY, z));

                if (y == -1)
                    continue;


                final Location loc = new Location(player.getWorld(), x, y, z);

                final Plot standingOnPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(loc);
                if (standingOnPlot != null) {
                    if (standingOnPlot.getType().isStaff())
                        continue;
                }

                if (isLapisNearby(loc, DEFAULT_LAPIS_NEARBY))
                    continue;

                Block placed = player.getLocation().getWorld().getBlockAt(x, y, z);

                placed.setType(Material.FIRE);
                blocks.add(placed);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Block iblock : blocks)
                {
                    if(iblock.getType() == Material.FIRE)
                    {
                        iblock.setType(Material.AIR);
                    }

                }
            }
        }.runTaskLater(LostShardPlugin.plugin, 20*DURATION);

        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        fireFieldCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    fireFieldCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                fireFieldCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (fireFieldCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = fireFieldCooldownMap.get(player.getUniqueId());
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
}
