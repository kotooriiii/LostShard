package com.github.kotooriiii.sorcery.spells;

public enum SpellType {

    FIREBALL(new String[]{"Fireball"}, "Charmanderous Ballicus"),
    HEAL(new String[]{"Heal Self", "Heal"}, "Selfishius Healicus"),
    ICE(new String[]{"Ice Ball", "Ice"}, "Freezius Ballicus"),
    LIGHTNING(new String[]{"Lightning"}, "Zeusius Similaricus"),
    TELEPORT(new String[]{"Teleport"}, "Nearius Porticus"),
    WEB_FIELD(new String[]{"Web Field", "Web"}, "Webicus Fieldicus"),
    //DARKNESS(new String[]{"Darkness"}),
    CLONE(new String[]{"Clone"}, "clonus"),
    CLANTP(new String[]{"Clan Teleport", "ClanTP", "Clan TP"}, "Arg Matius Teleportus"),
    MARK(new String[]{"Mark"}, "Runus Markius"),
    RECALL(new String[]{"Recall"}, "Runus Teleporticus"),
    PERMANENT_GATE_TRAVEL(new String[]{"Permanent Gate Travel", "Gate Travel", "Gate", "PGT"}, "Permanentus Gatius"),
    CHRONOPORT(new String[]{"Chronoport", "Chrono"}, "Rubberbandicus Elasticus");

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
