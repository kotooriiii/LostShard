package com.github.kotooriiii.tutorial.default_chapters.volume1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.SpellCastEvent;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WandInstructionChapter extends AbstractChapter {

    private int z;
    private boolean isComplete,isHologramSetup;
    private static final int dontGoPastOver = 354;

    public static int getLimitZone()
    {
        return dontGoPastOver;
    }

    public WandInstructionChapter() {
        isComplete = false;
        z = 467;
    }

    @Override
    public void onBegin() {

        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        setLocation(new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 487, 65, 383, -27, 2));

        new BukkitRunnable() {
            @Override
            public void run() {
                sendMessage(player, "Follow the path!", ChapterMessageType.HELPER);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20*2);
        sendMessage(player, "To teleport, swing the stick in the direction you want to teleport.", ChapterMessageType.HOLOGRAM_TO_TEXT);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
    }

    @EventHandler
    public void onCast2(SpellCastEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(event.getSpell().getType() != SpellType.TELEPORT)
            return;
        if(isHologramSetup)
            return;

        isHologramSetup=true;
        sendMessage(event.getPlayer(), "Make it past the ravine to get to the next marker!", ChapterMessageType.HOLOGRAM_TO_TEXT);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
    }


    @EventHandler
    public void move(PlayerMoveEvent event) {
        if (isComplete)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (event.getTo().getBlockZ() < z)
            return;
        isComplete = true;
        setComplete();

    }

    @EventHandler
    public void onCast(SpellCastEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        event.getPlayer().getInventory().setItem(9, new ItemStack(Material.FEATHER, 64));
    }

    @EventHandler
    public void onFall(PlayerMoveEvent event) {

        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if (event.getTo().getBlockY() < 45) {
            if (!event.getPlayer().isDead())
                event.getPlayer().damage(500.0f);
        }
    }

    @EventHandler
    public void onProximity(PlayerMoveEvent event) {

        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(isHologramSetup)
            return;

        Location to = event.getTo();
        if (to.getBlockZ() < WandInstructionChapter.getLimitZone()+10)
            return;

        sendMessage(event.getPlayer(), "You can't go past here until you swing your wand!", ChapterMessageType.HELPER);
        event.setCancelled(true);

    }


    @Override
    public void onDestroy() {
        //No clean up needed :)
    }

}
