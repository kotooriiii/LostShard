package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class RemoveVanillaThings implements Listener {
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPhantomSpawn(EntitySpawnEvent event)
    {

        if(LostShardPlugin.isTutorial())
            return;
        Entity entity = event.getEntity();
        if(CitizensAPI.getNPCRegistry().isNPC(entity))
            return;
        if(!(entity instanceof Phantom))
            return;
        event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onNetherStarDrop(EntityDeathEvent event)
    {
        Entity entity = event.getEntity();
        if(CitizensAPI.getNPCRegistry().isNPC(entity))
            return;
        if(!entity.getType().equals(EntityType.WITHER))
            return;
        event.getDrops().removeIf(itemStack -> itemStack.getType() == Material.NETHER_STAR);
    }

}
