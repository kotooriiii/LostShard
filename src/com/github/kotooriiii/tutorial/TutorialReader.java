package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.sorcery.Gate;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class TutorialReader {

    private File file;
    private String id;

    public TutorialReader() {
        this.file = new File(LostShardPlugin.plugin.getDataFolder() + File.separator + "tutorialCompleted.yml");
        this.id = "playersWithTutorialCompleted";
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasCompletedTutorial(UUID uuid) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> playerValues = yaml.getStringList(id + "."  + uuid.toString());
        return playerValues != null;
    }

    public boolean isAuthentic(UUID uuid) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> playerValues = yaml.getStringList(id + "."  + uuid.toString());
        if (playerValues == null)
            return false;

        boolean isAuthentic = Boolean.valueOf(playerValues.get(1));

        return isAuthentic;
    }

    public boolean isAwarded(UUID uuid) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> playerValues = yaml.getStringList(id + "."  + uuid.toString());
        if (playerValues == null)
            return false;

        boolean isAwarded = Boolean.valueOf(playerValues.get(0));

        return isAwarded;
    }

    public void award(UUID uuid)
    {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> playerValues = yaml.getStringList(id + "."  + uuid.toString());
        if (playerValues == null)
            return;
        playerValues.set(0, true + "");
        yaml.set(id + "." + uuid.toString(), playerValues);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void completeTutorial(UUID uuid, boolean isAuthentic) {

        if(hasCompletedTutorial(uuid))
            return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        ArrayList<String> list = new ArrayList<>();
        list.add(false + "");
        list.add(isAuthentic + "");
        yaml.set(id + "." + uuid.toString(), list);

        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
