package com.github.kotooriiii.util;

import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

public final class HelperMethods {

    private static Set<Material> set = new HashSet<>();
    private static Set<Material> ignoreLiquidsSet = new HashSet<>();

    private final static int CHAT_CENTER_PX = 154, BOOK_CENTER_PX = 46;


    public enum CenteredType {
        CHAT, BOOK
    }

    private HelperMethods() {
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
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

    public static String getCenteredMessage(CenteredType type, boolean isUnderlined, String message) {
        if (message == null || message.equals("")) {
            return "";
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode == true) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                } else isBold = false;
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int CENTER_PX;
        switch (type) {
            default:
            case CHAT:
                CENTER_PX = HelperMethods.CHAT_CENTER_PX;
                break;
            case BOOK:
                CENTER_PX = HelperMethods.BOOK_CENTER_PX;
                break;
        }
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return isUnderlined ? sb.toString() + ChatColor.UNDERLINE + message : sb.toString() + message;
    }

    public static String getCenteredMessage(String message) {
        return getCenteredMessage(CenteredType.CHAT, false, message);
    }


    public static void initLookingSet() {
        //air and liquids
        set.add(Material.AIR);
        set.add(Material.CAVE_AIR);
        set.add(Material.VOID_AIR);

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
        for (Material material : Material.values()) {

            if (material.getKey().getKey().toLowerCase().endsWith("_banner"))
                set.add(material);
            else if (material.getKey().getKey().toLowerCase().endsWith("_coral"))
                set.add(material);
            else if (material.getKey().getKey().toLowerCase().endsWith("_coral_fan"))
                set.add(material);
            else if (material.getKey().getKey().toLowerCase().endsWith("_pressure_plate"))
                set.add(material);
            else if (material.getKey().getKey().toLowerCase().endsWith("_button"))
                set.add(material);
        }
    }

    public static void initIgnoreLiquids() {
        //air and liquids
        ignoreLiquidsSet.add(Material.AIR);
        ignoreLiquidsSet.add(Material.CAVE_AIR);
        ignoreLiquidsSet.add(Material.VOID_AIR);

        ignoreLiquidsSet.add(Material.WATER);
        ignoreLiquidsSet.add(Material.LAVA);


        //sapling
        ignoreLiquidsSet.add(Material.ACACIA_SAPLING);
        ignoreLiquidsSet.add(Material.BAMBOO_SAPLING);
        ignoreLiquidsSet.add(Material.BIRCH_SAPLING);
        ignoreLiquidsSet.add(Material.DARK_OAK_SAPLING);
        ignoreLiquidsSet.add(Material.JUNGLE_SAPLING);
        ignoreLiquidsSet.add(Material.OAK_SAPLING);
        ignoreLiquidsSet.add(Material.SPRUCE_SAPLING);
        ignoreLiquidsSet.add(Material.GRASS);
        ignoreLiquidsSet.add(Material.FERN);
        ignoreLiquidsSet.add(Material.DEAD_BUSH);
        ignoreLiquidsSet.add(Material.SEAGRASS);
        ignoreLiquidsSet.add(Material.DANDELION);
        ignoreLiquidsSet.add(Material.POPPY);
        ignoreLiquidsSet.add(Material.BLUE_ORCHID);
        ignoreLiquidsSet.add(Material.ALLIUM);
        ignoreLiquidsSet.add(Material.AZURE_BLUET);
        ignoreLiquidsSet.add(Material.RED_TULIP);
        ignoreLiquidsSet.add(Material.ORANGE_TULIP);
        ignoreLiquidsSet.add(Material.WHITE_TULIP);
        ignoreLiquidsSet.add(Material.PINK_TULIP);
        ignoreLiquidsSet.add(Material.OXEYE_DAISY);
        ignoreLiquidsSet.add(Material.CORNFLOWER);
        ignoreLiquidsSet.add(Material.LILY_OF_THE_VALLEY);
        ignoreLiquidsSet.add(Material.WITHER_ROSE);
        ignoreLiquidsSet.add(Material.BROWN_MUSHROOM);
        ignoreLiquidsSet.add(Material.RED_MUSHROOM);
        ignoreLiquidsSet.add(Material.TORCH);
        ignoreLiquidsSet.add(Material.SUNFLOWER);
        ignoreLiquidsSet.add(Material.LILAC);
        ignoreLiquidsSet.add(Material.ROSE_BUSH);
        ignoreLiquidsSet.add(Material.PEONY);
        ignoreLiquidsSet.add(Material.TALL_GRASS);
        ignoreLiquidsSet.add(Material.LARGE_FERN);
        ignoreLiquidsSet.add(Material.TALL_SEAGRASS);

        //redstone
        ignoreLiquidsSet.add(Material.COMPARATOR);
        ignoreLiquidsSet.add(Material.REPEATER);
        ignoreLiquidsSet.add(Material.REDSTONE_TORCH);
        ignoreLiquidsSet.add(Material.REDSTONE_WIRE);

        //some farm
        ignoreLiquidsSet.add(Material.SUGAR_CANE);
        ignoreLiquidsSet.add(Material.WHEAT);
        ignoreLiquidsSet.add(Material.WHEAT_SEEDS);
        ignoreLiquidsSet.add(Material.PUMPKIN_STEM);
        ignoreLiquidsSet.add(Material.PUMPKIN_SEEDS);
        ignoreLiquidsSet.add(Material.POTATO);
        ignoreLiquidsSet.add(Material.BEETROOT_SEEDS);
        ignoreLiquidsSet.add(Material.BEETROOT);
        ignoreLiquidsSet.add(Material.MELON_SEEDS);
        ignoreLiquidsSet.add(Material.MELON_STEM);

        ignoreLiquidsSet.add(Material.VINE);

        //banners
        for (Material material : Material.values()) {

            if (material.getKey().getKey().toLowerCase().endsWith("_banner"))
                ignoreLiquidsSet.add(material);
            else if (material.getKey().getKey().toLowerCase().endsWith("_coral"))
                ignoreLiquidsSet.add(material);
            else if (material.getKey().getKey().toLowerCase().endsWith("_coral_fan"))
                ignoreLiquidsSet.add(material);
            else if (material.getKey().getKey().toLowerCase().endsWith("_pressure_plate"))
                ignoreLiquidsSet.add(material);
            else if (material.getKey().getKey().toLowerCase().endsWith("_button"))
                ignoreLiquidsSet.add(material);
        }
    }

