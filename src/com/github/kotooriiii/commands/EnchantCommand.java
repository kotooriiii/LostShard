package com.github.kotooriiii.commands;

import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.util.HelperMethods;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.*;

public class EnchantCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("enchant"))
            return false;
        if(!Staff.isStaff(((Player) commandSender).getUniqueId()))
        {
            commandSender.sendMessage(ERROR_COLOR + "You must be staff to access this command.");
            return false;
        }

        if (args.length < 2) {
            commandSender.sendMessage(ERROR_COLOR + "No arguments. Do /enchant <enchantmentType> <level>");
            return false;
        }

        Player player = (Player) commandSender;
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().isAir()) {
            player.sendMessage(ERROR_COLOR + "No item in hand.");
            return false;
        }

//        switch (args[0].toLowerCase().replace(" ", "_"))
//        {
//            case
//            public static final Enchantment PROTECTION_ENVIRONMENTAL = new EnchantmentWrapper("protection");
//            public static final Enchantment PROTECTION_FIRE = new EnchantmentWrapper("fire_protection");
//            public static final Enchantment PROTECTION_FALL = new EnchantmentWrapper("feather_falling");
//            public static final Enchantment PROTECTION_EXPLOSIONS = new EnchantmentWrapper("blast_protection");
//            public static final Enchantment PROTECTION_PROJECTILE = new EnchantmentWrapper("projectile_protection");
//            public static final Enchantment OXYGEN = new EnchantmentWrapper("respiration");
//            public static final Enchantment WATER_WORKER = new EnchantmentWrapper("aqua_affinity");
//            public static final Enchantment THORNS = new EnchantmentWrapper("thorns");
//            public static final Enchantment DEPTH_STRIDER = new EnchantmentWrapper("depth_strider");
//            public static final Enchantment FROST_WALKER = new EnchantmentWrapper("frost_walker");
//            public static final Enchantment BINDING_CURSE = new EnchantmentWrapper("binding_curse");
//            public static final Enchantment DAMAGE_ALL = new EnchantmentWrapper("sharpness");
//            public static final Enchantment DAMAGE_UNDEAD = new EnchantmentWrapper("smite");
//            public static final Enchantment DAMAGE_ARTHROPODS = new EnchantmentWrapper("bane_of_arthropods");
//            public static final Enchantment KNOCKBACK = new EnchantmentWrapper("knockback");
//            public static final Enchantment FIRE_ASPECT = new EnchantmentWrapper("fire_aspect");
//            public static final Enchantment LOOT_BONUS_MOBS = new EnchantmentWrapper("looting");
//            public static final Enchantment SWEEPING_EDGE = new EnchantmentWrapper("sweeping");
//            public static final Enchantment DIG_SPEED = new EnchantmentWrapper("efficiency");
//            public static final Enchantment SILK_TOUCH = new EnchantmentWrapper("silk_touch");
//            public static final Enchantment DURABILITY = new EnchantmentWrapper("unbreaking");
//            public static final Enchantment LOOT_BONUS_BLOCKS = new EnchantmentWrapper("fortune");
//            public static final Enchantment ARROW_DAMAGE = new EnchantmentWrapper("power");
//            public static final Enchantment ARROW_KNOCKBACK = new EnchantmentWrapper("punch");
//            public static final Enchantment ARROW_FIRE = new EnchantmentWrapper("flame");
//            public static final Enchantment ARROW_INFINITE = new EnchantmentWrapper("infinity");
//            public static final Enchantment LUCK = new EnchantmentWrapper("luck_of_the_sea");
//            public static final Enchantment LURE = new EnchantmentWrapper("lure");
//            public static final Enchantment LOYALTY = new EnchantmentWrapper("loyalty");
//            public static final Enchantment IMPALING = new EnchantmentWrapper("impaling");
//            public static final Enchantment RIPTIDE = new EnchantmentWrapper("riptide");
//            public static final Enchantment CHANNELING = new EnchantmentWrapper("channeling");
//            public static final Enchantment MULTISHOT = new EnchantmentWrapper("multishot");
//            public static final Enchantment QUICK_CHARGE = new EnchantmentWrapper("quick_charge");
//            public static final Enchantment PIERCING = new EnchantmentWrapper("piercing");
//            public static final Enchantment MENDING = new EnchantmentWrapper("mending");
//            public static final Enchantment VANISHING_CURSE = new EnchantmentWrapper("vanishing_curse");
//        }

        String enchName = "";
        for(int i = 0; i < args.length; i ++)
        {
            if(i == args.length- 1)
                continue;

            if(i == args.length - 2)
                enchName += args[i] ;

            else
            enchName += args[i] + " ";
        }




        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchName.toLowerCase().replace(" ", "_")));
        if (enchantment == null)
            return false;

        if (!NumberUtils.isNumber(args[args.length-1]) || args[args.length-1].contains(".")) {
            commandSender.sendMessage(ERROR_COLOR + "Must be an integer between 0-1000.");
            return false;
        }

        int enchLvl = NumberUtils.createInteger(args[args.length-1]).intValue();

        if (!(enchLvl >= 0 && enchLvl <= 1000)) {
            commandSender.sendMessage(ERROR_COLOR + "Must be an integer between 0-1000.");
            return false;
        }


        commandSender.sendMessage(STANDARD_COLOR + "Enchanted with " + enchantment.getKey().getKey() + " " + enchLvl + ".");
        player.getInventory().getItemInMainHand().addUnsafeEnchantment(enchantment, enchLvl);
            return true;
    }
}
