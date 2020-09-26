package com.github.kotooriiii.tutorial.default_chapters.volume1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class IntroWandChapter extends AbstractChapter {


    @Override
    public void onBegin() {

        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;
        sendMessage(player, "Follow the holograms.");

        sendMessage(player, "You can use wands to cast spells!");

        setComplete();


    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximity(PlayerMoveEvent event) {

        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        Location to = event.getTo();
        if (to.getBlockZ() < WandInstructionChapter.getLimitZone())
            return;

        sendMessage(event.getPlayer(), "You can't go past here until you bind your wand with teleport! You can do so with: /bind tp");
        event.setCancelled(true);

    }

}


