package com.github.kotooriiii.bannedplayer;

import com.github.kotooriiii.files.FileManager;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BannedPlayer implements Serializable {

    private static final long serialVersionUID = 1L;


    private UUID playerUUID;
    private ZonedDateTime unbanDate;
    private String bannedMessage;

    public BannedPlayer(UUID playerUUID, ZonedDateTime zdt, String bannedMessage) {
        this.playerUUID = playerUUID;
        this.unbanDate = zdt;
        this.bannedMessage = bannedMessage;
    }


    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public ZonedDateTime getUnbanDate() {
        return unbanDate;
    }

    public String getBannedMessage() {
        return bannedMessage;
    }

}
