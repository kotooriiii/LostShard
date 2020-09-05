package com.github.kotooriiii.sorcery.wands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.SpellCastEvent;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.github.kotooriiii.sorcery.wands.Wand.getWielding;
import static com.github.kotooriiii.sorcery.wands.Wand.isWielding;

public class WandListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (isWielding(player)) {
                SpellType type = getWielding(player);
                if (type == null)
                    return;
                Spell spell = Spell.of(type);
                if (spell == null)
                    return;
                Wand wand = new Wand(spell);

                SpellCastEvent spellCastEvent = new SpellCastEvent(player, spell);
                LostShardPlugin.plugin.getServer().getPluginManager().callEvent(spellCastEvent);

                if(!spellCastEvent.isCancelled()) {
                    wand.cast(player);
                    event.setCancelled(true);
                }
            }
        }
    }
}

