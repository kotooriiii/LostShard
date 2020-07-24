package com.github.kotooriiii.bannedplayer;

import com.github.kotooriiii.files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BanManager {

    private HashMap<UUID, BannedPlayer> map;

    public BanManager() {
        map = new HashMap<>();
    }

    public void ban(BannedPlayer bannedPlayer, boolean isKick, boolean saveToFile) {

        if (isBanned(bannedPlayer.getPlayerUUID()))
            return;

        Player player = Bukkit.getPlayer(bannedPlayer.getPlayerUUID());
        if (isKick)
            if (player != null && player.isOnline())
                player.kickPlayer(bannedPlayer.getBannedMessage());

        map.put(bannedPlayer.getPlayerUUID(), bannedPlayer);
        if (saveToFile)
            save(bannedPlayer);
    }

    public void unban(BannedPlayer bannedPlayer) {
        map.remove(bannedPlayer.getPlayerUUID());
        FileManager.removeFile(bannedPlayer);
    }

    public void unban(UUID uuid) {
        BannedPlayer bannedPlayer = this.getBannedPlayer(uuid);
        if (bannedPlayer == null)
            return;
        unban(bannedPlayer);
    }

    public void save(BannedPlayer bannedPlayer) {
        FileManager.write(bannedPlayer);
    }

    public boolean isBanned(UUID uuid) {
        return map.containsKey(uuid);
    }

    public BannedPlayer getBannedPlayer(UUID uuid) {
        return map.get(uuid);
    }


    public void setBannedPlayers(HashMap<UUID, BannedPlayer> bannedPlayers) {
        this.map = bannedPlayers;
    }

    public BannedPlayer[] getBannedPlayers() {
        return this.map.values().toArray(new BannedPlayer[this.map.values().size()]);
    }
}
