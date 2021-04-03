package com.github.kotooriiii.plots.action;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.privacy.PlotPrivacy;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PlotPublicAction extends AbstractPlotAction {


    public PlotPublicAction(Player playerCommittingAction, Plot plot, PlotActionType type) {
        super(playerCommittingAction, plot, type);
    }


    @Override
    public boolean isRequirementMet() {
        if (this.getPlot() == null) {
            this.getPlayer().sendMessage(ERROR_COLOR + "You are not standing on any plot.");
            return false;
        }

        if (!this.getPlot().getType().equals(PlotType.PLAYER)) {
            this.getPlayer().sendMessage(ERROR_COLOR + "You can't change the town status of staff plots.");
            return false;
        }

        PlayerPlot playerPlot = (PlayerPlot) this.getPlot();

        if (!playerPlot.isTown()) {
            getPlayer().sendMessage(ERROR_COLOR + "The plot must be a town in order to change town status.");
            return false;
        }

        if (!playerPlot.getOwnerUUID().equals(getPlayer().getUniqueId()) && !playerPlot.isJointOwner(getPlayer().getUniqueId())) {
            getPlayer().sendMessage(ERROR_COLOR + "You must be the owner or co-owner of the town in order to change the town status.");
            return false;
        }

        if (playerPlot.getPrivacy() == PlotPrivacy.PUBLIC) {
            getPlayer().sendMessage(ERROR_COLOR + "The town status is already set to public.");
            return false;
        }

        return true;
    }

    @Override
    public boolean isKeyword(String keyword) {
        return keyword.equalsIgnoreCase("PUBLIC");
    }

    @Override
    public void apply() {
        PlayerPlot playerPlot = (PlayerPlot) getPlot();
        playerPlot.setPrivacy(PlotPrivacy.PUBLIC);
        playerPlot.sendToMembers(ChatColor.GOLD + "Your town’s status is now set to: " + playerPlot.getPrivacy().name() +  ".");


    }
}
