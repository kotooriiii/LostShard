package com.github.kotooriiii.instaeat;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.skill_listeners.SurvivalismListener;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class InstaEatListener implements Listener {

    private static HashMap<UUID, Object[]> foodOnCooldown = new HashMap<>();

    @EventHandler
    public void onInstaEat(PlayerInteractEvent playerInteractEvent) {
        if(playerInteractEvent.useItemInHand() == Event.Result.DENY)
            return;

        Player player = playerInteractEvent.getPlayer();

        if (!playerInteractEvent.getAction().equals(Action.RIGHT_CLICK_AIR) && !playerInteractEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        //Check if it is an insta eat item
        if (!InstaEatType.isCarryingInstaEat(player))
            return;

        //check if stunned

        final InstaEatType instaEatType = InstaEatType.getCarryingInstaEat(player);

        if (isCooldown(player))
            return;



        Stat stat = Stat.wrap(player);

        double currentStamina = stat.getStamina();

        double currentHealth = player.getHealth();
        float currentFoodLevel = player.getFoodLevel();

        double staminaCost = instaEatType.getStaminaCost();

        double replenishedHealth = instaEatType.getHeal();
        double replenishedFoodLevel = instaEatType.getFoodLevel();

        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
        double maxFoodLevel = 20;


        //Check if already have more than what you need in both categories
        if (!instaEatType.isSplashPotion())
            if (currentHealth >= maxHealth
                    && currentFoodLevel >= maxFoodLevel)
                return;

        //If it's health/regen pot OR if its not a splash potion
        if (instaEatType.isApplicableSplash(player.getInventory().getItemInMainHand()) || !instaEatType.isSplashPotion()) {

            //If you dont have enough mana stop
            if (currentStamina < staminaCost) {

                //only health potions cost stamina

                if (instaEatType.isSplashPotion()) {
                    player.sendMessage(ERROR_COLOR + "You do not have enough stamina. You need " + (int) staminaCost + " stamina for that.");
                    playerInteractEvent.setCancelled(true);
                } else {
                    player.sendMessage(ERROR_COLOR + "You do not have enough stamina. You need " + (int) staminaCost + " stamina for that.");
                    playerInteractEvent.setCancelled(true);

                }
                return;
            }

            stat.setStamina(currentStamina - staminaCost);

        }


        double newHealth = currentHealth + replenishedHealth;
        double newFoodLevel = currentFoodLevel + replenishedFoodLevel;

        final float survivalismLevel = LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getSurvivalism().getLevel();
        //if has perk
        if ((int) survivalismLevel >= 50) {
            //if its a survivalist food
            if (SurvivalismListener.SurvivalistFood.isSurvivalistFood(instaEatType.getMaterial())) {
                SurvivalismListener.SurvivalistFood survivalistFood = SurvivalismListener.SurvivalistFood.getSurvivalistFood(instaEatType.getMaterial());
                //add food attribute
                newFoodLevel += survivalistFood.getFoodLevel();
            }
        }

        if (newHealth > maxHealth)
            player.setHealth(maxHealth);
        else
            player.setHealth(newHealth);

        if (newFoodLevel > maxFoodLevel)
            player.setFoodLevel((int) maxFoodLevel);
        else
            player.setFoodLevel((int) newFoodLevel);

        if(instaEatType == InstaEatType.ROTTEN_FLESH)
        {
            if(survivalismLevel < 50 && Math.random() < 0.5d)
            {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20*10, 1, false, false, false));
            }
        }

        // Add player to cooldown list
        foodOnCooldown.put(player.getUniqueId(), new Object[]{instaEatType, new Double(instaEatType.getCooldown() * 20)});
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = instaEatType.getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    foodOnCooldown.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Object[] properties = foodOnCooldown.get(player.getUniqueId());
                properties[1] = new Double(cooldown - counter);
                foodOnCooldown.put(player.getUniqueId(), properties);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);

        if (instaEatType.isSplashPotion()) {

        } else {
            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            int amount = mainHandItem.getAmount();
            if (amount == 1) {
                player.getInventory().setItemInMainHand(null);
            } else {
                mainHandItem.setAmount(amount - 1);
                player.getInventory().setItemInMainHand(mainHandItem);
            }

        }
    }

    public boolean isCooldown(Player player) {
        if (!InstaEatType.isCarryingInstaEat(player))
            return false;

        InstaEatType type = InstaEatType.getCarryingInstaEat(player);

        if (foodOnCooldown.containsKey(player.getUniqueId()) && ((InstaEatType) foodOnCooldown.get(player.getUniqueId())[0]).equals(type)) {
            if (type.isSplashPotion()) {
                Object[] properties = foodOnCooldown.get(player.getUniqueId());
                DecimalFormat df = new DecimalFormat("##.##");
                double timeOnCooldown = ((Double) properties[1]).doubleValue() / 20;
                player.sendMessage(ERROR_COLOR + "Your splash potion is on cooldown. You have " + df.format(timeOnCooldown) + " seconds before you are able to throw another potion.");
            }

            return true;
        }
        return false;
    }
}
