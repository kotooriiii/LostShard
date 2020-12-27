package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class LightningListener implements Listener {
    @EventHandler
    public void onFire(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
            Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(event.getBlock().getLocation());
            if (plot != null && plot.getType().isStaff()) {
                event.setCancelled(true);
            }
        }
    }
}
