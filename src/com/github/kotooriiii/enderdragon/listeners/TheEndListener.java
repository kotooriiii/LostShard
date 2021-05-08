package com.github.kotooriiii.enderdragon.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.enderdragon.EnderDragonManager;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.CitizensAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class TheEndListener implements Listener {


    /**
     * No explosive damage to phantom
     * @param event
     */
    @EventHandler
    public void onPhantomDamageExplosion(EntityDamageEvent event)
    {
        if(event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
        {
            if(event.getEntity().getType() == EntityType.PHANTOM || event.getEntity().getType() == EntityType.WITHER || event.getEntity().getType() == EntityType.ENDER_DRAGON)
            {
                event.setCancelled(true);
            }
        }
    }

    /**
     * No damage to phantom or wither from dragon
     * @param event
     */
    @EventHandler
    public void onEnderDragonAtk(EntityDamageByEntityEvent event)
    {
        if(event.getDamager().getType() == EntityType.ENDER_DRAGON)
        {
            if(event.getEntity().getType() == EntityType.PHANTOM || event.getEntity().getType() == EntityType.WITHER || event.getEntity().getType() == EntityType.ENDER_DRAGON)
            {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Safely spawns the dragon on world change
     *
     * @param event
     */
    @EventHandler
    public void changeToEnd(PlayerChangedWorldEvent event) {
        switch (event.getPlayer().getWorld().getEnvironment()) {
            case THE_END:
                if (!LostShardPlugin.getEnderDragonManager().getCooldownStrategy().isCooldown() && !LostShardPlugin.getEnderDragonManager().getCooldownStrategy().isAlive()) {
                    LostShardPlugin.getEnderDragonManager().getSpawnStrategy().spawnDragonSafely();
                    return;
                }
        }
    }

    /**
     * Safely spawns the dragon on join
     *
     * @param event
     */
    @EventHandler
    public void changeToEnd(PlayerJoinEvent event) {
        switch (event.getPlayer().getWorld().getEnvironment()) {
            case THE_END:
                if (!LostShardPlugin.getEnderDragonManager().getCooldownStrategy().isCooldown() && !LostShardPlugin.getEnderDragonManager().getCooldownStrategy().isAlive()) {
                    LostShardPlugin.getEnderDragonManager().getSpawnStrategy().spawnDragonSafely();
                    return;
                }
        }
    }

    /**
     * Remove the ability to use end gateway
     *
     * @param event
     */
    @EventHandler
    public void onEndGateWayTP(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            event.setTo(new Location(event.getTo().getWorld(), 0, 256, 0));
        }
    }

    /**
     * Called when Ender Dragon is summoned
     * @param event
     */
    @EventHandler
    public void onSpawnDragon(EntitySpawnEvent event) {
        if (LostShardPlugin.getEnderDragonManager().getCooldownStrategy().isCooldown())
            return;
        if (LostShardPlugin.getEnderDragonManager().getCooldownStrategy().isAlive())
            return;
        if (event.getEntity().getWorld().getEnvironment() != World.Environment.THE_END)
            return;
        if (event.getEntity().getType() != EntityType.ENDER_DRAGON)
            return;

        final TextComponent textComponent = new TextComponent("The Ender Dragon has been summoned.");
        textComponent.setColor(ChatColor.RED);
        Bukkit.broadcast(textComponent);
    }

    /**
     * Handles dragon death
     * @param event
     */
    @EventHandler
    public void onDragonKill(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.ENDER_DRAGON)
            return;
        if (event.getEntity().getWorld().getEnvironment() != World.Environment.THE_END)
            return;
        if (LostShardPlugin.getEnderDragonManager().getCooldownStrategy().isCooldown())
            return;
        if (!LostShardPlugin.getEnderDragonManager().getCooldownStrategy().isAlive())
            return;

        //Guarantees an egg
        boolean hasDragonEgg = false;
        for (ItemStack itemStack : event.getDrops()) {
            if (itemStack.getType() == Material.DRAGON_EGG) {
                hasDragonEgg = true;
                break;
            }
        }

        if (!hasDragonEgg) {
            event.getDrops().add(new ItemStack(Material.DRAGON_EGG, 1));
        }

        //CLEAR ALL DROPS SINCE WE ARE CHANGING IT. NOW IT GOES TO PLAYER INVENTORY

        final EnderDragonManager enderDragonManager = LostShardPlugin.getEnderDragonManager();


        Player killer = event.getEntity().getKiller();
        UUID lastKillerUUID = null;
        if (killer != null) {
            final Player playerDamagerONLY = HelperMethods.getPlayerDamagerONLY(event.getEntity(), killer);
           lastKillerUUID = playerDamagerONLY == null ? null : playerDamagerONLY.getUniqueId();

           if(playerDamagerONLY != null)
           {
               event.getDrops().clear();
               HashMap<Integer, ItemStack> map = playerDamagerONLY.getInventory().addItem(new ItemStack(Material.DRAGON_EGG, 1));
               if(!map.isEmpty())
               {
                   playerDamagerONLY.sendMessage(ERROR_COLOR + "Your inventory is full. The Dragon Egg has dropped around you.");
                   playerDamagerONLY.getWorld().dropItemNaturally(playerDamagerONLY.getLocation(), new ItemStack(Material.DRAGON_EGG, 1));
               }
               else
               {
                   playerDamagerONLY.sendMessage(ChatColor.RED + "You found a Dragon Egg!");

               }
           }
        }


        final TextComponent textComponent = new TextComponent("The Ender Dragon was slain.");
        textComponent.setColor(ChatColor.RED);
        Bukkit.broadcast(textComponent);

        LostShardPlugin.getEnderDragonManager().getBossBar().setVisible(false);
        enderDragonManager.getCooldownStrategy().killedDragon(lastKillerUUID);
        enderDragonManager.getSpawnStrategy().setLSEnderDragon(null);

    }
}
