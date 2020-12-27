package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PathToEventChapter extends AbstractChapter {

    private boolean isComplete, isComplete2;
    private static Zone zone = new Zone(917, 884, 110, 89, 978, 938);
    private static Zone fallZone = new Zone(915, 871, 61, 52, 991, 857);

    public PathToEventChapter() {
        isComplete = false;
    }


    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 * 5, 3, false, false, false));

    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onProximity(PlayerMoveEvent event) {
        if (isComplete)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (zone == null)
            return;

        Location to = event.getTo();
        if (!zone.contains(to))
            return;

        isComplete = true;
        final Player player = event.getPlayer();
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        player.removePotionEffect(PotionEffectType.SPEED);
        sendMessage(player, "That's a setback. Looks like the only way to keep moving forward is to jump...", ChapterMessageType.HOLOGRAM_TO_TEXT);
    }

    @EventHandler
    public void onDmg(PlayerMoveEvent event) {
        if (isComplete2)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (fallZone == null)
            return;

        Location to = event.getTo();
        if (!fallZone.contains(to))
            return;
        isComplete2 = true;

        setComplete();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!event.getEntity().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        isComplete = false;

    }
}
