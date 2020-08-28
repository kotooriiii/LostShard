package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class TutorialHelper {

    public static final int DELAY_TICK = 30;

    public static void sendMessage(Player player, String message) {
        player.sendMessage(STANDARD_COLOR + "");
    }

    public static void next(Player player) {
        LostShardPlugin.getTutorialManager().wrap(player.getUniqueId()).nextProgression(player.getLocation());

    }

}
