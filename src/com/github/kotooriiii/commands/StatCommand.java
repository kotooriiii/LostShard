package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class StatCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("stat")) {
                Stat stat = Stat.getStatMap().get(playerUUID);
                //No arguments regarding this command
                if (args.length == 0) {
                    Bank bank = Bank.getBanks().get(playerUUID);
                    StatusPlayer statusPlayer = StatusPlayer.wrap(playerUUID);
                    playerSender.sendMessage(
                            ChatColor.GOLD + "-" + playerSender.getName() + "-" + "\n" +
                                    ChatColor.YELLOW + "Mana: " + ChatColor.WHITE + stat.getManaString() + "\n" +
                                    ChatColor.YELLOW + "Stamina: " + ChatColor.WHITE + stat.getStaminaString() + "\n" +
                                    ChatColor.YELLOW + "Gold:  " + ChatColor.WHITE + bank.getCurrency() + "\n" +
                                    ChatColor.YELLOW + "Murder Count: " + ChatColor.WHITE + statusPlayer.getKills()
                    );

                } else if (args.length >= 1) {
                    switch (args[0].toLowerCase()) {
                        case "refill":
                            if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                                playerSender.sendMessage(ERROR_COLOR + "You don't have permission to use this refilling command.");
                                return false;
                            }

                            if (args.length == 1) {
                                playerSender.sendMessage(STANDARD_COLOR + "Refilled mana and stamina.");
                                stat.setStamina(stat.getMaxStamina());
                                stat.setMana(stat.getMaxMana());
                            } else if (args.length == 2) {
                                String otherName = args[1];
                                Player otherPlayer = Bukkit.getPlayer(otherName);
                                if (otherPlayer == null) {
                                    playerSender.sendMessage(ERROR_COLOR + "We were not able to find the player you were searching for.");
                                    return true;
                                }
                                Stat otherPlayerStat = Stat.getStatMap().get(otherPlayer.getUniqueId());
                                otherPlayerStat.setStamina(otherPlayerStat.getMaxStamina());
                                otherPlayerStat.setMana(otherPlayerStat.getMaxMana());
                                playerSender.sendMessage(STANDARD_COLOR + "You have refilled " + PLAYER_COLOR + otherPlayer.getName() + STANDARD_COLOR + "'s mana and stamina.");
                                otherPlayer.sendMessage(STANDARD_COLOR + "Your mana and stamina have been refilled.");

                            } else {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean " + COMMAND_COLOR + "/stat refill (player)" + ERROR_COLOR + "?");

                            }
                            break;
                        default:
                            playerSender.sendMessage(ERROR_COLOR + "Did you mean " + COMMAND_COLOR + "/stat" + ERROR_COLOR + "?");
                            break;

                    }
                }
            }
        }


        return true;
    }
}
