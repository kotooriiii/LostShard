package com.github.kotooriiii.tips;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

public class TipsManager {
    /**
     * Player UUIDs who don't want to have tips.
     */
    private HashSet<UUID> set;
    private String[] tips;
    private int index;
    private BukkitTask task;
    private ChatColor textColor, tipColor;

    public TipsManager() {
        set = new HashSet<>();
        tips = new String[0];
        textColor = ChatColor.GREEN;
        tipColor = ChatColor.GREEN;
        index = -1;
        task = null;
    }

    public void init() {
        tips = new String[]
                {
                        "Hold a stick in your hand and type '/bind (spell name)' to bind a spell to your stick.",
                        "Eating rotten flesh, melons, and soup instantly heals your heart and hunger.",
                        "Deposit your gold at the banker at Order or Chaos. Type: '/deposit (amount)'.",
                        "Can't find the banker or build the build changer? Type: '/plot info' to see their locations.",
                        "Plots keep your builds safe. To create a plot, type: '/plot create (name)'. You need a diamond and 10 gold deposited at the banker.",
                        "Spells use reagents. Teleport = 1 feather. Web field = 1 string.",
                        "Don't know all the spells? Type: '/cast' for a list of all of them.",
                        "Blue names spawn at Order. Red and Grey names spawn at Chaos.",
                        "Typing '/guards' near a " + ChatColor.RED + "murderer " + textColor + "or " + ChatColor.GRAY + "criminal " + textColor + "instantly kills them.",
                        "Tired of hearing of a player? Type: '/ignore (username)' to ignore messages and pings.",
                        "Need to get the attention of someone? Ping them with '@username'.",
                        "Grey names turn back to Blue after 5 minutes of no combat.",
                        "You can make chain armor using cobblestone. Make it like normal armor.",
                        "Red names can turn back Blue when they visit a Shrine of Atonement. Type: '/atone' when nearby.",
                        "The Permanent Gate Travel spell can teleport items, arrows, TNT, mobs, and you.",
                        "You can view your skill stats by typing: '/skills'.",
                        "Don't like the HUD? Type: '/hud' to toggle.",
                        "Remember to create a clan to keep your friends safe. Type: '/clan create (name)'.",
                        "Don't know all the commands to clans or plots? Type: '/plot help' or '/clan help' for more help.",
                        "Want more information about a player? Type: '/whois (username).",
                        "Need help? Type: '/help' to join the community.",
                        "Help the server by donating. Type: '/donate' for more information.",
                        "Every Hostility capture rewards you. Type: '/host' to view the upcoming and active matches.",
                        "Need to reduce skill points? Type: /skill reduce <skill name> <amount to subtract>.",
                        "This server has a YOUTUBE channel! https://www.youtube.com/channel/UCE0EW5M4FXWo79aiJ3TDfhQ/videos",
                        "Need help? Check this out: https://docs.google.com/document/d/1UfFwn_xJrgPkjKC9Bs7OAFAG22IDhPg4wmk7cZtsv9c"
                };
    }

    public void loop() {
        if (task != null)
            task.cancel();
        task = new BukkitRunnable() {
            @Override
            public void run() {

                int ranIndex;
                do {
                    ranIndex = new Random().nextInt(tips.length);
                } while (index == ranIndex);

                index = ranIndex;
                String prefix = tipColor + "[Tip] ";
                for (Player player : Bukkit.getOnlinePlayers()) {

                    if (!isSubscribed(player.getUniqueId()))
                        player.sendMessage(prefix + tips[index]);
                }

            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 20 * 60 * 10); //23 mins
    }

    /**
     * Subscribe to not have any tips. In other words, does not receive tips.
     *
     * @param uuid player uuid
     * @return if subscribe was successful, returns false if already subscribed
     */
    public boolean subscribe(UUID uuid) {
        return set.add(uuid);
    }

    /**
     * Unsubscribes to not receive any tips. In other words, receives tips.
     *
     * @param uuid player uuid
     * @return if unsub was successful, returns false if not found in this list.
     */
    public boolean unsubcribe(UUID uuid) {
        return set.remove(uuid);
    }

    /**
     * Checks to see if subscribed to the list. If subscribed, the player does not receive tips.
     *
     * @param uuid player uuid
     * @return true if subscribed, false otherwise
     */
    public boolean isSubscribed(UUID uuid) {
        return set.contains(uuid);
    }


}
