package com.github.kotooriiii.sorcery.spells.type.circle2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.sorcery.spells.ImageParticles;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class WallSpell extends Spell {

    private static HashMap<UUID, Double> wallCooldownMap = new HashMap<UUID, Double>();
    final private static boolean isDebug = false;

    private final static int WALL_DISTANCE_FROM_PLAYER = 2, WALL_RADIUS_X = 2, WALL_RADIUS_Y = 1, WALL_DURATION = 10;


    public WallSpell() {
        super(SpellType.WALL,
                "Creates a wall made of stone in front of you. Useful for when you want to block the projectiles of your enemies. ",
                2,
                ChatColor.GRAY,
                new ItemStack[]{new ItemStack(Material.COBBLESTONE, 1)},
                1.0f,
                15,
                true, true, false);
    }

    private boolean createWall(Player p) {

        //Get the normal vector to the plane, nv:
        Location c = p.getEyeLocation();
        Vector nv = c.getDirection().normalize();

        // Coordinates where we want the origin to appear
        double nx = WALL_DISTANCE_FROM_PLAYER * nv.getX() + c.getX();
        double ny = WALL_DISTANCE_FROM_PLAYER * nv.getY() + c.getY();
        double nz = WALL_DISTANCE_FROM_PLAYER * nv.getZ() + c.getZ();

        // Get your basis vectors for the plane
        Vector ya = perp(nv, new Vector(0, 1, 0)).normalize();
        Vector xa = ya.getCrossProduct(nv).normalize();

        //nv.multiply(-1);


        int timer = 0;
        int offset = 1;

        // For loop for your parametric equation

        List<Block> blocks = new ArrayList<>();


        spawnWings(p);

        for (int x = -WALL_RADIUS_X; x <= WALL_RADIUS_X; x++) {
            for (int y = -WALL_RADIUS_Y; y <= WALL_RADIUS_Y; y++) {

                // Coordinates with respect to our basis
                double xb = x; //calculate x coordinate
                double yb = y; //calculate y coordinate


                // Multiply the transformation matrix with our coordinates for the change of basis
                double xi = xa.getX() * xb + ya.getX() * yb + nv.getX();
                double yi = xa.getY() * xb + ya.getY() * yb + nv.getY();
                double zi = xa.getZ() * xb + ya.getZ() * yb + nv.getZ();

                // Translate the coordinates in front of the player
                int finalX = (int) (xi + nx);
                int finalY = (int) (yi + ny);
                int finalZ = (int) (zi + nz);

                Block block = p.getWorld().getBlockAt(finalX, finalY, finalZ);
                blocks.add(block);

                // Spawn your particle
                new BukkitRunnable() {
                    @Override
                    public void run() {

                        //todo plots?

                        if (isLapisNearby(block.getLocation(), DEFAULT_LAPIS_NEARBY))
                            return;

                        if(block.getType().isAir()) {
                            block.setType(Material.STONE);
                            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_PLACE, 5.0f, 0.0f);
                        }
                    }
                }.runTaskLater(LostShardPlugin.plugin, (long) (timer++) * offset);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : blocks) {
                    if(block.getType() == Material.STONE)
                    block.getWorld().spawnParticle(Particle.DRIP_LAVA, block.getLocation(), 7, 2, 2, 2);
                }

            }
        }.runTaskLaterAsynchronously(LostShardPlugin.plugin, (long) (20 * (WALL_DURATION - 2)));

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : blocks) {
                    if (block.getType() == Material.STONE) {
                        block.setType(Material.AIR);
                        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_BREAK, 5.0f, 0.0f);
                    }
                }


            }
        }.runTaskLater(LostShardPlugin.plugin, (long) (20 * WALL_DURATION));


        return true;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
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
        final int WIDTH=300/6,HEIGHT=300/6;
       bufferedImage = resize(bufferedImage, WIDTH,HEIGHT);
        ImageParticles particles = new ImageParticles(bufferedImage, 1);

        //width = 50 , height = 10
        particles.setAnchor(WIDTH/2, HEIGHT/2);
        // 0.1 means 10 particles in a block
        particles.setDisplayRatio(0.3);


        Map<Location, Color> particle = particles.getParticles(player.getEyeLocation(), 25, player.getEyeLocation().getYaw());

        new BukkitRunnable() {

            final int DURATION = 5;
            int progress = 0;

            @Override
            public void run() {

                if((float) (progress)/20f >= DURATION)
                {
                    this.cancel();
                    return;
                }

                for (Map.Entry<Location, Color> entry : particle.entrySet()) {
                    entry.getKey().getWorld().spawnParticle(Particle.REDSTONE, entry.getKey(), 1, new Particle.DustOptions(entry.getValue(), 1));
                }

                progress += 5;
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin,  0,5);
    }

    public void drawInPlane(Player p) {

        // We will use these for drawing our parametric curve on the plane:
        double twopi = 2 * Math.PI;
        double times = 1 * twopi;
        double division = twopi / 100;
        //This is how far away we want the plane's origin to be:
        double radius = 2;

        //Get the normal vector to the plane, nv:
        Location c = p.getEyeLocation();
        Vector nv = c.getDirection().normalize();

        // Coordinates where we want the origin to appear
        double nx = radius * nv.getX() + c.getX();
        double ny = radius * nv.getY() + c.getY();
        double nz = radius * nv.getZ() + c.getZ();

        // Get your basis vectors for the plane
        Vector ya = perp(nv, new Vector(0, 1, 0)).normalize();
        Vector xa = ya.getCrossProduct(nv).normalize();

        //nv.multiply(-1);


        int timer = 0;
        int offset = 1;

        // For loop for your parametric equation
        for (double theta = 0; theta < times; theta += division) {

            // Coordinates with respect to our basis
            double xb = Math.cos(theta); //calculate x coordinate
            double yb = Math.sin(theta); //calculate y coordinate
            double zb = 0.2 * theta; //calculate y coordinate


            // Multiply the transformation matrix with our coordinates for the change of basis
            double xi = xa.getX() * xb + ya.getX() * yb + nv.getX() * zb;
            double yi = xa.getY() * xb + ya.getY() * yb + nv.getY() * zb;
            double zi = xa.getZ() * xb + ya.getZ() * yb + nv.getZ() * zb;

            // Translate the coordinates in front of the player
            double x = xi + nx;
            double y = yi + ny;
            double z = zi + nz;

            // Spawn your particle
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.spawnParticle(Particle.TOTEM, new Location(c.getWorld(), x, y, z), 1, 0, 0, 0, 0);
                }
            }.runTaskLaterAsynchronously(LostShardPlugin.plugin, (long) (timer++) * offset);
        }

    }

    private Vector perp(Vector onto, Vector u) {
        return u.clone().subtract(proj(onto, u));
    }

    private Vector proj(Vector onto, Vector u) {
        return onto.clone().multiply(onto.dot(u) / onto.lengthSquared());
    }

    @Override
    public boolean executeSpell(Player player) {

        createWall(player);

        //drawInPlane(player);

        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        wallCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    wallCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                wallCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (wallCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = wallCooldownMap.get(player.getUniqueId());
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
}
