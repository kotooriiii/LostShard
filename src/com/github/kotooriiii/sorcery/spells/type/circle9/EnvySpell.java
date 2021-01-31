package com.github.kotooriiii.sorcery.spells.type.circle9;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellToggleable;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import com.github.kotooriiii.sorcery.spells.type.circle7.RadiateSpell;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.platforms;
import static com.github.kotooriiii.util.HelperMethods.getPlayerInduced;

public class EnvySpell extends Spell implements Listener {

    //Cooldown map
    private static final HashMap<UUID, Double> envyCooldownMap = new HashMap<UUID, Double>();
    private static final HashSet<UUID> envySet = new HashSet<>();

    //Constants
    private final static int ENVY_DURATION = 20;


    private EnvySpell() {
        super(SpellType.ENVY,
                "Switch stats with the next player you hit. You will get their stamina, mana, and health, and they will get yours. Best if you are low on all of them when you use this.",
                9,
                ChatColor.DARK_RED,
                new ItemStack[]{new ItemStack(Material.DRAGON_EGG, 1), new ItemStack(Material.NETHER_STAR, 1)},
                1.0d,
                100,
                true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.ENDER_DRAGON}, 0.1111111111));


    }


    private static EnvySpell instance;

    public static EnvySpell getInstance() {
        if (instance == null) {
            synchronized (EnvySpell.class) {
                if (instance == null)
                    instance = new EnvySpell();
            }
        }
        return instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDmg(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()) || CitizensAPI.getNPCRegistry().isNPC(event.getDamager()))
            return;
        if (!HelperMethods.isPlayerInduced(event.getEntity(), event.getDamager()))
            return;

        //Init basic variables
        Player defender = (Player) event.getEntity();
        Player attacker = getPlayerInduced(defender, event.getDamager());

        if (!envySet.contains(attacker.getUniqueId()))
            return;
        envySet.remove(attacker.getUniqueId());


        UUID defenderUUID = defender.getUniqueId();
        Stat defenderStat = Stat.wrap(defenderUUID);

        final double oldDefenderMana = defenderStat.getMana();
        final double oldDefenderStamina = defenderStat.getStamina();
        final double oldDefenderHealth = defender.getHealth();
        final int oldDefenderHunger = defender.getFoodLevel();
        final Location oldDefenderLocation = defender.getLocation().clone();
        final Location oldDefenderEyeHeight = defender.getEyeLocation().clone();
        final int oldTotalXP = defender.getTotalExperience();
        final Collection<PotionEffect> oldDefenderPotionEffects = new ArrayList<>(defender.getActivePotionEffects());

        final Location oldAttackerLocation = attacker.getLocation().clone();

        UUID attackerUUID = attacker.getUniqueId();
        Stat attackerStat = Stat.wrap(attackerUUID);

        //Swap
        defender.setHealth(attacker.getHealth());
        defender.setFoodLevel(attacker.getFoodLevel());
        defenderStat.setMana(attackerStat.getMana());
        defenderStat.setStamina(attackerStat.getStamina());
        defender.setTotalExperience(attacker.getTotalExperience());
        defender.teleport(attacker.getLocation());

        for (PotionEffect effect : oldDefenderPotionEffects)
            defender.removePotionEffect(effect.getType());
        defender.addPotionEffects(attacker.getActivePotionEffects());

        attacker.setHealth(oldDefenderHealth);
        attacker.setFoodLevel(oldDefenderHunger);
        attackerStat.setMana(oldDefenderMana);
        attackerStat.setStamina(oldDefenderStamina);
        attacker.teleport(oldDefenderLocation);
        for (PotionEffect effect : attacker.getActivePotionEffects())
            attacker.removePotionEffect(effect.getType());
        attacker.addPotionEffects(oldDefenderPotionEffects);
        attacker.setTotalExperience(oldTotalXP);

        attacker.sendMessage(ChatColor.RED + "You have sated your envious desire with " + defender.getName() + ".");
        defender.sendMessage(ChatColor.RED + attacker.getName() + " sated his envious desire.");

        attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 5.0f, 5.333f);
        defender.getWorld().playSound(defender.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 5.0f, 5.333f);

        Vector dir = oldAttackerLocation.toVector().subtract(oldDefenderLocation.toVector());
        BlockIterator blockIterator = new BlockIterator(oldAttackerLocation.getWorld(), oldDefenderEyeHeight.toVector(), dir, 0, 120);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (blockIterator.hasNext()) {
                    final Block next = blockIterator.next();
                    next.getWorld().spawnParticle(Particle.REDSTONE, next.getLocation(), 10, 0, 0, 0, new Particle.DustOptions(Color.RED, 1f));
                } else {
                    this.cancel();
                }

            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 10, 1);
        event.setCancelled(true);
    }


    @Override
    public boolean executeSpell(Player player) {


        envySet.add(player.getUniqueId());
        final UUID uuid = player.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!envySet.contains(uuid))
                    return;
                player.sendMessage(ERROR_COLOR + "You no longer feel envious.");
                envySet.remove(uuid);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * ENVY_DURATION);

        return true;
    }


    @Override
    public void updateCooldown(Player player) {
        envyCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    envyCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                envyCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (envyCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = envyCooldownMap.get(player.getUniqueId());
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
    public void onMoveEnvyListener(PlayerMoveEvent event) {

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

        if (!envySet.contains(event.getPlayer().getUniqueId()))
            return;
        if (!Spell.isLapisNearby(event.getTo(), Spell.getDefaultLapisNearbyValue()))
            return;
        envySet.remove(event.getPlayer().getUniqueId());
        event.getPlayer().sendMessage(ERROR_COLOR + "Something doesn't seem to make you envious anymore...");
    }

}
