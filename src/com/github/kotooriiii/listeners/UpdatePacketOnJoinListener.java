package com.github.kotooriiii.listeners;

import com.github.kotooriiii.guards.ShardBanker;
import com.github.kotooriiii.guards.ShardGuard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdatePacketOnJoinListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for(ShardGuard guard : ShardGuard.getActiveShardGuards())
        {
            guard.update(player);
        }

        for(ShardBanker banker : ShardBanker.getActiveShardBankers())
        {
            banker.update(player);
        }
    }
}
