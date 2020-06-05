package com.github.kotooriiii.npc;

import com.github.kotooriiii.LostShardPlugin;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ShardClone extends ShardLocationNPC {
    /**
     * Creates a new NPC with the given name and following skin.
     *
     * @param world
     * @param name  The name of the NPC
     */
    public ShardClone(World world, String name) {
        super(world, "", name, Skin.GUARD);
    }

    /**
     * Spawns the Guard on the given parameters
     *
     * @param x     The block on the x coordinate
     * @param y     The block on the y coordinate
     * @param z     The block on the z coordinate
     * @param yaw   The yaw of the guard
     * @param pitch The pitch of the guard
     */
    public boolean spawn(int x, int y, int z, float yaw, float pitch) {
        if (isSpawned() || isDestroyed())
            return false;
        //Set spawn to true
        super.setSpawned(true);



        //Set location to the center of a block.
        super.setCurrentLocation(new Location(getCurrentLocation().getWorld(), x + 0.5, y, z + 0.5, yaw, pitch));
        getNPC().setLocation(x + 0.5, y, z + 0.5, yaw, pitch);
        //Rotate the head. Fix it since for some reason nms has it only working of yaw

        //Packet for playing out a player
        PacketPlayOutPlayerInfo playerDeclarePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, getNPC());
        //Spawn the player
        PacketPlayOutNamedEntitySpawn playerSpawnPacket = new PacketPlayOutNamedEntitySpawn(getNPC());

        //Send packets
        sendPacket(playerDeclarePacket);
        sendPacket(playerSpawnPacket);

        //Points to all corners
        armorStands = new EntityArmorStand[4];

        armorStands[0] = new EntityArmorStand(((CraftWorld) getCurrentLocation().getWorld()).getHandle(), getCurrentLocation().getBlockX() + 0.75, getCurrentLocation().getBlockY(), getCurrentLocation().getBlockZ() + 0.75); //max max,max
        armorStands[1] = new EntityArmorStand(((CraftWorld) getCurrentLocation().getWorld()).getHandle(), getCurrentLocation().getBlockX() + 0.75, getCurrentLocation().getBlockY(), getCurrentLocation().getBlockZ() + 0.25); //combo max,min
        armorStands[2] = new EntityArmorStand(((CraftWorld) getCurrentLocation().getWorld()).getHandle(), getCurrentLocation().getBlockX() + 0.25, getCurrentLocation().getBlockY(), getCurrentLocation().getBlockZ() + 0.75); //combo min,max
        armorStands[3] = new EntityArmorStand(((CraftWorld) getCurrentLocation().getWorld()).getHandle(), getCurrentLocation().getBlockX() + 0.25, getCurrentLocation().getBlockY(), getCurrentLocation().getBlockZ() + 0.25); //min min,min


        for (EntityArmorStand armorStand : armorStands) {
            armorStand.setCustomName(new ChatMessage("NPCHitBox"));
            armorStand.setNoGravity(true);//float
            armorStand.setInvulnerable(false);//make sure its false so events are called
            armorStand.setInvisible(true); //FIX THIS AT COMPILETIME
            freezeEntity(armorStand); //FREEZE entity cannot move this.

            ((CraftWorld) getCurrentLocation().getWorld()).addEntity(armorStand, CreatureSpawnEvent.SpawnReason.CUSTOM); //Add entity to the world. get out of packets and put to server

        }

        playerInteractManager.setGameMode(EnumGamemode.SURVIVAL);



        return true;
    }

    public void move(double x, double y, double z, float yaw, float pitch) {
        short multiplier = 4096;
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook movePacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(this.getNPC().getId(), (short) (x * multiplier), (short) (y * multiplier), (short) (z * multiplier), (byte) yaw, (byte) pitch, true);
        sendPacket(movePacket);
        rotateHead(yaw, pitch);
    }

    public void move(Vector vector) {

        getNPC().getBukkitEntity().setVelocity(vector);
        getNPC().setMot(vector.getX(), vector.getY(), vector.getZ());
        getNPC().move(EnumMoveType.SELF, new Vec3D(vector.getX(), vector.getY(), vector.getZ()));

    }
}
