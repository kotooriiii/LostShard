package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.type.circle4.ScreechSpell;
import com.github.kotooriiii.sorcery.spells.type.circle7.RadiateSpell;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class MonsterTargetListener implements Listener {
    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event)
    {
        LivingEntity target = event.getTarget();

        if(target == null)
            return;

        if(target.getType() != EntityType.PLAYER)
            return;

        if(RadiateSpell.getInstance().isScreeching(target.getUniqueId()) || ScreechSpell.getInstance().isScreeching(target.getUniqueId()))
        {
            event.setCancelled(true);
            return;
        }

        for(Entity entity : target.getWorld().getNearbyEntities(target.getLocation(), ScreechSpell.getInstance().SCREECH_DISTANCE, ScreechSpell.getInstance().SCREECH_DISTANCE, ScreechSpell.getInstance().SCREECH_DISTANCE))
        {
            if(entity.getType() == EntityType.PLAYER && ScreechSpell.getInstance().isScreeching(entity.getUniqueId()))
            {
                event.setCancelled(true);
                return;
            }
        }

        for(Entity entity : target.getWorld().getNearbyEntities(target.getLocation(), RadiateSpell.RADIATE_SCREECH_DISTANCE, RadiateSpell.RADIATE_SCREECH_DISTANCE, RadiateSpell.getInstance().RADIATE_SCREECH_DISTANCE))
        {
            if(entity.getType() == EntityType.PLAYER && RadiateSpell.getInstance().isScreeching(entity.getUniqueId()))
            {
                event.setCancelled(true);
                return;
            }
        }

//        ScreechSpell.get
    }
}
