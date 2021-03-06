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

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class SpawnCommand implements CommandExecutor {

    public static HashMap<UUID, Integer> notAllowedSpawn = new HashMap<>();

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


                    Integer seconds = notAllowedSpawn.get(playerUUID);
                    if(seconds != null && seconds > 0)
                    {

                        int minutesLeft = seconds/60;
                        int secondsLeft = seconds%60;

                        String minuteString = "minute";
                        if(minutesLeft>1)
                            minuteString = "minutes";
                        String secondString = "second";
                        if(secondsLeft>1)
                            secondString = "seconds";


                        playerSender.sendMessage(ERROR_COLOR + "Cannot go to spawn. " + minutesLeft + " " + minuteString + " and " + secondsLeft +  " " + secondString + " remaining.");
                        return false;
                    }

                    spawnTimer.add(playerUUID);
                    StatusPlayer statusPlayer = StatusPlayer.wrap(playerUUID);
                    Stat stat = Stat.wrap(playerSender);
                    String organization = statusPlayer.getStatus().getOrganization();
                    SpawnPlot plot = (SpawnPlot) LostShardPlugin.getPlotManager().getPlot(organization);
                    plot.getSpawn().getChunk().load(true);
                    new BukkitRunnable() {
                        private int secondsLeft = 10;

                        @Override
                        public void run() {

                            if (!spawnTimer.contains(playerUUID)) {
                                this.cancel();
                                return;
                            }

                            if (playerSender == null || !playerSender.isOnline()) {
                                this.cancel();
                                spawnTimer.remove(playerUUID);
                                return;
                            }

                            if (secondsLeft == 0) {

                                this.cancel();
                                playerSender.teleport(plot.getSpawn());
                                stat.setStamina(0);
                                stat.setMana(0);
                                playerSender.sendMessage(ChatColor.GRAY + "Teleporting to spawn has exhausted you.");
                                spawnTimer.remove(playerUUID);
                                notAllowedSpawn.put(playerUUID, 60*60);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        int timeLeft = notAllowedSpawn.get(playerUUID);

                                        if(timeLeft == 0)
                                        {
                                            notAllowedSpawn.remove(playerUUID);
                                            this.cancel();
                                            return;
                                        }

                                        notAllowedSpawn.put(playerUUID, --timeLeft);
                                    }
                                }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, 20); //20->second
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

