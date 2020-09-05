package com.github.kotooriiii.npc;

import com.github.kotooriiii.LostShardPlugin;
import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ShardNMS {


    private ShardNMS() {
    }

    /**
     * Gets the value of the given object's field.
     *
     * @param obj        The object being referenced
     * @param field_name The private field of the object
     * @return The value of the given field
     */
    public static Object getField(Object obj, String field_name) {
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
     *
     * @param obj        The object being referenced
     * @param field_name The private field of the object
     * @param value      The value of the given field
     */
    public static void setField(Object obj, String field_name, Object value) {
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
     *
     * @param packet The NMS packet being sent to the client
     * @param player The player receiving the packet
     */
    public static void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }


    /**
     * Sends the packet to all online players
     *
     * @param packet The NMS packet being sent to the client.
     */
    public static void sendPacket(Packet<?> packet) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendPacket(packet, p);
        }
    }


}
