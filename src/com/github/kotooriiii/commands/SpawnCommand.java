package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.plots.struct.SpawnPlot;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("spawn")) {
                //No arguments regarding this command
                if (args.length == 0) {

                    spawnTimer.add(playerUUID);
                    new BukkitRunnable()
                    {
                        private int secondsLeft = 10;
                        @Override
                        public void run() {

                            if(!spawnTimer.contains(playerUUID))
                            {
                                this.cancel();
                                return;
                            }

                            if(playerSender == null || !playerSender.isOnline())
                            {
                                this.cancel();
                                spawnTimer.remove(playerUUID);
                                return;
                            }

                            if(secondsLeft == 0)
                            {

                                StatusPlayer statusPlayer = StatusPlayer.wrap(playerSender.getUniqueId());
                                Stat stat = Stat.wrap(playerSender);
                                String organization = statusPlayer.getStatus().getOrganization();
                                SpawnPlot plot = (SpawnPlot) LostShardPlugin.getPlotManager().getPlot(organization);

                           //     if(plot.getCenter().getBlock())

                                playerSender.teleport(plot.getSpawn());
                                stat.setStamina(0);
                                stat.setMana(0);
                                playerSender.sendMessage(ChatColor.GOLD + "You have teleported to spawn.");
                                spawnTimer.remove(playerUUID);
                                this.cancel();
                                return;
                            }

                            playerSender.sendMessage(ChatColor.GOLD + "Returning to spawn in " + secondsLeft + " seconds.");

                            secondsLeft--;
                        }
                    }.runTaskTimer(LostShardPlugin.plugin, 0, 20);

                } else {
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean " + ERROR_COLOR + "/spawn" + ERROR_COLOR + "?");
                }
            }
        }
        return true;
    }
}

