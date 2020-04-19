package com.github.kotooriiii.hostility;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class HostilityPlatform implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private ArrayList<Zone> zones;

    private int[] time;

    public HostilityPlatform(String name) {
        this.name = name;
        this.zones = new ArrayList<>();
        this.time = new int[3];
    }

    public boolean contains(int x, int y, int z) {
        for (Zone zone : getZones()) {
            if (zone.contains(x, y, z)) {
                return true;
            }
        }
        return false;
    }

    public Player[] getPlayers() {
        ArrayList<Player> playersInRegion = new ArrayList<Player>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline() && this.contains(player)) {
                playersInRegion.add(player);
            }
        }

        return playersInRegion.toArray(new Player[playersInRegion.size()]);
    }

    public boolean hasPlayers() {
        return this.getPlayers().length > 0;
    }

    public Player[] getClanlessPlayers() {
        Player[] players = getPlayers();
        ArrayList<Player> clanlessPlayers = new ArrayList<>();
        for (Player player : players) {
            if (Clan.getClan(player.getUniqueId()) == null) {
                clanlessPlayers.add(player);
            }
        }
        return clanlessPlayers.toArray(new Player[clanlessPlayers.size()]);
    }

    public Clan getUniqueClan() {
        Player[] players = getPlayers();

        Clan uniqueClan = null;
        for (int i = 0; i < players.length; i++) {
            if (uniqueClan == null) {
                uniqueClan = Clan.getClan(players[i].getUniqueId());
                if (uniqueClan == null)
                    continue;
            }


            if (!uniqueClan.isInThisClan(players[i].getUniqueId())) {
                Clan clan = Clan.getClan(players[i].getUniqueId());
                if (clan != null)
                    return null;
            }
        }
        return uniqueClan;
    }

    public Player[] getUniqueClanPlayers() {
        Player[] players = getPlayers();
        ArrayList<Player> uniquePlayersInClan = new ArrayList<>();
        Clan uniqueClan = null;
        for (int i = 0; i < players.length; i++) {
            if (uniqueClan == null) {
                uniqueClan = Clan.getClan(players[i].getUniqueId());
                if (uniqueClan == null)
                    continue;
            }

            if (!uniqueClan.isInThisClan(players[i].getUniqueId())) {
                Clan clan = Clan.getClan(players[i].getUniqueId());
                if (clan != null)
                    return null;
            } else {
                uniquePlayersInClan.add(players[i]);
            }
        }
        return uniquePlayersInClan.toArray(new Player[uniquePlayersInClan.size()]);
    }

    public boolean hasUniqueClan() {
        return getUniqueClan() != null;
    }

    public boolean contains(Block block) {
        for (Zone zone : getZones()) {
            if (zone.contains(block)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAdjacency(Location location) {
        for (Zone zone : getZones()) {
            if (zone.hasAdjacency(location)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Player player) {
        return contains(player.getLocation().getBlock());
    }

    //START BASIC GETTER AND SETTER

    public Zone[] getZones() {
        return this.zones.toArray(new Zone[this.zones.size()]);
    }

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public boolean undo() {
        if (this.zones.isEmpty())
            return false;
        this.zones.remove(this.zones.size() - 1);
        return true;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(int[] time) {
        this.time = time;
    }

    public int[] getTime() {
        return time;
    }

    public void runCountdown() {

        Calendar todayCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        Calendar targetCalendar = getMatchCalendar();
        final HostilityPlatform gamePlatform = this;

        double initialDelay = getBetweenInSeconds(todayCalendar, targetCalendar) * 20;

        new BukkitRunnable() {
            @Override
            public void run() {
                HostilityMatch match = new HostilityMatch(gamePlatform);
                match.startGame();
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        runCountdown();
                    }
                }.runTaskLater(LostShardPlugin.plugin, 20);
            }
        }.runTaskLater(LostShardPlugin.plugin, (long) Math.ceil(initialDelay));
    }

    private Calendar getMatchCalendar() {
        //Declare two calendars
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        Calendar targetCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));

        //Format the date
        DateFormat format = DateFormat.getDateTimeInstance();
        format.setTimeZone(calendar.getTimeZone());

        int time[] = this.getTime();
        int dayOfWeek = time[0];
        int hr24format = time[1];
        int minute = time[2];

        //Get the current day
        int currDay = targetCalendar.get(Calendar.DAY_OF_WEEK);
        //Set the day we want.
        int targetDay = dayOfWeek;

        targetCalendar.set(Calendar.DAY_OF_WEEK, targetDay); //Set day
        targetCalendar.set(Calendar.HOUR_OF_DAY, hr24format);
        targetCalendar.set(Calendar.MINUTE, minute);
        targetCalendar.set(Calendar.SECOND, 0);
        targetCalendar.set(Calendar.MILLISECOND, 0);

        //If we substract today and the target time. If it's negative it would mean that we are BEFORE target time. Don't do anything
        if (calendar.getTimeInMillis() - targetCalendar.getTimeInMillis() < 0) {
            //Don't do anything, it's already set

        } else if (calendar.getTimeInMillis() - targetCalendar.getTimeInMillis() >= 0) {
            //Set it to next week's day

            targetCalendar.add(Calendar.WEEK_OF_YEAR, 1);

        }


        return targetCalendar;
    }

    private double getBetweenInSeconds(Calendar from, Calendar to) {
        return (double) (to.getTimeInMillis() - from.getTimeInMillis()) / 1000;
    }

    public String getExactTimeLeft() {

        return toBetterFormat();
    }

    public String toBetterFormat()
    {
        Calendar from = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        Calendar to = getMatchCalendar();
        String[] splitTime =  getTimeLeft(from, to).split(", ");
        if(splitTime.length == 1)
            return splitTime[0];

        return splitTime[0] + " and " + splitTime[1];
    }

    private String getTimeLeft(Calendar from, Calendar to) {
        long left = to.getTimeInMillis() - from.getTimeInMillis();
        return getTimeLeft(left);
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

    public String getTargetTime() {
        int hr12 = -1;
        String suffix = "";
        if (this.getTime()[1] == 0) {
            hr12 = 12;
            suffix = "a.m.";
        } else if (this.getTime()[1] < 12) {
            hr12 = this.getTime()[1];
            suffix = "a.m.";
        } else if (this.getTime()[1] == 12) {
            hr12 = this.getTime()[1];
            suffix = "p.m.";
        } else if (this.getTime()[1] > 12) {
            hr12 = this.getTime()[1] - 12;
            suffix = "p.m.";
        }

        String day = HelperMethods.getDay(this.getTime()[0]);

        return day + " " + hr12 + ":" + String.format("%02d", this.getTime()[2]) + suffix + " EST";
    }


}
