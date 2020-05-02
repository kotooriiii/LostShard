package com.github.kotooriiii.plots;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.ranks.RankPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ShardPlotPlayer {
    private UUID ownerUUID;
    private HashSet<PlayerPlot> plotsOwned;

    private static HashMap<UUID, ShardPlotPlayer> shardPlotPlayer = new HashMap<>();
    private static int MAX_PLOTS = 3;

    public ShardPlotPlayer(UUID ownerUUID)
    {
        this.ownerUUID = ownerUUID;
        plotsOwned = new HashSet<>();
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void addPlot(PlayerPlot playerPlot)
    {
        plotsOwned.add(playerPlot);
    }

    public void removePlot(PlayerPlot playerPlot)
    {
        plotsOwned.remove(playerPlot);
    }

    public PlayerPlot[] getPlotsOwned()
    {
        return plotsOwned.toArray(new PlayerPlot[plotsOwned.size()]);
    }

    public boolean isEmpty()
    {
        return plotsOwned.size() == 0;
    }

    public boolean hasReachedMaxPlots()
    {
        return plotsOwned.size() >= getMaxPlots();
    }

    public int getMaxPlots()
    {
        return MAX_PLOTS;
    }

    public boolean hasPlot(PlayerPlot playerPlot)
    {
        return plotsOwned.contains(playerPlot);
    }

    public void add()
    {
        shardPlotPlayer.put(ownerUUID, this);
    }

    public void save()
    {
        FileManager.write(this);
    }

    public static HashMap<UUID, ShardPlotPlayer> getShardPlotPlayer() {
        return shardPlotPlayer;
    }

    public static ShardPlotPlayer wrap(UUID ownerUUID)
    {
        return shardPlotPlayer.get(ownerUUID);
    }
}
