package com.github.kotooriiii.sorcery.spells.type.circle3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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

public class MagicArrowSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> magicArrowCooldownMap = new HashMap<>();

    private MagicArrowSpell() {
        super(SpellType.MAGIC_ARROW,
                "Shoots 3 arrows in a cone in the direction you are facing. Sort of like a multi-shot.",
                3, ChatColor.DARK_RED,
                new ItemStack[]{new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1)},
                2.0f, 20, true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.SKELETON}, 0.10));

    }

    private  static MagicArrowSpell instance;
    public static MagicArrowSpell getInstance() {
        if (instance == null) {
            synchronized (MagicArrowSpell.class) {
                if (instance == null)
                    instance = new MagicArrowSpell();
            }
        }
        return instance;
    }

    @Override
    public void updateCooldown(Player player) {
        magicArrowCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    magicArrowCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                magicArrowCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (magicArrowCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = magicArrowCooldownMap.get(player.getUniqueId());
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

        final float MAGNITUDE = 15/4;

        Arrow a1 = player.launchProjectile(Arrow.class, player.getLocation().getDirection());
        a1.setVelocity(a1.getVelocity().clone().multiply(new Vector(MAGNITUDE, 1,MAGNITUDE)));
        a1.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

        Arrow a2 = player.launchProjectile(Arrow.class, player.getLocation().getDirection().clone().rotateAroundY(Math.toRadians(45)));
        a2.setVelocity(a2.getVelocity().clone().multiply(new Vector(MAGNITUDE,1,MAGNITUDE)));
        a2.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

        Arrow a3 =player.launchProjectile(Arrow.class, player.getLocation().getDirection().clone().rotateAroundY(Math.toRadians(360-45)));
        a3.setVelocity(a3.getVelocity().clone().multiply(new Vector(MAGNITUDE,1,MAGNITUDE)));
        a3.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);



        return true;
    }
}
