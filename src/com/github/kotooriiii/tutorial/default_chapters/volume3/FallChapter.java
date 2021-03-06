package com.github.kotooriiii.tutorial.default_chapters.volume3;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.wands.Wand;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class FallChapter extends AbstractChapter {

    private boolean isComplete;
    private boolean isCompleteToLeave;
    private boolean hasBrokenMelon;
    private Zone zone;
    private Zone completeZone;
    private int melonCounter;


    public FallChapter() {
        this.isComplete = this.isCompleteToLeave = this.hasBrokenMelon = false;
        this.zone = new Zone(753, 691, 48, 75, 1066, 1131);
        melonCounter = 0;
        completeZone = new Zone(561, 486, 78, 30, 1132, 1160);
    }

    @Override
    public void onBegin() {

        final Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;
        setLocation(new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 891, 54, 976, 47, 13));
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID());
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        sendMessage(player, "Ouch, that was a hard hit!\nMaybe we'll find something on the way to heal us...", ChapterMessageType.HOLOGRAM_TO_TEXT);
        player.getInventory().setItem(3, new Wand(Spell.of(SpellType.TELEPORT)).createItem());
        player.getInventory().setItem(5, new ItemStack(Material.FEATHER, 64));
        player.updateInventory();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 * 5, 3, false, false, false));

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public double getDefaultHealth() {
        return 10.0f;
    }

    @Override
    public int getDefaultFoodLevel() {
        return 17;
    }

    @Override
    public boolean isUsingHeal() {
        return false;
    }

    @EventHandler
    public void onMelonBreak(BlockBreakEvent event) {
        if(hasBrokenMelon) {
            event.getPlayer().sendMessage(ERROR_COLOR + "You don't need any more melons.");
            event.setCancelled(true);
            return;
        }
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (event.getBlock().getType() != Material.MELON)
            return;
        if(++melonCounter< 3)
        {
            switch (melonCounter)
            {
                case 1:
                    event.getPlayer().sendMessage(STANDARD_COLOR + "Break 2 more melons!");
                    break;
                case 2:
                    event.getPlayer().sendMessage(STANDARD_COLOR + "Break 1 more melon!");
                    break;
            }
            return;
        }
        event.getPlayer().sendMessage(STANDARD_COLOR + "Perfect! Let's keep walking...");
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 * 5, 3, false, false, false));
        event.setCancelled(true);
        hasBrokenMelon = true;
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
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);


        sendMessage(player, "Some melons! Break them to collect them!\nMelons can be instantly eaten by right clicking them.\nThey instantly heal your hearts and hunger.\nThis can be very useful in combat, let's move on.", ChapterMessageType.HOLOGRAM_TO_TEXT);
        setLocation(player.getLocation());
        event.getPlayer().removePotionEffect(PotionEffectType.SPEED);

    }

    @EventHandler
    public void onMelonAbandon(PlayerMoveEvent event) {
        if (hasBrokenMelon)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        Location to = event.getTo();
        if (to.getBlockX() > 694)
            return;

        final Player player = event.getPlayer();
        sendMessage(player, "You must break some melons before continuing.", ChapterMessageType.HELPER);
        event.setCancelled(true);

    }

    @EventHandler
    public void onLeave(PlayerMoveEvent event) {
        if (isCompleteToLeave)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (completeZone == null)
            return;

        Location to = event.getTo();
        if (!completeZone.contains(to))
            return;

        isCompleteToLeave = true;
        setComplete();
    }

}
