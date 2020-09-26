package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnerCancelListener implements Listener {
    @EventHandler
    public void onMobSpawnCancel(CreatureSpawnEvent event) {
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER))
            return;
        if(CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Location location = event.getLocation();
        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(location);
        if(plot == null) {
            event.setCancelled(true);
            return;
        }

        if(plot instanceof PlayerPlot)
        {
            PlayerPlot playerPlot = (PlayerPlot) plot;
            if(!playerPlot.isDungeon()) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
