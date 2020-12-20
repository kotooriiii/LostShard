package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.sorcery.spells.type.IceSpell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class IceBallSuffocationListener implements Listener {
    @EventHandler
    public void onSuffocation(EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        //is a player

        if (event.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION)
            return;

        Location location = entity.getLocation();
        location = location.add(0, 1, 0);
        location.setPitch(0);
        location.setYaw(0);
        location.setX(location.getBlockX());
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ());

        //is suffocating
        for (Map.Entry<UUID, HashSet<Location>> entry : IceSpell.getUuidBlockPlacedMap().entrySet()) {
            UUID playerPlaced = entry.getKey();
            HashSet<Location> set = entry.getValue();
            if (set.contains(location)) {
                Player damagerPlayer = Bukkit.getPlayer(playerPlaced);
                if (damagerPlayer.isOnline()) {


                    if (entity.getUniqueId().equals(playerPlaced))

                        ((Player) entity).damage(2.0f);

                    else
                        ((Player) entity).damage(2.0f, damagerPlayer);

                    event.setCancelled(true);
                }
            }
        }
    }
}
