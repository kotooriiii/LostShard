package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.commands.HealCommand;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.npc.type.tutorial.murderer.MurdererNPC;
import com.github.kotooriiii.npc.type.tutorial.murderer.MurdererTrait;
import com.github.kotooriiii.tutorial.events.TutorialMurdererDeathEvent;
import com.github.kotooriiii.tutorial.events.TutorialPlayerDeathEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class MurdererChapter extends AbstractChapter {

    private int counter = 0;
    private boolean isSpawnedMurderer = false, isHologramSetup = false, isHologramSetup2 = false;

    private static Zone zone = new Zone(590, 596, 60, 70, 813, 792);
    private static Location[] locations = new Location[]
            {
                    new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 617, 66, 804),
                    new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 618, 66, 802),
                    new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 618, 66, 806)
            };

    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        isSpawnedMurderer = false;
        sendIntro(player);
    }

    @Override
    public void onDestroy() {

    }

    public void sendIntro(Player player) {
        if (!isHologramSetup)
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        sendMessage(player, "This is Order. It is where all Lawful players spawn.\nOrder protects Lawful players with Guards.\nIf you see a murderer, type /guards.", ChapterMessageType.HOLOGRAM_TO_TEXT);
        isHologramSetup = true;
    }

    public void spawnMurderer(Player player) {
        if (!player.isOnline())
            return;
        Location location = locations[new Random().nextInt(locations.length)];
        location.getWorld().strikeLightningEffect(location);
        MurdererNPC npc = new MurdererNPC(player);
        npc.spawn(location);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (isSpawnedMurderer)
            return;
        if (!zone.contains(event.getTo()))
            return;

        Player player = event.getPlayer();
        isSpawnedMurderer = true;
        spawnMurderer(player);
        if (!isHologramSetup2)
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        isHologramSetup2 = true;
        sendMessage(player, "Watch out! A murderer!", ChapterMessageType.HOLOGRAM_TO_TEXT);
        sendMessage(player, ChatColor.LIGHT_PURPLE + "Type: /guards", ChapterMessageType.HELPER);

        if (counter++ >= 1)
            player.sendTitle("", "Type \"/guards\" when a Murderer is nearby.", 40, 40, 10);
        return;
    }

    @EventHandler
    public void onDeath(TutorialPlayerDeathEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        isSpawnedMurderer = false;
        sendIntro(event.getPlayer());
    }

    @EventHandler
    public void onNPCDeath(TutorialMurdererDeathEvent event) {
        if (!event.getNPC().getTrait(MurdererTrait.class).getTargetTutorial().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        Player player = Bukkit.getPlayer(getUUID());
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        player.sendMessage(ERROR_COLOR + "The murderer, Colton, has been killed!");
        if (player != null)
            sendMessage(player, "Good job! You killed the murderer using guards.", ChapterMessageType.HOLOGRAM_TO_TEXT);
        HealCommand.heal(player, false);
        new BukkitRunnable() {
            @Override
            public void run() {
                setComplete();
                LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
                LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
                LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);

            }
        }.runTaskLater(LostShardPlugin.plugin, 20*3);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        NPC npc = null;
        for (NPC inpc : MurdererNPC.getAllMurdererNPC()) {
            if (!inpc.getTrait(MurdererTrait.class).getTargetTutorial().getUniqueId().equals(event.getPlayer().getUniqueId()))
                continue;
            npc = inpc;
            break;
        }

        if (npc == null)
            return;

        npc.getOwningRegistry().deregister(npc);

    }

    @EventHandler
    public void onLeaveOrder(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!PlotIntroChapter.getExitOrderZone().contains(event.getTo()))
            return;
        sendMessage(event.getPlayer(), "It's not time to venture out just yet.", ChapterMessageType.HELPER);

        event.setCancelled(true);
    }

}
