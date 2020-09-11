package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.events.PlotExpandEvent;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class PlotExpandChapter extends AbstractChapter {
    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if(player==null)
            return;
        sendMessage(player, "Expand your plot by typing: /plot expand.\nDo this multiple times to make it bigger.");
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onExpand(PlotExpandEvent event) {
        if(!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;

        if(event.getNextRadius() == 3 || !canExpand(event.getPlot()))
        {
            LostShardPlugin.getPlotManager().removePlot(event.getPlot());
            sendMessage(event.getPlayer(), "Looks like you ran out of gold!\nLet's get some more by capturing an event.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    setComplete();
                }
            }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);
            event.setCancelled(true);
        }
    }

    private boolean canExpand(PlayerPlot expandPlot)
    {

        if (!expandPlot.isExpandable()) {
            return false;
        }

        if (expandPlot.getBalance() < expandPlot.getExpandCost()) {
            return false;
        }
        return true;
    }
}
