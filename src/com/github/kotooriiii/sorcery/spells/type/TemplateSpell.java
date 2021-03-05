package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.skill_listeners.TamingListener;
import com.github.kotooriiii.sorcery.spells.KVectorUtils;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellToggleable;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class TemplateSpell extends Spell implements SpellToggleable {

    //todo Cooldown map
    private static HashMap<UUID, Double> templateCooldownMap = new HashMap<UUID, Double>();

    //todo Constants
    private final static int TEMPLATE_DISTANCE = 15;


    private TemplateSpell() {
        super(SpellType.GRASS,
                "Template",
                1,
                ChatColor.YELLOW,
                new ItemStack[]{new ItemStack(Material.GLOWSTONE, 1), new ItemStack(Material.REDSTONE, 1)},
                2.0d,
                5,
                true, true, false, new SpellMonsterDrop(new EntityType[]{}, 0.00d));


    }


    //todo switch to ur name
    private  static TemplateSpell instance;
    public static TemplateSpell getInstance() {
        if (instance == null) {
            synchronized (TemplateSpell.class) {
                if (instance == null)
                    instance = new TemplateSpell();
            }
        }
        return instance;
    }


    @Override
    public boolean executeSpell(Player player) {


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

    }



    @Override
    public void updateCooldown(Player player) {
        templateCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    templateCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                templateCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (templateCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = templateCooldownMap.get(player.getUniqueId());
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
