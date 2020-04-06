package com.github.kotooriiii.skills.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.SkillPlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.util.HelperMethods.*;
import static com.github.kotooriiii.util.HelperMethods.getPlayerDamagerONLY;

public class SurvivalismListener implements Listener {

    public static class Campfire {
        private Location location;
        private UUID ownerUUID;
        private BukkitTask task;
        private boolean isDestroyed;

        private final int HEALING = 1; //half hearts
        private final int DURATION_TICKS = 15 * 20; //seconds

        public static final float MANA_COST = 40;
        public static final int LEVEL = 25;
        public static final int RANGE = 5;

        private final int RADIUS = 3;
        private final int HEIGHT_Y = 1;


        private static HashMap<UUID, Campfire> campfireHashMap = new HashMap<>();

        public Campfire(UUID ownerUUID, Location location) {
            this.ownerUUID = ownerUUID;
            this.location = location;
            isDestroyed = false;
        }

        public Player[] getCampers() {
            Location boundingBox = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + HEIGHT_Y, location.getBlockZ());
            ArrayList<Player> players = new ArrayList<>();
            for (Entity entity : Bukkit.getWorld("world").getNearbyEntities(boundingBox, RADIUS, HEIGHT_Y, RADIUS)) {
                if (!(entity instanceof Player))
                    continue;

                Player player = (Player) entity;
                players.add(player);
            }
            return players.toArray(new Player[players.size()]);
        }

