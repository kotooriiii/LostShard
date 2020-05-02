package com.github.kotooriiii.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DonateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if(!(commandSender instanceof Player))
            return false;
        if(!command.getName().equalsIgnoreCase("donate"))
            return false;

        Player player = (Player) commandSender;

        boolean isUsingBuycraftMenu = true;
        if(isUsingBuycraftMenu)
        {
            player.performCommand("buy");
            return false;
        }
        TextComponent tc = new TextComponent("-Donate-\n");
        tc.setColor(net.md_5.bungee.api.ChatColor.GOLD);

        TextComponent component = new TextComponent("Lostshard.buycraft.net");
        component.setColor(ChatColor.GOLD);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to buycraft site.").color(ChatColor.GOLD).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://lostshard.buycraft.net/"));
        tc.addExtra(component.duplicate());

        player.spigot().sendMessage(tc.duplicate());

    return false;
    }
}
