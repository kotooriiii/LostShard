package com.github.kotooriiii.tutorial.listeners;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class PlayerJoinRealServerListener implements Listener {
    @EventHandler
    public void onListenJoin(PlayerJoinEvent event) {
        if (LostShardPlugin.isTutorial())
            return;
        final Player player = event.getPlayer();
        verify(player);
    }

    public static void verify(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return;
        verify(player);
    }


    public static void verify(Player player) {
        if (!player.isOnline())
            return;

        if (!LostShardPlugin.getTutorialReader().hasCompletedTutorial(player.getUniqueId()))
            return;

        if (LostShardPlugin.getTutorialReader().isAwarded(player.getUniqueId())) {
            return;
        }

        if (LostShardPlugin.getTutorialReader().isAuthentic(player.getUniqueId())) {
            ItemStack[] rewards = new ItemStack[]
                    {
                            new ItemStack(Material.DIAMOND, 1),
                            new ItemStack(Material.GOLD_INGOT, 10)
                    };
            player.getInventory().addItem(rewards);
            player.sendMessage(ChatColor.GOLD + "Your rewards for completing the tutorial are in your inventory.");
        }
        LostShardPlugin.getTutorialReader().award(player.getUniqueId());
    }

}
