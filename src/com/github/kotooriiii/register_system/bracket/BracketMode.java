package com.github.kotooriiii.register_system.bracket;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.plots.struct.BracketPlot;
import com.github.kotooriiii.plots.struct.FFAPlot;
import com.github.kotooriiii.register_system.Gathering;
import com.github.kotooriiii.register_system.GatheringType;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BracketMode extends Gathering {

    private final int GOLD_REWARDED = 100;

    public BracketMode() {
        super(GatheringType.BRACKET);
    }

    @Override
    public void startGame() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "The Bracket match is now beginning.");
        BracketPlot bracketPlot = (BracketPlot) LostShardPlugin.getPlotManager().getPlot("Bracket");

        Player[] registeredPlayers = getRegisterManager().getRegisteredPlayers();

        if(registeredPlayers.length == 1)
            endGame(registeredPlayers[0]);
        if(registeredPlayers.length == 0)
            endGame();
    }
    @Override
    public void endGame() {
        LostShardPlugin.getGatheringManager().setGathering(null);
    }

    public void endGame(Player player) {
        Bank bank = LostShardPlugin.getBankManager().wrap(player.getUniqueId());
        bank.addCurrency(GOLD_REWARDED);
        Bukkit.broadcastMessage(StatusPlayer.wrap(player.getUniqueId()).getStatus().getChatColor() + player.getName() + ChatColor.GREEN + " won the Bracket event.");
        endGame();
    }
}
