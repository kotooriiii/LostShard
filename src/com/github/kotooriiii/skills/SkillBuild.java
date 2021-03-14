package com.github.kotooriiii.skills;

import com.google.gson.internal.$Gson$Preconditions;

public class SkillBuild {

    private final int MAX_POINTS = 400;

    private SkillPlayer skillPlayer;
    private Skill[] skills;

    public SkillBuild(SkillPlayer parent) {
        this.skillPlayer = parent;
        skills = new Skill[]{
                new Skill(this, SkillType.ARCHERY)
                , new Skill(this, SkillType.BLACKSMITHY)
                , new Skill(this, SkillType.BRAWLING)
                , new Skill(this, SkillType.ENCHANTING)
                , new Skill(this, SkillType.FISHING)
                , new Skill(this, SkillType.MINING)
                , new Skill(this, SkillType.SURVIVALISM)
                , new Skill(this, SkillType.SWORDSMANSHIP)
                , new Skill(this, SkillType.TAMING)
        };
    }

    public SkillPlayer getSkillPlayer() {
        return this.skillPlayer;
    }

    public Skill[] getSkillsWithAdded(Skill[] skills) {
        for (int i = 0; i < skills.length; i++) {
            final Skill skill = skills[i];

            if (skill.getType().getIndex() == i)
                continue;

            //BAD INDEX

            Skill[] newArr = new Skill[SkillType.values().length];

            for (int j = 0; j < skills.length; j++) {
                final Skill jSkill = skills[i];
                newArr[jSkill.getType().getIndex()] = jSkill;
            }

            for(int k = 0 ; k < newArr.length; k++)
            {
                if(newArr[k] == null)
                {
                    newArr[k] = new Skill(this, SkillType.ofIndex(k));
                }
            }

            return newArr;

        }
        return skills;
    }

    public void setSkills(Skill[] skills) {
        this.skills = skills;
    }

    public Skill getSkill(SkillType type) {
        switch (type) {
            case ARCHERY:
                return getArchery();
            case BLACKSMITHY:
                return getBlacksmithy();
            case BRAWLING:
                return getBrawling();
            case ENCHANTING:
                return getEnchanting();
            case FISHING:
                return getFishing();
            case MINING:
                return getMining();
            case SURVIVALISM:
                return getSurvivalism();
            case SWORDSMANSHIP:
                return getSwordsmanship();
            case TAMING:
                return getTaming();
            default:
                return null;
        }
    }

    public Skill getArchery() {
        return skills[0];
    }

    public Skill getBlacksmithy() {
        return skills[1];
    }

    public Skill getBrawling() {
        return skills[2];
    }

    public Skill getEnchanting() {
        return skills[3];
    }

    public Skill getFishing() {
        return skills[4];
    }

    public Skill getMining() {
        return skills[5];
    }

    public Skill getSurvivalism() {
        return skills[6];
    }

    public Skill getSwordsmanship() {
        return skills[7];
    }

    public Skill getTaming() {
        return skills[8];
    }

    public boolean isMaxBuild() {
        int counter = 0;
        for (Skill skill : getSkills()) {
            counter += (int) skill.getLevel();
        }
        return counter >= this.MAX_POINTS;
    }

    public int getMaxPoints() {
        return MAX_POINTS;
    }

    public Skill[] getSkills() {
        Skill[] skillsClone = new Skill[this.skills.length];
        for (int i = 0; i < skills.length; i++) {
            skillsClone[i] = skills[i];
        }
        return skillsClone;
    }
}
