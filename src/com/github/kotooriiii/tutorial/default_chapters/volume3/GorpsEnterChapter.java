package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.hostility.HostilityMatch;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.hostility.events.PlatformCaptureEvent;
import com.github.kotooriiii.hostility.events.PlatformStartEvent;
import com.github.kotooriiii.hostility.events.PlatformVictoryEvent;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static com.github.kotooriiii.data.Maps.*;

public class GorpsEnterChapter extends AbstractChapter {

    private boolean isComplete;
    private Zone zone;

    public GorpsEnterChapter() {
        this.isComplete = false;
        //todo zA =
    }

    @Override
    public void onBegin() {


    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximityA(PlayerMoveEvent event) {
        if (isComplete)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (zone == null)
            return;

        Location to = event.getTo();
        if (!zone.contains(to))
            return;

        isComplete = true;
        final Player player = event.getPlayer();
        setLocation(event.getTo());
        Clan clan = new Clan(player.getName(), player.getUniqueId());
        LostShardPlugin.getClanManager().addClan(clan, true);

        sendMessage(player, "You've made it to Gorps!\nHead to the center to capture it.");

        new BukkitRunnable() {
            @Override
            public void run() {
                startGame(player);
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);
        //available
    }

    private void startGame(Player player) {
        for (HostilityPlatform platform : platforms) {

            if (platform.getName().equalsIgnoreCase("Gorps")) {

                for (HostilityMatch match : activeHostilityGames) {
                    if (match.getPlatform().getName().equalsIgnoreCase("Gorps")) {
                        if (player != null)
                            sendMessage(player, ERROR_COLOR + "Gorps is currently being played on. You must fight for it or wait your turn.");
                        return;
                    }
                }
                HostilityMatch match = new HostilityMatch(platform);
                match.startGame();
                return;
            }
        }
    }

    @EventHandler
    public void onStart(PlatformStartEvent event) {
        if (!isActive())
            return;

        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        player.sendMessage(ChatColor.GOLD + event.getPlatform().getName() + " is now available for capture.");
    }


    @EventHandler
    public void onCap(PlatformCaptureEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        sendMessage(event.getPlayer(), "Stay on the platform without getting knocked off to capture it!");
        event.setWins(2);
    }

    @EventHandler
    public void onWin(PlatformVictoryEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        LostShardPlugin.getClanManager().removeClan(LostShardPlugin.getClanManager().getClan(event.getPlayer().getUniqueId()));
        setComplete();
        sendMessage(event.getPlayer(), "Congratulations! You've captured Gorps! You've been awarded 100 gold for your efforts and your max mana has increased to 115 for 24 hours.");
        event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 100));
        startGame(null);
    }
}
