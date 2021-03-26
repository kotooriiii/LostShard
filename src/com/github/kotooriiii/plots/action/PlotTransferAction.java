package com.github.kotooriiii.plots.action;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PlotTransferAction extends AbstractPlotAction {

    private String newOwnerName;
    private static final int TRANSFER_COST = 100;


    public PlotTransferAction(Player playerCommittingAction, Plot plot, PlotActionType type, String newOwnerName)
    {
        super(playerCommittingAction, plot, type);
        this.newOwnerName = newOwnerName;
    }


    @Override
    public boolean isRequirementMet() {
        if (this.getPlot() == null) {
            this.getPlayer().sendMessage(ERROR_COLOR + "You are not standing on any plot.");
            return false;
        }

        if (!this.getPlot().getType().equals(PlotType.PLAYER)) {
            this.getPlayer().sendMessage(ERROR_COLOR + "You can't transfer ownership of staff plots.");
            return false;
        }

        PlayerPlot playerPlot = (PlayerPlot) this.getPlot();

        if (!playerPlot.getOwnerUUID().equals(getPlayer().getUniqueId())) {
            getPlayer().sendMessage(ERROR_COLOR + "You must be the owner of the plot in order to transfer ownership.");
            return false;
        }


        if (playerPlot.getBalance() < TRANSFER_COST) {
            getPlayer().sendMessage(ERROR_COLOR + "Insufficient funds. You must have at least " + TRANSFER_COST + "g in your plot’s balance to transfer ownership.");
            return false;
        }

        Player newOwnerPlayer = Bukkit.getPlayer(newOwnerName);

        if (newOwnerPlayer == null) {
            getPlayer().sendMessage(ERROR_COLOR + "You must transfer ownership to an online player.");
            return false;
        }

        ShardPlotPlayer shardPlotPlayer = ShardPlotPlayer.wrap(newOwnerPlayer.getUniqueId());
        if(shardPlotPlayer.hasReachedMaxPlots())
        {
            getPlayer().sendMessage(ERROR_COLOR + "The player has already reached max plots.");
            return false;
        }

        return true;
    }

    @Override
    public boolean isKeyword(String keyword) {
        return keyword.equalsIgnoreCase("YES");
    }

    @Override
    public void apply() {
        Player newOwnerPlayer = Bukkit.getPlayer(newOwnerName);

        if (newOwnerPlayer == null) {
            getPlayer().sendMessage(ERROR_COLOR + "You must transfer ownership to an online player.");
            return;
        }
        PlayerPlot playerPlot = (PlayerPlot) this.getPlot();


        ShardPlotPlayer shardPlotPlayer = ShardPlotPlayer.wrap(newOwnerPlayer.getUniqueId());
        if(shardPlotPlayer.hasReachedMaxPlots())
        {
            getPlayer().sendMessage(ERROR_COLOR + "The player has already reached max plots.");
            return;
        }

        //Remove old cached file
        FileManager.removeFile(playerPlot);

        //Wrap shard plot player and remove his file
        ShardPlotPlayer getOldShardPlotPlayer = ShardPlotPlayer.wrap(playerPlot.getOwnerUUID());
        getOldShardPlotPlayer.removePlot(playerPlot);

        //Add it to the new player
        shardPlotPlayer.addPlot(playerPlot);

        //Change ownership
        playerPlot.setOwner(getPlayer().getUniqueId());

        //Make old owner coowner
        playerPlot.addJointOwner(this.getPlayer().getUniqueId());

        //Cost
        playerPlot.withdraw(TRANSFER_COST);
    }
}
