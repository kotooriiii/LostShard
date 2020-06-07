package com.github.kotooriiii.skills.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.util.HelperMethods.*;

public class SwordsmanshipListener implements Listener {
    private static HashMap<UUID, Object[]> bleedingMap = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPHurtPlayer(EntityDamageByEntityEvent event) {

        if (event.isCancelled())
            return;

        Entity damager = event.getDamager();
        Entity defender = event.getEntity();

        if (!isPlayerInduced(defender, damager))
            return;

        //Entties are players
        Player damagerPlayer = getPlayerInduced(defender, damager);
        Player defenderPlayer = (Player) defender;

        //
        //The code for each skill will follow on the bottom
        //

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || !(damager instanceof Player))
            return;
        if (!HelperMethods.isCarryingSword(damagerPlayer))
            return;

        addXP(damagerPlayer, defenderPlayer);
        applyLevelBonus(damagerPlayer, defenderPlayer, event);

    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPHurtEntity(EntityDamageByEntityEvent event) {

        if (event.isCancelled())
            return;

        Entity damager = event.getDamager();
        Entity defender = event.getEntity();

        if (!HelperMethods.isPlayerDamagerONLY(defender, damager))
            return;
        //Entties are players
        Player damagerPlayer = getPlayerDamagerONLY(defender, damager);
        Entity defenderEntity = defender;

        //
        //The code for each skill will follow on the bottom
        //

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || !(damager instanceof Player))
            return;
        if (!HelperMethods.isCarryingSword(damagerPlayer))
            return;
        addXP(damagerPlayer, defenderEntity);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPPlayerDeath(PlayerDeathEvent event) {

        Player defenderPlayer = event.getEntity();

        EntityDamageEvent damagerCause = defenderPlayer.getLastDamageCause();
        if (damagerCause == null || !(damagerCause instanceof EntityDamageByEntityEvent))
            return;

        EntityDamageByEntityEvent betterDamageCause = (EntityDamageByEntityEvent) damagerCause;
        Entity damager = betterDamageCause.getEntity();

        if (damager == null)
            return;

        if (!isPlayerInduced(defenderPlayer, damager))
            return;

        Player damagerPlayer = getPlayerInduced(defenderPlayer, damager);
        //DefenderPlayer already defined

        //
        //The code for each skill will follow on the bottom
        //
        if (!damagerCause.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || !(damager instanceof Player))
            return;
        if (!HelperMethods.isCarryingSword(damagerPlayer))
            return;

        addXP(damagerPlayer, defenderPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPEntityDeath(EntityDeathEvent event) {
        Entity defenderEntity = event.getEntity();

        EntityDamageEvent damagerCause = defenderEntity.getLastDamageCause();
        if (damagerCause == null || !(damagerCause instanceof EntityDamageByEntityEvent))
            return;

        EntityDamageByEntityEvent betterDamageCause = (EntityDamageByEntityEvent) damagerCause;

        Entity damagerEntity = betterDamageCause.getEntity();

        if (damagerEntity == null)
            return;


        if (!isPlayerDamagerONLY(defenderEntity, damagerEntity))
            return;

        Player damagerPlayer = getPlayerDamagerONLY(defenderEntity, damagerEntity);
        //DamagerEntity already defined

        //
        //The code for each skill will follow on the bottom
        //
        if (!damagerCause.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || !(damagerEntity instanceof Player))
            return;
        if (!HelperMethods.isCarryingSword(damagerPlayer))
            return;
        addXP(damagerPlayer, defenderEntity);

    }


    private void applyBleed(Player damager, Entity defender, double chance) {
        if (!(defender instanceof Player))
            return;
        double randomValue = Math.random();

        if (randomValue <= chance) {

            damager.sendMessage(ChatColor.GREEN + defender.getName() + " is bleeding!");
            ((Player) defender).sendMessage(ChatColor.GREEN + "You are bleeding!");

            //Remove in case old entry
            if (getBleedingMap().containsKey(defender.getUniqueId())) {
                Object[] props = getBleedingMap().get(defender.getUniqueId());
                ((BukkitTask) props[0]).cancel();
                getBleedingMap().remove(defender.getUniqueId());

            }

            //Stun timer
            final int stunTimer = 20 * 5;

            //Properties of object
            Object[] properties = new Object[]{null, new Double(stunTimer)};

            //Cancel timer
            properties[0] = new BukkitRunnable() {
                final UUID playerUUID = defender.getUniqueId();
                final int timer = stunTimer;
                int counter = 0;

                @Override
                public void run() {

                    if (isCancelled())
                        return;

                    if (counter >= timer) {
                        getBleedingMap().remove(playerUUID);
                        ((Player) defender).sendMessage(ChatColor.GRAY + "Your bleeding has stopped.");
                        this.cancel();
                        return;
                    }

                    if (!isBleeding(playerUUID)) {
                        this.cancel();
                        return;
                    }

                    //Damage by half-heart

                    double newHP = ((Player) defender).getHealth() - 1;

                    if (newHP <= 0) {
                        if (!defender.isDead())
                            ((Player) defender).setHealth(0);
                        getBleedingMap().remove(playerUUID);
                        this.cancel();
                        return;

                    } else {
                        ((Player) defender).setHealth(newHP);
                    }

                    counter += 20;
                    Object[] properties = getBleedingMap().get(playerUUID);
                    int left = timer - counter;
                    if (left < 0) left = 0;

                    properties[1] = new Double(left);
                }
            }.runTaskTimer(LostShardPlugin.plugin, 0, 20);

            bleedingMap.put(defender.getUniqueId(), properties);

        }
    }

    private boolean isCrit(Player damager) {
        return damager.getFallDistance() > 0.0f && !damager.isOnGround() && !damager.getLocation().getBlock().isLiquid() && !damager.isSprinting()  &&!damager.isInsideVehicle() && !damager.hasPotionEffect(PotionEffectType.BLINDNESS);
    }

    private void applyLevelBonus(Player damager, Entity defender, EntityDamageByEntityEvent event) {
        int level = (int) SkillPlayer.wrap(damager.getUniqueId()).getSwordsmanship().getLevel();

        double damage = (int) event.getDamage();

        boolean isCrit = isCrit(damager);

        if (isCrit) {
            damage/=1.5f;
        }

        damage -= 4;

        if (level >= 100) {
            damage += 4;
            applyBleed(damager, defender, 0.225);
        } else if (75 <= level && level < 100) {
            damage += 3;
            applyBleed(damager, defender, 0.15);
        } else if (50 <= level && level < 75) {
            damage += 2;
            applyBleed(damager, defender, 0.1);
        } else if (25 <= level && level < 50) {
            damage += 1;
            applyBleed(damager, defender, 0.05);
        } else if (0 <= level && level < 25) {

        }


        if(isCrit)
            damage*=1.5f;

        double sharpnessLevel = damager.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        double sharpnessDamage = 0.5 * sharpnessLevel + 0.5;

        double totalDamage = damage + sharpnessDamage;

        event.setDamage(totalDamage);
    }

    private boolean addXP(Player player, Entity entity) {
        return SkillPlayer.wrap(player.getUniqueId()).getSwordsmanship().addXP(getXP(entity));
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

    public static HashMap<UUID, Object[]> getBleedingMap() {
        return bleedingMap;
    }

    public static boolean isBleeding(UUID uuid) {
        return bleedingMap.get(uuid) != null;
    }
}
