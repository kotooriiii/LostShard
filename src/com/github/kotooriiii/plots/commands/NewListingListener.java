package com.github.kotooriiii.plots.commands;

import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.npc.type.vendor.VendorTrait;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class NewListingListener implements Listener {
    @EventHandler
    public void onChat(ShardChatEvent event)
    {
        Player player = event.getPlayer();

        if (!VendorCommand.getNewListingMap().containsKey(player.getUniqueId()))
            return;

        String msg = event.getMessage();

//        Double totalPrice;
//        try {
//            totalPrice = NumberUtils.createDouble(msg);
//            if (totalPrice == null || totalPrice < 0) {
//                player.sendMessage(ERROR_COLOR + "The total price must be a positive number.");
//                return;
//            }
//        } catch (NumberFormatException e) {
//            player.sendMessage(ERROR_COLOR + "The total price must be a positive number.");
//            return;
//        }

        VendorCommand.getNewListingMap().remove(player.getUniqueId());
        player.performCommand("vendor stock " + msg);
        event.setCancelled(true);
    }

    @EventHandler
    public void onChat(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;

        if(VendorCommand.getNewListingMap().isEmpty())
            return;

        final VendorTrait vendorTrait = VendorCommand.getNewListingMap().get(player.getUniqueId());

        if (vendorTrait == null)
            return;

        //Moved a block and is in map
        if(vendorTrait.isSocialDistance(player.getLocation()))
            return;

        player.sendMessage(ERROR_COLOR + "You moved too far from the Vendor you were stocking.");
        VendorCommand.getNewListingMap().remove(player.getUniqueId());
    }

    @EventHandler
    public void onChat(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;

        if(VendorCommand.getNewListingMap().isEmpty())
            return;

        final VendorTrait vendorTrait = VendorCommand.getNewListingMap().get(player.getUniqueId());

        if (vendorTrait == null)
            return;

        //Moved a block and is in map
        if(vendorTrait.isSocialDistance(player.getLocation()))
            return;

        player.sendMessage(ERROR_COLOR + "You moved too far from the Vendor you were stocking.");
        VendorCommand.getNewListingMap().remove(player.getUniqueId());
    }

    @EventHandler
    public void onChat(PlayerDeathEvent event)
    {
        Player player = event.getEntity();

        if(VendorCommand.getNewListingMap().isEmpty())
            return;

        final VendorTrait vendorTrait = VendorCommand.getNewListingMap().get(player.getUniqueId());

        if (vendorTrait == null)
            return;

        //Moved a block and is in map
        if(vendorTrait.isSocialDistance(player.getLocation()))
            return;

        player.sendMessage(ERROR_COLOR + "You moved too far from the Vendor you were stocking.");
        VendorCommand.getNewListingMap().remove(player.getUniqueId());
    }
}
