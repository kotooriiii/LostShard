//package com.github.kotooriiii.sorcery.spells.type;
//
//import com.github.kotooriiii.LostShardPlugin;
//import com.github.kotooriiii.clans.Clan;
//import com.github.kotooriiii.npc.type.clone.CloneNPC;
//import com.github.kotooriiii.sorcery.scrolls.Scroll;
//import com.github.kotooriiii.sorcery.spells.Spell;
//import com.github.kotooriiii.sorcery.spells.SpellType;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerMoveEvent;
//import org.bukkit.event.player.PlayerToggleSneakEvent;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.potion.PotionEffect;
//import org.bukkit.potion.PotionEffectType;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.util.Vector;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.text.DecimalFormat;
//import java.util.HashMap;
//import java.util.UUID;
//
//import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
//
//public class CloneSpell extends Spell implements Listener {
//
//    private static HashMap<UUID, Double> cloneCooldownMap = new HashMap<UUID, Double>();
//
//    public CloneSpell() {
//
//        super(SpellType.CLONE,
//                ChatColor.GRAY,
//                new ItemStack[]{},
//                30.0f,
//                15,
//                false, false, true);
//    }
//
////    //todo remove this
////    @EventHandler
////    public void onCopyMovement(PlayerToggleSneakEvent event) {
////        event.getPlayer().getInventory().addItem(new Scroll(new CloneSpell()).createItem());
////    }
//
//
//
//    @Override
//    public boolean executeSpell(Player player) {
//        CloneNPC cloneNPC = new CloneNPC(player, 3, 0);
//        cloneNPC.spawn(new Location(player.getLocation().getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
//        return true;
//    }
//
//    @Override
//    public void updateCooldown(Player player) {
//        cloneCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
//        // This runnable will remove the player from cooldown list after a given time
//        BukkitRunnable runnable = new BukkitRunnable() {
//            final double cooldown = getCooldown() * 20;
//            int counter = 0;
//
//            @Override
//            public void run() {
//
//                if (counter >= cooldown) {
//                    cloneCooldownMap.remove(player.getUniqueId());
//                    this.cancel();
//                    return;
//                }
//
//                counter += 1;
//                Double newCooldown = new Double(cooldown - counter);
//                cloneCooldownMap.put(player.getUniqueId(), newCooldown);
//            }
//        };
//        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
//    }
//
//    @Override
//    public boolean isCooldown(Player player) {
//        if (cloneCooldownMap.containsKey(player.getUniqueId())) {
//
//            Double cooldownTimeTicks = cloneCooldownMap.get(player.getUniqueId());
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
//}
