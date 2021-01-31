package com.github.kotooriiii.sorcery.spells.type.circle7;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.skill_listeners.TamingListener;
import com.github.kotooriiii.sorcery.spells.KVectorUtils;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellToggleable;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class RadiateSpell extends Spell implements SpellToggleable {

    private static HashMap<UUID, Double> radiateSpellCooldownMap = new HashMap<UUID, Double>();

    private final static HashMap<UUID, Location> lightMap = new HashMap();

    private final static int RADIATE_LIGHT_LEVEL = 15;
    public final static int RADIATE_SCREECH_DISTANCE = 15;
    private static HashSet<UUID> screechSet = new HashSet<>();


    private RadiateSpell() {
        super(SpellType.RADIATE,
                "Creates an aura of light around you for as long as you have it toggled, or until your mana runs out. This light wards off any mobs, so you won’t be attacked by monsters.",
                7,
                ChatColor.YELLOW,
                new ItemStack[]{new ItemStack(Material.GLOWSTONE, 1), new ItemStack(Material.REDSTONE, 1)},
                2.0d,
                5,
                true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.SILVERFISH}, 0.02));



    }

    private static RadiateSpell instance;

    public static RadiateSpell getInstance() {
        if (instance == null) {
            synchronized (RadiateSpell.class) {
                if (instance == null)
                    instance = new RadiateSpell();
            }
        }
        return instance;
    }

    private void screech(Player player) {

        float magnitude = 6.0f;

        final int REFRESH = 20;


        final int[] screechParticles = {1};


        int frequency = 3;

        final boolean[] isFinished = {false};

        screechSet.add(player.getUniqueId());

        final UUID uniqueId = player.getUniqueId();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isDead() || !player.isOnline() || !hasDraining(player.getUniqueId()) || isFinished[0] || !screechSet.contains(uniqueId)) {
                    screechSet.remove(uniqueId);
                    this.cancel();
                    return;
                }


                if (screechParticles[0] > RADIATE_SCREECH_DISTANCE) {
                    isFinished[0] = true;
                    screechParticles[0] = 1;
                    return;
                }

                screechParticles[0]++;

                drawInPlane(player.getEyeLocation().clone().add(0, -3, 0), screechParticles[0]);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10.0f, 0.33f);


            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, REFRESH / frequency);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (player.isDead() || !player.isOnline() || !hasDraining(player.getUniqueId()) || isFinished[0] || !screechSet.contains(uniqueId)) {
                    this.cancel();
                    return;
                }

                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), RADIATE_SCREECH_DISTANCE, RADIATE_SCREECH_DISTANCE, RADIATE_SCREECH_DISTANCE)) {
                    if (!TamingListener.isHostile(entity))
                        continue;
                    if (!(entity instanceof Mob))
                        continue;

                    // attract   Vector dir =   entity.getLocation().toVector().clone().multiply(-1).add(player.getLocation().toVector().clone()).normalize();
                    Vector dir = entity.getLocation().toVector().clone().add(player.getLocation().toVector().clone().multiply(-1)).normalize();

                    Location location = entity.getLocation().toVector().add(dir.clone().multiply(magnitude)).toLocation(entity.getWorld());

                    CraftMob craftMob = (CraftMob) entity;

                    if(craftMob.getTarget() != null)
                    craftMob.setTarget(null);

                    // PathEntity pathEntity = craftMob.getHandle().getNavigation().a(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), 20);
                    craftMob.getHandle().getNavigation().a(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 1.5d);
                }
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, REFRESH);
    }

    private void drawInPlane(Location location, double distance) {

        // We will use these for drawing our parametric curve on the plane:
        double twopi = 2 * Math.PI;
        double times = 1 * twopi;
        double division = twopi / 100;
        //This is how far away we want the plane's origin to be:
        double radius = 1d;

        //Get the normal vector to the plane, nv:
        Location c = location;
        Vector nv = new Vector(0.0000000001f, 1, 0.0000000001f); // c.getDirection().normalize();

        // Coordinates where we want the origin to appear
        double nx = radius * nv.getX() + c.getX();
        double ny = radius * nv.getY() + c.getY();
        double nz = radius * nv.getZ() + c.getZ();

        // Get your basis vectors for the plane
        Vector ya = KVectorUtils.perp(nv, new Vector(0, 1, 0)).normalize();
        Vector xa = ya.getCrossProduct(nv).normalize();

        //nv.multiply(-1);


        double color = 0;
        // For loop for your parametric equation
        for (double theta = 0; theta < times; theta += division) {

            // Coordinates with respect to our basis
            double xb = distance * Math.cos(theta); //calculate x coordinate
            double yb = distance * Math.sin(theta); //calculate y coordinate

            // Multiply the transformation matrix with our coordinates for the change of basis
            double xi = xa.getX() * xb + ya.getX() * yb + nv.getX();
            double yi = xa.getY() * xb + ya.getY() * yb + nv.getY();
            double zi = xa.getZ() * xb + ya.getZ() * yb + nv.getZ();

            // Translate the coordinates in front of the player
            double x = xi + nx;
            double y = yi + ny;
            double z = zi + nz;

            color = (color % 24) + 1;

            location.getWorld().spawnParticle(Particle.SPELL_WITCH,  new Location(c.getWorld(), x, y, z), 1);
            //location.getWorld().spawnParticle(Particle.NOTE, new Location(c.getWorld(), x, y, z), 1, color / 24D, 0, 0, 1);
        }

    }

    public static int getRadiateLightLevel() {
        return RADIATE_LIGHT_LEVEL;
    }

    public static void createLight(Location loc) {
        LightAPI.createLight(loc, LightType.BLOCK, getRadiateLightLevel(), true);
        for (ChunkInfo info : LightAPI.collectChunks(loc, LightType.BLOCK, getRadiateLightLevel()))
            LightAPI.updateChunk(info, LightType.BLOCK);
    }

    public static void deleteLight(Location loc) {
        LightAPI.deleteLight(loc, LightType.BLOCK, true);

        for (ChunkInfo info : LightAPI.collectChunks(loc, LightType.BLOCK, getRadiateLightLevel()))
            LightAPI.updateChunk(info, LightType.BLOCK);
    }

    @Override
    public boolean executeSpell(Player player) {

        Location loc = player.getLocation().clone().add(0, 1, 0);

        Location oldLocation = lightMap.get(player.getUniqueId());
        if (oldLocation != null) {
            deleteLight(oldLocation);
        }

        lightMap.put(player.getUniqueId(), loc);
        createLight(loc);
        player.getWorld().playSound(loc, Sound.BLOCK_FURNACE_FIRE_CRACKLE, 5.0f, 0.0f);

        screech(player);


        return true;
    }

    @Override
    public double getManaCostPerSecond() {
        return 1.3f;
    }

    @Override
    public void manaDrainExecuteSpell(UUID uuid) {


    }

    @Override
    public void stopManaDrain(UUID uuid) {

        Player player = Bukkit.getPlayer(uuid);

        if (player != null && player.isOnline()) {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 5.0f, 0.0f);

        }
        final Location loc = lightMap.get(uuid);
        if (loc != null) {
            deleteLight(loc);
            lightMap.remove(uuid);
        }

    }


    @Override
    public void updateCooldown(Player player) {
        radiateSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    radiateSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                radiateSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (radiateSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = radiateSpellCooldownMap.get(player.getUniqueId());
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

    public static HashMap<UUID, Location> getLightMap() {
        return lightMap;
    }


    public static boolean isScreeching(UUID uuid) {
        return screechSet.contains(uuid);
    }
}
