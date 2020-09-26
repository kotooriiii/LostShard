package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EnterOrderChapter extends AbstractChapter {

    private Zone zone;
    private boolean isComplete;

    public EnterOrderChapter() {
        isComplete=false;
        this.zone = new Zone(558, 576, 72, 66, 796, 804);
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximity(PlayerMoveEvent event) {
        if(isComplete)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (zone == null)
            return;

        Location to = event.getTo();
        if (!zone.contains(to))
            return;

        isComplete=true;

        final Player player = event.getPlayer();


        final int fadeIn = 10;
        final int stay = 40;
        final int fadeOut = 10;

        player.sendTitle(ChatColor.DARK_AQUA + "ORDER", "", fadeIn, stay, fadeOut);

        new BukkitRunnable() {
            @Override
            public void run() {
                setComplete();
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, fadeIn + stay + fadeOut);
    }


    @EventHandler
    public void onLeaveOrder(PlayerMoveEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(!PlotIntroChapter.getExitOrderZone().contains(event.getTo()))
            return;
        sendMessage(event.getPlayer(), "It's not time to venture out just yet.");

        event.setCancelled(true);
    }

}
