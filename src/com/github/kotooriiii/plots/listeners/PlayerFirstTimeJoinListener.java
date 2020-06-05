package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.SpawnPlot;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerFirstTimeJoinListener implements Listener {
    @EventHandler (priority =  EventPriority.HIGHEST)
    public void firstTimeJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if(player.hasPlayedBefore())
            return;

        //player has not played before
        SpawnPlot spawnPlot = (SpawnPlot) LostShardPlugin.getPlotManager().getPlot("order");
        if(spawnPlot == null || spawnPlot.getSpawn() == null)
            return;
        player.teleport(spawnPlot.getSpawn());

    }
}
