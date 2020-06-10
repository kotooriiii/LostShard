//package com.github.kotooriiii.sorcery.spells.type;
//
//import com.github.kotooriiii.LostShardPlugin;
//import com.github.kotooriiii.clans.Clan;
//import com.github.kotooriiii.sorcery.spells.Spell;
//import com.github.kotooriiii.sorcery.spells.SpellType;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.potion.PotionEffect;
//import org.bukkit.potion.PotionEffectType;
//import org.bukkit.scheduler.BukkitRunnable;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.text.DecimalFormat;
//import java.util.HashMap;
//import java.util.UUID;
//
//import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
//
//public class DarknessSpell extends Spell {
//    private static HashMap<UUID, Double> darknessCooldownMap = new HashMap<UUID, Double>();
//
//    private boolean isReflexive = false;
//    private boolean isBypassingFriendlyFire = true;
//
//    public DarknessSpell() {
//
//        super(SpellType.DARKNESS,
//                ChatColor.DARK_GRAY,
//                new ItemStack[]{new ItemStack(Material.INK_SAC, 1), new ItemStack(Material.PAPER, 1)},
//                5.0f,
//                15,
//                false, false, true);
//    }
//
//    @Override
//    public boolean executeSpell(Player player) {
//        for (Entity nearbyEntity : player.getWorld().getNearbyEntities(player.getLocation(), 5, 5, 5)) {
//
//            //Only blinds players
//            if (!(nearbyEntity instanceof Player))
//                continue;
//
//            //If it is NOT REFLEXIVE (not going to do damage to itself) and this is THE SAME PLAYER move on to the next
//            if (!isReflexive && nearbyEntity.equals(player))
//                continue;
//
//
//            //Ignore itself since if case at top is in charge of this
//            if (!nearbyEntity.equals(player)) {
//                Clan clan = Clan.getClan(player.getUniqueId());
//                //Yes, same clan
//                if (clan.isInThisClan(nearbyEntity.getUniqueId())) {
//                    //Yes, friendly fire
//                    if (clan.isFriendlyFire())
//                        //Is not ignoring friendly fire
//                        if (!isBypassingFriendlyFire)
//                            continue;
//                }
//            }
//
//            ((Player) nearbyEntity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 1, true, true, true));
//        }
//        return true;
//    }
//
//    @Override
//    public void updateCooldown(Player player) {
//        darknessCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
//        // This runnable will remove the player from cooldown list after a given time
//        BukkitRunnable runnable = new BukkitRunnable() {
//            final double cooldown = getCooldown() * 20;
//            int counter = 0;
//
//            @Override
//            public void run() {
//
//                if (counter >= cooldown) {
//                    darknessCooldownMap.remove(player.getUniqueId());
//                    this.cancel();
//                    return;
//                }
//
//                counter += 1;
//                Double newCooldown = new Double(cooldown - counter);
//                darknessCooldownMap.put(player.getUniqueId(), newCooldown);
//            }
//        };
//        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
//    }
//
//    @Override
//    public boolean isCooldown(Player player) {
//        if (darknessCooldownMap.containsKey(player.getUniqueId())) {
//
//            Double cooldownTimeTicks = darknessCooldownMap.get(player.getUniqueId());
//            DecimalFormat df = new DecimalFormat("##.##");
//            double cooldownTimeSeconds = cooldownTimeTicks / 20;
//            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(0, RoundingMode.UP);
//            int value = bd.intValue();
//            if (value == 0)
//                value = 1;
//
//            String time = "seconds";
//            if (value == 1) {
//                time = "second";
//            }
//
//            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
//            return true;
//        }
//        return false;
//    }
//
//}
