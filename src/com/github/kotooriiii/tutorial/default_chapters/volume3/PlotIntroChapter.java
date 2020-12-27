package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.events.PlotCreateEvent;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PlotIntroChapter extends AbstractChapter {
    private boolean isFound, isHologramCreated, exited;
    private Location location;
    private static Zone exitOrderZone = new Zone(738, 754, 72, 65, 796, 812);
    private static Zone plotCreateZone = new Zone(999, 958, 83, 92, 767, 727);
    private static Zone abandonMissionZone = new Zone(920, 1020, 0, 100, 813, 814);

    public PlotIntroChapter() {
        this.isFound = false;
    }

    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);


        sendMessage(player, "You can't stay here forever!\nGo outside and make a plot.", ChapterMessageType.HOLOGRAM_TO_TEXT);
    }

    @Override
    public void onDestroy() {

    }


    @EventHandler
    public void onLeaveOrder(PlayerMoveEvent event) {
        if (exited)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!PlotIntroChapter.getExitOrderZone().contains(event.getTo()))
            return;

        exited = true;
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 * 5, 3, false, false, false));
    }

    @EventHandler
    public void onAppropiatePlot(PlayerMoveEvent event) {

        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;
        if (LostShardPlugin.getPlotManager().hasNearbyPlots(event.getTo())) {
            isFound = false;
            return;
        }

        if (isFound)
            return;

        isFound = true;

        if (!isHologramCreated)
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        isHologramCreated = true;
        event.getPlayer().removePotionEffect(PotionEffectType.SPEED);

        // sendMessage(event.getPlayer(), "This is a good spot for a plot.\nPlots cost 10 gold and 1 diamond to create.\nType /plot create (name) to create your plot.");
    }




    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlotCreate(PlotCreateEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (event.isCancelled())
            return;

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        LostShardPlugin.getTutorialManager().wrap(getUUID()).setPlot(true);
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
        sendMessage(event.getPlayer(), "You must place the plot banner before continuing.", ChapterMessageType.HELPER);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBannerPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (event.isCancelled())
            return;

        event.setCancelled(true);

        if (ShardPlotPlayer.wrap(event.getPlayer().getUniqueId()).getPlotsOwned().length > 0) {
            event.getPlayer().sendMessage(ERROR_COLOR + "You can only create one plot in the tutorial phase.");
            return;
        }
        if (!plotCreateZone.contains(event.getBlockPlaced().getLocation())) {
            sendMessage(event.getPlayer(), "Move the plot banner closer to the elevated area by the body of water.", ChapterMessageType.HELPER);
            return;
        }

        if (!(event.getBlockPlaced().getState() instanceof Banner
                && event.getBlockPlaced().getRelative(BlockFace.DOWN).getType().getKey().getKey().toUpperCase().endsWith("_WOOL"))) {
            sendMessage(event.getPlayer(), "Place the plot banner on the wool block.", ChapterMessageType.HELPER);
            return;
        }
        event.setCancelled(false);
        event.getPlayer().getInventory().setItemInMainHand(null);
        event.getPlayer().sendBlockChange(event.getBlockPlaced().getLocation(), event.getBlockPlaced().getBlockData());
//        Bank bank = LostShardPlugin.getBankManager().wrap(event.getPlayer().getUniqueId());
//        bank.setCurrency(bank.getCurrency() - PlayerPlot.CREATE_COST);
//        ItemStack[] ingredients = new ItemStack[]{new ItemStack(Material.DIAMOND, 1)};
//        event.getPlayer().getInventory().remove(ingredients[0]);
    }



    public static Zone getExitOrderZone() {
        return exitOrderZone;
    }
}
