package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.events.TutorialPlayerDeathEvent;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ZombieChapter extends AbstractChapter {

    private int counter;
    private List<Zombie> entityList;

    public ZombieChapter() {
        this.counter = 0;
        this.entityList = new ArrayList<>();
        //todo zone
    }

    @Override
    public void onBegin() {

        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                spawnZombies(player);
                sendMessage(player, "Some zombies... these are always worth killing.");

            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);

    }

    private void spawnZombies(Player player) {
        final int x =?,y =?,z ?;

        player.getWorld().createExplosion(x, y, z, 6f, false, false);

        for (int i = 0; i < 4; i++) {
            //4
            //8
            //9
            //0-8
            //-4-4;
            int random = new Random().nextInt((4 * 2) + 1) - 4; //0-3
            Location location = new Location(player.getWorld(), x + random, y, z + random);
            entityList.add((Zombie) player.getWorld().spawnEntity(location, EntityType.ZOMBIE));
        }
    }

    @Override
    public void onDestroy() {
    }



    @EventHandler
    public void entDmg(EntityDamageByEntityEvent event) {
        if (!isActive())
            return;
        if (!event.getDamager().getUniqueId().equals(getUUID()) && entityList.contains(event.getEntity())) {
            event.setCancelled(true);
            return;
        }

        //good to go

    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (!entityList.contains(event.getEntity()))
            return;
        if(event.getTarget().getUniqueId().equals(getUUID()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();

        if (!(livingEntity instanceof Zombie))
            return;

        Zombie zombie = (Zombie) livingEntity;

        if (!event.getEntity().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!entityList.contains(zombie))
            return;
        counter++;

        if (counter == 4) {
            sendMessage(Bukkit.getPlayer(getUUID()), "Zombies drop rotten flesh which is instantly eatable like melons.\nThey also drop feathers, which is a useful ingredient in casting spells.\nLet's continue to the event along the path.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    setComplete();
                    this.cancel();
                    return;
                }
            }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);
        }

    }

    @EventHandler
    public void death(TutorialPlayerDeathEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        for (Zombie z : entityList)
            if (!z.isDead())
                z.damage(20);
        entityList.clear();
        counter=0;

        //kill zombies
        onBegin();
    }

}

