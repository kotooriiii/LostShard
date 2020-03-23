package com.github.kotooriiii.stats;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class StatJoinListener implements Listener {
    @EventHandler
    public void onStatJoin(PlayerJoinEvent event) {
        if (!Stat.getStatMap().containsKey(event.getPlayer().getUniqueId()))
            Stat.getStatMap().put(event.getPlayer().getUniqueId(), new Stat(event.getPlayer().getUniqueId()));
    }
}
