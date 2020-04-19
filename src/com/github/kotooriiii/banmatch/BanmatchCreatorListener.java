package com.github.kotooriiii.banmatch;

import com.github.kotooriiii.channels.ShardChatEvent;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class BanmatchCreatorListener implements Listener {
    @EventHandler
    public void onChat(ShardChatEvent event) {
        Player player = event.getPlayer();

        if (!Banmatch.isCreatingBanmatch(player.getUniqueId()))
            return;

        event.setCancelled(true);

        String message = event.getMessage();
        Banmatch banmatch = Banmatch.getBanmatchCreator().get(player.getUniqueId());

        if (banmatch.getArmorType() == null) {

            switch (message.toLowerCase()) {
                case "diamond":
                    banmatch.setArmorType(Material.DIAMOND);
                    break;
                case "gold":
                    banmatch.setArmorType(Material.GOLD_INGOT);
                    break;
                case "iron":
                    banmatch.setArmorType(Material.IRON_INGOT);
                    break;
                case "chainmail":
                    banmatch.setArmorType(Material.CHAINMAIL_BOOTS);
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid armor type.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "What is the armor protection? (0-4)");
            return;

        }

        if (banmatch.getProtection() == -1) {

            switch (message.toLowerCase()) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                    banmatch.setProtection(Integer.parseInt(message));
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid protection value.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "What is the sword type? (diamond,gold,iron,chainmail)");
            return;
        }

        if (banmatch.getSwordType() == null) {
            switch (message.toLowerCase()) {
                case "diamond":
                    banmatch.setSwordType(Material.DIAMOND);
                    break;
                case "gold":
                    banmatch.setSwordType(Material.GOLD_INGOT);
                    break;
                case "iron":
                    banmatch.setSwordType(Material.IRON_INGOT);
                    break;
                case "chainmail":
                    banmatch.setSwordType(Material.CHAINMAIL_BOOTS);
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid sword type.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "What is the sword sharpness? (0-5)");
            return;
        }

        if (banmatch.getSharpness() == -1) {
            switch (message.toLowerCase()) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                    banmatch.setSharpness(Integer.parseInt(message));
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid sharpness value.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "What is the sword fire aspect? (0-2)");
            return;
        }

        if (banmatch.getFireAspect() == -1) {
            switch (message.toLowerCase()) {
                case "0":
                case "1":
                case "2":
                    banmatch.setFireAspect(Integer.parseInt(message));
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid fire aspect value.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "What is the bow power? (0-5)");
            return;
        }

        if (banmatch.getPower() == -1) {

            switch (message.toLowerCase()) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                    banmatch.setPower(Integer.parseInt(message));
                    break;
                default:
                    player.sendMessage(ERROR_COLOR + "Not a valid power value.");
                    return;
            }

            player.sendMessage(STANDARD_COLOR + "How long is the ban? (Any message goes here)");
            return;
        }

        if (banmatch.getUnbannedTime() == null) {

            banmatch.setUnbannedTime(message);
            player.sendMessage(STANDARD_COLOR + "When does this banmatch begin? (0-60 minutes)");
            return;
        }

        if (banmatch.getBanmatchBegin() == -1) {

            if (!NumberUtils.isNumber(message) || message.contains(".")) {
                player.sendMessage(ERROR_COLOR + "Must be an integer value between [0-60].");
                return;
            }

            int numberBegin = Integer.parseInt(message);
            if (!(0 <= numberBegin && numberBegin <= 60)) {
                player.sendMessage(ERROR_COLOR + "Must be an integer value between [0-60].");
                return;
            }

            banmatch.setBanmatchBegin(numberBegin);

            Banmatch.getBanmatchCreator().remove(player.getUniqueId());
            player.sendMessage(STANDARD_COLOR + "The banmatch has been completed.");
            banmatch.inform();
            return;
        }


    }
}
