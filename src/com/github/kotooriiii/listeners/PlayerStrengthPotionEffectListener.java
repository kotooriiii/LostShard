package com.github.kotooriiii.listeners;

import com.github.kotooriiii.events.PlayerStrengthPotionEffectEvent;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PlayerStrengthPotionEffectListener implements Listener {
    @EventHandler
    public void onStrength(PlayerStrengthPotionEffectEvent effectEvent)
    {
        effectEvent.setCancelled(true);
    }

    @EventHandler
    public void onHarming(PotionSplashEvent event)
    {
        for(PotionEffect effect : event.getPotion().getEffects())
        {
            if(effect.getType() == PotionEffectType.HARM)
            {
                if(event.getPotion().getShooter() instanceof Player)
                {
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
