package com.github.kotooriiii.sorcery.spells.type.circle7;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class CleanseSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> cleanseSpellCooldownMap = new HashMap<>();

    private CleanseSpell() {
        super(SpellType.CLEANSE,
                "Clears you of all potion effects, good and bad.",
                7, ChatColor.AQUA, new ItemStack[]{new ItemStack(Material.SUGAR_CANE, 1), new ItemStack(Material.REDSTONE, 1)}, 1.0f, 25, true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.WITCH}, 0.04));

    }

    private  static CleanseSpell instance;
    public static CleanseSpell getInstance() {
        if (instance == null) {
            synchronized (CleanseSpell.class) {
                if (instance == null)
                    instance = new CleanseSpell();
            }
        }
        return instance;
    }


    @Override
    public void updateCooldown(Player player) {
        cleanseSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    cleanseSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                cleanseSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (cleanseSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = cleanseSpellCooldownMap.get(player.getUniqueId());
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

    @Override
    public boolean executeSpell(Player player) {

        player.setGlowing(false);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE,  7.0f, 6f);
        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation(), 10, 2, 2, 2);
        for(PotionEffect effect:player.getActivePotionEffects()) player.removePotionEffect(effect.getType());

        return true;
    }
}
