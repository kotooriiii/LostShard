package com.github.kotooriiii.plots;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static com.github.kotooriiii.data.Maps.platforms;

public class Plot implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private String name;
    private boolean isStaff;
    private UUID ownerUUID;
    private Zone zone;

    private PointBlock center;

    private final static int defaultRadius = 5;
    private int radius;

    private double balance;

    private ArrayList<UUID> friends;
    private ArrayList<UUID> jointOwners;

    public static final int CREATE_COST = 10;
    public static final double REFUND_RATE = 0.75;
    public static final int MINIMUM_PLOT_CREATE_RANGE = 10;

    public static HashMap<UUID, Plot> playerPlots = new HashMap<>();
    public static ArrayList<Plot> allPlots = new ArrayList<>();

    protected class PointBlock implements Serializable {
        private static final long serialVersionUID = 1L;

        private String world;
        private int x;
        private int y;
        private int z;
        private float pitch;
        private float yaw;

        public PointBlock(Location location) {
            this(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getPitch(), location.getYaw());
            world = location.getWorld().getName();
        }

        public PointBlock(World world, int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.pitch = 0;
            this.yaw = 0;
           this.world = world.getName();
        }

        public PointBlock(int x, int y, int z, float pitch, float yaw) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.pitch = pitch;
            this.yaw = yaw;
            world = "world";
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public float getPitch() {
            return pitch;
        }

        public float getYaw() {
            return yaw;
        }

        public Location getLocation() {
            if(world == null)
                world="world";
            return new Location(Bukkit.getWorld(world), x + 0.5, y, z + 0.5, yaw, pitch);
        }
    }

    public Plot(Player player, String name) {

        boolean isUnique = false;

        uniqueLoop:
        while (!isUnique) {
            UUID possibleID = UUID.randomUUID();
            plotLoop:
            for (Plot plot : allPlots) {
                if (plot.getID().equals(possibleID))
                    continue uniqueLoop;

            }
            this.id = possibleID;
            isUnique = true;
        }

        this.name = name;
        this.ownerUUID = player.getUniqueId();
        this.center = new PointBlock(player.getLocation());
        this.radius = defaultRadius;
        this.zone = getCalculatedZone();
        this.balance = 0;
        this.friends = new ArrayList<>();
        this.jointOwners = new ArrayList<>();
        playerPlots.put(ownerUUID, this);
        allPlots.add(this);
        FileManager.write(this);
    }

    public Plot(World world, Zone zone, String name) {

        boolean isUnique = false;

        uniqueLoop:
        while (!isUnique) {
            UUID possibleID = UUID.randomUUID();
            plotLoop:
            for (Plot plot : allPlots) {
                if (plot.getID().equals(possibleID))
                    continue uniqueLoop;

            }
            this.id = possibleID;
            isUnique = true;
        }

        this.name = name;
        this.ownerUUID = null;
        isStaff = true;
        this.center = new PointBlock(world, -1, -1, -1);
        this.radius = -1;
        this.zone = zone;
        this.balance = -1;
        this.friends = new ArrayList<>();
        this.jointOwners = new ArrayList<>();
        allPlots.add(this);
        FileManager.write(this);
    }

    public void setSpawn(Location location) {
        this.center = new PointBlock(location);
        FileManager.write(this);
    }

    //Set the zone variable
    private Zone getCalculatedZone() {
        Location center = this.getCenter();
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();

        int minX = centerX - radius;
        int maxX = centerX + radius;

        int minY = 0;
        int maxY = 256;

        int minZ = centerZ - radius;
        int maxZ = centerZ + radius;

        Zone zone = new Zone(minX, maxX, minY, maxY, minZ, maxZ);
        return zone;
    }

    //Is player in plot
    public boolean contains(Player player) {
        return contains(player.getLocation());
    }

    //Is location in plot
    public boolean contains(Location location) {
        return contains(location.getBlock());
    }

    //Is block in plot
    public boolean contains(Block block) {

        if(!block.getWorld().equals(this.center.getLocation().getWorld()))
            return false;

        return this.zone.contains(block);
    }

    //Is next to plot
    public boolean isNearby(Player player) {

        if(!this.center.getLocation().getWorld().equals(player.getLocation().getWorld()))
            return false;

        final int distance = MINIMUM_PLOT_CREATE_RANGE;

        Location center = this.getCenter();
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();

        int minX = centerX - radius - distance;
        int maxX = centerX + radius + distance;

        int minY = 0;
        int maxY = 256;

        int minZ = centerZ - radius - distance;
        int maxZ = centerZ + radius + distance;

        Zone zone = new Zone(minX, maxX, minY, maxY, minZ, maxZ);
        if (zone.contains(player.getLocation().getBlock()))
            return true;
        return false;
    }

    //Is expandable? so no collision with other plots occur
    public boolean isExpandable() {

        final int distance = 1;
        final int leeway = 1;

        Location center = this.getCenter();
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();

        int minX = centerX - radius - distance - leeway;
        int maxX = centerX + radius + distance + leeway;

        int minY = 0;
        int maxY = 256;

        int minZ = centerZ - radius - distance - leeway;
        int maxZ = centerZ + radius + distance + leeway;

        Zone expandedZone = new Zone(minX, maxX, minY, maxY, minZ, maxZ);
        for (Plot plot : allPlots) {

            if(!this.center.getLocation().getWorld().equals(plot.getCenter().getWorld()))
               continue;

            if (plot.equals(this))
                continue;
            if (expandedZone.overlaps(plot.getZone()))
                return false;

        }
        return true;
    }

    //Disbanding and getting refund
    public double disband() {
        if(!isStaff)
            playerPlots.remove(ownerUUID);
        allPlots.remove(this);
        FileManager.removeFile(this);
        return refund();
    }

    private double refund() {
        final double percentage = REFUND_RATE;
        double adder = 0;

        for (int i = radius; i > defaultRadius; i--) {
            if (defaultRadius < radius && radius <= 10) {
                adder += 5;
            } else if (radius <= 20) {
                adder += 10;
            } else if (radius <= 30) {
                adder += 20;
            } else if (radius <= 40) {
                adder += 30;
            } else if (radius <= 50) {
                adder += 40;
            }
        }
        return percentage * (adder + CREATE_COST);
    }

    //Get tax rate
    public double getTax() {
        double goldPerPlot;
        if (radius <= defaultRadius) {
            goldPerPlot = 1;
        } else {
            goldPerPlot = (radius - defaultRadius);
        }


        return goldPerPlot;
    }

    public boolean rent() {
        if (getBalance() < getTax()) {
            this.balance = 0;
            shrink();
            return false;
        }
        this.balance = this.balance - getTax();
        FileManager.write(this);
        return true;
    }

    //ExpandCost
    public double getExpandCost() {
        //If the plot is between size 5 (inclusive) and
        if (1 <= radius && radius < 10) {
            return 5;
        } else if (radius < 20) {
            return 10;
        } else if (radius < 30) {
            return 20;
        } else if (radius < 40) {
            return 30;
        } else if (radius < 50) {
            return 40;
        } else {
            return 40;
        }
    }

    //Expand management
    public void expand() {
        this.balance -= getExpandCost();
        this.radius++;
        this.zone = getCalculatedZone();
        FileManager.write(this);

    }

    public void shrink() {
        this.radius--;
        this.zone = getCalculatedZone();
        if (this.radius == 0) {
            disband();
        } else {
            FileManager.write(this);
        }
    }

    //Balance management

    public double deposit(double deposit) {
        this.balance += deposit;
        FileManager.write(this);
        return this.balance;
    }

    public double withdraw(double withdraw) {
        this.balance -= withdraw;
        FileManager.write(this);
        return this.balance;
    }

    //Friend management
    public UUID[] getFriends() {
        return friends.toArray(new UUID[friends.size()]);
    }

    public boolean isFriend(UUID playerUUID) {
        return friends.contains(playerUUID);
    }

    public boolean addFriend(UUID playerUUID) {
        if (isFriend(playerUUID))
            return false;
        if (isJointOwner(playerUUID))
            removeJointOwner(playerUUID);

        boolean isSuccessful = friends.add(playerUUID);
        FileManager.write(this);

        return isSuccessful;
    }

    public boolean removeFriend(UUID playerUUID) {
        if (!isFriend(playerUUID))
            return false;

        boolean isSuccessful = friends.remove(playerUUID);
        FileManager.write(this);

        return isSuccessful;
    }

    //Joint owner management
    public UUID[] getJointOwners() {
        return jointOwners.toArray(new UUID[jointOwners.size()]);
    }

    public boolean isJointOwner(UUID playerUUID) {
        return jointOwners.contains(playerUUID);
    }

    public boolean addJointOwner(UUID playerUUID) {
        if (isJointOwner(playerUUID))
            return false;
        if (isFriend(playerUUID))
            removeFriend(playerUUID);

        boolean isSuccessful = jointOwners.add(playerUUID);

        FileManager.write(this);

        return isSuccessful;
    }

    public boolean removeJointOwner(UUID playerUUID) {
        if (!isJointOwner(playerUUID))
            return false;

        boolean isSuccessful = jointOwners.remove(playerUUID);

        FileManager.write(this);

        return isSuccessful;
    }

    public boolean isOwner(UUID playerUUID) {
        if (ownerUUID == null)
            return false;
        return ownerUUID.equals(playerUUID);
    }

    //BASIC GETTERS

    public boolean isDisbanded() {
        return radius == 0;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public Location getCenter() {
        return center.getLocation();
    }

    public int getRadius() {
        return radius;
    }

    public int getSize() {
        return radius;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public void setStaff(boolean isStaff)
    {
        this.isStaff = isStaff;
    }
    public double getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public Zone getZone() {
        return zone;
    }

    public UUID getID() {
        return id;
    }

    //Info returner
    public String info(Player perspectivePlayer) {

        if (isStaffPlot(this)) {
            return ChatColor.GRAY + "This sacred land belongs to " + this.getName() + ".";
        }

        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);
        ArrayList<OfflinePlayer> friends = new ArrayList<>();
        OfflinePlayer[] friendsArray = friends.toArray(new OfflinePlayer[friends.size()]);
        for (UUID friendUUID : this.getFriends()) {
            friends.add(Bukkit.getOfflinePlayer(friendUUID));
        }
        ArrayList<OfflinePlayer> jointOwners = new ArrayList<>();
        for (UUID jointOwnerUUID : this.getJointOwners()) {
            jointOwners.add(Bukkit.getOfflinePlayer(jointOwnerUUID));
        }
        OfflinePlayer[] jointOwnersArray = jointOwners.toArray(new OfflinePlayer[jointOwners.size()]);

        DecimalFormat df = new DecimalFormat("#.##");

        //Header
        String header = ChatColor.GOLD + "-" + this.name + "'s Plot Info-";

        //Relationship to plot
        String relationshipToPlot = "";
        if (isFriend(perspectivePlayer.getUniqueId()))
            relationshipToPlot = ChatColor.YELLOW + "\nYou are a friend of this plot's owner.";
        if (isJointOwner(perspectivePlayer.getUniqueId()))
            relationshipToPlot = ChatColor.YELLOW + "\nYou are a co-owner of this plot.";
        if (this.ownerUUID.equals(perspectivePlayer.getUniqueId()))
            relationshipToPlot = ChatColor.YELLOW + "\nYou are the owner of this plot.";

        String ownerString = ChatColor.YELLOW + "\nOwner: " + ChatColor.WHITE + owner.getName();

        String size = ChatColor.YELLOW + "\nSize: " + ChatColor.WHITE + getSize();

        String privacy = "";
        if (!relationshipToPlot.isEmpty())
            privacy = ChatColor.YELLOW + ", Funds: " + ChatColor.WHITE + df.format(getBalance()) + ChatColor.YELLOW + ", Tax: " + ChatColor.WHITE + df.format(getTax()) + "\n"
                    + ChatColor.GRAY + "(" + daysLeft() + ")";
        String location = ChatColor.YELLOW + "\nCenter: " + ChatColor.WHITE + "(" + center.getX() + ", " + center.getY() + ", " + center.getZ() + ") " + ChatColor.YELLOW + "Distance from Center: " + ChatColor.WHITE + getRadius();

        String jointOwnerConcat = ChatColor.YELLOW + "\nCo-owners: " + ChatColor.WHITE;
        for (int i = 0; i < jointOwnersArray.length; i++) {
            if (i == 0)
                jointOwnerConcat += jointOwnersArray[i].getName();
            else
                jointOwnerConcat += ", " + jointOwnersArray[i].getName();

        }

        String friendsConcat = ChatColor.YELLOW + "\nFriends: " + ChatColor.WHITE;
        for (int i = 0; i < friendsArray.length; i++) {
            if (i == 0)
                friendsConcat += friendsArray[i].getName();
            else
                friendsConcat += ", " + friendsArray[i].getName();
        }

        return header + relationshipToPlot + ownerString + size + privacy + location + jointOwnerConcat + friendsConcat;
    }

    private String daysLeft() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

        int times = (int) Math.floor(getBalance()/getTax()) + 1;

        if (now.compareTo(nextRun) >= 0)
            nextRun = nextRun.plusDays(times);

        Duration duration = Duration.between(now, nextRun);
        long milliseconds = duration.getSeconds() * 1000;

        String time = toBetterFormat(milliseconds);
        return time + " of funds remaining.";

    }

    private String toBetterFormat(long left) {
        String[] splitTime = getTimeLeft(left).split(", ");
        if (splitTime.length == 1)
            return splitTime[0];

        return splitTime[0] + " and " + splitTime[1];
    }

    private String getTimeLeft(long left) {

        final long ms = 1;
        final long sec = ms * 1000;
        final long min = sec * 60;
        final long hour = min * 60;
        final long day = hour * 24;
        final long week = day * 7;
        final long year;


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

    //Static managers
    public static HashMap<UUID, Plot> getPlayerPlots() {
        return playerPlots;
    }

    public static ArrayList<Plot> getAllPlots() {
        return allPlots;
    }

    public static boolean isStaffPlot(Plot plot) {
        return plot.isStaff;
    }

    public static boolean isPlot(String name) {
        for (Plot plot : allPlots) {
            if (plot.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public static Plot getPlot(String name) {
        for (Plot plot : allPlots) {
            if (plot.getName().equalsIgnoreCase(name))
                return plot;
        }
        return null;
    }

    public static boolean isStaffPlot(String name) {

        //Is reserved for order and chaos
        if (name.equalsIgnoreCase("order") || name.equalsIgnoreCase("chaos") || name.equalsIgnoreCase("arena"))
            return true;

        //Is reserved
        for (HostilityPlatform platform : platforms) {
            if (platform.getName().equalsIgnoreCase(name))
                return true;
        }


        for (Plot plot : allPlots) {
            if (plot.getName().equalsIgnoreCase(name)) {
                if (plot.isStaff)
                    return true;
                else return false;

            }
        }
        return false;
    }

    public static boolean hasPlotName(String name) {

        for (Plot plot : getPlayerPlots().values()) {
            if (plot.getName().equalsIgnoreCase(name))
                return true;
        }

        return false;
    }

    public static boolean hasPlot(Player player) {
        return wrap(player.getUniqueId()) != null;
    }

    public static boolean isStandingOnPlot(Player player) {
        for (Plot plot : allPlots) {
            if (plot.contains(player))
                return true;
        }
        return false;
    }

    public static boolean isStandingOnPlot(Location location) {
        for (Plot plot : allPlots) {
            if (plot.contains(location))
                return true;
        }
        return false;
    }

    public static Plot getStandingOnPlot(Player player) {
        for (Plot plot : allPlots) {
            if (plot.contains(player))
                return plot;
        }
        return null;
    }

    public static Plot getStandingOnPlot(Location location) {
        for (Plot plot : allPlots) {
            if (plot.contains(location))
                return plot;
        }
        return null;
    }

    public static boolean hasNearbyPlots(Player player) {
        for (Plot plot : allPlots) {
            if (plot.isNearby(player))
                return true;
        }
        return false;
    }

    public static void add(Plot plot) {
        if (Plot.isStaffPlot(plot))
            allPlots.add(plot);
        else {
            getPlayerPlots().put(plot.getOwnerUUID(), plot);
            allPlots.add(plot);
        }

    }

    public static Plot wrap(UUID ownerUUID) {
        return playerPlots.get(ownerUUID);
    }
}
