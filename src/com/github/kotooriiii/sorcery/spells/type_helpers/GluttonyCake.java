package com.github.kotooriiii.sorcery.spells.type_helpers;

import com.github.kotooriiii.sorcery.spells.type.circle9.GluttonySpell;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;

import java.util.UUID;

public class GluttonyCake
{
    //Cake location
    private Location location;
    //Username hologram location
    private Hologram hologram;
    //Player who is cake
    private UUID playerIsCakeUUID;
    //Player who casted the spell
    private UUID playerWhoCastedUUID;
    //Amount of damage
    private int damage;
    private boolean isEaten;

    public GluttonyCake(Location location, Hologram hologram, UUID playerIsCakeUUID, UUID playerWhoCastedUUID) {
        this.location = location;
        this.hologram = hologram;
        this.playerIsCakeUUID = playerIsCakeUUID;
        this.playerWhoCastedUUID = playerWhoCastedUUID;
        this.damage = 0;
        this.isEaten = false;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    public UUID getPlayerIsCakeUUID() {
        return playerIsCakeUUID;
    }

    public UUID getPlayerWhoCastedUUID() {
        return playerWhoCastedUUID;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void addDamageDefault()
    {
        this.damage += GluttonySpell.getGluttonyEatDamage();
    }

    public void setEaten(boolean b) {
        this.isEaten = b;
    }

    public boolean isEaten()
    {
        return this.isEaten;
    }

}

