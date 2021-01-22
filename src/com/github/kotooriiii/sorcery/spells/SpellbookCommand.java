package com.github.kotooriiii.sorcery.spells;

import com.github.kotooriiii.commands.DiscordCommand;
import com.github.kotooriiii.commands.DocCommand;
import com.github.kotooriiii.commands.WikiCommand;
import com.github.kotooriiii.util.HelperMethods;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.ChatPaginator;
import ru.beykerykt.lightapi.LightAPI;

import java.util.ArrayList;

public class SpellbookCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("spellbook"))
            return false;

        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

        bookMeta.setAuthor(ChatColor.DARK_PURPLE + "" + ChatColor.MAGIC + "Nickolov");
        bookMeta.setTitle(ChatColor.DARK_PURPLE + "Spellbook");
        bookMeta.setGeneration(BookMeta.Generation.TATTERED);

        TextComponent tc = new TextComponent("");
        tc.setBold(false);
        tc.setColor(ChatColor.BLACK);

        TextComponent welcomeComponent = new TextComponent("\n\n\n\n\n\n" + HelperMethods.getCenteredMessage(HelperMethods.CenteredType.BOOK, false, "Spellbook") + "\n");
        welcomeComponent.setColor(net.md_5.bungee.api.ChatColor.DARK_PURPLE);
        welcomeComponent.setBold(true);
        tc.addExtra(welcomeComponent);
        bookMeta.spigot().addPage(new BaseComponent[]{tc});


        for (int i = 1; i <= 9; i++) {
            TextComponent circle = addCircle(i);
            bookMeta.spigot().addPage(new BaseComponent[]{circle});

        }



        itemStack.setItemMeta(bookMeta);

        Player player = (Player) commandSender;
        player.getInventory().addItem(itemStack);
        player.sendMessage(ChatColor.GOLD + "You summon your spellbook.");
        return true;
    }

    private TextComponent addCircle(int i) {

        String suffix = "";
        if (i == 1)
            suffix = "st";
        else if (i == 2)
            suffix = "nd";
        else
            suffix = "th";


        TextComponent root = new TextComponent(HelperMethods.getCenteredMessage(HelperMethods.CenteredType.BOOK, true, i + suffix + " Circle") + "\n\n");
        root.setColor(ChatColor.DARK_PURPLE);
        root.setBold(true);

        Spell[] spells = Spell.getCircleSpells(i);


        for(Spell spell : spells) {
            TextComponent textComponent = getComponentOfSpell(spell);
            root.addExtra(textComponent);
        }



        return root;
    }

    public TextComponent getComponentOfSpell(Spell spell)
    {
        boolean isOwned = false; //todo

        TextComponent spellComponent = new TextComponent(spell.getName() + "\n\n");
        spellComponent.setColor(isOwned ? ChatColor.GREEN : ChatColor.DARK_GRAY);
        spellComponent.setBold(false);
        spellComponent.setUnderlined(false);
        spellComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the page containing this spell information.").create()));
        spellComponent.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "1"));

       return spellComponent;
    }


}
