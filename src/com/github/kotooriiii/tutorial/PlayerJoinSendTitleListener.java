package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinSendTitleListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(LostShardPlugin.getTutorialManager().wrap(player.getUniqueId()) != null)
        {
            //tp to spawn
            player.teleport(event.getPlayer().getLocation().getWorld().getSpawnLocation());
        }

       TutorialProgression tp = LostShardPlugin.getTutorialManager().addPlayer(player.getUniqueId());
       tp.nextProgression(player.getLocation());



    }

}
