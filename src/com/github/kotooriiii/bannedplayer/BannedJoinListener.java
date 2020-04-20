package com.github.kotooriiii.bannedplayer;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.util.HelperMethods;
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
        Player player = event.getPlayer();
        for(UUID uuid : FileManager.getBanned())
        {
            if(player.getUniqueId().equals(uuid))
            {
                BannedPlayer bannedPlayer = FileManager.getBannedPlayer(player.getUniqueId());
                String banMessage = bannedPlayer.getBannedMessage();
                ZonedDateTime unbanZDT = bannedPlayer.getUnbanDate();
                String unbanDate = "Banned until: ";

                if(unbanZDT == null)
                {
                    unbanDate += "Indefinite ban";
                } else {
                    if(ZonedDateTime.now().compareTo(unbanZDT) >= 0)
                    {
                        FileManager.unban(bannedPlayer);
                        return;
                    }
                    unbanDate += HelperMethods.until(unbanZDT);
                }



                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMessage + "\n\n" + unbanDate);
            }
        }
    }
}
