package com.github.kotooriiii.sorcery.scrolls;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.wands.Glow;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Scroll {
    private Spell spell;
    public final static Material scrollMaterial = Material.GLOBE_BANNER_PATTERN;

    public Scroll(Spell spell) {
        // Set local variables to the given variables
        this.spell = spell;
    }

    public ItemStack createItem() {

        // Create stick
        ItemStack scrollItem = new ItemStack(scrollMaterial, 1);

        // Get sticks meta data
        ItemMeta scrollMeta = scrollItem.getItemMeta();

        // Set stick name based on input
        scrollMeta.setDisplayName(spell.getColor() + spell.getName() + " Scroll");

        // Add stick enchantment
        Glow glow = new Glow(new NamespacedKey(LostShardPlugin.plugin, Glow.NAME));
        scrollMeta.addEnchant(glow, 1, true);

        // Set stick lore
        List<String> lore = new ArrayList<>();
        lore.add(spell.getColor() + "Right click to use this spell.");
        lore.add(spell.getColor() + "The scroll has a cooldown of " + spell.getCooldown() + " seconds.");
        lore.add(spell.getColor() + "The scroll has a mana cost of " + spell.getManaCost() + " mana.");
        lore.add("ID:" + spell.getName());
        scrollMeta.setLore(lore);
        scrollItem.setItemMeta(scrollMeta);
        // Return the finished want item
        return scrollItem;
    }

    public static boolean isWielding(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty())
            return false;
        if (!itemStack.getType().equals(scrollMaterial))
            return false;

        for (SpellType type : SpellType.values()) {
            String lastLine = itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1);
            if (lastLine.equals("ID:" + type.getName()))
                if (Spell.of(type) != null)
                    if (Spell.of(type).isScrollable())
                        return true;
        }
        return false;
    }

    public static boolean isScroll(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty())
            return false;
        if (!itemStack.getType().equals(scrollMaterial))
            return false;

        for (SpellType type : SpellType.values()) {
            String lastLine = itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1);
            if (lastLine.equals("ID:" + type.getName()))
                if (Spell.of(type) != null)
                    if (Spell.of(type).isScrollable())
                        return true;
        }
        return false;
    }

    public static Scroll getWielding(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty())
            return null;
        if (!itemStack.getType().equals(scrollMaterial))
            return null;
        for (SpellType type : SpellType.values()) {
            String lastLine = itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1);
            if (lastLine.equals("ID:" + type.getName()))
                if (Spell.of(type) != null)
                    if (Spell.of(type).isScrollable())
                        return new Scroll(Spell.of(type));
        }
        return null;
    }

    public boolean cast(Player player) {
       return spell.cast(player);
    }

    public Spell getSpell() {
        return spell;
    }
}
