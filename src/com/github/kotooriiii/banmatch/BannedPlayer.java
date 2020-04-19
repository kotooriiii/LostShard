package com.github.kotooriiii.banmatch;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class BannedPlayer implements Serializable {

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
