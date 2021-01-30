package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.type.circle8.DaySpell;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class DayListener implements Listener {

    private final static String ID = "{UNIQUE_ID}";
    private final static String ERROR_MESSAGE = ERROR_COLOR + ID + " broke the channeling.";

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        DaySpell.getInstance().remove(event.getPlayer().getUniqueId(), ERROR_MESSAGE.replace(ID, event.getPlayer().getName()));

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;

        DaySpell.getInstance().remove(event.getEntity().getUniqueId(), ERROR_MESSAGE.replace(ID, event.getEntity().getName()));

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
        DaySpell.getInstance().remove(event.getPlayer().getUniqueId(), ERROR_MESSAGE.replace(ID, event.getPlayer().getName()));

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        if(event.getBlockPlaced().getType() != Material.LAPIS_BLOCK)
            return;
        DaySpell.getInstance().remove(event.getPlayer().getUniqueId(), ERROR_MESSAGE.replace(ID, event.getPlayer().getName()));

    }
}
