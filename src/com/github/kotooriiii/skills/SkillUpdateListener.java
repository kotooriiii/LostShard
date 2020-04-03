package com.github.kotooriiii.skills;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class SkillUpdateListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        if(SkillPlayer.getPlayerSkills().get(uuid) == null)
        {
            SkillPlayer skillPlayer = new SkillPlayer(uuid);
            skillPlayer.save();
        }
    }
}
