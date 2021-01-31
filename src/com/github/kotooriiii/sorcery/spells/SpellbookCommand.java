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
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

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


        Queue<Spell> spellsOrdered = new ArrayBlockingQueue<Spell>(SpellType.values().length);

        for (int i = 1; i <= 9; i++) {
            TextComponent circle = addCircle(i, spellsOrdered);
            bookMeta.spigot().addPage(new BaseComponent[]{circle});
        }

        final int INITIAL_SIZE = spellsOrdered.size();

        while (spellsOrdered.size() > 0) {

            Spell spell = spellsOrdered.poll();
            int currentSize = spellsOrdered.size();
            bookMeta.spigot().addPage(new BaseComponent[]{addSpell(spell)});
        }


        itemStack.setItemMeta(bookMeta);

        Player player = (Player) commandSender;
        player.getInventory().addItem(itemStack);
        player.sendMessage(ChatColor.GOLD + "You summon your spellbook.");
        return true;
    }

    private TextComponent addSpell(Spell spell) {

        boolean isOwned = false; //todo


        TextComponent root = new TextComponent(HelperMethods.getCenteredMessage(HelperMethods.CenteredType.BOOK, false, "  " + spell.getName()) + "\n\n");
        root.setColor(isOwned ? ChatColor.GREEN : ChatColor.DARK_GRAY);
        root.setBold(false);

        TextComponent castComponent = new TextComponent("/cast " + spell.getName().toLowerCase() + "\n\n");
        castComponent.setColor(ChatColor.BLACK);
        root.addExtra(castComponent);

        String ingredients = "";

        for (int i = 0; i < spell.getIngredients().length; i++ ) {

            ItemStack itemStack = spell.getIngredients()[i];

            String name = itemStack.getType().getKey().getKey().replace("_", " ");

            if (name.length() >= 2)
                name =  name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
           else if (name.length() == 1)
                name = name.substring(0, 1).toUpperCase();
           else
               name = "";


            ingredients += itemStack.getAmount() + " " + name + "%";
        }

        String fullIngredients = HelperMethods.stringBuilder(ingredients.split("%"), 0, " ", ", and", " and ");

        TextComponent reagentComponent = new TextComponent("Reagents: " + fullIngredients + "\n\n");
        reagentComponent.setColor(ChatColor.BLACK);
        root.addExtra(reagentComponent);

        TextComponent manaComponent = new TextComponent("Mana: " + spell.getManaCost()+ "\n\n");
        manaComponent.setColor(ChatColor.BLACK);
        root.addExtra(manaComponent);

        TextComponent infoComponent = new TextComponent("Info: \n" + spell.getDescription());
        infoComponent.setColor(ChatColor.BLACK);
        root.addExtra(infoComponent);



        return root;
    }

    /**
     * @param i circle number
     * @return
     */
    private TextComponent addCircle(int i, Queue<Spell> queue) {

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


        for (Spell spell : spells) {
            TextComponent textComponent = getComponentOfSpell(spell, queue);
            root.addExtra(textComponent);
        }


        return root;
    }

    public TextComponent getComponentOfSpell(Spell spell, Queue<Spell> queue) {
        boolean isOwned = false; //todo
        String newliner = spell.getCircle() == 9 ? "\n" : "\n\n";

        TextComponent spellComponent = new TextComponent(spell.getName() + newliner);

        if(spell.getCircle() == 9 && !isOwned)
            spellComponent = new TextComponent(spell.getName().replaceAll("[A-Za-z]", "?") + newliner);

        spellComponent.setColor(isOwned ? ChatColor.GREEN : ChatColor.DARK_GRAY);
        spellComponent.setBold(false);
        spellComponent.setUnderlined(false);
        spellComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the page containing this spell information.").create()));

        queue.add(spell);

        spellComponent.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, String.valueOf(1 + 9 + queue.size())));

        return spellComponent;
    }

    private class Counter {
        public int getSpellNum;
    }

}
