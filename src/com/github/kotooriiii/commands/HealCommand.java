package com.github.kotooriiii.commands;

import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class HealCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("heal"))
            return false;
        final Player playerSender = (Player) commandSender;

        if (!playerSender.hasPermission(STAFF_PERMISSION)) {
            playerSender.sendMessage(ERROR_COLOR + "You must be staff to access this command.");
            return false;
        }

        playerSender.setHealth(playerSender.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

        Stat  stat =  Stat.wrap(playerSender.getUniqueId());
        stat.setStamina(stat.getMaxStamina());
        stat.setMana(stat.getMaxMana());

        return false;
    }
}
