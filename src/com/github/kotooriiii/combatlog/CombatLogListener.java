package com.github.kotooriiii.combatlog;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.minecraft.server.v1_15_R1.CommandGamemode;
import org.bukkit.Bukkit;
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

import java.util.LinkedHashSet;
import java.util.Set;
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


        Set<OfflinePlayer> attackersSet = new LinkedHashSet<>();
        OfflinePlayer[] attackers = taggedPlayer.getAttackers();

        for (OfflinePlayer offlinePlayer : attackers) {
            attackersSet.add(offlinePlayer);
        }

        Player killer = event.getEntity().getKiller();
        if(killer != null) {
            attackersSet.add(Bukkit.getOfflinePlayer(killer.getUniqueId()));
        }

        attackers = attackersSet.toArray(new OfflinePlayer[attackersSet.size()]);


        if (attackers.length == 0)
            return;

        String message = StatusPlayer.wrap(player.getUniqueId()).getStatus().getChatColor() + player.getName();
        message += ChatColor.WHITE + " was killed by ";

        int counter = 0;
        for (int i = attackers.length - 1; i >= 0; i--) {
            OfflinePlayer offlinePlayer = attackers[i];
            if(offlinePlayer == null || (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()))
                continue;
            StatusPlayer statusPlayer =  StatusPlayer.wrap(offlinePlayer.getUniqueId());
            Status status = statusPlayer.getStatus();
            ChatColor color = status.getChatColor();
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
        if(CitizensAPI.getNPCRegistry().isNPC(defender) || CitizensAPI.getNPCRegistry().isNPC(damager))
            return;

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
