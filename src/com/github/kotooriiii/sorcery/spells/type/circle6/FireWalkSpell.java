package com.github.kotooriiii.sorcery.spells.type.circle6;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.google.TutorialSheet;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.EntityType;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class FireWalkSpell extends Spell  implements Listener {

    private static HashMap<UUID, Double> fireWalkCooldownMap = new HashMap<UUID, Double>();
    private static final HashSet<UUID> fireWalkActiveSet= new HashSet<>();

    private final static int DURATION = 10;

    private static FireWalkSpell instance;


    private FireWalkSpell() {
        super(SpellType.FIRE_WALK,
                "Leaves a trail of fire behind you, as well as giving you Speed II for " + DURATION + " seconds. ",
                6,
                ChatColor.RED,
                new ItemStack[]{new ItemStack(Material.GUNPOWDER, 1), new ItemStack(Material.REDSTONE, 1)},
                1d,
                20,
                true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.BLAZE}, 0.01));

    }

    public static FireWalkSpell getInstance() {
        if (instance == null) {
            synchronized (FireWalkSpell.class) {
                if (instance == null)
                    instance = new FireWalkSpell();
            }
        }
        return instance;

    }

    @Override
    public boolean executeSpell(Player player) {

        final UUID uuid = player.getUniqueId();
        getFireWalkActiveSet().add(uuid);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION*20, 2, false, false, false));

        new BukkitRunnable() {
            @Override
            public void run() {

                getFireWalkActiveSet().remove(uuid);

                if(player != null) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 5.0f, 3.0f);
                }
            }
        }.runTaskLater(LostShardPlugin.plugin, DURATION*20);

        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        fireWalkCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    fireWalkCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                fireWalkCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (fireWalkCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = fireWalkCooldownMap.get(player.getUniqueId());
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

        if(!fireWalkActiveSet.contains(event.getPlayer().getUniqueId()))
            return;
        if (!Spell.isLapisNearby(event.getTo(), Spell.getDefaultLapisNearbyValue()))
            return;

        fireWalkActiveSet.remove(event.getPlayer().getUniqueId());

        event.getPlayer().sendMessage(ERROR_COLOR + "Something doesn't seem to let you leave a trail of fire anymore...");
    }


    public static HashSet<UUID> getFireWalkActiveSet() {
        return fireWalkActiveSet;
    }
}
