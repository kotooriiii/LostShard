package com.github.kotooriiii.plots.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerHitListener implements Listener {
    @EventHandler (priority =  EventPriority.LOWEST)
    public void onHit(EntityDamageByEntityEvent event)
    {
        if(event.getDamager() == null || event.getEntity() == null)
            return;
        if(!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player))
            return;
        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            return;
        if(((Player) event.getDamager()).hasLineOfSight(event.getEntity()))
            return;
        event.setCancelled(true);
    }
}
