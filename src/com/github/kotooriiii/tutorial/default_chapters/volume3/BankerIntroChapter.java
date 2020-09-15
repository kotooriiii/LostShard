package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.bank.events.BankDepositEvent;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class BankerIntroChapter extends AbstractChapter {

    private Zone zone;
    private boolean isComplete;
    public BankerIntroChapter()
    {
        isComplete=false;
        this.zone = new Zone(712, 703, 72, 68, 861, 874);
    }

    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        sendMessage(player, "Deposit your gold at the banker. It is in here somewhere...");
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximity(PlayerMoveEvent event) {
        if(isComplete)
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

        isComplete=true;

        final Player player = event.getPlayer();
        player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 10));
        sendMessage(player, "You found it! Let's deposit all the gold you mined.\nType: /deposit (amount)");
    }

    @EventHandler
    public void onDeposit(BankDepositEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        setComplete();
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
