package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.events.PlotDepositEvent;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class PlotDepositChapter extends AbstractChapter {
    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if(player==null)
            return;
        sendMessage(player, "No sense in keeping all that gold on you!\nDeposit the rest of it in your plot by typing: /plot deposit (amount).");

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!isActive())
                {
                    this.cancel();
                    return;
                }
                sendMessage(player, "Your entire balance is seen on the right side of your HUD.");
            }
        }.runTaskLater(LostShardPlugin.plugin, 20*5L);


    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onDeposit(PlotDepositEvent event)
    {
        if(!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;

        double curr = LostShardPlugin.getBankManager().wrap(event.getPlayer().getUniqueId()).getCurrency();
        if((int) curr - (int) event.getAmount() != 0)
        {
            sendMessage(event.getPlayer(), "Deposit all your money into the plot! You can view your entire balance on the right side of your HUD.");
            return;
        }

        setComplete();

    }
}
