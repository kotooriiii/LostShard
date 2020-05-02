package com.github.kotooriiii.plots;

public enum PlotType {
    DEFAULT,
    PLAYER,
    STAFF_DEFAULT,
    STAFF_SPAWN,
    STAFF_ARENA,
    STAFF_HOSTILITY;

    public boolean isStaff()
    {
        return this.name().toLowerCase().startsWith("staff");
    }

}
