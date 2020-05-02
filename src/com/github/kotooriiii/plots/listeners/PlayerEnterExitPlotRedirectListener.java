package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.PlayerEnterPlotEvent;
import com.github.kotooriiii.events.PlayerLeavePlotEvent;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerEnterExitPlotRedirectListener implements Listener {
    @EventHandler
    public void onPlotEnterRedirect(PlayerMoveEvent event) {
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

        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            if (!(plot.contains(to) && !plot.contains(from)))
                continue;

            PlayerEnterPlotEvent playerEnterPlotEvent = new PlayerEnterPlotEvent(player, plot);
            Bukkit.getPluginManager().callEvent(playerEnterPlotEvent);
            if (playerEnterPlotEvent.isCancelled())
                return;

            //Code

            player.sendMessage(ChatColor.GRAY + "You have entered " + plot.getName() + ".");


            //End code
            break;
        }
    }

    @EventHandler
    public void onPlotLeaveRedirect(PlayerMoveEvent event) {
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

        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            if (!(!plot.contains(to) && plot.contains(from)))
                continue;

            PlayerLeavePlotEvent playerLeavePlotEvent = new PlayerLeavePlotEvent(player, plot);
            Bukkit.getPluginManager().callEvent(playerLeavePlotEvent);
            if (playerLeavePlotEvent.isCancelled())
                return;

            //Code

            player.sendMessage(ChatColor.GRAY + "You have left " + plot.getName() + ".");

            //End code
            break;
        }
    }
}
