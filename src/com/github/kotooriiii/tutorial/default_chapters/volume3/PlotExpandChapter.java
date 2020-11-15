package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.events.PlotExpandEvent;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.tutorial.AbstractChapter;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlotExpandChapter extends AbstractChapter {
    private boolean isReady = false;
    private int counter = 0;
    private final static int COUNTER_END = 3;

    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;
        Hologram h = LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        faceDirection(player, h.getLocation());
        isReady = true;
        sendMessage(player, "Expand your plot by typing: /plot expand.\nDo this multiple times to make it bigger.", ChapterMessageType.HOLOGRAM_TO_TEXT);
    }

    @Override
    public void onDestroy() {

    }


    private void faceDirection(Player player, Location target) {
        Vector dir = target.clone().subtract(player.getEyeLocation()).toVector();
        Location loc = player.getLocation().setDirection(dir);
        player.teleport(loc);
    }

    @EventHandler
    public void onMovee(PlayerMoveEvent event) {

        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!isReady)
            return;

        sendMessage(event.getPlayer(), "You must expand your plot before leaving: /plot expand.", ChapterMessageType.HELPER);
        event.setCancelled(true);
    }


    @EventHandler
    public void onExpand(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        event.setCancelled(true);
        if (!event.getMessage().substring(1).equalsIgnoreCase("plot expand")) {
            sendMessage(event.getPlayer(), "Type: \"/plot expand\" to continue.", ChapterMessageType.HELPER);
            return;
        }
        counter++;
        if (counter == COUNTER_END) {
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
            LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
            sendMessage(event.getPlayer(), "Looks like you ran out of gold!\nLet's get some more by capturing an event.", ChapterMessageType.HOLOGRAM_TO_TEXT);
            sendMessage(event.getPlayer(), "Type: /cast mark", ChapterMessageType.HELPER);
            setComplete();
            return;
        }

        event.getPlayer().sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " expanded the plot to size " + counter + ".");

    }
}
