package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.sorcery.wands.Wand;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class NoAbuseBlockBreakMaterialListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        {
            Location loc = event.getBlock().getLocation();
            if (Wand.getLocationsForNonBlockBreak().contains(loc))
                event.setDropItems(false);
        }
    }
}
