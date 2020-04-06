package com.github.kotooriiii.instaeat;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public enum InstaEat {
    MELON("Melon", Material.MELON, 4, 4, 0,10),
    ROTTEN_FLESH("Rotten Flesh", Material.ROTTEN_FLESH, 5, 5, 0,11),
    SOUP("Soup", Material.MUSHROOM_STEW, 7, 7, 0,15),
    COOKIE("Cookie", Material.COOKIE, 1, 1, 0,7),

    SPLASH_HEALING("Healing", PotionType.INSTANT_HEAL, 0, 0, 0,13);

    private String name;
    private Material material;
    private PotionType potionType;
    private int level;
    private int heal;
    private int hunger;
    private double cooldown;
    private double staminaCost;

    private InstaEat(String name, Material material, int heal, int hunger, double cooldown, double staminaCost) {
        this.name = name;
        this.material = material;
        this.potionType = null;
        this.heal = heal;
        this.hunger = hunger;
        this.cooldown = cooldown;
        this.staminaCost = staminaCost;
    }

    private InstaEat(String name, PotionType potionType, int heal, int hunger, double cooldown, double staminaCost) {
        this(name, Material.SPLASH_POTION, heal, hunger, cooldown, staminaCost);
        this.potionType = potionType;
    }

    public boolean isPotion()
    {
        return potionType != null;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public PotionType getPotionType() {
        return potionType;
    }

    public int getHeal() {
        return heal;
    }

    public int getHunger() {
        return hunger;
    }

    public double getCooldown() {
        return cooldown;
    }

    public double getStaminaCost() {
        return staminaCost;
    }
}