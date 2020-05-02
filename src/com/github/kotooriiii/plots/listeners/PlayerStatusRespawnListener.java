package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.plots.struct.SpawnPlot;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PlayerStatusRespawnListener implements Listener {
    @EventHandler
    public void onRespawnStatus(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();

        event.setRespawnLocation(getSpawnLocation(player));
    }

    public static Location getSpawnLocation(Player player)
    {
        StatusPlayer statusPlayer = StatusPlayer.wrap(player.getUniqueId());
        Stat stat = Stat.wrap(statusPlayer.getPlayerUUID());
        stat.setMana(stat.getMaxMana());
        stat.setStamina(stat.getMaxStamina());
        String organization = statusPlayer.getStatus().getOrganization();
        SpawnPlot plot = (SpawnPlot) LostShardPlugin.getPlotManager().getPlot(organization);
        if(plot == null || plot.getSpawn() == null) {
            Bukkit.broadcastMessage(ERROR_COLOR + "The spawn is not created for " + organization + ".") ;
            return null;
        }

        return plot.getSpawn();
    }
}
