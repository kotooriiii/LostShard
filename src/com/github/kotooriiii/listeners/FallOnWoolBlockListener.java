package com.github.kotooriiii.listeners;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FallOnWoolBlockListener implements Listener {
    @EventHandler
    public void onFallOnWool(EntityDamageEvent event)
    {
        if(event.isCancelled())
            return;
        if(event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;
        if(!(event.getEntity() instanceof Player))
            return;
        if(!event.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().getKey().getKey().toUpperCase().equals("_WOOL"))
            return;

        event.setCancelled(true);
    }
}
