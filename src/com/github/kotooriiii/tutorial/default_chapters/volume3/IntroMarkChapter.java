package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.SpellCastEvent;
import com.github.kotooriiii.sorcery.events.MarkCreateEvent;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.tutorial.AbstractChapter;
import com.github.kotooriiii.tutorial.default_chapters.volume1.WandInstructionChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class IntroMarkChapter extends AbstractChapter {
    @Override
    public void onBegin() {
        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;
        player.getInventory().addItem(new ItemStack(Material.FEATHER, 10), new ItemStack(Material.REDSTONE, 10));
        sendMessage(player, "Before we leave, we should set a mark here so we can teleport back.\nTo set a mark, type: /cast mark\nName the mark something easy to remember, like 'Home'.");
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximity(PlayerMoveEvent event) {

        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        Location to = event.getTo();
        if (to.getBlockZ() < 803)
            return;

        sendMessage(event.getPlayer(), "You can't go past here until you set a mark! You can do so with: /cast mark");
        event.setCancelled(true);
    }

    @EventHandler
    public void onMark(SpellCastEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (event.getSpell().getType() != SpellType.MARK) {
            sendMessage(event.getPlayer(), ChatColor.RED + "You will be able to try out other spells after the tutorial.");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onMark(MarkCreateEvent event)
    {
        if(!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if(!isActive())
            return;

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        sendMessage(event.getPlayer(), "Awesome job! Now follow the path to get to the event.");
        setComplete();
        return;
    }
}
