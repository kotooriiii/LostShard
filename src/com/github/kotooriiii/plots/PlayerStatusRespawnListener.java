package com.github.kotooriiii.plots;

import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerStatusRespawnListener implements Listener {
    @EventHandler
    public void onRespawnStatus(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        StatusPlayer statusPlayer = StatusPlayer.wrap(player.getUniqueId());
        Stat stat = Stat.wrap(statusPlayer.getPlayerUUID());
        stat.setMana(stat.getMaxMana());
        stat.setStamina(stat.getMaxStamina());
        String organization = statusPlayer.getStatus().getOrganization();
        Plot plot = Plot.getPlot(organization);
        event.setRespawnLocation(plot.getCenter());
    }
}
