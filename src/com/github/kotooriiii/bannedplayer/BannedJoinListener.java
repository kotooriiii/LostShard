package com.github.kotooriiii.bannedplayer;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.match.banmatch.Banmatch;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.ZonedDateTime;
import java.util.UUID;

public class BannedJoinListener  implements Listener {
    @EventHandler
    public void joinOnBan(PlayerLoginEvent event)
    {

        debug(event.getPlayer().getName() + " trying to log in.");
        Player player = event.getPlayer();
        for(BannedPlayer bannedPlayer : FileManager.getBanned())
        {
            debug("LOOPING: " + event.getPlayer().getName());

            if(player.getUniqueId().equals(bannedPlayer.getPlayerUUID()))
            {
                debug(event.getPlayer() + " is banned.");

                String banMessage = bannedPlayer.getBannedMessage();
                ZonedDateTime unbanZDT = bannedPlayer.getUnbanDate();
                String unbanDate = "Banned until: ";

                if(unbanZDT.getYear() == 0)
                {
                    debug(event.getPlayer() + " has an indefinite ban.");

                    unbanDate += Banmatch.getIndefiniteBanIdentifier() + " ban";
                } else {
                    if(ZonedDateTime.now().compareTo(unbanZDT) >= 0)
                    {
                        debug(event.getPlayer() + " was able to log in because date has expired.");
                        debug(ZonedDateTime.now() + "\nVERSUS\n" + unbanZDT);

                        FileManager.removeFile(bannedPlayer);
                        event.allow();
                        return;
                    }
                    unbanDate += HelperMethods.until(unbanZDT);
                }



                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMessage + "\n\n" + unbanDate);
            }
        }
    }

    public void debug(String message)
    {
        Bukkit.broadcastMessage(message);
    }
}
