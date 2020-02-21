package com.github.kotooriiii.hostility;

import com.github.kotooriiii.LostShardK;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class Hostility {

    private UUID capturer;

    private int streak;

    private final int countDownTimerMax;
    private int countdownTimer;

    private boolean isCancelled;

    public Hostility() {
        this.capturer = null;
        this.streak = 0;
        this.countDownTimerMax = 8 * 60 * 20;
        this.countdownTimer = countDownTimerMax;
    }

    public void loop() {

        BukkitTask task = new BukkitRunnable() {
            private long ticks = 0;

            @Override
            public void run() {

                if (isCancelled) { //If the task has been canceled, stop!
                    this.cancel();
                    return;
                }
                if (ticks == 20) //A second has passed by. Reduce the timer by a second AND reset the ticks to 0.
                {
                    countdownTimer = -20;
                    ticks=0;
                }
                //check if still in platform

                //countdown

                ticks++;
            }
        }.runTaskTimer(LostShardK.plugin, 0, 1L);
    }


    // START BASIC GETTER/SETTER1
    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public int getCountdownTimer() {
        return countdownTimer;
    }

    public void setCountdownTimer(int countdownTimer) {
        this.countdownTimer = countdownTimer;
    }
    //END BASIC GETTER/SETTER

}
