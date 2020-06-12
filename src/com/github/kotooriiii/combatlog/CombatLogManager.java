package com.github.kotooriiii.combatlog;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CombatLogManager {

    //Logged player w timer task
    private static HashMap<UUID, BukkitTask> combatLoggedPlayerTask = new HashMap<>();
    //Logged player IN CASE they die, look at this
    private static HashMap<UUID, CombatTaggedPlayer> combatTaggedPlayersInvolved = new HashMap<>();

    private final int SECONDS_TAGGED = 30;


    public CombatLogManager() {

    }

    public void remove(UUID playerUUID) {
        BukkitTask oldTask = combatLoggedPlayerTask.get(playerUUID);
        if (oldTask == null) {
            return;
        }

        oldTask.cancel();
        combatLoggedPlayerTask.remove(playerUUID);
        combatTaggedPlayersInvolved.remove(playerUUID);
    }

    public void add(UUID defenderUUID, UUID attackerUUID) {
        //Remove old task if there was one
        BukkitTask oldTask = combatLoggedPlayerTask.get(defenderUUID);

        //Combat logged before
        if (oldTask != null) {
            oldTask.cancel();
        } else {
            //Just now getting combat logged for the first time
            sendTagMessage(defenderUUID, attackerUUID);

        }

        BukkitTask task = new BukkitRunnable() {

            private int counter = 0;

            @Override
            public void run() {

                if (isCancelled())
                    return;

                if (counter == SECONDS_TAGGED) {
                    combatLoggedPlayerTask.remove(defenderUUID);
                    combatTaggedPlayersInvolved.remove(defenderUUID);
                    this.cancel();
                    sendUntagMessage(defenderUUID, attackerUUID);
                    return;
                }
                counter++;
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, 20);

        combatLoggedPlayerTask.put(defenderUUID, task);

        CombatTaggedPlayer taggedPlayer = combatTaggedPlayersInvolved.get(defenderUUID);
        if (taggedPlayer == null) {
            taggedPlayer = new CombatTaggedPlayer(defenderUUID);
        }
        if (!taggedPlayer.isAttacker(attackerUUID))
            taggedPlayer.addAttacker(attackerUUID);
        combatTaggedPlayersInvolved.put(defenderUUID, taggedPlayer);
    }

    private void sendTagMessage(UUID defenderUUID, UUID attackerUUID) {
        ChatColor color = ChatColor.RED;

        Player defenderPlayer = Bukkit.getPlayer(defenderUUID);
        Player damagerPlayer = Bukkit.getPlayer(attackerUUID);

        if (defenderPlayer != null && damagerPlayer != null)
            defenderPlayer.sendMessage(color + "You've been combat tagged by " + StatusPlayer.wrap(damagerPlayer.getUniqueId()).getStatus().getChatColor() + damagerPlayer.getName() + color + ".");
    }


    private void sendUntagMessage(UUID defenderUUID, UUID attackerUUID) {
        ChatColor color = ChatColor.RED;

        Player defenderPlayer = Bukkit.getPlayer(defenderUUID);
        Player damagerPlayer = Bukkit.getPlayer(attackerUUID);

        if (defenderPlayer != null && damagerPlayer != null)
            defenderPlayer.sendMessage(color + "You're no longer combat tagged.");
    }

    public boolean isTagged(UUID playerUUID) {
        return combatLoggedPlayerTask.containsKey(playerUUID);
    }

    public CombatTaggedPlayer wrap(UUID playerUUID) {
        return combatTaggedPlayersInvolved.get(playerUUID);
    }
}
