package com.github.kotooriiii.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class PreventRemovalChunkListener implements Listener {
    @EventHandler
    public void preventRemovalChunkListener(ChunkLoadEvent event)
    {
     //   event.getChunk().getWorld().setChunkForceLoaded(event.getChunk().getX(), event.getChunk().getZ(), true);
    }
}
