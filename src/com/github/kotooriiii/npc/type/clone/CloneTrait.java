package com.github.kotooriiii.npc.type.clone;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.Skin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.ai.PrioritisableGoal;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.

public class CloneTrait extends Trait {

    //Static
    private final int socialDistance = 5;

    private Player hostPlayer;
    private int x = 3;
    private int z = 3;

    public CloneTrait() {
        super("CloneTrait");
    }


    public CloneTrait(Player hostPlayer, int x, int z) {
        super("CloneTrait");
        this.hostPlayer = hostPlayer;
        this.x = x;
        this.z = z;
    }


    // Here you should load up any values you have previously saved (optional).
    // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
    // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
    // This is called BEFORE onSpawn, npc.getBukkitEntity() will return null.
    public void load(DataKey key) {
        //isBusy = key.getBoolean("isBusy", false);
    }

    // Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
    public void save(DataKey key) {
        //key.setBoolean("isBusy", isBusy);
    }

    // An example event handler. All traits will be registered automatically as Bukkit Listeners.
    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCDeathEvent event) {
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        if (!event.getNPC().equals(this.getNPC()))
            return;

        Location location = event.getNPC().getStoredLocation();
        location.getWorld().spawnParticle(Particle.SNOW_SHOVEL, location, 1, 0.3, 0.3, 0.3);
        location.getWorld().playSound(location, Sound.ENTITY_VILLAGER_NO, 10, 0.0f);

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player movingPlayer = event.getPlayer();
        if (!movingPlayer.equals(hostPlayer))
            return;

        final int xOffset = 4;
        final int zOffset = 3;

        final Location moveTo = event.getTo();
        final Location moveFrom = event.getFrom();

        final Location subbed = moveTo.clone().subtract(moveFrom.clone());


        Location moveToNPC = getNPC().getStoredLocation().add(subbed);

        Location oldTarget = getNPC().getNavigator().getTargetAsLocation();
        if(oldTarget != null)
            moveToNPC = oldTarget.add(subbed);

    //   final Location moveToNPC = new Location(moveTo.getWorld(), moveTo.getX() + x + xOffset, getNPC().getStoredLocation().getY()-2, moveTo.getZ() + z + zOffset, moveTo.getYaw(), moveTo.getPitch());

//        if (moveFrom.getX() == moveTo.getX() && moveFrom.getY() == moveTo.getY() && moveFrom.getZ() == moveTo.getZ()) {
//           getNPC().faceLocation(hostPlayer.getLastTwoTargetBlocks(null, 100).get(0).getLocation());
//           return;
//        }

        if (hostPlayer.isSprinting()) {
            getNPC().getNavigator().getLocalParameters().baseSpeed(2);
            getNPC().getStoredLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, getNPC().getStoredLocation().add(0,1,0), 10,0, 0, 0, getNPC().getStoredLocation().getBlock().getBlockData());
        } else {
            getNPC().getNavigator().getLocalParameters().baseSpeed(1);

        }
        getNPC().getNavigator().setTarget(moveToNPC);

    }

    // Called every tick
    @Override
    public void run() {
        if (getNPC() == null)
            return;
        if (!getNPC().isSpawned())
            return;
        if (hostPlayer == null || !hostPlayer.isOnline()) {
            return;
        }
        ItemStack[] armorContents = hostPlayer.getInventory().getArmorContents();
        if (armorContents[0] != null)
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, armorContents[0].clone());
        if (armorContents[1] != null)
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, armorContents[1].clone());
        if (armorContents[2] != null)
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, armorContents[2].clone());
        if (armorContents[3] != null)
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, armorContents[3].clone());
        if (hostPlayer.getInventory().getItemInMainHand() != null)
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, hostPlayer.getInventory().getItemInMainHand().clone());
    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
    }

    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getBukkitEntity() is still valid.
    @Override
    public void onDespawn() {
    }

    //Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
    //This is called AFTER onAttach and AFTER Load when the server is started.
    @Override
    public void onSpawn() {
        ItemStack[] armorContents = hostPlayer.getInventory().getArmorContents();
        if (armorContents[0] != null)
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, armorContents[0].clone());
        if (armorContents[1] != null)
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, armorContents[1].clone());
        if (armorContents[2] != null)
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, armorContents[2].clone());
        if (armorContents[3] != null)
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, armorContents[3].clone());
        if (hostPlayer.getInventory().getItemInMainHand() != null)
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, hostPlayer.getInventory().getItemInMainHand().clone());


        //range
        getNPC().getNavigator().getLocalParameters().useNewPathfinder();
        getNPC().getNavigator().getLocalParameters().range(150);


        //Despawn
        new BukkitRunnable() {
            @Override
            public void run() {
                getNPC().despawn(DespawnReason.PLUGIN);
                getNPC().destroy();
                CitizensAPI.getNPCRegistry().deregister(getNPC());
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 30);

    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }

    //BASIC GETTERS/SETTERS


    //END OF BASIC GETTERS/SETTERS

    public boolean isSocialDistance(Location loc) {
        int xIntDiff = loc.getBlockX() - getNPC().getStoredLocation().getBlockX();
        int yIntDiff = loc.getBlockY() - getNPC().getStoredLocation().getBlockY();
        int zIntDiff = loc.getBlockZ() - getNPC().getStoredLocation().getBlockZ();
        return Math.abs(xIntDiff) <= socialDistance && Math.abs(yIntDiff) <= socialDistance && Math.abs(zIntDiff) <= socialDistance;
    }
}
