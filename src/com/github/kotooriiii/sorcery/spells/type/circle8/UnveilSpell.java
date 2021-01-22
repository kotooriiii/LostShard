package com.github.kotooriiii.sorcery.spells.type.circle8;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class UnveilSpell extends Spell  {

    //Cooldown map
    private static HashMap<UUID, Double> unveilCooldownMap = new HashMap<UUID, Double>();

    //Constants
    private final static int UNVEIL_DISTANCE = 25;


    private UnveilSpell() {
        super(SpellType.UNVEIL,
                "Strips everyone around you of their invisibility potion effects. Basically, whoever was hidden is not anymore." +
                        "you casted it. It is very useful in PvP, or just general gameplay.",
                8,
                ChatColor.LIGHT_PURPLE,
                new ItemStack[]{new ItemStack(Material.FERMENTED_SPIDER_EYE, 1), new ItemStack(Material.GOLDEN_CARROT, 1)},
                2.0d,
                20,
                true, true, false);


    }


    //todo switch to ur name
    private  static UnveilSpell instance;
    public static UnveilSpell getInstance() {
        if (instance == null) {
            synchronized (UnveilSpell.class) {
                if (instance == null)
                    instance = new UnveilSpell();
            }
        }
        return instance;
    }


    @Override
    public boolean executeSpell(Player player) {

        for(Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), UNVEIL_DISTANCE, UNVEIL_DISTANCE, UNVEIL_DISTANCE))
        {
            if(entity.getType() != EntityType.PLAYER)
                continue;
            if(CitizensAPI.getNPCRegistry().isNPC(entity))
                continue;
            if(entity == player)
                continue;

            Player iplayer = (Player) entity;

            if(!iplayer.hasPotionEffect(PotionEffectType.INVISIBILITY))
                continue;

            iplayer.removePotionEffect(PotionEffectType.INVISIBILITY);
            iplayer.getWorld().playSound(iplayer.getLocation(), Sound.BLOCK_GLASS_BREAK, 10.0f, 1.0f);
            iplayer.getWorld().spawnParticle(Particle.SQUID_INK, iplayer.getLocation(), 50, 1.5f, 1.5f ,1.5f);
        }
        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        unveilCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    unveilCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                unveilCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (unveilCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = unveilCooldownMap.get(player.getUniqueId());
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
