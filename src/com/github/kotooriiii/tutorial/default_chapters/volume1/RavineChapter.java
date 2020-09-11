package com.github.kotooriiii.tutorial.default_chapters.volume1;

import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class RavineChapter extends AbstractChapter {
    private Zone zone;
    private boolean isComplete;

    public RavineChapter() {
        isComplete = false;
        //todo
        // zone = new Zone();
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        if (isComplete)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!zone.contains(event.getTo()))
            return;
        isComplete = true;

        PlayerInventory inventory = event.getPlayer().getInventory();
        for (int i = 0; i < inventory.getContents().length; i++)
        {
            if(inventory.getContents()[i].getType() == Material.FEATHER)
            inventory.setItem(i, null);
        }


        sendMessage(event.getPlayer(), "Great job!");
        setComplete();

    }

}
