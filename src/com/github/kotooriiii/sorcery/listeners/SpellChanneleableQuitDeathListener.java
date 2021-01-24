package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellToggleable;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class SpellChanneleableQuitDeathListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if(Spell.isChanneling(event.getPlayer()))
        {
            Spell.removeChanneling(event.getPlayer());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;

        if(Spell.isChanneling(event.getEntity()))
        {
            Spell.removeChanneling(event.getEntity());
        }
    }

    @EventHandler
    public void onDeath(PlayerMoveEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        int fX = event.getFrom().getBlockX();
        int fY = event.getFrom().getBlockY();
        int fZ = event.getFrom().getBlockZ();

        int tX = event.getTo().getBlockX();
        int tY = event.getTo().getBlockY();
        int tZ = event.getTo().getBlockZ();

        if (fX == tX && fY == tY && fZ == tZ)
            return;

        if(Spell.isChanneling(event.getPlayer()))
        {
            Spell.removeChanneling(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        if(event.getBlockPlaced().getType() != Material.LAPIS_BLOCK)
            return;
    }
}
