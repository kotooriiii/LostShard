package com.github.kotooriiii.sorcery.spells.type.circle7;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellToggleable;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.platforms;

public class SilentWalkSpell extends Spell implements SpellToggleable {

    private static HashMap<UUID, Double> silentWalkCooldownMap = new HashMap<UUID, Double>();

    private static final String ID = SpellType.SILENT_WALK.name();

    private SilentWalkSpell() {
        super(SpellType.SILENT_WALK,
                "Silences your footsteps for as long as it is toggled, or until you run out of mana. Great for sneaking around.",
                7,
                ChatColor.BLACK,
                new ItemStack[]{new ItemStack(Material.WHITE_WOOL, 1), new ItemStack(Material.LAPIS_BLOCK, 1), new ItemStack(Material.GOLD_NUGGET, 1)},
                2.0d,
                5,
                true, true, false);


    }

    private static SilentWalkSpell instance;

    public static SilentWalkSpell getInstance() {
        if (instance == null) {
            synchronized (SilentWalkSpell.class) {
                if (instance == null)
                    instance = new SilentWalkSpell();
            }
        }
        return instance;
    }


    @Override
    public boolean executeSpell(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false, false));
        ShardScoreboardManager.add(player, getID());
        return true;
    }

    @Override
    public double getManaCostPerSecond() {
        return 1.3f;
    }

    @Override
    public void manaDrainExecuteSpell(UUID uuid) {

    }

    @Override
    public void stopManaDrain(UUID uuid) {
        Bukkit.getPlayer(uuid).removePotionEffect(PotionEffectType.SPEED);
        ShardScoreboardManager.add(Bukkit.getPlayer(uuid), StatusPlayer.wrap(uuid).getStatus().getName());
    }


    @Override
    public void updateCooldown(Player player) {
        silentWalkCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    silentWalkCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                silentWalkCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (silentWalkCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = silentWalkCooldownMap.get(player.getUniqueId());
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

    public static String getID() {
        return ID;
    }
}
