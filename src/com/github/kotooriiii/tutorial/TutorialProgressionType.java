package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.function.Function;

public enum TutorialProgressionType {


    INTRODUCTION_TITLE(new Function<Player, Boolean>() {
        @Override
        public Boolean apply(Player player) {

            int fadeIn = 10;
            int stay = 40;
            int fadeOut = 10;

            player.sendTitle(ChatColor.DARK_PURPLE + "Welcome to LostShard!", ChatColor.DARK_PURPLE + "Please complete the tutorial", fadeIn, stay, fadeOut);
            player.getInventory().setItem(9, new ItemStack(Material.FEATHER, 64));

            new BukkitRunnable() {
                @Override
                public void run() {
                    TutorialHelper.next(player);
                    this.cancel();
                    return;
                }
            }.runTaskLater(LostShardPlugin.plugin, fadeIn + stay + fadeOut);


            return true;
        }
    }),

    WANDS_CAST_SPELLS(new Function<Player, Boolean>() {
        @Override
        public Boolean apply(Player player) {

            TutorialHelper.sendMessage(player, "You can use wands to cast spells!");

            new BukkitRunnable() {
                @Override
                public void run() {
                    TutorialHelper.next(player);
                    this.cancel();
                    return;
                }
            }.runTaskLater(LostShardPlugin.plugin, TutorialHelper.DELAY_TICK);
            return true;
        }
    }),

    GRAB_STICK_FROM_CHEST(new Function<Player, Boolean>() {
        @Override
        public Boolean apply(Player player) {
            TutorialHelper.sendMessage(player, "Grab a stick from the chest.");
            return true;
        }
    }),

    HOLD_AND_BIND_TELEPORT(new Function<Player, Boolean>() {
        @Override
        public Boolean apply(Player player) {
            TutorialHelper.sendMessage(player, "Hold the stick in your hand. Type /bind teleport");
            return true;
        }
    });



    private Function function;

    private TutorialProgressionType(Function function) {
        this.function = function;
    }

    public Function<Player, Boolean> getFunction() {
        return function;
    }


}
