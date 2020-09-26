package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.sorcery.events.MarkCreateEvent;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class MarkSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> markSpellCooldownMap = new HashMap<>();

    public MarkSpell() {
        super(SpellType.MARK, ChatColor.DARK_PURPLE,  new ItemStack[]{new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1)},  2.0f , 15, true,  true, false);
    }

    @EventHandler
    public void onChatArg(ShardChatEvent event)
    {
        Player player = event.getPlayer();
        SpellType type = waitingForArgumentMap.get(player.getUniqueId());
        if(type == null)
            return;
        if(!type.equals(getType()))
            return;

        waitingForArgumentMap.remove(player.getUniqueId());
        mark(player, event.getMessage());
        event.setCancelled(true);

    }

    @Override
    public void updateCooldown(Player player)
    {
        markSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    markSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                markSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (markSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = markSpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(0, RoundingMode.UP);
            int value = bd.intValue();
            if (value == 0)
                value = 1;

            String time = "seconds";
            if (value == 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }

    @Override
    public boolean executeSpell(Player player) {

        waitingForArgumentMap.put(player.getUniqueId(), this.getType());
        player.sendMessage(ChatColor.AQUA +  "What would you like to name your Mark?");
        return true;
    }

    /**
     * teleports player to clan member
     */
    private void mark(Player playerSender, String message) {
        if (playerSender == null || playerSender.isDead() || !playerSender.isOnline())
            return;

        Location markLocation = playerSender.getLocation();

        if (!hasMarkRequirements(playerSender, message))
            return;

        //success
        postCast(playerSender, markLocation, message);
    }


    /**
     * after the successful tp
     * @return
     */
    private void postCast(Player playerSender, Location location, String name)
    {
        MarkCreateEvent event = new MarkCreateEvent(playerSender, location, name);
        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(event);
        //message
        playerSender.sendMessage(ChatColor.GOLD + "You have created a mark called \"" + name + "\".");
        //add mark
        MarkPlayer.wrap(playerSender.getUniqueId()).addMark(name, location);

    }


    /**
     * checks if you are able to create a mark with said name
     * @param playerSender
     * @return
     */
    private boolean hasMarkRequirements(Player playerSender, String name)
    {
        UUID playerUUID = playerSender.getUniqueId();

        MarkPlayer markPlayer = MarkPlayer.wrap(playerUUID);
        MarkPlayer.Mark[] marks = markPlayer.getMarks();

        RankPlayer rankPlayer = RankPlayer.wrap(playerUUID);

        if (marks.length == rankPlayer.getRankType().getMaxMarksNum()) {
            playerSender.sendMessage(ERROR_COLOR + "You have reached the maximum limit of marks.");
            return false;
        }

        if (markPlayer.hasMark(name)) {
            playerSender.sendMessage(ERROR_COLOR + "You already have a mark by this name.");
            return false;
        }

        if(markPlayer.isPremadeMark(name))
        {
            playerSender.sendMessage(ERROR_COLOR + "You cannot name your mark \"" + name + "\". This is a server-made mark for your use.");
            return false;
        }
        return true;
    }

}
