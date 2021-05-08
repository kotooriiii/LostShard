package com.github.kotooriiii.listeners;

import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class FeatherTickleListener implements Listener {
    @EventHandler (priority =  EventPriority.LOWEST)
    public void onTickle(EntityDamageByEntityEvent event)
    {
        Entity damagerEntity = event.getDamager();
        if(!(damagerEntity instanceof Player))
            return;
        Entity defenderEntity = event.getEntity();
        if(!(defenderEntity instanceof Player))
            return;

        Player damager = (Player) damagerEntity;
        Player defender = (Player) defenderEntity;

        if(CitizensAPI.getNPCRegistry().isNPC(damager) || CitizensAPI.getNPCRegistry().isNPC(defender))
            return;

        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            return;

        ItemStack mainHand = damager.getInventory().getItemInMainHand();

        if(mainHand == null)
            return;
        if(mainHand.getType() != Material.FEATHER)
            return;

        defender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + damager.getName() + " tickles you with a feather.");
        damager.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You tickle " + damager.getName() + " with a feather.");
        event.setCancelled(true);
    }
}
