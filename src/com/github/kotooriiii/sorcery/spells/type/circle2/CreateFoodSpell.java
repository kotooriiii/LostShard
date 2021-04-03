package com.github.kotooriiii.sorcery.spells.type.circle2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import com.github.kotooriiii.sorcery.spells.type.circle1.TeleportSpell;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.sorcery.spells.type.circle1.TeleportSpell.getExactBlockFace;
import static com.github.kotooriiii.sorcery.spells.type.circle1.TeleportSpell.isAcceptableBlock;

public class CreateFoodSpell extends Spell {

    private static HashMap<UUID, Double> createFoodMap = new HashMap<UUID, Double>();
    private static final HashMap<ItemStack, Integer> foodMap = new HashMap<>();

    final private static boolean isDebug = false;
    private final static int RANGE_FOOD = 3;

    private CreateFoodSpell() {
        super(SpellType.CREATE_FOOD,
                "Makes a piece of food magically appear wherever you were looking when you casted it. Useful for when there is no food in sight.",
                2,
                ChatColor.RED,
                new ItemStack[]{new ItemStack(Material.WHEAT_SEEDS, 1)},
                1.0f,
                5,
                true, true, false,
                new SpellMonsterDrop(new EntityType[]{EntityType.COW, EntityType.PIG, EntityType.SHEEP}, 0.20));

        if (foodMap.isEmpty()) {

            final int S = 1, A = 10, B = 50, C = 75, D = 125, E = 175;

            //foodMap.put(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE), S);

            foodMap.put(new ItemStack(Material.GOLDEN_APPLE), A);
            foodMap.put(new ItemStack(Material.GOLDEN_CARROT), A);

            foodMap.put(new ItemStack(Material.COOKED_BEEF), B);
            foodMap.put(new ItemStack(Material.COOKED_PORKCHOP), B);
            foodMap.put(new ItemStack(Material.COOKED_MUTTON), B);
            foodMap.put(new ItemStack(Material.COOKED_SALMON), B);

            foodMap.put(new ItemStack(Material.BAKED_POTATO), C);
            foodMap.put(new ItemStack(Material.BEETROOT), C);
            foodMap.put(new ItemStack(Material.BEETROOT_SOUP), C);
            foodMap.put(new ItemStack(Material.BREAD), C);
            foodMap.put(new ItemStack(Material.CARROT), C);
            foodMap.put(new ItemStack(Material.COOKED_CHICKEN), C);
            foodMap.put(new ItemStack(Material.COOKED_COD), C);
            foodMap.put(new ItemStack(Material.COOKED_RABBIT), C);
            foodMap.put(new ItemStack(Material.MUSHROOM_STEW), C);
            foodMap.put(new ItemStack(Material.RABBIT_STEW), C);
            foodMap.put(new ItemStack(Material.SUSPICIOUS_STEW), C);

            foodMap.put(new ItemStack(Material.APPLE), D);
            foodMap.put(new ItemStack(Material.DRIED_KELP), D);
            foodMap.put(new ItemStack(Material.MELON_SLICE), D);
            foodMap.put(new ItemStack(Material.POISONOUS_POTATO), D);
            foodMap.put(new ItemStack(Material.POTATO), D);
            foodMap.put(new ItemStack(Material.PUMPKIN_PIE), D);
            foodMap.put(new ItemStack(Material.RABBIT), D);
            foodMap.put(new ItemStack(Material.BEEF), D);
            foodMap.put(new ItemStack(Material.MUTTON), D);
            foodMap.put(new ItemStack(Material.PORKCHOP), D);
            foodMap.put(new ItemStack(Material.CHICKEN), D);

            foodMap.put(new ItemStack(Material.CAKE), E);
            foodMap.put(new ItemStack(Material.COOKIE), E);
            foodMap.put(new ItemStack(Material.HONEY_BOTTLE), E);
            foodMap.put(new ItemStack(Material.PUFFERFISH), E);
            foodMap.put(new ItemStack(Material.COD), E);
            foodMap.put(new ItemStack(Material.SALMON), E);
            foodMap.put(new ItemStack(Material.ROTTEN_FLESH), E);
            foodMap.put(new ItemStack(Material.SPIDER_EYE), E);
            foodMap.put(new ItemStack(Material.SWEET_BERRIES), E);
            foodMap.put(new ItemStack(Material.TROPICAL_FISH), E);
        }
    }

    private  static CreateFoodSpell instance;
    public static CreateFoodSpell getInstance() {
        if (instance == null) {
            synchronized (CreateFoodSpell.class) {
                if (instance == null)
                    instance = new CreateFoodSpell();
            }
        }
        return instance;
    }

