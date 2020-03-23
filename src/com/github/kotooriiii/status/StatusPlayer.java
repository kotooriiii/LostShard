package com.github.kotooriiii.status;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class StatusPlayer {
    private static HashMap<UUID, StatusPlayer> playerStatus = new HashMap<>();

    private UUID uuid;
    private Status status;
    private int kills;

    public StatusPlayer(UUID playerUUID, Status status, int kills) {
        this.uuid = playerUUID;
        this.status = status;
        this.kills = kills;
        playerStatus.put(playerUUID, this);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String name = offlinePlayer.getName();

        LostShardPlugin.getScoreboard().getTeam(getStatus().getName()).addPlayer(offlinePlayer);

        if(offlinePlayer.isOnline())
            offlinePlayer.getPlayer().setScoreboard(LostShardPlugin.getScoreboard());
    }

    public UUID getPlayerUUID() {
        return uuid;
    }

    public Status getStatus() {
        return status;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        save();
    }

    public void setStatus(Status status) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String name = offlinePlayer.getName();

        LostShardPlugin.getScoreboard().getTeam(getStatus().getName()).addPlayer(offlinePlayer);

        this.status = status;
        LostShardPlugin.getScoreboard().getTeam(getStatus().getName()).addPlayer(offlinePlayer);

        if(offlinePlayer.isOnline())
            offlinePlayer.getPlayer().setScoreboard(LostShardPlugin.getScoreboard());

        save();
    }

    public void save()
    {
        FileManager.write(this);
    }

    public static StatusPlayer wrap(UUID playerUUID)
    {
        return playerStatus.get(playerUUID);
    }

    public boolean hasNearbyEnemy(final int range)
    {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if(!offlinePlayer.isOnline())
            return false;

        for(Player player : Bukkit.getOnlinePlayers())
        {
            if(player.getLocation().distance(offlinePlayer.getPlayer().getLocation()) <= range)
            {
                StatusPlayer statusPlayer = StatusPlayer.wrap(player.getUniqueId());
                if(!statusPlayer.getStatus().equals(Status.WORTHY))
                    return true;
            }
        }
        return false;
    }

    public static HashMap<UUID, StatusPlayer> getPlayerStatus() {
        return playerStatus;
    }

    public static ArrayList<StatusPlayer> getCorrupts()
    {
        ArrayList<StatusPlayer> corrupts = new ArrayList<>();
        for(StatusPlayer statusPlayer : getPlayerStatus().values())
        {
            if(statusPlayer.getStatus().equals(Status.CORRUPT))
                corrupts.add(statusPlayer);
        }
        return corrupts;
    }
}
