package com.github.kotooriiii.status;

import javax.xml.stream.Location;

public class Shrine {
    private Location location;

    public Shrine(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
