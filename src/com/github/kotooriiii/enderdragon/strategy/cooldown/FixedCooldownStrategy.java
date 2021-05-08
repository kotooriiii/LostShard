package com.github.kotooriiii.enderdragon.strategy.cooldown;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.enderdragon.EnderDragonManager;

import java.time.ZonedDateTime;

public class FixedCooldownStrategy extends CooldownStrategy {

    public FixedCooldownStrategy(EnderDragonManager manager) {
        super(manager);
    }

    @Override
    protected void applyCooldown() {

        final ZonedDateTime now = ZonedDateTime.now(LostShardPlugin.getZoneID());

        ZonedDateTime nextRun= now.withHour(1).withMinute(0).withSecond(0).withNano(0);

        if (now.compareTo(nextRun) >= 0)
        {
            nextRun = now.withHour(12+1).withMinute(0).withSecond(0).withNano(0);

            if (now.compareTo(nextRun) >= 0)
            {
                nextRun =  now.plusDays(1).withHour(1).withMinute(0).withSecond(0).withNano(0);
            }
        }

        this.nextSummonDate = nextRun;
    }
}
