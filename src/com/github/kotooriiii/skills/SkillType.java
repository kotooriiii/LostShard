package com.github.kotooriiii.skills;

public enum SkillType {
    ARCHERY("Archery", 1),
    SWORDSMANSHIP("Swordsmanship", 2),
    MINING("Mining", 3),
    FISHING("Fishing", 4),
    LUMBERJACKING("Lumberjacking", 5),
    TAMING("Taming", 6),
    SURVIVALISM("Survivalism", 7),
    BRAWLING("Brawling", 8),
    BLACKSMITHY("Blacksmithy", 9),
    SORCERY("Sorcery", 10);

    private String name;
    private int id;

    private SkillType(String name, int id)
    {
        this.name = name;
        this.id = id;
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

    public int getID()
    {
        return id;
    }
}
