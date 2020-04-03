package com.github.kotooriiii.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;


public class GoldArmorListener implements Listener {
    @EventHandler
    public void onDrowning(EntityDamageEvent entityDamageEvent) {
        Entity entity = entityDamageEvent.getEntity();
        DamageCause damageCause = entityDamageEvent.getCause();

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
