package com.github.kotooriiii.util;

import com.github.kotooriiii.commands.NotificationCommand;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public final class HelperMethods {
    private HelperMethods() {
    }

    public static String stringBuilder(String[] args, int n, String concat) {

        String string = "";
        for (int i = n; i < args.length; i++) {

            if (args[i] == null || args[i].isEmpty())
                continue;

            if (i == n)
                string += args[i];
            else
                string += concat + args[i];
        }
        return string;
    }

    private static Set<Material> set = new HashSet<>();

    public static void initLookingSet()
    {
        //air and liquids
        set.add(Material.AIR);
        set.add(Material.CAVE_AIR);
        set.add(Material.VOID_AIR);
        set.add(Material.WATER);
        set.add(Material.LAVA);

        //sapling
        set.add(Material.ACACIA_SAPLING);
        set.add(Material.BAMBOO_SAPLING);
        set.add(Material.BIRCH_SAPLING);
        set.add(Material.DARK_OAK_SAPLING);
        set.add(Material.JUNGLE_SAPLING);
        set.add(Material.OAK_SAPLING);
        set.add(Material.SPRUCE_SAPLING);
        set.add(Material.GRASS);
        set.add(Material.FERN);
        set.add(Material.DEAD_BUSH);
        set.add(Material.SEAGRASS);
        set.add(Material.DANDELION);
        set.add(Material.POPPY);
        set.add(Material.BLUE_ORCHID);
        set.add(Material.ALLIUM);
        set.add(Material.AZURE_BLUET);
        set.add(Material.RED_TULIP);
        set.add(Material.ORANGE_TULIP);
        set.add(Material.WHITE_TULIP);
        set.add(Material.PINK_TULIP);
        set.add(Material.OXEYE_DAISY);
        set.add(Material.CORNFLOWER);
        set.add(Material.LILY_OF_THE_VALLEY);
        set.add(Material.WITHER_ROSE);
        set.add(Material.BROWN_MUSHROOM);
        set.add(Material.RED_MUSHROOM);
        set.add(Material.TORCH);
        set.add(Material.SUNFLOWER);
        set.add(Material.LILAC);
        set.add(Material.ROSE_BUSH);
        set.add(Material.PEONY);
        set.add(Material.TALL_GRASS);
        set.add(Material.LARGE_FERN);
        set.add(Material.TALL_SEAGRASS);

        //redstone
        set.add(Material.COMPARATOR);
        set.add(Material.REPEATER);
        set.add(Material.REDSTONE_TORCH);
        set.add(Material.REDSTONE_WIRE);

        //some farm
        set.add(Material.SUGAR_CANE);
        set.add(Material.WHEAT);
        set.add(Material.WHEAT_SEEDS);
        set.add(Material.PUMPKIN_STEM);
        set.add(Material.PUMPKIN_SEEDS);
        set.add(Material.POTATO);
        set.add(Material.BEETROOT_SEEDS);
        set.add(Material.BEETROOT);
        set.add(Material.MELON_SEEDS);
        set.add(Material.MELON_STEM);

        set.add(Material.VINE);

        //banners
        for(Material material : Material.values())
        {

            if(material.getKey().getKey().toLowerCase().endsWith("_banner"))
                set.add(material);
            else if(material.getKey().getKey().toLowerCase().endsWith("_coral"))
                set.add(material);
            else  if (material.getKey().getKey().toLowerCase().endsWith("_coral_fan"))
                set.add(material);
            else  if (material.getKey().getKey().toLowerCase().endsWith("_pressure_plate"))
                set.add(material);
            else  if (material.getKey().getKey().toLowerCase().endsWith("_button"))
                set.add(material);
        }
    }

    public static Set getLookingSet() {
        return set;
    }

    public static String stringBuilder(String[] args, int n, String concat, String lastConcatMulti, String lastConcat) {

        String string = "";
        for (int i = n; i < args.length; i++) {

            if (args[i] == null || args[i].isEmpty())
                continue;

            if (i == n)
                string += args[i];
            else if (i == args.length - 1)
                if (args.length - 1 - 2 >= n)
                    string += lastConcatMulti + args[i];
                else if (args.length - 1 - 1 >= n)
                    string += lastConcat + args[i];
                else
                    string += args[i];
            else
                string += concat + args[i];
        }
        return string;
    }

    public static void playSound(Player[] players, Sound sound) {
        for (Player player : players) {
            player.playSound(player.getLocation(), sound, 10F, 0F);
        }
    }

    public static String[] getNearest(String name, String[] collection, int maxMatches) {

        HashMap<String, Double> mappedProbabilities = new HashMap<>();

        for (String iname : collection) {
            double probability = similarity(name.toUpperCase(), iname.toUpperCase());
            if (probability < 0.25)
                continue;
            mappedProbabilities.put(iname, new Double(probability));
        }

        Comparator<Map.Entry<String, Double>> valueComparator = (e1, e2) -> {
            Double v1 = e1.getValue();
            Double v2 = e2.getValue();
            return v2.compareTo(v1);
        };

        if (mappedProbabilities.isEmpty())
            return new String[0];

        // Sort method needs a List, so let's first convert Set to List in Java
        List<Map.Entry<String, Double>> listOfEntries = new ArrayList<>(mappedProbabilities.entrySet());

        // sorting HashMap by values using comparator
        Collections.sort(listOfEntries, valueComparator);

        if (listOfEntries.isEmpty())
            return null;

        String[] resultArray;
        if (listOfEntries.size() < maxMatches)
            resultArray = new String[listOfEntries.size()];
        else
            resultArray = new String[maxMatches];

        for (int i = 0; i < listOfEntries.size(); i++) {
            if (i == resultArray.length - 1)
                break;
            resultArray[i] = listOfEntries.get(i).getKey();
        }


        return resultArray;
    }

    public static Material[] getNearestMaterials(String name, int maxMaterials) {

        HashMap<Material, Double> mappedProbabilities = new HashMap<>();
        for (Material material : Material.values()) {
            String materialName = material.getKey().getKey().replace("_", " ");
            if (materialName.contains("LEGACY"))
                continue;
            double probability = similarity(name.toUpperCase(), materialName);
            if (probability < 0.25)
                continue;
            mappedProbabilities.put(material, new Double(probability));
        }

        Comparator<Map.Entry<Material, Double>> valueComparator = new Comparator<Map.Entry<Material, Double>>() {

            @Override
            public int compare(Map.Entry<Material, Double> e1, Map.Entry<Material, Double> e2) {
                Double v1 = e1.getValue();
                Double v2 = e2.getValue();
                return v2.compareTo(v1);
            }
        };

        if (mappedProbabilities.isEmpty())
            return null;

        // Sort method needs a List, so let's first convert Set to List in Java
        List<Map.Entry<Material, Double>> listOfEntries = new ArrayList<Map.Entry<Material, Double>>(mappedProbabilities.entrySet());

        // sorting HashMap by values using comparator
        Collections.sort(listOfEntries, valueComparator);

        if (listOfEntries.isEmpty())
            return null;
        Material[] materials;
        if (listOfEntries.size() < maxMaterials)
            materials = new Material[listOfEntries.size()];
        else
            materials = new Material[maxMaterials];

        for (int i = 0; i < listOfEntries.size(); i++) {
            if (i == materials.length - 1)
                break;
            materials[i] = listOfEntries.get(i).getKey();
        }


        return materials;
    }

    public static PotionEffectType[] getNearestPotionEffectTypes(String name, int maxTypes) {

        HashMap<PotionEffectType, Double> mappedProbabilities = new HashMap<>();
        for (PotionEffectType potionEffectType : PotionEffectType.values()) {
            String materialName = potionEffectType.getName().replace("_", " ");
            if (materialName.contains("LEGACY"))
                continue;
            double probability = similarity(name.toUpperCase(), materialName);
            if (probability < 0.25)
                continue;
            mappedProbabilities.put(potionEffectType, new Double(probability));
        }

        Comparator<Map.Entry<PotionEffectType, Double>> valueComparator = new Comparator<Map.Entry<PotionEffectType, Double>>() {

            @Override
            public int compare(Map.Entry<PotionEffectType, Double> e1, Map.Entry<PotionEffectType, Double> e2) {
                Double v1 = e1.getValue();
                Double v2 = e2.getValue();
                return v2.compareTo(v1);
            }
        };

        if (mappedProbabilities.isEmpty())
            return null;

        // Sort method needs a List, so let's first convert Set to List in Java
        List<Map.Entry<PotionEffectType, Double>> listOfEntries = new ArrayList<Map.Entry<PotionEffectType, Double>>(mappedProbabilities.entrySet());

        // sorting HashMap by values using comparator
        Collections.sort(listOfEntries, valueComparator);

        if (listOfEntries.isEmpty())
            return null;
        PotionEffectType[] potionEffectTypes;
        if (listOfEntries.size() < maxTypes)
            potionEffectTypes = new PotionEffectType[listOfEntries.size()];
        else
            potionEffectTypes = new PotionEffectType[maxTypes];

        for (int i = 0; i < listOfEntries.size(); i++) {
            if (i == potionEffectTypes.length - 1)
                break;
            potionEffectTypes[i] = listOfEntries.get(i).getKey();
        }


        return potionEffectTypes;
    }

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */
        }
    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static int getDay(String nameOfDay) {
        switch (nameOfDay.toLowerCase()) {
            case "mon":
                return Calendar.MONDAY;
            case "tue":
                return Calendar.TUESDAY;
            case "wed":
                return Calendar.WEDNESDAY;
            case "thu":
                return Calendar.THURSDAY;
            case "fri":
                return Calendar.FRIDAY;
            case "sat":
                return Calendar.SATURDAY;
            case "sun":
                return Calendar.SUNDAY;
        }
        return -1;
    }

    public static String getDay(int numOfDay) {
        switch (numOfDay) {
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thu";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            case Calendar.SUNDAY:
                return "Sun";
        }
        return null;
    }

    public static void localBroadcast(Player localPlayer, String name) {


        ArrayList<Player> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(localPlayer.getWorld()))
                continue;
            if (player.getLocation().distance(localPlayer.getLocation()) <= 60)
                players.add(player);
        }

        Status status = StatusPlayer.wrap(localPlayer.getUniqueId()).getStatus();

        for (Player lp : players) {
            lp.sendMessage(ChatColor.AQUA + localPlayer.getName() + ChatColor.AQUA + " chants \"" + name + "\".");
        }
    }

    public static boolean isPlayerInduced(Entity defender, Entity damager) {
        if (!(defender instanceof Player))
            return false;

        if (damager instanceof Player)
            return true;

        else if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)
            return true;

        else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(damager.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Player getPlayerInduced(Entity defender, Entity damager) {
        if (!(defender instanceof Player))
            return null;

        if (damager instanceof Player)
            return (Player) damager;

        else if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)
            return (Player) ((Projectile) damager).getShooter();

        else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(damager.getName())) {
                    return player;
                }
            }
        }
        return null;
    }

    public static boolean isPlayerDamagerONLY(Entity defender, Entity damager) {
        if (defender instanceof Player)
            return false;

        if (damager instanceof Player)
            return true;

        else if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)
            return true;

        else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(damager.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Player getPlayerDamagerONLY(Entity defender, Entity damager) {


        if (defender instanceof Player)
            return null;

        if (damager instanceof Player)
            return (Player) damager;

        else if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)
            return (Player) ((Projectile) damager).getShooter();

        else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(damager.getName())) {
                    return player;
                }
            }
        }
        return null;
    }

    public static boolean isCarryingSword(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        switch (item.getType()) {
            case DIAMOND_SWORD:
            case GOLDEN_SWORD:
            case IRON_SWORD:
            case STONE_SWORD:
            case WOODEN_SWORD:
                return true;
            default:
                return false;
        }
    }

    public static ZonedDateTime toZDT(long seconds) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime unbanDate = now.plusSeconds(seconds);
        return unbanDate;
    }

    public static ZonedDateTime toZDT(int years, int months, int weeks, int days, int hours, int minutes, int seconds) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime nextRun = now.plusYears(years).plusMonths(months).plusWeeks(weeks).plusDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        return nextRun;
    }

    public static ZonedDateTime toZDT(int[] props) {
        if (props == null)
            return null;
        return toZDT(props[0], props[1], props[2], props[3], props[4], props[5], props[6]);
    }


    public static String getTimeLeft(ZonedDateTime zonedDateTime) {

        ZonedDateTime now = ZonedDateTime.now();
        long left = Duration.between(now, zonedDateTime).toMillis();

        String[] splitTime = getTimeLeft(left).split(", ");
        if (splitTime.length == 1)
            return splitTime[0];
        return splitTime[0] + " " + splitTime[1];

    }

    private static String getTimeLeft(long left) {

        final long ms = 1;
        final long sec = ms * 1000;
        final long min = sec * 60;
        final long hour = min * 60;
        final long day = hour * 24;
        final long week = day * 7;

        long weeks = left / week;
        long weeksRemaining = left % week;
        long days = weeksRemaining / day;
        long daysRemaining = weeksRemaining % day;
        long hours = daysRemaining / hour;
        long hoursRemaining = daysRemaining % hour;
        long minutes = hoursRemaining / min;
        long minutesRemaining = hoursRemaining % min;
        long seconds = minutesRemaining / sec;
        long secondsRemaining = minutesRemaining % sec;
        long milliseconds = secondsRemaining / ms;
        String result = "";
        if (weeks != 0)
            if (result.isEmpty())
                result += weeks + " week(s)";
            else
                result += ", " + weeks + " week(s)";
        if (days != 0)
            if (result.isEmpty())
                result += days + " day(s)";
            else
                result += ", " + days + " day(s)";
        if (hours != 0)
            if (result.isEmpty())
                result += hours + " hour(s)";
            else
                result += ", " + hours + " hour(s)";
        if (minutes != 0)
            if (result.isEmpty())
                result += minutes + " minute(s)";
            else
                result += ", " + minutes + " minute(s)";

        if (seconds != 0)
            if (result.isEmpty())
                result += seconds + " second(s)";
            else
                result += ", " + seconds + " second(s)";

        if (milliseconds != 0)
            if (result.isEmpty())
                result += milliseconds + " millisecond(s)";
            else
                result += ", " + milliseconds + " millisecond(s)";


        String[] words = result.split(", ");
        if (words.length == 1)
            return result;
        else {
            words[words.length - 1] = "" + words[words.length - 1];

            return HelperMethods.stringBuilder(words, 0, ", ");
        }
    }

    public static String until(ZonedDateTime of) {
        int hour = of.getHour();
        String ampm = "";

        if (hour == 0) {
            hour = 12;
            ampm = "a.m.";
        } else if (1 <= hour && hour <= 11) {
            hour = hour;
            ampm = "a.m.";
        } else if (hour == 12) {
            hour = 12;
            ampm = "p.m.";
        } else if (13 <= hour && hour <= 23) {
            hour = hour - 12;
            ampm = "p.m.";
        }

        String monthName = of.getMonth().name();
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

        String minuteName = new DecimalFormat("#00").format(of.getMinute());

        return of.getDayOfMonth() + " " + monthName + " " + of.getYear() + " " + hour + ":" + minuteName + ampm + " EST";
    }

    public static void sendToAll(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

}
