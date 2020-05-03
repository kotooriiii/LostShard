package com.github.kotooriiii.combatlog;

import com.github.kotooriiii.LostShardPlugin;
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

    public void add(UUID playerUUID, UUID attackerUUID) {
        //Remove old task if there was one
        BukkitTask oldTask = combatLoggedPlayerTask.get(playerUUID);
        if (oldTask != null) {
            oldTask.cancel();
        }

        BukkitTask task = new BukkitRunnable() {

            private int counter = 0;

            @Override
            public void run() {

                if (isCancelled())
                    return;

                if (counter == SECONDS_TAGGED) {
                    combatLoggedPlayerTask.remove(playerUUID);
                    combatTaggedPlayersInvolved.remove(playerUUID);
                    this.cancel();
                    return;
                }
                counter++;
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 20, 20);

        combatLoggedPlayerTask.put(playerUUID, task);

        CombatTaggedPlayer taggedPlayer = combatTaggedPlayersInvolved.get(playerUUID);
        if (taggedPlayer == null) {
            taggedPlayer = new CombatTaggedPlayer(playerUUID);
        }
        taggedPlayer.addAttacker(attackerUUID);
        combatTaggedPlayersInvolved.put(playerUUID, taggedPlayer);
    }

    public boolean isTagged(UUID playerUUID) {
        return combatLoggedPlayerTask.containsKey(playerUUID);
    }

    public CombatTaggedPlayer wrap(UUID playerUUID)
    {
        return combatTaggedPlayersInvolved.get(playerUUID);
    }
}