    public static Set<Material> getLookingSet() {
        return getLookingSet(true);
    }

    public static Set<Material> getLookingSet(boolean ignoreLiquids) {
        return ignoreLiquids ? ignoreLiquidsSet : set;
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
            case NETHERITE_SWORD:
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

    public static BlockFace getClosestFace(Location start, Location finish) {

        org.bukkit.util.Vector startVector = start.toVector();
        org.bukkit.util.Vector finishVector = finish.toVector();
        Vector angleVector = finishVector.subtract(startVector);

        double angle = Math.atan2(angleVector.getX(), angleVector.getZ()); // Math.atan(angleVector.getX()/(angleVector.getZ()*-1));

        double yaw = (angle * 180) / Math.PI;

        yaw += 270;

        if (yaw > 360) {
            yaw = yaw - 360;
        }


        BlockFace compassDir = null;


        if (0 <= yaw && yaw < 22.5) {
            compassDir = BlockFace.EAST;
        } else if (22.5 <= yaw && yaw < 67.5) {
            compassDir = BlockFace.NORTH_EAST;
        } else if (67.5 <= yaw && yaw < 112.5) {
            compassDir = BlockFace.NORTH;
        } else if (112.5 <= yaw && yaw < 157.5) {
            compassDir = BlockFace.NORTH_WEST;
        } else if (157.5 <= yaw && yaw < 202.5) {
            compassDir = BlockFace.WEST;
        } else if (202.5 <= yaw && yaw < 247.5) {
            compassDir = BlockFace.SOUTH_WEST;
        } else if (247.5 <= yaw && yaw < 292.5) {
            compassDir = BlockFace.SOUTH;
        } else if (292.5 <= yaw && yaw < 337.5) {
            compassDir = BlockFace.SOUTH_EAST;
        } else if (337.5 <= yaw && yaw <= 360) {
            compassDir = BlockFace.EAST;
        }
        return compassDir;
    }

    public static void customDamage(LivingEntity attacker, LivingEntity defender, int damage) {
        customDamage(attacker, defender, EntityDamageEvent.DamageCause.CUSTOM, damage);
    }

    public static void customDamage(LivingEntity attacker, LivingEntity defender, EntityDamageEvent.DamageCause cause, float damage) {
        EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(attacker, defender, cause, damage);
        defender.setLastDamageCause(damageByEntityEvent);
        Bukkit.getPluginManager().callEvent(damageByEntityEvent);

        if (!damageByEntityEvent.isCancelled()) {


            double totalHealth = defender.getHealth() + defender.getAbsorptionAmount();
            double totalDamage = damage;

            final double effectiveHealth = totalHealth - totalDamage;

            if (effectiveHealth < 0) {
                defender.setHealth(0);
            } else if (effectiveHealth <= 20) {
                defender.setHealth(effectiveHealth);
            } else {
                double health = 20d;
                double absorption = effectiveHealth - health;
                defender.setHealth(health);
                if (absorption != 0)
                    defender.setAbsorptionAmount(absorption);
            }
        }
    }

    public static void customDamage(LivingEntity defender, EntityDamageEvent.DamageCause cause, int damage) {
        EntityDamageEvent damageByEntityEvent = new EntityDamageEvent(defender, cause, damage);
        defender.setLastDamageCause(damageByEntityEvent);
        Bukkit.getPluginManager().callEvent(damageByEntityEvent);

        if (!damageByEntityEvent.isCancelled()) {


            double totalHealth = defender.getHealth() + defender.getAbsorptionAmount();
            double totalDamage = damage;

            final double effectiveHealth = totalHealth - totalDamage;

            if (effectiveHealth < 0) {
                defender.setHealth(0);
            } else if (effectiveHealth <= 20) {
                defender.setHealth(effectiveHealth);
            } else {
                double health = 20d;
                double absorption = effectiveHealth - health;
                defender.setHealth(health);
                if (absorption != 0)
                    defender.setAbsorptionAmount(absorption);
            }
        }
    }

}
