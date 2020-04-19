package com.github.kotooriiii.skills;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.plots.Plot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class SkillPlayer implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID playerUUID;

    private final int MAX_POINTS = 10000;

    private Skill archery;
    private Skill swordsmanship;
    private Skill mining;
    private Skill fishing;
    private Skill lumberjacking;
    private Skill taming;
    private Skill survivalism;
    private Skill brawling;
    private Skill blacksmithy;
    private Skill sorcery;

    private static HashMap<UUID, SkillPlayer> skillPlayerHashMap = new HashMap<>();

    //Writes to file every day on LostShardPlugin

    public SkillPlayer(UUID playerUUID) {

        this.playerUUID = playerUUID;

        this.archery = new Skill(SkillType.ARCHERY);
        this.swordsmanship = new Skill(SkillType.SWORDSMANSHIP);
        this.mining = new Skill(SkillType.MINING);
        this.fishing = new Skill(SkillType.FISHING);
        this.lumberjacking = new Skill(SkillType.LUMBERJACKING);
        this.taming = new Skill(SkillType.TAMING);
        this.survivalism = new Skill(SkillType.SURVIVALISM);
        this.brawling = new Skill(SkillType.BRAWLING);
        this.blacksmithy = new Skill(SkillType.BLACKSMITHY);
        this.sorcery = new Skill(SkillType.SORCERY);

        add(this);
    }

    public Skill[] getSkills() {
        return new Skill[]
                {
                        archery, swordsmanship, mining, fishing, lumberjacking, taming,
                        survivalism, brawling, blacksmithy, sorcery
                };
    }

    public Skill get(int skillID) {
        for (Skill skill : getSkills()) {
            if (skill.getType().getID() == skillID)
                return skill;
        }
        return null;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Skill getArchery() {
        return archery;
    }

    public Skill getSwordsmanship() {
        return swordsmanship;
    }

    public Skill getMining() {
        return mining;
    }

    public Skill getFishing() {
        return fishing;
    }

    public Skill getLumberjacking() {
        return lumberjacking;
    }

    public Skill getTaming() {
        return taming;
    }

    public Skill getSurvivalism() {
        return survivalism;
    }

    public Skill getBrawling() {
        return brawling;
    }

    public Skill getBlacksmithy() {
        return blacksmithy;
    }

    public Skill getSorcery() {
        return sorcery;
    }

    public boolean isMaxed() {
        int counter = 0;
        for (Skill skill : getSkills()) {
            counter += (int) skill.getLevel();
        }
        return counter >= this.MAX_POINTS;
    }

    public void save() {
        FileManager.write(this);
    }

    public static void add(SkillPlayer skillPlayer) {
        skillPlayerHashMap.put(skillPlayer.getPlayerUUID(), skillPlayer);
    }

    public static HashMap<UUID, SkillPlayer> getPlayerSkills() {
        return skillPlayerHashMap;
    }

    public static SkillPlayer wrap(UUID playerUUID) {
        return skillPlayerHashMap.get(playerUUID);
    }

    public class Skill implements Serializable {

        private static final long serialVersionUID = 1L;

        private final SkillType type;

        private float xp;
        private float level;
        private final float maxLevel;

        private final int CONSTANT = 75;

        public Skill(SkillType type) {
            this.type = type;
            this.xp = 0;
            this.level = 0;
            this.maxLevel = 100;
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
            if (isMaxed())
                return false;

            //Add XP
            float oldXP = this.xp;
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
            int newLevel = (int) Math.floor(this.level);

            if (newLevel > 100) {
                this.level = 100;
            }

            if (oldLevel == newLevel)
                return false;
            else return true;
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

    /*
    Helper level up
     */

        private void sendLevelUpMessage(float val) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
            if (!offlinePlayer.isOnline())
                return;

            BigDecimal valMessage = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP);
            BigDecimal levelMessage = new BigDecimal(getLevel()).setScale(1, RoundingMode.HALF_UP);

            Player player = offlinePlayer.getPlayer();
            player.sendMessage(ChatColor.GOLD + "You have gained " + valMessage + " " + this.getType().getName() + ", it is now " + levelMessage + ".");
        }


    }
}
