package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.guards.ShardBanker;
import com.github.kotooriiii.guards.ShardGuard;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.stringBuilder;

public class ChestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("chest")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    final Location playerLocation = playerSender.getLocation();
                    ShardBanker banker = ShardBanker.getNearestBanker(playerLocation);
                    if (banker == null || !banker.isSocialDistance(playerLocation)) {
                        playerSender.sendMessage(ERROR_COLOR + "No banker nearby!!!");
                        return true;
                    }

                    //get rank player
                    int rankSize = 27;
                    //todo make a way to get rank
                    Inventory inventory = null;
                    if (!Bank.getBanks().containsKey(playerUUID)) {
                        inventory = Bukkit.createInventory(playerSender, rankSize, Bank.NAME);
                    } else {
                        inventory = Bank.getBanks().get(playerUUID);
                    }
                    ItemStack[] itemStacks = inventory.getContents();
                    inventory = Bukkit.createInventory(playerSender, rankSize, Bank.NAME); //hashmap get
                    inventory.setContents(itemStacks);
                    playerSender.openInventory(inventory);

                }
            }
        }
        return true;
    }
}
