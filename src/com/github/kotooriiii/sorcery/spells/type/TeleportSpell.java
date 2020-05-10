package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class TeleportSpell extends Spell {

    private static HashMap<UUID, Double> teleportSpellCooldownMap = new HashMap<UUID, Double>();

    public TeleportSpell() {
        super(SpellType.TELEPORT,
                ChatColor.DARK_PURPLE,
                new ItemStack[]{new ItemStack(Material.FEATHER, 1)},
                1.0f,
                15);
    }

    @Override
    public boolean executeSpell(Player player) {
        final int rangeTeleport = 20;
        Location teleportLocation = teleportLocation(player, rangeTeleport);
        if (teleportLocation == null) {
            player.sendMessage(ERROR_COLOR + "You need more room to cast this spell!");
            return false;
        }
        if (!isAcceptableBlock(getBlockFace(player, rangeTeleport), teleportLocation.clone().add(0, 1, 0).getBlock(), true)) {
            player.sendMessage(ERROR_COLOR + "Invalid target.");
            return false;
        } else if (new Location(teleportLocation.getWorld(), teleportLocation.getX(), teleportLocation.getY() - 1, teleportLocation.getBlockZ()).getBlock().getType().equals(Material.AIR)) {
            teleportLocation.add(0, -1, 0);
        }

        player.teleport(new Location(teleportLocation.getWorld(), teleportLocation.getBlockX() + 0.5, teleportLocation.getBlockY(), teleportLocation.getBlockZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch()));
        return true;
    }

    @Override
    public void cast(Player player) {

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

    private boolean isAcceptableBlock(BlockFace facing, Block block, boolean isStrict) {

        if (block.getType().equals(Material.AIR))
            return true;

        if(block.getBoundingBox().contains(new BoundingBox(block.getX() + 0.45,block.getY() + 0.45,block.getZ() + 0.45,block.getX() + 0.55,block.getY() + 0.55,block.getZ() + 0.55)))
        {
            if (!block.getType().isSolid())
                return true;
            return false;
        }

        if (!block.getType().isSolid() && !isStrict)
            return true;


        if(block.getState().getBlockData().getMaterial().getKey().getKey().toLowerCase().endsWith("door"))
        {
            Door door = (Door) block.getState().getBlockData();
            BlockFace face = door.getFacing();
            if(door.isOpen())
            {
                switch (face)
                {
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

            if(face.equals(facing))
                return true;
        }

        if (facing.equals(BlockFace.WEST) || facing.equals(BlockFace.EAST)) {
            if (block.getBoundingBox().getWidthZ() < 0.5) {
                return true;
            }
        }

        if (facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) {
            if (block.getBoundingBox().getWidthX() < 0.5) {
                return true;
            }
        }

        return block.getBoundingBox().getHeight() < 0.5;
    }

    public Location teleportLocation(Player player, final int range) {

        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, range);

        Block targetBlock;
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        if (lastTwoTargetBlocks.size() > 1)
            targetBlock = lastTwoTargetBlocks.get(1);
        else
            return player.getLocation();


        if (isAcceptableBlock(getBlockFace(player, range), player.getLocation().getWorld().getBlockAt(adjacentBlock.getX(), adjacentBlock.getY() + 1, adjacentBlock.getZ()), false) && isAcceptableBlock(getBlockFace(player, range), player.getLocation().getWorld().getBlockAt(targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ()), false)) {
            adjacentBlock = new Location(adjacentBlock.getWorld(), targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ()).getBlock();
        }
        return adjacentBlock.getLocation();
    }

    public BlockFace getBlockFace(Player player, final int range) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, range);
        if (lastTwoTargetBlocks.size() != 2) return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }
}