package com.github.kotooriiii.sorcery.spells.type.circle8;

import com.comphenix.net.sf.cglib.asm.$ByteVector;
import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.xezard.glow.data.glow.Glow;
import ru.xezard.glow.data.glow.IGlow;
import ru.xezard.glow.data.glow.manager.GlowsManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PerceptionSpell extends Spell implements Listener {

    //Cooldown map
    private static HashMap<UUID, Double> perceptionCooldownMap = new HashMap<UUID, Double>();

    //Constants
    private final static int PERCEPTION_DISTANCE = 30, PERCEPTION_DURATION = 10;


    private PerceptionSpell() {
        super(SpellType.PERCEPTION,
                "Highlights all players within a " + PERCEPTION_DISTANCE + " block radius of you for " + PERCEPTION_DURATION +" seconds.",
                8,
                ChatColor.BLUE,
                new ItemStack[]{new ItemStack(Material.ENDER_EYE, 1), new ItemStack(Material.REDSTONE, 1)},
                2.0d,
                20,
                true, true, false);


    }


    private  static PerceptionSpell instance;
    public static PerceptionSpell getInstance() {
        if (instance == null) {
            synchronized (PerceptionSpell.class) {
                if (instance == null)
                    instance = new PerceptionSpell();
            }
        }
        return instance;
    }


    @Override
    public boolean executeSpell(Player player) {

        Glow glow = Glow.builder()
                .animatedColor(this.getColor())
                .name(this.getName())
                .build();

        for(Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), PERCEPTION_DISTANCE, PERCEPTION_DISTANCE, PERCEPTION_DISTANCE))
        {
            if(entity.getType() != EntityType.PLAYER)
                continue;
            if(CitizensAPI.getNPCRegistry().isNPC(entity))
                continue;
            glow.addHolders(entity);
        }
        glow.display(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                glow.destroy();
                if(player.isOnline())
                ShardScoreboardManager.registerScoreboard(player);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20*PERCEPTION_DURATION);


        return true;
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

        if (!Spell.isLapisNearby(event.getTo(), Spell.getDefaultLapisNearbyValue()))
            return;
        boolean exists =false;
        for (IGlow glow : GlowsManager.getInstance().getGlows())
        {
            if(glow.getViewers().contains(event.getPlayer()))
            {
                glow.hideFrom(event.getPlayer());
                exists=true;
            }
        }

        if(!exists)
            return;

        event.getPlayer().sendMessage(ERROR_COLOR + "Something doesn't seem to help you see anymore...");
    }


    @Override
    public void updateCooldown(Player player) {
        perceptionCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    perceptionCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                perceptionCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (perceptionCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = perceptionCooldownMap.get(player.getUniqueId());
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
