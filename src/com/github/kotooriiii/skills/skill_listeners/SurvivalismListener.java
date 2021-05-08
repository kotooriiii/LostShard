package com.github.kotooriiii.skills.skill_listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.google.common.collect.Lists;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.github.kotooriiii.util.HelperMethods.*;
import static com.github.kotooriiii.util.HelperMethods.getPlayerDamagerONLY;

public class SurvivalismListener implements Listener {

    public final static int TRACKING_XP = 100;
    public final static int CAMP_XP = 80;

    public static class Campfire {
        private Location location;
        private UUID ownerUUID;
        private BukkitTask task;
        private boolean isDestroyed;

        private final int HEALING = 1; //half hearts
        private final int DURATION_TICKS = 15 * 20; //seconds

        public static final float STAMINA_COST = 40;
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
            for (Entity entity : getLog().getWorld().getNearbyEntities(boundingBox, RADIUS, HEIGHT_Y, RADIUS)) {
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

        public Player[] getCampersRange(int radius) {
            Location boundingBox = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + HEIGHT_Y, location.getBlockZ());
            ArrayList<Player> players = new ArrayList<>();
            for (Entity entity : getLog().getWorld().getNearbyEntities(boundingBox, radius, HEIGHT_Y, radius)) {
                if (!(entity instanceof Player))
                    continue;

                Player player = (Player) entity;
                players.add(player);
            }
            return players.toArray(new Player[players.size()]);
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

            addXP(Bukkit.getOfflinePlayer(ownerUUID).getPlayer(), CAMP_XP);
            getLog().setType(Material.OAK_LOG); //set log
            getFireBlock().setType(Material.FIRE); //set fire

            task = new BukkitRunnable() {
                private final int max = DURATION_TICKS;
                private int counter = 0;

                @Override
                public void run() {

                    if (isCancelled() || isDestroyed) {
                        campfireHashMap.remove(ownerUUID);
                        this.cancel();
                        return;
                    }

//                    if (getFireBlock().getType() != Material.FIRE) {
//                        campfireHashMap.remove(ownerUUID);
//                        this.cancel();
//                        return;
//                    }

                    if (counter >= max) {
                        //Timer ran out duration
                        destroy();
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(ownerUUID);
                        if (offlinePlayer.isOnline())
                            offlinePlayer.getPlayer().sendMessage(ChatColor.GRAY + "Your camp fire has gone out.");
                        campfireHashMap.remove(ownerUUID);
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

    public enum SurvivalistFood {

        MELON(Material.MELON_SLICE, 8),
        ROTTEN_FLESH(Material.ROTTEN_FLESH, 6),
        SOUP(Material.MUSHROOM_STEW, 11);

        private Material type;
        private int foodLevel;

        private SurvivalistFood(Material type, int foodLevel) {
            this.type = type;
            this.foodLevel = foodLevel;
        }

        public int getFoodLevel() {
            return foodLevel;
        }

        public Material getType() {
            return type;
        }

        public static boolean isSurvivalistFood(Material materialType) {
            for (SurvivalistFood betterFood : SurvivalistFood.values()) {
                if (materialType.equals(betterFood.getType()))
                    return true;
            }
            return false;
        }

        public static SurvivalistFood getSurvivalistFood(Material materialType) {
            for (SurvivalistFood betterFood : SurvivalistFood.values()) {
                if (materialType.equals(betterFood.getType()))
                    return betterFood;
            }
            return null;
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onFireCampfire(EntityDamageEvent event) {
        Entity damagedEntity = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;

        if (!(damagedEntity instanceof Player))
            return;

        if (!cause.equals(EntityDamageEvent.DamageCause.FIRE_TICK) && !cause.equals(EntityDamageEvent.DamageCause.FIRE))
            return;

        Block fireblock = damagedEntity.getLocation().getBlock();

        for (Campfire campfire : Campfire.getCampfires().values()) {
            for (Player player : campfire.getCampersRange(1)) {
                if (player.equals(damagedEntity)) {
                    damagedEntity.setFireTicks(0);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFireCampfireSpread(BlockSpreadEvent event) {

        Block block = event.getSource();

        if (!block.getType().equals(Material.FIRE))
            return;

        for (Campfire campfire : Campfire.getCampfires().values()) {
            if (campfire.getFireBlock().equals(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFireBurn(BlockBurnEvent event) {

        Block burnedBlock = event.getBlock();

        for (Campfire campfire : Campfire.getCampfires().values()) {
            if (campfire.getLog().equals(burnedBlock)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPPlayerDeath(PlayerDeathEvent event) {
        Player defenderPlayer = event.getEntity();
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
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

        return;
        //addXP(damagerPlayer, defenderPlayer); players dont count as far as im concernced
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPredeath(EntityDamageEvent event) {
        final Entity entity = event.getEntity();

        if (!(entity instanceof Creature))
            return;

        Creature defenderEntity = (Creature) entity;

        if (!(defenderEntity.getHealth() - event.getFinalDamage() <= 0))
            return;

        if (defenderEntity.getEquipment() == null)
            return;


        ArrayList<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < 6; i++) {

            if(Math.random() >= 0.085)
                continue;
            switch (i) {
                case 0:
                    stacks.add(defenderEntity.getEquipment().getItemInMainHand());
                    break;
                case 1:
                    stacks.add(defenderEntity.getEquipment().getItemInOffHand());
                    break;
                case 2:
                    stacks.add(defenderEntity.getEquipment().getHelmet());
                    break;
                case 3:
                    stacks.add(defenderEntity.getEquipment().getChestplate());
                    break;
                case 4:
                    stacks.add(defenderEntity.getEquipment().getLeggings());
                    break;
                case 5:
                    stacks.add(defenderEntity.getEquipment().getBoots());
                    break;
            }
        }

        if (defenderEntity instanceof ChestedHorse) {
            ChestedHorse chestedHorse = (ChestedHorse) defenderEntity;
            final List<@NonNull ItemStack> itemStacks = Arrays.asList(chestedHorse.getInventory().getContents());
            stacks.addAll(itemStacks);
            stacks.add(new ItemStack(Material.CHEST,1));
            if (chestedHorse.getInventory().getSaddle() != null)
                stacks.add(chestedHorse.getInventory().getSaddle());
            if (chestedHorse.getInventory() instanceof HorseInventory) {
                HorseInventory horseInventory = (HorseInventory) chestedHorse.getInventory();
                if (horseInventory.getArmor() != null)
                    stacks.add(horseInventory.getArmor());
            }
        }

        final ItemStack[] equipmentArr = stacks.toArray(new ItemStack[0]);


        for (int i = 0; i < equipmentArr.length; i++) {
            if (equipmentArr[i] == null || equipmentArr[i].getType().isAir())
                continue;
            defenderEntity.getWorld().dropItem(defenderEntity.getLocation(), equipmentArr[i]);
        }

        defenderEntity.getEquipment().clear();

        if (defenderEntity instanceof ChestedHorse) {
            ChestedHorse chestedHorse = (ChestedHorse) defenderEntity;
            chestedHorse.getInventory().clear();
            if (chestedHorse.getInventory().getSaddle() != null)
                chestedHorse.getInventory().setSaddle(null);
            if (chestedHorse.getInventory() instanceof HorseInventory) {
                HorseInventory horseInventory = (HorseInventory) chestedHorse.getInventory();
                if (horseInventory.getArmor() != null)
                    horseInventory.setArmor(null);
            }
        }


    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onXPEntityDeath(EntityDeathEvent event) {

        LivingEntity defenderEntity = event.getEntity();
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;

        EntityDamageEvent damagerCause = defenderEntity.getLastDamageCause();

        if (damagerCause == null || !(damagerCause instanceof EntityDamageByEntityEvent))
            return;

        if (defenderEntity instanceof ArmorStand)
            return;

        EntityDamageByEntityEvent betterDamagerCause = (EntityDamageByEntityEvent) damagerCause;

        Entity damagerEntity = betterDamagerCause.getDamager();

        if (damagerEntity == null)
            return;

        if (!isPlayerDamagerONLY(defenderEntity, damagerEntity))
            return;


        Player damagerPlayer = getPlayerDamagerONLY(defenderEntity, damagerEntity);
        //DamagerEntity already defined

        //
        //The code for each skill will follow on the bottom
        //
        if (LostShardPlugin.getSkillManager().getSkillPlayer(damagerPlayer.getUniqueId()).getActiveBuild().getSurvivalism().getLevel() >= 75) {

            if (false)//todo for config options in the future
                if (damagerPlayer.getInventory().getItemInMainHand() != null && damagerPlayer.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) > 0)
                    return;


            final List<ItemStack> drops = event.getDrops();
            final Iterator<ItemStack> iterator = drops.iterator();

            dropsLoop:
            while (iterator.hasNext()) {
                final ItemStack itemStack = iterator.next();

                if (isBlacklisted(itemStack.getType()))
                    continue;

                double random = Math.random();
                if (random <= 0.75) {

                    final int LIMIT = 2;
                    int originalAmount = itemStack.getAmount();
                    if (originalAmount > LIMIT)
                        originalAmount = LIMIT;

                    int addedBonus = new Random().nextInt(originalAmount);
                    // int multiplier = 2; // new Random().nextInt(2) + 2; //0 1 , 2 3

                    ItemStack item = itemStack.clone();
                    item.setAmount(itemStack.getAmount() + addedBonus);
                    defenderEntity.getLocation().getWorld().dropItemNaturally(event.getEntity().getLocation(), item);
                }

            }

        }
        addXP(damagerPlayer, defenderEntity);
    }

    private final boolean isBlacklisted(Material material) {
        for (Material m : getBlacklisted()) {
            if (m.equals(material))
                return true;
        }
        return false;
    }

    private final Material[] getBlacklisted() {
        return new Material[]{
                Material.NETHER_STAR,
                Material.DRAGON_EGG
        };
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onEat(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        ItemStack eatenItem = event.getItem();

        if (eatenItem == null)
            return;

        if (!SurvivalistFood.isSurvivalistFood(eatenItem.getType()))
            return;

        int level = (int) LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getSurvivalism().getLevel();

        if (level < 50)
            return;

        int eventFoodLevel = event.getFoodLevel();
        int currentFoodLevel = player.getFoodLevel();
        int survivalFoodLevel = SurvivalistFood.getSurvivalistFood(eatenItem.getType()).getFoodLevel();
        int fullyAdded = eventFoodLevel + currentFoodLevel + survivalFoodLevel;

        final int maxFoodLevel = 20;


        if (fullyAdded >= maxFoodLevel) {
            player.setFoodLevel(maxFoodLevel);

        } else {
            player.setFoodLevel(fullyAdded);
        }
    }


    private static boolean addXP(Player player, float XP) {
        return LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getSurvivalism().addXP(XP);
    }

    private static boolean addXP(Player player, Entity entity) {
        return LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getSurvivalism().addXP(getXP(entity));
    }

    private static float getXP(Entity entity) {

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

        return 45;
    }
}
