package com.github.kotooriiii.sorcery.spells.type.circle2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.util.HelperMethods;
import com.google.common.collect.Sets;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.sorcery.spells.type.circle1.TeleportSpell.*;

public class BridgeSpell extends Spell {

    private static HashMap<UUID, Double> teleportSpellCooldownMap = new HashMap<UUID, Double>();
    final private static boolean isDebug = false;

    private final static int BRIDGE_RANGE = 20, BRIDGE_DURATION = 10;


    public BridgeSpell() {
        super(SpellType.BRIDGE,
                "Creates a bridge out of leaves in the direction you casted it. The bridge shortly disappears after about " + BRIDGE_DURATION + " seconds, so cross quickly!",
                2,
                ChatColor.DARK_GREEN,
                new ItemStack[]{new ItemStack(Material.OAK_LEAVES, 1)},
                1.0f,
                10,
                true, true, false);
    }

    @Override
    public boolean executeSpell(Player player) {
        Location bridgeEndPoint = bridgeEndPoint(player, BRIDGE_RANGE);

        if (bridgeEndPoint == null) {
            player.sendMessage(ERROR_COLOR + "You need more room to cast this spell!");
            return false;
        }

        if (bridgeEndPoint.getBlockX() == player.getLocation().getBlockX() && bridgeEndPoint.getBlockY() == player.getLocation().getBlockY() && bridgeEndPoint.getBlockZ() == player.getLocation().getBlockZ() && player.getLocation().getWorld().equals(bridgeEndPoint.getWorld())) {
            if (!HelperMethods.getLookingSet(false).contains(bridgeEndPoint.getBlock())) {
                player.sendMessage(ERROR_COLOR + "Invalid target.");
                return false;
            }
        }

        if (!isAcceptableBlock(getExactBlockFace(player, BRIDGE_RANGE), bridgeEndPoint.clone().add(0, 1, 0).getBlock(), true)) {
//            player.sendMessage(ERROR_COLOR + "Invalid target.");
//            return false;
            //  teleportLocation.add(0, -1, 0);
            if (isDebug)
                Bukkit.broadcastMessage("DEBUG: Invoke 1");

        } else if (new Location(bridgeEndPoint.getWorld(), bridgeEndPoint.getX(), bridgeEndPoint.getY() - 1, bridgeEndPoint.getBlockZ()).getBlock().getType().equals(Material.AIR)) {
            bridgeEndPoint.add(0, -1, 0);
            if (isDebug)
                Bukkit.broadcastMessage("DEBUG: Invoke 2");
        }

        final Location finalBridgeLocation = new Location(bridgeEndPoint.getWorld(), bridgeEndPoint.getBlockX() + 0.5, bridgeEndPoint.getBlockY(), bridgeEndPoint.getBlockZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());

        if (isLapisNearby(finalBridgeLocation, DEFAULT_LAPIS_NEARBY)) {
            player.sendMessage(ERROR_COLOR + "You cannot seem to cast " + getName() + " here...");
            return false;
        }

        final List<Block> bridgeBlocks = getBridgeBlocks(player.getLocation(), finalBridgeLocation);

        int timer = 0;
        int offset = 1;

        for (Block block : bridgeBlocks) {
            if (!block.getType().isAir())
                continue;

//            final Plot standingOnPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(block.getLocation());
//            if (standingOnPlot != null) {
//                if (standingOnPlot.getType().isStaff())
//                    break;
//                if (standingOnPlot instanceof PlayerPlot) {
//                    if (!((PlayerPlot) standingOnPlot).isOwner(player.getUniqueId()) && !((PlayerPlot) standingOnPlot).isJointOwner(player.getUniqueId()))
//                        break;
//                }
//            }

            if (isLapisNearby(block.getLocation(), DEFAULT_LAPIS_NEARBY))
                break;

            Material leaf;
            switch ((int) Math.random() * 5) {
                default:
                case 0:
                    leaf = Material.OAK_LEAVES;
                    break;
                case 1:

                    leaf = Material.BIRCH_LEAVES;
                    break;
                case 2:

                    leaf = Material.DARK_OAK_LEAVES;
                    break;
                case 3:

                    leaf = Material.SPRUCE_LEAVES;
                    break;
                case 4:

                    leaf = Material.JUNGLE_LEAVES;
                    break;
                case 5:
                    leaf = Material.ACACIA_LEAVES;
                    break;
            }

            new BukkitRunnable() {
                @Override
                public void run() {

                    block.setType(leaf);

                    if (block.getBlockData() instanceof Leaves) {
                        Leaves leaves = (Leaves) block.getBlockData();
                        leaves.setPersistent(true);
                        block.setBlockData(leaves);
                    }

                    block.getWorld().spawnParticle(Particle.TOTEM, block.getLocation(), 5, 1, 1, 1);
                }

            }.runTaskLater(LostShardPlugin.plugin, (long) (timer++) * offset);

        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : bridgeBlocks) {
                    if (!block.getType().name().toUpperCase().endsWith("_LEAVES"))
                        continue;
                }
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * (BRIDGE_DURATION - 2) + offset * timer);


