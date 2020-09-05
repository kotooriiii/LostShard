package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SkillTitleChapter extends AbstractChapter {

    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if(player==null)
            return;

        final int fadeIn = 10;
        final int stay = 40;
        final int fadeOut = 10;

        player.sendTitle(ChatColor.DARK_AQUA + "MCMMO", "", fadeIn, stay, fadeOut);

        new BukkitRunnable() {
            @Override
            public void run() {
                sendMessage(player, "LostShard features a variety of fully custom McMMO skills.\nThe Survivalism skill allows you to track mobs or players.\nTo track a mob or player, type: /track (name)");
                setComplete();
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, fadeIn + stay + fadeOut);
    }

    @Override
    public void onDestroy() {

    }
}
