package com.github.kotooriiii.sorcery.spells.drops;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class SpellMonsterDrop {
    private EntityType[] type;
    private double chance;

    public SpellMonsterDrop(EntityType[] type, double chance) {
        this.type = type;
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

    public EntityType[] getTypes() {
        return type;
    }

}
