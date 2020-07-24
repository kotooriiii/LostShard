package com.github.kotooriiii.register_system;

import org.bukkit.block.data.type.Bed;

public class GatheringManager {
    private Gathering gathering;

    public GatheringManager()
    {

    }

    public void setGathering(Gathering gathering)
    {
        this.gathering = gathering;
    }

    public Gathering getGathering()
    {
        return gathering;
    }

    public boolean hasActiveGathering()
    {
        return getGathering()!=null;
    }
}
