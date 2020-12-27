package com.github.kotooriiii.tutorial.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.PlotBanner;
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
                    };
            LostShardPlugin.getBankManager().wrap(player.getUniqueId()).addCurrency(10);
            player.getInventory().addItem(rewards);
            player.sendMessage(ChatColor.GOLD + "Great job on completing the tutorial! You have been rewarded 10 free gold in your balance. Go out and start your journey!");
        }

        player.getInventory().addItem(PlotBanner.getInstance().getItem());
        player.getInventory().addItem(new ItemStack(Material.FEATHER, 32));
        player.getInventory().addItem(new ItemStack(Material.REDSTONE, 32));
        player.getInventory().addItem(new ItemStack(Material.MELON_SLICE, 16));
//todo plot banner
        LostShardPlugin.getTutorialReader().award(player.getUniqueId());
    }

}
