package com.github.kotooriiii.status;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

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

        ShardScoreboardManager.add(Bukkit.getOfflinePlayer(playerUUID), status.getName());
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
        this.status = status;
        save();

        if(Staff.isStaff(offlinePlayer.getUniqueId()))
            return;

        ShardScoreboardManager.add(offlinePlayer, status.getName());
    }

    public void save() {
        FileManager.write(this);
    }

    public static StatusPlayer wrap(UUID playerUUID) {
        return playerStatus.get(playerUUID);
    }

    public boolean hasNearbyEnemy(final int range) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.isOnline())
            return false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(offlinePlayer.getPlayer().getLocation()) <= range) {
                StatusPlayer statusPlayer = StatusPlayer.wrap(player.getUniqueId());
                if (!statusPlayer.getStatus().equals(Status.WORTHY))
                    return true;
            }
        }
        return false;
    }

    public static HashMap<UUID, StatusPlayer> getPlayerStatus() {
        return playerStatus;
    }

    public static ArrayList<StatusPlayer> getCorrupts() {
        ArrayList<StatusPlayer> corrupts = new ArrayList<>();
        for (StatusPlayer statusPlayer : getPlayerStatus().values()) {
            if (statusPlayer.getStatus().equals(Status.CORRUPT))
                corrupts.add(statusPlayer);
        }
        return corrupts;
    }
}