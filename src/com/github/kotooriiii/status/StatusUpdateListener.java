package com.github.kotooriiii.status;

import com.github.kotooriiii.LostShardPlugin;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.util.HelperMethods.getPlayerInduced;
import static com.github.kotooriiii.util.HelperMethods.isPlayerInduced;

public class StatusUpdateListener implements Listener {

    private static HashMap<UUID, BukkitTask> playersCriminal = new HashMap<>();


    @EventHandler(priority = EventPriority.MONITOR)
    public void onCriminalTimer(EntityDamageByEntityEvent event) {

        if(event.isCancelled())
            return;

        Entity damager = event.getDamager();
        Entity defender = event.getEntity();

        if(CitizensAPI.getNPCRegistry().isNPC(damager) || CitizensAPI.getNPCRegistry().isNPC(defender))
            return;

        if (!isPlayerInduced(defender, damager))
            return;

        //Entties are players
        Player damagerPlayer = getPlayerInduced(defender, damager);
        Player defenderPlayer = (Player) defender;

        if(damagerPlayer.equals(defenderPlayer))
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

    @EventHandler(priority = EventPriority.LOW)
    public void onKill(PlayerDeathEvent event) {
        Entity defender = event.getEntity();
        if (!(defender instanceof Player)) //Must be a player
            return;

        Player defenderPlayer = (Player) defender;
        if (defenderPlayer.getKiller() == null)
            return;
        Entity damager = defenderPlayer.getKiller();

        if(CitizensAPI.getNPCRegistry().isNPC(damager) || CitizensAPI.getNPCRegistry().isNPC(defender))
            return;

        if (!isPlayerInduced(defender, damager))
            return;
        Player damagerPlayer = getPlayerInduced(defender, damager);

        if(defenderPlayer.equals(damagerPlayer))
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
