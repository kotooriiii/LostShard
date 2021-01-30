package com.github.kotooriiii.sorcery.spells;

public enum SpellType {

    FIREBALL(new String[]{"Fireball"}, "Charmanderous Ballicus"),
    HEAL(new String[]{"Heal Self", "Self Heal"}, "Selfishius Healicus"),
    ICE(new String[]{"Ice Ball", "Ice"}, "Freezius Ballicus"),
    LIGHTNING(new String[]{"Lightning"}, "Zeusius Similaricus"),
    TELEPORT(new String[]{"Teleport", "TP"}, "Nearius Porticus"),
    WEB_FIELD(new String[]{"Web Field", "Web"}, "Webicus Fieldicus"),
    //DARKNESS(new String[]{"Darkness"}),
    // CLONE(new String[]{"Clone"}, "clonus"),
    CLANTP(new String[]{"Clan Teleport", "ClanTP", "Clan TP"}, "Arg Matius Teleportus"),
    MARK(new String[]{"Mark"}, "Runus Markius"),
    RECALL(new String[]{"Recall"}, "Runus Teleporticus"),
    PERMANENT_GATE_TRAVEL(new String[]{"Permanent Gate Travel", "PGT"}, "Permanentus Gatius"),
    CHRONOPORT(new String[]{"Chronoport", "Chrono"}, "Rubberbandicus Elasticus"),

    GRASS(new String[]{"Grass"}, "GRASS"),
    LIGHT(new String[]{"Light"}, "LIGHT"),
    CREATE_FOOD(new String[]{"Create Food", "Food"}, "CREATE_FOOD"),
    BRIDGE(new String[]{"Bridge"}, "BRIDGE"),
    WALL(new String[]{"Wall"}, "WALL"),
    FLOWER(new String[]{"Flower", "Flowers"}, "FLOWER"),
    MOON_JUMP(new String[]{"Moon Jump", "Jump"}, "MOON_JUMP"), MAGIC_ARROW(new String[]{"Magic Arrow"}, "MAGIC_ARROW"),
    SCREECH(new String[]{"Screech"}, "SCREECH"),
    GATE_TRAVEL(new String[]{"Gate Travel", "GT"}, "GATE_TRAVEL"),
    HEAL_OTHER(new String[]{"Heal Other"}, "HEAL_OTHER"),
    RESPIRATE(new String[]{"Respirate"}, "RESPIRATE"),
    FIRE_FIELD(new String[]{"Fire Field"}, "FIRE_FIELD"),
    FIRE_WALK(new String[]{"Fire Walk"}, "FIRE_WALK"),
    WATER_WALK(new String[]{"Water Walk"}, "WATER_WALK"),
    FORCE_PUSH(new String[]{"Force Push"}, "FORCE_PUSH"),
    FORCE_PULL(new String[]{"Force Pull"}, "FORCE_PULL"),
    CLEANSE(new String[]{"Cleanse"}, "CLEANSE"),
    RADIATE(new String[]{"Radiate"}, "RADIATE"),
    SILENT_WALK(new String[]{"Silent Walk"}, "SILENT_WALK"),
    SOAR(new String[]{"Soar"}, "SOAR"),
    DAY(new String[]{"Day"}, "DAY"),
    PERCEPTION(new String[]{"Perception"}, "PERCEPTION"),
    UNVEIL(new String[]{"Unveil"}, "UNVEIL"),

    WRATH(new String[]{"Wrath"}, "WRATH"),
    ENVY(new String[]{"Envy"}, "ENVY"),
    LUST(new String[]{"Lust"}, "LUST"),
    GREED(new String[]{"Greed"}, "GREED"),
    SLOTH(new String[]{"Sloth"}, "SLOTH"),
    PRIDE(new String[]{"Pride"}, "PRIDE"),
    GLUTTONY(new String[]{"Gluttony"}, "GLUTTONY");

    private String[] names;
    private String latin;

    private SpellType(String[] names, String latin) {
        this.names = names;
        this.latin = latin;
    }

    public String[] getNames() {
        return names;
    }

    public String getName() {
        return names[0];
    }

    public static SpellType matchSpellType(String name) {
        for (SpellType type : values()) {
            for (String iname : type.getNames()) {
                if (iname.equalsIgnoreCase(name))
                    return type;
            }
        }
        return null;
    }

    public static SpellType[] oldMapValues()
    {
        return new SpellType[]{FIREBALL,HEAL,ICE,LIGHTNING,TELEPORT,WEB_FIELD,CLANTP,MARK,RECALL,PERMANENT_GATE_TRAVEL,CHRONOPORT};
    }

    public String getLatin() {
        return latin;
    }
}
