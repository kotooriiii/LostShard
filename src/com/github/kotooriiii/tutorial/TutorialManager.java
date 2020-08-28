package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;

import java.util.HashMap;
import java.util.UUID;

public class TutorialManager {

    private HashMap<UUID, TutorialProgression> players;

    public TutorialManager() {
        players = new HashMap<>();
        LostShardPlugin.plugin.getServer().getPluginManager().registerEvents(new PlayerJoinSendTitleListener(), LostShardPlugin.plugin);
    }

    public TutorialProgression addPlayer(UUID uuid) {
        TutorialProgression tp = new TutorialProgression(uuid);
        players.put(uuid, tp);
        return tp;
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public TutorialProgression wrap(UUID uuid) {
        return players.get(uuid);
    }
}
