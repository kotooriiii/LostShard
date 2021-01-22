package com.github.kotooriiii.sorcery.spells.type.circle5;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class RespirateSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> respirateSpellCooldownMap = new HashMap<>();
    private final static int DURATION = 15;
    private HashSet<UUID> respirateSet = new HashSet<>();


    private RespirateSpell() {
        super(SpellType.RESPIRATE,
                "Breath underwater without taking damage for " + DURATION + " seconds.",
                5, ChatColor.BLUE, new ItemStack[]{new ItemStack(Material.KELP, 1), new ItemStack(Material.REDSTONE, 1)}, 1.0f, 25, true, true, false);
    }

    private  static RespirateSpell instance;
    public static RespirateSpell getInstance() {
        if (instance == null) {
            synchronized (RespirateSpell.class) {
                if (instance == null)
                    instance = new RespirateSpell();
            }
        }
        return instance;
    }
    @Override
    public void updateCooldown(Player player) {
        respirateSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    respirateSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                respirateSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (respirateSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = respirateSpellCooldownMap.get(player.getUniqueId());
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


        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20*DURATION, 16, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20*DURATION, 5, false, false, false));
        respirateSet.add(player.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                respirateSet.remove(player.getUniqueId());
            }
        }.runTaskLater(LostShardPlugin.plugin, 20*DURATION);

        return true;
    }

    public HashSet<UUID> getRespirators()
    {
        return respirateSet;
    }


    @EventHandler
    public void onMovePerceptionListener(PlayerMoveEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        final int x_initial, y_initial, z_initial,
                x_final, y_final, z_final;

        x_initial = event.getFrom().getBlockX();
        y_initial = event.getFrom().getBlockY();
        z_initial = event.getFrom().getBlockZ();

        x_final = event.getTo().getBlockX();
        y_final = event.getTo().getBlockY();
        z_final = event.getTo().getBlockZ();

        if (x_initial == x_final && y_initial == y_final && z_initial == z_final)
            return;

        if(!getRespirators().contains(event.getPlayer().getUniqueId()))
            return;
        if (!Spell.isLapisNearby(event.getTo(), Spell.getDefaultLapisNearbyValue()))
            return;

        getRespirators().remove(event.getPlayer().getUniqueId());

        event.getPlayer().sendMessage(ERROR_COLOR + "Something doesn't seem to let you breathe underwater anymore...");
    }
}
