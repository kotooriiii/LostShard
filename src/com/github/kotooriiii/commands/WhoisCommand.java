package com.github.kotooriiii.commands;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.match.banmatch.Banmatch;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.util.HelperMethods;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class WhoisCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player))
            return false;
        if(!command.getName().equalsIgnoreCase("whois"))
            return false;

        Player playerSender = (Player)commandSender;

        if(args.length == 0)
        {
            playerSender.performCommand("whois " + playerSender.getName());
        } else if (args.length == 1) {
            String name = args[0];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if(!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline())
            {
                playerSender.sendMessage(ERROR_COLOR + "The player does not exist.");
                return false;
            }

            Clan clan = Clan.getClan(offlinePlayer.getUniqueId());
            String clanName = "None";
            StatusPlayer statusPlayer = StatusPlayer.wrap(offlinePlayer.getUniqueId());
            if(clan != null)
                clanName = clan.getName();;

            TextComponent tc = new TextComponent(TextComponent.fromLegacyText(ChatColor.GOLD + "-" + offlinePlayer.getName() + "-"));
            TextComponent component = new TextComponent("\n" + ChatColor.GOLD + "Clan: " + ChatColor.GREEN + clanName);
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "Click here to show information about this player's clan.").create()));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan who " + offlinePlayer.getName()));
            TextComponent component2 = new TextComponent("\n" + ChatColor.GOLD + "Status: " + statusPlayer.getStatus().getChatColor() + statusPlayer.getStatus().getName());
            TextComponent component3 = new TextComponent("\n" + ChatColor.GOLD + "Murdercount: " + ChatColor.DARK_RED + statusPlayer.getKills());
            tc.addExtra(component);
            tc.addExtra(component2);
            tc.addExtra(component3);
            playerSender.spigot().sendMessage(tc);
        } else {
            playerSender.sendMessage(ERROR_COLOR + "Did you mean to type: " + COMMAND_COLOR + "/whois (username)"+ ERROR_COLOR + ".");
        }

        return true;
    }
}
