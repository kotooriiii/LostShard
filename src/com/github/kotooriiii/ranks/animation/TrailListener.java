package com.github.kotooriiii.ranks.animation;

import com.github.kotooriiii.LostShardPlugin;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TrailListener {

    public static void onTick() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isOnline()) {
                        final AnimationManager animatorPackage = LostShardPlugin.getAnimatorPackage();
                        final AnimationManager.Trail trail = animatorPackage.getTrail(player.getUniqueId());
                        if (animatorPackage.isAnimating(player.getUniqueId()) && trail != AnimationManager.Trail.NONE) {

                            for (int i = 0; i < 15; i++)
                                player.getWorld().spawnParticle(trail.getParticle(), player.getLocation(), 0, 0.4f, 0.4f, 0.4f, 0.05);


                        }

                    }

                }
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, 3);
    }
//
//    @EventHandler
//    public void onMoonJumpingAnimating(PlayerMoveEvent event) {
//        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
//            return;
//
//        final int x_initial, y_initial, z_initial,
//                x_final, y_final, z_final;
//
//        x_initial = event.getFrom().getBlockX();
//        y_initial = event.getFrom().getBlockY();
//        z_initial = event.getFrom().getBlockZ();
//
//        x_final = event.getTo().getBlockX();
//        y_final = event.getTo().getBlockY();
//        z_final = event.getTo().getBlockZ();
//
//        if (x_initial == x_final && y_initial == y_final && z_initial == z_final)
//            return;
//
//
//    }
}
