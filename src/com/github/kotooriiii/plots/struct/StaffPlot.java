package com.github.kotooriiii.plots.struct;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.plots.PlotType;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class StaffPlot extends Plot {

    private Location spawn;

    public StaffPlot(World world, Zone zone, String name) {

        super(world, name);
        this.zone = zone;
        this.plotType = PlotType.STAFF_DEFAULT;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        LostShardPlugin.getPlotManager().savePlot(this);
    }

    @Override
    protected Zone calculateZone() {
        return this.zone;
    }


}
