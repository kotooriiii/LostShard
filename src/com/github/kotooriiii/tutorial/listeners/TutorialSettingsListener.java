package com.github.kotooriiii.tutorial.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.commands.HealCommand;
import com.github.kotooriiii.events.BindEvent;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.hostility.events.PlatformCaptureEvent;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.events.PlotCreateEvent;
import com.github.kotooriiii.plots.events.PlotDepositEvent;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.tutorial.events.TutorialPlayerDeathEvent;
import com.github.kotooriiii.tutorial.TutorialBook;
import com.github.kotooriiii.tutorial.TutorialCompleteType;
import com.github.kotooriiii.tutorial.TutorialManager;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class TutorialSettingsListener implements Listener {

    @EventHandler
    public void onDeposit(PlotDepositEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlotCreate2(PlotCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBurn(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FIRE && event.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK)
            return;
        if (event.getEntity() != null && !event.getEntity().isDead() && (event.getEntity().getType() == EntityType.SKELETON || event.getEntity().getType() == EntityType.PHANTOM)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().substring(1).toLowerCase();
        if (cmd.startsWith("suicide") || cmd.startsWith("kill")) {
           // event.getPlayer().damage(20.0f);
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoinA(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final TutorialManager tutorialManager = LostShardPlugin.getTutorialManager();
        event.setJoinMessage(null);
        if (tutorialManager.hasTutorial(player.getUniqueId()))
            return;
        tutorialManager.addTutorial(player.getUniqueId());
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final TutorialManager tutorialManager = LostShardPlugin.getTutorialManager();
        if (tutorialManager.isRestartWhenLoggedOff()) {
            player.teleport(player.getWorld().getSpawnLocation());
            player.getInventory().clear();
            player.updateInventory();
            player.setGameMode(GameMode.SURVIVAL);
            HealCommand.heal(player, false);
        }
    }

    @EventHandler
    public void onLogOff(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final TutorialManager tutorialManager = LostShardPlugin.getTutorialManager();
        event.setQuitMessage(null);
        if (tutorialManager.isRestartWhenLoggedOff()) {
            tutorialManager.removeTutorial(player.getUniqueId(), TutorialCompleteType.RESET);
        }
        LostShardPlugin.getBankManager().wrap(player.getUniqueId()).setCurrency(0.0f);
        for (Plot plot : ShardPlotPlayer.wrap(player.getUniqueId()).getPlotsOwned())
            LostShardPlugin.getPlotManager().removePlot(plot);
        MarkPlayer markPlayer = MarkPlayer.wrap(event.getPlayer().getUniqueId());
        if (markPlayer != null)
            markPlayer.remove();
        Clan clan = LostShardPlugin.getClanManager().getClan(player.getUniqueId());
        if (clan != null) {
            clan.forceDisband();
            LostShardPlugin.getClanManager().removeClan(clan);
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageByEntityEvent ev) //Listens to EntityDamageEvent
    {
        if (CitizensAPI.getNPCRegistry().isNPC(ev.getDamager()))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(ev.getEntity()))
            return;

        if (ev.getEntity() instanceof Player && ev.getDamager() instanceof Player) {
            ev.setCancelled(true);
            return;
        }

        if (ev.getEntity() instanceof Player && ev.getDamager() instanceof AbstractArrow) {
            if (((AbstractArrow) ev.getDamager()).getShooter() instanceof Player) {
                ev.setCancelled(true);
                return;
            }
        }

        if (ev.getEntity().getType() == EntityType.PIG && ev.getDamager() instanceof Player) {
            ((Player) ev.getDamager()).damage(5);
            ev.getDamager().setFireTicks(40);
            ev.getDamager().getWorld().spawnParticle(Particle.VILLAGER_ANGRY, ev.getEntity().getLocation(), 10, 0, 0, 0);
            ev.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageEvent ev) //Listens to EntityDamageEvent
    {

        if (CitizensAPI.getNPCRegistry().isNPC(ev.getEntity()))
            return;

        if (!(ev.getEntity() instanceof Player))
            return;

        Player player = (Player) ev.getEntity();
        if (player.getHealth() - ev.getFinalDamage() <= 0) {
            TutorialBook book = LostShardPlugin.getTutorialManager().wrap(player.getUniqueId());
            if (book == null)
                return;

            LostShardPlugin.plugin.getServer().getPluginManager().callEvent(new TutorialPlayerDeathEvent(player));

            //Cancel damage event
            ev.setCancelled(true);

            //Set back to living
            player.teleport(book.getCurrentChapter().getLocation());

            if (book.getCurrentChapter().isUsingHeal())
                HealCommand.heal(player, false);
            else
            {
                player.setHealth(book.getCurrentChapter().getDefaultHealth());
                player.setFoodLevel(book.getCurrentChapter().getDefaultFoodLevel());
            }

            for (PotionEffectType potionEffectType : PotionEffectType.values())
                player.removePotionEffect(potionEffectType);
        }

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPortal(PlayerPortalEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (CitizensAPI.getNPCRegistry().isNPC(entity))
            return;


        if (event.getEntityType() == EntityType.SPIDER || event.getEntityType() == EntityType.SKELETON || event.getEntityType() == EntityType.PHANTOM || event.getEntityType() == EntityType.ZOMBIE) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity != null && entity.getCustomName() != null && !entity.isDead() && entity.getCustomName().startsWith("[Tutorial]"))
                        return;
                    entity.teleport(new Location(entity.getWorld(), -1000, 0, -1000), PlayerTeleportEvent.TeleportCause.UNKNOWN);
                    entity.damage(1000.0f);
                    this.cancel();
                    return;
                }
            }.runTask(LostShardPlugin.plugin);
            return;
        }
        event.setCancelled(true);


    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawn(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        if (event.getInventory().getType() != InventoryType.CHEST)
            return;
        if (event.getView().getTitle().equalsIgnoreCase(Bank.NAME))
            return;
        Player player = (Player) event.getPlayer();
        TutorialBook book = LostShardPlugin.getTutorialManager().wrap(player.getUniqueId());
        if (book == null)
            return;

        boolean containsOpenEvent = false;
        all:
        for (Method method : book.getCurrentChapter().getClass().getMethods()) {
            for (Class<?> clazz : method.getParameterTypes()) {
                if (clazz.equals(InventoryOpenEvent.class)) {
                    containsOpenEvent = true;
                    break all;
                }
            }
        }

        if (containsOpenEvent)
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlotCreate(PlotCreateEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        Player player = (Player) event.getPlayer();
        TutorialBook book = LostShardPlugin.getTutorialManager().wrap(player.getUniqueId());
        if (book == null)
            return;

        boolean containsPlotCreateEvent = false;
        all:
        for (Method method : book.getCurrentChapter().getClass().getMethods()) {
            for (Class<?> clazz : method.getParameterTypes()) {
                if (clazz.equals(PlotCreateEvent.class)) {
                    containsPlotCreateEvent = true;
                    break all;
                }
            }
        }

        if (containsPlotCreateEvent)
            return;
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryOpenFurnace(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        if (event.getInventory().getType() == InventoryType.FURNACE)
            event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBinding(BindEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        Player player = (Player) event.getPlayer();
        TutorialBook book = LostShardPlugin.getTutorialManager().wrap(player.getUniqueId());
        if (book == null)
            return;

        boolean containsBindEvent = false;
        all:
        for (Method method : book.getCurrentChapter().getClass().getMethods()) {
            for (Class<?> clazz : method.getParameterTypes()) {
                if (clazz.equals(BindEvent.class)) {
                    containsBindEvent = true;
                    break all;
                }
            }
        }

        if (containsBindEvent)
            return;

        player.sendMessage(ChatColor.RED + "Now is not the time to bind a spell to your stick...");
        event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBinding(PlatformCaptureEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        Player player = (Player) event.getPlayer();
        TutorialBook book = LostShardPlugin.getTutorialManager().wrap(player.getUniqueId());
        if (book == null)
            return;

        boolean containsEv = false;
        all:
        for (Method method : book.getCurrentChapter().getClass().getMethods()) {
            for (Class<?> clazz : method.getParameterTypes()) {
                if (clazz.equals(PlatformCaptureEvent.class)) {
                    containsEv = true;
                    break all;
                }
            }
        }

        if (containsEv)
            return;

        event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {

        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL)
            return;

        class Pair {
            private Zone z;
            private Material[] mats;
            private int seconds;

            public Pair(Zone z, Material[] mat, int seconds) {
                this.z = z;
                this.mats = mat;
                this.seconds = seconds;
            }

            public Material[] getMats() {
                return mats;
            }

            public int getSeconds() {
                return seconds;
            }

            public Zone getZ() {
                return z;
            }
        }

        Pair[] pairs = new Pair[]{
                new Pair(new Zone(360, 280, 38, 102, 723, 690), new Material[]{Material.IRON_ORE, Material.GOLD_ORE}, 7),
                new Pair(new Zone(392, 371, 38, 41, 719, 723), new Material[]{Material.IRON_ORE, Material.GOLD_ORE, Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE}, 30),
                new Pair(new Zone(403, 421, 38, 40, 722, 719), new Material[]{Material.IRON_ORE, Material.GOLD_ORE, Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE}, 30),
                new Pair(new Zone(746, 691, 48, 75, 1061, 1131), new Material[]{Material.MELON}, 5)
        };

        final Material type = event.getBlock().getType();

        boolean exists = false;
        int seconds = 0;


        out:
        for (Pair pair : pairs) {
            Zone z = pair.getZ();
            Material[] mats = pair.getMats();
            if (z.contains(event.getBlock().getLocation())) {
                for (Material m : mats) {
                    if (m == type) {
                        exists = true;
                        seconds = pair.getSeconds();
                        break out;
                    }
                }
            }

            if (z.hasAdjacency(event.getBlock().getLocation()))
                event.getPlayer().sendMessage(ERROR_COLOR + "Don't mine this way! You are going the wrong direction.");
        }


        if (!exists) {
            event.setCancelled(true);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getBlock().setType(type);
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * seconds);
    }

    @EventHandler
    public void onGrav(EntityChangeBlockEvent event) {
        Block b = event.getBlock();
        final Material mat = event.getTo();
        Entity e = event.getEntity();


        if (!(e instanceof FallingBlock)) {
            return;
        }

        if (mat != Material.SAND && mat != Material.GRAVEL) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getBlock().getType() == Material.AIR) {
                    event.getBlock().setType(mat);
                }
                this.cancel();
                return;
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 15);


    }

    @EventHandler
    public void onSwitchLever(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.LEVER && event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTP(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(entity))
            return;
        Player player = (Player) entity;
        if (player.getGameMode() != GameMode.SURVIVAL)
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION)
            return;
        HealCommand.heal(player, false);
        TutorialBook book = LostShardPlugin.getTutorialManager().wrap(player.getUniqueId());
        if (book == null)
            return;
        if (book.getCurrentChapter() == null)
            return;
        player.teleport(book.getCurrentChapter().getLocation());


    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockDropItemEvent event) {
        if (drop(event.getItems(), Material.IRON_ORE, Material.IRON_INGOT) || drop(event.getItems(), Material.GOLD_ORE, Material.GOLD_INGOT))
            event.setCancelled(true);
    }

    private boolean drop(List<Item> droppedItems, Material ore, Material ingot) {

        boolean hasOre = false;
        Location betterLocation = null;

        for (Item item : droppedItems) {
            if (item.getItemStack().getType().equals(ore)) {
                hasOre = true;
                betterLocation = item.getLocation();
                break;
            }
        }

        if (!hasOre)
            return false;

        betterLocation.getWorld().dropItemNaturally(betterLocation, new ItemStack(ingot, 2));
        return true;
    }

    @EventHandler
    public void onInteractLeash(PlayerInteractEntityEvent event) {
        Entity hit = event.getRightClicked();
        Entity remover = event.getPlayer();
        if (hit.getType() == EntityType.LEASH_HITCH && remover instanceof Player && ((Player) remover).getGameMode() == GameMode.SURVIVAL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onLeashHit(HangingBreakByEntityEvent event) {
        Entity hit = event.getEntity();
        Entity remover = event.getRemover();
        if (hit.getType() == EntityType.LEASH_HITCH && remover instanceof Player && ((Player) remover).getGameMode() == GameMode.SURVIVAL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onLeashHit(HangingBreakEvent event) {
        Entity hit = event.getEntity();
        if (event.getCause() != HangingBreakEvent.RemoveCause.ENTITY)
            event.setCancelled(true);
    }

    @EventHandler
    public void itemDmg(PlayerItemDamageEvent event) {

        int dmg = event.getDamage() + 4;

        if (event.getItem().getType().getMaxDurability() <= dmg) {
            event.setDamage(0);
        } else
            event.setDamage(dmg);
    }
}
