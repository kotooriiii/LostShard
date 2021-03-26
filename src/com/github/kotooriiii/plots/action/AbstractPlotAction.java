package com.github.kotooriiii.plots.action;

import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.entity.Player;

public abstract class AbstractPlotAction {

    private PlotActionType type;
    private Plot plot;
    private Player player;

    public AbstractPlotAction(Player playerCommittingAction, Plot plot, PlotActionType type)
    {
        this.type = type;
        this.plot = plot;
        this.player = playerCommittingAction;
    }

    public PlotActionType getType() {
        return type;
    }

    public Plot getPlot() {
        return plot;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract boolean isRequirementMet();
    public abstract void apply();
    public abstract boolean isKeyword(String keyword);
}
