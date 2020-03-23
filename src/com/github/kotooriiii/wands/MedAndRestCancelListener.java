package com.github.kotooriiii.wands;

import com.github.kotooriiii.stats.Stat;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.swing.plaf.synth.SynthTextAreaUI;

import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class MedAndRestCancelListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();

        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            return; // did not actually move to a new block. do nothing.
        }

        if(Stat.getMeditatingPlayers().contains(player.getUniqueId()))
        {
            Stat.getMeditatingPlayers().remove(player.getUniqueId());
player.sendMessage(STANDARD_COLOR + "You have stopped meditating.");
        }

        if(Stat.getRestingPlayers().contains(player.getUniqueId()))
        {
            Stat.getRestingPlayers().remove(player.getUniqueId());
            player.sendMessage(STANDARD_COLOR + "You have stopped resting.");

        }
    }
}
