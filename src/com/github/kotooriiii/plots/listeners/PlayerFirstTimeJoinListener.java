package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.PlotBanner;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.SpawnPlot;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class PlayerFirstTimeJoinListener implements Listener {
    @EventHandler (priority =  EventPriority.HIGHEST)
    public void firstTimeJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if(player.hasPlayedBefore() || LostShardPlugin.isTutorial())
            return;

        Stat.wrap(player.getUniqueId()).setMillisInit(ZonedDateTime.now().toInstant().toEpochMilli());

        //player has not played before
        SpawnPlot spawnPlot = (SpawnPlot) LostShardPlugin.getPlotManager().getPlot("order");
        if(spawnPlot == null || spawnPlot.getSpawn() == null)
            return;

        Location spawnLocation = spawnPlot.getSpawn();
        spawnLocation.getChunk().load(true);
        player.teleport(spawnLocation);
        player.getInventory().addItem(PlotBanner.getInstance().getItem());


    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        ZoneId id = ZoneId.of("America/New_York");

        ZonedDateTime now = ZonedDateTime.now(id);


        for(PlayerPlot plot : ShardPlotPlayer.wrap(event.getPlayer().getUniqueId()).getPlotsOwned())
        {
            ZonedDateTime creationDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(plot.getCreationMillisecondsDate()),id);
            ZonedDateTime week1FromCreationDate =  creationDate.plusWeeks(1);
            ZonedDateTime week2FromCreationDate =  creationDate.plusWeeks(2);

            if(now.compareTo(week1FromCreationDate) <= 0)
            {
                //Maybe reminder in the future?
            }
            //If today's time is between 1 week - 2 weeks
            else if(now.compareTo(week2FromCreationDate) <= 0)
            {
                DecimalFormat df = new DecimalFormat("#.##");

                event.getPlayer().sendMessage(ChatColor.GOLD + "REMEMBER TO PAY YOUR PLOT TAX. YOUR CURRENT DAILY PLOT \"" + plot.getName() + "\" TAX IS: " + df.format(plot.getTax()) + ". MAKE SURE YOU HAVE ENOUGH FUNDS IN YOUR PLOT. TYPE \"/PLOT INFO\" WHILE STANDING IN YOUR PLOT TO SEE YOUR PLOT FUNDS.");
            }

        }


    }

}
