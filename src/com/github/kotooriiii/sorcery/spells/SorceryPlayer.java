package com.github.kotooriiii.sorcery.spells;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class SorceryPlayer {

    private  UUID uuid;

    private HashSet<String> spellTypes;

    public SorceryPlayer(UUID uuid)
    {
        this.uuid = uuid;
        this.spellTypes = new HashSet<>();
    }


    public UUID getUUID() {
        return uuid;
    }

    public void addSpells(List<String> spellNames)
    {
        spellTypes.addAll(spellNames);
    }

    public boolean addSpell(SpellType type)
    {
        return spellTypes.add(type.getName());
    }

    public boolean removeSpell(SpellType type)
    {
        return spellTypes.remove(type.getName());
    }

    public boolean hasSpell(SpellType type)
    {
        return spellTypes.contains(type.getName());
    }

    public HashSet<String> getSpellTypes() {
        return spellTypes;
    }

    public Spell[] getSpells()
    {
        ArrayList<Spell> spells = new ArrayList<>();
        for(String spellName : spellTypes)
        {
            Spell spell = Spell.of(SpellType.matchSpellType(spellName));
            spells.add(spell);
        }
        return spells.toArray(new Spell[spells.size()]);
    }

    public ArrayList<String> getArrayList()
    {
        return new ArrayList<>(spellTypes);
    }
}
