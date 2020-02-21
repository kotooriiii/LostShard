package com.github.kotooriiii.hostility;

import org.bukkit.Location;
import sun.text.bidi.BidiLine;

import java.util.UUID;

public abstract class HostilityPlatform {

    private UUID id;
    private String name;

    public HostilityPlatform(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
