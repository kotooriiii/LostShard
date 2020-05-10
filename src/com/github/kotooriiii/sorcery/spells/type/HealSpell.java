package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class HealSpell extends Spell {

    private static HashMap<UUID, Double> healSpellCooldownMap = new HashMap<UUID, Double>();


    public HealSpell()
    {
        super(SpellType.HEAL,
                ChatColor.GREEN,
                new ItemStack[]{new ItemStack(Material.STRING, 1), new ItemStack(Material.WHEAT_SEEDS, 1)},
                1.0f,
                20);
    }


    @Override
    public boolean executeSpell(Player player) {
        double health = player.getHealth();
        health += 8;
        if (health > player.getMaxHealth())
            health = player.getMaxHealth();
        player.setHealth(health);
        return true;
    }

    @Override
    public void cast(Player player) {

    }

    public boolean isCooldown(Player player) {
        if (healSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = healSpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(0, RoundingMode.UP);
            int value = bd.intValue();
            if (value == 0)
                value = 1;

            String time = "seconds";
            if (value == 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }
}
