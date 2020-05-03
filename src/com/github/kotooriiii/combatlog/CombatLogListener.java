package com.github.kotooriiii.combatlog;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static com.github.kotooriiii.util.HelperMethods.getPlayerInduced;
import static com.github.kotooriiii.util.HelperMethods.isPlayerInduced;

public class CombatLogListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCombatLog(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (LostShardPlugin.getCombatLogManager().isTagged(uuid)) {
            event.getPlayer().setHealth(0);
            LostShardPlugin.getCombatLogManager().remove(uuid);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        CombatLogManager combatLogManager = LostShardPlugin.getCombatLogManager();
        if (!combatLogManager.isTagged(player.getUniqueId()))
            return;

        CombatTaggedPlayer taggedPlayer = combatLogManager.wrap(player.getUniqueId());
        OfflinePlayer[] attackers = taggedPlayer.getAttackers();
        if (attackers.length == 0)
            return;

        String message = StatusPlayer.wrap(player.getUniqueId()).getStatus().getChatColor() + player.getName();
        message += ChatColor.WHITE + " was killed by ";

        int counter = 0;
        for (int i = attackers.length - 1; i >= 0; i--) {
            OfflinePlayer offlinePlayer = attackers[i];
            ChatColor color = StatusPlayer.wrap(offlinePlayer.getUniqueId()).getStatus().getChatColor();
            if (counter == 3) {
                break;
            }

            if (i == attackers.length - 1) {
                //The first element
                message += color + offlinePlayer.getName();
            } else if (0 < i) {
                //Not last
                message += ChatColor.WHITE + ", " + color + offlinePlayer.getName();

            } else if (i==0){
                //On the last element
                if(attackers.length == 2)
                {
                    message += ChatColor.WHITE + " and " + color + offlinePlayer.getName() ;

                } else {
                    message += ChatColor.WHITE + ", and " + color + offlinePlayer.getName();
                }
            } else {
                message += "null";
            }

            counter++;
        }

        message += ChatColor.WHITE + ".";

        LostShardPlugin.getCombatLogManager().remove(player.getUniqueId());
        event.setDeathMessage(message);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTag(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
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

        LostShardPlugin.getCombatLogManager().add(damagerPlayer.getUniqueId(), defender.getUniqueId());
        LostShardPlugin.getCombatLogManager().add(defenderPlayer.getUniqueId(), damager.getUniqueId());


    }
}
