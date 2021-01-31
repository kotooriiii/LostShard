package com.github.kotooriiii.sorcery.spells.type.circle8;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class SoarSpell extends Spell implements Listener {

    //todo Cooldown map
    private static HashMap<UUID, Double> flightCooldownMap = new HashMap<UUID, Double>();
    private final static HashMap<UUID, Integer> waitingToRecallMap = new HashMap<>();
    private final static HashSet<UUID> flightSet = new HashSet<>();

    //todo Constants
    private final static int FLIGHT_DURATION = 15;
    private final static float FLIGHT_SPEED = 1f;


    private SoarSpell() {
        super(SpellType.SOAR,
                "Fly around super fast without taking fall damage!",
                8,
                ChatColor.YELLOW,
                new ItemStack[]{new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.PHANTOM_MEMBRANE, 1)},
                2.0d,
                90,
                true, true, false,
                new SpellMonsterDrop(new EntityType[]{}, 0.00d));
    }


    //todo switch to ur name
    private static SoarSpell instance;

    public static SoarSpell getInstance() {
        if (instance == null) {
            synchronized (SoarSpell.class) {
                if (instance == null)
                    instance = new SoarSpell();
            }
        }
        return instance;
    }

    @EventHandler
    public void onWaitToRecall(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        //not casting spell
        if (!waitingToRecallMap.containsKey(player.getUniqueId()))
            return;


        int fX = event.getFrom().getBlockX();
        int fY = event.getFrom().getBlockY();
        int fZ = event.getFrom().getBlockZ();

        int tX = event.getTo().getBlockX();
        int tY = event.getTo().getBlockY();
        int tZ = event.getTo().getBlockZ();

        if (fX == tX && fY == tY && fZ == tZ)
            return;

        //Is casting a spell and moved a block

        player.sendMessage(ERROR_COLOR + "Your spell was interrupted due to movement.");
        refund(player);
        waitingToRecallMap.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWaitToRecall(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (event.isCancelled())
            return;

        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;

        if (!(entity instanceof Player))
            return;
        Player player = (Player) entity;


        //not casting spell
        if (!waitingToRecallMap.containsKey(player.getUniqueId()))
            return;


        //Is casting a spell and moved a block

        player.sendMessage(ERROR_COLOR + "Your spell was interrupted due to damage.");
        refund(player);
        waitingToRecallMap.remove(player.getUniqueId());
    }

    private void flight(Player player) {

        final int WAITING_TO_RECALL_PERIOD = 3;
        player.sendMessage(ChatColor.GOLD + "You begin casting Soar...");
        waitingToRecallMap.put(player.getUniqueId(), WAITING_TO_RECALL_PERIOD);

        new BukkitRunnable() {
            int counter = WAITING_TO_RECALL_PERIOD;

            @Override
            public void run() {


                if (!waitingToRecallMap.containsKey(player.getUniqueId())) {
                    this.cancel();
                    return;
                }


                if (counter == 0) {
                    this.cancel();
                    waitingToRecallMap.remove(player.getUniqueId());
                    if (!postCast(player))
                        if (player.isOnline())
                            refund(player);

                    return;
                }

                counter--;
                waitingToRecallMap.put(player.getUniqueId(), counter);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
    }

    private boolean postCast(Player playerSender) {


        if (!playerSender.isOnline())
            return false;

        if (isLapisNearby(playerSender.getLocation(), DEFAULT_LAPIS_NEARBY)) {
            playerSender.sendMessage(ERROR_COLOR + "You can not seem to cast " + getName() + " there...");
            return false;
        }

        playerSender.setVelocity(new Vector(0, 30, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!playerSender.isOnline())
                    return;
                playerSender.setAllowFlight(true);
                playerSender.setFlying(true);
                playerSender.setFlySpeed(FLIGHT_SPEED);
            }
        }.runTaskLater(LostShardPlugin.plugin, 10);

        playerSender.sendMessage(ChatColor.GOLD + "You soar high into the sky!");
        playerSender.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, playerSender.getLocation(), 1, 0, 0, 0);
        playerSender.getWorld().playSound(playerSender.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10.0f, 0);
        flightSet.add(playerSender.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerSender.isOnline()) {
                    playerSender.getWorld().playSound(playerSender.getLocation(), Sound.ENTITY_PHANTOM_SWOOP, 10.0f, 0);
                }
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * (FLIGHT_DURATION - 4));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerSender.isOnline()) {
                    removeFlight(playerSender);
                }
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * FLIGHT_DURATION);
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player playerSender = event.getPlayer();
        removeFlight(playerSender);

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        final Player playerSender = event.getEntity();
        removeFlight(playerSender);
    }

    @EventHandler
    public void onMoveSoarListener(PlayerMoveEvent event) {

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

        if (!flightSet.contains(event.getPlayer().getUniqueId()))
            return;
        if (!Spell.isLapisNearby(event.getTo(), Spell.getDefaultLapisNearbyValue()))
            return;
        event.getPlayer().sendMessage(ERROR_COLOR + "Something doesn't seem to let you soar free here...");
        removeFlight(event.getPlayer());

    }

    private void removeFlight(Player player) {
        if (flightSet.contains(player.getUniqueId())) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                player.setFlySpeed(FLIGHT_SPEED);
                player.setFlying(false);
                player.setAllowFlight(false);
            }
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_SWOOP, 10.0f, 0);
            flightSet.remove(player.getUniqueId());

        }
    }

    @Override
    public boolean executeSpell(Player player) {

        flight(player);
        return true;
    }


    @Override
    public void updateCooldown(Player player) {
        flightCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    flightCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                flightCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (flightCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = flightCooldownMap.get(player.getUniqueId());
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
}
