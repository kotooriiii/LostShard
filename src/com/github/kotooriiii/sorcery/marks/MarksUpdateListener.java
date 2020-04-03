package com.github.kotooriiii.sorcery.marks;

import com.github.kotooriiii.skills.SkillPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.swing.*;
import java.util.UUID;

public class MarksUpdateListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        if(MarkPlayer.getMarkPlayers().get(uuid) == null)
        {
        }
        else {
            MarkPlayer markPlayer = MarkPlayer.wrap(event.getPlayer().getUniqueId());

        }
    }
}
