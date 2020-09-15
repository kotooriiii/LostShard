package com.github.kotooriiii.tutorial.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.status.Status;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class StatusListener implements Listener {

    private NPC a1, a2, b1, b2;

    public StatusListener() {
        this.a2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Status.WORTHY.getChatColor() + "[NPC] Elise");
        a2.setProtected(true);
        a2.spawn(new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 663, 66, 875));

        this.a1 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Status.WORTHY.getChatColor() + "[NPC] Jackie");
        a1.spawn(new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 665, 66, 875));

        a1.setProtected(true);
        a1.getNavigator().setTarget(a2.getEntity(), true);
        a1.getNavigator().getLocalParameters().attackDelayTicks(20 * 4);
        a1.getNavigator().getLocalParameters().baseSpeed(0.0f);

        this.b2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Status.WORTHY.getChatColor() + "[NPC] Daniel");
        b2.spawn(new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 665, 66, 733));

        this.b1 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Status.CORRUPT.getChatColor() + "[NPC] Marie");
        b1.setProtected(true);
        b1.getNavigator().setTarget(b2.getEntity(), true);
        b1.getNavigator().getLocalParameters().baseSpeed(0.0f);
        b1.spawn(new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 663, 66, 733));

    }

    @EventHandler
    public void onDmg(NPCDamageByEntityEvent event) {
        if (!CitizensAPI.getNPCRegistry().isNPC(event.getDamager())) {
            event.setCancelled(true);
            return;
        }
        Entity entity = event.getNPC().getEntity();

        if (a2 == event.getNPC()) {

            if (entity instanceof LivingEntity)
                ((LivingEntity) entity).setHealth(((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            event.setDamage(1);
            event.getNPC().getEntity().setVelocity(new Vector(0, 0, 0));
            a1.setName(Status.CORRUPT.getChatColor() + "[NPC] Jackie");

            new BukkitRunnable() {
                @Override
                public void run() {
                    a1.setName(Status.WORTHY.getChatColor() + "[NPC] Jackie");

                }
            }.runTaskLater(LostShardPlugin.plugin, a1.getNavigator().getLocalParameters().attackDelayTicks() / 2);

        } else if (b2 == event.getNPC()) {
            event.setDamage((int) ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }
    }

    @EventHandler
    public void onDeath(final NPCDeathEvent event) {
        if (event.getNPC() != this.b2)
            return;

        this.b1.setName(Status.EXILED.getChatColor() + "[NPC] Marie");

        new BukkitRunnable() {
            @Override
            public void run() {
                CitizensAPI.getNPCRegistry().deregister(event.getNPC());
            }
        }.runTask(LostShardPlugin.plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                b2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Status.WORTHY.getChatColor() + "[NPC] Daniel");
                //todo b2.spawn(new Location(LostShardPlugin.getWorld(), ));
                b1.setName(Status.CORRUPT.getChatColor() + "[NPC] Marie");
                b1.getNavigator().setTarget(b2.getEntity(), true);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 4);

    }

}
