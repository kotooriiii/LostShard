package com.github.kotooriiii.match;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.channels.ShardChatEvent;
import com.github.kotooriiii.match.banmatch.Banmatch;
import com.github.kotooriiii.match.moneymatch.Moneymatch;
import com.github.kotooriiii.util.HelperMethods;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
                    match.setArmorType(Material.DIAMOND);
                    break;
                case "gold":
                    match.setArmorType(Material.GOLD_INGOT);
                    break;
                case "iron":
                    match.setArmorType(Material.IRON_INGOT);
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

            player.sendMessage(STANDARD_COLOR + "What is the sword type? (diamond,gold,iron,chainmail)");
            return;
        }

        if (match.getSwordType() == null) {
            switch (message.toLowerCase()) {
                case "diamond":
                    match.setSwordType(Material.DIAMOND);
                    break;
                case "gold":
                    match.setSwordType(Material.GOLD_INGOT);
                    break;
                case "iron":
                    match.setSwordType(Material.IRON_INGOT);
                    break;
                case "chainmail":
                    match.setSwordType(Material.CHAINMAIL_BOOTS);
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

            if(match instanceof Banmatch)
            player.sendMessage(STANDARD_COLOR + "How long is the ban? \nExample: '1y 2m 3w 4d 5h 6m 0s' is 1year2months3weeks4days5hours6minutes0seconds.\nOR 'indefinite' for a forever ban.");
            else if (match instanceof Moneymatch)
                player.sendMessage(STANDARD_COLOR + "What is the wager amount per player?\nExample: PlayerA has 100. PlayerB has 100. Winner gets 200. In this case, wager amount is 100.");

            return;
        }
        if (match instanceof Banmatch) {

            Banmatch banmatch = (Banmatch) match;

            if (banmatch.getUnbannedTime() == null) {
                if (!message.matches("[0-9]+y\\s[0-9]+m\\s[0-9]+w\\s[0-9]+d\\s[0-9]+h\\s[0-9]+m\\s[0-9]+s") && !message.equalsIgnoreCase("indefinite")) {
                    player.sendMessage(STANDARD_COLOR + "Not a valid ban date.\nExample: '1y 2m 3w 4d 5h 6m 0s' is 1year2months3weeks4days5hours6minutes0seconds.\nOR 'indefinite' for a forever ban.");
                }
                if (message.equalsIgnoreCase("indefinite")) {
                    banmatch.setUnbannedTime(null);
                } else {
                    String[] args = message.split(" ");
                    int[] howLongInt = new int[7];
                    for (int i = 0; i < howLongInt.length; i++)
                        howLongInt[i] = Integer.parseInt(args[i].substring(0, args[i].length() - 1));
                    banmatch.setUnbannedTime(HelperMethods.toZDT(howLongInt));
                }
                player.sendMessage(STANDARD_COLOR + "When does this " + match.getName() + " begin? (0-60 minutes)");
                return;
            }
        } else if (match instanceof Moneymatch)
        {
            Moneymatch moneymatch = (Moneymatch) match;

            if (moneymatch.getWagerAmount() == -1) {
                if (!NumberUtils.isNumber(message)) {
                    player.sendMessage(STANDARD_COLOR + "Not a valid wager amount per player.\nExample: PlayerA has 100. PlayerB has 100. Winner gets 200. In this case, wager amount is 100.");
                }
                double wagerAmount = Double.parseDouble(message);

                Bank bankA = Bank.wrap(match.getFighterA());
                Bank bankB = Bank.wrap(match.getFighterB());

                if(bankA.getCurrency() < wagerAmount && bankB.getCurrency() < wagerAmount)
                {
                    player.sendMessage(PLAYER_COLOR + Bukkit.getOfflinePlayer(match.getFighterA()).getName() + ERROR_COLOR + " and " + PLAYER_COLOR + Bukkit.getOfflinePlayer(match.getFighterB()).getName() + ERROR_COLOR + " do not have enough to wager. To cancel creating this match do /mm cancel.");
                    return;
                }
               else if(bankA.getCurrency() < wagerAmount)
                {
                    player.sendMessage(PLAYER_COLOR + Bukkit.getOfflinePlayer(match.getFighterA()).getName() + ERROR_COLOR + " does not have enough to wager. To cancel creating this match do /mm cancel.");
                    return;
                } else if (bankB.getCurrency() < wagerAmount)
                {
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
