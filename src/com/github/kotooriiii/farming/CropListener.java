package com.github.kotooriiii.farming;

import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class CropListener implements Listener {
    @EventHandler
    public void onFarm(BlockGrowEvent event)
    {
        Block block =event.getBlock();
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Ageable))
            return;


    }
}
