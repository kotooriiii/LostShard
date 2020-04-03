package com.github.kotooriiii.sorcery.wands;

import com.github.kotooriiii.stats.Stat;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class MedAndRestCancelListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();



        if (to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()) {
            return; //Did not move from the block sitting at here.
        }

        //Not moving OR just moving mouse which is fine

        if(Stat.getMeditatingPlayers().contains(player.getUniqueId()))
        {
            Stat.getMeditatingPlayers().remove(player.getUniqueId());
player.sendMessage(ERROR_COLOR + "You have stopped meditating.");
        }

        if(Stat.getRestingPlayers().contains(player.getUniqueId()))
        {
            Stat.getRestingPlayers().remove(player.getUniqueId());
            player.sendMessage(ERROR_COLOR + "You have stopped resting.");

        }
    }
}
