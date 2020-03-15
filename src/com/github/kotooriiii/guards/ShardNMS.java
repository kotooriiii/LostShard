package com.github.kotooriiii.guards;

import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ShardNMS {

    private ArrayList<Packet> packetsStored = new ArrayList<>();

    protected ShardNMS()
    {}

    /**
     * Gets the value of the given object's field.
     * @param obj The object being referenced
     * @param field_name The private field of the object
     * @return The value of the given field
     */
    protected Object getField(Object obj, String field_name) {
        try {
            Field field = obj.getClass().getDeclaredField(field_name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the value of the given object's field.
     * @param obj The object being referenced
     * @param field_name The private field of the object
     * @param value The value of the given field
     */
    protected void setField(Object obj, String field_name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(field_name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a packet to the player
     * @param packet The NMS packet being sent to the client
     * @param player The player receiving the packet
     */
    protected void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        packetsStored.add(packet);
    }

    /**
     * Sends the packet to all online players
     * @param packet The NMS packet being sent to the client.
     */
    protected void sendPacket(Packet<?> packet) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.sendPacket(packet, p);
        }
    }

    protected void updatePackets(Player player)
    {
        for(Packet packet : packetsStored){
            this.sendPacket(packet, player);
        }
    }

    /**
     * Freezes the entity from behavior movement.
     * @param nmsEn The given entity
     */
    protected void freezeEntity(Entity nmsEn) {
        NBTTagCompound compound = new NBTTagCompound();
        nmsEn.c(compound);
        compound.setByte("NoAI", (byte) 1);
        nmsEn.f(compound);
    }
}
