package com.github.kotooriiii.enderdragon;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.enderdragon.strategy.cooldown.CooldownStrategy;
import com.github.kotooriiii.enderdragon.strategy.cooldown.FixedCooldownStrategy;
import com.github.kotooriiii.enderdragon.strategy.fight.DefaultFightStrategy;
import com.github.kotooriiii.enderdragon.strategy.fight.FightStrategy;
import com.github.kotooriiii.enderdragon.strategy.spawn.DefaultSpawnStrategy;
import com.github.kotooriiii.enderdragon.strategy.spawn.SpawnStrategy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class EnderDragonManager {

    /**
     * Manages the cooldown strategy the ender dragon should go.
     */
    private CooldownStrategy cooldownStrategy;

    /**
     * Manages the spawn strategy
     */
    private SpawnStrategy spawnStrategy;

    /**
     * Manages the fight strategy
     */
    private FightStrategy fightStrategy;

    /**
     * Ender dragon boss bar
     */
    private BossBar bossBar;

    /**
     * Constant location of ender dragon's spawn
     */
    private static final Location enderDragonSpawnLocation = new Location(LostShardPlugin.getEndWorld(), 0, 130, 0);


    public EnderDragonManager() {
        this.cooldownStrategy = new FixedCooldownStrategy(this);
        this.spawnStrategy = new DefaultSpawnStrategy(this);
        this.fightStrategy = new DefaultFightStrategy(this);
        this.bossBar = Bukkit.createBossBar(ChatColor.DARK_PURPLE + "Nickolov's Ender Dragon", BarColor.PURPLE, BarStyle.SEGMENTED_20, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY);
        this.bossBar.setVisible(false);
        start();
    }

    public void start() {
        if (cooldownStrategy.isCooldown())
            return;
        if (cooldownStrategy.isAlive())
            return;
        cooldownStrategy.setIllusivelyAlive(true);
        spawnStrategy.spawnDragonSafely();
    }

    public Location getEnderDragonSpawnLocation() {
        return enderDragonSpawnLocation;
    }

    public CooldownStrategy getCooldownStrategy() {
        return cooldownStrategy;
    }

    public void setCooldownStrategy(CooldownStrategy cooldownStrategy) {
        this.cooldownStrategy = cooldownStrategy;
    }

    public SpawnStrategy getSpawnStrategy() {
        return spawnStrategy;
    }

    public void setSpawnStrategy(SpawnStrategy spawnStrategy) {
        this.spawnStrategy = spawnStrategy;
    }

    public FightStrategy getFightStrategy() {
        return fightStrategy;
    }

    public void setFightStrategy(FightStrategy fightStrategy) {
        this.fightStrategy = fightStrategy;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    private void poofParticle(Location location) {

        final int times = 50;
        final int[] counter = {0};
        new BukkitRunnable() {
            @Override
            public void run() {

                if (counter[0]++ >= times) {
                    this.cancel();
                    return;
                }

                location.getWorld().spawnParticle(Particle.ASH, location, 5, 3, 3, 3);
                location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 5, 3, 3, 3);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 20, 5);

    }

    public void kill() {
        if (getSpawnStrategy().getLSEnderDragon() != null) {
            poofParticle(getSpawnStrategy().getLSEnderDragon().getBukkitEntity().getLocation());
            getSpawnStrategy().getLSEnderDragon().getBukkitEntity().remove();
        }


        for (Entity entity : LostShardPlugin.getEndWorld().getEntities()) {
            if (entity.getType() == EntityType.ENDER_DRAGON) {
                poofParticle(entity.getLocation());
                entity.remove();
            }
        }

    }
}
