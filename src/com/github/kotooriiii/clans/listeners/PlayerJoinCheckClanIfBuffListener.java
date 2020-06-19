package com.github.kotooriiii.clans.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinCheckClanIfBuffListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Clan clan = LostShardPlugin.getClanManager().getClan(player.getUniqueId());
        if (clan == null) {
            return;
        }

        if (!clan.hasHostilityBuff()) {
            Stat stat = Stat.wrap(player.getUniqueId());
            stat.setMaxMana(Stat.BASE_MAX_MANA);
            stat.setMaxStamina(Stat.BASE_MAX_STAMINA);
        }
    }
}
