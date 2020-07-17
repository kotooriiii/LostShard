package com.github.kotooriiii.skills;

public enum SkillType {
    ARCHERY("Archery"),
    BLACKSMITHY("Blacksmithy"),
    BRAWLING("Brawling"),
    FISHING("Fishing"),
    MINING("Mining"),
    SURVIVALISM("Survivalism"),
    SWORDSMANSHIP("Swordsmanship"),
    TAMING("Taming");

    private String name;

    private SkillType(String name)
    {
        this.name = name;
    }

    public static boolean isSkill(String skillString) {
        for(SkillType type : values())
        {
            if(type.getName().equalsIgnoreCase(skillString))
                return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

}
