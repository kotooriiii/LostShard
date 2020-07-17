package com.github.kotooriiii.skills;

import com.github.kotooriiii.files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class SkillPlayer {

    private UUID playerUUID;
    private SkillBuild[] skillBuilds;
    public static int MAX_BUILDS = 2;
    private int activeBuildIndex;

    public SkillPlayer(UUID playerUUID) {

        this.playerUUID = playerUUID;
        this.skillBuilds = new SkillBuild[]{new SkillBuild(this), new SkillBuild(this)};
        this.activeBuildIndex = 0;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public SkillBuild[] getSkillBuilds() {
        SkillBuild[] clones = new SkillBuild[this.skillBuilds.length];
        for (int i = 0; i < clones.length; i++) {
            clones[i] = skillBuilds[i];
        }
        return clones;
    }

    public void rotate() {
        int y = activeBuildIndex;
        y = y + 1; //try for next rotation
        if (y == MAX_BUILDS)
            y = 0;
        setActiveBuild(y);

    }

    public SkillBuild getActiveBuild() {
        return skillBuilds[activeBuildIndex];
    }

    public void setActiveBuild(int index) {
        this.activeBuildIndex = index;
    }

    public void setSkillBuilds(SkillBuild[] builds) {
        this.skillBuilds = builds;
    }

    public int getActiveIndex() {
        return this.activeBuildIndex;
    }


}
