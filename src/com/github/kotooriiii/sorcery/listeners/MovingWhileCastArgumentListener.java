package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class MovingWhileCastArgumentListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        //not casting spell
        if (!Spell.getWaitingForArgumentMap().containsKey(player.getUniqueId()))
            return;


        int fX = event.getFrom().getBlockX();
        int fY = event.getFrom().getBlockY();
        int fZ = event.getFrom().getBlockZ();

        int tX = event.getTo().getBlockX();
        int tY = event.getTo().getBlockY();
        int tZ = event.getTo().getBlockZ();

        if (fX == tX && fY == tY && fZ == tZ)
            return;

        //Is casting a spell and moved a block

        player.sendMessage(ERROR_COLOR + "Your spell was interrupted due to movement.");
        SpellType type = Spell.getWaitingForArgumentMap().get(player.getUniqueId());
        Spell.of(type).refund(player);
        Spell.getWaitingForArgumentMap().remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDmg(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (event.isCancelled())
            return;

        if (!(entity instanceof Player))
            return;

        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;

        Player player = (Player) entity;

        //not casting spell
        if (!Spell.getWaitingForArgumentMap().containsKey(player.getUniqueId()))
            return;


        //Is casting a spell and took dmg

        player.sendMessage(ERROR_COLOR + "Your spell was interrupted due to damage.");
        SpellType type = Spell.getWaitingForArgumentMap().get(player.getUniqueId());
        Spell.of(type).refund(player);
        Spell.getWaitingForArgumentMap().remove(player.getUniqueId());
    }
}
