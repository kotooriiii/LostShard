package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
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

        event.getPlayer().openInventory(PlotBannerListener.getInventory((Player) event.getPlayer()));

    }

    @EventHandler
    public void onPlotCreate(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        Inventory inv = event.getInventory();

        boolean isEmpty = true;
        for (ItemStack itemStack : inv.getContents()) {
            if (itemStack == null || itemStack.getType().isAir())
                continue;
            if (itemStack.getType() == Material.FEATHER || itemStack.getType() == Material.REDSTONE) {
                isEmpty = false;
                break;
            }

        }
        if (!isEmpty) {
            event.getPlayer().getInventory().addItem(new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1));
           // sendMessage((Player) event.getPlayer(), "Take the redstone and feather from the chest!", ChapterMessageType.HELPER);
            return;
        }

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        setComplete();
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
