package com.github.kotooriiii.combatlog;

import com.github.kotooriiii.LostShardPlugin;
import net.minecraft.server.v1_15_R1.Packet;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class CombatLogManager {

    private static HashMap<UUID, BukkitTask> combatLogging = new HashMap<>();
    private final int SECONDS_TAGGED = 30;

    public CombatLogManager()
    {

    }

    public void remove(UUID playerUUID)
    {
        BukkitTask oldTask = combatLogging.get(playerUUID);
        if(oldTask == null)
        {
            return;
        }

        oldTask.cancel();
        combatLogging.remove(playerUUID);
    }

    public void add(UUID playerUUID)
    {
        BukkitTask oldTask = combatLogging.get(playerUUID);
        if(oldTask != null)
        {
            oldTask.cancel();
        }

        BukkitTask task = new BukkitRunnable(){

            private int counter = 0;

            @Override
            public void run() {

                if(isCancelled())
                    return;

                if(counter == SECONDS_TAGGED)
                {
                    combatLogging.remove(playerUUID);
                    this.cancel();
                    return;
                }
                counter++;
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 20 , 20);

        combatLogging.put(playerUUID, task);
    }

    public boolean isTagged(UUID playerUUID)
    {
        return combatLogging.containsKey(playerUUID);
    }

}
