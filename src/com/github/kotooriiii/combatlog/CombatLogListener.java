package com.github.kotooriiii.combatlog;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static com.github.kotooriiii.util.HelperMethods.getPlayerInduced;
import static com.github.kotooriiii.util.HelperMethods.isPlayerInduced;

public class CombatLogListener implements Listener {
    @EventHandler (priority = EventPriority.LOWEST)
    public void onCombatLog(PlayerQuitEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        if(LostShardPlugin.getCombatLogManager().isTagged(uuid)) {
            event.getPlayer().setHealth(0);
            LostShardPlugin.getCombatLogManager().remove(uuid);
        }

    }

    @EventHandler (priority =  EventPriority.LOWEST)
    public void onTag(EntityDamageByEntityEvent event)
    {
        if(event.isCancelled())
            return;

        Entity damager = event.getDamager();
        Entity defender = event.getEntity();

        if (!isPlayerInduced(defender, damager))
            return;

        //Entties are players
        Player damagerPlayer = getPlayerInduced(defender, damager);
        Player defenderPlayer = (Player) defender;

        //
        //The code for each skill will follow on the bottom
        //

        LostShardPlugin.getCombatLogManager().add(damagerPlayer.getUniqueId());
        LostShardPlugin.getCombatLogManager().add(defenderPlayer.getUniqueId());

    }
}
