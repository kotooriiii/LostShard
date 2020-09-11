package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.events.BankDepositEvent;
import com.github.kotooriiii.hostility.Zone;
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

public class BankerIntroChapter extends AbstractChapter {

    private Zone zone;
    private boolean isComplete;
    public BankerIntroChapter()
    {
        isComplete=false;
        //todo
    }

    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        sendMessage(player, "Deposit your gold at the banker.");
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
        sendMessage(player, "Type: /deposit (amount)");
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
}
