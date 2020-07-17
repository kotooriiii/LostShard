package com.github.kotooriiii.sorcery.wands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Wand {

    private Spell spell;

    public Wand(Spell spell) {
        // Set local variables to the given variables
        this.spell = spell;
    }

    public ItemStack createItem() {

        // Create stick
        ItemStack wandItem = new ItemStack(Material.STICK, 1);

        // Get sticks meta data
        ItemMeta wandMeta = wandItem.getItemMeta();

        // Set stick name based on input
        wandMeta.setDisplayName(spell.getColor() + spell.getName() + " Wand");

        // Add stick enchantment
        Glow glow = new Glow(new NamespacedKey(LostShardPlugin.plugin, "GlowCustomEnchant"));
        wandMeta.addEnchant(glow, 1, true);

        // Set stick lore
        List<String> lore = new ArrayList<>();
        lore.add(spell.getColor() + "Left click to use this wand.");
        lore.add(spell.getColor() + "The wand has a cooldown of " + spell.getCooldown() + " seconds.");
        lore.add(spell.getColor() + "The wand has a mana cost of " + spell.getManaCost() + " mana.");
        lore.add("ID:" + spell.getName());
        wandMeta.setLore(lore);
        wandItem.setItemMeta(wandMeta);
        // Return the finished want item
        return wandItem;
    }

    public static boolean isWielding(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty())
            return false;
        if (!itemStack.getType().equals(Material.STICK))
            return false;

        for (SpellType type : SpellType.values()) {
            String lastLine = itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1);
            if (lastLine.equals("ID:" + type.getName())) {
                if (Spell.of(type).isWandable())
                    return true;
            }
        }
        return false;
    }

    public static SpellType getWielding(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty())
            return null;
        if (!itemStack.getType().equals(Material.STICK))
            return null;
        for (SpellType type : SpellType.values()) {
            String lastLine = itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1);
            if (lastLine.equals("ID:" + type.getName()))
                if (Spell.of(type).isWandable())
                    return type;
        }
        return null;
    }

    public void cast(Player player) {
        spell.cast(player);
    }

    public Spell getSpell() {
        return spell;
    }
}
