package com.github.kotooriiii.sorcery.spells.type.circle9;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class LustSpell extends Spell {

    private final static HashMap<UUID, Double> lustSpellCooldownMap = new HashMap<>();

    private LustSpell() {
        super(SpellType.LUST,
                "Replenishes every one of your clan member’s stamina and hearts to full.",
                9, ChatColor.DARK_PURPLE, new ItemStack[]{new ItemStack(Material.DRAGON_EGG, 1), new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1)}, 2.0f, 85, true, true, false);
    }

    private  static LustSpell instance;
    public static LustSpell getInstance() {
        if (instance == null) {
            synchronized (LustSpell.class) {
                if (instance == null)
                    instance = new LustSpell();
            }
        }
        return instance;
    }


    @Override
    public void updateCooldown(Player player) {
        lustSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    lustSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                lustSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (lustSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = lustSpellCooldownMap.get(player.getUniqueId());
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

        Clan clan = LostShardPlugin.getClanManager().getClan(player.getUniqueId());
        if(clan == null)
        {
            player.sendMessage(ERROR_COLOR + "You are not in a clan.");
        }

        Player[] onlinePlayers = clan.getOnlinePlayers();

        if(onlinePlayers.length == 0)
        {
            player.sendMessage(ERROR_COLOR + "No clan members are online.");
        }

        for(Player iplayer : onlinePlayers )
        {
            iplayer.setHealth(iplayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            iplayer.setFoodLevel(20);
            iplayer.getWorld().spawnParticle(Particle.HEART, iplayer.getLocation(), 3, 1,1,1);
            iplayer.getWorld().playSound(iplayer.getLocation(), Sound.ENTITY_CAT_PURREOW, 8.0f, 2.0f);
            Stat wrap = Stat.wrap(iplayer);
            wrap.setStamina(wrap.getMaxStamina());
        }

        return true;
    }
}
