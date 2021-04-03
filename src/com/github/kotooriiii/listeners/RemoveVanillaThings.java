package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class RemoveVanillaThings implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPhantomSpawn(EntitySpawnEvent event) {

        if (LostShardPlugin.isTutorial())
            return;
        Entity entity = event.getEntity();
        if (CitizensAPI.getNPCRegistry().isNPC(entity))
            return;
        if (!(entity instanceof Phantom))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void craftItem(PrepareItemCraftEvent e) {
        if(e.getRecipe() == null)
            return;

        Material itemType = e.getRecipe().getResult().getType();

        if(itemType == Material.BEACON)
        {
            e.getInventory().setResult(new ItemStack(Material.AIR));
            for(HumanEntity he:e.getViewers()) {
                if(he instanceof Player) {
                    ((Player)he).sendMessage(ChatColor.RED+ "Beacons are not craftable. They are obtainable by other means, however!");
                }
            }
        }
    }


}
