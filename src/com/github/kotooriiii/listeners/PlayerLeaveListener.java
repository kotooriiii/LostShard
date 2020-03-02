package com.github.kotooriiii.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class PlayerLeaveListener implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
            clanTagCreators.remove(uuid);
            clanColorCreators.remove(uuid);
            clanDisbandTimer.remove(uuid);
            leaderConfirmation.remove(uuid);
            invitationConfirmation.remove(uuid);
    }
}
