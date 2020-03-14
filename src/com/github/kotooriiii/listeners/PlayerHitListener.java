package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.events.PlayerLeftClickShardNPCEvent;
import com.github.kotooriiii.events.PlayerRightClickShardNPCEvent;
import com.github.kotooriiii.guards.ShardGuard;
import com.github.kotooriiii.guards.Skin;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerHitListener implements Listener {

    ArrayList<Player> players = new ArrayList<>();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {

            Player attacker = (Player) event.getDamager();
            UUID attackerUUID = attacker.getUniqueId();

            Player defender = (Player) event.getEntity();
            UUID defenderUUID = defender.getUniqueId();

            Clan attackerClan = Clan.getClan(attackerUUID);
            Clan defenderClan = Clan.getClan(defenderUUID);

            if (attackerClan != null && defenderClan != null) {
                if (attackerClan.equals(defenderClan)) {
                    if (attackerClan.isFriendlyFire())
                        event.setCancelled(true);
                }
            }

        }//end
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
    }

    @EventHandler
    public void OIC2(EntityDamageByEntityEvent entityEvent) {
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
                        entityEvent.setCancelled(true);

                        //Add what happens when left click with playerleftclick
                    }
                }
            }

        }
    }

    @EventHandler
    public void OIC(PlayerInteractAtEntityEvent entityEvent) {
        org.bukkit.entity.Entity en = entityEvent.getRightClicked();
        if (en instanceof ArmorStand) {
            for (ShardGuard shardGuard : ShardGuard.getActiveShardGuards()) {
                if (shardGuard.isId(en.getEntityId())) {
                    PlayerRightClickShardNPCEvent playerRightClickShardNPCEvent = new PlayerRightClickShardNPCEvent(entityEvent.getPlayer(), shardGuard);
                    Bukkit.getPluginManager().callEvent(playerRightClickShardNPCEvent);
                    if (playerRightClickShardNPCEvent.isCancelled())
                        return;
                    entityEvent.setCancelled(true);

                    //Add what happens when rightclick with playerInteractShardNPC

                }
            }
        }
    }

    @EventHandler
    public void onCrouch(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            Player player = event.getPlayer();

            ShardGuard shardGuard = new ShardGuard("GuardName");
            shardGuard.spawn(player.getLocation());
            Bukkit.broadcastMessage("Spawn");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage("Destroy");
                    shardGuard.destroy();
                }
            }.runTaskLater(LostShardK.plugin, 700);


        }
    }


//    @EventHandler
//    public void onSomethingRandomJustDeleteItLater(final PlayerInteractEvent e) {
//        if (e.getItem() != null && e.getItem().getType().equals(Material.JUKEBOX) && e.getAction().equals(Action.LEFT_CLICK_AIR)) {
//            if (players.contains(e.getPlayer())) {
//                players.remove(e.getPlayer());
//            } else {
//                players.add(e.getPlayer());
//
//                new BukkitRunnable()
//                {
//                    int i = 0;
//                    @Override
//                    public void run() {
//                        if(!players.contains(e.getPlayer()))
//                        {
//                            cancel();
//                        }
//
//                        HelperMethods.playSound(new Player[]{e.getPlayer()}, Sound.values()[i]);
//                        e.getPlayer().sendMessage("Playing - " + Sound.values()[i]);
//                        i++;
//                    }
//                }.runTaskTimer(LostShardK.plugin, 0, 20);
//
//            }
//        }
//    }

}
