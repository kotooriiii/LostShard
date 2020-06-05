package com.github.kotooriiii.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DocCommand implements CommandExecutor {

    public static String LINK = "https://docs.google.com/document/d/1UfFwn_xJrgPkjKC9Bs7OAFAG22IDhPg4wmk7cZtsv9c/edit?usp=sharing";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!command.getName().equalsIgnoreCase("doc"))
            return false;

        if (!(commandSender instanceof Player))
            return false;


        TextComponent tc = new TextComponent("Google Doc: ");
        tc.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        TextComponent link = new TextComponent("Click to visit the Google Doc");
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Redirects to the official LostShard Google Doc").create()));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, LINK));
        link.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        tc.addExtra(link);
        commandSender.spigot().sendMessage(tc);
        return true;
    }

}
