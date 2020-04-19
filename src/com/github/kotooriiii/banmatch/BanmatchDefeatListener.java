package com.github.kotooriiii.banmatch;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BanmatchDefeatListener implements Listener {
    @EventHandler
    public void onDeathMatch(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        if(Banmatch.hasActiveMatch())
        {
            Banmatch banmatch = Banmatch.getActiveMatch();
            if(banmatch.isFighter(player.getUniqueId()))
            {
                banmatch.end(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if(Banmatch.hasActiveMatch())
        {
            Banmatch banmatch = Banmatch.getActiveMatch();
            if(banmatch.isFighter(player.getUniqueId()))
            {
                banmatch.end(player.getUniqueId());
            }
        }
    }
}
