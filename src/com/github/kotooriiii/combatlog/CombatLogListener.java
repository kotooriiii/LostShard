package com.github.kotooriiii.combatlog;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.type.guard.GuardTrait;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import jdk.internal.org.objectweb.asm.commons.SerialVersionUIDAdder;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

    private boolean isGuard(Entity killer) {

        return killer != null && killer instanceof Player
                && CitizensAPI.getNPCRegistry().isNPC(killer) && CitizensAPI.getNPCRegistry().getNPC(killer).hasTrait(GuardTrait.class);
    }

    private boolean isLastDamageCauseFromGuard(Player playerDead) {
        if (playerDead.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            return isGuard(((EntityDamageByEntityEvent) playerDead.getLastDamageCause()).getDamager());
        }

        Bukkit.broadcastMessage("debug: " + playerDead.getLastDamageCause().getEventName());

        return false;
    }

    public Entity getGuardKiller(Player playerDead)
    {
        if(isGuard(playerDead.getKiller()))
            return playerDead.getKiller();

        if(isLastDamageCauseFromGuard(playerDead))
            return ((EntityDamageByEntityEvent) playerDead.getLastDamageCause()).getDamager();
        return null;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {

        //is npc
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;

        Player player = event.getEntity();
        CombatLogManager combatLogManager = LostShardPlugin.getCombatLogManager();
        StatusPlayer sp = StatusPlayer.wrap(player.getUniqueId());
        Player killer = event.getEntity().getKiller();


        //if not tagged, its possible a guard was called or a guard just found him.
        if (!combatLogManager.isTagged(player.getUniqueId())) {

            if (isGuard(killer) || isLastDamageCauseFromGuard(event.getEntity())) {
                Entity realKiller = getGuardKiller(event.getEntity());

                GuardTrait guardTrait = CitizensAPI.getNPCRegistry().getNPC(realKiller).getTrait(GuardTrait.class);
                if (guardTrait.getOwner() == null) {
                    event.setDeathMessage(ChatColor.WHITE + "The " + sp.getStatus().getChatColor() + sp.getStatus().getName().toLowerCase() + " " + player.getName() + ChatColor.WHITE + " was killed by " + ChatColor.YELLOW + "[Guard] Fred" + ChatColor.WHITE + ".");
                } else {
                    event.setDeathMessage(ChatColor.WHITE + "The " + sp.getStatus().getChatColor() + sp.getStatus().getName().toLowerCase() + " " + player.getName() + ChatColor.WHITE + " was killed by " + ChatColor.YELLOW + "[Guard] Fred " + ChatColor.WHITE + "for attacking " + StatusPlayer.wrap(guardTrait.getOwner()).getStatus().getChatColor() + "" + guardTrait.getCachedOwnerName() + ChatColor.WHITE + ".");
                }
            }
            LostShardPlugin.getCombatLogManager().remove(player.getUniqueId());
            return;
        }

        //Organize tagged players and ADD the killer
        CombatTaggedPlayer taggedPlayer = combatLogManager.wrap(player.getUniqueId());

        Set<OfflinePlayer> attackersSet = new LinkedHashSet<>();
        OfflinePlayer[] attackers = taggedPlayer.getAttackers();

        for (OfflinePlayer offlinePlayer : attackers) {
            attackersSet.add(offlinePlayer);
        }


        //What if the killer was the guard?
        if (isGuard(killer) || isLastDamageCauseFromGuard(event.getEntity())) {

            Entity realKiller = getGuardKiller(event.getEntity());

            GuardTrait guardTrait = CitizensAPI.getNPCRegistry().getNPC(realKiller).getTrait(GuardTrait.class);
            if (guardTrait.getOwner() == null) {
                event.setDeathMessage(ChatColor.WHITE + "The " + sp.getStatus().getChatColor() + sp.getStatus().getName().toLowerCase() + " " + player.getName() + ChatColor.WHITE + " was killed by " + ChatColor.YELLOW + "[Guard] Fred" + ChatColor.WHITE + ".");
            } else {
                event.setDeathMessage(ChatColor.WHITE + "The " + sp.getStatus().getChatColor() + sp.getStatus().getName().toLowerCase() + " " + player.getName() + ChatColor.WHITE + " was killed by " + ChatColor.YELLOW + "[Guard] Fred " + ChatColor.WHITE + "for attacking " + StatusPlayer.wrap(guardTrait.getOwner()).getStatus().getChatColor() + "" + guardTrait.getCachedOwnerName() + ChatColor.WHITE + ".");
            }
            LostShardPlugin.getCombatLogManager().remove(player.getUniqueId());
            return;
        }


        if (killer != null) {
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
            if (offlinePlayer == null || (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()))
                continue;
            StatusPlayer statusPlayer = StatusPlayer.wrap(offlinePlayer.getUniqueId());
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

            } else if (i == 0) {
                //On the last element
                if (attackers.length == 2) {
                    message += ChatColor.WHITE + " and " + color + offlinePlayer.getName();

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
        if (CitizensAPI.getNPCRegistry().isNPC(defender) || CitizensAPI.getNPCRegistry().isNPC(damager))
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
