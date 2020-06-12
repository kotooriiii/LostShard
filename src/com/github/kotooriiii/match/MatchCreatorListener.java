package com.github.kotooriiii.match;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.match.banmatch.Banmatch;
import com.github.kotooriiii.match.moneymatch.Moneymatch;
import com.github.kotooriiii.util.HelperMethods;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.ZonedDateTime;

import static com.github.kotooriiii.commands.BanCommand.matchesRegex;
import static com.github.kotooriiii.commands.BanCommand.toProperties;
import static com.github.kotooriiii.data.Maps.*;

public class MatchCreatorListener implements Listener {
    @EventHandler
    public void onChat(ShardChatEvent event) {
        Player player = event.getPlayer();

        if (!Match.isCreatingMatch(player.getUniqueId()))
            return;

        event.setCancelled(true);

        String message = event.getMessage();
        Match match = Match.getMatchCreatorMap().get(player.getUniqueId());

        if (match.getArmorType() == null) {

            switch (message.toLowerCase()) {
                case "diamond":
                    match.setArmorType(Material.DIAMOND_BOOTS);
                    break;
                case "gold":
                    match.setArmorType(Material.GOLDEN_BOOTS);
                    break;
                case "iron":
                    match.setArmorType(Material.IRON_BOOTS);
                    break;
                case "chainmail":
                    match.setArmorType(Material.CHAINMAIL_BOOTS);
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid armor type.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "What is the armor protection? (0-4)");
            return;

        }

        if (match.getProtection() == -1) {

            switch (message.toLowerCase()) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                    match.setProtection(Integer.parseInt(message));
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid protection value.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "What is the sword type? (diamond,gold,iron,wooden)");
            return;
        }

        if (match.getSwordType() == null) {
            switch (message.toLowerCase()) {
                case "diamond":
                    match.setSwordType(Material.DIAMOND_SWORD);
                    break;
                case "gold":
                    match.setSwordType(Material.GOLDEN_SWORD);
                    break;
                case "iron":
                    match.setSwordType(Material.IRON_SWORD);
                    break;
                case "wooden":
                    match.setSwordType(Material.WOODEN_SWORD);
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid sword type.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "What is the sword sharpness? (0-5)");
            return;
        }

