package com.github.kotooriiii.channels;

import com.github.kotooriiii.files.FileManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class IgnoreManager {
    private HashMap<UUID, IgnorePlayer> ignoreMap;

    public IgnoreManager() {
        this.ignoreMap = new HashMap<>();
    }

    public void addIgnorePlayer(IgnorePlayer ignorePlayer, boolean saveToFile) {
        ignoreMap.put(ignorePlayer.getSource(), ignorePlayer);
        if (saveToFile)
        save(ignorePlayer);
    }

    public void save(IgnorePlayer ignorePlayer) {
        FileManager.write(ignorePlayer);
    }


    public IgnorePlayer wrap(UUID uuid)
    {
        return ignoreMap.get(uuid);
    }

    public Collection<IgnorePlayer> getIgnorePlayers() {
        return ignoreMap.values();
    }

}
