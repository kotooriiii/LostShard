package com.github.kotooriiii.plots.struct;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.plots.listeners.SignChangeListener;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static com.github.kotooriiii.plots.PlotManager.allPlots;

public class PlayerPlot extends Plot {

    /**
     * The plot's owner.
     */
    private UUID ownerUUID;
    /**
     * The plot's center location
     */
    private Location center;

    /**
     * The default radius of a plot when first created
     */
    private final static int defaultRadius = LostShardPlugin.isTutorial() ? 3 : 5;
    /**
     * The plot's current radius.
     */
    private int radius;

    /**
     * The plot's current funds.
     */
    private double balance;

    /**
     * The plot's friends
     */
    private ArrayList<UUID> friends;
    /**
     * The plot's co-owners.
     */
    private ArrayList<UUID> jointOwners;
    private boolean isTown;
    private boolean isDungeon;

    /**
     * The cost to create a plot
     */
    public static final int CREATE_COST = 10;
    /**
     * The refund rate you receive when you disband a plot.
     */
    public static final double REFUND_RATE = 0.75;

    public PlayerPlot(String name, UUID playerOwner, Location center) {

        super(center.getWorld(), name);

        this.ownerUUID = playerOwner;
        this.center = center;
        this.radius = defaultRadius;

        this.zone = calculateZone();
        this.plotType = PlotType.PLAYER;

        this.balance = 0;
        this.friends = new ArrayList<>();
        this.jointOwners = new ArrayList<>();

        isTown = false;
        isDungeon = false;
    }

    @Override
    protected Zone calculateZone() {
        Location center = this.getCenter();

        //Coordinates of the center block location
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();

        //Take into account the radius of the plot respective to x-axis
        int minX = centerX - radius;
        int maxX = centerX + radius;

        //Take into account the height of the plot.
        int minY = 0; //The lowest can only ever be 0.
        int maxY = center.getWorld().getMaxHeight(); //This is able to be changed later so make sure we use a method to take this into account

        //Take into account the radius of the plot respective to x-axis
        int minZ = centerZ - radius;
        int maxZ = centerZ + radius;

        Zone zone = new Zone(minX, maxX, minY, maxY, minZ, maxZ);
        return zone;
    }

