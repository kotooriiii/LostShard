package com.github.kotooriiii.hostility;

import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.commands.HostilityCommand;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.text.DecimalFormat;

import static com.github.kotooriiii.data.Maps.*;

public class HostilityTimeCreatorListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onChat(ShardChatEvent event) {
        Player player = event.getPlayer();

        if (!hostilityTimeCreator.containsKey(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);

        String message = event.getMessage();
        if (!message.matches("\\w{3}\\s[0-9]{2}:[0-9]{2}")) {
            player.sendMessage(ERROR_COLOR + "The time format (EST) does not match with the given input:\nFormat: Day HourOf24:Minute\nExample 1: Tue 23:25 for every Tuesday at 11:25pm.\nExample 2: Sun 0:20 for every Sunday at 12:20am.");
            return;
        }

        String[] properties = message.split("\\s|:");
        String possibleDay = properties[0];
        String possibleHour = properties[1];
        String possibleMinute = properties[2];

        int day = HelperMethods.getDay(possibleDay);
        if (day == -1) {
            player.sendMessage(ERROR_COLOR + possibleDay + " is not a valid day.\nExample 1: Wed for Wednesday");
            return;
        }

        int hour = Integer.parseInt(possibleHour);
        if (hour < 0 || hour > 23) {
            player.sendMessage(ERROR_COLOR + "The given hour cannot be negative nor can it exceed 23.");
            return;
        }

        int minute = Integer.parseInt(possibleMinute);
        if (minute < 0 || minute > 59) {
            player.sendMessage(ERROR_COLOR + "The given minute cannot be negative nor can it exceed 59.");
            return;
        }
        int hr12 = -1;
        String suffix = "";
        if (hour == 0) {
            hr12 = 12;
            suffix = "a.m.";
        } else if (hour < 12) {
            hr12=hour;
            suffix = "a.m.";
        } else if (hour == 12)
        {
            hr12=hour;
            suffix = "p.m.";
        } else if (hour > 12)
        {
            hr12=hour-12;
            suffix="p.m.";
        }

        HostilityPlatform platform = hostilityTimeCreator.get(player.getUniqueId());
        platform.setTime(new int[]{day, hour, minute});

        DecimalFormat df = new DecimalFormat("#.##");
        player.sendMessage(STANDARD_COLOR + "You have set the match to start every " + possibleDay.substring(0, 1).toUpperCase() + possibleDay.substring(1).toLowerCase() + " at " + hr12 + ":" + String.format("%02d", minute) + suffix);
        player.sendMessage(STANDARD_COLOR + "You have enabled editing mode.");

        HostilityCommand.giveTools(player);

        //add to hostility platform creator and remove from time
        hostilityTimeCreator.remove(player.getUniqueId());
        hostilityPlatformCreator.put(player.getUniqueId(),platform);
    }


}
