package com.github.kotooriiii.npc.reworked;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.ShardNMS;
import com.github.kotooriiii.npc.Skin;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class PacketListener extends ShardNMS {


    public void init() {

        PacketListenerAPI.addPacketHandler(new PacketHandler() {

            @Override
            public void onSend(SentPacket packet) {

////                if (packet.getPacketName().contains("Rel") || packet.getPacketName().contains("Pos") || packet.getPacketName().contains("Chat") || packet.getPacketName().contains("Rot") || packet.getPacketName().contains("Vel") || packet.getPacketName().contains("Team") || packet.getPacketName().contains("Entity"))
////                    return;
//
//                if (!packet.getPacketName().equalsIgnoreCase("PacketPlayOutBlockChange"))
//                    return;
//
//                PacketPlayOutBlockChange specificPacket = (PacketPlayOutBlockChange) packet.getPacket();
//                BlockPosition position = (BlockPosition) getField(specificPacket, "a");
//                int x = (int) getField(position, "a");
//                int y = (int) getField(position, "b");
//                int z = (int) getField(position, "c");
//
//                Location location = new Location(packet.getPlayer().getWorld(), x, y, z);
//                onPlotBreakBlock(packet, packet.getPlayer(), location);
//                Bukkit.broadcastMessage("sent packet: " + packet.getPacketName());


            }

            @Override
            public void onReceive(ReceivedPacket packet) {

                //  Bukkit.broadcastMessage("Received packet: " + packet.getPacketName());


            }
        });
    }

    public void onPlotBreakBlock(SentPacket packet, Player playerBlockBreak, Location location) {

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

                    packet.setCancelled(true);
                    return;
                }

                PlayerPlot playerPlot = (PlayerPlot) plot;

                //If don't have permissions
                if (!(playerPlot.isJointOwner(playerUUID) || playerPlot.isOwner(playerUUID))) {
                    packet.setCancelled(true);
                    return;
                }

                //ALLOWED

                break;
            }
        }

    }

}
