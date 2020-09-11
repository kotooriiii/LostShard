package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.type.tutorial.murderer.MurdererNPC;
import com.github.kotooriiii.npc.type.tutorial.murderer.MurdererTrait;
import com.github.kotooriiii.tutorial.events.TutorialMurdererDeathEvent;
import com.github.kotooriiii.tutorial.events.TutorialPlayerDeathEvent;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class MurdererChapter extends AbstractChapter {

    private int counter = 0;

    //todo
    private static Location[] locations = new Location[]{};

    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        sendIntro(player);
    }

    @Override
    public void onDestroy() {

    }

    public void sendIntro(Player player) {
        sendMessage(player, "This is Order. It is where all worthy players spawn.\nOrder protects Worthy players with Guards.\nIf you see a murderer, type /guards.");

        new BukkitRunnable() {
            @Override
            public void run() {
                spawnMurderer(player);
                sendMessage(player, "Watch out! A murderer!");

                if(counter++>=1)
                    player.sendTitle("Type \"/guards\" when a Murderer is nearby.", "", 40, 40, 10);

                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 5);
    }

    public void spawnMurderer(Player player) {
        if(!player.isOnline())
            return;
        Location location = locations[new Random().nextInt(locations.length)];
        MurdererNPC npc = new MurdererNPC(player);
        npc.spawn(location);
    }

    @EventHandler
    public void onDeath(TutorialPlayerDeathEvent event)
    {
        if(!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;
        sendIntro(event.getPlayer());
    }

    @EventHandler
    public void onNPCDeath(TutorialMurdererDeathEvent event)
    {
        if(!event.getNPC().getTrait(MurdererTrait.class).getTargetTutorial().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;

        Player player = Bukkit.getPlayer(getUUID());
        if(player!=null)
        sendMessage(player, "Good job!\nGuards kill criminals and murderers instantly.");
        new BukkitRunnable() {
            @Override
            public void run() {
                setComplete();
            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);
    }

}
