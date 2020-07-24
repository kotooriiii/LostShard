package com.github.kotooriiii.register_system;

public enum GatheringType {
    FFA,
    BRACKET;

    public String getName() {
        return name().substring(0,1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
