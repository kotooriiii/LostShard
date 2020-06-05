package com.github.kotooriiii.commands;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;

public class WikiCommand implements CommandExecutor {

    public static String LINK = "https://lostshard.fandom.com/wiki/Lost_Shard_Wiki";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!command.getName().equalsIgnoreCase("wiki"))
            return false;

        if (!(commandSender instanceof Player))
            return false;


        TextComponent tc = new TextComponent("Wiki: ");
        tc.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        TextComponent link = new TextComponent("Click to visit the Wiki");
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Redirects to the official LostShard Wiki").create()));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, LINK));
        link.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        tc.addExtra(link);
        commandSender.spigot().sendMessage(tc);
        return true;
    }
}
