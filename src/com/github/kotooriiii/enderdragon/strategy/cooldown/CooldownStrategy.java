package com.github.kotooriiii.enderdragon.strategy.cooldown;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.enderdragon.EnderDragonManager;
import com.github.kotooriiii.util.HelperMethods;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.UUID;

public abstract class CooldownStrategy {

    /**
     * The date the dragon will be summoned in.
     */
    protected ZonedDateTime nextSummonDate;

    /**
     * Whether the dragon is in cooldown (is killed and waiting to be respawned)
     */
    private boolean isCooldown;

    /**
     * Whether the dragon is alive;
     */
    private boolean isAlive;

    /**
     * Whther the dragon is illusively spawned
     */
    private boolean isIllusivelyAlive;

    /**
     * The last player's UUID who killed the dragon
     */
    private UUID lastKillerUUID = null;
    /**
     * The last date the dragon was killed.
     */
    private ZonedDateTime killDate = null;

    /**
     * The parent manager
     */
    protected EnderDragonManager manager;

    public CooldownStrategy(EnderDragonManager manager) {
        nextSummonDate = null;
        isCooldown = false;
        isAlive = false;
        lastKillerUUID = null;
        isIllusivelyAlive = false;
        killDate = null;

        this.manager = manager;
    }

    public final void killedDragon(@Nullable UUID uuid)
    {
        this.lastKillerUUID = uuid;
        this.isAlive = false;
        this.killDate = ZonedDateTime.now(LostShardPlugin.getZoneID());
        this.isCooldown = true;

        manager.getFightStrategy().end();

        //Apply cooldown takes care of isAlive
        applyCooldown();
        startTimer();
    }

    private void startTimer()
    {

        final TextComponent textComponent = new TextComponent("The Ender Dragon will be summoned in: " + ChatColor.YELLOW + HelperMethods.getTimeLeft(getNextSummonDate()) + ChatColor.RED  + ".");
        textComponent.setColor(ChatColor.RED);
        Bukkit.broadcast(textComponent);

        long nextSeconds = nextSummonDate.toEpochSecond();
        long nowSeconds = ZonedDateTime.now(LostShardPlugin.getZoneID()).toEpochSecond();

        long diffSeconds = nextSeconds-nowSeconds;

        new BukkitRunnable() {
            @Override
            public void run() {
                isCooldown = false;
                isIllusivelyAlive = true;
                final TextComponent textComponent = new TextComponent("The Ender Dragon has arrived to The End...");
                textComponent.setColor(ChatColor.RED);
                Bukkit.broadcast(textComponent);
                manager.getSpawnStrategy().spawnDragonSafely();
            }
        }.runTaskLater(LostShardPlugin.plugin, diffSeconds*20);
    }

    protected abstract void applyCooldown();

    public final boolean isCooldown() {
        return isCooldown;
    }

    @Nullable
    public final ZonedDateTime getNextSummonDate() {
        return nextSummonDate;
    }

    @Nullable
    public final UUID getLastKillerUUID() {
        return lastKillerUUID;
    }

    @Nullable
    public final ZonedDateTime getKillDate() {
        return killDate;
    }

    @Nullable
    public final boolean isAlive()
    {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public void setIllusivelyAlive(boolean b) {
        this.isIllusivelyAlive = b;
    }

    public boolean isIllusivelyAlive() {
        return isIllusivelyAlive;
    }
}
