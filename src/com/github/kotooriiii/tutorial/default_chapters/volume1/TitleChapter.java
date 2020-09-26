package com.github.kotooriiii.tutorial.default_chapters.volume1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TitleChapter extends AbstractChapter {

    @Override
    public void onBegin() {

        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        final int fadeIn = 10;
        final int stay = 60;
        final int fadeOut = 10;

        player.sendTitle(ChatColor.DARK_PURPLE + "Welcome to LostShard!", ChatColor.DARK_PURPLE + "Please complete the tutorial", fadeIn, stay, fadeOut);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle(ChatColor.DARK_PURPLE + "", ChatColor.DARK_PURPLE + "You may skip the tutorial with '/skip' at any time, however...", fadeIn, stay, fadeOut);
                this.cancel();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendTitle(ChatColor.DARK_PURPLE + "", ChatColor.DARK_PURPLE + "Completing the tutorial rewards you.", fadeIn, stay, fadeOut);
                        this.cancel();
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                setComplete();
                                this.cancel();
                                return;
                            }
                        }.runTaskLater(LostShardPlugin.plugin, fadeIn + stay + fadeOut);
                    }
                }.runTaskLater(LostShardPlugin.plugin, fadeIn + stay + fadeOut);
            }
        }.runTaskLater(LostShardPlugin.plugin, fadeIn + stay + fadeOut);
    }

    @Override
    public void onDestroy() {
        //No clean up needed :)
    }
}