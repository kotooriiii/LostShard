package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class LightningSpell extends Spell {

    private static HashMap<UUID, Double> lightningSpellCooldownMap = new HashMap<UUID, Double>();

    public LightningSpell()
    {
        super(SpellType.LIGHTNING,
                ChatColor.GOLD,
                new ItemStack[]{new ItemStack(Material.GUNPOWDER, 1), new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1)},
                2.0f,
                20);

    }

    @Override
    public boolean executeSpell(Player player) {
        final int lightningRange = 50;
        List<Block> lineOfSightLightning = player.getLineOfSight(null, lightningRange);

        // Get target block (last block in line of sight)
        Location lightningLocation = lineOfSightLightning.get(lineOfSightLightning.size() - 1).getLocation();
        player.getWorld().strikeLightning(lightningLocation);
        callPlayerEvent(player, lightningLocation);
        return true;
    }

    @Override
    public void cast(Player player) {

    }

    public boolean isCooldown(Player player) {
        if (lightningSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = lightningSpellCooldownMap.get(player.getUniqueId());
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


    private void callPlayerEvent(Player attacker, Location location) {
        for (Player defender : Bukkit.getOnlinePlayers()) {
            if (defender.getLocation().getBlock().equals(location.getBlock()))
                Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(attacker
                        , defender, EntityDamageEvent.DamageCause.CUSTOM, 7));
        }
    }
}
