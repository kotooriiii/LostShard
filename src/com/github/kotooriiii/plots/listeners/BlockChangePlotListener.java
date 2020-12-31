package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.PlotManager;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import fr.neatmonster.nocheatplus.utilities.ds.bktree.BKLevenshtein;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class BlockChangePlotListener implements Listener {

    /**
     * Called when an Enderman changes a block in a plot.
     *
     * @param entityChangeBlockEvent The event being called
     */
    @EventHandler
    public void onBlockChangePlot(EntityChangeBlockEvent entityChangeBlockEvent) {
        final Block block = entityChangeBlockEvent.getBlock();
        final Location location = block.getLocation();
        final Entity en = entityChangeBlockEvent.getEntity();

        if (en == null || block == null)
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {
                //Check entity
                //If entity is not a player then cancel it
                if (en instanceof Enderman) {
                    entityChangeBlockEvent.setCancelled(true);
                    return;
                }
                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when a Player breaks a block in a staff plot OR in a plot they don't have permissions to build.
     *
     * @param blockBreakEvent Event being called
     */
    @EventHandler
    public void onBlockChangePlot(BlockBreakEvent blockBreakEvent) {
        final Block block = blockBreakEvent.getBlock();
        final Location location = block.getLocation();
        //Check entity
        final Player playerBlockBreak = blockBreakEvent.getPlayer();
        //If entity is not a player then cancel it
        final UUID playerUUID = playerBlockBreak.getUniqueId();

        if (playerBlockBreak.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {

                    playerBlockBreak.sendMessage(ERROR_COLOR + "Cannot break blocks here, " + plot.getName() + " is protected.");

                    blockBreakEvent.setCancelled(true);
                    return;
                }

                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    playerBlockBreak.sendMessage(ERROR_COLOR + "Cannot break blocks here, " + plot.getName() + " is protected.");
                    blockBreakEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when a living Vehicle takes damage ( horse, mule, donkey, pig with saddle) in a staff plot OR in a plot
     * they don't have permission in building.
     *
     * @param vehicleDamageEvent The event being called.
     */
    @EventHandler
    public void onEntityDamage(VehicleDamageEvent vehicleDamageEvent) {
        final Vehicle vehicle = vehicleDamageEvent.getVehicle();

        if (CitizensAPI.getNPCRegistry().isNPC(vehicle))
            return;


        final Location location = vehicle.getLocation();
        //Check entity
        final Entity entity = vehicleDamageEvent.getAttacker();
        if (!(entity instanceof Player))
            return;
        if (vehicle instanceof LivingEntity)
            return;

        //If entity is not a player then cancel it
        final Player player = (Player) entity;
        final UUID playerUUID = player.getUniqueId();

        if (player.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {

                    //   playerBlockBreak.sendMessage(ERROR_COLOR + "Cannot interact with blocks here, " + plot.getName() + " is protected.");
                    vehicleDamageEvent.setCancelled(true);
                    return;
                }

                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    vehicleDamageEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when an Armor Stand is being broken by a player at a staff plot or plot with no building permissions.
     *
     * @param event The event being called.
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();

        final Location location = entity.getLocation();
        //Check entity
        final Entity damagerEntity = event.getDamager();

        if (CitizensAPI.getNPCRegistry().isNPC(entity) || CitizensAPI.getNPCRegistry().isNPC(damagerEntity))
            return;


        if (!(entity instanceof ArmorStand))
            return;

        if (!(damagerEntity instanceof Player))
            return;

        //If entity is not a player then cancel it
        final Player player = (Player) damagerEntity;
        final UUID playerUUID = player.getUniqueId();

        if (player.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {

                    //   playerBlockBreak.sendMessage(ERROR_COLOR + "Cannot interact with blocks here, " + plot.getName() + " is protected.");
                    event.setCancelled(true);
                    return;
                }

                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    event.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when a vine grows at a staff plot.
     *
     * @param event
     */
    @EventHandler
    public void onVineSpread(BlockSpreadEvent event) {

        final Block source = event.getSource();
        if (source.getType() != Material.VINE)
            return;


        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(source.getLocation())) {

                //Staff no permission
                if (plot.getType().isStaff()) {

                    //   playerBlockBreak.sendMessage(ERROR_COLOR + "Cannot interact with blocks here, " + plot.getName() + " is protected.");
                    event.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }

    }

    @EventHandler
    public void onFlowToPlot(BlockFromToEvent event) {


        final Block source = event.getBlock();
        final Block to = event.getToBlock();

        PlotManager plotManager = LostShardPlugin.getPlotManager();

        Plot sourcePlot = plotManager.getStandingOnPlot(source.getLocation());
        Plot toPlot = plotManager.getStandingOnPlot(to.getLocation());

        //There is a plot where the water will flow
        if (toPlot != null) {
            //Then check if the source is inside a plot.

            //If the source is not in a plot OR the plots aren't equal
            if (sourcePlot == null || !sourcePlot.equals(toPlot)) {
                event.setCancelled(true);
                return;
            }
        }
    }


    /**
     * Called when a Player left clicks a block (attempt to break) on a staff plot or plot with no permissions..
     *
     * @param playerInteractEvent
     */
    @EventHandler
    public void onBlockChangePlot(PlayerInteractEvent playerInteractEvent) {

        final Action action = playerInteractEvent.getAction();

        if (action != Action.LEFT_CLICK_BLOCK && action != Action.LEFT_CLICK_AIR)
            return;

        final Block block = playerInteractEvent.getClickedBlock();

        if (block == null)
            return;


              /*
        So far it's a LEFT CLICK event
        It's a block event
         */

        final Location location = block.getLocation();
        //Check entity
        final Player playerBlockBreak = playerInteractEvent.getPlayer();
        //If entity is not a player then cancel it
        final UUID playerUUID = playerBlockBreak.getUniqueId();

        if (playerBlockBreak.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {

                    //playerBlockBreak.sendMessage(ERROR_COLOR + "Cannot break blocks here, " + plot.getName() + " is protected.");
                  //  playerInteractEvent.setCancelled(true);
                    return;
                }

                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    //  playerBlockBreak.sendMessage(ERROR_COLOR + "Cannot break blocks here, " + plot.getName() + " is protected.");
                  //  playerInteractEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when a Player right clicks a block (attempt to spawn entity) on a staff plot or plot with no permissions..
     *
     * @param playerInteractEvent
     */
    @EventHandler
    public void onSpawn(PlayerInteractEvent playerInteractEvent) {

        final Action action = playerInteractEvent.getAction();

        if (action != Action.RIGHT_CLICK_BLOCK)
            return;

        final Block block = playerInteractEvent.getClickedBlock();

        if (block == null)
            return;

        if (playerInteractEvent.getItem() != null) {
            Material[] mats = new Material[]
                    {
                            Material.ACACIA_BOAT, Material.BIRCH_BOAT, Material.DARK_OAK_BOAT, Material.JUNGLE_BOAT, Material.OAK_BOAT,
                            Material.SPRUCE_BOAT, Material.MINECART, Material.CHEST_MINECART, Material.COMMAND_BLOCK_MINECART, Material.FURNACE_MINECART,
                            Material.TNT_MINECART, Material.HOPPER_MINECART
                    };

            boolean exists = false;
            for (Material mat : mats) {
                if (playerInteractEvent.getItem().getType() == mat) {
                    exists = true;
                    break;
                }
            }
            if (playerInteractEvent.getItem().getType().getKey().getKey().toUpperCase().endsWith("_SPAWN_EGG"))
                exists = true;

            if (!exists)
                return;
        } else {
            return;
        }


              /*
        So far it's a RIGHT CLICK event
        It's a block event
         */

        final Location location = block.getLocation();
        //Check entity
        final Player playerBlockBreak = playerInteractEvent.getPlayer();
        //If entity is not a player then cancel it
        final UUID playerUUID = playerBlockBreak.getUniqueId();

        if (playerBlockBreak.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {

                    playerBlockBreak.sendMessage(ERROR_COLOR + "Cannot spawn this here, " + plot.getName() + " is protected.");
                    playerInteractEvent.setCancelled(true);
                    return;
                }

                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    playerBlockBreak.sendMessage(ERROR_COLOR + "Cannot spawn this here, " + plot.getName() + " is protected.");
                    playerInteractEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when a Player empties a bucket at a staff plot or plot with no permissions.
     *
     * @param event
     */
    @EventHandler
    public void onBucketfill(PlayerBucketEmptyEvent event) {
        final Location location = event.getPlayer().getLocation();
        //Check entity
        final Entity entity = event.getPlayer();
        //If entity is not a player then cancel it
        Player player = (Player) entity;
        final UUID playerUUID = player.getUniqueId();

        if (player.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {

                    player.sendMessage(ERROR_COLOR + "Cannot drain buckets here, " + plot.getName() + " is protected.");
                    event.setCancelled(true);
                    return;
                }
                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    player.sendMessage(ERROR_COLOR + "Cannot drain buckets here, " + plot.getName() + " is protected.");

                    event.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when a Player fills a bucket at a staff plot or plot with no building permissions.
     *
     * @param event
     */
    @EventHandler
    public void onBucketfill(PlayerBucketFillEvent event) {
        final Location location = event.getPlayer().getLocation();
        //Check entity
        final Entity entity = event.getPlayer();
        //If entity is not a player then cancel it
        Player player = (Player) entity;
        final UUID playerUUID = player.getUniqueId();

        if (player.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {


                    player.sendMessage(ERROR_COLOR + "Cannot fill buckets here, " + plot.getName() + " is protected.");
                    event.setCancelled(true);
                    return;
                }
                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    player.sendMessage(ERROR_COLOR + "Cannot fill buckets here, " + plot.getName() + " is protected.");

                    event.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when an Item Frame is broken by a Player at a staff plot or plot with no building permissions.
     *
     * @param event
     */
    @EventHandler
    public void onItemFrameItemStack(EntityDamageByEntityEvent event) {

        final Location location = event.getEntity().getLocation();
        //If entity is not a player then cancel it
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;


        if (!(event.getEntity() instanceof ItemFrame))
            return;

        if (!(event.getDamager() instanceof Player)) {
            event.setCancelled(true);
            return;
        }


        final Player player = (Player) event.getDamager();
        final UUID playerUUID = player.getUniqueId();


        if (player.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {


                    player.sendMessage(ERROR_COLOR + "Cannot break entities here, " + plot.getName() + " is protected.");
                    event.setCancelled(true);
                    return;
                }
                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    player.sendMessage(ERROR_COLOR + "Cannot break entities here, " + plot.getName() + " is protected.");

                    event.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when a Player tries to remove an Item from an ItemFrame on a staff plot or plot with no building permission.
     *
     * @param event
     */
    @EventHandler
    public void onArmorStand(PlayerInteractAtEntityEvent event) {
        final Location location = event.getRightClicked().getLocation();
        //If entity is not a player then cancel it

        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();

        if (!(event.getRightClicked().getType() == EntityType.ARMOR_STAND))
            return;


        if (player.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {


                    player.sendMessage(ERROR_COLOR + "Cannot steal from armor stands here, " + plot.getName() + " is protected.");
                    event.setCancelled(true);
                    return;
                }
                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    player.sendMessage(ERROR_COLOR + "Cannot steal from armor stands here, " + plot.getName() + " is protected.");

                    event.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when a Player tries to remove an Item from an ItemFrame on a staff plot or plot with no building permission.
     *
     * @param event
     */
    @EventHandler
    public void onItemFrameItemStack(PlayerInteractEntityEvent event) {
        final Location location = event.getRightClicked().getLocation();
        //If entity is not a player then cancel it

        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();

        if (!(event.getRightClicked() instanceof ItemFrame))
            return;


        if (player.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {


                    player.sendMessage(ERROR_COLOR + "Cannot break entities here, " + plot.getName() + " is protected.");
                    event.setCancelled(true);
                    return;
                }
                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    player.sendMessage(ERROR_COLOR + "Cannot break entities here, " + plot.getName() + " is protected.");

                    event.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when a Player tries to remove an Item from an ItemFrame on a staff plot or plot with no building permission.
     *
     * @param event
     */
    @EventHandler
    public void onItemBreakEvent(HangingBreakByEntityEvent event) {
        final Location location = event.getEntity().getLocation();
        //Check entity
        final Entity entity = event.getRemover();
        //If entity is not a player then cancel it

        if (!(entity instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) entity;
        final UUID playerUUID = player.getUniqueId();

        if (player.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {


                    player.sendMessage(ERROR_COLOR + "Cannot break entities here, " + plot.getName() + " is protected.");
                    event.setCancelled(true);
                    return;
                }
                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    player.sendMessage(ERROR_COLOR + "Cannot break entities here, " + plot.getName() + " is protected.");

                    event.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    /**
     * Called when an Entity is spawned on a staff plot and that said entity is hostile.
     *
     * @param entitySpawnEvent
     */
    @EventHandler
    public void onSpawn(EntitySpawnEvent entitySpawnEvent) {
        if (LostShardPlugin.isTutorial())
            return;
        if (entitySpawnEvent.getEntity() instanceof Player)
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(entitySpawnEvent.getEntity()))
            return;
        Location spawnedLocation = entitySpawnEvent.getLocation();
        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(spawnedLocation);
        if (plot == null)
            return;

        if (!plot.getType().isStaff())
            return;

        if (!isHostile(entitySpawnEvent.getEntity()))
            return;

        entitySpawnEvent.setCancelled(true);

    }

    /**
     * Called when a block explodes and is inside a plot.
     *
     * @param event
     */
    @EventHandler
    public void onExplosion(BlockExplodeEvent event) {
        List<Block> blocksExploding = event.blockList();
        for (Block block : blocksExploding) {
            Location loc = block.getLocation();
            Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(loc);
            if (plot == null)
                continue;

            event.setCancelled(true);
        }
    }

    /**
     * Called when an entity explodes and blocks which should be removed are inside a plot.
     *
     * @param event
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocksExploding = event.blockList();
        for (Block block : blocksExploding) {
            Location loc = block.getLocation();
            Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(loc);
            if (plot == null)
                continue;

            event.setCancelled(true);
        }
    }

    /**
     * Called when a block is ignited and trying to spread inside a plot
     *
     * @param event
     */
    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        final Block block = event.getBlock();
        final Location location = block.getLocation();
        final BlockIgniteEvent.IgniteCause cause = event.getCause();
        //Iterate through all plots
//        final Block source = event.getIgnitingBlock();
//        if (source == null)
//            return;
        if (!cause.equals(BlockIgniteEvent.IgniteCause.SPREAD))
            return;
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    /**
     * Called when a Player tries to place a block on a staff plot or a plot with no building permissions.
     *
     * @param blockPlaceEvent
     */
    @EventHandler
    public void onBlockPlaceChangePlot(BlockPlaceEvent blockPlaceEvent) {
        final Block block = blockPlaceEvent.getBlock();
        final Location location = block.getLocation();
        //Check entity
        final Player playerBlockBreak = blockPlaceEvent.getPlayer();
        //If entity is not a player then cancel it
        final UUID playerUUID = playerBlockBreak.getUniqueId();

        if (playerBlockBreak.hasPermission(STAFF_PERMISSION))
            return;

        //Iterate through all plots
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {
            //If the block being interacted is in the location of a plot
            if (plot.contains(location)) {

                //Staff no permission
                if (plot.getType().isStaff()) {

                    playerBlockBreak.sendMessage(ERROR_COLOR + "Cannot place blocks here, " + plot.getName() + " is protected.");

                    blockPlaceEvent.setCancelled(true);
                    return;
                }
                PlayerPlot playerPlot = (PlayerPlot) plot;
                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    blockPlaceEvent.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {

        final Block pistonBlock = event.getBlock();
        final List<Block> blocks = event.getBlocks();
        final BlockFace directionMoved = event.getDirection();

        //Iterate through all plots
        plotLoop:
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {

            //If the plot contains the piston block, continue
            if (plot.contains(pistonBlock.getLocation()))
                continue;

            //The plot does not contain the piston block

            final Iterator<Block> iterator = blocks.iterator();

            blockLoop:
            while (iterator.hasNext()) {
                Block blockWhichWillBeMoved = iterator.next();
                Block movedBlock = blockWhichWillBeMoved.getRelative(directionMoved);

//          Add this if slime block attachments are not detected.
//                if(movedBlock.getType() == Material.SLIME_BLOCK)
//                {
//                    Block grabbedBlock = movedBlock.getRelative(directionMoved);
//                    if
//                }

                if (plot.contains(movedBlock.getLocation())) {
                    event.setCancelled(true);
                    break plotLoop;
                }
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {

        final Block pistonBlock = event.getBlock();
        final List<Block> blocks = event.getBlocks();
        final BlockFace directionMoved = event.getDirection();

        //Iterate through all plots
        plotLoop:
        for (Plot plot : LostShardPlugin.getPlotManager().getAllPlots()) {

            //If the plot contains the piston block, continue
            if (plot.contains(pistonBlock.getLocation()))
                continue;

            //The plot does not contain the piston block

            final Iterator<Block> iterator = blocks.iterator();

            blockLoop:
            while (iterator.hasNext()) {
                Block blockWhichWillBeMoved = iterator.next();

                if (plot.contains(blockWhichWillBeMoved.getLocation())) {
                    event.setCancelled(true);
                    break plotLoop;
                }
            }
        }
    }

    private boolean isHostile(Entity entity) {

        switch (entity.getType()) {

            case DROPPED_ITEM:
            case EXPERIENCE_ORB:
            case AREA_EFFECT_CLOUD:
            case EGG:
            case LEASH_HITCH:
            case PAINTING:
            case ARROW:
            case SNOWBALL:
            case FIREBALL:
            case SMALL_FIREBALL:
            case ENDER_PEARL:
            case ENDER_SIGNAL:
            case SPLASH_POTION:
            case THROWN_EXP_BOTTLE:
            case ITEM_FRAME:
            case WITHER_SKULL:
            case PRIMED_TNT:
            case FALLING_BLOCK:
            case FIREWORK:
            case SPECTRAL_ARROW:
            case SHULKER_BULLET:
            case DRAGON_FIREBALL:
            case ARMOR_STAND:
            case UNKNOWN:
            case MINECART_COMMAND:
            case MINECART:
            case MINECART_CHEST:
            case MINECART_FURNACE:
            case MINECART_TNT:
            case MINECART_HOPPER:
            case MINECART_MOB_SPAWNER:
            case EVOKER_FANGS:
            case BOAT:
            case FISHING_HOOK:
            case TRIDENT:
            case ENDER_CRYSTAL:
            case LLAMA_SPIT:
            case LIGHTNING:
            case PLAYER:
            default:
                return false;


            //todo might do something custom with values down here v
            case ILLUSIONER:
            case GIANT:
            case ENDER_DRAGON:
            case WITHER:
                return true;


            case ELDER_GUARDIAN:
            case WITHER_SKELETON:
            case STRAY:
            case HUSK:
            case ZOMBIE_VILLAGER:
            case SKELETON_HORSE:
            case ZOMBIE_HORSE:
            case EVOKER:
            case VEX:
            case VINDICATOR:
            case CREEPER:
            case SKELETON:
            case SPIDER:
            case ZOMBIE:
            case SLIME:
            case GHAST:
            case PIG_ZOMBIE:
            case ENDERMAN:
            case CAVE_SPIDER:
            case SILVERFISH:
            case BLAZE:
            case MAGMA_CUBE:
            case PHANTOM:
            case WITCH:
            case ENDERMITE:
            case GUARDIAN:
            case SHULKER:
            case DROWNED:
            case PILLAGER:
            case RAVAGER:
                return true;
            case IRON_GOLEM:
            case SNOWMAN:
            case DONKEY:
            case MULE:
            case MUSHROOM_COW:
            case PIG:
            case SHEEP:
            case COW:
            case CHICKEN:
            case SQUID:
            case WOLF:
            case OCELOT:
            case BAT:
            case HORSE:
            case RABBIT:
            case POLAR_BEAR:
            case LLAMA:
            case PARROT:
            case VILLAGER:
            case TURTLE:
            case COD:
            case SALMON:
            case PUFFERFISH:
            case TROPICAL_FISH:
            case DOLPHIN:
            case CAT:
            case PANDA:
            case TRADER_LLAMA:
            case WANDERING_TRADER:
            case FOX:
            case BEE:
                return false;
        }
    }
}
