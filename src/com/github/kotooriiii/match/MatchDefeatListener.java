package com.github.kotooriiii.match;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MatchDefeatListener implements Listener {
    @EventHandler
    public void onDeathMatch(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        if(Match.hasActiveMatch())
        {
            Match match = Match.getActiveMatch();
            if(!match.hasGameStarted())
                return;

            if(match.isFighter(player.getUniqueId()))
            {
                match.end(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        if(Match.hasActiveMatch())
        {
            Match match = Match.getActiveMatch();
            if(!match.hasGameStarted())
                return;

            if(match.isFighter(player.getUniqueId()))
            {
                match.end(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onMoveOutOfArena(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if(Match.hasActiveMatch())
        {
            Match match = Match.getActiveMatch();

            if(!match.hasGameStarted())
                return;

            if(match.isFighter(player.getUniqueId()))
            {
                if(!LostShardPlugin.getPlotManager().isStandingOnPlot(player))
                {
                    match.end(player.getUniqueId());
                    return;
                }

                Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(player);
                if(!plot.getName().equalsIgnoreCase("arena")) {
                    match.end(player.getUniqueId());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        if(Match.hasActiveMatch())
        {
            Match match = Match.getActiveMatch();

            if(!match.hasGameStarted())
                return;

            if(match.isFighter(player.getUniqueId()))
            {
                if(!LostShardPlugin.getPlotManager().isStandingOnPlot(player))
                {
                    match.end(player.getUniqueId());
                    return;
                }

                Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(player);
                if(!plot.getName().equalsIgnoreCase("arena")) {
                    match.end(player.getUniqueId());
                    return;
                }
            }
        }
    }
}
