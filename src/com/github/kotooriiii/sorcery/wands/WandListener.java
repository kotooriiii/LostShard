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
import org.bukkit.inventory.EquipmentSlot;

import static com.github.kotooriiii.sorcery.wands.Wand.getWielding;
import static com.github.kotooriiii.sorcery.wands.Wand.isWielding;

public class WandListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if(event.getHand() != EquipmentSlot.HAND)
            return;

        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (isWielding(player)) {
                SpellType type = getWielding(player);
                if (type == null)
                    return;
                Spell spell = Spell.of(type);
                if (spell == null)
                    return;

                Wand wand = new Wand(spell);


                wand.cast(player);

            }
        }
    }
}

