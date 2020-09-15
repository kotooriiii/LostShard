package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class StatusInstructionChapter extends AbstractChapter {

    private boolean a, b;
    private Zone zA, zB;

    public StatusInstructionChapter() {
        this.a = false;
        this.b = false;
        zA = new Zone(657, 671, 73, 65, 878, 846);
        zB = new Zone(671, 657, 73, 64, 730, 762);
    }

    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximityA(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (a && b)
            setComplete();
        if (a)
            return;
        if (zA == null)
            return;

        Location to = event.getTo();
        if (!zA.contains(to))
            return;

        a = true;
        final Player player = event.getPlayer();
        sendMessage(player, "A player becomes a criminal when they hit another player.");
        if (!b)
            checkForOther(player);
    }

    private void checkForOther(Player player) {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                sendMessage(player, "Let's visit the other side of Order.");

            }
        }.runTaskLater(LostShardPlugin.plugin, 20*5);
    }

    @EventHandler
    public void onProximityB(PlayerMoveEvent event) {
        if (b)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (zB == null)
            return;

        Location to = event.getTo();
        if (!zB.contains(to))
            return;

        b = true;
        final Player player = event.getPlayer();
        sendMessage(player, "A player becomes a murderer when they kill 5 times.");
        if (!a)
            checkForOther(player);
    }


    @EventHandler
    public void onLeaveOrder(PlayerMoveEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(!PlotIntroChapter.getZone().contains(event.getTo()))
            return;
        sendMessage(event.getPlayer(), "It's not time to venture out just yet.");

        event.setCancelled(true);
    }

}
