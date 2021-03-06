package com.github.kotooriiii.status;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.npc.type.tutorial.murderer.MurdererNPC;
import com.github.kotooriiii.npc.type.tutorial.murderer.MurdererTrait;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class StatusPlayer {
    private static HashMap<UUID, StatusPlayer> playerStatus = new HashMap<>();

    private UUID uuid;
    private Status status;
    private int kills;
    private ZonedDateTime lastAtoneDate;

    public StatusPlayer(UUID playerUUID, Status status, int kills) {
        this.uuid = playerUUID;
        this.status = status;
        this.kills = kills;
        this.lastAtoneDate = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("America/New_York"));
        playerStatus.put(playerUUID, this);

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

    public ZonedDateTime getLastAtoneDate() {
        return lastAtoneDate;
    }

    public ZonedDateTime getNextAtoneDate() {
        return lastAtoneDate.plusDays(5);
    }

    public boolean isAbleToAtone() {
        ZonedDateTime days = ZonedDateTime.now().minusDays(7);

        Instant instantDaysSubtracted = days.toInstant();
        Instant instantLastAtone = lastAtoneDate.toInstant();
        return instantLastAtone.toEpochMilli() <= instantDaysSubtracted.toEpochMilli();
    }

    public boolean hasAtonedBefore() {
        return lastAtoneDate.toInstant().equals(Instant.EPOCH);
    }

    public void setKills(int kills) {
        this.kills = kills;
        save();
    }

    public void setLastAtoneDate(ZonedDateTime lastAtoneDate) {
        this.lastAtoneDate = lastAtoneDate;
        save();
    }

    public void setStatus(Status status) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        this.status = status;
        save();

        if (Staff.isStaff(offlinePlayer.getUniqueId()))
            return;

        ShardScoreboardManager.add(offlinePlayer, status.getName());
    }

    public void save() {
        FileManager.write(this);
    }

    public static StatusPlayer wrap(UUID playerUUID) {
        return playerStatus.get(playerUUID);
    }

    public boolean hasNearbyEnemyRange(final int range) {
        Player playerOnline = Bukkit.getPlayer(uuid);
        if (playerOnline == null)
            return false;


        if (LostShardPlugin.isTutorial()) {
            boolean isFound = false;
            Iterable<NPC> npcs = MurdererNPC.getAllMurdererNPC();
            for (NPC npc : npcs) {
                LivingEntity entity = npc.getTrait(MurdererTrait.class).getTargetTutorial();
                if (entity == null)
                    continue;
                if (!entity.getUniqueId().equals(getPlayerUUID()))
                    continue;
                isFound = true;
                break;
            }
            return isFound;
        }


        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!player.getWorld().equals(playerOnline.getWorld()))
                continue;

            if (player.getLocation().distance(playerOnline.getLocation()) <= range) {
                StatusPlayer statusPlayer = StatusPlayer.wrap(player.getUniqueId());
                if (!statusPlayer.getStatus().equals(Status.LAWFUL))
                    return true;
            }
        }
        return false;
    }

    public static HashMap<UUID, StatusPlayer> getPlayerStatus() {
        return playerStatus;
    }

    public static ArrayList<StatusPlayer> getCriminals() {
        ArrayList<StatusPlayer> criminals = new ArrayList<>();
        for (StatusPlayer statusPlayer : getPlayerStatus().values()) {
            if (statusPlayer.getStatus().equals(Status.CRIMINAL))
                criminals.add(statusPlayer);
        }
        return criminals;
    }
}
