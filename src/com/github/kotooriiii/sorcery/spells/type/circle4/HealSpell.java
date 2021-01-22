package com.github.kotooriiii.sorcery.spells.type.circle4;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.type.circle3.MoonJumpSpell;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class HealSpell extends Spell {

    private static HashMap<UUID, Double> healSpellCooldownMap = new HashMap<UUID, Double>();
    private final static int HALF_HEARTS_HEALED = 8;


    private HealSpell()
    {
        super(SpellType.HEAL,
                "Heals you for " + new BigDecimal(HALF_HEARTS_HEALED/2).setScale(1,RoundingMode.UNNECESSARY) + " hearts.",
                4,
            ChatColor.GREEN,
                new ItemStack[]{new ItemStack(Material.STRING, 1), new ItemStack(Material.WHEAT_SEEDS, 1)},
                0.5d,
                20,
                true, true, false);
    }


    private  static HealSpell instance;
    public static HealSpell getInstance() {
        if (instance == null) {
            synchronized (HealSpell.class) {
                if (instance == null)
                    instance = new HealSpell();
            }
        }
        return instance;
    }


    @Override
    public boolean executeSpell(Player player) {
        double health = player.getHealth();
        health += HALF_HEARTS_HEALED;
        if (health > player.getMaxHealth())
            health = player.getMaxHealth();
        player.setHealth(health);
        return true;
    }

    @Override
    public void updateCooldown(Player player)
    {
        healSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    healSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                healSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (healSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = healSpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(1, RoundingMode.HALF_UP);
            float value = bd.floatValue();
            if (value == 0)
                value = 0.1f;

            String time = "seconds";
            if (value <= 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }
}
