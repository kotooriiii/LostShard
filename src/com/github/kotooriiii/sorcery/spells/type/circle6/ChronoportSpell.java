package com.github.kotooriiii.sorcery.spells.type.circle6;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.type.circle5.WebFieldSpell;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class ChronoportSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> chronoportSpellCooldownMap = new HashMap<>();

    private final static HashMap<UUID, Location> chronoportMap = new HashMap<>();

    private final static int TIMING = 5;


    private ChronoportSpell() {
        super(SpellType.CHRONOPORT,"Functions as a rubberband that teleports you right back to where you casted it. After " + TIMING + " seconds, the place that you casted chronoport is where you will return. ", 6,  ChatColor.DARK_AQUA, new ItemStack[]{new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.LAPIS_LAZULI, 1), new ItemStack(Material.STRING, 1)}, 10.0f, 20, true, true, false);
    }

    private  static ChronoportSpell instance;
    public static ChronoportSpell getInstance() {
        if (instance == null) {
            synchronized (ChronoportSpell.class) {
                if (instance == null)
                    instance = new ChronoportSpell();
            }
        }
        return instance;
    }

    @Override
    public void updateCooldown(Player player) {
        chronoportSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    chronoportSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                chronoportSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    @Override
    public boolean isCooldown(Player player) {
        if (chronoportSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = chronoportSpellCooldownMap.get(player.getUniqueId());
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
        chronoport(player);
        return true;
    }


    /**
     *
     */
    private void chronoport(Player player) {

        chronoportMap.put(player.getUniqueId(), player.getLocation());
        new BukkitRunnable() {
            @Override
            public void run() {

                Location location = chronoportMap.get(player.getUniqueId());
                if (location == null || !player.isOnline() || player.isDead()) {
                    this.cancel();
                    return;
                }

                if(isLapisNearby(location, DEFAULT_LAPIS_NEARBY))
                {
                    player.sendMessage(ERROR_COLOR + "You can not seem to cast " + getName() + " there...");
                    refund(player);
                    return;
                }

                player.teleport(location);

            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * TIMING);
    }


}
