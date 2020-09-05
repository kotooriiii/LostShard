package com.github.kotooriiii.tutorial.default_chapters.volume1;

import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class RavineChapter extends AbstractChapter {
    private Zone zone;

    public RavineChapter() {

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
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(!zone.contains(event.getTo()))
            return;

        sendMessage(event.getPlayer(), "Great job!");
        setComplete();

    }

}
