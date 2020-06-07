package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.plots.struct.SpawnPlot;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class VoidDamageListener implements Listener {
    @EventHandler
    public void onVoidDMG(EntityDamageEvent event)
    {
        Entity damagedEntity = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();

        if(!(damagedEntity instanceof Player))
            return;

        if(!cause.equals(EntityDamageEvent.DamageCause.VOID))
            return;

        if(damagedEntity.getWorld().getName().contains("_the_end"))
            return;

        event.setCancelled(true);
        Player player = (Player) damagedEntity;

        player.sendMessage(ERROR_COLOR + "The chunk didn't load correctly. We are keeping you safe and teleporting you to spawn.");

        Location location = getSpawnLocation(player);
        location.getChunk().load(true);
        player.teleport(location);
    }

    public static Location getSpawnLocation(Player player)
    {
        StatusPlayer statusPlayer = StatusPlayer.wrap(player.getUniqueId());
        Stat stat = Stat.wrap(statusPlayer.getPlayerUUID());
        stat.setMana(stat.getMaxMana());
        stat.setStamina(stat.getMaxStamina());
        String organization = statusPlayer.getStatus().getOrganization();
        SpawnPlot plot = (SpawnPlot) LostShardPlugin.getPlotManager().getPlot(organization);
        if(plot == null || plot.getSpawn() == null) {
            Bukkit.broadcastMessage(ERROR_COLOR + "The spawn is not created for " + organization + ".") ;
            return null;
        }

        return plot.getSpawn();
    }
}
