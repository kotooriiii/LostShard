package com.github.kotooriiii.sorcery.spells;

import com.github.kotooriiii.files.FileManager;

import java.util.HashMap;
import java.util.UUID;

public class SorceryManager {
    private HashMap<UUID, SorceryPlayer> sorceryPlayerHashMap;

    public SorceryManager()
    {
        this.sorceryPlayerHashMap = new HashMap<>();
    }

    public void addSorceryPlayer(SorceryPlayer sorceryPlayer, boolean saveToFile)
    {
        sorceryPlayerHashMap.put(sorceryPlayer.getUUID(), sorceryPlayer);
        if(saveToFile)
            saveFile(sorceryPlayer);
    }

    public void saveFile(SorceryPlayer sorceryPlayer)
    {
        FileManager.write(sorceryPlayer);
    }

    public void removeSorceryPlayer(SorceryPlayer sorceryPlayer)
    {
        sorceryPlayerHashMap.remove(sorceryPlayer.getUUID());
        FileManager.removeFile(sorceryPlayer);
    }

    public boolean isSorceryPlayer(UUID uuid)
    {
        return wrap(uuid) != null;
    }

    public SorceryPlayer wrap(UUID uuid)
    {
        return sorceryPlayerHashMap.get(uuid);
    }
}
