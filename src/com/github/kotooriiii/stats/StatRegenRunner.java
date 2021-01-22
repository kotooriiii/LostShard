package com.github.kotooriiii.stats;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellToggleable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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

    public static void initManaDrainRunnable() {

        new BukkitRunnable() {
            @Override
            public void run() {

                for (Map.Entry<SpellToggleable, HashSet<UUID>> entry : Spell.getManaDrainMap().entrySet()) {

                    final SpellToggleable spell = entry.getKey();
                    final HashSet<UUID> set = entry.getValue();
                    final Iterator<UUID> iterator = set.iterator();
                    while(iterator.hasNext()) {

                        UUID uuid = iterator.next();

                        final Stat wrap = Stat.wrap(uuid);
                        double manaNew = wrap.getMana() - spell.getManaCostPerSecond();

                        if(manaNew < 0.0d)
                        {
                            spell.stopManaDrain(uuid);
                            iterator.remove();

                        }
                        else {
                            spell.manaDrainExecuteSpell(uuid);
                            wrap.setMana(manaNew);
                        }

                    }
                }
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
    }
}
