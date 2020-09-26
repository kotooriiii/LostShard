package com.github.kotooriiii.npc;

import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathNPCListener implements Listener {
    @EventHandler
    public void onNPCDeath(PlayerDeathEvent event)
    {
        if(!CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        event.setDeathMessage(null);
    }
}
