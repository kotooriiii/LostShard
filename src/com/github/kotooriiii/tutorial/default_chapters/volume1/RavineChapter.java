package com.github.kotooriiii.tutorial.default_chapters.volume1;

import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;

public class RavineChapter extends AbstractChapter {

    @Override
    public void onBegin() {

        final Player player = Bukkit.getPlayer(getUUID());
        if(player==null)
            return;

        PlayerInventory inventory =player.getInventory();
        for (int i = 0; i < inventory.getContents().length; i++)
        {
            if(inventory.getContents()[i].getType() == Material.FEATHER)
                inventory.setItem(i, null);
        }


        sendMessage(player, "Great job!");
        setComplete();
    }

    @Override
    public void onDestroy() {

    }

}
