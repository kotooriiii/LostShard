package com.github.kotooriiii.listeners;

import com.github.kotooriiii.events.PlayerEnterPlotEvent;
import com.github.kotooriiii.plots.Plot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static com.github.kotooriiii.data.Maps.spawnTimer;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PlayerSpawnMoveListener implements Listener {
    @EventHandler
    public void onPlayerSpawnMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        int fromX = from.getBlockX();
        int fromY = from.getBlockY();
        int fromZ = from.getBlockZ();

        int toX = to.getBlockX();
        int toY = to.getBlockY();
        int toZ = to.getBlockZ();

        //The player has not moved from the block they're standing in.
        if (fromX == toX && fromY == toY && fromZ == toZ)
            return;

        if (spawnTimer.contains(player.getUniqueId())) {
            player.sendMessage(ERROR_COLOR + "The teleportation request to spawn has been canceled due to movement.");
            spawnTimer.remove(player.getUniqueId());
            return;
        }
    }
}
