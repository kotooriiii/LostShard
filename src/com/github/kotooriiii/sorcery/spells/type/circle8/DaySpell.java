package com.github.kotooriiii.sorcery.spells.type.circle8;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellChanneleable;
import com.github.kotooriiii.sorcery.spells.SpellToggleable;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class DaySpell extends SpellChanneleable {

    //Cooldown map
    private final static HashMap<UUID, Double> daySpellCooldownMap = new HashMap<UUID, Double>();

    private final static HashMap<UUID, HashMap<HashSet<UUID>, BukkitTask>> castMap= new HashMap<>();
    private final static float CAST_TIME = 2;

    private DaySpell() {
        super(SpellType.DAY, 2, 30, -1,
                "Changes the time from night to day. Must be channeled, meaning two people must cast this spell at the same time for it to work.",
                8,
                ChatColor.YELLOW,
                new ItemStack[]{new ItemStack(Material.GLOWSTONE, 1), new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.OBSIDIAN)},
                2.0d,
                50,
                true, true, false);


    }


    //todo switch to ur name
    private  static DaySpell instance;
    public static DaySpell getInstance() {
        if (instance == null) {
            synchronized (DaySpell.class) {
                if (instance == null)
                    instance = new DaySpell();
            }
        }
        return instance;
    }


    @Override
    public boolean executeSpell(Player player) {

        player.sendMessage(ChatColor.GOLD + "You begin to channel Day...");
        return true;
    }

    @Override
    public void executeSuccessfulChannelSpell(Player player, UUID... values) {
        UUID uuid = player.getUniqueId();

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                player.getWorld().setTime(0);
                displayMessage(values);
                castMap.remove(uuid);
            }
        }.runTaskLater(LostShardPlugin.plugin, (long) (20 * CAST_TIME));

        removeMembers(player.getLocation());
        ArrayList<UUID> uuids = new ArrayList<>();
        for(UUID iterating_uuid : values)
            uuids.add(iterating_uuid);
        HashMap<HashSet<UUID>, BukkitTask> map = new HashMap<>();
        map.put(new HashSet<UUID>(uuids), task);
        castMap.put(uuid, map);
    }

    @Override
    public void executeFailedChannelSpell(Player player, UUID... values) {
    }

    private void displayMessage(UUID... values)
    {
        for(UUID uuid : values) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                player.sendMessage(ChatColor.GOLD + "The sun shines.");
        }
    }

    private void remove()
    {

    }

    @Override
    public void updateCooldown(Player player) {
        daySpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    daySpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                daySpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (daySpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = daySpellCooldownMap.get(player.getUniqueId());
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
