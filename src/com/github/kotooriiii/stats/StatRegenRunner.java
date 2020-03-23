package com.github.kotooriiii.stats;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class StatRegenRunner {
    public static void regen() {
        new BukkitRunnable() {
            @Override
            public void run() {
                HashMap<UUID, Stat> statMap = Stat.getStatMap();
                Stat[] stats = statMap.values().toArray(new Stat[statMap.values().size()]);

                for (Stat stat : stats) {
                    if (!Bukkit.getOfflinePlayer(stat.getPlayerUUID()).isOnline())
                        continue;

                    double staminaRecover = 1;
                    double manaRecover = 1;

                    if (Stat.getRestingPlayers().contains(stat.getPlayerUUID()))
                        staminaRecover = 1.5;

                    if (Stat.getMeditatingPlayers().contains(stat.getPlayerUUID()))
                        manaRecover = 1.5;

                    if (stat.getMaxStamina() > stat.getStamina() + staminaRecover)
                        stat.setStamina(stat.getStamina() + staminaRecover);
                    else
                        stat.setStamina(stat.getMaxStamina());

                    if (stat.getMaxMana() > stat.getMana() + manaRecover)
                        stat.setMana(stat.getMana() + manaRecover);
                    else
                        stat.setMana(stat.getMaxMana());
                }
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, 20);
    }
}
