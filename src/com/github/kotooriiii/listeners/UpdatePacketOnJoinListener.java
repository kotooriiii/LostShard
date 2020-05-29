package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.ShardBanker;
import com.github.kotooriiii.npc.ShardGuard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdatePacketOnJoinListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable(){
            @Override
            public void run() {
                Player player = event.getPlayer();
                for(ShardGuard guard : ShardGuard.getActiveShardGuards())
                {
                    guard.update(player);
                }

                for(ShardBanker banker : ShardBanker.getActiveShardBankers())
                {
                    banker.update(player);
                }}
        }.runTaskLater(LostShardPlugin.plugin, 80);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTP(PlayerTeleportEvent event) {

        if(event.getFrom().getWorld().equals(event.getTo().getWorld()))
            return;

        new BukkitRunnable(){
            @Override
            public void run() {
                Player player = event.getPlayer();
                for(ShardGuard guard : ShardGuard.getActiveShardGuards())
                {
                    guard.update(player);
                }

                for(ShardBanker banker : ShardBanker.getActiveShardBankers())
                {
                    banker.update(player);
                }}
        }.runTaskLater(LostShardPlugin.plugin, 80);
    }


    @EventHandler(ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event)
    {

        new BukkitRunnable(){
            @Override
            public void run() {
                Player player = event.getPlayer();
                for(ShardGuard guard : ShardGuard.getActiveShardGuards())
                {
                    guard.update(player);
                }

                for(ShardBanker banker : ShardBanker.getActiveShardBankers())
                {
                    banker.update(player);
                }}
            }.runTaskLater(LostShardPlugin.plugin, 80);
        }



}
