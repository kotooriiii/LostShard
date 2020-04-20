package com.github.kotooriiii.ranks;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.status.Status;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RankPlayer {
    private static HashMap<UUID, RankPlayer> rankPlayerHashMap = new HashMap<>();

    private UUID uuid;
    private RankType rankType;

    public RankPlayer(UUID playerUUID, RankType rankType) {
        this.uuid = playerUUID;
        this.rankType = rankType;
        rankPlayerHashMap.put(playerUUID, this);

        ShardScoreboardManager.add(Bukkit.getOfflinePlayer(playerUUID), rankType.getName());
    }

    public UUID getPlayerUUID() {
        return uuid;
    }

    public RankType getRankType() {
        return rankType;
    }

    public void setRankType(RankType rankType) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        this.rankType = rankType;
        save();

        if(Staff.isStaff(offlinePlayer.getUniqueId()))
            return;

        ShardScoreboardManager.add(offlinePlayer, rankType.getName());
    }

    public void save() {
        FileManager.write(this);
    }

    public boolean isDonator()
    {
        switch (rankType)
        {
            case SUBSCRIBER:
            case SUBSCRIBER_PLUS:
                return true;
            case DEFAULT:
                return false;
        }
        return false;
    }

    public String getChannelContent(String channelName)
    {
        return rankType.getPrefixContent() + channelName + rankType.getSuffixContent();
    }

    public static RankPlayer wrap(UUID playerUUID) {
        return rankPlayerHashMap.get(playerUUID);
    }

    public static HashMap<UUID, RankPlayer> getRankPlayerMap() {
        return rankPlayerHashMap;
    }

}
