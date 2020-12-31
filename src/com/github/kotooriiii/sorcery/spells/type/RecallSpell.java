package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.sorcery.events.SuccessfulRecallEvent;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
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

public class RecallSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> recallSpellCooldownMap = new HashMap<>();

    private final static HashMap<UUID, Integer> waitingToRecallMap = new HashMap<>();


    public RecallSpell() {
        super(SpellType.RECALL, ChatColor.BLUE, new ItemStack[]{new ItemStack(Material.FEATHER, 1)}, 2.0f, 15, true, true, false);
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
        receiveArgument(player, event.getMessage());
        event.setCancelled(true);
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWaitToRecall(EntityDamageEvent event) {

        if (event.isCancelled())
            return;
        Entity entity = event.getEntity();
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
        recallSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    recallSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                recallSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (recallSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = recallSpellCooldownMap.get(player.getUniqueId());
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
        player.sendMessage(ChatColor.YELLOW + "Where would you like to recall to?");
        return true;
    }

    /**
     * recalls a player to a mark
     */
    private void receiveArgument(Player playerSender, String message) {
        if (playerSender == null || playerSender.isDead() || !playerSender.isOnline())
            return;

        if (!hasRecallRequirements(playerSender, message)) {
            refund(playerSender);
            return;
        }

        MarkPlayer markPlayer = MarkPlayer.wrap(playerSender.getUniqueId());

        MarkPlayer.Mark mark = markPlayer.getAnyMark(message);

        if (mark.getType() == MarkPlayer.Mark.MarkType.RANDOM) {
            while (!(
                    !isObstructed(mark) && (LostShardPlugin.getPlotManager().getStandingOnPlot(mark.getLocation()) == null || isOnPlotFamily(markPlayer.getPlayerUUID(), mark.getLocation()))
            )) {
                mark = markPlayer.getAnyMark(message);

            }


        }

        //wait for time to tp
        recall(playerSender, mark);
    }

    private boolean isOnPlotFamily(UUID uuid, Location location) {
        return LostShardPlugin.getPlotManager().getStandingOnPlot(location) != null
                && LostShardPlugin.getPlotManager().getStandingOnPlot(location) instanceof PlayerPlot
                && (
                ((PlayerPlot) LostShardPlugin.getPlotManager().getStandingOnPlot(location)).isFriend(uuid) ||
                        ((PlayerPlot) LostShardPlugin.getPlotManager().getStandingOnPlot(location)).isJointOwner(uuid) ||
                        ((PlayerPlot) LostShardPlugin.getPlotManager().getStandingOnPlot(location)).isOwner(uuid)
        );


    }


    /**
     *
     */
    private void recall(Player player, MarkPlayer.Mark mark) {

        final int WAITING_TO_RECALL_PERIOD = 3;
        player.sendMessage(ChatColor.GOLD + "You begin to cast to the mark, \"" + mark.getName() + "\"...");
        waitingToRecallMap.put(player.getUniqueId(), WAITING_TO_RECALL_PERIOD);
        mark.getLocation().getChunk().load(true);
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

                    SuccessfulRecallEvent event = new SuccessfulRecallEvent(player);
                    LostShardPlugin.plugin.getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled())
                        return;

                    if (!postCast(player, mark))
                        if (player.isOnline())
                            refund(player);
                    return;
                }

                counter--;
                waitingToRecallMap.put(player.getUniqueId(), counter);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
    }


    /**
     * after the successful tp
     *
     * @return
     */
    private boolean postCast(Player playerSender, MarkPlayer.Mark mark) {

        if (!playerSender.isOnline())
            return false;


        if (isObstructed(mark)) {
            playerSender.sendMessage(ERROR_COLOR + "Cannot recall there. Your mark has been obstructed.");
            return false;
        }
        if (isLapisNearby(mark.getLocation(), DEFAULT_LAPIS_NEARBY)) {
            playerSender.sendMessage(ERROR_COLOR + "You can not seem to cast " + getName() + " there...");
            return false;
        }
        playerSender.teleport(mark.getLocation());
        playerSender.sendMessage(ChatColor.GOLD + "You have recalled to the mark \"" + mark.getName() + "\".");

        if (mark.getType() == MarkPlayer.Mark.MarkType.SPAWN) {
            Stat stat = Stat.wrap(playerSender);
            stat.setStamina(0);
            stat.setMana(0);
            playerSender.sendMessage(ChatColor.GRAY + "Teleporting to spawn has exhausted you.");
        }


        mark.getLocation().getWorld().strikeLightningEffect(mark.getLocation());

        return true;
    }

    private boolean isObstructed(MarkPlayer.Mark mark) {
        Block toFeet = mark.getLocation().getBlock();
        Block toHead = mark.getLocation().getBlock().getRelative(BlockFace.UP);

        return !isAvailable(toFeet) || !isAvailable(toHead);
    }

    private boolean isAvailable(Block block) {
        return HelperMethods.getLookingSet().contains(block.getType()) && HelperMethods.getLookingSet().contains(block.getType());
    }


    /**
     * checks if you are able to create a mark with said name
     *
     * @param playerSender
     * @return
     */
    private boolean hasRecallRequirements(Player playerSender, String name) {
        UUID playerUUID = playerSender.getUniqueId();

        MarkPlayer markPlayer = MarkPlayer.wrap(playerUUID);

        if (!markPlayer.hasMark(name) && !markPlayer.isPremadeMark(name)) {
            playerSender.sendMessage(ERROR_COLOR + "You don't have a mark by this name.");
            return false;
        }

        return true;
    }


}
