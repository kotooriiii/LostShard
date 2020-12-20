package com.github.kotooriiii.instaeat;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum InstaEatType {
    MELON("Melon", Material.MELON_SLICE, 4, 4, 0, 10),
    ROTTEN_FLESH("Rotten Flesh", Material.ROTTEN_FLESH, 5, 5, 0, 13),
    SOUP("Soup", Material.MUSHROOM_STEW, 8, 7, 0, 15),
    COOKIE("Cookie", Material.COOKIE, 1, 1, 0, 7),

    SPLASH_POTION("Splash Potion", Material.SPLASH_POTION, 0, 0, 0, 13);

    private String name;
    private Material material;
    private int heal;
    private int foodLevel;
    private double cooldown;
    private double staminaCost;

    private InstaEatType(String name, Material material, int heal, int foodLevel, double cooldown, double staminaCost) {
        this.name = name;
        this.material = material;
        this.heal = heal;
        this.foodLevel = foodLevel;
        this.cooldown = cooldown;
        this.staminaCost = staminaCost;
    }

    public String getName() {
        return name;
    }

    public boolean isSplashPotion()
    {
        return material.equals(Material.SPLASH_POTION);
    }

    public boolean isHealOrRegen(ItemStack itemStack)
    {
        if(itemStack.getItemMeta() instanceof PotionMeta)
        {
            for(PotionEffect effect : ((PotionMeta) itemStack.getItemMeta()).getCustomEffects())
            {
                if(effect.getType() == PotionEffectType.REGENERATION || effect.getType() == PotionEffectType.HEAL || effect.getType() == PotionEffectType.HEALTH_BOOST)
                    return true;
            }
        }
        return false;
    }

    public Material getMaterial() {
        return material;
    }


    public int getHeal() {
        return heal;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public double getCooldown() {
        return cooldown;
    }

    public double getStaminaCost() {
        return staminaCost;
    }

    public static boolean isCarryingInstaEat(Player player) {
        PlayerInventory inventory = player.getInventory();
        final ItemStack item = inventory.getItemInMainHand();

        if (item == null)
            return false;
        for (InstaEatType instaEatType : InstaEatType.values()) {
            if (item.getType().equals(instaEatType.getMaterial())) {
                return true;
            }
        }
        return false;
    }

    public static InstaEatType getCarryingInstaEat(Player player) {
        PlayerInventory inventory = player.getInventory();
        final ItemStack item = inventory.getItemInMainHand();

        if (item == null)
            return null;
        for (InstaEatType instaEatType : InstaEatType.values()) {
            if (item.getType().equals(instaEatType.getMaterial())) {
                return instaEatType;
            }
        }
        return null;
    }
}