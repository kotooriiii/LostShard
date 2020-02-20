package com.github.kotooriiii.listeners;

import com.github.kotooriiii.clans.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class PlayerHitEvent implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {

            Player attacker = (Player) event.getDamager();
            UUID attackerUUID = attacker.getUniqueId();

            Player defender = (Player) event.getEntity();
            UUID defenderUUID = defender.getUniqueId();

            Clan attackerClan =Clan.getClan(attackerUUID);
            Clan defenderClan =  Clan.getClan(defenderUUID);

            if(attackerClan != null && defenderClan != null)
            {
                if(attackerClan.equals(defenderClan))
                {
                    if(attackerClan.isFriendlyFire())
                        event.setCancelled(true);
                }
            }

        }//end

    }

}
