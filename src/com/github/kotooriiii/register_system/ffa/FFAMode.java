package com.github.kotooriiii.register_system.ffa;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.plots.struct.FFAPlot;
import com.github.kotooriiii.register_system.Gathering;
import com.github.kotooriiii.register_system.GatheringType;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.xml.stream.Location;

import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class FFAMode extends Gathering {

    private Location spawn;
    private final int GOLD_REWARDED = 100;

    public FFAMode() {
        super(GatheringType.FFA);
    }

    @Override
    public void startGame() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "The FFA match is now beginning.");
        FFAPlot ffaPlot = (FFAPlot) LostShardPlugin.getPlotManager().getPlot("FFA");
        for (Player player : getRegisterManager().getRegisteredPlayers()) {
            player.teleport(ffaPlot.getSpawn());
        }
        if(getRegisterManager().getRegisteredPlayers().length == 1)
            endGame(getRegisterManager().getRegisteredPlayers()[0]);
        if(getRegisterManager().getRegisteredPlayers().length == 0)
            endGame();
    }

    @Override
    public void endGame() {
        LostShardPlugin.getGatheringManager().setGathering(null);
    }

    public void endGame(Player player) {
        Bank bank = LostShardPlugin.getBankManager().wrap(player.getUniqueId());
        bank.addCurrency(GOLD_REWARDED);
        Bukkit.broadcastMessage(StatusPlayer.wrap(player.getUniqueId()).getStatus().getChatColor() + player.getName() + ChatColor.GREEN + " won the FFA event.");
        endGame();
    }


    //basic gett/sett

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }


}
