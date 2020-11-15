package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.SpellCastEvent;
import com.github.kotooriiii.sorcery.events.SuccessfulRecallEvent;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.type.RecallSpell;
import com.github.kotooriiii.tutorial.AbstractChapter;
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

                new BukkitRunnable()
                {
                    @Override
                    public void run() {
                        sendMessage(player, "If you forgot the name of your mark, type: /marks", ChapterMessageType.HELPER);
                    }
                }.runTaskLater(LostShardPlugin.plugin, (20*10));

                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onCast(SuccessfulRecallEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        sendMessage(event.getPlayer(), "Great job! You recalled back successfully.", ChapterMessageType.HOLOGRAM_TO_TEXT);
        setComplete();
    }
}
