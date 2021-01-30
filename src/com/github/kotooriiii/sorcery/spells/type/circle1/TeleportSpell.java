package com.github.kotooriiii.sorcery.spells.type.circle1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class TeleportSpell extends Spell {

    private static HashMap<UUID, Double> teleportSpellCooldownMap = new HashMap<UUID, Double>();
    final private static boolean isDebug = false;
    private final static int RANGE_TP = 20;

    private TeleportSpell() {
        super(SpellType.TELEPORT,
                "Teleport allows you to instantly teleport " + RANGE_TP + " blocks in the direction\n" +
                        "you casted it. It is very useful in PvP, or just general gameplay.",
                1,
                ChatColor.DARK_PURPLE,
                new ItemStack[]{new ItemStack(Material.FEATHER, 1)},
                1.0f,
                15,
                true, true, false);
    }

    private  static TeleportSpell instance;
    public static TeleportSpell getInstance() {
        if (instance == null) {
            synchronized (TeleportSpell.class) {
                if (instance == null)
                    instance = new TeleportSpell();
            }
        }
        return instance;
    }

    @Override
    public boolean executeSpell(Player player) {
        final int rangeTeleport = RANGE_TP;
        Location teleportLocation = teleportLocation(player, rangeTeleport);
        if (teleportLocation == null) {
            player.sendMessage(ERROR_COLOR + "You need more room to cast this spell!");
            return false;
        }

        if (teleportLocation.getBlockX() == player.getLocation().getBlockX() && teleportLocation.getBlockY() == player.getLocation().getBlockY() && teleportLocation.getBlockZ() == player.getLocation().getBlockZ() && player.getLocation().getWorld().equals(teleportLocation.getWorld())) {
            if (!HelperMethods.getLookingSet(false).contains(teleportLocation.getBlock())) {
                player.sendMessage(ERROR_COLOR + "Invalid target.");
                return false;
            }
        }

        if (!isAcceptableBlock(getExactBlockFace(player, rangeTeleport), teleportLocation.clone().add(0, 1, 0).getBlock(), true)) {

            if (isDebug)
                Bukkit.broadcastMessage("DEBUG: Invoke 1");

            if(!isAcceptableBlock(getExactBlockFace(player, rangeTeleport), teleportLocation.clone().getBlock(), true)) {
                player.sendMessage(ERROR_COLOR + "Invalid target.");
                return false;
            }
            //  teleportLocation.add(0, -1, 0);


        } else if (new Location(teleportLocation.getWorld(), teleportLocation.getX(), teleportLocation.getY() - 1, teleportLocation.getBlockZ()).getBlock().getType().equals(Material.AIR)) {
            teleportLocation.add(0, -1, 0);
            if (isDebug)
                Bukkit.broadcastMessage("DEBUG: Invoke 2");
        }

        final Location finalTeleportLocation = new Location(teleportLocation.getWorld(), teleportLocation.getBlockX() + 0.5, teleportLocation.getBlockY(), teleportLocation.getBlockZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());

        if (isLapisNearby(finalTeleportLocation, DEFAULT_LAPIS_NEARBY)) {
            player.sendMessage(ERROR_COLOR + "You cannot seem to cast " + getName() + " here...");
            return false;
        }

        player.setFallDistance(0.0f);
        player.teleport(finalTeleportLocation);
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


    public static boolean isAcceptableBlock(BlockFace facing, Block block, boolean isStrict) {

        if (block.getType().equals(Material.AIR)) {
            if (isDebug)
                Bukkit.broadcastMessage("DEBUG: isAcceptableBlock -> AIR");
            return true;
        }

        float HALF = 0.5f;
        float SIZE = 0.075f;

        if (block.getBoundingBox().contains(new BoundingBox(block.getX() + HALF - SIZE, block.getY() + HALF - SIZE, block.getZ() + HALF - SIZE, block.getX() + HALF + SIZE, block.getY() + HALF + SIZE, block.getZ() + HALF + SIZE))) {
            if (!block.getType().isSolid()) {
                if (isDebug)
                    Bukkit.broadcastMessage("DEBUG: Invoke not solid AND bounding box is at least a certain size");
                return true;
            }
            if (isDebug)
                Bukkit.broadcastMessage("DEBUG: Invoke bounding box is at least a certain size");
            return false;
        }

        if (!block.getType().isSolid() && !isStrict && !block.getType().equals(Material.SNOW)) {
            if (isDebug)
                Bukkit.broadcastMessage("DEBUG: Invoke not solid and not strict and not snow");
            return true;
        }


        if (block.getState().getBlockData().getMaterial().getKey().getKey().toLowerCase().endsWith("door") && !block.getState().getBlockData().getMaterial().getKey().getKey().toLowerCase().contains("trap")) {
            Door door = (Door) block.getState().getBlockData();
            BlockFace face = door.getFacing();
            if (door.isOpen()) {
                switch (face) {
                    case EAST:
                        face = BlockFace.SOUTH;
                        break;
                    case WEST:
                        face = BlockFace.NORTH;
                        break;
                    case NORTH:
                        face = BlockFace.EAST;
                        break;
                    case SOUTH:
                        face = BlockFace.WEST;
                        break;
                }
            }

            if (isDebug)
                Bukkit.broadcastMessage("DEBUG: Invoke isDoor");

            if (face.equals(facing))
                return true;
        }

        if (facing.equals(BlockFace.WEST) || facing.equals(BlockFace.EAST)) {
            if (block.getBoundingBox().getWidthZ() < 0.5) {
                if (isDebug)
                    Bukkit.broadcastMessage("DEBUG: Invoke bounding box widthZ is less than size");
                return true;
            }
        }

        if (facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) {
            if (block.getBoundingBox().getWidthX() < 0.5) {
                if (isDebug)
                    Bukkit.broadcastMessage("DEBUG: Invoke bounding box widthX is less than size");
                return true;
            }
        }

        if (isDebug)
            Bukkit.broadcastMessage("DEBUG: Invoke height less than size");
        return block.getBoundingBox().getHeight() < 0.5;
    }

    public static Location teleportLocation(Player player, final int range) {

        Block targetBlock = null;
        Block adjacentBlock = null;
        BlockFace face;

        RayTraceResult result = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.ALWAYS, true);

        RayTraceResult collideResult = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.ALWAYS, false);


        if (result == null || result.getHitBlock() == null) {
            List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(HelperMethods.getLookingSet(false), range);
            adjacentBlock = lastTwoTargetBlocks.get(0);

            if (lastTwoTargetBlocks.size() > 1) {
                targetBlock = lastTwoTargetBlocks.get(1);
                face = targetBlock.getFace(adjacentBlock);
            }
            else {
                return player.getLocation();
            }

        } else {

            if(collideResult != null && collideResult.getHitBlock() != null)
            {

                switch (collideResult.getHitBlock().getType())
                {
                    case COBWEB:
                        targetBlock = collideResult.getHitBlock();
                        adjacentBlock = targetBlock.getRelative(collideResult.getHitBlockFace());
                        break;
                }
            }

            if(targetBlock == null || adjacentBlock == null) {
                targetBlock = result.getHitBlock();
                adjacentBlock = targetBlock.getRelative(result.getHitBlockFace());
            }

            face = targetBlock.getFace(adjacentBlock);

        }


        if (isAcceptableBlock(face, player.getLocation().getWorld().getBlockAt(adjacentBlock.getX(), adjacentBlock.getY() + 1, adjacentBlock.getZ()), false) && isAcceptableBlock(getBlockFace(player, range), player.getLocation().getWorld().getBlockAt(targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ()), false)) {
            adjacentBlock = new Location(adjacentBlock.getWorld(), targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ()).getBlock();
        }
        return adjacentBlock.getLocation();
    }

    public static BlockFace getExactBlockFace(Player player, final int range) {

        Block targetBlock;
        Block adjacentBlock;
        BlockFace face;

        RayTraceResult result = player.rayTraceBlocks(range, FluidCollisionMode.ALWAYS);

        if (result == null || result.getHitBlock() == null) {
            List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(HelperMethods.getLookingSet(false), range);
            adjacentBlock = lastTwoTargetBlocks.get(0);

            if (lastTwoTargetBlocks.size() > 1) {
                targetBlock = lastTwoTargetBlocks.get(1);
                face = targetBlock.getFace(adjacentBlock);
            }
            else {
                return getBlockFace(player, range);
            }

        } else {
            targetBlock = result.getHitBlock();
            adjacentBlock = targetBlock.getRelative(result.getHitBlockFace());
            face = targetBlock.getFace(adjacentBlock);
        }

        return face;
    }

    public static BlockFace getBlockFace(Player player, final int range) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(HelperMethods.getLookingSet(), range);
        if (lastTwoTargetBlocks.size() != 2) return BlockFace.SELF;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }
}
