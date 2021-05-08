package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.plots.struct.SpawnPlot;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PlayerStatusRespawnListener implements Listener {
    @EventHandler
    public void onRespawnStatus(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();

        if(CitizensAPI.getNPCRegistry().isNPC(player))
            return;

        Location loc = getSpawnLocation(player);
        if(loc!=null)
        event.setRespawnLocation(loc);
    }

    public static Location getSpawnLocation(Player player)
    {
        StatusPlayer statusPlayer = StatusPlayer.wrap(player.getUniqueId());
        Stat stat = Stat.wrap(statusPlayer.getPlayerUUID());

        //Disabled to prevent skill xp grind
        //stat.setMana(stat.getMaxMana());
        //stat.setStamina(stat.getMaxStamina());

        Location possibleSpawn = stat.getSpawn();
        if(possibleSpawn!=null)
        {
            return possibleSpawn;

        } else {
            String organization = statusPlayer.getStatus().getOrganization();
            SpawnPlot plot = (SpawnPlot) LostShardPlugin.getPlotManager().getPlot(organization);
            if(plot == null || plot.getSpawn() == null) {
                Bukkit.broadcastMessage(ERROR_COLOR + "The spawn is not created for " + organization + ".") ;
                return null;
            }

            return plot.getSpawn();
        }

    }
}
