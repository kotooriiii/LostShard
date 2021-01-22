package com.github.kotooriiii.sorcery.spells.type.circle4;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class LightningSpell extends Spell {

    private static HashMap<UUID, Double> lightningSpellCooldownMap = new HashMap<UUID, Double>();

    private LightningSpell() {
        super(SpellType.LIGHTNING, "Strikes down a bolt of lightning in the direction you are facing. Great for burning enemies’ loot.",
                4,
                ChatColor.GOLD,
                new ItemStack[]{new ItemStack(Material.GUNPOWDER, 1), new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1)},
                2.0f,
                20,
                true, true, false);
    }

    private  static LightningSpell instance;
    public static LightningSpell getInstance() {
        if (instance == null) {
            synchronized (LightningSpell.class) {
                if (instance == null)
                    instance = new LightningSpell();
            }
        }
        return instance;
    }


    @Override
    public boolean executeSpell(Player player) {
        final int LIGHTNING_RANGE = 50, RADIUS = 2;
        List<Block> lineOfSightLightning = player.getLineOfSight(null, LIGHTNING_RANGE);

        // Get target block (last block in line of sight)
        Location lightningLocation = lineOfSightLightning.get(lineOfSightLightning.size() - 1).getLocation();
        player.getLocation().getWorld().strikeLightning(lightningLocation);
        callPlayerEvent(player, lightningLocation, RADIUS);

        for (int x = lightningLocation.getBlockX() - RADIUS; x < lightningLocation.getBlockX() + RADIUS; x++)
            for (int z = lightningLocation.getBlockZ() - RADIUS; z < lightningLocation.getBlockZ() + RADIUS; z++)
                if (Math.random() <= 0.90d)
                {
                   Location burnLocation =  new Location(lightningLocation.getWorld(), x, lightningLocation.getBlockY() + 1, z);
                    Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(burnLocation);
                    if(plot != null)
                    {
                        if(plot.getType().isStaff())
                            continue;
                        
                        if(plot instanceof PlayerPlot)
                        {
                            if(!(((PlayerPlot) plot).isJointOwner(player.getUniqueId()) || ((PlayerPlot) plot).isOwner(player.getUniqueId())))
                                continue;

                        }
                    }
                    lightningLocation.getWorld()
                            .getBlockAt(burnLocation)
                            .setType(Material.FIRE);
                }



        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        lightningSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    lightningSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                lightningSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (lightningSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = lightningSpellCooldownMap.get(player.getUniqueId());
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


    private void callPlayerEvent(Player attacker, Location location, int RADIUS) {
        Clan clan = LostShardPlugin.getClanManager().getClan(attacker.getUniqueId());

        for (Entity entity : attacker.getLocation().getWorld().getNearbyEntities(location, RADIUS, RADIUS, RADIUS)) {
            if (!(entity instanceof LivingEntity))
                continue;
            if (attacker.equals(entity)) {
                attacker.damage(3.0f);
                continue;
            }
            if (clan != null && entity instanceof Player && clan.isInThisClan(entity.getUniqueId()) && !clan.isFriendlyFire())
                continue;

            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.damage(3.0f, attacker);
        }
    }
}
