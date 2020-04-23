package com.github.kotooriiii.match;

import com.github.kotooriiii.status.Staff;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

import static com.github.kotooriiii.data.Maps.PLAYER_COLOR;
import static com.github.kotooriiii.util.HelperMethods.*;

public class MatchCheatingListener implements Listener {

    @EventHandler
    public void onBowFire(EntityShootBowEvent event) {
        if (event.isCancelled())
            return;
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!Match.hasActiveMatch())
            return;
        Match match = Match.getActiveMatch();
        if (!match.hasGameStarted())
            return;

        if (match.isFighter(player.getUniqueId())) {
            //Check if right equipment
            if (!isValidEquipment(match, player))
            {
                sendToAll(PLAYER_COLOR + player.getName() +ChatColor.GREEN + " cheated in the match.");
                match.end(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onMatchCheating(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;

        Entity defender = event.getEntity();
        Entity damager = event.getDamager();

        if (!isPlayerInduced(defender, damager))
            return;

        //Entties are players
        Player damagerPlayer = getPlayerInduced(defender, damager);
        Player defenderPlayer = (Player) defender;

        //
        //The code now checks if defender has right equipment AND CHECK ATTACKER TOO (armor).
        //

        if (!Match.hasActiveMatch())
            return;
        Match match = Match.getActiveMatch();
        if (!match.hasGameStarted())
            return;

        if (match.isFighter(damagerPlayer.getUniqueId()) && match.isFighter(defenderPlayer.getUniqueId())) {
            //Check if right equipment
            if(!isValidEquipment(match, damagerPlayer) && !isValidEquipment(match, defenderPlayer))
            {
                sendToAll(PLAYER_COLOR + damagerPlayer.getName() + ChatColor.GREEN + " and " + PLAYER_COLOR + defenderPlayer.getName() + ChatColor.GREEN + " both cheated in the match.");
                match.cancel(false);

            }
            else if (!isValidEquipment(match, damagerPlayer))
            {
                sendToAll(PLAYER_COLOR + damagerPlayer.getName() +ChatColor.GREEN + " cheated in the match.");
                match.end(damagerPlayer.getUniqueId());

            }
            else if(!isValidEquipment(match, defenderPlayer))
            {
                sendToAll(PLAYER_COLOR + defenderPlayer.getName() + ChatColor.GREEN + " cheated in the match.");
                match.end(defenderPlayer.getUniqueId());
            }

        } else if (!match.isFighter(damagerPlayer.getUniqueId()) && match.isFighter(defenderPlayer.getUniqueId())) {
            for (Player player : Staff.getOnlineStaffPlayers()) {
                player.sendMessage(PLAYER_COLOR + "" + damagerPlayer.getName() + " cheated by damaging a player in the arena.");
            }
            match.cancel(false);
        } else if (match.isFighter(damagerPlayer.getUniqueId()) && !match.isFighter(defenderPlayer.getUniqueId())) {
            for (Player player : Staff.getOnlineStaffPlayers()) {
                player.sendMessage(PLAYER_COLOR + "" + defenderPlayer.getName() + " cheated by damaging a player in the arena.");
            }
            match.cancel(false);
        }

    }

    private boolean isValidEquipment(Match match, Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();

        if (isSword(mainHand.getType())) {
            if (isHigherSwordType(match.getSwordType(), mainHand.getType()))   {
                return false;

            }
            if (isHigherSwordSharpness(match.getSharpness(), mainHand)) {
                return false;

            }
            if (isHigherSwordFire(match.getFireAspect(), mainHand)) {
                return false;

            }
        }

        if (isBow(mainHand.getType())) {
            if (isHigherBowPower(match.getPower(), mainHand)) {
                return false;

            }
        }

        if (isHigherArmorType(match, inventory.getArmorContents())) {
            return false;

        }
        if (isHigherProtection(match, inventory.getArmorContents())) {
            return false;

        }


        return true;
    }

    private boolean isSword(Material type) {

        switch (type) {
            case WOODEN_SWORD:
            case IRON_SWORD:
            case GOLDEN_SWORD:
            case DIAMOND_SWORD:
                return true;
            default:
                return false;
        }
    }

    private boolean isHigherSwordType(Material highestType, Material type) {
        final HashMap<Material, Integer> swordPriority = new HashMap<>();
        swordPriority.put(Material.WOODEN_SWORD, 0);
        swordPriority.put(Material.IRON_SWORD, 1);
        swordPriority.put(Material.GOLDEN_SWORD, 2);
        swordPriority.put(Material.DIAMOND_SWORD, 3);

        if (swordPriority.get(type) > swordPriority.get(highestType))
            return true;
        return false;
    }

    private boolean isHigherSwordSharpness(int highestLevel, ItemStack itemStack) {
        return itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) > highestLevel;
    }

    private boolean isHigherSwordFire(int highestLevel, ItemStack itemStack) {
        return itemStack.getEnchantmentLevel(Enchantment.FIRE_ASPECT) > highestLevel;
    }

    private boolean isBow(Material type) {

        switch (type) {
            case BOW:
                return true;
            default:
                return false;
        }
    }

    private boolean isHigherBowPower(int highestLevel, ItemStack itemStack) {
        return itemStack.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) > highestLevel;
    }

    private boolean isHigherArmorType(Match match, ItemStack[] armorContents) {

        final HashMap<Material, Integer> helmetPriority = new HashMap<>();
        helmetPriority.put(Material.CHAINMAIL_HELMET, 0);
        helmetPriority.put(Material.IRON_HELMET, 1);
        helmetPriority.put(Material.GOLDEN_HELMET, 2);
        helmetPriority.put(Material.DIAMOND_HELMET, 3);

        final HashMap<Material, Integer> chestPriority = new HashMap<>();
        chestPriority.put(Material.CHAINMAIL_CHESTPLATE, 0);
        chestPriority.put(Material.IRON_CHESTPLATE, 1);
        chestPriority.put(Material.GOLDEN_CHESTPLATE, 2);
        chestPriority.put(Material.DIAMOND_CHESTPLATE, 3);

        final HashMap<Material, Integer> leggingsPriority = new HashMap<>();
        leggingsPriority.put(Material.CHAINMAIL_LEGGINGS, 0);
        leggingsPriority.put(Material.IRON_LEGGINGS, 1);
        leggingsPriority.put(Material.GOLDEN_LEGGINGS, 2);
        leggingsPriority.put(Material.DIAMOND_LEGGINGS, 3);

        final HashMap<Material, Integer> bootsPriority = new HashMap<>();
        bootsPriority.put(Material.CHAINMAIL_BOOTS, 0);
        bootsPriority.put(Material.IRON_BOOTS, 1);
        bootsPriority.put(Material.GOLDEN_BOOTS, 2);
        bootsPriority.put(Material.DIAMOND_BOOTS, 3);


        int priority = bootsPriority.get(match.getArmorType());

        forLoop:
        for (int i = 0; i < 4; i++) {
            if(armorContents[i] == null || armorContents[i].getType() ==  null || armorContents[i].getType().equals(Material.AIR))
                continue;
            switchLoop:
            switch (i) {
                case 0:
                    if (priority < bootsPriority.get(armorContents[i].getType())){
                        return true;
                    }
                    break switchLoop;
                case 1:
                    if (priority < leggingsPriority.get(armorContents[i].getType())){
                        return true;
                    }
                    break switchLoop;
                case 2:
                    if (priority < chestPriority.get(armorContents[i].getType())){
                        return true;
                    }
                    break switchLoop;
                case 3:
                    if (priority < helmetPriority.get(armorContents[i].getType())){
                        return true;
                    }
                    break switchLoop;
            }
        }
        return false;
    }

    private boolean isHigherProtection(Match match, ItemStack[] armorContents) {

        for (int i = 0; i < 4; i++) {
            if(armorContents[i] == null)
                continue;
            if (match.getProtection() < armorContents[i].getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL))
                return true;
        }
        return false;
    }


}