        if (match.getSharpness() == -1) {
            switch (message.toLowerCase()) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                    match.setSharpness(Integer.parseInt(message));
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid sharpness value.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "What is the sword fire aspect? (0-2)");
            return;
        }

        if (match.getFireAspect() == -1) {
            switch (message.toLowerCase()) {
                case "0":
                case "1":
                case "2":
                    match.setFireAspect(Integer.parseInt(message));
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid fire aspect value.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "What is the bow power? (0-5)");
            return;
        }

        if (match.getPower() == -1) {

            switch (message.toLowerCase()) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                    match.setPower(Integer.parseInt(message));
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid power value.");
                    return;
            }



            if (match instanceof Banmatch) {
                player.sendMessage(STANDARD_COLOR + "What is the ban term? (Quick Example: type anything here..\nExample: 'forever and ur ip banned as well')");
            } else if (match instanceof Moneymatch)
                player.sendMessage(STANDARD_COLOR + "What is the wager amount per player?\nExample: PlayerA has 100. PlayerB has 100. Winner gets 200. In this case, wager amount is 100.");

            return;
        }

        if(match instanceof Banmatch)
        {
            Banmatch banmatch = (Banmatch) match;

            if(banmatch.getConsequentMessage() == null)
            {
                banmatch.setConsequentMessage(message);
                TextComponent tc = new TextComponent(STANDARD_COLOR + "How long is the ban? (Quick Example: 1h 25m 30s)");
                TextComponent component = new TextComponent("\n" + ChatColor.YELLOW + "Example 1: Hover to view another example.");
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "Full arguments: " + COMMAND_COLOR + "'1y 5mo 2w 0d 0h 0min 0s'\n" + STANDARD_COLOR + "Equivalent to: " + COMMAND_COLOR + "1 year, 5 months, 2 weeks, 0 days, 0 hours, 0 minutes, 0 seconds" + STANDARD_COLOR + ".\n\nFully uses all arguments provided.").create()));
                TextComponent component2 = new TextComponent("\n" + ChatColor.YELLOW + "Example 2: Hover to view another example.");
                component2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "Preferred arguments: " + COMMAND_COLOR + "'1y 9mo 4s'\n" + STANDARD_COLOR + "Equivalent to: " + COMMAND_COLOR + "1 year, 9 months, 4 seconds" + STANDARD_COLOR + ".\n\nNOTE: The other time identifiers not listed here will be by default, '0'.").create()));
                TextComponent component3 = new TextComponent("\n" + ChatColor.YELLOW + "Example 3: Hover to view another example.");
                component3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "One-way argument: " + COMMAND_COLOR + "'"  + Banmatch.getIndefiniteBanIdentifier()  + "'\n" + STANDARD_COLOR + "Equivalent to: " + COMMAND_COLOR + "Banned indefinitely" + STANDARD_COLOR + ".").create()));
                tc.addExtra(component);
                tc.addExtra(component2);
                tc.addExtra(component3);
                player.spigot().sendMessage(tc);
                return;
            }
        }

        if (match instanceof Banmatch) {

            Banmatch banmatch = (Banmatch) match;

            if (banmatch.getUnbannedTime() == null) {
                if (!matchesRegex(message) && !message.equalsIgnoreCase(Banmatch.getIndefiniteBanIdentifier())) {
                    TextComponent tc = new TextComponent(STANDARD_COLOR + "Not a valid ban date from now.\n" + STANDARD_COLOR + "How long is the ban? (Quick Example: 1h 25m 30s)");
                    TextComponent component = new TextComponent("\n" + ChatColor.YELLOW + "Example 1: Hover to view another example.");
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "Full arguments: " + COMMAND_COLOR + "'1y 5mo 2w 0d 0h 0min 0s'\n" + STANDARD_COLOR + "Equivalent to: " + COMMAND_COLOR + "1 year, 5 months, 2 weeks, 0 days, 0 hours, 0 minutes, 0 seconds" + STANDARD_COLOR + ".\n\nFully uses all arguments provided.").create()));
                    TextComponent component2 = new TextComponent("\n" + ChatColor.YELLOW + "Example 2: Hover to view another example.");
                    component2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "Preferred arguments: " + COMMAND_COLOR + "'1y 9mo 4s'\n" + STANDARD_COLOR + "Equivalent to: " + COMMAND_COLOR + "1 year, 9 months, 4 seconds" + STANDARD_COLOR + ".\n\nNOTE: The other time identifiers not listed here will be by default, '0'.").create()));
                    TextComponent component3 = new TextComponent("\n" + ChatColor.YELLOW + "Example 3: Hover to view another example.");
                    component3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "One-way argument: " + COMMAND_COLOR + "'"  + Banmatch.getIndefiniteBanIdentifier()  + "'\n" + STANDARD_COLOR + "Equivalent to: " + COMMAND_COLOR + "Banned indefinitely" + STANDARD_COLOR + ".").create()));
                    tc.addExtra(component);
                    tc.addExtra(component2);
                    tc.addExtra(component3);
                    player.spigot().sendMessage(tc);
                    return;
                }

                if (message.equalsIgnoreCase(Banmatch.getIndefiniteBanIdentifier())) {
                    banmatch.setUnbannedTime(ZonedDateTime.now().withYear(0));
                } else {
                    banmatch.setUnbannedTime(HelperMethods.toZDT(toProperties(message)));
                }
                player.sendMessage(STANDARD_COLOR + "When does this " + match.getName() + " begin? (0-60 minutes)");
                return;
            }
        } else if (match instanceof Moneymatch) {
            Moneymatch moneymatch = (Moneymatch) match;

            if (moneymatch.getWagerAmount() == -1) {
                if (!NumberUtils.isNumber(message)) {
                    player.sendMessage(STANDARD_COLOR + "Not a valid wager amount per player.\nExample: PlayerA has 100. PlayerB has 100. Winner gets 200. In this case, wager amount is 100.");
                    return;
                }
                double wagerAmount = Double.parseDouble(message);

                Bank bankA = Bank.wrap(match.getFighterA());
                Bank bankB = Bank.wrap(match.getFighterB());

                if (bankA.getCurrency() < wagerAmount && bankB.getCurrency() < wagerAmount) {
                    player.sendMessage(PLAYER_COLOR + Bukkit.getOfflinePlayer(match.getFighterA()).getName() + ERROR_COLOR + " and " + PLAYER_COLOR + Bukkit.getOfflinePlayer(match.getFighterB()).getName() + ERROR_COLOR + " do not have enough to wager. To cancel creating this match do /mm cancel.");
                    return;
                } else if (bankA.getCurrency() < wagerAmount) {
                    player.sendMessage(PLAYER_COLOR + Bukkit.getOfflinePlayer(match.getFighterA()).getName() + ERROR_COLOR + " does not have enough to wager. To cancel creating this match do /mm cancel.");
                    return;
                } else if (bankB.getCurrency() < wagerAmount) {
                    player.sendMessage(PLAYER_COLOR + Bukkit.getOfflinePlayer(match.getFighterB()).getName() + ERROR_COLOR + " does not have enough to wager. To cancel creating this match do /mm cancel.");
                    return;
                }

                moneymatch.setWagerAmount(Double.parseDouble(message));
                player.sendMessage(STANDARD_COLOR + "When does this " + match.getName() + " begin? (0-60 minutes)");
                return;
            }
        }


        if (match.getBeginCountdown() == -1) {

            if (!NumberUtils.isNumber(message) || message.contains(".")) {
                player.sendMessage(ERROR_COLOR + "Must be an integer value between [0-60].");
                return;
            }

            int numberBegin = Integer.parseInt(message);
            if (!(0 <= numberBegin && numberBegin <= 60)) {
                player.sendMessage(ERROR_COLOR + "Must be an integer value between [0-60].");
                return;
            }

            match.setBeginCountdown(numberBegin);

            //START GAME
            Match.getMatchCreatorMap().remove(player.getUniqueId());
            player.sendMessage(STANDARD_COLOR + "The " + match.getName() + " has been completed.");
            match.inform();
            match.startCountdown();
            return;
        }
    }



}
