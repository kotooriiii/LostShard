package com.github.kotooriiii.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.*;

public class RemoveOldEntitiesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("removeoldentities"))
            return false;

        if(!commandSender.hasPermission(STAFF_PERMISSION))
            return false;

        final Player playerSender = (Player) commandSender;
        for (Entity entity : playerSender.getWorld().getNearbyEntities(playerSender.getLocation(), 3, 3, 3)) {
            if (!(entity instanceof ArmorStand))
                continue;
            entity.remove();
        }


        return true;
    }
}
