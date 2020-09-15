package com.github.kotooriiii.tutorial.listeners;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinRealServerListener implements Listener {
    @EventHandler
    public void onListenJoin(PlayerJoinEvent event) {
        if (LostShardPlugin.isTutorial())
            return;

        Player player = event.getPlayer();
        if (!LostShardPlugin.getTutorialReader().hasCompletedTutorial(player.getUniqueId()))
            return;

        if (LostShardPlugin.getTutorialReader().isAwarded(player.getUniqueId()))
            return;

        if (LostShardPlugin.getTutorialReader().isAuthentic(player.getUniqueId())) {
            ItemStack[] rewards = new ItemStack[]
                    {
                            new ItemStack(Material.GOLD_INGOT, 10),
                            new ItemStack(Material.DIAMOND, 1)
                    };
            player.getInventory().addItem(rewards);
        }
        LostShardPlugin.getTutorialReader().award(player.getUniqueId());


    }
}
