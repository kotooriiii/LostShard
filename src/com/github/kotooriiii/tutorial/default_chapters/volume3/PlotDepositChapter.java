package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.plots.events.PlotDepositEvent;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.tutorial.AbstractChapter;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlotDepositChapter extends AbstractChapter {
    private boolean isReady = false;

    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        Hologram h = LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        faceDirection(player, h.getLocation());
        isReady = true;
        sendMessage(player, "No sense in keeping all that gold on you!\nDeposit the rest of it in your plot by typing: /plot deposit (amount).", ChapterMessageType.HOLOGRAM_TO_TEXT);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    this.cancel();
                    return;
                }
                sendMessage(player, "Your entire balance is seen on the right side of your HUD.", ChapterMessageType.HELPER);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 5L);


    }

    private void faceDirection(Player player, Location target) {
        Vector dir = target.clone().subtract(player.getEyeLocation()).toVector();
        Location loc = player.getLocation().setDirection(dir);
        player.teleport(loc);
    }


    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!isReady)
            return;

        sendMessage(event.getPlayer(), "You must deposit your entire balance to the plot before leaving: /plot deposit (amount).", ChapterMessageType.HELPER);
        event.setCancelled(true);
    }

    @EventHandler
    public void onDeposit(PlotDepositEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        double curr = LostShardPlugin.getBankManager().wrap(event.getPlayer().getUniqueId()).getCurrency();
        if ((int) curr - (int) event.getAmount() != 0) {
            sendMessage(event.getPlayer(), "Deposit all your money into the plot! You can view your entire balance with /balance.", ChapterMessageType.HOLOGRAM_TO_TEXT);
            return;
        }
        LostShardPlugin.getBankManager().wrap(getUUID()).setCurrency(0);
        setComplete();

    }
}
