package com.github.kotooriiii.stats;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

                    boolean manaRecovered = false;
                    boolean staminaRecovered = false;

                    if (stat.getMaxStamina() > stat.getStamina() + staminaRecover)
                        stat.setStamina(stat.getStamina() + staminaRecover);
                    else {
                        if (stat.getMaxStamina() > stat.getStamina())
                            staminaRecovered = true;
                        stat.setStamina(stat.getMaxStamina());
                    }

                    if (stat.getMaxMana() > stat.getMana() + manaRecover)
                        stat.setMana(stat.getMana() + manaRecover);
                    else {
                        if (stat.getMaxMana() > stat.getMana())
                            manaRecovered = true;
                        stat.setMana(stat.getMaxMana());
                    }

                    if (manaRecovered)
                        Bukkit.getOfflinePlayer(stat.getPlayerUUID()).getPlayer().sendMessage(ChatColor.GOLD + "Your mana has fully regenerated.");

                    if (staminaRecovered)
                        Bukkit.getOfflinePlayer(stat.getPlayerUUID()).getPlayer().sendMessage(ChatColor.GOLD + "Your stamina has fully regenerated.");

                }
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, 20);
    }
}
