package com.github.kotooriiii.skills.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MiningListener implements Listener {


    private boolean isStoneType(Block block)
    {
        Material type = block.getType();
        switch (type) {
            case STONE:
            case ANDESITE:
            case DIORITE:
            case GRANITE:
                return true;
        }
        return false;
    }

    private void calculate()
    {

    }
}
