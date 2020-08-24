package com.github.kotooriiii.channels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class IgnorePlayer {
    private UUID source;
    private HashSet<UUID> ignored;

    public IgnorePlayer(UUID source) {
        this.source = source;
        this.ignored = new HashSet<>();
    }

    public boolean ignore(UUID uuid)
    {
        return this.ignored.add(uuid);
    }

    public boolean unignore(UUID uuid)
    {
        return this.ignored.remove(uuid);
    }

    public boolean isIgnoring(UUID uuid)
    {
        return this.ignored.contains(uuid);
    }

    public UUID[] getIgnoredUUIDS()
    {
        return this.ignored.toArray(new UUID[0]);
    }

    public UUID getSource() {
        return source;
    }

    public void setIgnoredPlayers(HashSet<UUID> uuids) {
        this.ignored =uuids;
    }
}
