package com.github.kotooriiii.tutorial.default_chapters.volume1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class IntroWandChapter extends AbstractChapter {
    @Override
    public void onBegin() {

        Player player = Bukkit.getPlayer(getUUID());
        if(player==null)
            return;

        sendMessage(player, "You can use wands to cast spells!");

        new BukkitRunnable() {
            @Override
            public void run() {
               setComplete();
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);

    }

    @Override
    public void onDestroy() {

    }
}


