package com.github.kotooriiii.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class RemovePhantomListener implements Listener {
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPhantomSpawn(EntitySpawnEvent event)
    {
        Entity entity = event.getEntity();
        if(!(entity instanceof Phantom))
            return;
        event.setCancelled(true);
    }
}
