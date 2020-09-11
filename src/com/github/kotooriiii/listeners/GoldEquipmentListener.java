package com.github.kotooriiii.listeners;

import com.github.kotooriiii.plots.struct.PlayerPlot;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;


public class GoldEquipmentListener implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        Entity damager = event.getDamager();

        if(CitizensAPI.getNPCRegistry().isNPC(event.getEntity()) || CitizensAPI.getNPCRegistry().isNPC(damager))
            return;


        if (!(damager instanceof Player))
            return;

        if(!(event.getEntity() instanceof LivingEntity))
            return;

        Player player = (Player) damager;
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if(mainHand == null)
            return;

        if(!mainHand.getType().equals(Material.GOLDEN_SWORD))
            return;

        //Player damaging an entity with a gold sword
        final int damage = 5;

        event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());
        for(Entity entity : player.getLocation().getWorld().getNearbyEntities(player.getLocation(), 1, 1, 1))
        {
            if(!(entity instanceof LivingEntity))
                continue;
            if(player.equals(entity))
                continue;

            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.damage(damage);
        }
    }

    @EventHandler
    public void onDrowning(EntityDamageEvent entityDamageEvent) {
        Entity entity = entityDamageEvent.getEntity();
        DamageCause damageCause = entityDamageEvent.getCause();

        if(CitizensAPI.getNPCRegistry().isNPC(entityDamageEvent.getEntity()))
            return;

        //must be player
        if (!(entity instanceof Player))
            return;
        //is drowning
        if (!damageCause.equals(DamageCause.DROWNING))
            return;

        Player player = (Player) entity;
        ItemStack helmet = player.getInventory().getHelmet();
        //helmet must be gold
        if (helmet == null || helmet.getType() != Material.GOLDEN_HELMET)
            return;

        //Code if anything else wants to be added
        entityDamageEvent.setCancelled(true);

    }

    @EventHandler
    public void onSuffocate(EntityDamageEvent entityDamageEvent) {
        Entity entity = entityDamageEvent.getEntity();
        DamageCause damageCause = entityDamageEvent.getCause();
        if(CitizensAPI.getNPCRegistry().isNPC(entityDamageEvent.getEntity()))
            return;

        //must be player
        if (!(entity instanceof Player))
            return;
        //is drowning
        if (!damageCause.equals(DamageCause.SUFFOCATION))
            return;

        Player player = (Player) entity;
        ItemStack chestplate = player.getInventory().getChestplate();
        //helmet must be gold
        if (chestplate == null || chestplate.getType() != Material.GOLDEN_CHESTPLATE)
            return;

        //Code if anything else wants to be added
        entityDamageEvent.setCancelled(true);

    }

    @EventHandler
    public void onFire(EntityDamageEvent entityDamageEvent) {
        Entity entity = entityDamageEvent.getEntity();
        DamageCause damageCause = entityDamageEvent.getCause();

        if(CitizensAPI.getNPCRegistry().isNPC(entityDamageEvent.getEntity()))
            return;


        //must be player
        if (!(entity instanceof Player))
            return;
        //is drowning
        if (!damageCause.equals(DamageCause.FIRE) && !damageCause.equals(DamageCause.HOT_FLOOR) && !damageCause.equals(DamageCause.LAVA) && !damageCause.equals(DamageCause.FIRE_TICK))
            return;

        Player player = (Player) entity;
        ItemStack leggings = player.getInventory().getLeggings();
        //helmet must be gold
        if (leggings == null || leggings.getType() != Material.GOLDEN_LEGGINGS)
            return;

        //Code if anything else wants to be added
        entityDamageEvent.setCancelled(true);
    }

    @EventHandler
    public void onFall(EntityDamageEvent entityDamageEvent) {
        Entity entity = entityDamageEvent.getEntity();
        DamageCause damageCause = entityDamageEvent.getCause();

        if(CitizensAPI.getNPCRegistry().isNPC(entityDamageEvent.getEntity()))
            return;


        //must be player
        if (!(entity instanceof Player))
            return;
        //is drowning
        if (!damageCause.equals(DamageCause.FALL))
            return;

        Player player = (Player) entity;
        ItemStack boots = player.getInventory().getBoots();
        //helmet must be gold
        if (boots == null || boots.getType() != Material.GOLDEN_BOOTS)
            return;

        //Code if anything else wants to be added
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BIG_FALL, 10, 0);
        entityDamageEvent.setCancelled(true);
    }
}
