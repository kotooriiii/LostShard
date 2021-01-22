package com.github.kotooriiii.sorcery.spells;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface SpellToggleable {

    double getManaCostPerSecond();

    void manaDrainExecuteSpell(UUID uuid);

    void stopManaDrain(UUID uuid);

}
