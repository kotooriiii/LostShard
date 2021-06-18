package com.github.kotooriiii.sorcery.spells.type.circle7;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.sorcery.spells.KVectorUtils;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import com.github.kotooriiii.sorcery.spells.type.circle6.WaterWalkSpell;
import com.github.kotooriiii.stats.Stat;
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
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class ClanTPSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> clanTpSpellCooldownMap = new HashMap<>();
    private final static HashMap<UUID, Integer> waitingToRecallMap = new HashMap<>();


    private ClanTPSpell() {
        super(SpellType.CLANTP,  "Teleports you to your clan member. Type /cast clantp, and when the pop-up message asks you which clan member you want to teleport to, just type in their name!\n" +
                "Example:\n" +
                "/cast clantp\n" +
                "Nickolov",
                7, ChatColor.GREEN, new ItemStack[]{new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.FEATHER, 1)}, 2.0f, 15, true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.ENDERMAN}, 0.01));
    }

    private  static ClanTPSpell instance;
    public static ClanTPSpell getInstance() {
        if (instance == null) {
            synchronized (ClanTPSpell.class) {
                if (instance == null)
                    instance = new ClanTPSpell();
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
        clanTpSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    clanTpSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                clanTpSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (clanTpSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = clanTpSpellCooldownMap.get(player.getUniqueId());
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
        player.sendMessage(ChatColor.YELLOW + "Who would you like to teleport to?");
        return true;
    }


    /**
     * after the successful tp
     *
     * @return
     */
    private boolean postCast(Player playerSender, UUID clanMemberPlayerUUID, String justInCaseNameTheyLogOut) {

        Player clanMemberPlayer = Bukkit.getPlayer(clanMemberPlayerUUID);

        if (!playerSender.isOnline())
            return false;

        if (clanMemberPlayer == null || !clanMemberPlayer.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "Player has logged out, can not clan teleport to " + justInCaseNameTheyLogOut);
            return false;
        }

        if(isLapisNearby(clanMemberPlayer.getLocation(), DEFAULT_LAPIS_NEARBY))
        {
            playerSender.sendMessage(ERROR_COLOR + "You can not seem to cast " + getName() + " there...");
            return false;
        }

        playerSender.teleport(clanMemberPlayer.getLocation());
        playerSender.sendMessage(ChatColor.GOLD + "You have recalled to \"" + clanMemberPlayer.getName() + "\".");
        clanMemberPlayer.getLocation().getWorld().strikeLightningEffect(clanMemberPlayer.getLocation());
        Stat stat = Stat.wrap(playerSender.getUniqueId());
        stat.setMana(0);
        stat.setStamina(0);

        if(LostShardPlugin.getAnimatorPackage().isAnimating(playerSender.getUniqueId()))
            drawInPlane(clanMemberPlayer.getLocation(), Particle.REDSTONE, 1, 1);
        return true;

    }

    /**
     * recalls a player to a mark
     */
    private void receiveArgument(Player playerSender, String message) {
        if (playerSender == null || playerSender.isDead() || !playerSender.isOnline())
            return;

        if (!hasClanTPRequirements(playerSender, message)) {
            refund(playerSender);
            return;
        }

        Player clanMemberPlayer = Bukkit.getPlayer(message);

        //success
        clantp(playerSender, clanMemberPlayer);
    }


    /**
     *
     */
    private void clantp(Player player, Player clanMemberPlayer) {

        final int WAITING_TO_RECALL_PERIOD = 3;
        player.sendMessage(ChatColor.GOLD + "You begin to clan teleport to \"" + clanMemberPlayer.getName() + "\"...");
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
                    if (!postCast(player, clanMemberPlayer.getUniqueId(), clanMemberPlayer.getName()))
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
     * checks if you are able to teleport to this supposed player
     *
     * @param playerSender
     * @return
     */
    private boolean hasClanTPRequirements(Player playerSender, String name) {

        final UUID playerUUID = playerSender.getUniqueId();

        Clan clan = LostShardPlugin.getClanManager().getClan(playerUUID);

        Player clanMemberPlayer = Bukkit.getPlayer(name);

        if (clan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return false;
        }


        if (clanMemberPlayer == null || !clanMemberPlayer.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "The player is not online.");
            return false;
        }

        if (!clan.isInThisClan(clanMemberPlayer.getUniqueId())) {
            playerSender.sendMessage(ERROR_COLOR + "The player is not in your clan.");
            return false;
        }

        if (Stat.wrap(clanMemberPlayer.getUniqueId()).isPrivate()) {
            playerSender.sendMessage(ChatColor.GOLD + "Can't teleport to that player, they are set to private");
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
                    player.getWorld().spawnParticle(Particle.REDSTONE, spawnLocation0, 2, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(53, 81, 92), 1f));
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
