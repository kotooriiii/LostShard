package com.github.kotooriiii.npc.reworked;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.ShardNMS;
import com.github.kotooriiii.npc.Skin;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import java.util.UUID;

public class SNPC extends ShardNMS {


    public void init() {

        PacketListenerAPI.addPacketHandler(new PacketHandler() {

            @Override
            public void onSend(SentPacket packet) {
//
////                if(packet.getPacketName().contains("Rel") || packet.getPacketName().contains("Pos") || packet.getPacketName().contains("Chat") || packet.getPacketName().contains("Rot") || packet.getPacketName().contains("Vel") || packet.getPacketName().contains("Team"))
////                    return;
//
//                if (!packet.getPacketName().equalsIgnoreCase("PacketPlayOutSpawnEntityLiving"))
//                    return;
//
//                Bukkit.broadcastMessage("sent packet: " + packet.getPacketName());
//
//
//                final Location loc;
//                final int entityID;
//                final UUID UUID;
//                final DataWatcher dataWatcher;
//
//                PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntity = (PacketPlayOutSpawnEntityLiving) packet.getPacket();
//
//
//                loc = new Location(packet.getPlayer().getWorld(), (double) getField(packetPlayOutSpawnEntity, "d"), (double) getField(packetPlayOutSpawnEntity, "e"), (double) getField(packetPlayOutSpawnEntity, "f"), Float.valueOf((byte) getField(packetPlayOutSpawnEntity, "k")), Float.valueOf((byte) getField(packetPlayOutSpawnEntity, "j")));
//                entityID = (int) getField(packetPlayOutSpawnEntity, "a");
//                UUID = (UUID) getField(packetPlayOutSpawnEntity, "b");
//                EntityTypes<?> a = IRegistry.ENTITY_TYPE.fromId((int) getField(packetPlayOutSpawnEntity, "c"));
//                Bukkit.broadcastMessage("type: " + a);
//
//                if (!EntityTypes.ZOMBIE.equals(a))
//                    return;
//
//                Bukkit.broadcastMessage("Canceled spawn of entity: zombie.");
//
//                PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityID);
//                SNPC.this.sendPacket(destroyPacket);
//
//
//
//
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
//                        WorldServer minecraftWorld = ((CraftWorld) loc.getWorld()).getHandle();
//                        GameProfile gameProfile = new GameProfile(UUID, ChatColor.YELLOW + "Testing");
//                        gameProfile.getProperties().put("textures", new Property("textures", Skin.GUARD.getTexture(), Skin.GUARD.getSignature()));
//
//                        EntityPlayer npc = new EntityPlayer(minecraftServer, minecraftWorld, gameProfile, new PlayerInteractManager(minecraftWorld));
//                        npc.setLocation(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5, 90, 90);
//
//                        PacketPlayOutPlayerInfo playerDeclarePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc);
//                        PacketPlayOutNamedEntitySpawn playerSpawnPacket = new PacketPlayOutNamedEntitySpawn(npc);
//
//                        setField(playerSpawnPacket, "a", entityID);
//                        setField(playerSpawnPacket, "b", UUID);
//                        setField(playerSpawnPacket, "c", loc.getX());
//                        setField(playerSpawnPacket, "d", loc.getY());
//                        setField(playerSpawnPacket, "e", loc.getZ());
//                        setField(playerSpawnPacket, "f", Float.valueOf(loc.getYaw()).byteValue());
//                        setField(playerSpawnPacket, "g", Float.valueOf(loc.getPitch()).byteValue());
//                        npc.setLocation(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5, 90, 90);
//
//                        Bukkit.broadcastMessage("Spawned NPC: " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
//                        SNPC.this.sendPacket(playerDeclarePacket);
//                        SNPC.this.sendPacket(playerSpawnPacket);
//
//
//                        PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entityID, entity.getDataWatcher(), true);
//
//
//
//                    }
//
//                }.runTaskLater(LostShardPlugin.plugin, 100);


            }

            @Override
            public void onReceive(ReceivedPacket packet) {

                //  Bukkit.broadcastMessage("Received packet: " + packet.getPacketName());


            }
        });
    }

    public void spawnNPC(Location location) {

    }

}
