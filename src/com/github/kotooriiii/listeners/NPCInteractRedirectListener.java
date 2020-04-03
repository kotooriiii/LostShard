package com.github.kotooriiii.listeners;

import com.github.kotooriiii.events.PlayerLeftClickShardNPCEvent;
import com.github.kotooriiii.events.PlayerRightClickShardNPCEvent;
import com.github.kotooriiii.npc.ShardBanker;
import com.github.kotooriiii.npc.ShardGuard;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Random;

import static com.github.kotooriiii.data.Maps.GUARD_COLOR;

public class NPCInteractRedirectListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void leftClickGuard(EntityDamageByEntityEvent entityEvent) {
        org.bukkit.entity.Entity entityDamaged = entityEvent.getEntity();
        org.bukkit.entity.Entity damager = entityEvent.getDamager();
        if (entityDamaged instanceof ArmorStand) {
            if (damager instanceof Player) {
                final Location location = entityDamaged.getLocation();
                Player player = (Player) damager;
                for (ShardGuard shardGuard : ShardGuard.getActiveShardGuards()) {
                    if (shardGuard.isId(entityDamaged.getEntityId())) {
                        PlayerLeftClickShardNPCEvent playerLeftClickShardNPCEvent = new PlayerLeftClickShardNPCEvent(player, shardGuard);
                        Bukkit.getPluginManager().callEvent(playerLeftClickShardNPCEvent);
                        if (playerLeftClickShardNPCEvent.isCancelled())
                            return;

                        //Add what happens when left click with playerleftclick
                        // player.sendMessage("Left click @ " + shardGuard.getName());
                        entityEvent.setCancelled(true);
                        return;
                    }
                }

                if (ShardGuard.getNearestGuard(location) != null && ShardGuard.getNearestGuard(location).getCurrentLocation().distance(location) < 1)
                    entityDamaged.remove();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void rightClickGuard(PlayerInteractAtEntityEvent entityEvent) {
        org.bukkit.entity.Entity en = entityEvent.getRightClicked();
        if (en instanceof ArmorStand) {
            final Location location = en.getLocation();
            ShardGuard[] guards = ShardGuard.getActiveShardGuards().toArray(new ShardGuard[ShardGuard.getActiveShardGuards().size()]);

            for (int i = 0; i < guards.length; i++) {
                if (guards[i].isId(en.getEntityId())) {
                    PlayerRightClickShardNPCEvent playerRightClickShardNPCEvent = new PlayerRightClickShardNPCEvent(entityEvent.getPlayer(), guards[i]);
                    Bukkit.getPluginManager().callEvent(playerRightClickShardNPCEvent);
                    if (playerRightClickShardNPCEvent.isCancelled())
                        return;

                    StatusPlayer statusPlayer = StatusPlayer.wrap(entityEvent.getPlayer().getUniqueId());

                    if(!statusPlayer.getStatus().equals(Status.WORTHY))
                        return;
                    //Add what happens when rightclick with playerInteractShardNPC
                    String[] positiveMessages = new String[]{
                            "What do you need?",
                            "Have any questions?",
                            "Can I help you?",
                            "Let me know if there's anything I can do for you.",
                            "Are you interesting in becoming a guard?",
                            "Welcome to the Order."};

                    String[] negativeMessages = new String[]
                            {
                                    "I'm workin' here!",
                                    "Leave me alone.",
                                    "I'm not interested in whatever you have to offer.",
                                    "I have no opportunities for you.",
                                    "You are wasting my time.",
                                    "I don't have any time to talk, sorry."
                            };

                    String[] messages;
                    if (i % 2 == 0) {
                        messages = positiveMessages;
                    } else {
                        messages = negativeMessages;
                    }
                    String message = messages[new Random().nextInt(messages.length)];
                    entityEvent.getPlayer().sendMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "MSG" + ChatColor.WHITE + "] " + GUARD_COLOR + guards[i].getName() + ChatColor.WHITE + ": " + message);
                    entityEvent.setCancelled(true);
                    return;
                }
            }
            if (ShardGuard.getNearestGuard(location) != null  && ShardGuard.getNearestGuard(location).getCurrentLocation().distance(location) < 1)
                en.remove();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void leftClickBanker(EntityDamageByEntityEvent entityEvent) {
        org.bukkit.entity.Entity entityDamaged = entityEvent.getEntity();
        org.bukkit.entity.Entity damager = entityEvent.getDamager();
        if (entityDamaged instanceof ArmorStand) {
            final Location location = entityDamaged.getLocation();
            if (damager instanceof Player) {
                Player player = (Player) damager;
                for (ShardBanker shardBanker : ShardBanker.getActiveShardBankers()) {
                    if (shardBanker.isId(entityDamaged.getEntityId())) {
                        PlayerLeftClickShardNPCEvent playerLeftClickShardNPCEvent = new PlayerLeftClickShardNPCEvent(player, shardBanker);
                        Bukkit.getPluginManager().callEvent(playerLeftClickShardNPCEvent);
                        if (playerLeftClickShardNPCEvent.isCancelled())
                            return;

                        //Add what happens when left click with playerleftclick
                        player.performCommand("bank help");
                        entityEvent.setCancelled(true);
                        return;
                    }
                }
                if (ShardBanker.getNearestBanker(location) != null && ShardBanker.getNearestBanker(location).getCurrentLocation().distance(location) < 1)
                    entityDamaged.remove();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void rightClickBanker(PlayerInteractAtEntityEvent entityEvent) {
        org.bukkit.entity.Entity en = entityEvent.getRightClicked();
        if (en instanceof ArmorStand) {
            final Location location = en.getLocation();

            for (ShardBanker shardBanker : ShardBanker.getActiveShardBankers()) {

                if (shardBanker.isId(en.getEntityId())) {
                    PlayerRightClickShardNPCEvent playerRightClickShardNPCEvent = new PlayerRightClickShardNPCEvent(entityEvent.getPlayer(), shardBanker);
                    Bukkit.getPluginManager().callEvent(playerRightClickShardNPCEvent);
                    if (playerRightClickShardNPCEvent.isCancelled())
                        return;

                    //Add what happens when rightclick with playerInteractShardNPC
                    entityEvent.getPlayer().performCommand("bank help");
                    entityEvent.setCancelled(true);
                    return;
                }
            }

            if (ShardBanker.getNearestBanker(location) != null && ShardBanker.getNearestBanker(location).getCurrentLocation().distance(location) < 1)
                en.remove();

        }
    }
}
