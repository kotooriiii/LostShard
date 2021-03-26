package com.github.kotooriiii.plots.struct;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.plots.PlotType;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public abstract class Plot implements Serializable {

    public static final int MINIMUM_PLOT_STAFF_CREATE_RANGE = LostShardPlugin.isTutorial() ? 180 : 100;
    public static final int MINIMUM_PLOT_EXPAND_RANGE = LostShardPlugin.isTutorial() ? 25 : 75;
    private static final long serialVersionUID = 1L;
    public static final int MINIMUM_PLOT_CREATE_RANGE = LostShardPlugin.isTutorial() ? 5 : 10;

    /**
     * The plot's ID
     */
    private UUID id;

    /**
     * The plot's name
     */
    private String name;

    /**
     * The plot's type
     */
    protected PlotType plotType;
    /**
     * The plot's belonging world
     */
    protected World world;


    /**
     * This zone represents the area of the plot.
     * Note: This zone must be recalculated if it is updated.
     */
    protected Zone zone;

    /**
     * The milliseconds date at which the plot was created in.
     */
    protected long creationMillisecondsDate;

    protected boolean isDeleted;

    public Plot(World world, String name) {
        this.name = name;
        this.world = world;
        this.isDeleted= false;

        //This is empty, must be calculated by subclass.
        this.zone = new Zone(0, 0, 0, 0, 0, 0);
        this.id = generateID();
        creationMillisecondsDate = ZonedDateTime.now(ZoneId.of("America/New_York")).toInstant().toEpochMilli();

    }

    /**
     * Calculates a zone and returns the new zone that represents the volume of this plot.
     *
     * @return a zone representing a plot's volume.
     */
    protected abstract Zone calculateZone();

    /**
     * Returns an info page of the plot.
     *
     * @param perspectivePlayer
     * @return
     */
    public abstract String info(Player perspectivePlayer);

    /**
     * Checks if the location is inside a plot.
     *
     * @param location The location being checked for
     * @return true if location is inside a plot, false otherwise.
     */
    public boolean contains(Location location) {

        if (!location.getWorld().equals(this.getWorld()))
            return false;


        return this.zone.contains(location);
    }

    /**
     * Checks if a location can safely create a plot when using *this* plot independently.
     *
     * @param location The location trying to create a plot.
     * @return true if player can create plot, false if player is not far enough yet.
     */
    public boolean isMinimumDistancePlotCreate(Location location) {

        int distance = MINIMUM_PLOT_CREATE_RANGE;
        if (plotType.isStaff())
            distance = MINIMUM_PLOT_STAFF_CREATE_RANGE;


        int minX = getZone().getX1() - distance;
        int maxX = getZone().getX2() + distance;

        int minY = getZone().getY1();
        int maxY = getZone().getY2();

        int minZ = getZone().getZ1() - distance;
        int maxZ = getZone().getZ2() + distance;

        Zone zone = new Zone(minX, maxX, minY, maxY, minZ, maxZ);
        if (zone.contains(location) && this.world.equals(location.getWorld()))
            return false;
        return true;
    }

    //Getters and setters

    public UUID getPlotUUID() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
        LostShardPlugin.getPlotManager().savePlot(this);
    }

    public String getName() {
        return name;
    }

    protected void setWorld(World world) {
        this.world = world;
        LostShardPlugin.getPlotManager().savePlot(this);

    }

    public World getWorld() {
        return this.world;
    }

    protected void setType(PlotType plotType) {
        this.plotType = plotType;
        LostShardPlugin.getPlotManager().savePlot(this);

    }

    public PlotType getType() {
        return plotType;
    }

    public Zone getZone() {
        return zone;
    }

    protected void setZone(Zone zone) {
        this.zone = zone;
        LostShardPlugin.getPlotManager().savePlot(this);

    }



    public void setID(UUID uuid) {
        this.id = uuid;
        LostShardPlugin.getPlotManager().savePlot(this);
    }

    public long getCreationMillisecondsDate() {
        return creationMillisecondsDate;
    }

    public void setCreationMillisecondsDate(long creationMillisecondsDate) {
        this.creationMillisecondsDate = creationMillisecondsDate;
    }

    public UUID generateID() {
        //Generates a unique ID.
        boolean isUnique = false;
        UUID uniqueID = null;
        uniqueLoop:
        while (!isUnique) {
            UUID possibleID = UUID.randomUUID();
            plotLoop:
            for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
                if (plot.getPlotUUID().equals(possibleID))
                    continue uniqueLoop;

            }
            uniqueID = possibleID;
            isUnique = true;
        }
        return uniqueID;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null)
            return false;
        if(!(object instanceof Plot))
            return false;
        Plot otherPlot = (Plot) object;
        return otherPlot.getPlotUUID().equals(this.getPlotUUID());
    }

    public void setDeleted(boolean b) {
        this.isDeleted = b;
    }
    public boolean isDeleted() {
        return isDeleted;
    }

}
