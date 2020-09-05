package com.github.kotooriiii.tutorial.default_chapters.volume1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TitleChapter extends AbstractChapter {

    @Override
    public void onBegin() {

       Player player = Bukkit.getPlayer(getUUID());
       if(player==null)
           return;

        final int fadeIn = 10;
        final int stay = 40;
        final int fadeOut = 10;

        player.sendTitle(ChatColor.DARK_PURPLE + "Welcome to LostShard!", ChatColor.DARK_PURPLE + "Please complete the tutorial", fadeIn, stay, fadeOut);
        player.getInventory().setItem(9, new ItemStack(Material.FEATHER, 64));

        new BukkitRunnable() {
            @Override
            public void run() {
                setComplete();
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, fadeIn + stay + fadeOut);

    }

    @Override
    public void onDestroy() {
        //No clean up needed :)
    }
}