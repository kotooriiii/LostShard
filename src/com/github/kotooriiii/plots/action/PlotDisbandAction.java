package com.github.kotooriiii.plots.action;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.plots.listeners.SignChangeListener;
import com.github.kotooriiii.plots.privacy.PlotPrivacy;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.platforms;

public class PlotDisbandAction extends AbstractPlotAction {


    public PlotDisbandAction(Player playerCommittingAction, Plot plot, PlotActionType type) {
        super(playerCommittingAction, plot, type);
    }


    @Override
    public boolean isRequirementMet() {
        if (getPlot() == null) {
            getPlayer().sendMessage(ERROR_COLOR + "You are not standing on any plot.");
            return false;
        }

        if (!getPlot().getType().equals(PlotType.PLAYER)) {
            getPlayer().sendMessage(ERROR_COLOR + "You can't disband staff plots.");
            return false;
        }

        PlayerPlot disbandPlot = (PlayerPlot) getPlot();

        if (!disbandPlot.isOwner(getPlayer().getUniqueId())) {
            getPlayer().sendMessage(ERROR_COLOR + "You can't disband a plot that you don't own.");
            return false;
        }

        return true;
    }

    @Override
    public boolean isKeyword(String keyword) {
        return keyword.equalsIgnoreCase("DISBAND");
    }

    @Override
    public void apply() {
        disbandPlot(getPlayer());
    }

    private void disbandPlot(Player playerSender) {
        Bank bank = LostShardPlugin.getBankManager().wrap(playerSender.getUniqueId());
        PlayerPlot plot = (PlayerPlot) getPlot();

        for (Stat stat : Stat.getStatMap().values()) {
            Location loc = stat.getSpawn();
            if (loc == null)
                continue;

            if (plot.contains(loc)) {

                stat.setSpawn(null);
                Player player = Bukkit.getPlayer(stat.getPlayerUUID());
                if (player == null)
                    continue;
                player.sendMessage(ERROR_COLOR + "Your spawnpoint has been reset because your bed has been broken.");
            }
        }

        for (Location loc : SignChangeListener.getBuildChangeLocations()) {
            if (loc == null)
                continue;

            if (plot.contains(loc)) {
                SignChangeListener.remove(loc);
            }
        }

        double currentCurrency = bank.getCurrency();

        DecimalFormat df = new DecimalFormat("#.##");
        double refund = plot.disband();
        double funds = plot.getBalance();

        bank.setCurrency(currentCurrency + refund + funds);
        plot.sendToMembers(ChatColor.GOLD + playerSender.getName() + " disbanded " + plot.getName() + ".");
        playerSender.sendMessage(ChatColor.GOLD + "You have been refunded " + df.format(refund) + " gold for " + (PlayerPlot.REFUND_RATE * 100) + "% of the plot's value.");
        playerSender.sendMessage(ChatColor.GOLD + "You have been given the remaining funds from your plot's balance (" + df.format(funds) + ").");

    }
}
