package com.github.kotooriiii.skills.skill_listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.util.Vector;

import static com.github.kotooriiii.util.HelperMethods.*;

public class ArcheryListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void attachMeta(EntityShootBowEvent entityShootBowEvent) {
        entityShootBowEvent.getProjectile().setCustomName("shardArrow:" + entityShootBowEvent.getForce() + ":" + entityShootBowEvent.getBow().getEnchantmentLevel(Enchantment.ARROW_DAMAGE));
        entityShootBowEvent.getProjectile().setCustomNameVisible(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPHurtPlayer(EntityDamageByEntityEvent event) {

        if (event.isCancelled())
            return;

        Entity damager = event.getDamager();
        Entity defender = event.getEntity();

        if(CitizensAPI.getNPCRegistry().isNPC(damager))
            return;
        if(CitizensAPI.getNPCRegistry().isNPC(defender))
            return;


        if (!isPlayerInduced(defender, damager))
            return;

        //Entties are players
        Player damagerPlayer = getPlayerInduced(defender, damager);
        Player defenderPlayer = (Player) defender;

        //
        //The code for each skill will follow on the bottom
        //

        if (!(damager instanceof AbstractArrow))
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

        if(CitizensAPI.getNPCRegistry().isNPC(damager) || CitizensAPI.getNPCRegistry().isNPC(defender))
            return;

        if (!HelperMethods.isPlayerDamagerONLY(defender, damager))
            return;
        //Entties are players
        Player damagerPlayer = getPlayerDamagerONLY(defender, damager);
        Entity defenderEntity = defender;

        //
        //The code for each skill will follow on the bottom
        //

        if (!(damager instanceof AbstractArrow))
            return;

        addXP(damagerPlayer, defenderEntity);


    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPPlayerDeath(PlayerDeathEvent event) {

        Player defenderPlayer = event.getEntity();

        if(CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;


        EntityDamageEvent damagerCause = defenderPlayer.getLastDamageCause();
        if (damagerCause == null || !(damagerCause instanceof EntityDamageByEntityEvent))
            return;

        EntityDamageByEntityEvent betterDamageCause = (EntityDamageByEntityEvent) damagerCause;

        Entity damager = betterDamageCause.getDamager();

        if (damager == null)
            return;

        if (!isPlayerInduced(defenderPlayer, damager))
            return;

        Player damagerPlayer = getPlayerInduced(defenderPlayer, damager);
        //DefenderPlayer already defined

        //
        //The code for each skill will follow on the bottom
        //
        if (!(damager instanceof AbstractArrow))
            return;

        addXP(damagerPlayer, defenderPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPEntityDeath(EntityDeathEvent event) {

        Entity defenderEntity = event.getEntity();
        if(CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;


        EntityDamageEvent damagerCause = defenderEntity.getLastDamageCause();

        if (damagerCause == null || !(damagerCause instanceof EntityDamageByEntityEvent) )
            return;
        EntityDamageByEntityEvent betterDamageCause = (EntityDamageByEntityEvent) damagerCause;

        Entity damagerEntity = betterDamageCause.getDamager();

        if (damagerEntity == null)
            return;

        if (!isPlayerDamagerONLY(defenderEntity, damagerEntity))
            return;

        Player damagerPlayer = getPlayerDamagerONLY(defenderEntity, damagerEntity);
        //DamagerEntity already defined

        //
        //The code for each skill will follow on the bottom
        //
        if (!(damagerEntity instanceof AbstractArrow))
            return;

        addXP(damagerPlayer, defenderEntity);

    }

    private void applyPierce(Player damager, Player defender,AbstractArrow arrow, EntityDamageByEntityEvent event, double chance) {
        double randomValue = Math.random();

        if (randomValue <= chance) {
            if (defender.getHealth() - 10 <= 0) {
                defender.setHealth(0.5);
            }
            else {
                defender.setHealth(defender.getHealth()-10);
            }

            arrow.remove();
            event.setCancelled(true);
            damager.sendMessage(ChatColor.GREEN + "Your arrow pierced through " + defender.getName() + "'s armor.");
            defender.sendMessage(ChatColor.GREEN + "The arrow pierces through your armor.");
        }
    }

    private void applyKnockback(Player damager, Player defender, AbstractArrow arrow, double chance) {
        double randomValue = Math.random();

        if (randomValue <= chance) {

            Vector vec = arrow.getVelocity();
            Vector norm = vec.normalize();
            Vector finalized = norm.multiply(new Vector(7,2.5,7));
            defender.setVelocity(finalized);

            damager.sendMessage(ChatColor.GREEN + defender.getName() + " has been knocked back!");
            defender.sendMessage(ChatColor.GREEN + "You have been knocked back!");
        }
    }

    private void applyLevelBonus(Player damager, Player defender, EntityDamageByEntityEvent event) {
        int level = (int) LostShardPlugin.getSkillManager().getSkillPlayer(damager.getUniqueId()).getActiveBuild().getArchery().getLevel();

        AbstractArrow arrow = (AbstractArrow) event.getDamager();
        if(arrow.getCustomName() == null)
            return;
        String[] properties = arrow.getCustomName().split(":");

        String id = properties[0];
        double force = Double.valueOf(properties[1]);
        int power = Integer.parseInt(properties[2]);

        double damage = 0;

        //force
        if (force <= 0.5)
            damage = 1;
        else if (force <= 0.9)
            damage = 3;
        else if (force <= 1)
            damage = 5;

        double archeryBonusDamage = 0;

        if(level>=100)
        {
            archeryBonusDamage += 4;
            applyKnockback(damager, defender, arrow, 0.2);
            applyPierce(damager, defender, arrow, event, 0.15);
        }
        else if(75 <= level && level < 100)
        {
            archeryBonusDamage += 3;
            applyKnockback(damager, defender, arrow, 0.15);
            applyPierce(damager, defender, arrow, event, 0.10);
        }
        else if(50 <= level && level < 75)
        {
            archeryBonusDamage += 2;
            applyKnockback(damager, defender, arrow, 0.10);
            applyPierce(damager, defender, arrow, event, 0.075);
        }
        else if(25 <= level && level < 50)
        {
            archeryBonusDamage += 1;
            applyKnockback(damager, defender, arrow, 0.07);
            applyPierce(damager, defender, arrow, event, 0.05);
        }
        else if(0 <= level && level < 25)
        {

        }


        //power
        if (power == 0) power = -1;
        double powerRatio = 0.25 * (power + 1);
        double powerDamage = powerRatio*(damage+archeryBonusDamage);


        double finalDamage = (damage+archeryBonusDamage) + powerDamage;

        event.setDamage(finalDamage);
    }

    private boolean addXP(Player player, Entity entity) {

        return LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getArchery().addXP(getXP(entity));
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
            case ZOMBIFIED_PIGLIN:
                break;
            case PIGLIN_BRUTE:
                break;
            case STRIDER:
                break;
            case ZOGLIN:
                break;
            case HOGLIN:
                break;
            case PIGLIN:
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

        return 20;
    }

}
