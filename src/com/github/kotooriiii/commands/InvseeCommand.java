package com.github.kotooriiii.commands;

import com.github.kotooriiii.files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static com.github.kotooriiii.data.Maps.*;

public class InvseeCommand implements CommandExecutor {

    public static final String IDENTIFIER = "Full Inventory";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("invsee"))
            return false;

        final Player playerSender = (Player) commandSender;

        if (!playerSender.hasPermission(STAFF_PERMISSION)) {
            playerSender.sendMessage(ERROR_COLOR + "You must be staff to access this command.");
            return false;
        }

        if (args.length != 1) {
            playerSender.sendMessage(ERROR_COLOR + "The proper usage of the command is: " + COMMAND_COLOR + "/invsee (username)" + ERROR_COLOR + ".");
            return false;
        }

        String name = args[0];

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (!offlinePlayer.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "The player you are looking for is not online.");
            return false;
        }


        Player victimPlayer = offlinePlayer.getPlayer();
        PlayerInventory inventory = victimPlayer.getInventory();

        Inventory customInventory = Bukkit.createInventory(victimPlayer.getPlayer(), (27+9)+9, victimPlayer.getName() + "'s " + IDENTIFIER);

        for(int i = 0; i < inventory.getSize(); i++)
        {
            ItemStack item = inventory.getItem(i);
            if(item == null || item.getType() == null || item.getType().equals(Material.AIR))
            {
                customInventory.setItem(i, new ItemStack(Material.AIR, 1));
                continue;
            }

            customInventory.setItem(i, item.clone());
        }

        ItemStack[] armorContents = victimPlayer.getInventory().getArmorContents();

        for(int i = 0; i < 9; i++)
        {

            if(i > 3)
            {
                customInventory.setItem(i+(27+9), new ItemStack(Material.BARRIER, 1));
                continue;
            }

            if(armorContents[i] == null || armorContents[i].getType() == null || armorContents[i].getType().equals(Material.AIR))
            {
                customInventory.setItem(i+(27+9), new ItemStack(Material.AIR, 1));
                continue;
            }

            customInventory.setItem(i+(27+9), armorContents[i].clone());
        }

        playerSender.openInventory(customInventory);

        playerSender.sendMessage(ChatColor.RED + "You have opened " + PLAYER_COLOR + offlinePlayer.getName() + ChatColor.RED + "'s inventory.");

        return true;

    }
}
