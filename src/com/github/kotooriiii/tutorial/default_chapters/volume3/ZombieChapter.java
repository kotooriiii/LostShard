package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.events.TutorialPlayerDeathEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ZombieChapter extends AbstractChapter {

    private int counter;
    private List<UUID> entityList;
    private Zone cantLeaveZone;
    private boolean isPause, isHologramSetup;

    public ZombieChapter() {
        this.counter = 0;
        isPause = false;
        isHologramSetup = false;
        this.entityList = new ArrayList<>();
        cantLeaveZone = new Zone(442, 477, 78, 30, 1109, 1068);
    }

    @Override
    public void onBegin() {

        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        setLocation(new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 540, 58, 1160, 126, 3));


        if (!player.getInventory().contains(Material.DIAMOND_SWORD)) {
            final ItemStack itemStack = player.getInventory().getItem(0);
            player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
            player.getInventory().addItem(itemStack);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                spawnZombies(player);
                if (!isHologramSetup)
                    LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
                sendMessage(player, "Some zombies... these are always worth killing.");
                isHologramSetup = true;

            }
        }.runTaskLater(LostShardPlugin.plugin, 10);

    }

    private void spawnZombies(Player player) {
        final int x = 523, y = 56, z = 1149;

        player.getWorld().createExplosion(x, y, z, 6f, false, false);

        for (int i = 0; i < 4; i++) {
            //4
            //8
            //9
            //0-8
            //-4-4;
            int random = new Random().nextInt((4 * 2) + 1) - 4; //0-3
            Location location = new Location(player.getWorld(), x + random, y, z + random);
            Zombie zombie = (Zombie) player.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            zombie.setCustomName("[Tutorial] Zombie");
            zombie.setCustomNameVisible(true);
            entityList.add(zombie.getUniqueId());

        }
    }

    @Override
    public void onDestroy() {
    }


    @EventHandler
    public void entDmg(EntityDamageByEntityEvent event) {
        if (!isActive())
            return;
        if (!event.getDamager().getUniqueId().equals(getUUID()) && entityList.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        //good to go

    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (!entityList.contains(event.getEntity().getUniqueId()))
            return;
        if (event.getTarget() == null)
            return;
        if (event.getTarget().getUniqueId().equals(getUUID()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {

        if (isPause)
            return;

        LivingEntity livingEntity = event.getEntity();

        if (!(livingEntity instanceof Zombie))
            return;

        Zombie zombie = (Zombie) livingEntity;

        if (!isActive())
            return;
        if (!entityList.contains(zombie.getUniqueId()))
            return;
        counter++;

        if (counter == 4) {
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);

            sendMessage(Bukkit.getPlayer(getUUID()), "Zombies drop rotten flesh which is instantly eatable like melons.\nThey also drop feathers, which is a useful ingredient in casting spells.\nLet's continue to the event along the path.");
            setComplete();
        }

    }

    @EventHandler
    public void death(TutorialPlayerDeathEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        isPause = true;

        for (UUID uuid : entityList) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity == null)
                continue;
            if (entity.isDead())
                continue;
            entity.remove();
        }
        entityList.clear();
        counter = 0;

        isPause = false;

        //kill zombies
        onBegin();
    }


    @EventHandler
    public void onLeaveOrder(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!cantLeaveZone.contains(event.getTo()))
            return;

        sendMessage(event.getPlayer(), "You must defeat the zombies before being able to venture out.");
        event.setCancelled(true);
    }

}

