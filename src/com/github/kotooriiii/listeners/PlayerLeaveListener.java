package com.github.kotooriiii.listeners;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.hostility.HostilityPlatform;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.commands.CastCommand.markCommand;
import static com.github.kotooriiii.commands.CastCommand.recallCommand;
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
        hostilityCreatorConfirmation.remove(uuid);
        hostilityRemoverConfirmation.remove(uuid);
        hostilityPlatformCreator.remove(uuid);
        hostilityTimeCreator.remove(uuid);
        spawnTimer.remove(uuid);
        markCommand.remove(uuid);
        recallCommand.remove(uuid);

    }
}
