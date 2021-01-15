package com.github.kotooriiii.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static com.github.kotooriiii.data.Maps.COMMAND_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class BookCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("book"))
            return false;

        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

        bookMeta.setAuthor(ChatColor.DARK_PURPLE + "Nickolov");
        bookMeta.setTitle(ChatColor.DARK_PURPLE + "LostShard");
        bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);

        TextComponent tc = new TextComponent("");
        tc.setBold(false);
        tc.setColor(ChatColor.BLACK);

        TextComponent welcomeComponent = new TextComponent("Welcome to Lostshard!\n");
        welcomeComponent.setColor(net.md_5.bungee.api.ChatColor.DARK_PURPLE);
        welcomeComponent.setBold(true);
        tc.addExtra(welcomeComponent);

        TextComponent beginning = new TextComponent("Go outside of Order and use the Plot Banner in your\n");
        beginning.setColor(ChatColor.BLACK);
        tc.addExtra(beginning);

        TextComponent beginning2 = new TextComponent("inventory to claim your base!\n");
        beginning2.setColor(ChatColor.BLACK);
        tc.addExtra(beginning2);

        TextComponent forHelp = new TextComponent("For help:\n");
        forHelp.setColor(net.md_5.bungee.api.ChatColor.DARK_PURPLE);
        forHelp.setBold(true);
        tc.addExtra(forHelp);

        TextComponent youtubeComponent = new TextComponent("Click: Getting Started Video" + "\n\n");
        youtubeComponent.setColor(ChatColor.BLACK);
        youtubeComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official LostShard 'Quick Start Guide' Video").create()));
        youtubeComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/watch?v=CEEuxxRv2Lw"));
        tc.addExtra(youtubeComponent);

        TextComponent wikiComponent = new TextComponent("Click: Wiki" + "\n");
        wikiComponent.setColor(ChatColor.BLACK);
        wikiComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official LostShard Wiki").create()));
        wikiComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WikiCommand.LINK));
        tc.addExtra(wikiComponent);

        TextComponent discordComponent = new TextComponent("Click: Discord" + "\n");
        discordComponent.setColor(ChatColor.BLACK);
        discordComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official Discord server").create()));
        discordComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DiscordCommand.LINK));
        tc.addExtra(discordComponent);

        TextComponent wikiDocComponent = new TextComponent("Click: Help Page (Doc)" + "\n");
        wikiDocComponent.setColor(ChatColor.BLACK);
        wikiDocComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official LostShard Google Doc").create()));
        wikiDocComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DocCommand.LINK));
        tc.addExtra(wikiDocComponent);

        bookMeta.spigot().addPage(new BaseComponent[]{tc});
        //bookMeta.spigot().addPage(new BaseComponent[]{tc}, new BaseComponent[]{discordComponent});
        itemStack.setItemMeta(bookMeta);

        Player player = (Player) commandSender;
        player.getInventory().addItem(itemStack);
        return true;

    }
}
