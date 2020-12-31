package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.plots.PlotBanner;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.events.PlotCreateEvent;
import com.github.kotooriiii.plots.listeners.PlotBannerListener;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PlotGrabChestChapter extends AbstractChapter {
    private static Zone abandonMissionZone = new Zone(920, 1020, 0, 100, 813, 814);

    public PlotGrabChestChapter() {

    }

    @Override
    public void onBegin() {
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onPlotGrab(InventoryOpenEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(!(event.getPlayer() instanceof Player))
            return;
        if(event.getView().getTitle().equals(PlotBannerListener.getTitle()))
            return;

        event.getPlayer().openInventory(PlotBannerListener.getInventory((Player) event.getPlayer()));
        event.setCancelled(true);

    }

    @EventHandler
    public void onPlotCreate(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!event.getView().getTitle().equals(PlotBannerListener.getTitle()))
            return;

        final Player player = (Player) event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!player.isOnline())
                {
                    this.cancel();
                    return;
                }

                if(player.getInventory().contains(Material.FEATHER) && player.getInventory().contains(Material.REDSTONE))
                {
                    LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
                    setComplete();
                    this.cancel();
                    return;
                } else
                {
                    sendMessage((Player) event.getPlayer(), "Take the redstone and feather from the chest!", ChapterMessageType.HELPER);
                    this.cancel();
                    return;
                }

            }
        }.runTaskLater(LostShardPlugin.plugin, 10);
    }

    @EventHandler
    public void onMoveWithoutPlot(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!abandonMissionZone.contains(event.getTo()))
            return;
        sendMessage(event.getPlayer(), "You must take the items in the chest before continuing.", ChapterMessageType.HELPER);
        event.setCancelled(true);
    }


}
