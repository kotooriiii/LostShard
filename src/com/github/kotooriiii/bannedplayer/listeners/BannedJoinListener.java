package com.github.kotooriiii.bannedplayer.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bannedplayer.BannedPlayer;
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

        Player player = event.getPlayer();
        for(BannedPlayer bannedPlayer : LostShardPlugin.getBanManager().getBannedPlayers())
        {

            if(player.getUniqueId().equals(bannedPlayer.getPlayerUUID()))
            {

                String banMessage = bannedPlayer.getBannedMessage();
                ZonedDateTime unbanZDT = bannedPlayer.getUnbanDate();
                String unbanDate = "Banned until: ";

                if(unbanZDT.getYear() == 0)
                {

                    unbanDate += Banmatch.getIndefiniteBanIdentifier() + " ban";
                } else {
                    if(ZonedDateTime.now().compareTo(unbanZDT) >= 0)
                    {
                        LostShardPlugin.getBanManager().unban(bannedPlayer);
                        event.allow();
                        return;
                    }
                    unbanDate += HelperMethods.until(unbanZDT);
                }



                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMessage + "\n\n" + unbanDate);
                break;
            }
        }
    }
}
