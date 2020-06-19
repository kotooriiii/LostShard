package com.github.kotooriiii.ranks;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.print.DocFlavor;
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
        update();

        save();

        if (Staff.isStaff(offlinePlayer.getUniqueId()))
            return;


    }

    private void update()
    {
        MarkPlayer markPlayer = MarkPlayer.wrap(getPlayerUUID());
        if(markPlayer == null || markPlayer.getMarks() == null)
        {
            //Don't worry about this
        } else {

            int size = -1;
            if(rankType.getMaxMarksNum() < markPlayer.getMarks().length)
            {
                size = rankType.getMaxMarksNum();
            } else {
                size = markPlayer.getMarks().length;
            }
            MarkPlayer.Mark[] leftoverMarks = new MarkPlayer.Mark[size];

                for (int i = 0; i < size; i++) {
                    leftoverMarks[i] = markPlayer.getMarks()[i];
                }

            markPlayer.setMarks(leftoverMarks);
        }

       Bank bank =  LostShardPlugin.getBankManager().wrap(getPlayerUUID());
        if(bank == null || bank.getInventory() == null)
        {
            //Don't worry about this
        } else {

        }

    }

    public void save() {
        FileManager.write(this);
    }

    public boolean isDonator() {
        switch (rankType) {
            case SUBSCRIBER:
            case SUBSCRIBER_PLUS:
                return true;
            case DEFAULT:
                return false;
        }
        return false;
    }

    public String getChannelContent(String channelName) {
        return rankType.getPrefixContent() + channelName + rankType.getSuffixContent();
    }

    public static RankPlayer wrap(UUID playerUUID) {
        return rankPlayerHashMap.get(playerUUID);
    }

    public static HashMap<UUID, RankPlayer> getRankPlayerMap() {
        return rankPlayerHashMap;
    }

}
