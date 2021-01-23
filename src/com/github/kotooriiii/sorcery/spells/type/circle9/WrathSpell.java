package com.github.kotooriiii.sorcery.spells.type.circle9;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.ImageParticles;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.util.HelperMethods;
import jdk.jfr.events.FileReadEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class WrathSpell extends Spell {

    private static HashMap<UUID, Double> wrathSpellCooldownMap = new HashMap<UUID, Double>();

    private static final float DAMAGE = 3f;
    private static final int DURATION = 10;


    private WrathSpell() {
        super(SpellType.WRATH, "Summons a huge lightning storm that deals tons of damage in the direction you casted it. Very useful in team fights.",
                9,
                ChatColor.BLACK,
                new ItemStack[]{new ItemStack(Material.DRAGON_EGG, 1), new ItemStack(Material.FIRE_CHARGE, 1)},
                1.0f,
                80,
                true, true, false);
    }

    private static WrathSpell instance;

    public static WrathSpell getInstance() {
        if (instance == null) {
            synchronized (WrathSpell.class) {
                if (instance == null)
                    instance = new WrathSpell();
            }
        }
        return instance;
    }


    @Override
    public boolean executeSpell(Player player) {
        final int LIGHTNING_RANGE = 50, RADIUS = 5;
        List<Block> lineOfSightLightning = player.getLineOfSight(null, LIGHTNING_RANGE);

        // Get target block (last block in line of sight)

        final Location lightningLocation = lineOfSightLightning.get(lineOfSightLightning.size() - 1).getLocation();


        final int[] timer = {0};

        spawnWings(player);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (timer[0] / 4 == DURATION) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Block roseBlock = lightningLocation.clone().add(0, 1, 0).getBlock();
                            int roseY = getWitherY(roseBlock.getLocation());
                            if(roseY == -1)
                                return;
                            roseBlock = roseBlock.getLocation().getWorld().getBlockAt(roseBlock.getX(), roseY, roseBlock.getZ());

                            removeFire(roseBlock.getRelative(BlockFace.WEST));
                            removeFire(roseBlock.getRelative(BlockFace.NORTH));
                            removeFire(roseBlock.getRelative(BlockFace.SOUTH));
                            removeFire(roseBlock.getRelative(BlockFace.EAST));


                            final Plot standingOnPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(roseBlock.getLocation());
                            if (standingOnPlot == null) {
                                roseBlock.getWorld().spawnParticle(Particle.SMOKE_LARGE, roseBlock.getLocation(), 10, 3, 3, 3);



                                roseBlock.setType(Material.WITHER_ROSE);
                            } else {
                                if (!standingOnPlot.getType().isStaff()) {
                                    roseBlock.getWorld().spawnParticle(Particle.SMOKE_LARGE, roseBlock.getLocation(), 10, 3, 3, 3);
                                    roseBlock.setType(Material.WITHER_ROSE);
                                }
                            }


                        }
                    }.runTaskLater(LostShardPlugin.plugin, 10);
                    this.cancel();
                    return;
                }


                cylinder(player, lightningLocation.clone().add(0, 1, 0), Material.FIRE, RADIUS);
                timer[0]++;
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 5);


        return true;
    }

    private void removeFire(Block block) {
        if (block.getType() == Material.FIRE) {
            block.setType(Material.AIR);
        }
    }

    private void spawnWings(Player player) {
        BufferedImage bufferedImage = null;
        try {
            if (!FileManager.getAngelWings().exists()) {
                LostShardPlugin.plugin.getLogger().severe("Angel wings file is not loaded on the server.");
                return;
            }
            bufferedImage = ImageIO.read(FileManager.getAngelWings());

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        final int WIDTH = 300 / 3, HEIGHT = 300 / 3;
        bufferedImage = HelperMethods.resize(bufferedImage, WIDTH, HEIGHT);
        ImageParticles particles = new ImageParticles(bufferedImage, 1);

        //width = 50 , height = 10
        particles.setAnchor(WIDTH / 2, HEIGHT / 2);
        // 0.1 means 10 particles in a block
        particles.setDisplayRatio(0.1);


        Map<Location, Color> particle = particles.getParticles(player.getEyeLocation(), 25, player.getEyeLocation().getYaw());


        new BukkitRunnable() {

            final int DURATION = 5;
            int progress = 0;

            @Override
            public void run() {

                if ((float) (progress) / 4f >= DURATION) {


                    this.cancel();
                    return;
                }

                for (Map.Entry<Location, Color> entry : particle.entrySet()) {
                    entry.getKey().getWorld().spawnParticle(Particle.REDSTONE, entry.getKey(), 1, new Particle.DustOptions(entry.getValue(), 1));
                }

                progress += 5;
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, 5);
    }

    private void cylinder(Player player, Location loc, Material mat, int r) {

        //CenterX
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();

        World w = loc.getWorld();
        int rSquared = r * r;

        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {

                //Location at x, center Y, and z.
                Location burnLocation = w.getBlockAt(x, cy, z).getLocation();
                int y = getY(burnLocation);
                if (y == -1)
                    continue;

                burnLocation = w.getBlockAt(x, y, z).getLocation();

                if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {

                    if (Math.random() <= 0.30d) {
                        Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(burnLocation);

                        player.getLocation().getWorld().strikeLightning(burnLocation);
                        damage(player, burnLocation);

                        if (plot != null) {
                            if (plot.getType().isStaff())
                                continue;

                            if (plot instanceof PlayerPlot) {
                                if (!(((PlayerPlot) plot).isJointOwner(player.getUniqueId()) || ((PlayerPlot) plot).isOwner(player.getUniqueId())))
                                    continue;

                            }
                        }

                        burnLocation.getBlock().setType(mat);


                    }


                }
            }
        }
    }

    public int getWitherY(Location location) {

        Location cloneLoc = location.clone();

        //exclusive. i.e 1 block for feet.
        final int Y_SPACE = 3, Y_SPACE_DOWN = 12;

        if (cloneLoc.getBlock().getType().isAir()) {

            //If Y point is still higher than the space we let it, and y is not bedrock, and its not air and its not fire OR  if block below is not solid OR  block below is fire ,
            //End Loop: If Y point is less than the space we let it, OR y is bedrock, OR its air or its fire AND [if block below is solid AND block below is not fire] ,
            for (int y = cloneLoc.getBlockY(); y >= location.getBlockY() - Y_SPACE_DOWN && y != 0 && !( cloneLoc.getBlock().getType().isAir() && cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()); y--) {
                cloneLoc.setY(y);
            }
        } else {
            for (int y = cloneLoc.getBlockY(); y <= location.getBlockY() + Y_SPACE && y != cloneLoc.getWorld().getMaxHeight() && !( cloneLoc.getBlock().getType().isAir() && cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) ; y++) {
                cloneLoc.setY(y);
            }
        }

        if (cloneLoc.getBlockY() == cloneLoc.getWorld().getMaxHeight() || cloneLoc.getBlockY() == 0)
            return -1;
        if (cloneLoc.getBlockY() - 1 < location.getBlockY() - Y_SPACE_DOWN || cloneLoc.getBlockY() + 1 > location.getBlockY() + Y_SPACE) {
            return -1;
        }

        return cloneLoc.getBlockY();
    }

    public int getY(Location location) {

        Location cloneLoc = location.clone();

        //exclusive. i.e 1 block for feet.
        final int Y_SPACE = 3, Y_SPACE_DOWN = 10;

        if (cloneLoc.getBlock().getType().isAir()) {

            //If Y point is still higher than the space we let it, and y is not bedrock, and its not air and its not fire OR  if block below is not solid OR  block below is fire ,
            //End Loop: If Y point is less than the space we let it, OR y is bedrock, OR its air or its fire AND [if block below is solid AND block below is not fire] ,
            for (int y = cloneLoc.getBlockY(); y >= location.getBlockY() - Y_SPACE_DOWN && y != 0 && !( (cloneLoc.getBlock().getType().isAir() || cloneLoc.getBlock().getType() == Material.FIRE) && (cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid() && cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType() != Material.FIRE)); y--) {
                cloneLoc.setY(y);
            }
        } else {
            for (int y = cloneLoc.getBlockY(); y <= location.getBlockY() + Y_SPACE && y != cloneLoc.getWorld().getMaxHeight() && !( (cloneLoc.getBlock().getType().isAir() || cloneLoc.getBlock().getType() == Material.FIRE) && (cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid() && cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType() != Material.FIRE)) ; y++) {
                cloneLoc.setY(y);
            }
        }

        if (cloneLoc.getBlockY() == cloneLoc.getWorld().getMaxHeight() || cloneLoc.getBlockY() == 0)
            return -1;
        if (cloneLoc.getBlockY() - 1 < location.getBlockY() - Y_SPACE_DOWN || cloneLoc.getBlockY() + 1 > location.getBlockY() + Y_SPACE) {
            return -1;
        }

        if (cloneLoc.getBlockY() - 1 <= 0)
            return -1;

        return cloneLoc.getBlockY() - 1;
    }


    @Override
    public void updateCooldown(Player player) {
        wrathSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    wrathSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                wrathSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (wrathSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = wrathSpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(0, RoundingMode.UP);
            int value = bd.intValue();
            if (value == 0)
                value = 1;

            String time = "seconds";
            if (value == 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }


    private void damage(Player attacker, final Location location) {
        Clan clan = LostShardPlugin.getClanManager().getClan(attacker.getUniqueId());

        for (Entity entity : attacker.getLocation().getWorld().getNearbyEntities(location, 1, 1, 1)) {
            if (!(entity instanceof LivingEntity))
                continue;
            if (attacker.equals(entity)) {
                attacker.damage(DAMAGE);
                continue;
            }
            if (clan != null && entity instanceof Player && clan.isInThisClan(entity.getUniqueId()) && !clan.isFriendlyFire())
                continue;

            EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(attacker, entity, EntityDamageEvent.DamageCause.CUSTOM, DAMAGE);
            entity.setLastDamageCause(damageByEntityEvent);
            Bukkit.getPluginManager().callEvent(damageByEntityEvent);
            ((Player) entity).setHealth(((Player) entity).getHealth() - DAMAGE);
        }
    }
}
