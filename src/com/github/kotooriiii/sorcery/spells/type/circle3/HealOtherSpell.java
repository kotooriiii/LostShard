package com.github.kotooriiii.sorcery.spells.type.circle3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class HealOtherSpell extends Spell {

    private static HashMap<UUID, Double> healSpellCooldownMap = new HashMap<UUID, Double>();
    private final static int HALF_HEARTS_HEALED = 8;


    private HealOtherSpell()
    {
        super(SpellType.HEAL_OTHER,
                "Heals your closest clan member for a total of " + new BigDecimal(HALF_HEARTS_HEALED/2).setScale(1,RoundingMode.UNNECESSARY)
                        + " hearts.",
                4,
            ChatColor.GREEN,
                new ItemStack[]{new ItemStack(Material.STRING, 1), new ItemStack(Material.WHEAT_SEEDS, 1)},
                2d,
                15,
                true, true, false);
    }

    private  static HealOtherSpell instance;
    public static HealOtherSpell getInstance() {
        if (instance == null) {
            synchronized (HealOtherSpell.class) {
                if (instance == null)
                    instance = new HealOtherSpell();
            }
        }
        return instance;
    }


    @Override
    public boolean executeSpell(Player player) {


        Clan clan = LostShardPlugin.getClanManager().getClan(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(ERROR_COLOR + "Invalid target. You must be in a clan to perform this spell.");
            return false;
        }

        double distance = -1f;
        Player clanPlayer = null;
        for (Player iplayer : clan.getOnlinePlayers()) {
            if (!iplayer.getWorld().equals(player.getWorld()))
                continue;
            if (iplayer.equals(player))
                continue;

            double tempDistance = iplayer.getLocation().distance(player.getLocation());
            if (tempDistance > distance) {
                distance = tempDistance;
                clanPlayer = iplayer;
            }
        }

        if (distance == -1 || distance > 15.0f) {
            player.sendMessage(ERROR_COLOR + "Invalid target. There are no clan mates around you.");
            return false;
        }

        if (isLapisNearby(clanPlayer.getLocation(), DEFAULT_LAPIS_NEARBY))
        {
            player.sendMessage(ERROR_COLOR + "You can't seem to heal your ally...");
            return false;
        }


        double health = clanPlayer.getHealth();
        health += HALF_HEARTS_HEALED;
        if (health > clanPlayer.getMaxHealth())
            health = clanPlayer.getMaxHealth();
        clanPlayer.setHealth(health);

        clanPlayer.sendMessage(STANDARD_COLOR + "" + StatusPlayer.wrap(player.getUniqueId()).getStatus().getChatColor() + player.getName() + " heals you.");
        player.sendMessage(STANDARD_COLOR + "You healed " + StatusPlayer.wrap(clanPlayer.getUniqueId()).getStatus().getChatColor() + player.getName() + STANDARD_COLOR + ".");


        return true;
    }

    @Override
    public void updateCooldown(Player player)
    {
        healSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    healSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                healSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (healSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = healSpellCooldownMap.get(player.getUniqueId());
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
