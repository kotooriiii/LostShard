package com.github.kotooriiii.skills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class Skill  {

    private SkillBuild parentBuild;

    private final SkillType type;
    private float xp;
    private float level;
    private final static float maxLevel = 100;
    private final static int CONSTANT = 75;

    public Skill(SkillBuild parentBuild, SkillType type) {
        this.parentBuild = parentBuild;
        this.type = type;
        this.xp = 0;
        this.level = 0;
    }


    public SkillType getType() {
        return type;
    }


    /*

    XP Functions

     */

    /**
     * Adds XP to this skill.
     *
     * @param addedXP the amount of xp to be added
     * @return true if the player is promoted in this branch of skills or false if not promoted.
     */
    public boolean addXP(float addedXP) {

        //The cap for this skill is reached
        if (level == maxLevel)
            return false;

        //Total xp is reached
        if (parentBuild.isMaxBuild())
            return false;

        //Add XP
        this.xp += addedXP;

        float randomSkillPoint = getRandomSkillPoint();

        //Should we level up?
        if (this.xp >= getMaxXP()) {

            boolean isLeveledUp = addLevels(randomSkillPoint);


            if (isLeveledUp) {
                float leftoverXP = this.xp - getMaxXPOf(this.level);
                this.xp = leftoverXP;
            } else {
                float leftoverXP = this.xp - getMaxXP();
                this.xp = leftoverXP;
            }

            sendLevelUpMessage(randomSkillPoint);

            if (this.xp < 0) this.xp = 0;
            return isLeveledUp;
        }
        return false;
    }

    private float getRandomSkillPoint() {

        float[] chances = new float[]
                {
                        0.1f,
                        0.2f,
                        0.3f,
                        0.4f,
                        0.5f,
                        0.6f,
                        0.7f,
                        0.8f,
                        0.9f,
                        1.0f,
                        1.1f,
                        1.2f,
                        1.3f,
                        1.4f,
                        1.5f,
                };

        Random random = new Random();
        int ranNum = random.nextInt(chances.length);
        return chances[ranNum];
    }

    /**
     * Gets the max total xp of the current level.
     *
     * @return max xp of current level
     */
    public float getMaxXP() {
        int roundDown = (int) Math.floor(level + 1);

        return (float) Math.floor(CONSTANT * Math.sqrt(roundDown));
    }

    public float getMaxLevel() {
        return maxLevel;
    }

    /**
     * Gets the max total XP of a certain level
     *
     * @param ofLevel level to check max xp of
     * @return max xp of level
     */
    private float getMaxXPOf(float ofLevel) {
        int roundDown = (int) Math.floor(ofLevel);


        return (float) Math.floor(CONSTANT * Math.sqrt(roundDown));
    }

    /**
     * Gets the total XP of the skill.
     *
     * @return total XP
     */
    public float getXP() {
        return xp;
    }

    /*

    Level Functions

     */

    /**
     * Adds a certain amount of levels to the skill.
     *
     * @param levels the amount of levels to be added
     */
    private boolean addLevels(float levels) {
        int oldLevel = (int) Math.floor(this.level);
        this.level += levels;
        this.level = new BigDecimal(level).setScale(1, RoundingMode.HALF_UP).floatValue();
        int newLevel = (int) Math.floor(this.level);

        if (this.level > 100) {
            this.level = 100;
        }

        return oldLevel != newLevel;
    }

    /**
     * Get the level of the skill.
     *
     * @return level of skill
     */
    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.xp = 0;
        this.level = level;
    }

    public void setLevel(float level, float XP) {
        this.xp = XP;
        this.level = level;
    }

    /*
    Helper level up
     */

    private void sendLevelUpMessage(float val) {
        Player player = Bukkit.getPlayer(parentBuild.getSkillPlayer().getPlayerUUID());
        if(player == null || !player.isOnline())
            return;

        BigDecimal valMessage = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP);
        BigDecimal levelMessage = new BigDecimal(getLevel()).setScale(1, RoundingMode.HALF_UP);

        player.sendMessage(ChatColor.GOLD + "You have gained " + valMessage + " " + this.getType().getName() + ", it is now " + levelMessage + ".");
    }


}
