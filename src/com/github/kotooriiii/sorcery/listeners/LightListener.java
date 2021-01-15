package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.type.circle1.LightSpell;
import javafx.scene.effect.Light;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;

public class LightListener implements Listener {

    @EventHandler
    public void onTPLightListener(PlayerTeleportEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        final int x_initial, y_initial, z_initial,
                x_final, y_final, z_final;

        x_initial = event.getFrom().getBlockX();
        y_initial = event.getFrom().getBlockY();
        z_initial = event.getFrom().getBlockZ();

        x_final = event.getTo().getBlockX();
        y_final = event.getTo().getBlockY();
        z_final = event.getTo().getBlockZ();

        if (x_initial == x_final && y_initial == y_final && z_initial == z_final)
            return;

        Location location = LightSpell.getMap().get(event.getPlayer().getUniqueId());
        if (location == null)
            return;

        LightSpell.deleteLight(location);

        Location newLocation = event.getTo().clone().add(0, 1, 0);
        LightSpell.createLight(newLocation);
        LightSpell.getMap().put(event.getPlayer().getUniqueId(), newLocation);
    }

    @EventHandler
    public void onMoveLightListener(PlayerMoveEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        final int x_initial, y_initial, z_initial,
                x_final, y_final, z_final;

        x_initial = event.getFrom().getBlockX();
        y_initial = event.getFrom().getBlockY();
        z_initial = event.getFrom().getBlockZ();

        x_final = event.getTo().getBlockX();
        y_final = event.getTo().getBlockY();
        z_final = event.getTo().getBlockZ();

        if (x_initial == x_final && y_initial == y_final && z_initial == z_final)
            return;

        Location location = LightSpell.getMap().get(event.getPlayer().getUniqueId());
        if (location == null)
            return;

        LightSpell.deleteLight(location);

        Location newLocation = event.getTo().clone().add(0, 1, 0);
        LightSpell.createLight(newLocation);
        LightSpell.getMap().put(event.getPlayer().getUniqueId(), newLocation);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        Location location = LightSpell.getMap().get(event.getPlayer().getUniqueId());
        if (location == null)
            return;

        LightSpell.deleteLight(location);
        LightSpell.getMap().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Location location = LightSpell.getMap().get(event.getEntity().getUniqueId());
        if (location == null)
            return;

        LightSpell.deleteLight(location);
        LightSpell.getMap().remove(event.getEntity().getUniqueId());

    }
}
