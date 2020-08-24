package com.github.kotooriiii.register_system.ffa;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.register_system.Gathering;
import com.github.kotooriiii.register_system.GatheringType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FFAListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();

        Gathering gathering = LostShardPlugin.getGatheringManager().getGathering();
        if(gathering == null)
            return;
        if(!gathering.getType().equals(GatheringType.FFA))
            return;
        if(!gathering.getRegisterManager().hasPlayer(player))
            return;

        FFAMode ffa = (FFAMode) gathering;

        gathering.getRegisterManager().removePlayer(player);

        Player[] players = gathering.getRegisterManager().getRegisteredPlayers();

        if(players.length == 1)
        {
            ffa.endGame(players[0]);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        Gathering gathering = LostShardPlugin.getGatheringManager().getGathering();
        if(gathering == null)
            return;
        if(!gathering.getType().equals(GatheringType.FFA))
            return;
        if(!gathering.getRegisterManager().hasPlayer(player))
            return;
        FFAMode ffa = (FFAMode) gathering;

        gathering.getRegisterManager().removePlayer(player);

        Player[] players = gathering.getRegisterManager().getRegisteredPlayers();

        if(players.length == 1)
        {
            ffa.endGame(players[0]);
        }
    }
}
