package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.sorcery.Gate;
import com.github.kotooriiii.sorcery.GateBlock;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PGTListener  implements Listener {
    HashSet<UUID> map = new HashSet<>();

    private boolean isBrokenGate(Player player) {
        Location entityLocation = player.getLocation();
        Location gateLocation = LostShardPlugin.getGateManager().getGateNearbyUpdatedLocation(entityLocation);

        if (gateLocation == null) {
            return false;
        }

        Gate gate = LostShardPlugin.getGateManager().getGate(gateLocation);
        if (gate == null) {
            return false;
        }

        if (!gate.isBuilt()) {
            player.sendMessage(ERROR_COLOR + "The gate was obstructed.");
            LostShardPlugin.getGateManager().removeGate(gate);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerEnterPortal(PlayerMoveEvent event) {

        if (!(event.getPlayer() instanceof Player))
            return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        if (map.contains(event.getPlayer().getUniqueId()))
            return;
        if (!event.getTo().getBlock().getType().equals(Material.NETHER_PORTAL))
            return;

        if (isBrokenGate(event.getPlayer()))
            return;

        map.add(event.getPlayer().getUniqueId());
        Bukkit.getPluginManager().callEvent(new PlayerPortalEvent((Player) event.getPlayer(), event.getTo(), event.getTo()));
    }

    @EventHandler
    public void onPlayerExitPortal(PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        if (event.getFrom().getBlock().getType().equals(Material.NETHER_PORTAL) && !event.getTo().getBlock().getType().equals(Material.NETHER_PORTAL)) {
            map.remove(event.getPlayer().getUniqueId());

        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerTeleportEvent event) {

        if (!(event.getPlayer() instanceof Player))
            return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        if (map.contains(event.getPlayer().getUniqueId()))
            return;
        if (!event.getTo().getBlock().getType().equals(Material.NETHER_PORTAL))
            return;

        if (isBrokenGate(event.getPlayer()))
            return;
        map.add(event.getPlayer().getUniqueId());
        Bukkit.getPluginManager().callEvent(new PlayerPortalEvent((Player) event.getPlayer(), event.getTo(), event.getTo()));
    }

    @EventHandler
    public void onPlayerPortalR(PlayerTeleportEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        if (event.getFrom().getBlock().getType().equals(Material.NETHER_PORTAL) && !event.getTo().getBlock().getType().equals(Material.NETHER_PORTAL)) {
            map.remove(event.getPlayer().getUniqueId());

        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        map.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerGameModeChangeEvent event) {
        map.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {

        Player player = (Player) event.getPlayer();

        Location entityLocation = player.getLocation();
        Location gateLocation = LostShardPlugin.getGateManager().getGateNearbyUpdatedLocation(entityLocation);

        if (gateLocation == null) {
            return;
        }

        Gate gate = LostShardPlugin.getGateManager().getGate(gateLocation);
        if (gate == null) {
            return;
        }

        if (!gate.isBuilt()) {
            player.sendMessage(ERROR_COLOR + "The gate was obstructed.");
            LostShardPlugin.getGateManager().removeGate(gate);
            event.setCancelled(true);
            return;
        }

        Location teleportingTo = gate.getTeleportTo(new GateBlock(gateLocation));
        if (teleportingTo == null)
            player.sendMessage(ERROR_COLOR + "The gate encountered a critical error.");
        else
            player.teleport(teleportingTo);

        event.setCancelled(true);
    }


    /**
     * This event appears to only affect entities othen than players.
     *
     * @param event
     */
    @EventHandler
    public void onPortalUse(EntityPortalEvent event) {

        Entity entity = event.getEntity();
        if (entity instanceof Player)
            return;
        if (entity instanceof Projectile || entity instanceof Explosive)
            return;

        Location entityLocation = event.getEntity().getLocation();
        Location gateLocation = LostShardPlugin.getGateManager().getGateNearbyUpdatedLocation(entityLocation);

        if (gateLocation == null) {
            return;
        }

        Gate gate = LostShardPlugin.getGateManager().getGate(gateLocation);
        if (gate == null) {
            return;
        }

        if (!gate.isBuilt()) {
            LostShardPlugin.getGateManager().removeGate(gate);
            event.setCancelled(true);
            return;
        }
        Vector vector = entity.getVelocity();
        Location teleportingTo = gate.getTeleportTo(new GateBlock(gateLocation));

        if (teleportingTo != null) {
            entity.teleport(new Location(teleportingTo.getWorld(), teleportingTo.getBlockX(), teleportingTo.getBlockY(), teleportingTo.getBlockZ(), entity.getLocation().getYaw(), entity.getLocation().getPitch()));
            entity.setVelocity(vector);
        }

        event.setCancelled(true);
    }

    HashMap<UUID, Boolean> projectileSet = new HashMap<>();

    /**
     * This event appears to only affect projectile  othen than players.
     **/


    private boolean isHittingPortal(Location location) {
        final int radius = 1;
        for (int x = 0; x < radius * 2 + 1; x++) {
            for (int y = 0; y < radius * 2 + 1; y++) {
                for (int z = 0; z < radius * 2 + 1; z++) {
                    Location testers = new Location(location.getWorld(), location.getBlockX() - radius + x, location.getBlockY() - radius + y, location.getBlockZ() - radius + z);
                    if (testers.getBlock().getType().equals(Material.NETHER_PORTAL))
                        return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Projectile) && !(entity instanceof Explosive))
            return;
        projectileSet.put(entity.getUniqueId(), false);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (isHittingPortal(entity.getLocation())) {
                    Boolean objectBool = projectileSet.get(entity.getUniqueId());
                    if (objectBool != null && !objectBool.booleanValue()) {
                        projectileSet.put(entity.getUniqueId(), true);

                        Location entityLocation = event.getEntity().getLocation();
                        Location gateLocation = LostShardPlugin.getGateManager().getGateNearbyUpdatedLocation(entityLocation);

                        if (gateLocation == null) {
                            return;
                        }

                        Gate gate = LostShardPlugin.getGateManager().getGate(gateLocation);
                        if (gate == null) {
                            return;
                        }

                        if (!gate.isBuilt()) {
                            LostShardPlugin.getGateManager().removeGate(gate);
                            return;
                        }

                        Vector vector = entity.getVelocity();
                        Location teleportingTo = gate.getTeleportTo(new GateBlock(gateLocation));
                        entity.teleport(teleportingTo.clone().add(0, 1, 0));
                        entity.setVelocity(vector);
                        if (entity instanceof TNTPrimed)
                            ((TNTPrimed) entity).setFuseTicks(((TNTPrimed) entity).getFuseTicks() + (20 * 4));

                    }

                }
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                projectileSet.remove(entity.getUniqueId());
                task.cancel();
            }
        }.runTaskLaterAsynchronously(LostShardPlugin.plugin, 20 * 15);
    }


    @EventHandler
    public void onCancelPortal(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null)
            return;

        if(event.getHand() != EquipmentSlot.HAND)
            return;

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == event.getAction().RIGHT_CLICK_BLOCK))
            return;
        ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (mainHand != null && !mainHand.getType().equals(Material.AIR))
            return;

        Gate gate = LostShardPlugin.getGateManager().getGate(block.getLocation());
        if (gate == null)
            return;

        if (!gate.getSource().equals(event.getPlayer().getUniqueId()))
            return;

        /*
        Is a block
        Is right click
        is a gate
        is owner of gate
         */

        if (event.getPlayer().isSneaking())
            LostShardPlugin.getGateManager().rotateGate(gate, block);
        else
            LostShardPlugin.getGateManager().removeGate(gate);


    }

    @EventHandler
    public void onRemove(EntityExplodeEvent event) {

        for (Block block : event.blockList()) {
            if (block == null)
                continue;
            Gate gate = LostShardPlugin.getGateManager().getGate(block.getLocation());
            if (gate == null)
                continue;
            LostShardPlugin.getGateManager().removeGate(gate);
        }
    }

    @EventHandler
    public void onRemove(BlockBreakEvent event) {

        Block block = event.getBlock();
        if (block == null)
            return;
        Gate gate = LostShardPlugin.getGateManager().getGate(block.getLocation());
        if (gate == null)
            return;
        LostShardPlugin.getGateManager().removeGate(gate);
    }

}
