package com.github.kotooriiii.skills.skill_listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.Skill;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class EnchantingListener implements Listener {

    //Skill XP
    final int ADDED_XP = 100;


    private static class BetterEnchantment {

        //100
        public static final BetterEnchantment LOOTING_3 = new BetterEnchantment(Enchantment.LOOT_BONUS_MOBS, 3, 20);
        public static final BetterEnchantment FORTUNE_2 = new BetterEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 2, 20);

        //90
        public static final BetterEnchantment FEATHER_FALLING_4 = new BetterEnchantment(Enchantment.PROTECTION_FALL, 4, 15);
        public static final BetterEnchantment SILK_TOUCH_1 = new BetterEnchantment(Enchantment.SILK_TOUCH, 1, 15);

        //80
        public static final BetterEnchantment SMITE_5 = new BetterEnchantment(Enchantment.DAMAGE_UNDEAD, 5, 15);
        public static final BetterEnchantment BANE_OF_ARTHROPODS_5 = new BetterEnchantment(Enchantment.DAMAGE_ARTHROPODS, 5, 15);
        public static final BetterEnchantment RESPIRATION_3 = new BetterEnchantment(Enchantment.OXYGEN, 3, 15);

        //70
        public static final BetterEnchantment FIRE_PROTECTION_4 = new BetterEnchantment(Enchantment.PROTECTION_FIRE, 4, 10);
        public static final BetterEnchantment BLAST_PROTECTION_4 = new BetterEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4, 10);
        public static final BetterEnchantment LOOTING_2 = new BetterEnchantment(Enchantment.LOOT_BONUS_MOBS, 2, 10);
        public static final BetterEnchantment PROJECTILE_PROTECTION_4 = new BetterEnchantment(Enchantment.PROTECTION_PROJECTILE, 4, 10);
        public static final BetterEnchantment LURE_3 = new BetterEnchantment(Enchantment.LURE, 3, 10);

        //60
        public static final BetterEnchantment SMITE_4 = new BetterEnchantment(Enchantment.DAMAGE_UNDEAD, 4, 8);
        public static final BetterEnchantment BANE_OF_ARTHROPODS_4 = new BetterEnchantment(Enchantment.DAMAGE_ARTHROPODS, 4, 8);
        public static final BetterEnchantment LUCK_OF_THE_SEA_3 = new BetterEnchantment(Enchantment.LUCK, 3, 8);
        public static final BetterEnchantment FEATHER_FALLING_3 = new BetterEnchantment(Enchantment.PROTECTION_FALL, 3, 8);

        //50
        public static final BetterEnchantment FORTUNE_1 = new BetterEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1, 8);
        public static final BetterEnchantment LURE_2 = new BetterEnchantment(Enchantment.LURE, 2, 8);
        public static final BetterEnchantment RESPIRATION_2 = new BetterEnchantment(Enchantment.OXYGEN, 2, 8);
        public static final BetterEnchantment SOUL_SPEED_3 = new BetterEnchantment(Enchantment.SOUL_SPEED, 3, 8);

        //40
        public static final BetterEnchantment SMITE_3 = new BetterEnchantment(Enchantment.DAMAGE_UNDEAD, 3, 5);
        public static final BetterEnchantment BANE_OF_ARTHROPODS_3 = new BetterEnchantment(Enchantment.DAMAGE_ARTHROPODS, 3, 5);
        public static final BetterEnchantment LOOTING_1 = new BetterEnchantment(Enchantment.LOOT_BONUS_MOBS, 1, 5);
        public static final BetterEnchantment LUCK_OF_THE_SEA_2 = new BetterEnchantment(Enchantment.LUCK, 2, 5);

        //30
        public static final BetterEnchantment FIRE_PROTECTION_3 = new BetterEnchantment(Enchantment.PROTECTION_FIRE, 3, 5);
        public static final BetterEnchantment BLAST_PROTECTION_3 = new BetterEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 3, 5);
        public static final BetterEnchantment PROJECTILE_PROTECTION_3 = new BetterEnchantment(Enchantment.PROTECTION_PROJECTILE, 3, 5);
        public static final BetterEnchantment SOUL_SPEED_2 = new BetterEnchantment(Enchantment.SOUL_SPEED, 2, 5);

        //25
        public static final BetterEnchantment RESPIRATION_1 = new BetterEnchantment(Enchantment.OXYGEN, 1, 5);
        public static final BetterEnchantment FEATHER_FALLING_2 = new BetterEnchantment(Enchantment.PROTECTION_FALL, 2, 5);
        public static final BetterEnchantment AQUA_AFFINITY_1 = new BetterEnchantment(Enchantment.WATER_WORKER, 1, 5);

        //20
        public static final BetterEnchantment SMITE_2 = new BetterEnchantment(Enchantment.DAMAGE_UNDEAD, 2, 3);
        public static final BetterEnchantment BANE_OF_ARTHROPODS_2 = new BetterEnchantment(Enchantment.DAMAGE_ARTHROPODS, 2, 3);
        public static final BetterEnchantment SOUL_SPEED_1 = new BetterEnchantment(Enchantment.SOUL_SPEED, 1, 3);
        public static final BetterEnchantment LUCK_OF_THE_SEA_1 = new BetterEnchantment(Enchantment.LUCK, 1, 3);

        //15
        public static final BetterEnchantment FIRE_PROTECTION_2 = new BetterEnchantment(Enchantment.PROTECTION_FIRE, 2, 3);
        public static final BetterEnchantment BLAST_PROTECTION_2 = new BetterEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2, 3);
        public static final BetterEnchantment PROJECTILE_PROTECTION_2 = new BetterEnchantment(Enchantment.PROTECTION_PROJECTILE, 2, 3);

        //10
        public static final BetterEnchantment FEATHER_FALLING_1 = new BetterEnchantment(Enchantment.PROTECTION_FALL, 1, 1);

        //5
        public static final BetterEnchantment FIRE_PROTECTION_1 = new BetterEnchantment(Enchantment.PROTECTION_FIRE, 1, 1);
        public static final BetterEnchantment BANE_OF_ARTHROPODS_1 = new BetterEnchantment(Enchantment.DAMAGE_ARTHROPODS, 1, 1);
        public static final BetterEnchantment PROJECTILE_PROTECTION_1 = new BetterEnchantment(Enchantment.PROTECTION_PROJECTILE, 1, 1);

        //0
        public static final BetterEnchantment SMITE_1 = new BetterEnchantment(Enchantment.DAMAGE_UNDEAD, 1, 1);
        public static final BetterEnchantment BLAST_PROTECTION_1 = new BetterEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 1, 1);
        public static final BetterEnchantment LURE_1 = new BetterEnchantment(Enchantment.LURE, 1, 1);


        public final Enchantment enchantment;
        private final int emeraldCount;
        public final int level;

        public BetterEnchantment(Enchantment enchantment, int level, int emeraldCount) {
            this.enchantment = enchantment;
            this.level = level;
            this.emeraldCount = emeraldCount;
        }

        @Override
        public String toString() {
            return this.enchantment.getKey().getKey() + " " + this.level;
        }

        @Override
        public boolean equals(Object obj) {
            return enchantment.equals(obj);
        }

        @Override
        public int hashCode() {
            return enchantment.hashCode();
        }
    }

    private static class SmartEnchantment {
        public EnchantmentOffer[] offers;
        public int oldXP;

        public SmartEnchantment() {

        }

        public SmartEnchantment(EnchantmentOffer[] offers, int oldXP) {
            this.offers = offers;
            this.oldXP = oldXP;
        }
    }

    //Mutual exlusives
    private static final Enchantment[] protectionsArr = new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE};
    private static final Enchantment[] bootsArr = new Enchantment[]{Enchantment.DEPTH_STRIDER, Enchantment.FROST_WALKER};
    private static final Enchantment[] physicalArr = new Enchantment[]{Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ARTHROPODS, Enchantment.DAMAGE_UNDEAD};
    private static final Enchantment[] toolsArr = new Enchantment[]{Enchantment.LOOT_BONUS_BLOCKS, Enchantment.SILK_TOUCH};
    private static final Enchantment[] tridentArr = new Enchantment[]{Enchantment.CHANNELING, Enchantment.LOYALTY, Enchantment.RIPTIDE};
    private static final Enchantment[] crossbowArr = new Enchantment[]{Enchantment.PIERCING, Enchantment.MULTISHOT};

    //Lapis
    private static ItemStack lapis;

    //XP
    private static int ENCHANT_LEVEL_XP = 30;

    //ID
    private final static String ID = ChatColor.DARK_PURPLE + "Enchantments: ";
    private final static String NONE = ChatColor.DARK_PURPLE + "-NONE";

    private final static HashMap<UUID, SmartEnchantment> trackMap = new HashMap<>();


    public EnchantingListener() {
        lapis = new ItemStack(Material.LAPIS_LAZULI, 3);
        final ItemMeta itemMeta = lapis.getItemMeta();
        itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Nickolov's Lazuli");
        lapis.setItemMeta(itemMeta);
    }

    //
    //METHODS
    //

    private String toReadable(String s) {
        final String[] arr = s.split("_");
        for (int i = 0; i < arr.length; i++) {

            if (arr[i] == null || arr[i].isEmpty())
                continue;

            arr[i] = arr[i].substring(0, 1).toUpperCase() + arr[i].substring(1).toLowerCase();
        }

        return HelperMethods.stringBuilder(arr, 0, " ");
    }

    private static String toEnchantID(String s) {
        final String[] arr = s.split(" ");
        for (int i = 0; i < arr.length; i++) {

            if (arr[i] == null || arr[i].isEmpty())
                continue;

            arr[i] = arr[i].toLowerCase();
        }

        return HelperMethods.stringBuilder(arr, 0, "_");
    }


    private ArrayList<BetterEnchantment> getValidEnchantment(int level) {
        ArrayList<BetterEnchantment> betterEnchantments = new ArrayList<>();

        if (level >= 100) {
            betterEnchantments.add(BetterEnchantment.LOOTING_3);
            betterEnchantments.add(BetterEnchantment.FORTUNE_2);
        }

        if (level >= 90) {
            betterEnchantments.add(BetterEnchantment.FEATHER_FALLING_4);
            betterEnchantments.add(BetterEnchantment.SILK_TOUCH_1);
        }

        if (level >= 80) {
            betterEnchantments.add(BetterEnchantment.SMITE_5);
            betterEnchantments.add(BetterEnchantment.BANE_OF_ARTHROPODS_5);
            betterEnchantments.add(BetterEnchantment.RESPIRATION_3);
        }

        if (level >= 70) {
            betterEnchantments.add(BetterEnchantment.FIRE_PROTECTION_4);
            betterEnchantments.add(BetterEnchantment.BLAST_PROTECTION_4);
            betterEnchantments.add(BetterEnchantment.LOOTING_2);
            betterEnchantments.add(BetterEnchantment.PROJECTILE_PROTECTION_4);
            betterEnchantments.add(BetterEnchantment.LURE_3);
        }
        if (level >= 60) {
            betterEnchantments.add(BetterEnchantment.SMITE_4);
            betterEnchantments.add(BetterEnchantment.BANE_OF_ARTHROPODS_4);
            betterEnchantments.add(BetterEnchantment.LUCK_OF_THE_SEA_3);
            betterEnchantments.add(BetterEnchantment.FEATHER_FALLING_3);
        }

        if (level >= 50) {
            betterEnchantments.add(BetterEnchantment.FORTUNE_1);
            betterEnchantments.add(BetterEnchantment.LURE_2);
            betterEnchantments.add(BetterEnchantment.RESPIRATION_2);
            betterEnchantments.add(BetterEnchantment.SOUL_SPEED_3);
        }

        if (level >= 40) {
            betterEnchantments.add(BetterEnchantment.SMITE_3);
            betterEnchantments.add(BetterEnchantment.BANE_OF_ARTHROPODS_3);
            betterEnchantments.add(BetterEnchantment.LOOTING_1);
            betterEnchantments.add(BetterEnchantment.LUCK_OF_THE_SEA_2);
        }

        if (level >= 30) {
            betterEnchantments.add(BetterEnchantment.FIRE_PROTECTION_3);
            betterEnchantments.add(BetterEnchantment.BLAST_PROTECTION_3);
            betterEnchantments.add(BetterEnchantment.PROJECTILE_PROTECTION_3);
            betterEnchantments.add(BetterEnchantment.SOUL_SPEED_2);
        }

        if (level >= 25) {
            betterEnchantments.add(BetterEnchantment.RESPIRATION_1);
            betterEnchantments.add(BetterEnchantment.FEATHER_FALLING_2);
            betterEnchantments.add(BetterEnchantment.AQUA_AFFINITY_1);
        }

        if (level >= 20) {
            betterEnchantments.add(BetterEnchantment.SMITE_2);
            betterEnchantments.add(BetterEnchantment.BANE_OF_ARTHROPODS_2);
            betterEnchantments.add(BetterEnchantment.SOUL_SPEED_1);
            betterEnchantments.add(BetterEnchantment.LUCK_OF_THE_SEA_1);
        }

        if (level >= 15) {
            betterEnchantments.add(BetterEnchantment.FIRE_PROTECTION_2);
            betterEnchantments.add(BetterEnchantment.BLAST_PROTECTION_2);
            betterEnchantments.add(BetterEnchantment.PROJECTILE_PROTECTION_2);
        }


        if (level >= 10) {
            betterEnchantments.add(BetterEnchantment.FEATHER_FALLING_1);

        }

        if (level >= 5) {
            betterEnchantments.add(BetterEnchantment.FIRE_PROTECTION_1);
            betterEnchantments.add(BetterEnchantment.BANE_OF_ARTHROPODS_1);
            betterEnchantments.add(BetterEnchantment.PROJECTILE_PROTECTION_1);
        }

        if (level >= 0) {
            betterEnchantments.add(BetterEnchantment.SMITE_1);
            betterEnchantments.add(BetterEnchantment.BLAST_PROTECTION_1);
            betterEnchantments.add(BetterEnchantment.LURE_1);
        }

        return betterEnchantments;
    }

    private void filter(ArrayList<BetterEnchantment> list, Material material) {

        if (material == null) {
            list.clear();
        }

        String name = material.getKey().getKey().toLowerCase();
        if (name.endsWith("_helmet")) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.PROTECTION_FIRE &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_PROJECTILE &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_EXPLOSIONS &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_ENVIRONMENTAL &&
                            betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.OXYGEN &&
                            betterEnchantment.enchantment != Enchantment.WATER_WORKER &&
                            betterEnchantment.enchantment != Enchantment.THORNS &&
                            betterEnchantment.enchantment != Enchantment.MENDING &&
                            betterEnchantment.enchantment != Enchantment.BINDING_CURSE &&
                            betterEnchantment.enchantment != Enchantment.VANISHING_CURSE);
        } else if (name.endsWith("_chestplate")) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.PROTECTION_FIRE &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_PROJECTILE &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_EXPLOSIONS &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_ENVIRONMENTAL &&
                            betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.THORNS &&
                            betterEnchantment.enchantment != Enchantment.MENDING &&
                            betterEnchantment.enchantment != Enchantment.BINDING_CURSE &&
                            betterEnchantment.enchantment != Enchantment.VANISHING_CURSE);
        } else if (name.endsWith("_leggings")) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.PROTECTION_FIRE &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_PROJECTILE &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_EXPLOSIONS &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_ENVIRONMENTAL &&
                            betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.THORNS &&
                            betterEnchantment.enchantment != Enchantment.MENDING &&
                            betterEnchantment.enchantment != Enchantment.BINDING_CURSE &&
                            betterEnchantment.enchantment != Enchantment.VANISHING_CURSE);
        } else if (name.endsWith("_boots")) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.PROTECTION_FIRE &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_PROJECTILE &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_EXPLOSIONS &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_ENVIRONMENTAL &&
                            betterEnchantment.enchantment != Enchantment.PROTECTION_FALL &&
                            betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.THORNS &&
                            betterEnchantment.enchantment != Enchantment.DEPTH_STRIDER &&
                            betterEnchantment.enchantment != Enchantment.SOUL_SPEED &&
                            betterEnchantment.enchantment != Enchantment.FROST_WALKER &&
                            betterEnchantment.enchantment != Enchantment.MENDING &&
                            betterEnchantment.enchantment != Enchantment.BINDING_CURSE &&
                            betterEnchantment.enchantment != Enchantment.VANISHING_CURSE);
        } else if (name.endsWith("_sword")) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.DAMAGE_ARTHROPODS &&
                            betterEnchantment.enchantment != Enchantment.DAMAGE_UNDEAD &&
                            betterEnchantment.enchantment != Enchantment.DAMAGE_ALL &&
                            betterEnchantment.enchantment != Enchantment.FIRE_ASPECT &&
                            betterEnchantment.enchantment != Enchantment.LOOT_BONUS_MOBS &&
                            betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.MENDING &&
                            betterEnchantment.enchantment != Enchantment.KNOCKBACK &&
                            betterEnchantment.enchantment != Enchantment.SWEEPING_EDGE &&
                            betterEnchantment.enchantment != Enchantment.VANISHING_CURSE);
        } else if (name.endsWith("_axe") || name.endsWith("_pickaxe") || name.endsWith("_shovel") || name.endsWith("_hoe")) {


            list.removeIf(betterEnchantment -> {

                boolean result = true;

                if (name.endsWith("_axe")) {

                    result = betterEnchantment.enchantment != Enchantment.DAMAGE_ARTHROPODS &&
                            betterEnchantment.enchantment != Enchantment.DAMAGE_UNDEAD &&
                            betterEnchantment.enchantment != Enchantment.DAMAGE_ALL;


                }

                //If result is FALSE, it WILL never make result true.

                result = result &&
                        betterEnchantment.enchantment != Enchantment.DURABILITY &&
                        betterEnchantment.enchantment != Enchantment.DIG_SPEED &&
                        betterEnchantment.enchantment != Enchantment.SILK_TOUCH &&
                        betterEnchantment.enchantment != Enchantment.LOOT_BONUS_BLOCKS &&
                        betterEnchantment.enchantment != Enchantment.MENDING &&
                        betterEnchantment.enchantment != Enchantment.VANISHING_CURSE;
                return result;
            });
        } else if (material == Material.BOW) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.ARROW_DAMAGE &&
                            betterEnchantment.enchantment != Enchantment.ARROW_KNOCKBACK &&
                            betterEnchantment.enchantment != Enchantment.ARROW_FIRE &&
                            betterEnchantment.enchantment != Enchantment.ARROW_INFINITE &&
                            betterEnchantment.enchantment != Enchantment.MENDING &&
                            betterEnchantment.enchantment != Enchantment.VANISHING_CURSE);
        } else if (material == Material.FISHING_ROD) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.LURE &&
                            betterEnchantment.enchantment != Enchantment.MENDING &&
                            betterEnchantment.enchantment != Enchantment.LUCK &&
                            betterEnchantment.enchantment != Enchantment.VANISHING_CURSE);
        } else if (material == Material.TRIDENT) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.IMPALING &&
                            betterEnchantment.enchantment != Enchantment.MENDING &&

                            betterEnchantment.enchantment != Enchantment.CHANNELING &&
                            betterEnchantment.enchantment != Enchantment.LOYALTY &&
                            betterEnchantment.enchantment != Enchantment.RIPTIDE &&

                            betterEnchantment.enchantment != Enchantment.VANISHING_CURSE);
        } else if (material == Material.CROSSBOW) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.QUICK_CHARGE &&
                            betterEnchantment.enchantment != Enchantment.MENDING &&

                            betterEnchantment.enchantment != Enchantment.PIERCING &&
                            betterEnchantment.enchantment != Enchantment.MULTISHOT &&

                            betterEnchantment.enchantment != Enchantment.VANISHING_CURSE);
        } else if (material == Material.SHEARS) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.DIG_SPEED &&
                            betterEnchantment.enchantment != Enchantment.MENDING &&

                            betterEnchantment.enchantment != Enchantment.VANISHING_CURSE);
        } else if (material == Material.SHIELD || material == Material.ELYTRA || material == Material.FLINT_AND_STEEL || material == Material.CARROT_ON_A_STICK || material == Material.WARPED_FUNGUS_ON_A_STICK) {
            list.removeIf(betterEnchantment ->
                    betterEnchantment.enchantment != Enchantment.DURABILITY &&
                            betterEnchantment.enchantment != Enchantment.MENDING);
        } else {
            list.clear();
        }


    }

    private void removeExclusives(ArrayList<BetterEnchantment> list, Set<Enchantment> currentEnchantmentsOnItem) {
        removeExclusive(list, currentEnchantmentsOnItem, protectionsArr);
        removeExclusive(list, currentEnchantmentsOnItem, bootsArr);
        removeExclusive(list, currentEnchantmentsOnItem, physicalArr);
        removeExclusive(list, currentEnchantmentsOnItem, toolsArr);
        removeExclusive(list, currentEnchantmentsOnItem, tridentArr);
        removeExclusive(list, currentEnchantmentsOnItem, crossbowArr);
    }

    private void removeExclusive(ArrayList<BetterEnchantment> list, Set<Enchantment> currentEnchantmentsOnItem, Enchantment[] exclusiveArray) {
        int index = -1;

        //Loop exclusives
        for (int i = 0; i < exclusiveArray.length; i++) {

            Enchantment iteratedEnchantment = exclusiveArray[i];

            if (index == -1) {
                //Loop current enchanted on item
                for (Enchantment currentEnchantment : currentEnchantmentsOnItem) {
                    //Check if current enchant equals exclusive enchant
                    if (iteratedEnchantment.getKey().getKey().equals(currentEnchantment.getKey().getKey())) {
                        index = i;
                        i = -1;
                        continue;
                    }
                }
            } else {
                if (index == i)
                    continue;
                list.removeIf(betterEnchantment -> (iteratedEnchantment.getKey().getKey().equals(betterEnchantment.enchantment.getKey().getKey())));
            }

        }
    }

    private void removeHigherLevels(ArrayList<BetterEnchantment> list, Map<Enchantment, Integer> currentEnchantmentsOnItem) {

        for (Map.Entry<Enchantment, Integer> entry : currentEnchantmentsOnItem.entrySet()) {
            final Enchantment enchantment = entry.getKey();
            final Integer enchantedLevel = entry.getValue();

            //We are removing enchantments of the same TYPE.
            //Once we find the same type, we only keep the NEXT level


            list.removeIf(betterEnchantment -> (enchantment.getKey().getKey().equals(betterEnchantment.enchantment.getKey().getKey()))
                    && (enchantedLevel + 1 != (betterEnchantment.level))
            );
        }


        HashMap<String, Integer> enchantmentIntegerHashMap = new HashMap<>();

        for (BetterEnchantment betterEnchantment : list) {
            final Integer integer = enchantmentIntegerHashMap.get(betterEnchantment.enchantment.getKey().getKey());
            if (integer == null) {
                enchantmentIntegerHashMap.put(betterEnchantment.enchantment.getKey().getKey(), betterEnchantment.level);
                continue;
            }

            if (integer.intValue() > betterEnchantment.level) {
                enchantmentIntegerHashMap.put(betterEnchantment.enchantment.getKey().getKey(), betterEnchantment.level);
                continue;
            }
        }

        list.removeIf(betterEnchantment -> enchantmentIntegerHashMap.get(betterEnchantment.enchantment.getKey().getKey()) != betterEnchantment.level);
    }

    private BetterEnchantment[] getThree(ArrayList<BetterEnchantment> list) {
        //Smallest size of list to a min of 3 because theres only 3 offers.
        final int SIZE = Math.min(list.size(), 3);

        BetterEnchantment[] betterEnchantments = new BetterEnchantment[3];

        /*

            The index must be less than the size.
                Example:
                    Size of array is 3.
                    Index MUST BE 2 at the most.
                Example 2:
                    Size of array is 2.
                    Index MUST BE 1 at most.


         */

//        int index = 0;
//
//        FillTheBE:
//        while (index < SIZE) {
//            int randomInt = (int) (Math.random() * list.size()); //random selection of list element
//            BetterEnchantment betterEnchantmentIterated = list.get(randomInt);
//
//
//            //WE DO NOT WANT DUPLICATES SO FIND IT
//            Set:
//            for (BetterEnchantment betterEnchantmentCheck : betterEnchantments) {
//                if (betterEnchantmentCheck == betterEnchantmentIterated) {
//                    continue FillTheBE;
//                }
//            }
//
//            betterEnchantments[index++] = betterEnchantmentIterated;
//        }

        int index = 0;
        while (index < SIZE) {
            betterEnchantments[index] = list.get(index);
            index++;
        }

        return betterEnchantments;

    }

    private EnchantmentOffer[] toEnchantmentOffer(BetterEnchantment[] betterEnchantments) {

        EnchantmentOffer[] enchantmentOffers = new EnchantmentOffer[3];

        for (int i = 0; i < betterEnchantments.length; i++) {

            final BetterEnchantment betterEnchantment = betterEnchantments[i];
            if (betterEnchantment == null)
                continue;

            EnchantmentOffer enchantmentOffer = new EnchantmentOffer(betterEnchantment.enchantment, betterEnchantment.level, betterEnchantment.emeraldCount);

            enchantmentOffers[i] = enchantmentOffer;
        }
        return enchantmentOffers;
    }

    private void toLore(ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final Map<Enchantment, Integer> enchants = itemMeta.getEnchants();

        List<String> lore = itemMeta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        } else {
            lore = new ArrayList<>(lore);
        }

        if (enchants.isEmpty()) {
            lore.add(NONE);

        } else {
            lore.add(ID);

            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                final Enchantment key = entry.getKey();
                final Integer value = entry.getValue();
                lore.add(ChatColor.DARK_PURPLE + "- " + toReadable(key.getKey().getKey()) + " " + value);
            }

            for (Enchantment enchantment : itemMeta.getEnchants().keySet())
                itemMeta.removeEnchant(enchantment);
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

    }

    public static void toEnchant(ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        final Iterator<String> iterator = lore.iterator();

        boolean isActive = false;

        HashMap<Enchantment, Integer> map = new HashMap<>();

        while (iterator.hasNext()) {
            final String next = iterator.next();

            if (!isActive && next.equals(NONE)) {
                iterator.remove();
                break;
            }

            if (!isActive && next.equals(ID)) {
                isActive = true;
                iterator.remove();
                continue;

//           }  else if (isActive && next.startsWith(ChatColor.DARK_PURPLE + "- ")) {
//                iterator.remove();
//                break;
            } else if (isActive && !next.startsWith(ChatColor.DARK_PURPLE + "- ")) {
                break;
            } else if (!isActive) {
                continue;
            }

            final String[] args = next.split(" ");
            //Ignore args[0] since it is just args[0] == COLOR + "-"

            String enchantmentName = HelperMethods.stringBuilder(args, 1, args.length-1, " ");
            final Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(toEnchantID(enchantmentName)));
            final int level = Integer.parseInt(args[args.length-1]);

            map.put(ench, level);
            iterator.remove();
        }

        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {

            if (itemMeta.hasEnchant(entry.getKey()))
                continue;

            itemMeta.addEnchant(entry.getKey(), entry.getValue(), false);
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        if (event.getPlayer().getOpenInventory().getType() == InventoryType.ENCHANTING) {
            if (event.getItemDrop() != null)
                toEnchant(event.getItemDrop().getItemStack());
        }

    }

    @EventHandler
    public void onDrop(PlayerAttemptPickupItemEvent event) {

        if (event.getPlayer().getOpenInventory().getType() == InventoryType.ENCHANTING) {
            if (event.getItem() != null)
                toLore(event.getItem().getItemStack());
        }

    }

    @EventHandler
    public void onPrepareItem(PrepareItemEnchantEvent event) {

        Player player = event.getEnchanter();
        ItemStack itemStack = event.getItem();

        if (itemStack == null)
            return;

        ItemStack clone = itemStack.clone();
        toEnchant(clone);

        final Map<Enchantment, Integer> enchantments = clone.getEnchantments();

        int level = (int) LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getEnchanting().getLevel();

        final ArrayList<BetterEnchantment> validEnchantments = getValidEnchantment(level);
        filter(validEnchantments, clone.getType());
        removeExclusives(validEnchantments, enchantments.keySet());
        removeHigherLevels(validEnchantments, enchantments);

        final BetterEnchantment[] three = getThree(validEnchantments);
        final EnchantmentOffer[] enchantmentOffers = toEnchantmentOffer(three);

        for (int i = 0; i < event.getOffers().length; i++) {
            if (enchantmentOffers[i] == null) {
                event.getOffers()[i] = null;
                continue;
            }

            event.getOffers()[i] = enchantmentOffers[i];
        }

        SmartEnchantment smartEnchantment = trackMap.get(player.getUniqueId());

        if (smartEnchantment == null) {

            int emeraldCount = 0;

            for (ItemStack iteratedItemStack : player.getInventory().getContents()) {
                if (iteratedItemStack == null)
                    continue;

                if (iteratedItemStack.getType() == Material.EMERALD) {
                    emeraldCount += iteratedItemStack.getAmount();
                }
            }

            smartEnchantment = new SmartEnchantment();
            smartEnchantment.oldXP = player.getLevel();
            player.setLevel(emeraldCount);
        }

        smartEnchantment.offers = enchantmentOffers;


        trackMap.put(player.getUniqueId(), smartEnchantment);

    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {

        if (event.getInventory().getType() == InventoryType.ENCHANTING) {

            final HumanEntity entity = event.getPlayer();
            if (!(entity instanceof Player))
                return;
            final Player player = (Player) entity;


            int emeraldCount = 0;

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack == null)
                    continue;

                if (itemStack.getType() == Material.EMERALD) {
                    emeraldCount += itemStack.getAmount();
                }
            }

            int oldXP = player.getLevel();
            player.setLevel(emeraldCount+100000);

            final SmartEnchantment smartEnchantment = new SmartEnchantment();
            smartEnchantment.oldXP = oldXP;
            trackMap.put(player.getUniqueId(), smartEnchantment);

            event.getInventory().setItem(1, lapis);

            for (ItemStack itemStack : player.getInventory()) {
                if (itemStack == null)
                    continue;
                toLore(itemStack);
            }


        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory() instanceof EnchantingInventory) {
            final HumanEntity entity = event.getPlayer();
            if (!(entity instanceof Player))
                return;
            final Player player = (Player) entity;

            final SmartEnchantment smartEnchantment = trackMap.get(player.getUniqueId());

            if (smartEnchantment != null) {
                player.setLevel(smartEnchantment.oldXP);
                trackMap.remove(player.getUniqueId());
            }

            event.getInventory().setItem(1, null);

            for (ItemStack itemStack : player.getInventory()) {
                if (itemStack == null)
                    continue;

                toEnchant(itemStack);
            }

            if (event.getInventory().getItem(0) != null)
                toEnchant(event.getInventory().getItem(0));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline())
                        player.updateInventory();
                }
            }.runTask(LostShardPlugin.plugin);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        if (event.getCurrentItem() == null)
            return;
        if (event.getCurrentItem().getItemMeta() == null)
            return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null)
            return;
        if (event.getClickedInventory() instanceof EnchantingInventory && event.getCurrentItem().getItemMeta().getDisplayName().equals(lapis.getItemMeta().getDisplayName()) && (event.getSlot() == 1 || event.getRawSlot() == 1)) {
            event.setCancelled(true);
        }
    }

    //WE WILL IMPLEMENT OUR OWN
    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (!event.getView().getType().equals(InventoryType.ENCHANTING))
            return;

        final Player player = event.getEnchanter();

        final PlayerInventory inventory = player.getInventory();

        int emeraldCount = 0;
        final int emeraldCost = event.getExpLevelCost();

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null)
                continue;

            if (itemStack.getType() == Material.EMERALD) {
                emeraldCount += itemStack.getAmount();

                if (emeraldCount > emeraldCost)
                    break;
            }
        }


        if (emeraldCost > emeraldCount) {
            player.sendMessage(ERROR_COLOR + "You don't have enough emeralds (" + emeraldCount + "/" + emeraldCost + ").");
            event.setCancelled(true);
            return;
        }

        final SmartEnchantment smartEnchantment = trackMap.get(player.getUniqueId());

        if (smartEnchantment == null) {
            player.sendMessage(ERROR_COLOR + "There was an error with this selection. Contact staff!");
            event.setCancelled(true);
            return;
        }

        toEnchant(event.getItem());

        //Rework enchant
        event.getEnchantsToAdd().clear();

        event.getEnchantsToAdd().put(smartEnchantment.offers[event.whichButton()].getEnchantment(), smartEnchantment.offers[event.whichButton()].getEnchantmentLevel());

        //Add XP
        Skill enchantingSkill = LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getEnchanting();
        enchantingSkill.addXP(ADDED_XP + (emeraldCost * 2));

        //Remove emerald
        HelperMethods.remove(player.getInventory(), Material.EMERALD, emeraldCost);
        player.setLevel(player.getLevel() + (event.whichButton() + 1));
        //Show ALL
        event.setExpLevelCost(1);
        //To enchant
        event.getInventory().setItem(1, lapis.clone());


        new BukkitRunnable() {
            @Override
            public void run() {
                toLore(event.getItem());
            }
        }.runTask(LostShardPlugin.plugin);

    }

}

