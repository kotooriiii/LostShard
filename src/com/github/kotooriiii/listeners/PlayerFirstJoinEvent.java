package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerFirstJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if(player.hasPlayedBefore())
            return;
        if(LostShardPlugin.isTutorial())
            return;

        //first time played

        player.performCommand("book");
    }
}
