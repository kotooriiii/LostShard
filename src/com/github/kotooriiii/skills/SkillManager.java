package com.github.kotooriiii.skills;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class SkillManager {
    private HashMap<UUID, SkillPlayer> skillPlayerHashMap;

    public SkillManager() {
        this.skillPlayerHashMap = new HashMap<>();
    }

    public void addSkillPlayer(SkillPlayer skillPlayer, boolean saveToFile) {
        skillPlayerHashMap.put(skillPlayer.getPlayerUUID(), skillPlayer);
        if (saveToFile)
            saveSkillPlayer(skillPlayer);
    }

    public void saveSkillPlayer(SkillPlayer skillPlayer) {
        FileManager.write(skillPlayer);
    }

    public Collection<SkillPlayer> getSkillPlayers()
    {
        return skillPlayerHashMap.values();
    }

    public SkillPlayer getSkillPlayer(UUID uuid)
    {
        return skillPlayerHashMap.get(uuid);
    }

}
