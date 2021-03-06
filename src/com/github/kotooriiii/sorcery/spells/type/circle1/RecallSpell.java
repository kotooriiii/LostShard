package com.github.kotooriiii.sorcery.spells.type.circle1;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.sorcery.events.SuccessfulRecallEvent;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.spells.KVectorUtils;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class RecallSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> recallSpellCooldownMap = new HashMap<>();

    private final static HashMap<UUID, Integer> waitingToRecallMap = new HashMap<>();


    private RecallSpell() {
        super(SpellType.RECALL, "Recall back to the mark you created. Type /cast recall, and when the pop-up message asks you which mark you want to recall back to, type in the name of the mark, such as “home”. If you've forgotten the name of your mark, type /marks.\n" +
                "Example:\n" +
                "/cast recall\n" +
                "home",
                1, ChatColor.BLUE, new ItemStack[]{new ItemStack(Material.FEATHER, 1)}, 2.0f, 15, true, true, false,
                new SpellMonsterDrop(new EntityType[]{}, 0.00));

    }

    private  static RecallSpell instance;
    public static RecallSpell getInstance() {
        if (instance == null) {
            synchronized (RecallSpell.class) {
                if (instance == null)
                    instance = new RecallSpell();
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

        if(LostShardPlugin.getAnimatorPackage().isAnimating(playerSender.getUniqueId()))
        drawInPlane(mark.getLocation().clone().add(0,-1,0), Particle.REDSTONE, 1, 1);

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

    public void drawInPlane(Location location, Particle particle, double chance, double distance) {

        if (Math.random() < chance && particle != Particle.REDSTONE)
            return;

        // We will use these for drawing our parametric curve on the plane:
        double twopi = 2 * Math.PI;
        double times = 1 * twopi;
        double division = twopi / 24;

        //This is how far away we want the plane's origin to be:
        double radius = 1d;

        //Get the normal vector to the plane, nv:
        Location c = location;
        Vector nv = new Vector(0.0000000001f, 1, 0.0000000001f); // c.getDirection().normalize();

        // Coordinates where we want the origin to appear
        double nx = radius * nv.getX() + c.getX();
        double ny = radius * nv.getY() + c.getY();
        double nz = radius * nv.getZ() + c.getZ();

        // Get your basis vectors for the plane
        Vector ya = KVectorUtils.perp(nv, new Vector(0, 1, 0)).normalize();
        Vector xa = ya.getCrossProduct(nv).normalize();

        //nv.multiply(-1);

        // For loop for your parametric equation
        for (double theta = chance == 1 ? 0 : Math.random() * twopi; theta < times; theta += division) {


            // Coordinates with respect to our basis
            double xb = distance * Math.cos(theta); //calculate x coordinate
            double yb = distance * Math.sin(theta); //calculate y coordinate

            // Multiply the transformation matrix with our coordinates for the change of basis
            double xi = xa.getX() * xb + ya.getX() * yb + nv.getX();
            double yi = xa.getY() * xb + ya.getY() * yb + nv.getY();
            double zi = xa.getZ() * xb + ya.getZ() * yb + nv.getZ();

            // Translate the coordinates in front of the player
            double x = xi + nx;
            double y = yi + ny;
            double z = zi + nz;

//
//            // 6 = RED
//            double ran = 6d / 24d;

            Location spawnLocation0 = new Location(c.getWorld(), x, y, z);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (particle == Particle.REDSTONE)
                    player.getWorld().spawnParticle(Particle.REDSTONE, spawnLocation0, 2, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(255, 250, 250), 1f));
                else {
                    player.getWorld().spawnParticle(particle, spawnLocation0, 2);
                }

            }
            if (chance != 1)
                break;


            //player.spawnParticle(Particle.NOTE, new Location(c.getWorld(), x, y, z), 0, ran, 0, 0, 1);
        }

    }


}
