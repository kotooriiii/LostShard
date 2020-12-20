package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

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

    public TeleportSpell() {
        super(SpellType.TELEPORT,
                ChatColor.DARK_PURPLE,
                new ItemStack[]{new ItemStack(Material.FEATHER, 1)},
                1.0f,
                15,
                true, true, false);
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
//            player.sendMessage(ERROR_COLOR + "Invalid target.");
//            return false;
          //  teleportLocation.add(0, -1, 0);
        } else if (new Location(teleportLocation.getWorld(), teleportLocation.getX(), teleportLocation.getY() - 1, teleportLocation.getBlockZ()).getBlock().getType().equals(Material.AIR)) {
            teleportLocation.add(0, -1, 0);
        }

        final Location finalTeleportLocation = new Location(teleportLocation.getWorld(), teleportLocation.getBlockX() + 0.5, teleportLocation.getBlockY(), teleportLocation.getBlockZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());

        if(isLapisNearby(finalTeleportLocation, DEFAULT_LAPIS_NEARBY))
        {
            player.sendMessage(ERROR_COLOR + "You cannot seem to cast " + getName() + " here...");
            return false;
        }

        player.teleport(finalTeleportLocation);
        return true;
    }

    @Override
    public void updateCooldown(Player player)
    {
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

    private boolean isAcceptableBlock(BlockFace facing, Block block, boolean isStrict) {

        if (block.getType().equals(Material.AIR))
            return true;

        if(block.getBoundingBox().contains(new BoundingBox(block.getX() + 0.45,block.getY() + 0.45,block.getZ() + 0.45,block.getX() + 0.55,block.getY() + 0.55,block.getZ() + 0.55)))
        {
            if (!block.getType().isSolid())
                return true;
            return false;
        }

        if (!block.getType().isSolid() && !isStrict && !block.getType().equals(Material.SNOW))
            return true;


        if(block.getState().getBlockData().getMaterial().getKey().getKey().toLowerCase().endsWith("door") && !block.getState().getBlockData().getMaterial().getKey().getKey().toLowerCase().contains("trap"))
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

        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(HelperMethods.getLookingSet(), range);

//        for(int i = 0; i < lastTwoTargetBlocks.size(); i++)
//        Bukkit.broadcastMessage("i: " + i + " | " + "material: " + lastTwoTargetBlocks.get(i).getType().getKey().getKey());

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
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(HelperMethods.getLookingSet(), range);
        if (lastTwoTargetBlocks.size() != 2) return BlockFace.SELF;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }
}
