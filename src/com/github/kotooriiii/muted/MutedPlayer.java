package com.github.kotooriiii.muted;

import com.github.kotooriiii.files.FileManager;
import com.google.gson.internal.$Gson$Preconditions;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MutedPlayer {

    private UUID mutedUUID;
    private ZonedDateTime bannedTime;

    private static HashMap<UUID, MutedPlayer> mutedPlayers = new HashMap<>();

    public MutedPlayer(UUID mutedUUID, ZonedDateTime bannedTime)
    {
        this.mutedUUID = mutedUUID;
        this.bannedTime = bannedTime;
    }

    public MutedPlayer(UUID mutedUUID)
    {
        this.mutedUUID = mutedUUID;
    }

    public UUID getMutedUUID()
    {
        return mutedUUID;
    }

    public ZonedDateTime getBannedTime()
    {
        return this.bannedTime;
    }

    public void add()
    {
        mutedPlayers.put(this.mutedUUID, this);
    }

    public void save()
    {
        FileManager.write(this);
    }

    public void remove()
    {
        FileManager.removeFile(this);
        mutedPlayers.remove(this.mutedUUID);
    }

    public static HashMap<UUID, MutedPlayer> getMutedPlayers() {
        return mutedPlayers;
    }
}
