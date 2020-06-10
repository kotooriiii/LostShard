package com.github.kotooriiii.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EnchantmentListener implements Listener {

    @EventHandler
    public void onEnchantment(EnchantItemEvent event) {
        boolean isForNextMap = true;
        if (isForNextMap)
            return;


        Map<Enchantment, Integer> map = event.getEnchantsToAdd();
        event.setExpLevelCost(moreExpensiveCost(event.getExpLevelCost()));

        HashMap<Enchantment, Integer> newMap = new HashMap<>();
        for (Map.Entry entry : map.entrySet()) {
            Enchantment enchantment = (Enchantment) entry.getKey();
            enchantment = replaceEnchantment(event.getItem(), enchantment);

            int level = (int) entry.getValue();
            level = getAllowedLevel(enchantment, level);

            newMap.put(enchantment, level);
        }

        event.getEnchantsToAdd().clear();;
        event.getEnchantsToAdd().putAll(newMap);

    }


    @EventHandler
    public void onEnchantment(PrepareItemEnchantEvent event) {
        boolean isForNextMap = true;
        if (isForNextMap)
            return;

        EnchantmentOffer[] offers = event.getOffers();


        for (int i = 0; i < offers.length; i++) {

            if (offers[i] == null)
                continue;

            Enchantment enchantment = replaceEnchantment(event.getItem());
            int allowedLevel = getAllowedLevel(enchantment, offers[i].getEnchantmentLevel());

            offers[i].setEnchantment(enchantment);
            offers[i].setEnchantmentLevel(allowedLevel);
            offers[i].setCost(moreExpensiveCost(offers[i].getCost()));
        }

    }

    private int moreExpensiveCost(int originalValue) {
        return originalValue + (int) (0.8f * (double) originalValue);
    }


    private Enchantment replaceEnchantment(ItemStack item) {
        Enchantment[] enchantments = Enchantment.values();

        int random = new Random().nextInt(Enchantment.values().length);

        Enchantment enchantment = enchantments[random];

        if (!enchantment.canEnchantItem(item) || isIllegalEnchantment(enchantment)) {
            return replaceEnchantment(item);
        }
        return enchantment;
    }

    private Enchantment replaceEnchantment(ItemStack item, Enchantment enchantment) {


        if (isIllegalEnchantment(enchantment)) {
            return replaceEnchantment(item);
        }
        return enchantment;
    }

    private boolean isIllegalEnchantment(Enchantment enchantment) {
        for (Enchantment illegalEnchantment : getIllegalEnchantments()) {
            if (enchantment.equals(illegalEnchantment))
                return true;
        }
        return false;
    }

    private Enchantment[] getIllegalEnchantments() {
        //unbreaking, sharpness, power, efficiency, protection
        return new Enchantment[]{Enchantment.DURABILITY, Enchantment.DAMAGE_ALL, Enchantment.ARROW_DAMAGE, Enchantment.DIG_SPEED, Enchantment.PROTECTION_ENVIRONMENTAL};
    }

    private int getAllowedLevel(Enchantment enchantment, int attemptedLevel) {
        if (enchantment.getMaxLevel() < attemptedLevel)
            return enchantment.getMaxLevel();
        return attemptedLevel;
    }
}
