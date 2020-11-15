package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class BankChestChapter extends AbstractChapter {
    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;
        LostShardPlugin.getBankManager().wrap(player.getUniqueId()).getInventory().setItem(13, new ItemStack(Material.DIAMOND, 1));
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        sendMessage(player, "To access your bank, type /bank\nNo one else has access to your bank.", ChapterMessageType.HOLOGRAM_TO_TEXT);
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (!event.getView().getTitle().equals(Bank.NAME))
            return;
        if (!event.getInventory().contains(Material.DIAMOND)) {
        } else {
            sendMessage((Player) event.getPlayer(), "Don't you want that diamond? It might just come in handy...", ChapterMessageType.HELPER);
            event.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
        }
        setComplete();

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

    @EventHandler
    public void onLeaveBank(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (event.getTo().getBlockZ() > 853 && event.getTo().getBlockX() > 699)
            return;
        sendMessage(event.getPlayer(), "The banker has one more request! Don't go just yet.", ChapterMessageType.HELPER);

        event.setCancelled(true);
    }

}
