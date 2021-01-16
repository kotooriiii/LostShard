package com.github.kotooriiii.sorcery.spells.type.circle4;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.skill_listeners.TamingListener;
import com.github.kotooriiii.sorcery.spells.KVectorUtils;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.PathEntity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftMob;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class ScreechSpell extends Spell {

    private static HashMap<UUID, Double> screechSpellCooldownMap = new HashMap<UUID, Double>();
    private final static int SCREECH_DISTANCE = 6;
    private final static float SCREECH_DURATION = 10.0f;


    public ScreechSpell() {
        super(SpellType.SCREECH,
                "Scares mobs away through pulses.",
                4,
                ChatColor.DARK_AQUA,
                new ItemStack[]{new ItemStack(Material.STRING, 1), new ItemStack(Material.REDSTONE, 1)},
                2d,
                20,
                true, true, false);
    }

    public void drawInPlane(Location location, double distance) {

        // We will use these for drawing our parametric curve on the plane:
        double twopi = 2 * Math.PI;
        double times = 1 * twopi;
        double division = twopi / 100;
        //This is how far away we want the plane's origin to be:
        double radius = 1d;

        //Get the normal vector to the plane, nv:
        Location c =location;
        Vector nv = new Vector(0.0000000001f, 1, 0.0000000001f); // c.getDirection().normalize();

        // Coordinates where we want the origin to appear
        double nx = radius * nv.getX() + c.getX();
        double ny = radius * nv.getY() + c.getY();
        double nz = radius * nv.getZ() + c.getZ();

        // Get your basis vectors for the plane
        Vector ya = KVectorUtils.perp(nv, new Vector(0, 1, 0)).normalize();
        Vector xa = ya.getCrossProduct(nv).normalize();

        //nv.multiply(-1);

        // For loop for your parametric equation
        for (double theta = 0; theta < times; theta += division) {

            // Coordinates with respect to our basis
            double xb = distance * Math.cos(theta); //calculate x coordinate
            double yb = distance * Math.sin(theta); //calculate y coordinate

            // Multiply the transformation matrix with our coordinates for the change of basis
            double xi = xa.getX() * xb + ya.getX() * yb + nv.getX();
            double yi = xa.getY() * xb + ya.getY() * yb + nv.getY() ;
            double zi = xa.getZ() * xb + ya.getZ() * yb + nv.getZ();

            // Translate the coordinates in front of the player
            double x = xi + nx;
            double y = yi + ny;
            double z = zi + nz;


            c.getWorld().spawnParticle(Particle.NOTE, new Location(c.getWorld(), x, y, z), 1, 0, 0, 0, 0);
        }

    }

    @Override
    public boolean executeSpell(Player player) {

        Vector dir = player.getLocation().getDirection().clone().normalize();
        float magnitude = 6.0f;

        final int REFRESH = 20;

        final int[] timerA = {0};
        final int[] timerB = {0};

        final int[] screechParticles = {1};


        int frequency = 3;


        new BukkitRunnable() {
            @Override
            public void run() {
                if (timerA[0] * REFRESH/frequency >= SCREECH_DURATION*20) {
                    this.cancel();
                    return;
                }

                if(player.isOnline() && !player.isDead()) {

                    if(screechParticles[0] > SCREECH_DISTANCE)
                        screechParticles[0] = 1;

                    screechParticles[0] ++;

                    drawInPlane(player.getEyeLocation().clone().add(0,-3,0), screechParticles[0]);
                }

                timerA[0]++;
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, REFRESH/frequency);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (timerB[0] * REFRESH >= SCREECH_DURATION*20) {
                    this.cancel();
                    return;
                }

                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), SCREECH_DISTANCE, SCREECH_DISTANCE, SCREECH_DISTANCE)) {
                    if (!TamingListener.isHostile(entity))
                        continue;
                    if (!(entity instanceof Mob))
                        continue;

                    Location location = entity.getLocation().toVector().add(dir.clone().multiply(magnitude)).toLocation(entity.getWorld());

                    CraftMob craftMob = (CraftMob) entity;

                  // PathEntity pathEntity = craftMob.getHandle().getNavigation().a(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), 20);
                   craftMob.getHandle().getNavigation().a(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 1.5d);
                }

                timerB[0]++;
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, REFRESH);

        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        screechSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    screechSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                screechSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (screechSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = screechSpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(1, RoundingMode.HALF_UP);
            float value = bd.floatValue();
            if (value == 0)
                value = 0.1f;

            String time = "seconds";
            if (value <= 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }
}
