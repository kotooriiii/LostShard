package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.type.circle3.IceSpell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class IceBallSuffocationListener implements Listener {

    private static HashSet<UUID> hashSet = new HashSet<>();

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
                    event.setCancelled(true);


                    if(hashSet.contains(entity.getUniqueId()))
                        return;

                    hashSet.add(entity.getUniqueId());

                    final float DAMAGE = 1.0f;
              //      ((Player) entity).damage(0.1f);
                    EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(damagerPlayer, entity, EntityDamageEvent.DamageCause.CUSTOM, DAMAGE);
                    entity.setLastDamageCause(damageByEntityEvent);
                    Bukkit.getPluginManager().callEvent(damageByEntityEvent);
                    ((Player) entity).setHealth(((Player) entity).getHealth() - DAMAGE);

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            hashSet.remove(entity.getUniqueId());
                        }
                    }.runTaskLater(LostShardPlugin.plugin, 20/2);

                }
            }
        }
    }
}
