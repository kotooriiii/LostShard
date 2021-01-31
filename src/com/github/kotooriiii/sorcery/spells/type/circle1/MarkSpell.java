package com.github.kotooriiii.sorcery.spells.type.circle1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.sorcery.events.MarkCreateEvent;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    private final static HashMap<UUID, Integer> waitingToRecallMap = new HashMap<>();


    private MarkSpell() {
        super(SpellType.MARK,
                "Makes a mark where you’re standing that you can recall back to. Type /cast mark, and when the pop up asks you to name the mark, name it something you will remember, such as “home”. \n" +
                "Example:\n" +
                "/cast mark\n" +
                "home",
                1, ChatColor.DARK_PURPLE, new ItemStack[]{new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1)}, 2.0f, 15, true, true, false,
                new SpellMonsterDrop(new EntityType[]{}, 0.00));

    }

    private  static MarkSpell instance;
    public static MarkSpell getInstance() {
        if (instance == null) {
            synchronized (MarkSpell.class) {
                if (instance == null)
                    instance = new MarkSpell();
            }
        }
        return instance;
    }

    @EventHandler
    public void onChatArg(ShardChatEvent event) {
        Player player = event.getPlayer();
        SpellType type = waitingForArgumentMap.get(player.getUniqueId());
        if (type == null)
            return;
        if (!type.equals(getType()))
            return;

        waitingForArgumentMap.remove(player.getUniqueId());
        receiveArgument(event.getPlayer(), event.getMessage());
        event.setCancelled(true);

    }

    /**
     * recalls a player to a mark
     */
    private void receiveArgument(Player playerSender, String message) {
        if (playerSender == null || playerSender.isDead() || !playerSender.isOnline())
            return;

        Location markLocation = playerSender.getLocation();

        if (!hasMarkRequirements(playerSender, message)) {
            refund(playerSender);
            return;
        }

        //success
        mark(playerSender, message, markLocation);
    }

    @EventHandler
    public void onWaitToRecall(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        //not casting spell
        if (!waitingToRecallMap.containsKey(player.getUniqueId()))
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
        refund(player);
        waitingToRecallMap.remove(player.getUniqueId());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onWaitToRecall(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if(event.isCancelled())
            return;

        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;

        if (!(entity instanceof Player))
            return;
        Player player = (Player) entity;


        //not casting spell
        if (!waitingToRecallMap.containsKey(player.getUniqueId()))
            return;


        //Is casting a spell and moved a block

        player.sendMessage(ERROR_COLOR + "Your spell was interrupted due to damage.");
        refund(player);
        waitingToRecallMap.remove(player.getUniqueId());
    }


    @Override
    public void updateCooldown(Player player) {
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
        player.sendMessage(ChatColor.YELLOW + "What would you like to name your Mark?");
        return true;
    }

    /**
     * teleports player to clan member
     */
    private void mark(Player player, String message, Location markLocation) {

        final int WAITING_TO_RECALL_PERIOD = 2;
        player.sendMessage(ChatColor.GOLD + "You begin to cast mark...");
        waitingToRecallMap.put(player.getUniqueId(), WAITING_TO_RECALL_PERIOD);

        new BukkitRunnable() {
            int counter = WAITING_TO_RECALL_PERIOD;

            @Override
            public void run() {


                if (!waitingToRecallMap.containsKey(player.getUniqueId())) {
                    this.cancel();
                    return;
                }


                if (counter == 0) {
                    this.cancel();
                    waitingToRecallMap.remove(player.getUniqueId());

                    if (!HelperMethods.getLookingSet().contains(markLocation.getBlock().getType()) && !markLocation.getBlock().getType().getKey().getKey().toUpperCase().endsWith("_SLAB")) {
                        player.sendMessage(ERROR_COLOR + "You cannot create a mark in an obstructed location. Find a more open area!");
                        refund(player);
                        return;
                    }


                    if (!postCast(player, markLocation, message)) {
                        if (player.isOnline())
                            refund(player);

                    }
                    return;
                }

                counter--;
                waitingToRecallMap.put(player.getUniqueId(), counter);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
        //success
    }


    /**
     * after the successful tp
     *
     * @return
     */
    private boolean postCast(Player playerSender, Location location, String name) {

        if (!playerSender.isOnline())
            return false;

        if (isLapisNearby(location, DEFAULT_LAPIS_NEARBY)) {
            playerSender.sendMessage(ERROR_COLOR + "You can not seem to cast " + getName() + " there...");
            return false;
        }

        MarkCreateEvent event = new MarkCreateEvent(playerSender, location, name);
        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(event);
        //message
        playerSender.sendMessage(ChatColor.GOLD + "You have created a mark called \"" + name + "\".");
        //add mark
        MarkPlayer.wrap(playerSender.getUniqueId()).addMark(name, location);
        return true;

    }


    /**
     * checks if you are able to create a mark with said name
     *
     * @param playerSender
     * @return
     */
    private boolean hasMarkRequirements(Player playerSender, String name) {
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

        if (markPlayer.isPremadeMark(name)) {
            playerSender.sendMessage(ERROR_COLOR + "You cannot name your mark \"" + name + "\". This is a server-made mark for your use.");
            return false;
        }
        return true;
    }

}
