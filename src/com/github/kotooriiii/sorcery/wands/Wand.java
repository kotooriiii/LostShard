package com.github.kotooriiii.sorcery.wands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.HostilityMatch;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.skills.listeners.BrawlingListener;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import com.google.common.base.Function;
import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.activeHostilityGames;
import static com.github.kotooriiii.util.HelperMethods.localBroadcast;

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
        if(!itemStack.getType().equals(Material.STICK))
            return false;

        for (SpellType type : SpellType.values()) {
            String lastLine = itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1);
            if (lastLine.equals("ID:" + type.getName()))
                return true;
        }
        return false;
    }

    public static SpellType  getWielding(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty())
            return null;
        if(!itemStack.getType().equals(Material.STICK))
            return null;
        for (SpellType type : SpellType.values()) {
            String lastLine = itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1);
            if (lastLine.equals("ID:" + type.getName()))
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
