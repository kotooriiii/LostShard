package com.github.kotooriiii.sorcery.spells.drops;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.wands.Glow;
import net.citizensnpcs.npc.ai.speech.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpellDropScroll {

    private final static String OWNER_ID = ChatColor.WHITE + "Owner ID: ";
    private final static String SPELL_ID = ChatColor.WHITE + "Spell ID: ";

    private SpellDropScroll()
    {

    }

    public static ItemStack getScrollPaper(Player player, Spell spell)
    {
        ItemStack itemStack = new ItemStack(Material.PAPER, 1);


        ItemMeta itemMeta = itemStack.getItemMeta();


        // Add stick enchantment
        Glow glow = new Glow(new NamespacedKey(LostShardPlugin.plugin, Glow.NAME));
        itemMeta.addEnchant(glow, 1, true);

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "Spell of " + spell.getName());
        lore.add(ChatColor.DARK_PURPLE + "Right-click to add to your spellbook!");
        lore.add(" ");
        lore.add(OWNER_ID + player.getUniqueId());
        lore.add(SPELL_ID + spell.getName());

        itemMeta.setLore(lore);
        itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Spell of " + spell.getName());

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static Spell getScrollSpell(Player player, ItemStack itemStack)
    {
        if(itemStack == null || itemStack.getType() != Material.PAPER)
            return null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if(itemMeta == null || lore == null || lore.isEmpty() || lore.size() != 5)
            return null;

        String spellName = lore.get(lore.size() - 1).substring(SPELL_ID.length());
        String ownerUUID = lore.get(lore.size()-2).substring(OWNER_ID.length());

        SpellType type = SpellType.matchSpellType(spellName);
        UUID uuid = UUID.fromString(ownerUUID);

        if(type == null || uuid == null || !player.getUniqueId().equals(uuid))
            return null;

        Spell spell = Spell.of(type);
        return spell;
    }
}
