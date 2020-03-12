package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.guards.Guard;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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
        org.bukkit.entity.Entity en = entityEvent.getEntity();
        if (en instanceof ArmorStand) {
            for (Guard guard : Guard.activeGuards) {
                if (guard.isId(en.getEntityId())) {
                    entityEvent.setCancelled(true);
                }
            }


        }
    }

    @EventHandler
    public void OIC(PlayerInteractAtEntityEvent entityEvent) {
        org.bukkit.entity.Entity en = entityEvent.getRightClicked();
        if (en instanceof ArmorStand) {
            for (Guard guard : Guard.activeGuards) {
                if (guard.isId(en.getEntityId())) {
                    entityEvent.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCrouch(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            Player player = event.getPlayer();
            Bukkit.broadcastMessage("Start");

            Guard guard = new Guard("Manny");
            guard.spawn(player.getLocation());
            Bukkit.broadcastMessage("Spawn");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage("Teleport");

                    guard.teleport(event.getPlayer().getLocation(), true);
                }
            }.runTaskLater(LostShardK.plugin, 20);

            new BukkitRunnable() {
                @Override
                public void run() {
//                   Bukkit.broadcastMessage("Bounds");
//                   guard.showBounds();
                }
            }.runTaskLater(LostShardK.plugin, 40);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage("Search");
                    guard.checkForEnemy();
                }
            }.runTaskLater(LostShardK.plugin, 80);
            new BukkitRunnable() {
                @Override
                public void run() {


                }
            }.runTaskLater(LostShardK.plugin, 160);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage("Destroy");

                    guard.destroy();
                }
            }.runTaskLater(LostShardK.plugin, 700);

            Bukkit.broadcastMessage("End");

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
