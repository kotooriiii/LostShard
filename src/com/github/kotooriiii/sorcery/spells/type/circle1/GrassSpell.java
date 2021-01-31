package com.github.kotooriiii.sorcery.spells.type.circle1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import com.github.kotooriiii.sorcery.spells.type.circle6.FireWalkSpell;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.hostilityRemoverConfirmation;
import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.UP;

public class GrassSpell extends Spell {

    private static HashMap<UUID, Double> grassSpellCooldownMap = new HashMap<UUID, Double>();

    private final static int RANGE = 2;
    private final static HashMap<Material, Float> map = new HashMap();


    private GrassSpell() {
        super(SpellType.GRASS,
                "Creates a grassy area around the casting point.",
                1,
                ChatColor.DARK_GREEN,
                new ItemStack[]{new ItemStack(Material.WHEAT_SEEDS, 1)},
                1d,
                5,
                true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.CHICKEN, EntityType.COW, EntityType.SHEEP}, 0.3));

        if (map.isEmpty()) {
            map.put(Material.GRASS, 0.5f);
            map.put(Material.FERN, 0.5f);
            map.put(Material.TALL_GRASS, 0.2f);

        }

    }


    private  static GrassSpell instance;
    public static GrassSpell getInstance() {
        if (instance == null) {
            synchronized (GrassSpell.class) {
                if (instance == null)
                    instance = new GrassSpell();
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

        Block block = player.getTargetBlockExact(12, FluidCollisionMode.NEVER);

        if(block == null || block.getType() == Material.AIR)
        {
            player.sendMessage(ERROR_COLOR + "You must be near the block.");
            return false;
        }

        int castingX = block.getLocation().getBlockX();
        int castingY = block.getLocation().getBlockY();
        int castingZ = block.getLocation().getBlockZ();

        for (int x = castingX - RANGE; x <= castingX + RANGE; x++) {
            for (int z = castingZ - RANGE; z <= castingZ + RANGE; z++) {
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

                if(isLapisNearby(loc, DEFAULT_LAPIS_NEARBY))
                    continue;


                Material mat;

                if (!player.getLocation().getWorld().getBlockAt(x, y + 1, z).getType().isAir())
                    mat = Material.GRASS;
                else
                    mat = randomMaterial();


                Block placed = player.getLocation().getWorld().getBlockAt(x, y, z);


                if (mat == Material.TALL_GRASS) {

                    Bisected bisected = (Bisected) mat.createBlockData();
                    bisected.setHalf(Bisected.Half.BOTTOM);

                    Bisected bisected2 = (Bisected) mat.createBlockData();
                    bisected2.setHalf(Bisected.Half.TOP);

                    placed.setBlockData(bisected,false);
                    placed.getRelative(UP).setBlockData(bisected2, false);
                } else {
                    placed.setType(mat);
                }

                Block below = placed.getRelative(DOWN);
                if(below.getType() == Material.DIRT)
                {
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
}
