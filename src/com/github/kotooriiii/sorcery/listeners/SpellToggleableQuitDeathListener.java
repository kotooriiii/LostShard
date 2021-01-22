package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellToggleable;
import com.github.kotooriiii.sorcery.spells.type.circle7.RadiateSpell;
import com.github.kotooriiii.stats.Stat;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class SpellToggleableQuitDeathListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        for (Map.Entry<SpellToggleable, HashSet<UUID>> entry : Spell.getManaDrainMap().entrySet()) {
            final SpellToggleable spell = entry.getKey();
            final HashSet<UUID> set = entry.getValue();
            final Iterator<UUID> iterator = set.iterator();
            while (iterator.hasNext()) {

                UUID uuid = iterator.next();

                if(!uuid.equals(event.getPlayer().getUniqueId()))
                    continue;
                spell.stopManaDrain(uuid);
                iterator.remove();
                break;
            }
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;

        for (Map.Entry<SpellToggleable, HashSet<UUID>> entry : Spell.getManaDrainMap().entrySet()) {
            final SpellToggleable spell = entry.getKey();
            final HashSet<UUID> set = entry.getValue();
            final Iterator<UUID> iterator = set.iterator();
            while (iterator.hasNext()) {

                UUID uuid = iterator.next();
                if(!uuid.equals(event.getEntity().getUniqueId()))
                    continue;
                spell.stopManaDrain(uuid);
                iterator.remove();
                break;
            }
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

        if(!Spell.isLapisNearby(event.getTo(), Spell.getDefaultLapisNearbyValue()))
            return;


        for (Map.Entry<SpellToggleable, HashSet<UUID>> entry : Spell.getManaDrainMap().entrySet()) {
            final SpellToggleable spell = entry.getKey();
            final HashSet<UUID> set = entry.getValue();
            final Iterator<UUID> iterator = set.iterator();
            while (iterator.hasNext()) {

                UUID uuid = iterator.next();
                if(!uuid.equals(event.getPlayer().getUniqueId()))
                    continue;
                event.getPlayer().sendMessage(ERROR_COLOR + "Something toggled off your spell...");
                spell.stopManaDrain(uuid);
                iterator.remove();
                break;
            }
        }
    }
}
