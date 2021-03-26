package com.github.kotooriiii.plots;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.npc.type.vendor.VendorNPC;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.platforms;

public class PlotManager {

    public static HashMap<UUID, Plot> allPlots = new HashMap<>();

    public PlotManager() {

    }

    public void addPlot(Plot plot, boolean saveToFile) {
        switch (plot.getType()) {
            case PLAYER:
                PlayerPlot playerPlot = (PlayerPlot) plot;
                ShardPlotPlayer shardPlotPlayer = ShardPlotPlayer.wrap(playerPlot.getOwnerUUID());
                shardPlotPlayer.addPlot(playerPlot);
                break;
            case STAFF_DEFAULT:
            case STAFF_ARENA:
            case STAFF_SPAWN:
            case STAFF_HOSTILITY:
                break;
        }
        allPlots.put(plot.getPlotUUID(), plot);
        if (saveToFile)
            savePlot(plot);
    }

    public void savePlot(Plot plot) {
        FileManager.write(plot);
    }

    public void removePlot(Plot plot) {
        removePlot(plot, false);
    }

    public void removePlot(Plot plot, boolean checkVendorLives) {
        switch (plot.getType()) {
            case PLAYER:
                PlayerPlot playerPlot = (PlayerPlot) plot;
                ShardPlotPlayer shardPlotPlayer = ShardPlotPlayer.wrap(playerPlot.getOwnerUUID());
                shardPlotPlayer.removePlot(playerPlot);
                break;
            case STAFF_DEFAULT:
            case STAFF_ARENA:
            case STAFF_SPAWN:
            case STAFF_HOSTILITY:
                break;
        }
        plot.setDeleted(true);
        allPlots.remove(plot.getPlotUUID());
        FileManager.removeFile(plot);

        if (checkVendorLives)
            VendorNPC.checkLives();
    }


    public boolean containsPlot(Plot plot) {
        return allPlots.containsValue(plot);
    }


    public Collection<Plot> getAllPlots() {
        return allPlots.values();
    }

    public boolean isPlot(String name) {
        for (Plot plot : getAllPlots())
            if (plot.getName().equalsIgnoreCase(name))
                return true;

        return false;
    }

    public Plot getPlot(String name) {
        for (Plot plot : allPlots.values())
            if (plot.getName().equalsIgnoreCase(name))
                return plot;

        return null;
    }

    public boolean isStaffPlotName(String name) {

        //Is reserved for order and chaos
        if (name.equalsIgnoreCase("order") || name.equalsIgnoreCase("chaos") || name.equalsIgnoreCase("arena") || name.equalsIgnoreCase("ffa") || name.equalsIgnoreCase("bracket"))
            return true;

        //Is reserved
        for (HostilityPlatform platform : platforms) {
            if (platform.getName().equalsIgnoreCase(name))
                return true;
        }


        for (Plot plot : getAllPlots())
            if (plot.getType().isStaff())
                if (plot.getName().equalsIgnoreCase(name))
                    return true;


        return false;
    }

    public boolean isStandingOnPlot(Player player) {
        return isStandingOnPlot(player.getLocation());
    }

    public boolean isStandingOnPlot(Location location) {
        for (Plot plot : allPlots.values()) {
            if (plot.contains(location))
                return true;
        }
        return false;
    }

    public Plot getStandingOnPlot(Player player) {
        return getStandingOnPlot(player.getLocation());
    }

    public Plot getStandingOnPlot(Location location) {
        for (Plot plot : allPlots.values()) {
            if (plot.contains(location))
                return plot;
        }
        return null;
    }

    public boolean hasNearbyPlots(Location location) {
        for (Plot plot : getAllPlots())
            if (!plot.isMinimumDistancePlotCreate(location))
                return true;
        return false;
    }

    public Plot wrap(UUID uuid) {
        return allPlots.get(uuid);
    }
}
