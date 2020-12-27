package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.HashSet;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class MountListener implements Listener {

    private static final HashSet<UUID> set = new HashSet<>();

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        final Entity dismounted = event.getDismounted();
        final Entity entity = event.getEntity();

        if(dismounted == null || entity == null)
            return;
        if(entity.getType() != EntityType.PLAYER)
            return;

//        Bukkit.broadcastMessage(dismounted.getLocation().toString());
//        Bukkit.broadcastMessage(entity.getLocation().toString());
    }

    @EventHandler
    public void onMount(EntityMountEvent event)
    {
        final Entity dismounted = event.getMount();
        final Entity entity = event.getEntity();

        if(dismounted == null || entity == null)
            return;
        if(entity.getType() != EntityType.PLAYER)
            return;
        Player player = (Player) entity;
        if(set.contains(player.getUniqueId()))
        {
            player.sendMessage(ERROR_COLOR + "You are trying to mount too fast!");
            return;
        }

        set.add(player.getUniqueId());

        final UUID uuid = player.getUniqueId();

        new BukkitRunnable() {
            @Override
            public void run() {
                set.remove(uuid);
            }
        }.runTaskLater(LostShardPlugin.plugin, 40);

    }
}
