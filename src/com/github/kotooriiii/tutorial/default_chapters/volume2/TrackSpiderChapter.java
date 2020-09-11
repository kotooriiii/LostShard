package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.npc.ShardNMS;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import net.citizensnpcs.api.CitizensAPI;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import java.util.UUID;

public class TrackSpiderChapter extends AbstractChapter {

    private PacketHandler packetHandler;
    private UUID entityUUID;
    private Location location;
    private boolean isComplete;


    public TrackSpiderChapter() {
        packetHandler = null;
        entityUUID = null;
        isComplete=false;
        location = null; //todo set this
    }

    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getSurvivalism().setLevel(100.0f);

        Entity entity = player.getWorld().spawnEntity(location, EntityType.SPIDER);
        entityUUID = entity.getUniqueId();

        initListener();

        sendMessage(player, "Track the spider to get to the next marker.");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    this.cancel();
                    return;
                }
                sendMessage(player, "Give it a go! Try typing: \"/track Spider\"");
                this.cancel();

            }
        }.runTaskLater(LostShardPlugin.plugin, TIP_DELAY);

    }

    @Override
    public void onDestroy() {
        PacketListenerAPI.removePacketHandler(packetHandler);

        if (entityUUID != null) {
            Entity entity = Bukkit.getEntity(entityUUID);
            if (entity != null && entity instanceof LivingEntity)
                ((LivingEntity) entity).setHealth(0);
        }


        if (packetHandler != null) {
            PacketListenerAPI.removePacketHandler(packetHandler);
        }


    }

    public void initListener() {
        this.packetHandler = new PacketHandler(LostShardPlugin.plugin) {

            @Override
            public void onSend(SentPacket packet) {


                //todo pseudo code

                //If the packet is not the one we are searching for, return.
                if (!packet.getPacketName().equals("PacketPlayOutSpawnEntityLiving"))
                    return;

                //If the player who is progressing in this chapter is the one forcing the spawn, let them see it, so return.
                if (getUUID().equals(packet.getPlayer().getUniqueId()))
                    return;

                //Catch the packet
                PacketPlayOutSpawnEntityLiving castedPacket = (PacketPlayOutSpawnEntityLiving) packet.getPacket();
                UUID uuid = (UUID) ShardNMS.getField(castedPacket, "b");

                //If the ID is some other one in the world, return
                if (!uuid.equals(entityUUID))
                    return;

                /*
                The packet we are lookign for
                Not the player spawning it
               The entity is the one being spawned
                 */
                packet.setCancelled(true);
                return;
            }

            @Override
            public void onReceive(ReceivedPacket packet) {
                //  Bukkit.broadcastMessage("Received packet: " + packet.getPacketName());
            }
        };
        PacketListenerAPI.addPacketHandler(packetHandler);
    }

    /*
    cancel the spider event targeting someone
     */
    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getEntity().getUniqueId().equals(entityUUID))
            return;
        event.setCancelled(true);
    }
    @EventHandler
    public void onDmg(EntityDamageByEntityEvent event)
    {
        Entity damager =event.getDamager();

        if(CitizensAPI.getNPCRegistry().isNPC(event.getEntity()) || CitizensAPI.getNPCRegistry().isNPC(damager))
            return;
        if(!event.getEntity().getUniqueId().equals(entityUUID))
            return;
        if(damager.getUniqueId().equals(getUUID()))
            return;
        event.setCancelled(true);
    }

    /*
   spider dies
     */
    @EventHandler
    public void onProximity(PlayerMoveEvent event) {
        if(isComplete)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (location == null)
            return;

        final int distance = 5;

        Location to = event.getTo();

        Zone zone = new Zone(location.getBlockX() - distance, location.getBlockX() + distance, location.getBlockY() - distance, location.getBlockY() + distance, location.getBlockZ() - distance, location.getBlockZ() + distance);
        if (!zone.contains(to))
            return;

        isComplete=true;
        sendMessage(event.getPlayer(), "Great Job!");
        setComplete();
    }

}