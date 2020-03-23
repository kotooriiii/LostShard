package com.github.kotooriiii.wands;

import com.github.kotooriiii.status.Status;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum WandType {
    FIREBALL("Fireball", ChatColor.RED, new ItemStack[]{new ItemStack(Material.GUNPOWDER, 1), new ItemStack(Material.REDSTONE, 1)}, 2, 15),
    HEALING("Healing",ChatColor.GREEN,new ItemStack[]{new ItemStack(Material.STRING, 1), new ItemStack(Material.WHEAT_SEEDS, 1)},1,20),
    ICE("Ice",ChatColor.AQUA,new ItemStack[]{new ItemStack(Material.STRING, 1)},2,15),
    LIGHTNING("Lightning",ChatColor.GOLD,new ItemStack[]{new ItemStack(Material.GUNPOWDER, 1), new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1)},2,20),
    TELEPORT("Teleport",ChatColor.DARK_PURPLE,new ItemStack[]{new ItemStack(Material.FEATHER, 1)},1,15),
    WEB("Web",ChatColor.DARK_GRAY,new ItemStack[]{new ItemStack(Material.FEATHER, 1)},1,15);


    private String name;
    private ChatColor chatColor;
    private ItemStack[] ingredients;
    private double cooldown;
    private int manaCost;

    private WandType (String name, ChatColor chatColor, ItemStack[] ingredients, double cooldown, int manaCost)
    {
        this.name = name;
        this.chatColor = chatColor;
        this.ingredients = ingredients;
        this.cooldown = cooldown;
        this.manaCost = manaCost;
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public double getCooldown() {
        return cooldown;
    }

    public int getManaCost() {
        return manaCost;
    }

    public ItemStack[] getIngredients() {
        return ingredients;
    }

    public static WandType matchWandType(String name)
    {
        for(WandType type : WandType.values())
        {
            if(type.getName().equals(name))
            {
                return type;
            }
        }
        return null;
    }
}