        final int[] timerLast = {0};
        int offsetLast = 1;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : bridgeBlocks) {

                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            if (!block.getType().name().toUpperCase().endsWith("_LEAVES"))
                                return;
                            block.setType(Material.AIR);
                            block.getWorld().spawnParticle(Particle.CURRENT_DOWN, block.getLocation(), 7, 2, 2, 2);

                        }

                    }.runTaskLater(LostShardPlugin.plugin, (long) (timerLast[0]++) * offsetLast);



                    //block.getWorld().spawnParticle(Particle.BUBBLE_POP, block.getLocation(), 5, 1,1,1);


                }
            }
        }.runTaskLater(LostShardPlugin.plugin, (20 * BRIDGE_DURATION) + offset * timer);

        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        teleportSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    teleportSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                teleportSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (teleportSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = teleportSpellCooldownMap.get(player.getUniqueId());
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


    public static Location bridgeEndPoint(Player player, final int range) {

        Block targetBlock = null;
        Block adjacentBlock = null;
        BlockFace face;


        List<Block> blocksToLocation = player.getLastTwoTargetBlocks(HelperMethods.getLookingSet(false), range);
        adjacentBlock = blocksToLocation.get(0);

        if (blocksToLocation.size() > 1) {
            targetBlock = blocksToLocation.get(1);
            face = targetBlock.getFace(adjacentBlock);
        } else {
            return player.getLocation();
        }


        if (isAcceptableBlock(face, player.getLocation().getWorld().getBlockAt(adjacentBlock.getX(), adjacentBlock.getY() + 1, adjacentBlock.getZ()), false) && isAcceptableBlock(getBlockFace(player, range), player.getLocation().getWorld().getBlockAt(targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ()), false)) {
            adjacentBlock = new Location(adjacentBlock.getWorld(), targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ()).getBlock();
        }

        return adjacentBlock.getLocation();
    }


    private List<Block> getBridgeBlocks(Location initialLocation, Location endingLocation) {

        int maxDistance = BRIDGE_RANGE;
        int maxLength = 0;

        if (maxDistance > 120) {
            maxDistance = 120;
        }
        ArrayList<Block> blocks = new ArrayList<Block>();


        Location clonedInitialLocation = initialLocation.clone().add(0, -1, 0);
        endingLocation.setY(endingLocation.getY() - 1);
        Vector direction = new Vector(endingLocation.getBlockX() - clonedInitialLocation.getBlockX(), endingLocation.getBlockY() - clonedInitialLocation.getBlockY(), endingLocation.getBlockZ() - clonedInitialLocation.getBlockZ());



        Iterator<Block> itr = new BlockIterator(initialLocation.getWorld(), clonedInitialLocation.toVector(), direction, 0, maxDistance);

        while (itr.hasNext()) {
            Block block = itr.next();
            Block leftBlock = block.getLocation().clone().add(getLeftHeadDirection(direction).multiply(1.0D)).getBlock();
            Block rightBlock = block.getLocation().clone().add(getRightHeadDirection(direction).multiply(1.0D)).getBlock();

            blocks.add(leftBlock);
            blocks.add(block);
            blocks.add(rightBlock);
            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }
        }
        return blocks;
    }

    public static Vector getRightHeadDirection(Vector vector) {
        Vector direction = vector.normalize();
        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
    }

    public static Vector getLeftHeadDirection(Vector vector) {
        Vector direction = vector.normalize();
        return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
    }

}
