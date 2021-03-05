package com.github.kotooriiii.sorcery.spells.type.circle9;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.skills.skill_listeners.TamingListener;
import com.github.kotooriiii.sorcery.spells.KVectorUtils;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import com.github.kotooriiii.sorcery.spells.type.circle8.DaySpell;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PrideSpell extends Spell implements Listener {

    private static HashMap<UUID, Double> prideSpellCooldownMap = new HashMap<UUID, Double>();

    public final static int PRIDE_DISTANCE = 4;
    private final static float PRIDE_DURATION = 15.0f;

    private final static int ABSORB_POINTS = 2*2;

    private static HashSet<UUID> prideSet = new HashSet<>();
    private static HashSet<UUID> afterHoursPrideSet = new HashSet<>();


    private PrideSpell() {
        super(SpellType.PRIDE,
                "For " + (int) PRIDE_DURATION + " seconds, you get a pride circle around you that whoever steps foot in, drains " + (float) ABSORB_POINTS / 2 + " hp per second and you gain " + (float) ABSORB_POINTS / 2 + " hp per second.",
                9,
                ChatColor.BLUE,
                new ItemStack[]{new ItemStack(Material.DRAGON_EGG, 1), new ItemStack(Material.POPPY, 1)},
                2d,
                60,
                true, true, false, new SpellMonsterDrop(new EntityType[]{EntityType.ENDER_DRAGON}, 0.1111111111));

    }

    private static PrideSpell instance;

    public static PrideSpell getInstance() {
        if (instance == null) {
            synchronized (PrideSpell.class) {
                if (instance == null)
                    instance = new PrideSpell();
            }
        }
        return instance;
    }

    public void drawInPlane(Location location, Particle particle, double chance, double distance) {

        if(Math.random() < chance && particle != Particle.REDSTONE)
            return;

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

        // For loop for your parametric equation
        for (double theta = chance == 1 ?  0 : Math.random()*twopi; theta < times; theta += division) {



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

//
//            // 6 = RED
//            double ran = 6d / 24d;

            Location prideLocation =  new Location(c.getWorld(), x, y, z);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(particle == Particle.REDSTONE)
                player.getWorld().spawnParticle(Particle.REDSTONE, prideLocation, 2, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(26, 5, 4), 1f));
                else {

                    player.getWorld().spawnParticle(particle, prideLocation, 2);
                }

            }
            if(chance != 1)
                break;


            //player.spawnParticle(Particle.NOTE, new Location(c.getWorld(), x, y, z), 0, ran, 0, 0, 1);
        }

    }

    @Override
    public boolean executeSpell(Player player) {

        final int REFRESH = 20;

        final int[] timerA = {0};
        final int[] timerB = {0};

        final UUID uniqueId = player.getUniqueId();

        Clan clan = LostShardPlugin.getClanManager().getClan(uniqueId);

        prideSet.add(player.getUniqueId());

        Particle particle = Particle.FALLING_LAVA;

        final int PARTICLE_FREQUENCY = 1;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (timerA[0] * (PARTICLE_FREQUENCY) >= PRIDE_DURATION * 20 || player.isDead() || !player.isOnline() || !prideSet.contains(uniqueId)) {
                    this.cancel();
                    return;
                }

                if (player.isOnline() && !player.isDead()) {
                    drawInPlane(player.getEyeLocation().clone().add(0, -3, 0), Particle.REDSTONE, 1, PRIDE_DISTANCE);
                    drawInPlane(player.getEyeLocation().clone().add(0, -3, 0), particle, 0.25, PRIDE_DISTANCE-1);
                    drawInPlane(player.getEyeLocation().clone().add(0, -3, 0), particle, 0.25, PRIDE_DISTANCE-2);
                    drawInPlane(player.getEyeLocation().clone().add(0, -3, 0), particle, 0.25, PRIDE_DISTANCE-3);

                    if((timerA[0] * (PARTICLE_FREQUENCY)) % 20 == 0)
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10.0f, 0.33f);
                }

                timerA[0]++;
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, PARTICLE_FREQUENCY);


        new BukkitRunnable() {
            @Override
            public void run() {

                if (timerB[0] * REFRESH >= PRIDE_DURATION * 20 || player.isDead() || !player.isOnline() || !prideSet.contains(uniqueId)) {

                    if (player.isOnline() && !player.isDead() && prideSet.contains(uniqueId)) {
                        afterHoursPrideSet.add(uniqueId);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.isOnline() && player.getAbsorptionAmount() > 0 && afterHoursPrideSet.contains(uniqueId)) {
                                    double newAbsorption = player.getAbsorptionAmount() - 2;
                                    if (newAbsorption < 0) {
                                        player.setAbsorptionAmount(0);
                                    } else {
                                        player.setAbsorptionAmount(newAbsorption);
                                    }
                                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 5.0f, 1f);
                                    player.getWorld().spawnParticle(Particle.FALLING_HONEY, player.getEyeLocation().clone().add(0, 0.5f, 0), 3);
                                    return;
                                }

                                afterHoursPrideSet.remove(uniqueId);
                                this.cancel();
                            }
                        }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
                    }
                    prideSet.remove(uniqueId);
                    this.cancel();
                    return;
                }

                float damage = 0;

                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), PRIDE_DISTANCE, PRIDE_DISTANCE, PRIDE_DISTANCE)) {
                    if (!(entity instanceof Player))
                        continue;
                    if (entity == player)
                        continue;

                    Player loopedPlayer = (Player) entity;

                    if (clan != null) {
                        if (!clan.isFriendlyFire()) {
                            if (clan.isInThisClan(uniqueId) && clan.isInThisClan(loopedPlayer.getUniqueId())) {
                                continue;
                            }
                        }
                    }

                    Vector dir = ((Player) entity).getEyeLocation().toVector().clone().subtract(player.getEyeLocation().toVector().clone());

                    BlockIterator iterator = new BlockIterator(entity.getLocation().getWorld(), player.getEyeLocation().toVector(), dir, 0, 120);
                    new BukkitRunnable() {
                        private boolean color = false;

                        @Override
                        public void run() {
                            if (iterator.hasNext()) {
                                Block next = iterator.next();
                                entity.getWorld().spawnParticle(Particle.REDSTONE, next.getLocation(), 1, 0, 0, 0, color ? new Particle.DustOptions(Color.fromRGB(55, 0, 0), 1f) : new Particle.DustOptions(Color.fromRGB(60, 85, 90), 1f));
                                color = !color;

                            } else {
                                this.cancel();
                            }

                        }
                    }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, 1);

                    damage += ABSORB_POINTS;

                    EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(player, entity, EntityDamageEvent.DamageCause.CUSTOM, ABSORB_POINTS);
                    entity.setLastDamageCause(damageByEntityEvent);
                    Bukkit.getPluginManager().callEvent(damageByEntityEvent);
                    if (!damageByEntityEvent.isCancelled()) {
                        double newHealth = ((Player) entity).getHealth() - ABSORB_POINTS;
                        if (newHealth < 0)
                            ((Player) entity).setHealth(0);
                        else
                            ((Player) entity).setHealth(newHealth);
                    }

                }

                double newHealth = player.getHealth() + ABSORB_POINTS;
                if (newHealth > player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                    double newAbsorption = player.getAbsorptionAmount() + ABSORB_POINTS;
                    if (newAbsorption > 20) {

                    } else {
                        player.setAbsorptionAmount(newAbsorption);
                    }
                } else {
                    player.setHealth(newHealth);
                }

                timerB[0]++;
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, REFRESH);

        return true;
    }

    public boolean isPrideEffect(UUID uuid) {
        return prideSet.contains(uuid);
    }

    public static boolean removePride(UUID uuid) {
        return prideSet.remove(uuid);
    }

    @Override
    public void updateCooldown(Player player) {
        prideSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    prideSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                prideSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (prideSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = prideSpellCooldownMap.get(player.getUniqueId());
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

    @EventHandler
    public void onMoveToLapisPrideEffect(PlayerMoveEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        final int x_initial, y_initial, z_initial,
                x_final, y_final, z_final;

        x_initial = event.getFrom().getBlockX();
        y_initial = event.getFrom().getBlockY();
        z_initial = event.getFrom().getBlockZ();

        x_final = event.getTo().getBlockX();
        y_final = event.getTo().getBlockY();
        z_final = event.getTo().getBlockZ();

        if (x_initial == x_final && y_initial == y_final && z_initial == z_final)
            return;

        if (!prideSet.contains(event.getPlayer().getUniqueId()))
            return;
        if (!Spell.isLapisNearby(event.getTo(), Spell.getDefaultLapisNearbyValue()))
            return;

        prideSet.remove(event.getPlayer().getUniqueId());

        event.getPlayer().sendMessage(ERROR_COLOR + "Something doesn't make you full of pride anymore...");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.LAPIS_BLOCK)
            return;
        if (!prideSet.contains(event.getPlayer().getUniqueId()))
            return;
        prideSet.remove(event.getPlayer().getUniqueId());

        event.getPlayer().sendMessage(ERROR_COLOR + "Something doesn't make you full of pride anymore...");
    }
}
