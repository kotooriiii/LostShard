package com.github.kotooriiii.sorcery.spells.type.circle9;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.getPlayerInduced;

public class SlothSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> slothSpellCooldownMap = new HashMap<>();
    private final static int DURATION = 15;

    private final static HashSet<UUID> slothers = new HashSet<>();


    private SlothSpell() {
        super(SpellType.SLOTH,
                "You take less damage and knock people far away for " + DURATION + " seconds! However, you become really slow.",
                9, ChatColor.DARK_PURPLE, new ItemStack[]{new ItemStack(Material.DRAGON_EGG, 1), new ItemStack(Material.SCUTE, 1)}, 2.0f, 45, true, true, false);
    }

    private static SlothSpell instance;

    public static SlothSpell getInstance() {
        if (instance == null) {
            synchronized (SlothSpell.class) {
                if (instance == null)
                    instance = new SlothSpell();
            }
        }
        return instance;
    }


    @Override
    public void updateCooldown(Player player) {
        slothSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    slothSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                slothSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (slothSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = slothSpellCooldownMap.get(player.getUniqueId());
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


        getSlothers().add(player.getUniqueId());
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * DURATION, 2, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * DURATION, 4, false, false, false));

        final UUID uuidConst = player.getUniqueId();

        final int[] timer = {0};
        int period = 20;
        new BukkitRunnable() {
            @Override
            public void run() {

                if (timer[0]++ * (period) >= DURATION * 20 || !getSlothers().contains(uuidConst) || !player.isOnline() || player.isDead()) {
                    this.cancel();
                    getSlothers().remove(uuidConst);
                    return;
                }

                player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 4, 0.4f, 0.4f, 0.4f, new Particle.DustOptions(Color.fromRGB(82,    100,18), 1f));
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, period);


        return true;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()) || CitizensAPI.getNPCRegistry().isNPC(event.getDamager()))
            return;
        if (!HelperMethods.isPlayerInduced(event.getEntity(), event.getDamager()))
            return;

        //Init basic variables
        Player defender = (Player) event.getEntity();
        Player attacker = getPlayerInduced(defender, event.getDamager());

        if (!getSlothers().contains(attacker.getUniqueId()))
            return;

        /*
        Event is not canceled
        NPC free
        Player induced
        Attacker is a sloth
         */

        Vector dir = defender.getLocation().toVector().clone().subtract(attacker.getLocation().toVector().clone());

        defender.setVelocity(dir.normalize().multiply(new Vector(10, 1, 10)));

    }

    @EventHandler
    public void onMoveJumpersListener(PlayerMoveEvent event) {

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

        if (!getSlothers().contains(event.getPlayer().getUniqueId()))
            return;
        if (!Spell.isLapisNearby(event.getTo(), Spell.getDefaultLapisNearbyValue()))
            return;

        getSlothers().remove(event.getPlayer().getUniqueId());

        event.getPlayer().sendMessage(ERROR_COLOR + "Something doesn't seem to let you become a sloth anymore...");
    }


    public static HashSet<UUID> getSlothers() {
        return slothers;
    }
}
