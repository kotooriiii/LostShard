package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.npc.type.tutorial.murderer.MurdererNPC;
import com.github.kotooriiii.npc.type.tutorial.murderer.MurdererTrait;
import com.github.kotooriiii.tutorial.events.TutorialMurdererDeathEvent;
import com.github.kotooriiii.tutorial.events.TutorialPlayerDeathEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class MurdererChapter extends AbstractChapter {

    private int counter = 0;
    private boolean isSpawnedMurderer = false;


    //todo
    private static Zone zone = new Zone(590,592,66,66,796,794);
    private static Location[] locations = new Location[]
            {
                    new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 635, 71, 785),
                    new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 599, 70, 821),
                    new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 625, 71, 823),
                    new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 600, 71, 788)
            };

    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        isSpawnedMurderer=false;
        sendIntro(player);
    }

    @Override
    public void onDestroy() {

    }

    public void sendIntro(Player player) {
        sendMessage(player, "This is Order. It is where all worthy players spawn.\nOrder protects Worthy players with Guards.\nIf you see a murderer, type /guards.");
    }

    public void spawnMurderer(Player player) {
        if(!player.isOnline())
            return;
        Location location = locations[new Random().nextInt(locations.length)];
        MurdererNPC npc = new MurdererNPC(player);
        npc.spawn(location);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        if(!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;
        if(isSpawnedMurderer)
            return;
        if(!zone.contains(event.getTo()))
            return;

        Player player = event.getPlayer();
        isSpawnedMurderer=true;
        spawnMurderer(player);
        sendMessage(player, "Watch out! A murderer!");

        if(counter++>=1)
            player.sendTitle("Type \"/guards\" when a Murderer is nearby.", "", 40, 40, 10);
        return;
    }

    @EventHandler
    public void onDeath(TutorialPlayerDeathEvent event)
    {
        if(!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;

        isSpawnedMurderer=false;
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
        sendMessage(player, "Good job!\nGuards kill criminals and murderers instantly.\nLet's explore the site.");
        new BukkitRunnable() {
            @Override
            public void run() {
                setComplete();
            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);
    }


    @EventHandler
    public void onLeaveOrder(PlayerMoveEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(!PlotIntroChapter.getZone().contains(event.getTo()))
            return;
        sendMessage(event.getPlayer(), "It's not time to venture out just yet.");

        event.setCancelled(true);
    }

}
