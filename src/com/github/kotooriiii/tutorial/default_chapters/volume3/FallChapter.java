package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.plots.commands.BuildCommand;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class FallChapter extends AbstractChapter {

    private boolean isComplete;
    private Zone zone;

    public FallChapter() {
        this.isComplete = false;
        //todo zone
    }

    @Override
    public void onBegin() {

        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;
        sendMessage(player, "Ouch, that was a hard hit!\nMaybe we'll find something on the way to heal us...");
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximity(PlayerMoveEvent event) {
        if (isComplete)
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

        isComplete = true;
        final Player player = event.getPlayer();
        sendMessage(player, "Some melons! Break them to collect them!\nMelons can be instantly eaten by right clicking them.\nThey instantly heal your hearts and hunger.\nThis can be very useful in combat.");

        new BukkitRunnable() {
            @Override
            public void run() {
                setComplete();
            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);
    }


}
