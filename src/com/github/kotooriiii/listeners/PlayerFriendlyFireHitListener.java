package com.github.kotooriiii.listeners;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.events.PlayerLeftClickShardNPCEvent;
import com.github.kotooriiii.events.PlayerRightClickShardNPCEvent;
import com.github.kotooriiii.guards.ShardBanker;
import com.github.kotooriiii.guards.ShardGuard;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerFriendlyFireHitListener implements Listener {

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

//    @EventHandler
//    public void onCrouch(PlayerToggleSneakEvent event) {
//        if (event.isSneaking()) {
//            Player player = event.getPlayer();
//
//            ShardGuard shardGuard = new ShardGuard("GuardName");
//            shardGuard.spawn(player.getLocation());
//            Bukkit.broadcastMessage("Spawn");
//
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    Bukkit.broadcastMessage("Destroy");
//                    shardGuard.destroy();
//                }
//            }.runTaskLater(LostShardK.plugin, 700);
//
//
//        }
//    }


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
