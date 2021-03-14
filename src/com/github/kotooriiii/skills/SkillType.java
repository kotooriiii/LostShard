package com.github.kotooriiii.skills;

public enum SkillType {
    ARCHERY("Archery", 0),
    BLACKSMITHY("Blacksmithy", 1),
    BRAWLING("Brawling", 2),
    ENCHANTING("Enchanting", 3),
    FISHING("Fishing", 4),
    MINING("Mining", 5),
    SURVIVALISM("Survivalism", 6),
    SWORDSMANSHIP("Swordsmanship",7),
    TAMING("Taming", 8);

    private String name;
    private int index;

    private SkillType(String name, int index)
    {
        this.name = name;
        this.index = index;
    }

    public static boolean isSkill(String skillString) {
        for(SkillType type : values())
        {
            if(type.getName().equalsIgnoreCase(skillString))
                return true;
        }
        return false;
    }

    public static SkillType ofIndex(int k) {
        for(SkillType type : SkillType.values())
        {
            if(type.getIndex() == k)
                return type;
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}
