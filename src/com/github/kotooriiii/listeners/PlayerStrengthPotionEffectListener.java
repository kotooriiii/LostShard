package com.github.kotooriiii.listeners;

import com.github.kotooriiii.events.PlayerStrengthPotionEffectEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

public class PlayerStrengthPotionEffectListener implements Listener {
    @EventHandler
    public void onStrength(PlayerStrengthPotionEffectEvent effectEvent)
    {
        effectEvent.setCancelled(true);
    }
}
