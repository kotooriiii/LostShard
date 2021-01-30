package com.github.kotooriiii.sorcery.spells.type.circle9;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.type_helpers.GluttonyCake;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.util.HelperMethods;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Cake;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class GluttonySpell extends Spell implements Listener {

    //Cooldown map
    private static HashMap<UUID, Double> gluttonySpellCooldownMap = new HashMap<UUID, Double>();

    //Tracker
    private static HashMap<UUID, GluttonyCake> uuidCakeHashMap = new HashMap<UUID, GluttonyCake>();
    private static HashMap<Location, GluttonyCake> locationCakeHashMap = new HashMap<Location, GluttonyCake>();

    //Constants
    private final static int GLUTTONY_DISTANCE = 15, GLUTTONY_DURATION = 10, GLUTTONY_EAT_DAMAGE = 2;


    private GluttonySpell() {
        super(SpellType.GLUTTONY,
                "Gluttony turns someone into a piece of cake. Cake with the name tag on top of the cake. Lasts 10 seconds. Each bite of the cake takes 1 heart. ",
                9,
                ChatColor.AQUA,
                new ItemStack[]{new ItemStack(Material.DRAGON_EGG, 1), new ItemStack(Material.CAKE, 1)},
                2.0d,
                75,
                true, true, false);
    }


    //todo switch to ur name
    private static GluttonySpell instance;

    public static GluttonySpell getInstance() {
        if (instance == null) {
            synchronized (GluttonySpell.class) {
                if (instance == null)
                    instance = new GluttonySpell();
            }
        }
        return instance;
    }


    @Override
    public boolean executeSpell(Player player) {

        final RayTraceResult result = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), GLUTTONY_DISTANCE, e -> e.getType() == EntityType.PLAYER && !uuidCakeHashMap.containsKey(e.getUniqueId()) && !((LivingEntity) e).hasPotionEffect(PotionEffectType.INVISIBILITY) && e != player);
        final Player hitPlayer = (Player) result.getHitEntity();

        //todo how does it affect invis entities?

        if (result == null || hitPlayer == null) {
            player.sendMessage(ERROR_COLOR + "No player targeted.");
            return false;
        }

        Clan clan = LostShardPlugin.getClanManager().getClan(player.getUniqueId());
        if (clan != null) {
            if (clan.isInThisClan(hitPlayer.getUniqueId())) {
                player.sendMessage(ERROR_COLOR + "You can't eat your family!");
                return false;
            }
        }

        if (uuidCakeHashMap.containsKey(hitPlayer.getUniqueId())) {
            player.sendMessage(ERROR_COLOR + "Player is already a cake.");
            return false;
        }

        cake(hitPlayer, player);


        return true;
    }

    //
    // Cake methods
    //
    private void cake(Player target, Player caster) {
        Hologram hologram = HologramsAPI.createHologram(LostShardPlugin.plugin, target.getLocation().clone().add(0, 1.5f, 0));

        final StatusPlayer targetStatus = StatusPlayer.wrap(target.getUniqueId());

        hologram.appendTextLine(targetStatus.getStatus().getChatColor() + target.getName());

        target.getLocation().getBlock().setType(Material.CAKE);

        GluttonyCake gluttonyCake = new GluttonyCake(target.getLocation().getBlock().getLocation(), hologram, target.getUniqueId(), caster.getUniqueId());

        LostShardPlugin.getCombatLogManager().combat(target, caster);


        target.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * GLUTTONY_DURATION, 1, false, false, false));

        for(Player player : Bukkit.getOnlinePlayers())
        {
            player.hidePlayer(LostShardPlugin.plugin, target);
        }
        target.setSwimming(true);
        target.setCollidable(false);

        locationCakeHashMap.put(target.getLocation().getBlock().getLocation(), gluttonyCake);
        uuidCakeHashMap.put(target.getUniqueId(), gluttonyCake);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (gluttonyCake.isEaten()) {
                    this.cancel();
                    return;
                }

                gluttonyCake.getLocation().getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, gluttonyCake.getLocation(), 2);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * (GLUTTONY_DURATION - 3));


        new BukkitRunnable() {
            @Override
            public void run() {
                if (gluttonyCake.isEaten()) {
                    this.cancel();
                    return;
                }

                cakeBaked(gluttonyCake);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * (GLUTTONY_DURATION - 3));

    }

    private void cakeBaked(GluttonyCake cake) {
        Player cakee = Bukkit.getPlayer(cake.getPlayerIsCakeUUID());

        if(cake.getLocation().getBlock().getType() == Material.CAKE)
        {
            cake.getLocation().getBlock().setType(Material.AIR);
        }

        locationCakeHashMap.remove(cake.getLocation());
        uuidCakeHashMap.remove(cake.getPlayerIsCakeUUID());
        cake.getHologram().delete();


        if (cakee != null) {
            cake.getLocation().getWorld().spawnParticle(Particle.VILLAGER_ANGRY, cake.getLocation(), 5, 1, 1, 1);
            cakee.removePotionEffect(PotionEffectType.INVISIBILITY);
            for(Player player : Bukkit.getOnlinePlayers())
            {
                player.showPlayer(LostShardPlugin.plugin, cakee);
            }
            cakee.setSwimming(true);
            cakee.setCollidable(false);
            HelperMethods.customDamage(cakee, EntityDamageEvent.DamageCause.CUSTOM, cake.getDamage());
            return;
        }


    }


    //
    // Event JoinHiders
    //

    @EventHandler
    public void onQuitShow(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();
        for(UUID uuid : uuidCakeHashMap.keySet())
        {
            final Player player1 = Bukkit.getPlayer(uuid);
            if(player1 == null)
                continue;
            player.showPlayer(LostShardPlugin.plugin, player1);
        }
    }

    @EventHandler
    public void onJoinHide(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        for(UUID uuid : uuidCakeHashMap.keySet())
        {
            final Player player1 = Bukkit.getPlayer(uuid);
            if(player1 == null)
                continue;
            player.hidePlayer(LostShardPlugin.plugin, player1);
        }
    }

    //
    // On Cakee Control
    //
    @EventHandler (priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (CitizensAPI.getNPCRegistry().isNPC(player))
            return;

        final GluttonyCake gluttonyCake = uuidCakeHashMap.get(player.getUniqueId());
        if (gluttonyCake == null)
            return;
        cakeBaked(gluttonyCake);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (CitizensAPI.getNPCRegistry().isNPC(player))
            return;
        if (!uuidCakeHashMap.containsKey(player.getUniqueId()))
            return;
        player.sendMessage(ERROR_COLOR + "Cakes don't move!");
        event.setCancelled(true);
    }

    @EventHandler
    public void onTp(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        if (CitizensAPI.getNPCRegistry().isNPC(player))
            return;
        if (!uuidCakeHashMap.containsKey(player.getUniqueId()))
            return;
        player.sendMessage(ERROR_COLOR + "Cakes don't teleport!");
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (CitizensAPI.getNPCRegistry().isNPC(player))
            return;
        if (!uuidCakeHashMap.containsKey(player.getUniqueId()))
            return;
        player.sendMessage(ERROR_COLOR + "Cakes don't interact!");
        event.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        if (CitizensAPI.getNPCRegistry().isNPC(player))
            return;
        if (!uuidCakeHashMap.containsKey(player.getUniqueId()))
            return;
        if(Staff.isStaff(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "Bypassed CAKE command restrictions because you're staff :) .");
            return;
        }
        if (isAllowedCommands(event.getMessage())) {
            return;
        }
        player.sendMessage(ERROR_COLOR + "Cakes don't type commands!");
        event.setCancelled(true);
    }

    private boolean isAllowedCommands(String message)
    {
        message = message.substring(1).toLowerCase();

        return message.startsWith("msg") || message.startsWith("message")
                || message.startsWith("whisper")  || message.startsWith("w ")  || message.equals("w")
                || message.startsWith("g ") || message.equals("g") || message.startsWith("global ") || message.equals("global") ||
                message.startsWith("l ") || message.equals("l")  || message.startsWith("local")
                || message.startsWith("shout") || message.startsWith("s ") || message.equals("s")
                || message.startsWith("reply") || message.startsWith("r ") || message.equals("r");
    }

    //On Damage Cakee
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (entity == null)
            return;
        if (!(entity instanceof Player))
            return;
        Player player = (Player) entity;
        if (CitizensAPI.getNPCRegistry().isNPC(player))
            return;
        if (!uuidCakeHashMap.containsKey(player.getUniqueId()))
            return;
        event.setCancelled(true);
    }

    //
    // On Eat
    //

    @EventHandler(ignoreCancelled = true)
    public void onEatCake(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        //todo check empty hand?
//        if (!(event.getItem() == null || event.getItem().getType().isAir()))
//            return;
        final Block cakeBlock = event.getClickedBlock();
        if (cakeBlock == null || cakeBlock.getType().isAir())
            return;
        if (cakeBlock.getType() != Material.CAKE)
            return;
        final GluttonyCake gluttonyCake = locationCakeHashMap.get(cakeBlock.getLocation());
        if (gluttonyCake == null)
            return;

        Cake cakeBlockData = (Cake) cakeBlock.getBlockData();
        if (cakeBlockData.getBites() < cakeBlockData.getMaximumBites()) {
            cakeBlockData.setBites(cakeBlockData.getBites() + 1);

            if (cakeBlockData.getBites() + 1 == cakeBlockData.getMaximumBites()) {
                cakeBlock.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, cakeBlock.getLocation(), 4, 0, 0.2f, 0);
                gluttonyCake.setEaten(true);
                cakeBaked(gluttonyCake);
            }
        }

        final int foodLevel = event.getPlayer().getFoodLevel();
        final int totalFoodLevel = foodLevel + 2;
        if (totalFoodLevel <= 20) {
            event.getPlayer().setFoodLevel(totalFoodLevel);
        }

        cakeBlock.getWorld().playSound(cakeBlock.getLocation(), Sound.ENTITY_PLAYER_BURP, 4.0f, 3f);

        cakeBlock.setBlockData(cakeBlockData);

        final Player eater = event.getPlayer();
        final Player eaten = Bukkit.getPlayer(gluttonyCake.getPlayerIsCakeUUID());
        if (eaten != null)
            LostShardPlugin.getCombatLogManager().combat(eaten, eater);

        gluttonyCake.addDamageDefault();
        event.setCancelled(true);
    }

    //
    // On Cake Break
    //

    @EventHandler
    public void onEatCake(BlockBreakEvent event) {
        Block cakeBlock = event.getBlock();
        if (cakeBlock == null || cakeBlock.getType().isAir())
            return;
        if (cakeBlock.getType() != Material.CAKE)
            return;
        final GluttonyCake gluttonyCake = locationCakeHashMap.get(cakeBlock.getLocation());
        if (gluttonyCake == null)
            return;

        final UUID playerIsCakeUUID = gluttonyCake.getPlayerIsCakeUUID();
        final Player player = Bukkit.getPlayer(playerIsCakeUUID);
        if (player == null) {
            event.getPlayer().sendMessage(ERROR_COLOR + "You can't break this block right now. Someone is the cake!");

        } else {
            event.getPlayer().sendMessage(ERROR_COLOR + "You can't break this block right now. " + player.getName() + " is the cake!");
        }
        event.setCancelled(true);
    }

    /**
     * Called when a block explodes and is inside a plot.
     *
     * @param event
     */
    @EventHandler
    public void onExplosion(BlockExplodeEvent event) {
        List<Block> blocksExploding = event.blockList();
        for (Block block : blocksExploding) {
            Location loc = block.getLocation();
            Block cakeBlock = loc.getBlock();
            if (cakeBlock == null || cakeBlock.getType().isAir())
                return;
            if (cakeBlock.getType() != Material.CAKE)
                return;
            final GluttonyCake gluttonyCake = locationCakeHashMap.get(cakeBlock.getLocation());
            if (gluttonyCake == null)
                return;

            event.setCancelled(true);
            break;
        }
    }

    /**
     * Called when an entity explodes and blocks which should be removed are inside a plot.
     *
     * @param event
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocksExploding = event.blockList();
        for (Block block : blocksExploding) {
            Location loc = block.getLocation();
            Block cakeBlock = loc.getBlock();
            if (cakeBlock == null || cakeBlock.getType().isAir())
                return;
            if (cakeBlock.getType() != Material.CAKE)
                return;
            final GluttonyCake gluttonyCake = locationCakeHashMap.get(cakeBlock.getLocation());
            if (gluttonyCake == null)
                return;

            event.setCancelled(true);
            break;
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {

        final Block pistonBlock = event.getBlock();
        final List<Block> blocks = event.getBlocks();
        final BlockFace directionMoved = event.getDirection();


        final Iterator<Block> iterator = blocks.iterator();

        blockLoop:
        while (iterator.hasNext()) {
            Block blockWhichWillBeMoved = iterator.next();
            Block movedBlock = blockWhichWillBeMoved.getRelative(directionMoved);

            if (movedBlock == null || movedBlock.getType().isAir())
                return;
            if (movedBlock.getType() != Material.CAKE)
                return;
            final GluttonyCake gluttonyCake = locationCakeHashMap.get(movedBlock.getLocation());
            if (gluttonyCake == null)
                return;

            event.setCancelled(true);
            return;

        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {

        final Block pistonBlock = event.getBlock();
        final List<Block> blocks = event.getBlocks();
        final BlockFace directionMoved = event.getDirection();

        final Iterator<Block> iterator = blocks.iterator();

        blockLoop:
        while (iterator.hasNext()) {
            Block blockWhichWillBeMoved = iterator.next();

            if (blockWhichWillBeMoved == null || blockWhichWillBeMoved.getType().isAir())
                return;
            if (blockWhichWillBeMoved.getType() != Material.CAKE)
                return;
            final GluttonyCake gluttonyCake = locationCakeHashMap.get(blockWhichWillBeMoved.getLocation());
            if (gluttonyCake == null)
                return;

            event.setCancelled(true);
            return;
        }
    }


    //
    // Cooldowns
    //

    @Override
    public void updateCooldown(Player player) {
        gluttonySpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    gluttonySpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                gluttonySpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (gluttonySpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = gluttonySpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(1, RoundingMode.HALF_UP);
            float value = bd.floatValue();
            if (value == 0)
                value = 0.1f;

            String time = "seconds";
            if (value <= 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }

    //
    // Getters
    //

    public static int getGluttonyEatDamage() {
        return GLUTTONY_EAT_DAMAGE;
    }

    public static HashMap<Location, GluttonyCake> getLocationCakeHashMap() {
        return locationCakeHashMap;
    }

    public static HashMap<UUID, GluttonyCake> getUuidCakeHashMap() {
        return uuidCakeHashMap;
    }

    public static boolean isCake(UUID uuid)
    {
        return uuidCakeHashMap.containsKey(uuid);
    }
}
