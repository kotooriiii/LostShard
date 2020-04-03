package com.github.kotooriiii.status;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.ScoreboardManager;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.util.HelperMethods.getPlayerInduced;
import static com.github.kotooriiii.util.HelperMethods.isPlayerInduced;

public class StatusUpdateListener implements Listener {

    private static HashMap<UUID, BukkitTask> playersCorrupt = new HashMap<>();


    @EventHandler(priority = EventPriority.LOWEST)
    public void onCorruptTimer(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity defender = event.getEntity();

        if (!isPlayerInduced(defender, damager))
            return;

        //Entties are players
        Player damagerPlayer = getPlayerInduced(defender, damager);
        Player defenderPlayer = (Player) defender;

        if (Staff.isStaff(damagerPlayer.getUniqueId()) || Staff.isStaff(defenderPlayer.getUniqueId()))
            return;

        if (defender.isDead())
            return;

        //Get each players' status
        Status defenderStatus = StatusPlayer.wrap(defenderPlayer.getUniqueId()).getStatus();
        Status damagerStatus = StatusPlayer.wrap(damagerPlayer.getUniqueId()).getStatus();

        //The defender must be worthy
        if (!defenderStatus.equals(Status.WORTHY)) //If defender is not worthy ignore.
            return;
        //The damager must be corrupt or worthy
        if (damagerStatus.equals(Status.EXILED))
            return;

        StatusPlayer.wrap(damagerPlayer.getUniqueId()).setStatus(Status.CORRUPT);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (this.isCancelled())
                    return;
                if (playersCorrupt.get(damagerPlayer.getUniqueId()) == null)
                    return;
                StatusPlayer.wrap(damagerPlayer.getUniqueId()).setStatus(Status.WORTHY);
                playersCorrupt.remove(damagerPlayer.getUniqueId());
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 60 * 5);

        if (playersCorrupt.get(damagerPlayer.getUniqueId()) != null) {
            playersCorrupt.get(damagerPlayer.getUniqueId()).cancel();
        }
        playersCorrupt.put(damagerPlayer.getUniqueId(), task);
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

        if (!isPlayerInduced(defender, damager))
            return;
        Player damagerPlayer = getPlayerInduced(defender, damager);

        if (Staff.isStaff(damagerPlayer.getUniqueId()) || Staff.isStaff(defenderPlayer.getUniqueId()))
            return;


        StatusPlayer damagerStatusPlayer = StatusPlayer.wrap(damagerPlayer.getUniqueId());
        Status defenderStatus = StatusPlayer.wrap(defenderPlayer.getUniqueId()).getStatus();
        Status damagerStatus = damagerStatusPlayer.getStatus();

        if (damagerStatus.equals(Status.WORTHY) && (defenderStatus.equals(Status.EXILED) || defenderStatus.equals(Status.CORRUPT)))
            return;

        damagerStatusPlayer.setKills(damagerStatusPlayer.getKills() + 1);
        if (damagerStatusPlayer.getKills() == 5) {
            damagerStatusPlayer.setStatus(Status.EXILED);
            playersCorrupt.remove(damagerPlayer.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (StatusPlayer.getPlayerStatus().get(uuid) == null) {
            StatusPlayer statusPlayer = new StatusPlayer(uuid, Status.WORTHY, 0);
            statusPlayer.save();
        }
        ShardScoreboardManager.registerScoreboard(event.getPlayer());

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        StatusPlayer statusPlayer = StatusPlayer.getPlayerStatus().get(uuid);
        Status status = statusPlayer.getStatus();
        //  LostShardPlugin.getScoreboard().getTeam(status.getName()).removePlayer(Bukkit.getOfflinePlayer(uuid));
    }

    public static HashMap<UUID, BukkitTask> getPlayersCorrupt() {
        return playersCorrupt;
    }
}
