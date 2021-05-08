package com.github.kotooriiii.enderdragon.strategy.spawn;

import com.github.kotooriiii.enderdragon.EnderDragonManager;
import com.github.kotooriiii.enderdragon.entity.LSEnderDragon;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;

import javax.annotation.Nullable;

public abstract class SpawnStrategy {

    protected EnderDragonManager manager;
    protected LSEnderDragon enderDragon;

    private boolean isLocked;

    public SpawnStrategy(EnderDragonManager enderDragonManager) {
        this.manager = enderDragonManager;
        enderDragon = null;
        isLocked = false;
    }

    public final void spawnDragonSafely() {
        if (!manager.getEnderDragonSpawnLocation().getWorld().getPlayers().isEmpty()) {
            spawnDragon();
        }
    }

    private synchronized void spawnDragon() {

        if (isLocked)
            return;
        isLocked = true;
        this.manager.getEnderDragonSpawnLocation().getChunk().setForceLoaded(true);

        final TextComponent textComponent = new TextComponent("The Ender Dragon is in the process of being summoned again...");
        textComponent.setColor(ChatColor.RED);
        Bukkit.broadcast(textComponent);

        spawn();

    }

    protected abstract void spawn();

    protected final void spawnComplete() {
        final EnderDragon bukkitDragon = (EnderDragon) enderDragon.getBukkitEntity();
        bukkitDragon.getDragonBattle().generateEndPortal(false);
        this.manager.getCooldownStrategy().setAlive(true);
        this.manager.getCooldownStrategy().setIllusivelyAlive(false);

        this.manager.getBossBar().setProgress(1);
        this.manager.getBossBar().setVisible(true);

        isLocked = false;

    }


    @Nullable
    public LSEnderDragon getLSEnderDragon() {
        return enderDragon;
    }

    public void setLSEnderDragon(@Nullable LSEnderDragon enderDragon) {
        this.enderDragon = enderDragon;
    }
}
