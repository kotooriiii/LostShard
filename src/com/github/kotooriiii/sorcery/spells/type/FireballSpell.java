package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class FireballSpell extends Spell {

    private static HashMap<UUID, Double> fireballSpellCooldownMap = new HashMap<UUID, Double>();

    public FireballSpell() {

        super(SpellType.FIREBALL,
                ChatColor.RED,
                new ItemStack[]{new ItemStack(Material.GUNPOWDER, 1), new ItemStack(Material.REDSTONE, 1)},
                2.0f,
                15);
    }

    @Override
    public boolean executeSpell(Player player) {
        List<Block> lineOfSightFireball = player.getLineOfSight(null, 4);

        // Only spawn fireball if there is some room infront of player
//                if (lineOfSightFireball.size() < 4) {
//                    player.sendMessage(ERROR_COLOR + "You need more space to cast that spell!");
//                    return false;
//                }
        // Get location of block infront of player
        Location targetLocationFireball = lineOfSightFireball.get(lineOfSightFireball.size() - 1).getLocation();

        // Calculate fireball position from block position
        // Take the player rotation so it flies in the correct direction
        Location fireballLocation = new Location(
                player.getWorld(),
                targetLocationFireball.getX(),
                targetLocationFireball.getY(),
                targetLocationFireball.getZ(),
                player.getLocation().getYaw(),
                player.getLocation().getPitch());

        // Spawn fireball
        Fireball fireball = (Fireball) player.launchProjectile(Fireball.class, player.getEyeLocation().getDirection());
        fireball.setShooter(player);
        Vector origVector = fireball.getDirection();
        Vector vector = new Vector(origVector.getX(), origVector.getY(), origVector.getZ()).multiply(2);
        fireball.setVelocity(vector);
        fireball.setDirection(vector);
        return true;
    }

    @Override
    public void updateCooldown(Player player)
    {
        fireballSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    fireballSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                fireballSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (fireballSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = fireballSpellCooldownMap.get(player.getUniqueId());
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
