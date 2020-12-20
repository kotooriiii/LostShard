package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.plots.struct.SpawnPlot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
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
    private static HashSet<Location> set = new HashSet<>();

    public static void save() {
        FileManager.write(set);
    }

    public static void remove(Location location) {
        location.getBlock().breakNaturally();
        set.remove(location);
    }

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

        if (!(plot.getClass().equals(SpawnPlot.class))) {
            if (plot instanceof PlayerPlot) {
                if (!((PlayerPlot) plot).isTown())
                    return;
            } else {
                return;
            }
        }

        if (!event.getLine(0).equals("[Build]") || !isValidGoldBlock(block)) {
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
        if ((hasSignCode(block) && !set.contains(block.getLocation())) || !isValidGoldBlock(block)) {
            block.breakNaturally();
            set.remove(block.getLocation());
            event.getPlayer().sendMessage(STANDARD_COLOR + "The build location was out of date...");
            return;
        }
        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(block.getLocation());
        if (plot != null) {
            boolean isAllowedToRotateInPlayerPlot = isAllowedToRotateInPlayerPlot(plot, event.getPlayer());
            if (plot.getClass().equals(SpawnPlot.class) || isAllowedToRotateInPlayerPlot) {
                LostShardPlugin.getSkillManager().getSkillPlayer(event.getPlayer().getUniqueId()).rotate();
                event.getPlayer().sendMessage(STANDARD_COLOR + "You rotated to build " + LostShardPlugin.getSkillManager().getSkillPlayer(event.getPlayer().getUniqueId()).getActiveIndex() + ".");
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

    public static boolean isValidGoldBlock(Block b) {
        switch (b.getType()) {
            case SPRUCE_SIGN:
            case ACACIA_SIGN:
            case BIRCH_SIGN:
            case DARK_OAK_SIGN:
            case JUNGLE_SIGN:
            case OAK_SIGN:
                return b.getRelative(BlockFace.DOWN).getType().equals(Material.GOLD_BLOCK);
            case SPRUCE_WALL_SIGN:
            case JUNGLE_WALL_SIGN:
            case ACACIA_WALL_SIGN:
            case BIRCH_WALL_SIGN:
            case DARK_OAK_WALL_SIGN:
            case OAK_WALL_SIGN:
                WallSign sign = (WallSign) b.getBlockData();
                return b.getRelative(sign.getFacing().getOppositeFace()).getType().equals(Material.GOLD_BLOCK);
            default:
                return false;
        }
    }

    private static boolean isSign(Block b) {
        switch (b.getType()) {
            case SPRUCE_SIGN:
            case ACACIA_SIGN:
            case BIRCH_SIGN:
            case DARK_OAK_SIGN:
            case JUNGLE_SIGN:
            case OAK_SIGN:
            case SPRUCE_WALL_SIGN:
            case JUNGLE_WALL_SIGN:
            case ACACIA_WALL_SIGN:
            case BIRCH_WALL_SIGN:
            case DARK_OAK_WALL_SIGN:
            case OAK_WALL_SIGN:
                return true;
            default:
                return false;
        }
    }

    private static boolean hasSignCode(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String code = sign.getLine(0);
            if (code.equals(SignChangeListener.BUILD_CHANGE_ID))
                return true;
        }

        return false;
    }

    public static boolean isOld(Block block) {
        if (block == null)
            return true;
        if (!isSign(block))
            return true;
        if (!hasSignCode(block))
            return true;
        if (!isValidGoldBlock(block))
            return true;
        return false;
    }

    public static boolean hasSignBuilder(Location location) {
        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(location);
        if (plot == null)
            return false;

        clean();
        for (Location buildSign : set) {
            if (plot.contains(buildSign)) {
                return true;
            }
        }
        return false;
    }

    private static void clean() {
        for (Location buildSign : set) {
            if (isOld(buildSign.getBlock())) {
                set.remove(buildSign);
                if (isSign(buildSign.getBlock()))
                    buildSign.getBlock().breakNaturally();
            }
        }
    }

    public static Location getSignBuilder(Location location) {
        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(location);
        if (plot == null)
            return null;
        clean();
        for (Location buildSign : set) {
            if (plot.contains(buildSign)) {

                return buildSign;
            }
        }
        return null;
    }

    public static Location[] getBuildChangeLocations() {

        clean();

        return set.toArray(new Location[set.size()]);
    }

    public static void setBuildChangers(HashSet<Location> locations) {
        set = locations;
    }

    public static boolean isNearbySign(Location testingLocation) {
        final int NEARBY_RANGE = 5;

        for (Location location : getBuildChangeLocations()) {
            if (!location.getWorld().equals(testingLocation.getWorld()))
                continue;
            if (location.distance(testingLocation) <= NEARBY_RANGE)
                return true;
        }
        return false;
    }
}
