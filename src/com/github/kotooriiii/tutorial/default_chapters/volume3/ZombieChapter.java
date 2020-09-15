package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.events.TutorialPlayerDeathEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieChapter extends AbstractChapter {

    private int counter;
    private List<Zombie> entityList;
    private Zone cantLeaveZone;

    public ZombieChapter() {
        this.counter = 0;
        this.entityList = new ArrayList<>();
        cantLeaveZone = new Zone(442, 477, 78, 30, 1109, 1068);
    }

    @Override
    public void onBegin() {

        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        setLocation(new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 540, 58, 1160, 126, 3));

        new BukkitRunnable() {
            @Override
            public void run() {
                spawnZombies(player);
                sendMessage(player, "Some zombies... these are always worth killing.");

            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);

    }

    private void spawnZombies(Player player) {
        final int x =523, y=56, z=1149;

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


    @EventHandler
    public void onLeaveOrder(PlayerMoveEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(!cantLeaveZone.contains(event.getTo()))
            return;
        sendMessage(event.getPlayer(), "You must defeat the zombies before being able to venture out.");
        event.setCancelled(true);
    }

}