    @Override
    public boolean executeSpell(Player player) {
        final int rangeTeleport = RANGE_FOOD;
        Location foodLocation = TeleportSpell.teleportLocation(player, rangeTeleport);
        if (foodLocation == null) {
            player.sendMessage(ERROR_COLOR + "You need more room to cast this spell!");
            return false;
        }

        //SELF

//        if (teleportLocation.getBlockX() == player.getLocation().getBlockX() && teleportLocation.getBlockY() == player.getLocation().getBlockY() && teleportLocation.getBlockZ() == player.getLocation().getBlockZ() && player.getLocation().getWorld().equals(teleportLocation.getWorld())) {
//            if (!HelperMethods.getLookingSet(false).contains(teleportLocation.getBlock())) {
//                player.sendMessage(ERROR_COLOR + "Invalid target.");
//                return false;
//            }
//        }

        if (!isAcceptableBlock(getExactBlockFace(player, rangeTeleport), foodLocation.clone().add(0, 1, 0).getBlock(), true)) {
//            player.sendMessage(ERROR_COLOR + "Invalid target.");
//            return false;
            //  teleportLocation.add(0, -1, 0);
            if (isDebug)
                Bukkit.broadcastMessage("DEBUG: Invoke 1");

        } else if (new Location(foodLocation.getWorld(), foodLocation.getX(), foodLocation.getY() - 1, foodLocation.getBlockZ()).getBlock().getType().equals(Material.AIR)) {
            foodLocation.add(0, -1, 0);
            if (isDebug)
                Bukkit.broadcastMessage("DEBUG: Invoke 2");
        }

        final Location finalFoodLocation = new Location(foodLocation.getWorld(), foodLocation.getBlockX() + 0.5, foodLocation.getBlockY(), foodLocation.getBlockZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());

        if (isLapisNearby(finalFoodLocation, DEFAULT_LAPIS_NEARBY)) {
            player.sendMessage(ERROR_COLOR + "You cannot seem to cast " + getName() + " here...");
            return false;
        }


        boolean isMultiple = true;

        if (isMultiple) {
            for (ItemStack itemStack : getFoodItems(true, 3))
                finalFoodLocation.getWorld().dropItemNaturally(finalFoodLocation, itemStack);
        }
        else
        {
            ItemStack itemStack = getFoodItem(true);
            if(itemStack != null)
            finalFoodLocation.getWorld().dropItemNaturally(finalFoodLocation, itemStack);

        }

        finalFoodLocation.getWorld().spawnParticle(Particle.COMPOSTER, finalFoodLocation, 10, 0.5f, 1.5f, 0.5f);
        finalFoodLocation.getWorld().playSound(finalFoodLocation, Sound.ENTITY_PLAYER_BURP, 10.0f, 3.0f);


        return true;
    }

    @Override
    public void updateCooldown(Player player) {
        createFoodMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    createFoodMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                createFoodMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (createFoodMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = createFoodMap.get(player.getUniqueId());
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

    private ItemStack[] getFoodItems(boolean atLeastOne, int maxFoodItems) {
        ArrayList<ItemStack> foods = new ArrayList<>();



        for (Map.Entry<ItemStack, Integer> foodWeight : foodMap.entrySet()) {

            if(foods.size() == maxFoodItems)
                break;

            int random = (int) (Math.random() * 1000);

            if (random < foodWeight.getValue()) {
                ItemStack food = foodWeight.getKey().clone();
                food.setAmount(new Random().nextInt(3)+1);
                foods.add(food);
            }
        }

        if (foods.isEmpty() && atLeastOne) {
            ItemStack food = getFoodItem(true);

            return food != null ? new ItemStack[]{food} : new ItemStack[]{};
        }


        return foods.toArray(new ItemStack[foods.size()]);
    }

    /**
     * Gets a food item.
     * @param atLeastOne If no food items found, force one.
     * @return Food item
     */
    @Nullable
    private ItemStack getFoodItem(boolean atLeastOne) {
        int sum_of_weight = 0;

        for (Map.Entry<ItemStack, Integer> foodWeight : foodMap.entrySet()) {
            sum_of_weight += foodWeight.getValue();
        }

        int rnd = new Random().nextInt(sum_of_weight);

        for (Map.Entry<ItemStack, Integer> foodWeight : foodMap.entrySet()) {
            if (rnd < foodWeight.getValue()) {
                ItemStack food = foodWeight.getKey().clone();
                food.setAmount( Math.min(food.getType().getMaxStackSize(), new Random().nextInt(3)+1));
                return food;
            }
            rnd -= foodWeight.getValue();
        }

        //shouldnt ever proc tbh
        return atLeastOne ? foodMap.entrySet().iterator().next().getKey() : null;
    }
}
