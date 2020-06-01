package com.github.kotooriiii.sorcery.scrolls;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ScrollListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (Scroll.isWielding(player)) {
                Scroll scroll = Scroll.getWielding(player);
                if (scroll == null)
                    return;
                scroll.cast(player);
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                event.setCancelled(true);
            }
        }
    }
}

