package com.github.kotooriiii.commands;

import com.github.kotooriiii.status.Staff;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordCommand implements CommandExecutor {

    public static String LINK = "https://discord.gg/hUAfPmS";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!command.getName().equalsIgnoreCase("discord"))
            return false;

        /*
        if (!(commandSender instanceof Player))
            return false;
*/

        TextComponent tc = new TextComponent("Discord: ");
        tc.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        TextComponent link = new TextComponent("Click to visit the Discord");
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Redirects to the official Discord server").create()));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, LINK));
        link.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        tc.addExtra(link);
        commandSender.spigot().sendMessage(tc);

        if(commandSender instanceof Player && Staff.isStaff(((Player) commandSender).getUniqueId()))
        {

            commandSender.sendMessage(ChatColor.RED + "Staff Message: ");
            TextComponent staffTC = new TextComponent("Discord ID: ");
            staffTC.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            TextComponent staffID = new TextComponent("Click to copy Discord ID for tellraw messages.");
            staffID.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Copies to chat message for you to copy.").create()));
            staffID.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "tellraw @p {\"text\":\"Discord: \", \"color\":\"gold\", \"extra\":[{\"text\":\"Click to visit the Discord\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + LINK + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Redirects to the official Discord server\", \"color\":\"yellow\"}}}]}\n"));
            staffID.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
            staffTC.addExtra(staffID);
            commandSender.spigot().sendMessage(staffTC);
        }

        return true;
    }
}
