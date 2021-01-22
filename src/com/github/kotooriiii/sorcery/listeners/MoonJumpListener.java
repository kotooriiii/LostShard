package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.sorcery.spells.type.circle3.MoonJumpSpell;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class MoonJumpListener implements Listener {
    @EventHandler
    public void onFallDamage(EntityDamageEvent event)
    {
        final Entity entity = event.getEntity();
        if(!(entity instanceof Player))
            return;
        if(CitizensAPI.getNPCRegistry().isNPC(entity))
            return;
        if(event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;
        if(!((Player) entity).hasPotionEffect(PotionEffectType.JUMP))
            return;
        if(!MoonJumpSpell.getInstance().getJumpers().contains(entity.getUniqueId()))
            return;
        event.setCancelled(true);
    }
}
