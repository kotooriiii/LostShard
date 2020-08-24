package com.github.kotooriiii.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerConnectServerEvent implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent event)
    {
        String ip = event.getHostname().split(":")[0];

        if (ip.startsWith("70.181.242.67") || ip.startsWith("localhost"
        ))
            return;

        String[] networkIPArr = new String[]{"18.211.33.247",
                "3.208.149.27",
                "35.170.34.124"};

        boolean isNetwork = false;
        boolean isStringNetwork = false;
        for(String networkIP : networkIPArr)
        {
            if(ip.equals(networkIP))
                isNetwork=true;

            if(ip.equalsIgnoreCase("lostshard.net") || ip.equalsIgnoreCase("play.lostshard.club"))
                isStringNetwork=true;
        }

        if(!isStringNetwork)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "\nNot connected from network.\nHostname: " + ip);
        }
    }
}
