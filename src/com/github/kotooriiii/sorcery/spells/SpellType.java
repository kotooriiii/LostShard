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
    GRASS(new String[]{"Grass"}, "Grassius Maximus"),
    LIGHT(new String[]{"Light"}, "Lightus Emiticus"),
    CREATE_FOOD(new String[]{"Create Food", "Food"}, "Magickus Delicious"),
    BRIDGE(new String[]{"Bridge"}, "An Bridgius"),
    WALL(new String[]{"Wall"}, "Stonicus Shieldius"),
    FLOWER(new String[]{"Flower", "Flowers"}, "Flowerus Erupticus"),
    MOON_JUMP(new String[]{"Moon Jump", "Jump"}, "Leapius Soarius"),
    MAGIC_ARROW(new String[]{"Magic Arrow"}, "Stabius Projectilus"),
    SCREECH(new String[]{"Screech"}, "Fearius Mobicus"),
    GATE_TRAVEL(new String[]{"Gate Travel", "GT"}, "Gatius Teleportus"),
    HEAL_OTHER(new String[]{"Heal Other"}, "Buddius Healicus"),
    RESPIRATE(new String[]{"Respirate"}, "Immunius Drownicus"),
    FIRE_FIELD(new String[]{"Fire Field"}, "Charmanderous Fieldicus"),
    FIRE_WALK(new String[]{"Fire Walk"}, "Charmanderous Feetius"),
    WATER_WALK(new String[]{"Water Walk"}, "Aquas Levitatus"),
    FORCE_PUSH(new String[]{"Force Push"}, "Fus Ro Dah"),
    FORCE_PULL(new String[]{"Force Pull"}, "Lurius Backicus"),
    CLEANSE(new String[]{"Cleanse"}, "Elixirus Vaporizus"),
    RADIATE(new String[]{"Radiate"}, "Illuminatus Releasus"),
    SILENT_WALK(new String[]{"Silent Walk"}, "Secretus Stepicus"),
    SOAR(new String[]{"Soar"}, "Glidius Freeicus"),
    DAY(new String[]{"Day"}, "Morningus Erupticus"),
    PERCEPTION(new String[]{"Perception"}, "Revealius Hiddenus"),
    UNVEIL(new String[]{"Unveil"}, "Exposius Invisiblius"),

    WRATH(new String[]{"Wrath"}, "Ragius Lightningicus"),
    ENVY(new String[]{"Envy"}, "Switchius Staticus"),
    LUST(new String[]{"Lust"}, "Healius Matius"),
    GREED(new String[]{"Greed"}, "Selfishius Keepicus"),
    SLOTH(new String[]{"Sloth"}, "Brutius Hammerus"),
    PRIDE(new String[]{"Pride"}, "Warrioricus Ringus"),
    GLUTTONY(new String[]{"Gluttony"}, "Cakius Morphius");

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

    public String getLatin() {
        return latin;
    }
}
