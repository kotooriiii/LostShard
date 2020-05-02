package com.github.kotooriiii.skills.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.skills.listeners.SurvivalismListener;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class CampCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            if (cmd.getName().equalsIgnoreCase("camp")) {

                int level = (int) SkillPlayer.wrap(playerUUID).getSurvivalism().getLevel();
                if (level < SurvivalismListener.Campfire.LEVEL) {
                    playerSender.sendMessage(ERROR_COLOR + "You must be at least level 25 to place a camp.");
                    return false;
                }

                Stat stat = Stat.wrap(playerUUID);

                if (stat.getStamina() < SurvivalismListener.Campfire.STAMINA_COST) {
                    playerSender.sendMessage(ERROR_COLOR + "You must at least have " + SurvivalismListener.Campfire.STAMINA_COST + " stamina.");
                    return false;
                }

                if (SurvivalismListener.Campfire.hasCampfire(playerUUID)) {
                    playerSender.sendMessage(ERROR_COLOR + "You already have a campfire somewhere.");
                    return false;
                }


                //check plots

                Location location = getLocation(playerSender, SurvivalismListener.Campfire.RANGE + 1);
                if (location == null) {
                    playerSender.sendMessage(ERROR_COLOR + "Not a valid location.");
                    return false;
                }

                if (LostShardPlugin.getPlotManager().isStandingOnPlot(location)) {
                    Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(location);
                    boolean exists = false;

                    if (!plot.getType().isStaff()) {
                        PlayerPlot playerPlot = (PlayerPlot) plot;
                        if (playerPlot.isOwner(playerUUID) || playerPlot.isJointOwner(playerUUID))
                            exists = true;
                    }


                    if (!exists) {
                        playerSender.sendMessage(ERROR_COLOR + "You cannot place a campfire in this location.");
                        return false;
                    }


                }

                SurvivalismListener.Campfire campfire = new SurvivalismListener.Campfire(playerUUID, location);
                if (!campfire.isSpawnable()) {
                    // playerSender.sendMessage(ERROR_COLOR + "There was an error placing a campfire in this location. Is there a block occupying that space?");
                    playerSender.sendMessage(ERROR_COLOR + "Not a valid location.");
                    return false;
                }

                stat.setStamina(stat.getStamina() - SurvivalismListener.Campfire.STAMINA_COST);
                campfire.spawn();
                playerSender.sendMessage(ChatColor.GOLD + "You set up a temporary camp.");
            }
        }
        return true;

    }

    private Location getLocation(Player player, int range) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, range);
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);

        if (targetBlock.getType() == Material.AIR) {
            //         Bukkit.broadcastMessage("The campfire spot is in air. Cannot be placed here.");
            return null;
        }


        if (adjacentBlock.getType() != Material.AIR) {
            //Bukkit.broadcastMessage("The campfire spot block is already taken by something not air. Cannot be placed here.");
            return null;
        }

        if (adjacentBlock.getY() < targetBlock.getY()) {
            // Bukkit.broadcastMessage("The campfire spot is less than what you are looking at. Cannot be placed here.");
            return null;
        }

        if (new Location(adjacentBlock.getWorld(), adjacentBlock.getX(), adjacentBlock.getY() + 1, adjacentBlock.getZ()).getBlock().getType() == Material.AIR) {
            adjacentBlock = new Location(adjacentBlock.getWorld(), targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ()).getBlock();
        }
        return adjacentBlock.getLocation();
    }
}
