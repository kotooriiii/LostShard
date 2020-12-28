package com.github.kotooriiii.status;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.npc.type.guard.GuardNPC;
import com.github.kotooriiii.npc.type.guard.GuardTrait;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.stats.Stat;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.time.ZonedDateTime;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.getPlayerInduced;
import static com.github.kotooriiii.util.HelperMethods.isPlayerInduced;

public class StatusUpdateListener implements Listener {

    private static HashMap<UUID, BukkitTask> playersCriminal = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCriminalTimer(EntityDamageByEntityEvent event) {

        if (event.isCancelled())
            return;

        Entity damager = event.getDamager();
        Entity defender = event.getEntity();

        if (CitizensAPI.getNPCRegistry().isNPC(damager) || CitizensAPI.getNPCRegistry().isNPC(defender))
            return;

        if (!isPlayerInduced(defender, damager))
            return;

        //Entties are players
        Player damagerPlayer = getPlayerInduced(defender, damager);
        Player defenderPlayer = (Player) defender;

        if (damagerPlayer.equals(defenderPlayer))
            return;

        if (Staff.isStaff(damagerPlayer.getUniqueId()) || Staff.isStaff(defenderPlayer.getUniqueId()))
            return;

        if (defender.isDead())
            return;

        Stat stat = Stat.wrap(damagerPlayer.getUniqueId());
        if (stat.getMillisInit() != 0 && ZonedDateTime.now().toInstant().toEpochMilli() - stat.getMillisInit() < 1000 * 60 * 60 * 24 * 7) {

           Plot plot=  LostShardPlugin.getPlotManager().getStandingOnPlot(defenderPlayer.getLocation());
            if(plot != null && plot.getName().equalsIgnoreCase("order"))
            {
                guardKnockback(damagerPlayer);
                stat.setMillisInit(0);
                damager.sendMessage(ChatColor.GOLD + "[Guard]" + ERROR_COLOR + " DO NOT HIT OTHER BLUE NAMES IN ORDER OR YOU WILL BECOME CRIMINAL AND THE GUARDS WILL KILL YOU. THIS IS YOUR ONLY WARNING. ");
                event.setCancelled(true);
                return;
            }
        }

        //Get each players' status
        Status defenderStatus = StatusPlayer.wrap(defenderPlayer.getUniqueId()).getStatus();
        Status damagerStatus = StatusPlayer.wrap(damagerPlayer.getUniqueId()).getStatus();

        //The defender must be lawful
        if (!defenderStatus.equals(Status.LAWFUL)) //If defender is not lawful ignore.
            return;
        //The damager must be a criminal or lawful
        if (damagerStatus.equals(Status.MURDERER))
            return;

        Clan clan = LostShardPlugin.getClanManager().getClan(damagerPlayer.getUniqueId());
        //if clan is not null and other guy is in clan
        if (clan != null && clan.isInThisClan(defenderPlayer.getUniqueId()))
            return;

        StatusPlayer.wrap(damagerPlayer.getUniqueId()).setStatus(Status.CRIMINAL);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (this.isCancelled())
                    return;
                if (playersCriminal.get(damagerPlayer.getUniqueId()) == null)
                    return;
                StatusPlayer.wrap(damagerPlayer.getUniqueId()).setStatus(Status.LAWFUL);
                playersCriminal.remove(damagerPlayer.getUniqueId());
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 60 * 5);

