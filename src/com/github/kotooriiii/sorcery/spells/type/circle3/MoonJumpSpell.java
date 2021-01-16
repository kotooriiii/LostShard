package com.github.kotooriiii.sorcery.spells.type.circle3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.sorcery.events.MarkCreateEvent;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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

public class MoonJumpSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> moonJumpSpell = new HashMap<>();
    private final static int DURATION = 10;


    public MoonJumpSpell() {
        super(SpellType.MOON_JUMP,
                "Makes you jump really high each jump for " + DURATION + " seconds! No fall damage, just high jumping. Very useful if you are in a trap!",
                3, ChatColor.WHITE, new ItemStack[]{new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1)}, 2.0f, 20, true, true, false);
    }


    @Override
    public void updateCooldown(Player player) {
        moonJumpSpell.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    moonJumpSpell.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                moonJumpSpell.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (moonJumpSpell.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = moonJumpSpell.get(player.getUniqueId());
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


        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*DURATION, 16, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20*DURATION, 1, false, false, false));

        return true;
    }
}
