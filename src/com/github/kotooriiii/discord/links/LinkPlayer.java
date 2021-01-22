package com.github.kotooriiii.discord.links;

import com.github.kotooriiii.files.FileManager;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class LinkPlayer implements Serializable {
    private String userSnowflake;
    private UUID playerUUID;

    private static final HashMap<String, LinkPlayer> snowflakeLinkMap = new HashMap<>();
    private static final HashMap<UUID, LinkPlayer> uuidLinkMap = new HashMap<>();


    public LinkPlayer(String userSnowflake, UUID playerUUID) {
        this.userSnowflake = userSnowflake;
        this.playerUUID = playerUUID;
    }

    public void addToMap() {
        if (isOpted())
            snowflakeLinkMap.put(userSnowflake, this);
        uuidLinkMap.put(playerUUID, this);
    }

    public String getUserSnowflake() {
        return userSnowflake;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean isOpted() {
        return userSnowflake != null;
    }


    public void save() {
        if (isOpted())
            snowflakeLinkMap.put(userSnowflake, this);
        uuidLinkMap.put(playerUUID, this);
        FileManager.write(this);
    }

    public void remove() {
        if (isOpted())
            snowflakeLinkMap.remove(userSnowflake, this);
        uuidLinkMap.remove(playerUUID, this);
        FileManager.removeFile(this);
    }

    public static boolean isLinked(String snowflake) {
        return snowflakeLinkMap.containsKey(snowflake);
    }

    public static boolean isLinked(UUID playerUUID) {
        return uuidLinkMap.containsKey(playerUUID);
    }

    public static LinkPlayer ofSnowflake(String snowflake) {
        return snowflakeLinkMap.get(snowflake);
    }

    public static LinkPlayer ofUUID(UUID playerUUID) {
        return uuidLinkMap.get(playerUUID);
    }

}