        if (playersCriminal.get(damagerPlayer.getUniqueId()) != null) {
            playersCriminal.get(damagerPlayer.getUniqueId()).cancel();
        }
        playersCriminal.put(damagerPlayer.getUniqueId(), task);
    }

    private void guardKnockback(Player player) {
        NPC guardNPC = GuardNPC.getNearestGuard(player.getLocation());
        GuardTrait guardTrait = guardNPC.getTrait(GuardTrait.class);
        if (guardNPC == null) {
            return;
        }

        if (!StatusPlayer.wrap(player.getUniqueId()).hasNearbyEnemyRange(5)) {
            return;
        }


        guardTrait.setCalled(true);
        guardTrait.setBusy(true);
        guardTrait.setOwner(player.getUniqueId());
        guardTrait.setCachedOwnerName(player.getName());
        guardNPC.teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        guardNPC.faceLocation(player.getEyeLocation());

        new BukkitRunnable() {

            @Override
            public void run() {
                if(guardNPC.isSpawned())
                {
                    if(guardNPC.getEntity() instanceof Player)
                    {
                        int height=3,range=20;
                        Vector vector;
                        switch (new Random().nextInt(4))
                        {
                            case 0:
                               vector = new Vector(range,height,range);
                                break;
                            case 1:
                                vector = new Vector(-range,height,range);
                                break;
                            case 2:
                                vector = new Vector(range,height,-range);
                                break;
                            case 3:
                            default:
                                vector = new Vector(-range,height,-range);
                        }

                        player.setVelocity(vector);
                    }
                }

                int curX = guardNPC.getStoredLocation().getBlockX();
                int postX = guardTrait.getGuardingLocation().getBlockX();
                int curY = guardNPC.getStoredLocation().getBlockY();
                int postY = guardTrait.getGuardingLocation().getBlockY();
                int curZ = guardNPC.getStoredLocation().getBlockZ();
                int postZ = guardTrait.getGuardingLocation().getBlockZ();

                guardNPC.getStoredLocation().getWorld().spawnParticle(Particle.BARRIER, new Location(guardNPC.getStoredLocation().getWorld(), guardNPC.getStoredLocation().getBlockX() + 0.5, guardNPC.getStoredLocation().getBlockY() + 3, guardNPC.getStoredLocation().getBlockZ() + 0.5), 1);
                if (curX != postX && curY != postY && curZ != postZ) {
                    guardNPC.getStoredLocation().getWorld().playSound(guardNPC.getStoredLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 0);
                }
            }
        }.runTaskLater(LostShardPlugin.plugin, 20);

        new BukkitRunnable() {
            @Override
            public void run() {

                int curX = guardNPC.getStoredLocation().getBlockX();
                int postX = guardTrait.getGuardingLocation().getBlockX();
                int curY = guardNPC.getStoredLocation().getBlockY();
                int postY = guardTrait.getGuardingLocation().getBlockY();
                int curZ = guardNPC.getStoredLocation().getBlockZ();
                int postZ = guardTrait.getGuardingLocation().getBlockZ();

                if (curX != postX && curY != postY && curZ != postZ) {
                    guardNPC.teleport(guardTrait.getGuardingLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    guardTrait.setCalled(false);
                    guardTrait.setOwner(null);
                    guardTrait.setCachedOwnerName("");
                    guardTrait.setBusy(false);

                }

            }

        }.runTaskLater(LostShardPlugin.plugin, 20*2);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onKill(PlayerDeathEvent event) {
        Entity defender = event.getEntity();
        if (!(defender instanceof Player)) //Must be a player
            return;

        Player defenderPlayer = (Player) defender;
        if (defenderPlayer.getKiller() == null)
            return;
        Entity damager = defenderPlayer.getKiller();

        if (CitizensAPI.getNPCRegistry().isNPC(damager) || CitizensAPI.getNPCRegistry().isNPC(defender))
            return;

        if (!isPlayerInduced(defender, damager))
            return;
        Player damagerPlayer = getPlayerInduced(defender, damager);

        if (defenderPlayer.equals(damagerPlayer))
            return;

        if (Staff.isStaff(damagerPlayer.getUniqueId()) || Staff.isStaff(defenderPlayer.getUniqueId()))
            return;


        StatusPlayer damagerStatusPlayer = StatusPlayer.wrap(damagerPlayer.getUniqueId());
        Status defenderStatus = StatusPlayer.wrap(defenderPlayer.getUniqueId()).getStatus();
        Status damagerStatus = damagerStatusPlayer.getStatus();

        if (damagerStatus.equals(Status.LAWFUL) && (defenderStatus.equals(Status.MURDERER) || defenderStatus.equals(Status.CRIMINAL)))
            return;

        Clan clan = LostShardPlugin.getClanManager().getClan(damagerPlayer.getUniqueId());
        //if clan is not null and other guy is in clan
        if (clan != null && clan.isInThisClan(defenderPlayer.getUniqueId()))
            return;

        int newKills = damagerStatusPlayer.getKills() + 1;

        if (newKills == 5) {
            damagerStatusPlayer.setStatus(Status.MURDERER);
            playersCriminal.remove(damagerPlayer.getUniqueId());
        }
        damagerStatusPlayer.setKills(newKills);

    }

    public static void onHit(Entity defender, Entity damager) {

        if (CitizensAPI.getNPCRegistry().isNPC(damager) || CitizensAPI.getNPCRegistry().isNPC(defender))
            return;

        if (!isPlayerInduced(defender, damager))
            return;

        //Entties are players
        Player damagerPlayer = getPlayerInduced(defender, damager);
        Player defenderPlayer = (Player) defender;

        if (damagerPlayer.equals(defenderPlayer))
            return;

        if (Staff.isStaff(damagerPlayer.getUniqueId()) || Staff.isStaff(defenderPlayer.getUniqueId()))
            return;

        if (defender.isDead())
            return;

        //Get each players' status
        Status defenderStatus = StatusPlayer.wrap(defenderPlayer.getUniqueId()).getStatus();
        Status damagerStatus = StatusPlayer.wrap(damagerPlayer.getUniqueId()).getStatus();

        //The defender must be lawful
        if (!defenderStatus.equals(Status.LAWFUL)) //If defender is not lawful ignore.
            return;
        //The damager must be a criminal or lawful
        if (damagerStatus.equals(Status.MURDERER))
            return;

        StatusPlayer.wrap(damagerPlayer.getUniqueId()).setStatus(Status.CRIMINAL);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (this.isCancelled())
                    return;
                if (playersCriminal.get(damagerPlayer.getUniqueId()) == null)
                    return;
                StatusPlayer.wrap(damagerPlayer.getUniqueId()).setStatus(Status.LAWFUL);
                playersCriminal.remove(damagerPlayer.getUniqueId());
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 60 * 5);

        if (playersCriminal.get(damagerPlayer.getUniqueId()) != null) {
            playersCriminal.get(damagerPlayer.getUniqueId()).cancel();
        }
        playersCriminal.put(damagerPlayer.getUniqueId(), task);
    }

    public static void onDeath(Entity defender) {
        if (!(defender instanceof Player)) //Must be a player
            return;

        Player defenderPlayer = (Player) defender;
        if (defenderPlayer.getKiller() == null)
            return;
        Entity damager = defenderPlayer.getKiller();

        if (CitizensAPI.getNPCRegistry().isNPC(damager) || CitizensAPI.getNPCRegistry().isNPC(defender))
            return;

        if (!isPlayerInduced(defender, damager))
            return;
        Player damagerPlayer = getPlayerInduced(defender, damager);

        if (defenderPlayer.equals(damagerPlayer))
            return;

        if (Staff.isStaff(damagerPlayer.getUniqueId()) || Staff.isStaff(defenderPlayer.getUniqueId()))
            return;


        StatusPlayer damagerStatusPlayer = StatusPlayer.wrap(damagerPlayer.getUniqueId());
        Status defenderStatus = StatusPlayer.wrap(defenderPlayer.getUniqueId()).getStatus();
        Status damagerStatus = damagerStatusPlayer.getStatus();

        if (damagerStatus.equals(Status.LAWFUL) && (defenderStatus.equals(Status.MURDERER) || defenderStatus.equals(Status.CRIMINAL)))
            return;

        int newKills = damagerStatusPlayer.getKills() + 1;

        if (newKills == 5) {
            damagerStatusPlayer.setStatus(Status.MURDERER);
            playersCriminal.remove(damagerPlayer.getUniqueId());
        }
        damagerStatusPlayer.setKills(newKills);
    }

    public static HashMap<UUID, BukkitTask> getPlayersCriminals() {
        return playersCriminal;
    }
}
