package com.github.kotooriiii.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class DropLostShardBookListener implements Listener {
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();

        if (!itemStack.getType().equals(Material.WRITTEN_BOOK))
            return;

        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

        String author = ChatColor.stripColor(bookMeta.getAuthor());
        if (!author.equalsIgnoreCase("Nickolov"))
            return;

        String title = ChatColor.stripColor(bookMeta.getTitle());
        if (!title.equalsIgnoreCase("LostShard"))
            return;

        item.remove();
    }
}
