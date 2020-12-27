package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.status.StatusUpdateListener;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class FireballExplodeListener implements Listener {


    private void explode(Fireball fireball) {
        Location location = fireball.getLocation();
        final int RADIUS = 5;
        final int DAMAGE = 10;

        Player shooter = (Player) fireball.getShooter();


        damage(location, shooter, DAMAGE, RADIUS);

        if (LostShardPlugin.getPlotManager().isStandingOnPlot(location)) {
            Plot standingPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(location);

            if (standingPlot == null)
                return;

            if (standingPlot.getType().isStaff())
                return;

            PlayerPlot playerPlot = (PlayerPlot) standingPlot;
            if (!playerPlot.isJointOwner(shooter.getUniqueId()) && !playerPlot.isOwner(shooter.getUniqueId()))
                return;

        }

        destroy(shooter, location, 2);
        ;
    }

    private void destroy(Player shooter, Location location, int RADIUS) {

        final int firstX = location.getBlockX();
        final int firstY = location.getBlockY();
        final int firstZ = location.getBlockZ();

        final double chance = 0.75;

        xloop:
        for (int x = firstX - RADIUS; x < firstX + RADIUS; x++) {
            zloop:
            for (int z = firstZ - RADIUS; z < firstZ + (RADIUS); z++) {

                yloop:
                for (int y = firstY + RADIUS; y > firstY - RADIUS; y--) {
                    Block block = new Location(location.getWorld(), x, y, z).getBlock();
                    Plot standingPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(block.getLocation());


                    if (standingPlot != null) {

                        if (standingPlot.getType().isStaff())
                            continue zloop;

                        PlayerPlot playerPlot = (PlayerPlot) standingPlot;
                        if (!playerPlot.isJointOwner(shooter.getUniqueId()) && !playerPlot.isOwner(shooter.getUniqueId()))
                            continue zloop;
                    }


                    if (block.getType() == Material.AIR)
                        continue yloop;

                    double random = Math.random();
                    if (random < chance) {
                        if (block.getType() != Material.BEDROCK)
                            block.setType(Material.FIRE);
                    }
                }
            }
        }
    }

    private void damage(Location location, Player shooter, int damage, int RADIUS) {

        Clan clan =LostShardPlugin.getClanManager().getClan(shooter.getUniqueId());

        for (Entity entity : location.getWorld().getNearbyEntities(location, RADIUS, RADIUS, RADIUS)) {
            if (!(entity instanceof Damageable))
                continue;
            if(entity.equals(shooter)) {
                ((Damageable) entity).damage(damage);
                continue;
            }
            if(clan != null && entity instanceof Player && clan.isInThisClan(entity.getUniqueId()) && !clan.isFriendlyFire())
                continue;

            final float DAMAGE = damage;
            //      ((Player) entity).damage(0.1f);
            EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(shooter, entity, EntityDamageEvent.DamageCause.CUSTOM, DAMAGE);
            entity.setLastDamageCause(damageByEntityEvent);
            Bukkit.getPluginManager().callEvent(damageByEntityEvent);
            ((Player) entity).setHealth(((Player) entity).getHealth() - DAMAGE);


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
