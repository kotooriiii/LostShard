package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.plots.struct.SpawnPlot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class SignChangeListener implements Listener {

    public static final String BUILD_CHANGE_ID = ChatColor.YELLOW + "[Build]";
    private static final HashSet<Location> set = new HashSet<>();

    @EventHandler

    public void onPlaceSign(SignChangeEvent event) {
        Block block = event.getBlock();
        if (block == null)
            return;
        if (!isSign(block))
            return;
        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(block.getLocation());
        if (plot == null)
            return;

        if (!(plot.getClass().equals(SpawnPlot.class)))
            return;
        if (!event.getLine(0).equals("[Build]")) {
            set.remove(event.getBlock());
            return;
        }

        if (hasSignBuilder(block.getLocation())) {
            event.getPlayer().sendMessage(ERROR_COLOR + "You can't have more than one build changer location!");
            event.getBlock().breakNaturally();
            return;
        }

        event.setLine(0, BUILD_CHANGE_ID);
        set.add(block.getLocation());
    }

    @EventHandler
    public void onRemove(EntityExplodeEvent event) {

        for (Block block : event.blockList()) {
            if (isSign(block))
                set.remove(block.getLocation());
        }
    }

    @EventHandler
    public void onRemove(BlockBurnEvent event) {

        Block block = event.getBlock();
        if (isSign(block))
            set.remove(block.getLocation());

    }

    @EventHandler
    public void onRemove(BlockBreakEvent event) {

        Block block = event.getBlock();
        if (isSign(block))
            set.remove(block.getLocation());

    }

    @EventHandler
    public void onChangeBuild(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null)
            return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (!isSign(block))
            return;
        if (!hasSignCode(block))
            return;
        if (hasSignCode(block) && !set.contains(block.getLocation())) {
            block.breakNaturally();
            set.remove(block.getLocation());
            event.getPlayer().sendMessage(STANDARD_COLOR + "The build location was out of date...");
            return;
        }

        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(block.getLocation());
        if (plot != null) {
            boolean isAllowedToRotateInPlayerPlot = isAllowedToRotateInPlayerPlot(plot, event.getPlayer());
            if (plot.getClass().equals(SpawnPlot.class) || isAllowedToRotateInPlayerPlot) {
                event.getPlayer().sendMessage(STANDARD_COLOR + "You rotated your build.");
                LostShardPlugin.getSkillManager().getSkillPlayer(event.getPlayer().getUniqueId()).rotate();
                event.getPlayer().getWorld().strikeLightningEffect(event.getPlayer().getLocation());
                event.getPlayer().setHealth(0);
                return;
            } else if (!isAllowedToRotateInPlayerPlot) {
                event.getPlayer().sendMessage(ERROR_COLOR + "This build changer is private.");
            }
        }


    }

    private boolean isAllowedToRotateInPlayerPlot(Plot plot, Player player) {
        if (!plot.getClass().equals(PlayerPlot.class))
            return false;
        PlayerPlot playerPlot = (PlayerPlot) plot;
        return playerPlot.isFriend(player.getUniqueId()) || playerPlot.isJointOwner(player.getUniqueId()) || playerPlot.isOwner(player.getUniqueId());
    }

    private boolean isSign(Block b) {
        switch (b.getType()) {
            case SPRUCE_SIGN:
            case ACACIA_SIGN:
            case BIRCH_SIGN:
            case DARK_OAK_SIGN:
            case JUNGLE_SIGN:
            case OAK_SIGN:
                return true;
            default:
                return false;
        }
    }

    private boolean hasSignCode(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String code = sign.getLine(0);
            if (code.equals(SignChangeListener.BUILD_CHANGE_ID))
                return true;
        }

        return false;
    }

    public static boolean hasSignBuilder(Location location) {
        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(location);
        if (plot == null)
            return false;
        for (Location buildSign : set) {
            if (plot.contains(buildSign))
                return true;
        }
        return false;
    }

    public static Location getSignBuilder(Location location) {
        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(location);
        if (plot == null)
            return null;
        for (Location buildSign : set) {
            if (plot.contains(buildSign))
                return buildSign;
        }
        return null;
    }

    public static Location[] getBuildChangeLocations() {
        return set.toArray(new Location[set.size()]);
    }
}
