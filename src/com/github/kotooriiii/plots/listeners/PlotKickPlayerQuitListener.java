package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlotKickPlayerQuitListener implements Listener {
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();

        if(player.isDead())
            return;

        final Plot standingOnPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(player);
        if(!(standingOnPlot instanceof PlayerPlot))
            return;

        PlayerPlot playerPlot = (PlayerPlot) standingOnPlot;
        if(playerPlot.isMember(player.getUniqueId()))
            return;

        if(!playerPlot.isKick())
            return;

        //Not a member
        //Playerplot
        //isKick

        final Block highestBlockAt = player.getLocation().getWorld().getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
        player.teleport(highestBlockAt.getLocation());
    }
}