        public Block getFireBlock() {
            return new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + 1, location.getBlockZ()).getBlock();
        }

        public Block getLog() {
            return location.getBlock();
        }

        public boolean isSpawnable() {
            if (getLog().getType() != Material.AIR)
                return false;
            if (getFireBlock().getType() != Material.AIR)
                return false;
            return true;
        }

        public void spawn() {

            campfireHashMap.put(this.ownerUUID, this);

            getLog().setType(Material.OAK_LOG); //set log
            getFireBlock().setType(Material.FIRE); //set fire

            task = new BukkitRunnable() {
                private final int max = DURATION_TICKS;
                private int counter = 0;

                @Override
                public void run() {

                    if (isCancelled() || isDestroyed) {
                        this.cancel();
                        return;
                    }

                    if (getFireBlock().getType() != Material.FIRE) {
                        this.cancel();
                        return;
                    }

                    if (counter >= max) {
                        //Timer ran out duration
                        destroy();
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(ownerUUID);
                        if(offlinePlayer.isOnline())
                            offlinePlayer.getPlayer().sendMessage(ChatColor.GRAY + "Your camp fire has gone out.");
                        this.cancel();
                        return;
                    }

                    //heal campers

                    for (Player player : getCampers()) {

                        if (player.getHealth() + HEALING >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
                            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                        else
                            player.setHealth(player.getHealth() + HEALING);

                    }
                    //timer
                    counter += 20;
                }
            }.runTaskTimer(LostShardPlugin.plugin, 20, 20);
        }

        public void destroy() {
            isDestroyed = true;
            campfireHashMap.remove(this.ownerUUID);

            if (getLog().getType() == Material.OAK_LOG)
                getLog().setType(Material.AIR);

            if (getFireBlock().getType() == Material.FIRE)
                getFireBlock().setType(Material.AIR);

        }

        public static HashMap<UUID, Campfire> getCampfires() {
            return campfireHashMap;
        }

        public static Campfire wrap(UUID playerUUID) {
            return campfireHashMap.get(playerUUID);
        }

        public static boolean hasCampfire(UUID playerUUID) {
            return campfireHashMap.get(playerUUID) != null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPPlayerDeath(PlayerDeathEvent event) {
        Player defenderPlayer = event.getEntity();

        EntityDamageEvent damagerCause = defenderPlayer.getLastDamageCause();
        if (damagerCause == null)
            return;

        Entity damager = damagerCause.getEntity();

        if (damager == null)
            return;

        if (!isPlayerInduced(defenderPlayer, damager))
            return;

        Player damagerPlayer = getPlayerInduced(defenderPlayer, damager);
        //DefenderPlayer already defined

        //
        //The code for each skill will follow on the bottom
        //

        return;
        //addXP(damagerPlayer, defenderPlayer); players dont count as far as im concernced
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPEntityDeath(EntityDeathEvent event) {
        Entity defenderEntity = event.getEntity();

        EntityDamageEvent damagerCause = defenderEntity.getLastDamageCause();
        if (damagerCause == null)
            return;

        Entity damagerEntity = damagerCause.getEntity();

        if (damagerEntity == null)
            return;

        if (!isPlayerDamagerONLY(defenderEntity, damagerEntity))
            return;

        Player damagerPlayer = getPlayerDamagerONLY(defenderEntity, damagerEntity);
        //DamagerEntity already defined

        //
        //The code for each skill will follow on the bottom
        //

        addXP(damagerPlayer, defenderEntity);

    }

    private boolean addXP(Player player, Entity entity) {
        return SkillPlayer.wrap(player.getUniqueId()).getSurvivalism().addXP(getXP(entity));
    }

    private float getXP(Entity entity) {

        switch (entity.getType()) {

            case DROPPED_ITEM:
            case EXPERIENCE_ORB:
            case AREA_EFFECT_CLOUD:
            case EGG:
            case LEASH_HITCH:
            case PAINTING:
            case ARROW:
            case SNOWBALL:
            case FIREBALL:
            case SMALL_FIREBALL:
            case ENDER_PEARL:
            case ENDER_SIGNAL:
            case SPLASH_POTION:
            case THROWN_EXP_BOTTLE:
            case ITEM_FRAME:
            case WITHER_SKULL:
            case PRIMED_TNT:
            case FALLING_BLOCK:
            case FIREWORK:
            case SPECTRAL_ARROW:
            case SHULKER_BULLET:
            case DRAGON_FIREBALL:
            case ARMOR_STAND:
            case UNKNOWN:
            case MINECART_COMMAND:
            case MINECART:
            case MINECART_CHEST:
            case MINECART_FURNACE:
            case MINECART_TNT:
            case MINECART_HOPPER:
            case MINECART_MOB_SPAWNER:
            case EVOKER_FANGS:
            case BOAT:
            case FISHING_HOOK:
            case TRIDENT:
            case ENDER_CRYSTAL:
            case LLAMA_SPIT:
            case LIGHTNING:
            default:
                return 0;

            case ELDER_GUARDIAN:
                break;
            case WITHER_SKELETON:
                break;
            case STRAY:
                break;
            case HUSK:
                break;
            case ZOMBIE_VILLAGER:
                break;
            case SKELETON_HORSE:
                break;
            case ZOMBIE_HORSE:
                break;
            case DONKEY:
                break;
            case MULE:
                break;
            case EVOKER:
                break;
            case VEX:
                break;
            case VINDICATOR:
                break;
            case ILLUSIONER:
                break;
            case CREEPER:
                break;
            case SKELETON:
                break;
            case SPIDER:
                break;
            case GIANT:
                break;
            case ZOMBIE:
                break;
            case SLIME:
                break;
            case GHAST:
                break;
            case PIG_ZOMBIE:
                break;
            case ENDERMAN:
                break;
            case CAVE_SPIDER:
                break;
            case SILVERFISH:
                break;
            case BLAZE:
                break;
            case MAGMA_CUBE:
                break;
            case ENDER_DRAGON:
                break;
            case WITHER:
                break;
            case BAT:
                break;
            case WITCH:
                break;
            case ENDERMITE:
                break;
            case GUARDIAN:
                break;
            case SHULKER:
                break;
            case PIG:
                break;
            case SHEEP:
                break;
            case COW:
                break;
            case CHICKEN:
                break;
            case SQUID:
                break;
            case WOLF:
                break;
            case MUSHROOM_COW:
                break;
            case SNOWMAN:
                break;
            case OCELOT:
                break;
            case IRON_GOLEM:
                break;
            case HORSE:
                break;
            case RABBIT:
                break;
            case POLAR_BEAR:
                break;
            case LLAMA:
                break;
            case PARROT:
                break;
            case VILLAGER:
                break;
            case TURTLE:
                break;
            case PHANTOM:
                break;
            case COD:
                break;
            case SALMON:
                break;
            case PUFFERFISH:
                break;
            case TROPICAL_FISH:
                break;
            case DROWNED:
                break;
            case DOLPHIN:
                break;
            case CAT:
                break;
            case PANDA:
                break;
            case PILLAGER:
                break;
            case RAVAGER:
                break;
            case TRADER_LLAMA:
                break;
            case WANDERING_TRADER:
                break;
            case FOX:
                break;
            case BEE:
                break;
            case PLAYER:
                break;

        }

        return 10;
    }
}
