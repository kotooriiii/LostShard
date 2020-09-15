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

public class FallChapter extends AbstractChapter {

    private boolean isComplete;
    private boolean isCompleteToLeave;
    private Zone zone;
    private Zone completeZone;


    public FallChapter() {
        this.isComplete = false;
        this.isCompleteToLeave = false;
        this.zone = new Zone(746, 691, 48, 75, 1061, 1131);
        completeZone = new Zone(561, 486, 78, 30, 1132, 1160);
    }

    @Override
    public void onBegin() {

        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;
        setLocation(new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 891, 54, 976, 47, 13));
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
        sendMessage(player, "Some melons! Break them to collect them!\nMelons can be instantly eaten by right clicking them.\nThey instantly heal your hearts and hunger.\nThis can be very useful in combat, let's move on.");

    }

    @EventHandler
    public void onLeave(PlayerMoveEvent event) {
        if (isCompleteToLeave)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (completeZone == null)
            return;

        Location to = event.getTo();
        if (!completeZone.contains(to))
            return;

        isCompleteToLeave = true;
        setComplete();
    }

}
