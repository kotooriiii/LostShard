package com.github.kotooriiii.tutorial.default_chapters.volume1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.BindEvent;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WandInstructionChapter extends AbstractChapter {

    private Zone zone;

    public WandInstructionChapter() {

      //todo
        // zone = new Zone();
    }

    @Override
    public void onBegin() {

        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        //todo
        //setLocation();

        sendMessage(player, "To teleport, swing the stick in the direction you want to teleport.");

        new BukkitRunnable() {
            @Override
            public void run() {
                sendMessage(player, "Make it past the ravine to get to the next marker!");
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, DELAY_TICK);

    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(!zone.contains(event.getTo()))
            return;
        setComplete();

    }


    @Override
    public void onDestroy() {
        //No clean up needed :)
    }

}
