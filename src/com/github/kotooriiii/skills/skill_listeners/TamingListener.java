package com.github.kotooriiii.skills.skill_listeners;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class TamingListener implements Listener {


    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreedEvent(EntityBreedEvent event) {

        if (event.isCancelled())
            return;

        LivingEntity breederEntity = event.getBreeder();

        if (breederEntity == null) {
            event.setCancelled(true);
            return;
        }

        if (!(breederEntity instanceof Player))
            return;

        //Entties are players
        Player breederPlayer = (Player) breederEntity;

        //
        //The code for each skill will follow on the bottom
        //

        if (LostShardPlugin.getSkillManager().getSkillPlayer(breederPlayer.getUniqueId()).getActiveBuild().getTaming().getLevel() >= 25) {
            LivingEntity entity = event.getEntity();
            if (entity instanceof Wolf) {
                if (!addWolf(breederPlayer)) {
                    event.setCancelled(true);
                    return;
                }
            }
            addXP(breederPlayer, entity, 100);
        } else {
            breederPlayer.sendMessage(ERROR_COLOR + "You can not breed animals until Taming level 25.");

            LivingEntity fatherEntity = event.getFather();
            LivingEntity motherEntity = event.getMother();

            if (fatherEntity instanceof Animals) {
                Animals fatherAnimals = (Animals) fatherEntity;
                fatherAnimals.setLoveModeTicks(0);
            }

            if (motherEntity instanceof Animals) {
                Animals motherAnimals = (Animals) motherEntity;
                motherAnimals.setLoveModeTicks(0);
            }

            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onTamingEvent(EntityTameEvent event) {
        LivingEntity entity = event.getEntity();
        AnimalTamer animalTamer = event.getOwner();
        if (!(animalTamer instanceof Player))
            return;
        Player player = (Player) animalTamer;


        if (entity instanceof Wolf)
            if (!addWolf(player)) {
                event.setCancelled(true);
                return;
            } else {
                addXP(player, entity, 50);

            }
        else
            addXP(player, entity, 30);

    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onFed(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        //If entity is tameable
        if (entity instanceof Tameable) {
            //entity is not tamed!!
            Tameable tameable = (Tameable) entity;
            //MUST NOT BE TAMED FOR THIS CASE TO TRIGGER
            if (!tameable.isTamed())
                return;
        }

        //must be an entity
        if (!(entity instanceof LivingEntity))
            return;

        LivingEntity livingEntity = (LivingEntity) entity;

        //must be lower than max hp
        if (livingEntity.getHealth() >= livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
            return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        //Check if the item is a tameable item


        boolean exists = false;
        for (Material breedingFood : getBreedingFoods(entity))
            if (itemStack.getType().equals(breedingFood))
                exists = true;
        if (!exists)
            return;

           /*
        If entity is tameable, Entity is tamed.

        OR

        Entity is living
        Entity lower than full hp
        Is of our definition 'tameable'
         */
        int addedHP = 2;
        double newHP = livingEntity.getHealth() + addedHP;
        double maxHP = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if (newHP > maxHP) {
            livingEntity.setHealth(maxHP);
        } else {
            livingEntity.setHealth(newHP);
        }

        addXP(player, entity);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTameAttempt(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        //If entity is not tameable
        if (!(entity instanceof Tameable))
            return;

        //If entity is already tamed
        Tameable tameable = (Tameable) entity;
        if (tameable.isTamed())
            return;

        //must be max hp
        if (((LivingEntity) entity).getHealth() < ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
            return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        //Check if the item is a tameable item
        boolean exists = false;
        for (Material breedingFood : getTameableItem(tameable))
            if (itemStack.getType().equals(breedingFood))
                exists = true;
        if (!exists)
            return;

        //
        if (entity instanceof Wolf) {
            Wolf[] wolves = getWolves(player);
            int maxSize = getMaxSizeOfWolves(player);
            if (wolves.length >= maxSize) {
                player.sendMessage(ERROR_COLOR + "You have reached your wolf pack limit. Limit: " + maxSize + ".");
                event.setCancelled(true);
                return;
            }

        }

                   /*
        entity is tameable
        entity is not tamed
        is at full hp
        if is our definition of "tameable"
*/
        addXP(player, entity);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTameAttemptVehicle(VehicleExitEvent event) {


        LivingEntity livingEntity = event.getExited();
        //Must be player
        if (!(livingEntity instanceof Player))
            return;

        Vehicle vehicle = event.getVehicle();

        //Must be a tameable entity
        if (!(vehicle instanceof Tameable))
            return;

        Tameable tameable = (Tameable) vehicle;
        //Already tamed
        if (tameable.isTamed())
            return;

        if (!(vehicle instanceof AbstractHorse))
            return;
        AbstractHorse horse = (AbstractHorse) vehicle;

        //    addXP((Player) livingEntity, vehicle);
    }


    @EventHandler
    public void denyMobSpawnerChange(PlayerInteractEvent event) {
        if ((event.getClickedBlock() != null) && (event.getItem() != null) && (event.getClickedBlock().getType() == Material.SPAWNER) && (event.getItem().getType().getKey().getKey().toUpperCase().endsWith("SPAWN_EGG")))
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
    }

    //Pokeball
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPokeball(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Egg))
            return;

        Entity entity = event.getHitEntity();
        if (entity == null || !(entity instanceof LivingEntity))
            return;

        ProjectileSource source = event.getEntity().getShooter();
        if (source == null || !(source instanceof Player))
            return;

        Player shooter = (Player) source;
        LivingEntity livingEntity = (LivingEntity) entity;
        Egg egg = (Egg) projectile;

        if (shooter.getLevel() < 30)
            return;

        if (LostShardPlugin.getSkillManager().getSkillPlayer(shooter.getUniqueId()).getActiveBuild().getTaming().getLevel() < 50)
            return;


        //Passive and monsters
        if (livingEntity instanceof Tameable) {
            if (((Tameable) livingEntity).isTamed() && !((Tameable) livingEntity).getOwner().getUniqueId().equals(shooter.getUniqueId()))
                return;
        }

        //Calculate level
        if (!isHostile(livingEntity) && !isPassive(livingEntity))
            return; //Not living :( remove in case

        int level = (int) LostShardPlugin.getSkillManager().getSkillPlayer(shooter.getUniqueId()).getActiveBuild().getTaming().getLevel();

        int levelRatio = level - 49;

        if (isHostile(livingEntity)) {
            if (level < 75) {
                return;
            }
            levelRatio = level - 74;
        }

        double chance = levelRatio * 0.04;


        applyPokeball(shooter, livingEntity, egg, chance);
        shooter.setLevel(shooter.getLevel() - 30);
        addXP(shooter, entity, 150);
    }

    private void applyPokeball(Player shooter, LivingEntity entity, Egg egg, double chance) {
        double random = Math.random();

        EntityType type = entity.getType();


        //lvl100
        if (LostShardPlugin.getSkillManager().getSkillPlayer(shooter.getUniqueId()).getActiveBuild().getTaming().getLevel() >= 100) {
            if (entity instanceof Cow) {
                if (random <= 0.05) {
                    type = EntityType.MUSHROOM_COW;
                    shooter.sendMessage(ChatColor.RED + "You have been extremely lucky in your capturing efforts and have received a special mob!");
                }
            }
        }

        if (random <= chance) {

            ItemStack spawnEgg = getEgg(type);
            if (spawnEgg == null)
                return;

            egg.remove();
            entity.remove();
            shooter.sendMessage(ChatColor.RED + "The " + entity.getName().toLowerCase().replace("_", " ") + " has drained your experience.");
            HashMap<Integer, ItemStack> map = shooter.getInventory().addItem(spawnEgg);
            if (!map.isEmpty()) {
                shooter.sendMessage(STANDARD_COLOR + "Your inventory is full. The spawn egg has been dropped on the ground.");
                shooter.getLocation().getWorld().dropItem(shooter.getLocation(), spawnEgg);
            }

        } else {
            shooter.sendMessage(ChatColor.RED + "The " + entity.getName().toLowerCase().replace("_", " ") + " refused to be caught.");
        }

    }

    private Material[] getTameableItem(Tameable tameable) {
        switch (tameable.getType()) {
            case WOLF:
                return new Material[]{Material.BONE};
            case CAT:
                return new Material[]{Material.COD, Material.SALMON};
            case HORSE:
            case DONKEY:
            case MULE:
            case LLAMA:
            case FOX: //fox cant be tamed only bred
                return new Material[]{Material.END_PORTAL};
            case PARROT:
                return new Material[]{Material.WHEAT_SEEDS, Material.PUMPKIN_SEEDS, Material.BEETROOT_SEEDS, Material.MELON_SEEDS};
        }
        return new Material[]{Material.END_PORTAL};
    }

    private Material[] getBreedingFoods(Entity entity) {
        switch (entity.getType()) {
            case WOLF:
                return new Material[]{Material.PORKCHOP, Material.BEEF, Material.CHICKEN, Material.RABBIT, Material.MUTTON, Material.ROTTEN_FLESH,
                        Material.COOKED_PORKCHOP, Material.COOKED_BEEF, Material.COOKED_CHICKEN, Material.COOKED_RABBIT, Material.COOKED_MUTTON};
            case CAT:
            case OCELOT:
                return new Material[]{Material.COD, Material.SALMON};
            case RABBIT:
                return new Material[]{Material.DANDELION, Material.CARROT, Material.GOLDEN_CARROT};
            case HORSE:
            case DONKEY:
                return new Material[]{Material.GOLDEN_APPLE, Material.GOLDEN_CARROT, Material.SUGAR, Material.WHEAT, Material.APPLE, Material.HAY_BLOCK};
            case SHEEP:
            case COW:
            case MUSHROOM_COW:
                return new Material[]{Material.WHEAT};
            case PIG:
                return new Material[]{Material.CARROT, Material.POTATO, Material.BEETROOT};
            case CHICKEN:
                return new Material[]{Material.WHEAT_SEEDS, Material.PUMPKIN_SEEDS, Material.BEETROOT_SEEDS, Material.MELON_SEEDS};
            case LLAMA:
                return new Material[]{Material.HAY_BLOCK, Material.WHEAT};
            case TURTLE:
                return new Material[]{Material.SEAGRASS};
            case PANDA:
                return new Material[]{Material.BAMBOO};
            case BEE:
                return new Material[]{Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET, Material.RED_TULIP,
                        Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY,
                        Material.WITHER_ROSE, Material.SUNFLOWER, Material.LILAC, Material.ROSE_BUSH, Material.PEONY};
            case FOX:
                return new Material[]{Material.SWEET_BERRIES};

            case PARROT: //cannot be bred
            case MULE:
            case POLAR_BEAR:
            case TRADER_LLAMA:
            case SKELETON_HORSE:
                return new Material[]{Material.END_PORTAL};
        }
        return new Material[]{Material.END_PORTAL};
    }

    public static boolean isHostile(Entity entity) {

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
            case PLAYER:
            default:
                return false;


            //todo might do something custom with values down here v
            case ILLUSIONER:
            case GIANT:
            case ENDER_DRAGON:
            case IRON_GOLEM:
            case SNOWMAN:
            case WITHER:
                return false;


            case ELDER_GUARDIAN:
            case WITHER_SKELETON:
            case STRAY:
            case HUSK:
            case ZOMBIE_VILLAGER:
            case SKELETON_HORSE:
            case ZOMBIE_HORSE:
            case EVOKER:
            case VEX:
            case VINDICATOR:
            case CREEPER:
            case SKELETON:
            case SPIDER:
            case ZOMBIE:
            case SLIME:
            case GHAST:
            case PIGLIN:
            case HOGLIN:
            case ZOGLIN:
            case STRIDER:
            case PIGLIN_BRUTE:
            case ENDERMAN:
            case CAVE_SPIDER:
            case ZOMBIFIED_PIGLIN:
            case SILVERFISH:
            case BLAZE:
            case MAGMA_CUBE:
            case PHANTOM:
            case WITCH:
            case ENDERMITE:
            case GUARDIAN:
            case SHULKER:
            case DROWNED:
            case PILLAGER:
            case RAVAGER:
                return true;

            case DONKEY:
            case MULE:
            case MUSHROOM_COW:
            case PIG:
            case SHEEP:
            case COW:
            case CHICKEN:
            case SQUID:
            case WOLF:
            case OCELOT:
            case BAT:
            case HORSE:
            case RABBIT:
            case POLAR_BEAR:
            case LLAMA:
            case PARROT:
            case VILLAGER:
            case TURTLE:
            case COD:
            case SALMON:
            case PUFFERFISH:
            case TROPICAL_FISH:
            case DOLPHIN:
            case CAT:
            case PANDA:
            case TRADER_LLAMA:
            case WANDERING_TRADER:
            case FOX:
            case BEE:
                return false;
        }
    }

    private boolean isPassive(Entity entity) {

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
            case PLAYER:
            default:
                return false;


            //todo might do something custom with values down here v
            case ILLUSIONER:
            case GIANT:
            case ENDER_DRAGON:
            case IRON_GOLEM:
            case SNOWMAN:
            case WITHER:
                return false;


            case ELDER_GUARDIAN:
            case WITHER_SKELETON:
            case STRAY:
            case HUSK:
            case ZOMBIE_VILLAGER:
            case SKELETON_HORSE:
            case ZOMBIE_HORSE:
            case EVOKER:
            case VEX:
            case VINDICATOR:
            case CREEPER:
            case SKELETON:
            case SPIDER:
            case ZOMBIE:
            case SLIME:
            case GHAST:
            case ZOMBIFIED_PIGLIN:
            case HOGLIN:
            case PIGLIN:
            case STRIDER:
            case ZOGLIN:
            case PIGLIN_BRUTE:
            case ENDERMAN:
            case CAVE_SPIDER:
            case SILVERFISH:
            case BLAZE:
            case MAGMA_CUBE:
            case PHANTOM:
            case WITCH:
            case ENDERMITE:
            case GUARDIAN:
            case SHULKER:
            case DROWNED:
            case PILLAGER:
            case RAVAGER:
                return false;

            case MUSHROOM_COW:
            case DONKEY:
            case MULE:
            case PIG:
            case SHEEP:
            case COW:
            case CHICKEN:
            case SQUID:
            case WOLF:
            case OCELOT:
            case BAT:
            case HORSE:
            case RABBIT:
            case POLAR_BEAR:
            case LLAMA:
            case PARROT:
            case VILLAGER:
            case TURTLE:
            case COD:
            case SALMON:
            case PUFFERFISH:
            case TROPICAL_FISH:
            case DOLPHIN:
            case CAT:
            case PANDA:
            case TRADER_LLAMA:
            case WANDERING_TRADER:
            case FOX:
            case BEE:
                return true;
        }
    }

    private ItemStack getEgg(EntityType type) {

        Material material = null;
        switch (type) {

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
            case PLAYER:
            default:
                material = null;
                break;

            //todo might do something custom with values down here v
            case ILLUSIONER:
            case GIANT:
            case ENDER_DRAGON:
            case IRON_GOLEM:
            case SNOWMAN:
            case WITHER:
                material = null;
                break;


            case MUSHROOM_COW:
                material = Material.MOOSHROOM_SPAWN_EGG;
                break;
            case ELDER_GUARDIAN:
                material = Material.ELDER_GUARDIAN_SPAWN_EGG;
                break;
            case WITHER_SKELETON:
                material = Material.WITHER_SKELETON_SPAWN_EGG;
                break;
            case STRAY:
                material = Material.STRAY_SPAWN_EGG;
                break;
            case HUSK:
                material = Material.HUSK_SPAWN_EGG;
                break;
            case ZOMBIE_VILLAGER:
                material = Material.ZOMBIE_VILLAGER_SPAWN_EGG;
                break;
            case SKELETON_HORSE:
                material = Material.SKELETON_HORSE_SPAWN_EGG;
                break;
            case ZOMBIE_HORSE:
                material = Material.ZOMBIE_HORSE_SPAWN_EGG;
                break;
            case EVOKER:
                material = Material.EVOKER_SPAWN_EGG;
                break;
            case VEX:
                material = Material.VEX_SPAWN_EGG;
                break;
            case VINDICATOR:
                material = Material.VINDICATOR_SPAWN_EGG;
                break;
            case CREEPER:
                material = Material.CREEPER_SPAWN_EGG;
                break;
            case SKELETON:
                material = Material.SKELETON_SPAWN_EGG;
                break;
            case SPIDER:
                material = Material.SPIDER_SPAWN_EGG;
                break;
            case ZOMBIE:
                material = Material.ZOMBIE_SPAWN_EGG;
                break;
            case SLIME:
                material = Material.SLIME_SPAWN_EGG;
                break;
            case GHAST:
                material = Material.GHAST_SPAWN_EGG;
                break;
            case ZOMBIFIED_PIGLIN:
                material = Material.ZOMBIFIED_PIGLIN_SPAWN_EGG;
                break;
            case ENDERMAN:
                material = Material.ENDERMAN_SPAWN_EGG;
                break;
            case HOGLIN:
                material = Material.HOGLIN_SPAWN_EGG;
                break;
            case PIGLIN:
                material = Material.PIGLIN_SPAWN_EGG;
                break;
            case STRIDER:
                material = Material.STRIDER_SPAWN_EGG;
                break;
            case ZOGLIN:
                material = Material.ZOGLIN_SPAWN_EGG;
                break;
            case PIGLIN_BRUTE:
                material = Material.PIGLIN_BRUTE_SPAWN_EGG;
                break;
            case CAVE_SPIDER:
                material = Material.CAVE_SPIDER_SPAWN_EGG;
                break;
            case SILVERFISH:
                material = Material.SILVERFISH_SPAWN_EGG;
                break;
            case BLAZE:
                material = Material.BLAZE_SPAWN_EGG;
                break;
            case MAGMA_CUBE:
                material = Material.MAGMA_CUBE_SPAWN_EGG;
                break;
            case PHANTOM:
                material = Material.PHANTOM_SPAWN_EGG;
                break;
            case WITCH:
                material = Material.WITCH_SPAWN_EGG;
                break;
            case ENDERMITE:
                material = Material.ENDERMITE_SPAWN_EGG;
                break;
            case GUARDIAN:
                material = Material.GUARDIAN_SPAWN_EGG;
                break;
            case SHULKER:
                material = Material.SHULKER_SPAWN_EGG;
                break;
            case DROWNED:
                material = Material.DROWNED_SPAWN_EGG;
                break;
            case PILLAGER:
                material = Material.PILLAGER_SPAWN_EGG;
                break;
            case RAVAGER:
                material = Material.RAVAGER_SPAWN_EGG;
                break;
            case DONKEY:
                material = Material.DONKEY_SPAWN_EGG;
                break;
            case MULE:
                material = Material.MULE_SPAWN_EGG;
                break;
            case PIG:
                material = Material.PIG_SPAWN_EGG;
                break;
            case SHEEP:
                material = Material.SHEEP_SPAWN_EGG;
                break;
            case COW:
                material = Material.COW_SPAWN_EGG;
                break;
            case CHICKEN:
                material = Material.CHICKEN_SPAWN_EGG;
                break;
            case SQUID:
                material = Material.SQUID_SPAWN_EGG;
                break;
            case WOLF:
                material = Material.WOLF_SPAWN_EGG;
                break;
            case OCELOT:
                material = Material.OCELOT_SPAWN_EGG;
                break;
            case BAT:
                material = Material.BAT_SPAWN_EGG;
                break;
            case HORSE:
                material = Material.HORSE_SPAWN_EGG;
                break;
            case RABBIT:
                material = Material.RABBIT_SPAWN_EGG;
                break;
            case POLAR_BEAR:
                material = Material.POLAR_BEAR_SPAWN_EGG;
                break;
            case LLAMA:
                material = Material.LLAMA_SPAWN_EGG;
                break;
            case PARROT:
                material = Material.PARROT_SPAWN_EGG;
                break;
            case VILLAGER:
                material = Material.VILLAGER_SPAWN_EGG;
                break;
            case TURTLE:
                material = Material.TURTLE_SPAWN_EGG;
                break;
            case COD:
                material = Material.COD_SPAWN_EGG;
                break;
            case SALMON:
                material = Material.SALMON_SPAWN_EGG;
                break;
            case PUFFERFISH:
                material = Material.PUFFERFISH_SPAWN_EGG;
                break;
            case TROPICAL_FISH:
                material = Material.TROPICAL_FISH_SPAWN_EGG;
                break;
            case DOLPHIN:
                material = Material.DOLPHIN_SPAWN_EGG;
                break;
            case CAT:
                material = Material.CAT_SPAWN_EGG;
                break;
            case PANDA:
                material = Material.PANDA_SPAWN_EGG;
                break;
            case TRADER_LLAMA:
                material = Material.TRADER_LLAMA_SPAWN_EGG;
                break;
            case WANDERING_TRADER:
                material = Material.WANDERING_TRADER_SPAWN_EGG;
                break;
            case FOX:
                material = Material.FOX_SPAWN_EGG;
                break;
            case BEE:
                material = Material.BEE_SPAWN_EGG;
                break;

        }

        return new ItemStack(material, 1);
    }

    public static int getMaxSizeOfWolves(Player player) {
        int level = (int) LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getTaming().getLevel();

        int maxSize = 0;
        if (level < 25)
            maxSize = 1;
        else if (level < 50)
            maxSize = 1;
        else if (level < 75)
            maxSize = 2;
        else if (level < 100)
            maxSize = 3;
        else if (level == 100)
            maxSize = 5;

        return maxSize;
    }

    public static boolean addWolf(Player player) {
        Wolf[] wolves = getWolves(player);
        int maxSize = getMaxSizeOfWolves(player);

        //If we try to add one more wolf, is it allowed?
        if (wolves.length + 1 > maxSize) {
            //TOO MUCH
            player.sendMessage(ERROR_COLOR + "You have reached the maximum number of wolves you can capture for your Taming level.");
            return false;
        } else {
            //Added wolf
            return true;
        }

    }

    public static Wolf[] getWolves(Player player) {
        ArrayList<Wolf> wolves = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getLivingEntities()) {
                if (entity instanceof Wolf) {
                    Wolf wolf = (Wolf) entity;
                    if (!wolf.isTamed())
                        continue;

                    AnimalTamer tamer = wolf.getOwner();
                    if (!(tamer instanceof Player))
                        continue;

                    Player owner = (Player) tamer;

                    if (player.equals(owner))
                        wolves.add(wolf);
                }
            }
        }
        return wolves.toArray(new Wolf[wolves.size()]);
    }

    public static Creature[] getPets(Player player) {
        ArrayList<Creature> creatures = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getLivingEntities()) {
                if (entity instanceof Wolf) {
                    Wolf wolf = (Wolf) entity;
                    if (!wolf.isTamed())
                        continue;

                    AnimalTamer tamer = wolf.getOwner();
                    if (!(tamer instanceof Player))
                        continue;

                    Player owner = (Player) tamer;

                    if (player.equals(owner))
                        creatures.add(wolf);
                }
                else if (entity instanceof Fox)
                {
                    Fox fox = (Fox) entity;
                    if(fox.getFirstTrustedPlayer() != null && fox.getFirstTrustedPlayer().equals(player))
                    {
                        creatures.add(fox);
                    }
                    else if (fox.getSecondTrustedPlayer() != null && fox.getSecondTrustedPlayer().equals(player))
                    {
                        creatures.add(fox);
                    }
                }
            }
        }
        return creatures.toArray(new Creature[creatures.size()]);
    }

    private static final ArrayList<Chunk> chunks = new ArrayList<>();
    private boolean initialized = false;

    @EventHandler
    public void onChunkDespawnwolf(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof Wolf) {
                Wolf wolf = (Wolf) entity;
                if (!wolf.isTamed())
                    continue;

                AnimalTamer tamer = wolf.getOwner();
                if (!(tamer instanceof Player))
                    continue;

                event.getChunk().setForceLoaded(true);
                chunks.add(event.getChunk());

                if (!initialized) {
                    initialized = true;
                    initRunnable();
                }
                break;
            }
        }
    }

    public static void initRunnable() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Chunk chunk : chunks) {
                    chunk.setForceLoaded(false);
                }

                chunks.clear();
            }
        }.runTaskTimer(LostShardPlugin.plugin, 20 * 60 * 5, 20 * 60 * 1);
    }

    private boolean addXP(Player player, Entity entity) {
        return addXP(player, entity, 0);
    }

    private boolean addXP(Player player, Entity entity, float bonusXP) {
        return LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getTaming().addXP(getXP(entity) + bonusXP);
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

        return 100;
    }

}
