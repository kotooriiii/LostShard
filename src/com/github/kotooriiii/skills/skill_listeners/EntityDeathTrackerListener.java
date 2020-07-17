package com.github.kotooriiii.skills.skill_listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.UUID;

@Deprecated
public class EntityDeathTrackerListener implements Listener {

    private static HashMap<UUID, UUID> tracker = new HashMap<>();

    //@EventHandler
    public void onTracker()
    {}

    @EventHandler(priority = EventPriority.LOWEST)
    public void ad(EntityDeathEvent event) {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void ad(EntityDamageEvent event) {
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void ad(EntityDamageByEntityEvent event) {
      // tracker.put(event.getEntity().getUniqueId(), event.getDamager().getUniqueId());
    }

}
