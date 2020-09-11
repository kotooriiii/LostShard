package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.SpellCastEvent;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.type.RecallSpell;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class RecallChapter extends AbstractChapter {
    @Override
    public void onBegin() {
        new BukkitRunnable() {
            @Override
            public void run() {
                final Player player = Bukkit.getPlayer(getUUID());
                if (player == null) {
                    this.cancel();
                    return;
                }

                for (ItemStack itemStack : new RecallSpell().getIngredients()) {
                    player.getInventory().addItem(itemStack);

                }
                sendMessage(player, "Head back to your plot by recalling to the mark you set there.\nType: /cast recall\nIf you forgot the name of your mark, type: /marks");
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onCast(SpellCastEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (event.getSpell().getType() != SpellType.RECALL) {
            sendMessage(event.getPlayer(), ChatColor.RED + "You will be able to try out other spells after the tutorial.");
            event.setCancelled(true);
            return;
        }

        sendMessage(event.getPlayer(), "Great job! You recalled back successfully.");
        setComplete();
    }
}
