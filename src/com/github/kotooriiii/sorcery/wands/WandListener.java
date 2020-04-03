package com.github.kotooriiii.sorcery.wands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.github.kotooriiii.sorcery.wands.Wand.getWielding;
import static com.github.kotooriiii.sorcery.wands.Wand.isWielding;

public class WandListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (isWielding(player)) {
                WandType type = getWielding(player);
                Wand wand = new Wand(type);
                if (wand.hasIngredients(player)) {
                    wand.cast(player);
                }
            }
        }
    }
}