    public void sendToMembers(String message) {
        ArrayList<UUID> allMembers = new ArrayList<>();
        allMembers.add(ownerUUID);
        allMembers.addAll(jointOwners);
        allMembers.addAll(friends);

        for (UUID uuid : allMembers) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (!offlinePlayer.isOnline())
                continue;
            offlinePlayer.getPlayer().sendMessage(message);

        }
    }


    /**
     * Checks if the plot is able to be expanded.
     * A plot can only expand if it can grow by one block radius and still have a one block space between plots.
     *
     * @return true if can expand, false otherwise.
     */
    public boolean isExpandable() {

        //Distance trying to expand to
        final int distance = 1;
        //Blocks needed to be spaced (padding)
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


        for (Plot plot : allPlots) {

            int minStaffX = 0;
            int maxStaffX = 0;
            int minStaffZ = 0;
            int maxStaffZ = 0;

            if (!world.equals(plot.getWorld()))
                continue;

            if (plot.equals(this))
                continue;

            if(plot.getType().isStaff())
            {
                minStaffX = MINIMUM_PLOT_EXPAND_RANGE;
                maxStaffX = MINIMUM_PLOT_EXPAND_RANGE;
                minStaffZ = MINIMUM_PLOT_EXPAND_RANGE;
                maxStaffZ = MINIMUM_PLOT_EXPAND_RANGE;
            }
            Zone expandedZone = new Zone(minX + minStaffX, maxX + maxStaffX, minY, maxY, minZ +minStaffZ, maxZ+maxStaffZ);

            if (expandedZone.overlaps(plot.getZone()))
                return false;

        }
        return true;
    }

    /**
     * Disbands the plot. Removes the plot completely invoking the PlotManager#removePlot(Plot plot) method.
     *
     * @return a refund of what the player spent creating the plot and the expansion of the plot.
     */
    public double disband() {
        LostShardPlugin.getPlotManager().removePlot(this);
        return refund();
    }

    /**
     * Calculates the money spent on the plot.
     *
     * @return money spent on plot (plot create cost AND size of plot)
     */
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

    /**
     * Gets the plot's tax. The plot tax is determined by plot size.
     *
     * @return plot's tax
     */
    public double getTax() {
        double goldPerPlot;
        if (radius <= defaultRadius) {
            goldPerPlot = 1;
        } else {
            goldPerPlot = (radius - defaultRadius);
        }
        return goldPerPlot;
    }

    /**
     * Uses the plot's funds to pay the daily rent. If the plot doesn't have the necessary funds to pay taxes, it shrinks to the size before.
     *
     * @return true if the plot had enough funds, false if the plot didn't have the funds to pay for tax, consequently, shrinking.
     */
    public boolean rent() {

        if (!RankPlayer.wrap(ownerUUID).getRankType().isObligatedRent())
            return true;


        if (getBalance() < getTax()) {
            this.balance = 0;
            shrink();
            return false;
        }
        this.balance = this.balance - getTax();
        LostShardPlugin.getPlotManager().savePlot(this);
        return true;
    }

    /**
     * Gets the cost to expand your plot to the next size
     *
     * @return the cost to expand plot
     */
    public double getExpandCost() {
        //If the plot is between size 5 (inclusive) and
        if (0 <= radius && radius < 10) {
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
        setBalance(this.balance - getExpandCost());
        setRadius(this.radius + 1);
        LostShardPlugin.getPlotManager().savePlot(this);
    }

    public void shrink() {
        setRadius(this.radius - 1);
        if (this.getRadius() == 0) {
            disband();
        } else {
            LostShardPlugin.getPlotManager().savePlot(this);
        }
    }

    //Balance management

    public double deposit(double deposit) {
        this.balance += deposit;
        LostShardPlugin.getPlotManager().savePlot(this);
        return this.balance;
    }

    public double withdraw(double withdraw) {
        this.balance -= withdraw;
        LostShardPlugin.getPlotManager().savePlot(this);
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
        LostShardPlugin.getPlotManager().savePlot(this);

        return isSuccessful;
    }

    public boolean removeFriend(UUID playerUUID) {
        if (!isFriend(playerUUID))
            return false;

        boolean isSuccessful = friends.remove(playerUUID);
        LostShardPlugin.getPlotManager().savePlot(this);

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

        LostShardPlugin.getPlotManager().savePlot(this);

        return isSuccessful;
    }

    public boolean removeJointOwner(UUID playerUUID) {
        if (!isJointOwner(playerUUID))
            return false;

        boolean isSuccessful = jointOwners.remove(playerUUID);

        LostShardPlugin.getPlotManager().savePlot(this);

        return isSuccessful;
    }

    public boolean isOwner(UUID playerUUID) {
        if (ownerUUID == null)
            return false;
        return ownerUUID.equals(playerUUID);
    }

    public boolean isTown() {
        return this.isTown;
    }

    public boolean isDungeon() {
        return this.isDungeon;
    }


    @Override
    public String info(Player perspectivePlayer) {

        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);


        ArrayList<OfflinePlayer> friends = new ArrayList<>();
        for (UUID friendUUID : this.getFriends()) {
            friends.add(Bukkit.getOfflinePlayer(friendUUID));
        }
        OfflinePlayer[] friendsArray = friends.toArray(new OfflinePlayer[friends.size()]);


        ArrayList<OfflinePlayer> jointOwners = new ArrayList<>();
        for (UUID jointOwnerUUID : this.getJointOwners()) {
            jointOwners.add(Bukkit.getOfflinePlayer(jointOwnerUUID));
        }
        OfflinePlayer[] jointOwnersArray = jointOwners.toArray(new OfflinePlayer[jointOwners.size()]);

        DecimalFormat df = new DecimalFormat("#.##");

        //Header
        String header = ChatColor.GOLD + "-" + this.getName() + "'s Plot Info-";

        //Relationship to plot
        String relationshipToPlot = "";
        if (isFriend(perspectivePlayer.getUniqueId()))
            relationshipToPlot = ChatColor.YELLOW + "\nYou are a friend of this plot's owner.";
        if (isJointOwner(perspectivePlayer.getUniqueId()))
            relationshipToPlot = ChatColor.YELLOW + "\nYou are a co-owner of this plot.";
        if (this.ownerUUID.equals(perspectivePlayer.getUniqueId()))
            relationshipToPlot = ChatColor.YELLOW + "\nYou are the owner of this plot.";

        String ownerString = ChatColor.YELLOW + "\nOwner: " + ChatColor.WHITE + owner.getName();

        String size = ChatColor.YELLOW + "\nSize: " + ChatColor.WHITE + this.getRadius() * 2;

        String privacy = "";
        if (!relationshipToPlot.isEmpty()) {

            if (RankPlayer.wrap(ownerUUID).getRankType().isObligatedRent())
                privacy = ChatColor.YELLOW + ", Funds: " + ChatColor.WHITE + df.format(getBalance()) + ChatColor.YELLOW + ", Tax: " + ChatColor.WHITE + df.format(getTax()) + "\n"
                        + ChatColor.GRAY + "(" + daysLeft() + ")";
            else
                privacy = ChatColor.YELLOW + ", Funds: " + ChatColor.WHITE + df.format(getBalance()) + ChatColor.YELLOW + ", Tax: " + ChatColor.WHITE + "EXEMPT";
        }
        String location = ChatColor.YELLOW + "\nCenter: " + ChatColor.WHITE + "(" + center.getBlockX() + ", " + center.getBlockY() + ", " + center.getBlockZ() + ") " + ChatColor.YELLOW + "Distance from Center: " + ChatColor.WHITE + getRadius();

        String jointOwnerConcat = ChatColor.YELLOW + "\nYou are not a friend of this plot.";
        String friendsConcat = "";
        String signBuilder = "";
        String statuses = "";


        //Show coowner and friends

        if (!relationshipToPlot.isEmpty()) {
            statuses = ChatColor.YELLOW + "\nTown Status: " + ChatColor.WHITE + this.isTown();
            statuses += ChatColor.YELLOW + "\nDungeon Status: " + ChatColor.WHITE + this.isDungeon();

            Location signBuildLoc = SignChangeListener.getSignBuilder(perspectivePlayer.getLocation());
            if (signBuildLoc != null)
                signBuilder = ChatColor.YELLOW + "\nBuild Changer: " + ChatColor.WHITE + "(" + signBuildLoc.getBlockX() + ", " + signBuildLoc.getBlockY() + ", " + signBuildLoc.getBlockZ() + ")";
            else
                signBuilder = ChatColor.YELLOW + "\nBuild Changer: " + ChatColor.WHITE + "NONE";


            jointOwnerConcat = ChatColor.YELLOW + "\nCo-owners: " + ChatColor.WHITE;
            for (int i = 0; i < jointOwnersArray.length; i++) {
                if (i == 0)
                    jointOwnerConcat += jointOwnersArray[i].getName();
                else
                    jointOwnerConcat += ", " + jointOwnersArray[i].getName();

            }

            friendsConcat = ChatColor.YELLOW + "\nFriends: " + ChatColor.WHITE;
            for (int i = 0; i < friendsArray.length; i++) {
                if (i == 0)
                    friendsConcat += friendsArray[i].getName();
                else
                    friendsConcat += ", " + friendsArray[i].getName();
            }
        }


        return header + relationshipToPlot + ownerString + size  + privacy + location +  statuses + signBuilder + jointOwnerConcat + friendsConcat;
    }

    private String daysLeft() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

        int times = (int) Math.floor(getBalance() / getTax()) + 1;

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

    //Getter /setter

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public Location getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        setZone(calculateZone());
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public void setFriends(ArrayList<UUID> friends) {
        this.friends = friends;
    }

    public void setJointOwners(ArrayList<UUID> jointOwners) {
        this.jointOwners = jointOwners;
    }


    public void setTown(boolean b) {
        this.isTown = b;
    }

    public void setDungeon(boolean b) {
        this.isDungeon = b;
    }

    public static int getDefaultRadius() {
        return defaultRadius;
    }
}
