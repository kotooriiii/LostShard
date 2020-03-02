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
    private int maxTicks;
    /**
     * grace period for capturing point after win
     */
    private final int gracePeriod = 20 * 30;
    /**
     * The looping task
     */
    private BukkitTask task;

    public HostilityMatch(HostilityPlatform platform) {
        this.platform = platform;
        this.maxTicks = toTicks(8);
        init();
    }

    private void init() {
        this.winStreak = 0;
        this.capturingClan = null;
        this.capturingPlayer = null;

        this.currentTicks = maxTicks;

        this.task = null;
    }

    public void start() {
        this.task = new BukkitRunnable() {
            @Override
            public void run() {

                if (!platform.contains(capturingPlayer)) {
                    broadcast(ChatColor.YELLOW + capturingClan.getName() + ChatColor.GOLD + " has lost control of Hostility. " + toTicks(currentTicks));
                    init();
                    this.cancel();
                    return;
                }

                currentTicks--;
            }
        }.runTaskTimer(LostShardK.plugin, 0, 1);

    }

    public void checkForCapturer()
    {
        new BukkitRunnable(){

            @Override
            public void run() {
               // if()

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

    private void alert() {

        int[] timerMinAlert = new int[]
                {
                        8, 6, 4, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0

                };
        int[] timerSecAlert = new int[]
                {
                        0, 0, 0, 0, 0, 30, 15, 10, 5, 4, 3, 2, 1, 0

                };

        for (int i = 0; i < timerMinAlert.length; i++) {

            int tickAlert = toTicks(timerMinAlert[i], timerSecAlert[i]);

            if (tickAlert == this.currentTicks) {
                if (i == 0) {
                    this.broadcast(ChatColor.YELLOW + this.capturingClan.getName() + ChatColor.GOLD + " has begun capturing Hostility for their " + tryingToPlace(winStreak) + " time. " + toMinutesSeconds(this.currentTicks));

                } else if (i > 0 && i < timerMinAlert.length - 1)
                    this.broadcast(ChatColor.YELLOW + this.capturingClan.getName() + ChatColor.GOLD + " is currently capturing Hostility for their " + tryingToPlace(winStreak) + " time. " + toMinutesSeconds(this.currentTicks));
                else if (i == timerMinAlert.length - 1) {
                    winStreak++;
                    if (winStreak == 3) {
                        this.broadcast(ChatColor.YELLOW + this.capturingClan.getName() + ChatColor.GOLD + " has fully captured Hostility.");

                        //win host
                    } else {
                        this.broadcast(ChatColor.YELLOW + this.capturingClan.getName() + ChatColor.GOLD + " has successfully captured Hostility for their " + tryingToPlace(winStreak - 1) + " time. Hostility will be active for capture in " + toMinutesSeconds(gracePeriod));
                    }
                }
            }
        }
    }

    private String tryingToPlace(int winStreak) {
        switch (winStreak) {
            case 0:
                return "first";
            case 1:
                return "second";
            case 2:
                return "third";
        }
        return "null";
    }


    private String toMinutesSeconds(int currentTicks) {
        int ticksToSecond = (int) (currentTicks / 20);

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

        return "[" + String.valueOf(desiredMinutes) + ":" + String.valueOf(desiredSeconds) + "]";
    }

    public void broadcast(String message) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
}
