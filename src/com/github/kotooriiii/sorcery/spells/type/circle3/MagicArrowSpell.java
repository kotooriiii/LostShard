package com.github.kotooriiii.sorcery.spells.type.circle3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    private static MagicArrowSpell instance;

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

    private static ArrayList<Arrow> arrows = new ArrayList<>();
    private Color[] colors = {Color.fromRGB(255, 0, 0), Color.fromRGB(255, 165, 0), Color.fromRGB(255, 255, 0), Color.fromRGB(0, 128, 0), Color.fromRGB(0, 0, 255), Color.fromRGB(75, 0, 130), Color.fromRGB(238, 130, 238)};
    private int index = 0;


    @Override
    public boolean executeSpell(Player player) {

        final float MAGNITUDE = 15 / 4;

        final boolean isAnimating = LostShardPlugin.getAnimatorPackage().isAnimating(player.getUniqueId());

        Arrow a1 = player.launchProjectile(Arrow.class, player.getLocation().getDirection());
        arrows.add(a1);
        a1.setMetadata("magicArrow", new FixedMetadataValue(LostShardPlugin.plugin, isAnimating));
        a1.setVelocity(a1.getVelocity().clone().multiply(new Vector(MAGNITUDE, 1, MAGNITUDE)));
        a1.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

        Arrow a2 = player.launchProjectile(Arrow.class, player.getLocation().getDirection().clone().rotateAroundY(Math.toRadians(45)));
        arrows.add(a2);
        a2.setMetadata("magicArrow", new FixedMetadataValue(LostShardPlugin.plugin, isAnimating));
        a2.setVelocity(a2.getVelocity().clone().multiply(new Vector(MAGNITUDE, 1, MAGNITUDE)));
        a2.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);


            Arrow a3 = player.launchProjectile(Arrow.class, player.getLocation().getDirection().clone().rotateAroundY(Math.toRadians(10)));
            arrows.add(a3);
            a3.setMetadata("magicArrow", new FixedMetadataValue(LostShardPlugin.plugin, isAnimating));
            a3.setVelocity(a3.getVelocity().clone().multiply(new Vector(MAGNITUDE, 1, MAGNITUDE)));
            a3.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);


        return true;
    }


    public void tick() {
        new BukkitRunnable() {
            @Override
            public void run() {
                final Iterator<Arrow> iterator = arrows.iterator();

                if (index == colors.length)
                    index = 0;

                while (iterator.hasNext()) {
                    final Arrow next = iterator.next();
                    final Location location = next.getLocation();

                    location.getWorld().spawnParticle(Particle.REDSTONE, location, 4, 0.25, 0.25, 0.25, new Particle.DustOptions(colors[index], 1f));
                }
                index++;
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, 1);
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        final Projectile entity = event.getEntity();
        if (entity.getType() != EntityType.ARROW)
            return;
        arrows.remove(entity);
    }

}
