package com.github.kotooriiii.plots.action;

import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.npc.type.vendor.VendorTrait;
import com.github.kotooriiii.plots.action.AbstractPlotAction;
import com.github.kotooriiii.plots.commands.PlotCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PlotActionListener implements Listener {
    @EventHandler
    public void onChat(ShardChatEvent event)
    {
        Player player = event.getPlayer();

        if(PlotCommand.actionMap.isEmpty())
            return;

        if (!PlotCommand.actionMap.containsKey(player.getUniqueId()))
            return;

        event.setCancelled(true);


        String msg = event.getMessage();

        final AbstractPlotAction plotAction = PlotCommand.actionMap.remove(player.getUniqueId());

        if(!plotAction.isKeyword(msg))
        {
            player.sendMessage(ERROR_COLOR + "You canceled the '" + plotAction.getType().toString().toLowerCase() + "' plot action.");
            return;
        }

        if(!plotAction.isRequirementMet())
            return;

        plotAction.apply();
    }

    @EventHandler
    public void onChat(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;

        if(PlotCommand.actionMap.isEmpty())
            return;

        if (!PlotCommand.actionMap.containsKey(player.getUniqueId()))
            return;


        final AbstractPlotAction plotAction = PlotCommand.actionMap.get(player.getUniqueId());

        if(plotAction.getPlot().contains(player.getLocation()))
            return;

        player.sendMessage(ERROR_COLOR + "You moved too far from the Plot." + " You canceled the '" + plotAction.getType().toString().toLowerCase() + "' plot action.");
        PlotCommand.actionMap.remove(player.getUniqueId());
    }


    @EventHandler
    public void onChat(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;

        if(PlotCommand.actionMap.isEmpty())
            return;

        if (!PlotCommand.actionMap.containsKey(player.getUniqueId()))
            return;


        final AbstractPlotAction plotAction = PlotCommand.actionMap.get(player.getUniqueId());

        if(plotAction.getPlot().contains(player.getLocation()))
            return;

        player.sendMessage(ERROR_COLOR + "You moved too far from the Plot." + " You canceled the '" + plotAction.getType().toString().toLowerCase() + "' plot action.");
        PlotCommand.actionMap.remove(player.getUniqueId());
    }



    @EventHandler
    public void onChat(PlayerDeathEvent event)
    {
        Player player = event.getEntity();

        if(PlotCommand.actionMap.isEmpty())
            return;

        if (!PlotCommand.actionMap.containsKey(player.getUniqueId()))
            return;

        final AbstractPlotAction plotAction = PlotCommand.actionMap.get(player.getUniqueId());

        player.sendMessage(ERROR_COLOR + "You moved too far from the Plot." + " You canceled the '" + plotAction.getType().toString().toLowerCase() + "' plot action.");
        PlotCommand.actionMap.remove(player.getUniqueId());
    }

}
