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
                , new Skill(this, SkillType.FISHING)
                , new Skill(this, SkillType.MINING)
                , new Skill(this, SkillType.SURVIVALISM)
                , new Skill(this, SkillType.SWORDSMANSHIP)
                , new Skill(this, SkillType.TAMING)
        };
    }

    public SkillPlayer getSkillPlayer()
    {
        return this.skillPlayer;
    }

    public void setSkills(Skill[] skills)
    {
        this.skills = skills;
    }

    public Skill getSkill(SkillType type)
    {
        switch (type)
        {

            case ARCHERY:
                return getArchery();
            case BLACKSMITHY:
                return getBlacksmithy();
            case BRAWLING:
                return getBrawling();
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

    public Skill getFishing() {
        return skills[3];
    }

    public Skill getMining() {
        return skills[4];
    }

    public Skill getSurvivalism()
    {
        return skills[5];
    }

    public Skill getSwordsmanship()
    {
        return skills[6];
    }

    public Skill getTaming()
    {
        return skills[7];
    }

    public boolean isMaxBuild() {
        int counter = 0;
        for (Skill skill : getSkills()) {
            counter += (int) skill.getLevel();
        }
        return counter >= this.MAX_POINTS;
    }

    public int getMaxPoints()
    {
        return MAX_POINTS;
    }

    public Skill[] getSkills() {
        Skill[] skillsClone = new Skill[this.skills.length];
        for(int i = 0; i < skills.length; i++)
        {
            skillsClone[i] = skills[i];
        }
        return skillsClone;
    }
}
