package com.github.kotooriiii.commands;

import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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

        if (args.length == 0) {
            heal(playerSender, true);
        } else if (args.length == 1) {

            String playerName = args[0];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.isOnline()) {
                playerSender.sendMessage(ERROR_COLOR + "The player is not online");
                return false;
            }

            Player healedPlayer = offlinePlayer.getPlayer();

            heal(healedPlayer, true);
            playerSender.sendMessage(ChatColor.GOLD + "Replenished " + PLAYER_COLOR + healedPlayer.getName() + ChatColor.GOLD + "'s health, mana, and stamina.");

        }

        return false;
    }

    public static void heal(Player healedPlayer, boolean isMessaging) {
        healedPlayer.setHealth(healedPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        healedPlayer.setFoodLevel(20);
        healedPlayer.setFireTicks(0);

        Stat stat = Stat.wrap(healedPlayer.getUniqueId());
        if(stat==null)
            return;
        stat.setStamina(stat.getMaxStamina());
        stat.setMana(stat.getMaxMana());
        if (isMessaging)
            healedPlayer.sendMessage(ChatColor.GOLD + "Replenished health, mana, and stamina.");
    }
}
