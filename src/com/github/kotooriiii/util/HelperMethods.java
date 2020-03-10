package com.github.kotooriiii.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class HelperMethods {
    private HelperMethods() {}

    public static String stringBuilder(String[] args, int n, String concat) {

        String string = "";
        for (int i = n; i < args.length; i++) {
            if (i == n)
                string += args[i];
            else
                string += concat + args[i];
        }
        return string;
    }

    public static void playSound(Player[] players, Sound sound)
    {
        for(Player player : players)
        {
            player.playSound(player.getLocation(), sound, 10F, 1F);
        }
    }
}
