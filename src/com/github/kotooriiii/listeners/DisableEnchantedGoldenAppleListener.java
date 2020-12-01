package com.github.kotooriiii.listeners;

import io.netty.handler.codec.redis.ErrorRedisMessage;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.swing.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class DisableEnchantedGoldenAppleListener implements Listener {
    @EventHandler
    public void onGapple(PlayerInteractEvent event) {

        if (event.getItem() == null || event.getAction() == null)
            return;
        if (event.getItem().getType() == Material.ENCHANTED_GOLDEN_APPLE && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            event.getPlayer().sendMessage(ERROR_COLOR + "Enchanted golden apples are disabled.");
            event.setCancelled(true);
        }
    }
}
