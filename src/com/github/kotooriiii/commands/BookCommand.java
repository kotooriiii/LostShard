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

        TextComponent suggestions = new TextComponent("Suggestions:\n");
        suggestions.setColor(net.md_5.bungee.api.ChatColor.RED);
        suggestions.setBold(true);
        tc.addExtra(suggestions);

        TextComponent beginning = new TextComponent("1) Escape spawn\n");
        tc.addExtra(beginning);
        tc.addExtra(new TextComponent("2) Build a base\n"));
        tc.addExtra(new TextComponent("3) Claim your base\n"));
        tc.addExtra(new TextComponent("4) Make a clan\n"));
        tc.addExtra(new TextComponent("5) Capture Hostility\n\n"));

        TextComponent helpComponent = new TextComponent("For help:\n");
        helpComponent.setColor(net.md_5.bungee.api.ChatColor.RED);
        helpComponent.setBold(true);
        tc.addExtra(helpComponent);

        TextComponent youtubeComponent = new TextComponent("Click: Youtube" + "\n\n");
        youtubeComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official LostShard Youtube Channel").create()));
        youtubeComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, YoutubeCommand.LINK));
        tc.addExtra(youtubeComponent);

        TextComponent wikiDocComponent = new TextComponent("Click: Doc" + "\n\n");
        wikiDocComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official LostShard Google Doc").create()));
        wikiDocComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DocCommand.LINK));
        tc.addExtra(wikiDocComponent);

        TextComponent wikiComponent = new TextComponent("Click: Wiki" + "\n\n");
        wikiComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official LostShard Wiki").create()));
        wikiComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WikiCommand.LINK));
        tc.addExtra(wikiComponent);

        TextComponent discordComponent = new TextComponent("Click: Discord" + "\n\n");
        discordComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official Discord server").create()));
        discordComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DiscordCommand.LINK));


        bookMeta.spigot().addPage(new BaseComponent[]{tc}, new BaseComponent[]{discordComponent});
        itemStack.setItemMeta(bookMeta);

        Player player = (Player) commandSender;
        player.getInventory().addItem(itemStack);
        return true;

    }
}
