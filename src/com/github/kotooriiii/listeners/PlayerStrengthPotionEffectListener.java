package com.github.kotooriiii.listeners;

import com.github.kotooriiii.events.PlayerStrengthPotionEffectEvent;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PlayerStrengthPotionEffectListener implements Listener {
    @EventHandler
    public void onStrength(PlayerStrengthPotionEffectEvent effectEvent) {
        effectEvent.setCancelled(true);
    }

    @EventHandler
    public void onDrink(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
            return;
        if (event.getItem() == null)
            return;
        if (event.getItem().getType() != Material.POTION)
            return;
        PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
        if(meta.getBasePotionData().getType() != PotionType.TURTLE_MASTER)
            return;

        event.getPlayer().sendMessage(ERROR_COLOR + "Potion has been disabled.");
        event.setCancelled(true);
    }

    @EventHandler
    public void onHarming(PotionSplashEvent event) {
        for (PotionEffect effect : event.getPotion().getEffects()) {
            if (effect.getType() == PotionEffectType.HARM) {
                if (event.getPotion().getShooter() instanceof Player) {
                    ((Player) event.getPotion().getShooter()).sendMessage(ERROR_COLOR + "Harming splash potions have been disabled. They will be enabled next patch.");
                }

                event.setCancelled(true);

            }
        }
    }

//    @EventHandler
//    public void onPotionDmg(EntityDamageEvent event)
//    {
//        event.getCause() == EntityDamageEvent.DamageCause.MAGIC
//    }

}
