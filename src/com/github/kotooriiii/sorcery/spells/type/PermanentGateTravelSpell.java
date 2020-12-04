package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.Gate;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.status.Staff;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class PermanentGateTravelSpell extends Spell implements Listener {


    private final static HashMap<UUID, Double> cooldownMap = new HashMap<>();

    private final static HashMap<UUID, Integer> waitingToRecallMap = new HashMap<>();


    public PermanentGateTravelSpell() {
        super(SpellType.PERMANENT_GATE_TRAVEL, ChatColor.DARK_PURPLE, new ItemStack[]{new ItemStack(Material.OBSIDIAN, 1), new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.LAPIS_LAZULI, 1), new ItemStack(Material.STRING, 1)}, 1.0f /*15.0f*/, 30, true, true, false);
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

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onWaitToRecall(EntityDamageEvent event) {
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
        cooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    cooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                cooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (cooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = cooldownMap.get(player.getUniqueId());
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
        player.sendMessage(ChatColor.YELLOW + "What mark would you like to open a permanent gate to?");
        return true;
    }

    /**
     * recalls a player to a mark
     */
    private void receiveArgument(Player playerSender, String message) {
        if (playerSender == null || playerSender.isDead() || !playerSender.isOnline())
            return;

        MarkPlayer.Mark mark = MarkPlayer.wrap(playerSender.getUniqueId()).getMark(message);

        if (!hasGateRequirements(playerSender, message)) {
            refund(playerSender);
            return;
        }

        Gate gate = new Gate(playerSender.getUniqueId(), playerSender.getLocation(), mark.getLocation());


        //wait for time to tp
        gateTravel(playerSender, gate, mark.getName());
    }


    /**
     *
     */
    private void gateTravel(Player player, Gate gate, String name) {

        final int WAITING_TO_RECALL_PERIOD = 3;
        player.sendMessage(ChatColor.GOLD + "You begin to cast Permanent Gate Travel to \"" + name + "\"...")
        ;
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

                    if (!postCast(player, gate)) {
                        if (player.isOnline())
                            refund(player);
                    }
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
    private boolean postCast(Player playerSender, Gate gate) {

        boolean existingGateFrom = LostShardPlugin.getGateManager().isGate(gate.getFrom());
        boolean existingGateTo = LostShardPlugin.getGateManager().isGate(gate.getTo());

        boolean existingGate = existingGateFrom || existingGateTo;

        if (!playerSender.isOnline())
            return false;


        if (isLapisNearby(gate.getFrom(), DEFAULT_LAPIS_NEARBY)) {
            playerSender.sendMessage(ERROR_COLOR + "You can not seem to cast " + getName() + " here...");
            return false;
        }

        if(isLapisNearby(gate.getTo(), DEFAULT_LAPIS_NEARBY))
        {
            playerSender.sendMessage(ERROR_COLOR + "You can not seem to cast " + getName() + " there...");
            return false;
        }

        if (LostShardPlugin.getGateManager().isYourOwnExistingGate(gate) && existingGate) {
            playerSender.sendMessage(ERROR_COLOR + "You've removed your previous gate to this location.");
            LostShardPlugin.getGateManager().deleteExistingGateIfAny(gate);
        } else if (!LostShardPlugin.getGateManager().isYourOwnExistingGate(gate) && existingGate) {
            playerSender.sendMessage(ERROR_COLOR + "A portal has already been set up here by another player.");
            return false;
        }


        if (!gate.isBuildable()) {
            playerSender.sendMessage(ERROR_COLOR + "Cannot gate travel there, the mark has been obstructed.");
            return false;
        } else if (LostShardPlugin.getGateManager().hasGateNearby(gate.getFrom()) || LostShardPlugin.getGateManager().hasGateNearby(gate.getTo())) {
            playerSender.sendMessage(ERROR_COLOR + "There's another gate too close to this one.");
            return false;
        } else if (gate.getFrom().getWorld().equals(gate.getTo().getWorld()) && gate.getFrom().distance(gate.getTo()) <= Gate.PORTAL_DISTANCE) {
            playerSender.sendMessage(ERROR_COLOR + "The gates must be farther than " + Gate.PORTAL_DISTANCE + " blocks away.");
            return false;
        }

        Plot fromPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(gate.getFrom());
        Plot toPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(gate.getTo());

        if (!Staff.isStaff(playerSender.getUniqueId()) && !hasPlotBuildingPerms(playerSender, fromPlot, toPlot))
            return false;


        if (gate.isBuildable()) {
            LostShardPlugin.getGateManager().addGate(gate, true);
            return true;
        }
        return false;
    }

    private boolean hasPlotBuildingPerms(Player player, Plot... plots) {

        for (Plot plot : plots) {
            if (plot != null) {
                if (plot.getType().isStaff()) {
                    if (player.isOnline())
                        player.sendMessage(ERROR_COLOR + "You can't build a gate on staff plots. Verify both portals are not in staff territory!");
                    return false;
                }

                if (plot instanceof PlayerPlot) {
                    PlayerPlot playerPlot = (PlayerPlot) plot;
                    if (!playerPlot.isJointOwner(player.getUniqueId()) && !playerPlot.isOwner(player.getUniqueId())) {
                        player.sendMessage(ERROR_COLOR + "You must be a co-owner or owner of the plot to build a gate. Verify both portals are in plots you can build!");
                        return false;
                    }
                }

            }
        }
        return true;
    }


    /**
     * checks if you are able to create a mark with said name
     *
     * @param playerSender
     * @return
     */
    private boolean hasGateRequirements(Player playerSender, String name) {
        UUID playerUUID = playerSender.getUniqueId();

        MarkPlayer markPlayer = MarkPlayer.wrap(playerUUID);

        if (!markPlayer.hasMark(name)) {
            playerSender.sendMessage(ERROR_COLOR + "You don't have a mark by this name.");
            return false;
        }

        return true;
    }


}

