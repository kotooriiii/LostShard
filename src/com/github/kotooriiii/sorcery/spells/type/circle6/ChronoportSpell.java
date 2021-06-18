package com.github.kotooriiii.sorcery.spells.type.circle6;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import com.github.kotooriiii.sorcery.spells.type.circle5.WebFieldSpell;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class ChronoportSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> chronoportSpellCooldownMap = new HashMap<>();

    private final static HashMap<UUID, Location> chronoportMap = new HashMap<>();

    private final static int TIMING = 5;


    private ChronoportSpell() {
        super(SpellType.CHRONOPORT, "Functions as a rubberband that teleports you right back to where you casted it. After " + TIMING + " seconds, the place that you casted chronoport is where you will return. ", 6, ChatColor.DARK_AQUA, new ItemStack[]{new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.LAPIS_LAZULI, 1), new ItemStack(Material.STRING, 1)}, 10.0f, 20, true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.HOGLIN}, 0.03));
    }

    private static ChronoportSpell instance;

    public static ChronoportSpell getInstance() {
        if (instance == null) {
            synchronized (ChronoportSpell.class) {
                if (instance == null)
                    instance = new ChronoportSpell();
            }
        }
        return instance;
    }

    @Override
    public void updateCooldown(Player player) {
        chronoportSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    chronoportSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                chronoportSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    @Override
    public boolean isCooldown(Player player) {
        if (chronoportSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = chronoportSpellCooldownMap.get(player.getUniqueId());
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

    @Override
    public boolean executeSpell(Player player) {
        chronoport(player);
        return true;
    }


    /**
     *
     */
    private void chronoport(Player player) {

        chronoportMap.put(player.getUniqueId(), player.getLocation());
        new BukkitRunnable() {
            @Override
            public void run() {

                Location location = chronoportMap.get(player.getUniqueId());
                if (location == null || !player.isOnline() || player.isDead()) {
                    this.cancel();
                    return;
                }

                if (isLapisNearby(location, DEFAULT_LAPIS_NEARBY)) {
                    player.sendMessage(ERROR_COLOR + "You can not seem to cast " + getName() + " there...");
                    refund(player);
                    return;
                }


                if (LostShardPlugin.getAnimatorPackage().isAnimating(player.getUniqueId())) {
                    final List<Block> blocks0 = getBridgeBlocks(player.getLocation(), location, 1);
                    final List<Block> blocks1 = getBridgeBlocks(player.getLocation(), location, 0);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Block block : blocks0) {
                                for(int i = 0 ; i < 4; i++)
                                block.getLocation().getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, block.getLocation(), 2, 0.3, 0.3, 0.3, 0.05);
                            }

                            for (Block block : blocks1) {
                                for(int i = 0 ; i < 4; i++)
                                    block.getLocation().getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, block.getLocation(), 2, 0.3, 0.3, 0.3, 0.05);
                            }
                        }
                    }.runTaskAsynchronously(LostShardPlugin.plugin);


                }

                player.teleport(location);

                if (LostShardPlugin.getAnimatorPackage().isAnimating(player.getUniqueId()))
                    location.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 10, 7);


            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * TIMING);
    }

    private List<Block> getBridgeBlocks(Location initialLocation, Location endingLocation, int offset) {

        int maxDistance = 119;
        int maxLength = 0;

        ArrayList<Block> blocks = new ArrayList<Block>();


        Location clonedInitialLocation = initialLocation.clone().add(0, offset, 0);
        endingLocation.setY(endingLocation.getY() + offset);
        Vector direction = new Vector(endingLocation.getBlockX() - clonedInitialLocation.getBlockX(), endingLocation.getBlockY() - clonedInitialLocation.getBlockY(), endingLocation.getBlockZ() - clonedInitialLocation.getBlockZ());

        Iterator<Block> itr = new BlockIterator(clonedInitialLocation.getWorld(), clonedInitialLocation.toVector(), direction, 0,(int) Math.round(Math.sqrt(direction.getX()*direction.getX() + direction.getY()*direction.getY() + direction.getZ()*direction.getZ())));

        while (itr.hasNext()) {
            Block block = itr.next();

            blocks.add(block);
        }
        return blocks;
    }

    public static Vector getRightHeadDirection(Vector vector) {
        Vector direction = vector.normalize();
        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
    }

    public static Vector getLeftHeadDirection(Vector vector) {
        Vector direction = vector.normalize();
        return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
    }


}
