package com.github.kotooriiii.sorcery.spells.type.circle2;

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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.UP;

public class FlowerSpell extends Spell {

    private static HashMap<UUID, Double> flowerSpellCooldownMap = new HashMap<UUID, Double>();

    private final static int RANGE = 3;
    private final static HashMap<Material, Float> map = new HashMap();


    private FlowerSpell() {
        super(SpellType.FLOWER,
                "Spawns in flowers where you were looking when you casted it. Must be looking at grass to work.",
                2,
                ChatColor.YELLOW,
                new ItemStack[]{new ItemStack(Material.WHEAT_SEEDS, 1)},
                1d,
                10,
                true, true, false);

        if (map.isEmpty()) {
            map.put(Material.DANDELION, 13 / 100f);
            map.put(Material.POPPY, 13 / 100f);
            map.put(Material.BLUE_ORCHID, 13 / 100f);
            map.put(Material.ALLIUM, 13 / 100f);
            map.put(Material.AZURE_BLUET, 13 / 100f);
            map.put(Material.RED_TULIP, 13 / 100f);
            map.put(Material.ORANGE_TULIP, 13 / 100f);
            map.put(Material.WHITE_TULIP, 13 / 100f);
            map.put(Material.PINK_TULIP, 13 / 100f);
            map.put(Material.OXEYE_DAISY, 13 / 100f);
            map.put(Material.CORNFLOWER, 13 / 100f);
            map.put(Material.LILY_OF_THE_VALLEY, 13 / 100f);

            map.put(Material.SUNFLOWER, 13 / 100f);
            map.put(Material.LILAC, 13 / 100f);
            map.put(Material.ROSE_BUSH, 13 / 100f);
            map.put(Material.PEONY, 13 / 100f);
        }

    }

    private  static FlowerSpell instance;
    public static FlowerSpell getInstance() {
        if (instance == null) {
            synchronized (FlowerSpell.class) {
                if (instance == null)
                    instance = new FlowerSpell();
            }
        }
        return instance;
    }


    public int getFoliageY(Location location) {

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

    public Material randomMaterial() {

        Material chosenMaterial = Material.AIR;
        float chosenValue = 0.0f;

        float heaviest = 0.0f;
        Material heaviestMat = Material.AIR;

        for (Map.Entry<Material, Float> entry : map.entrySet()) {
            Material mat = entry.getKey();
            float chance = entry.getValue();

            if (heaviest < chance) {
                heaviestMat = mat;
            }

            if (Math.random() <= chance) {
                if (chosenValue <= chance) {
                    chosenMaterial = mat;
                    chosenValue = chance;
                }
            }
        }

        if (chosenMaterial == Material.AIR)
            return heaviestMat;
        return chosenMaterial;
    }

    @Override
    public boolean executeSpell(Player player) {

        Block block = player.getTargetBlockExact(5, FluidCollisionMode.NEVER);

        if (block == null) {
            player.sendMessage(ERROR_COLOR + "You must be near the block.");
            return false;
        }

        Block upBlock = block.getRelative(UP);
        if (block.getType() != Material.GRASS_BLOCK) {
            player.sendMessage(ERROR_COLOR + "You must be looking at a grass block.");
            return false;
        }


        int castingX = upBlock.getX();
        int castingY = upBlock.getY();
        int castingZ = upBlock.getZ();

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



                int y = getFoliageY(new Location(player.getWorld(), x, castingY, z));

                if (y == -1)
                    continue;


                final Location loc = new Location(player.getWorld(), x, y, z);

                final Plot standingOnPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(loc);
                if (standingOnPlot != null) {
                    if (standingOnPlot.getType().isStaff())
                        continue;
                    if (standingOnPlot instanceof PlayerPlot) {
                        if (!((PlayerPlot) standingOnPlot).isOwner(player.getUniqueId()) && !((PlayerPlot) standingOnPlot).isJointOwner(player.getUniqueId()))
                            continue;
                    }
                }

                if (isLapisNearby(loc, DEFAULT_LAPIS_NEARBY))
                    continue;


                Material mat;

                if (!player.getLocation().getWorld().getBlockAt(x, y + 1, z).getType().isAir())
                    mat = Material.GRASS;
                else
                    mat = randomMaterial();


                Block placed = player.getLocation().getWorld().getBlockAt(x, y, z);


                if (mat == Material.SUNFLOWER || mat == Material.LILAC || mat == Material.ROSE_BUSH || mat == Material.PEONY) {

                    Bisected bisected = (Bisected) mat.createBlockData();
                    bisected.setHalf(Bisected.Half.BOTTOM);

                    Bisected bisected2 = (Bisected) mat.createBlockData();
                    bisected2.setHalf(Bisected.Half.TOP);

                    placed.setBlockData(bisected, false);
                    placed.getRelative(UP).setBlockData(bisected2, false);
                } else {
                    placed.setType(mat);
                }

                Block below = placed.getRelative(DOWN);
                if (below.getType() == Material.DIRT) {
                    below.setType(Material.GRASS_BLOCK);
                }


                if (Math.random() < 0.355d)
                    player.getWorld().spawnParticle(Particle.COMPOSTER, new Location(player.getWorld(), x, y + 1.5f, z), 1);

            }
        }

        player.getWorld().spawnParticle(Particle.COMPOSTER, new Location(player.getWorld(), (double) castingX + RANGE / 2, castingY + 1.5f, (double) castingZ + RANGE / 2), 1);
        player.getWorld().spawnParticle(Particle.COMPOSTER, new Location(player.getWorld(), (double) castingX - RANGE / 2, castingY + 1.5f, (double) castingZ - RANGE / 2), 1);


        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 7.0f, 7.0f);
        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        flowerSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    flowerSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                flowerSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (flowerSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = flowerSpellCooldownMap.get(player.getUniqueId());
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
