package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.plots.events.PlotCreateEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlotIntroChapter extends AbstractChapter {
    private boolean isFound;
    private Location location;
    private static Zone zone = new Zone(738, 754, 72, 65, 796, 812);

    public PlotIntroChapter()
    {
        this.isFound = false;
    }

    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        sendMessage(player, "You can't stay here forever!\nGo outside and make a plot.");
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onAppropiatePlot(PlayerMoveEvent event) {
        if(isFound)
            return;
        if(!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;
        if(LostShardPlugin.getPlotManager().hasNearbyPlots(event.getTo()))
            return;
        if(location != null && location.getBlockX() == event.getTo().getBlockX() && location.getBlockY() == event.getTo().getBlockY() && location.getBlockZ() == event.getTo().getBlockZ())
            return;

        isFound=true;
        location = event.getTo();
        new BukkitRunnable()
        {
            @Override
            public void run() {
                isFound=false;
            }
        }.runTaskLater(LostShardPlugin.plugin, 20*3);

        sendMessage(event.getPlayer(), "This is a good spot for a plot.\nPlots cost 10 gold and 1 diamond to create.\nType /plot create (name) to create your plot.");
    }

    @EventHandler
    public void onPlotCreate(PlotCreateEvent event)
    {
        if(!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;
        setComplete();
    }

    public static Zone getZone() {
        return zone;
    }
}
