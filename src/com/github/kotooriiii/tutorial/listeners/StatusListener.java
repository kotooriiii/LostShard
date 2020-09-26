package com.github.kotooriiii.tutorial.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.type.tutorial.TutorialTrait;
import com.github.kotooriiii.status.Status;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class StatusListener implements Listener {

    private NPC a1, a2, b1, b2;
    private Location spawnA, spawnA2, spawnB, spawnB2;

    public StatusListener() {
        this.a2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Status.WORTHY.getChatColor() + "[NPC] Elise");
        a2.addTrait(new TutorialTrait());
        a2.setProtected(false);
        spawnA2 = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 663.5, 66, 875.5, -90, 0);
        a2.spawn(spawnA2);

        this.a1 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Status.WORTHY.getChatColor() + "[NPC] Jackie");
        a1.addTrait(new TutorialTrait());
        spawnA = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 665.5, 66, 875.5, 90, 0);
        a1.spawn(spawnA);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (a2 == null || a1 == null)
                    return;
                if (!a1.isSpawned() || !a2.isSpawned())
                    return;
                this.cancel();
                a1.setProtected(true);
                a1.getNavigator().setTarget(a2.getEntity(), true);
                a1.getNavigator().getLocalParameters().attackDelayTicks(20);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 1);


        this.b2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Status.WORTHY.getChatColor() + "[NPC] Arizona");
        b2.addTrait(new TutorialTrait());
        b2.setProtected(false);
        spawnB2 = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 665.5, 66, 733.5, 90, 0);
        b2.spawn(spawnB2);

        this.b1 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Status.CORRUPT.getChatColor() + "[NPC] Clover");
        b1.addTrait(new TutorialTrait());
        spawnB = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 663.5, 66, 733.5, -90, 0);
        b1.spawn(spawnB);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (b1 == null || b2 == null)
                    return;
                if (!b1.isSpawned() || !b2.isSpawned())
                    return;
                this.cancel();
                b1.setProtected(true);
                b1.getNavigator().setTarget(b2.getEntity(), true);
                b1.getNavigator().getLocalParameters().attackDelayTicks(20);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 1);


    }

    @EventHandler
    public void onDmg(NPCDamageByEntityEvent event) {

        Entity entity = event.getNPC().getEntity();

        if (event.getDamager() instanceof Player && !CitizensAPI.getNPCRegistry().isNPC(event.getDamager()) && (entity == a2.getEntity() || entity == b2.getEntity())) {
            event.setCancelled(true);
            return;
        }

        if (a2 == event.getNPC() && event.getDamager() == a1.getEntity()) {

            if (entity instanceof LivingEntity)
                ((LivingEntity) entity).setHealth(((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            event.setDamage(1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    a1.setName(Status.CORRUPT.getChatColor() + "[NPC] Jackie");
                    a1.teleport(spawnA, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    a1.getNavigator().cancelNavigation();
                    a2.teleport(spawnA2, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }.runTask(LostShardPlugin.plugin);

            new BukkitRunnable() {
                @Override
                public void run() {
                    a1.setName(Status.WORTHY.getChatColor() + "[NPC] Jackie");
                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            if(!a1.isSpawned())
                                return;

                            if (!a1.getNavigator().isNavigating()) {
                                a1.getNavigator().setTarget(a2.getEntity(), true);
                            } else
                            {
                                this.cancel();
                            }

                        }
                    }.runTaskTimer(LostShardPlugin.plugin,20*3, 1);
                }
            }.runTaskLater(LostShardPlugin.plugin, 20 * 2);

        } else if (b2 == event.getNPC()) {
            event.setDamage((int) ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2);
        }
    }

    @EventHandler
    public void onDeath(final NPCDeathEvent event) {
        if (event.getNPC() != this.b2)
            return;

        b1.getNavigator().cancelNavigation();
        b1.teleport(spawnB, PlayerTeleportEvent.TeleportCause.PLUGIN);

        new BukkitRunnable() {
            @Override
            public void run() {
                b1.setName(Status.EXILED.getChatColor() + "[NPC] Clover");
            }
        }.runTask(LostShardPlugin.plugin);

        new BukkitRunnable() {
            @Override
            public void run() {

                event.getNPC().spawn(spawnB2);
                b1.setName(Status.CORRUPT.getChatColor() + "[NPC] Clover");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (b2 == null || b1 == null)
                            return;
                        if (!b2.isSpawned() || !b1.isSpawned())
                            return;
                        this.cancel();
                        b1.getNavigator().setTarget(b2.getEntity(), true);
                    }
                }.runTaskTimer(LostShardPlugin.plugin, 0, 1);

            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 4);

    }

}
