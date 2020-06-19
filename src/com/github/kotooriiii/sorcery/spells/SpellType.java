package com.github.kotooriiii.sorcery.spells;

public enum SpellType {
    FIREBALL(new String[]{"Fireball"}), HEAL(new String[]{"Heal Self", "Heal"}),
    ICE(new String[]{"Ice Ball", "Ice"}), LIGHTNING(new String[]{"Lightning"}),
    TELEPORT(new String[]{"Teleport"}), WEB_FIELD(new String[]{"Web Field", "Web"}),
    //DARKNESS(new String[]{"Darkness"}),
     CLONE(new String[]{"Clone"}),
    CLANTP(new String[]{"Clan Teleport", "ClanTP", "Clan TP"}),
    MARK(new String[]{"Mark"}),
    RECALL(new String[]{"Recall"}),
    PERMANENT_GATE_TRAVEL(new String[]{"Permanent Gate Travel", "Gate Travel", "Gate", "PGT"});

    private String[] names;


    private SpellType(String[] names)
    {
        this.names = names;
    }

    public String[] getNames() {
        return names;
    }

    public String getName(){
        return names[0];
    }

    public static SpellType matchSpellType(String name)
    {
        for(SpellType type : values())
        {
            for(String iname : type.getNames())
            {
                if(iname.equalsIgnoreCase(name))
                    return type;
            }
        }
        return null;
    }
}
