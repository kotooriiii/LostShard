package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.awt.*;

public class FireballExplodeListener implements Listener {


    private void explode(Fireball fireball) {
        Location location = fireball.getLocation();
        final int RADIUS = 2;
        final int DAMAGE = 10;

        Player shooter = (Player) fireball.getShooter();


        damage(location, DAMAGE, RADIUS);

        if (LostShardPlugin.getPlotManager().isStandingOnPlot(location)) {
            Plot standingPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(location);

            if (standingPlot.getType().isStaff())
                return;

            PlayerPlot playerPlot = (PlayerPlot) standingPlot;
            if(!playerPlot.isJointOwner(shooter.getUniqueId()) && !playerPlot.isOwner(shooter.getUniqueId()))
                    return;

        }

        destroy(location, 2);
    }

    private void destroy(Location location, int RADIUS) {

        final int firstX = location.getBlockX();
        final int firstY = location.getBlockY();
        final int firstZ = location.getBlockZ();

        final double chance = 0.5;

        xloop:
        for (int x = firstX - RADIUS; x < firstX + RADIUS; x++) {
            zloop:
            for (int z = firstZ - RADIUS; z < firstZ + (RADIUS); z++) {

                yloop:
                for (int y = firstY; firstY > firstY - 5; y--) {
                    Block block = new Location(location.getWorld(), x, y, z).getBlock();

                    if (block.getType() == Material.AIR)
                        continue yloop;

                    double random = Math.random();
                    if (random < chance)
                        block.setType(Material.FIRE);
                    break yloop;
                }
            }
        }
    }

    private void damage(Location location, int damage, int RADIUS) {

        for (Entity entity : location.getWorld().getNearbyEntities(location, RADIUS, RADIUS, RADIUS)) {
            if (!(entity instanceof Damageable))
                return;

            ((Damageable) entity).damage(damage);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity projectile = event.getEntity();

        if (projectile == null)
            return;

        if (!(projectile instanceof Fireball))
            return;


        Fireball fireball = (Fireball) projectile;

        if (!(fireball.getShooter() instanceof Player))
            return;

        //At this point, it is a player-made fireball
        event.setCancelled(true);
        explode(fireball);
    }


}
