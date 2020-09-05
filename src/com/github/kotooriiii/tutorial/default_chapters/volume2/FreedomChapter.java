package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class FreedomChapter extends AbstractChapter {

    private Zone zone;
    public FreedomChapter()
    {
        //todo zone
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximity(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (zone == null)
            return;

        Location to = event.getTo();
        if (!zone.contains(to))
            return;

        sendMessage(event.getPlayer(), "You've made it!\nMake your way to Order straight ahead.");
        setComplete();
    }
}
