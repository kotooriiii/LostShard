package com.github.kotooriiii.listeners;

import com.github.kotooriiii.events.PlayerLeftClickShardNPCEvent;
import com.github.kotooriiii.events.PlayerRightClickShardNPCEvent;
import com.github.kotooriiii.guards.ShardGuard;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class NPCInteractRedirectListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void leftClickShardNPC(EntityDamageByEntityEvent entityEvent) {
        org.bukkit.entity.Entity entityDamaged = entityEvent.getEntity();
        org.bukkit.entity.Entity damager = entityEvent.getDamager();
        if (entityDamaged instanceof ArmorStand) {
            if (damager instanceof Player) {
                Player player = (Player) damager;
                for (ShardGuard shardGuard : ShardGuard.getActiveShardGuards()) {
                    if (shardGuard.isId(entityDamaged.getEntityId())) {
                        PlayerLeftClickShardNPCEvent playerLeftClickShardNPCEvent = new PlayerLeftClickShardNPCEvent(player, shardGuard);
                        Bukkit.getPluginManager().callEvent(playerLeftClickShardNPCEvent);
                        if (playerLeftClickShardNPCEvent.isCancelled())
                            return;

                        //Add what happens when left click with playerleftclick
                        player.sendMessage("Left click @ " + shardGuard.getName());
                        entityEvent.setCancelled(true);

                    }
                }
            }

        }
    }

    @EventHandler (ignoreCancelled = true)
    public void rightClickShardNPC(PlayerInteractAtEntityEvent entityEvent) {
        org.bukkit.entity.Entity en = entityEvent.getRightClicked();
        if (en instanceof ArmorStand) {
            for (ShardGuard shardGuard : ShardGuard.getActiveShardGuards()) {
                if (shardGuard.isId(en.getEntityId())) {
                    PlayerRightClickShardNPCEvent playerRightClickShardNPCEvent = new PlayerRightClickShardNPCEvent(entityEvent.getPlayer(), shardGuard);
                    Bukkit.getPluginManager().callEvent(playerRightClickShardNPCEvent);
                    if (playerRightClickShardNPCEvent.isCancelled())
                        return;

                    //Add what happens when rightclick with playerInteractShardNPC
                    entityEvent.getPlayer().sendMessage("Right click @ " + shardGuard.getName());
                    entityEvent.setCancelled(true);

                }
            }
        }
    }
}
