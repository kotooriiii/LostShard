package com.github.kotooriiii.commands;

import com.github.kotooriiii.bank.Bank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class BalanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("balance")) {
                //No arguments regarding this command
                if (args.length == 0) {
//                    final Location playerLocation = playerSender.getLocation();
//                    ShardBanker banker = ShardBanker.getNearestBanker(playerLocation);
//                    if (banker == null || !banker.isSocialDistance(playerLocation)) {
//                        playerSender.sendMessage(ERROR_COLOR + "No banker nearby!!!");
//                        return true;
//                    }

                    DecimalFormat df = new DecimalFormat("#.##");
                    Bank bank = Bank.getBanks().get(playerUUID);
                    playerSender.sendMessage(ChatColor.GRAY + "You have " + df.format(bank.getCurrency()) + ChatColor.GRAY + " in your bank account.");

                } else {
                    playerSender.sendMessage(ERROR_COLOR + "Did you mean " + COMMAND_COLOR + "/balance" + ERROR_COLOR + "?");
                }
            }
        }
        return true;
    }
}