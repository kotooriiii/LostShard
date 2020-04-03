package com.github.kotooriiii.skills;

public enum SkillType {
    ARCHERY("Archery"),
    SWORDSMANSHIP("Swordsmanship"),
    MINING("Mining"),
    FISHING("Fishing"),
    LUMBERJACKING("Lumberjacking"),
    TAMING("Taming"),
    SURVIVALISM("Survivalism"),
    BRAWLING("Brawling"),
    BLACKSMITHY("Blacksmithy"),
    SORCERY("Sorcery");

    private String name;

    private SkillType(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
