package com.github.kotooriiii.hostility;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.clans.Clan;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HostilityMatch {

    /**
     * The platform that players have to capture.
     */
    private HostilityPlatform platform;

    /**
     * Win streak of the clan
     */
    private int winStreak;
    /**
     * The clan capturing the platform
     */
    private Clan capturingClan;
    /**
     * The player who SHOULD be in the clan
     */
    private Player capturingPlayer;

    /**
     * The current ticks counting down from the max ticks
     */
    private int currentTicks;
    /**
     * The max ticks. The beginning of the countdown
     */
    private final int maxTicks;
    /**
     * The looping task
     */
    private BukkitTask task;

    public HostilityMatch(HostilityPlatform platform) {
        this.platform = platform;

        this.winStreak = 0;
        this.capturingClan = null;
        this.capturingPlayer = null;

        this.maxTicks = toTicks(8);
        this.currentTicks = maxTicks;

        this.task = null;
    }

    public void start() {
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!platform.contains(capturingPlayer)) {

                }

                currentTicks--;
            }
        }.runTaskTimer(LostShardK.plugin, 0, 1);

    }

    public final boolean isActive() {
        return currentTicks > 0 && currentTicks != maxTicks;
    }

    private final int toTicks(int minutes) {
        return minutes * 60 * 20;
    }

    private final int toTicks(int minutes, int seconds) {
        return (minutes * 60 * 20) + (seconds * 20);
    }

    private void announce() {
        int[][] countdownAnnouncement = new int[][]
                {
                        
                };
        final int sixMins = toTicks(6);
        final int fourMins = toTicks(4);
        final int twoMins = toTicks(2);
        final int oneMin = toTicks(1);
        final int thirtySecs = toTicks(0, 30);
        final int fifteenSecs = toTicks(0, 15);
        final int tenSecs = toTicks(0, 10);
        final int fiveSecs = toTicks(0, 5);
        final int fourSecs = toTicks(0, 4);
        final int threeSecs = toTicks(0, 3);
        final int twoSecs = toTicks(0, 2);
        final int oneSecs = toTicks(0, 1);
        final int zeroSecs = toTicks(0, 0);

        if (sixMins == this.currentTicks)
            broadcast(ChatColor.YELLOW + this.capturingClan.getName() + ChatColor.GOLD + " has begun con");

    }

    private String toMinutesSeconds() {
        int ticksToSecond = (int) (this.currentTicks / 20);

        int minutes = ticksToSecond / 60;
        int seconds = ticksToSecond % 60;

        char[] rawMinutes = String.valueOf(minutes).toCharArray();
        char[] rawSeconds = String.valueOf(seconds).toCharArray();

        char[] desiredMinutes = new char[2];
        char[] desiredSeconds = new char[2];

        //get string mins
        int desMinsLen = desiredMinutes.length;
        int rawMinsLen = rawMinutes.length;

        int skippedMinsNum = desMinsLen - rawMinsLen;

        for (int i = 0; i < skippedMinsNum; i++) {
            desiredMinutes[i] = '0';
        }

        for (int i = skippedMinsNum; i < desMinsLen; i++) {
            desiredMinutes[i] = rawMinutes[i - skippedMinsNum];
        }

        //get string secs
        int desSecsLen = desiredSeconds.length;
        int rawSecsLen = rawSeconds.length;

        int skippedSecsNum = desSecsLen - rawSecsLen;

        for (int i = 0; i < skippedSecsNum; i++) {
            desiredSeconds[i] = '0';
        }

        for (int i = skippedSecsNum; i < desSecsLen; i++) {
            desiredSeconds[i] = rawSeconds[i - skippedSecsNum];
        }

        return String.valueOf(desiredMinutes) + ":" + String.valueOf(desiredSeconds);
    }

    public void broadcast(String message) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
}
