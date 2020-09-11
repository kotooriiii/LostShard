package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PathToEventChapter extends AbstractChapter {

    private boolean isComplete;
    private Zone zone;

    public PathToEventChapter()
    {
        isComplete = false;
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
        sendMessage(player, "That's a setback. Looks like the only way to keep moving forward is to jump...");
    }

    @EventHandler
    public void onDmg(EntityDamageEvent event)
    {
        if (!event.getEntity().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
       if(event.getCause() != EntityDamageEvent.DamageCause.FALL)
           return;
       if(!(event.getEntity() instanceof Player))
           return;
       Player player = (Player) event.getEntity();

       if(player.getHealth() - event.getDamage() == 0)
           return;

       setComplete();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        if (!event.getEntity().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        isComplete = false;

    }
}
