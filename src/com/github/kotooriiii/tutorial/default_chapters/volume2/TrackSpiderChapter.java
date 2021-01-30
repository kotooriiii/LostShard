package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.npc.ShardNMS;
import com.github.kotooriiii.skills.events.EntityTrackEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import net.citizensnpcs.api.CitizensAPI;

import net.minecraft.server.v1_16_R3.EntitySpider;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import java.util.UUID;

public class TrackSpiderChapter extends AbstractChapter {

    private PacketHandler packetHandler;
    private UUID entityUUID;
    private Location spiderLocation;
    private boolean isComplete, isCrossed, isTracked;


    public TrackSpiderChapter() {
        packetHandler = null;
        entityUUID = null;
        isComplete = isCrossed = isTracked = false;
        spiderLocation = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 297, 54, 684);
    }

    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getSurvivalism().setLevel(100.0f);

        initListener();


        sendMessage(player, "Track the spider to get to the next marker.", ChapterMessageType.HOLOGRAM_TO_TEXT);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
        player.updateInventory();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    this.cancel();
                    return;
                }
                sendMessage(player, "Give it a go! Try typing: \"/track Spider\"", ChapterMessageType.HOLOGRAM_TO_TEXT);
                this.cancel();

            }
        }.runTaskLater(LostShardPlugin.plugin, TIP_DELAY);

    }

    @EventHandler
    public void onApproachA(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (isTracked)
            return;
        if (event.getTo().getZ() < 557)
            return;
        event.setCancelled(true);
        sendMessage(event.getPlayer(), "You must type \"/track Spider\" before continuing.", ChapterMessageType.HELPER);
    }

    @EventHandler
    public void onApproachA(PlayerTeleportEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (isTracked)
            return;
        if (event.getTo().getZ() < 557)
            return;
        event.setCancelled(true);
        sendMessage(event.getPlayer(), "You must type \"/track Spider\" before continuing.", ChapterMessageType.HELPER);
    }

    @EventHandler
    public void onTrack(EntityTrackEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (isTracked)
            return;
        if (event.getType() != EntityType.SPIDER)
            return;
        isTracked = true;

       event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 3, false, false, false));
        new BukkitRunnable() {
            @Override
            public void run() {
                sendMessage(event.getPlayer(), "To find the spider, press F3 and use the directions (North, East, South, West) to guide you.", ChapterMessageType.HELPER);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20*5);
    }


    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (isCrossed)
            return;
        if (event.getTo().getX() > 335)
            return;
        isCrossed = true;

        CraftWorld craftWorld = ((CraftWorld) spiderLocation.getWorld());
        final EntitySpider entitySpider = new EntitySpider(EntityTypes.SPIDER, craftWorld.getHandle());
        entitySpider.setPosition(spiderLocation.getX(), spiderLocation.getY(), spiderLocation.getZ());
        final Spider spider = (Spider) craftWorld.addEntity(entitySpider, CreatureSpawnEvent.SpawnReason.CUSTOM);

        spider.setCustomName("[Tutorial] Spider");
        spider.setCustomNameVisible(true);
        entityUUID = spider.getUniqueId();
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

                //If the packet is not the one we are searching for, return.
                if (!packet.getPacketName().equals("PacketPlayOutSpawnEntityLiving"))
                    return;
                if (entityUUID == null)
                    return;

                if (packet == null || packet.getPlayer() == null)
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
        if (!event.getEntity().getUniqueId().equals(entityUUID))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDmg(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()) || CitizensAPI.getNPCRegistry().isNPC(damager))
            return;
        if (!event.getEntity().getUniqueId().equals(entityUUID))
            return;
        if (damager.getUniqueId().equals(getUUID()))
            return;
        event.setCancelled(true);
    }

    /*
   spider dies
     */
    @EventHandler
    public void onProximity(PlayerMoveEvent event) {
        if (isComplete)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (spiderLocation == null)
            return;

        final int distance = 5;

        Location to = event.getTo();
        if (entityUUID == null)
            return;
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(entityUUID);
        if (entity == null) {
            isComplete = true;
            sendMessage(event.getPlayer(), "Great Job!", ChapterMessageType.HOLOGRAM_TO_TEXT);

            ItemStack[] contents = event.getPlayer().getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] == null)
                    continue;
                if (contents[i].getType() == Material.STICK)
                    event.getPlayer().getInventory().setItem(i, null);
            }

            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendMessage(event.getPlayer(), "Follow the cave.", ChapterMessageType.HELPER);
                }
            }.runTaskLater(LostShardPlugin.plugin, 20*20);
            setComplete();
            return;
        }

        if (!(to.distance(entity.getLocation()) < distance))
            return;

        isComplete = true;
        sendMessage(event.getPlayer(), "Great Job!", ChapterMessageType.HOLOGRAM_TO_TEXT);

        ItemStack[] contents = event.getPlayer().getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null)
                continue;
            if (contents[i].getType() == Material.STICK)
                event.getPlayer().getInventory().setItem(i, null);
        }

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        setComplete();
    }

}