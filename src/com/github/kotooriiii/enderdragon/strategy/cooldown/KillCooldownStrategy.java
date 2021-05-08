package com.github.kotooriiii.enderdragon.strategy.cooldown;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.enderdragon.EnderDragonManager;
import org.bukkit.Bukkit;

import java.time.ZonedDateTime;

public class KillCooldownStrategy extends CooldownStrategy {
    public KillCooldownStrategy(EnderDragonManager manager) {
        super(manager);
    }

    @Override
    protected void applyCooldown() {

        final ZonedDateTime now = ZonedDateTime.now(LostShardPlugin.getZoneID());

        int hrs = Bukkit.getOnlinePlayers().size() < 20 ? 12 : 6;

        final ZonedDateTime plus = now.plusHours(hrs);

        this.nextSummonDate = plus;
    }
}
