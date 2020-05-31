package com.github.kotooriiii.channels;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChannelManager {

    private HashMap<ChannelStatus, ArrayList<UUID>> channels;
    private HashMap<UUID, ChannelStatus> playerChannel;
    private boolean isAdminChat;

    public ChannelManager()
    {
        channels=new HashMap<>();
        playerChannel=new HashMap<>();
        for(Player player : Bukkit.getOnlinePlayers())
        {
            joinChannel(player, ChannelStatus.GLOBAL);
        }
        isAdminChat = false;
    }

    public boolean isAdminChat() {
        return isAdminChat;
    }

    public void setAdminChat(boolean adminChat) {
        isAdminChat = adminChat;
    }

    public void joinChannel(Player player, ChannelStatus status)
    {
        if(playerChannel.get(player.getUniqueId()) != null)
        {
            ChannelStatus prevChannelStatus = playerChannel.get(player.getUniqueId());
            leaveChannel(player, prevChannelStatus);
        }

        ArrayList<UUID> playersUUID = channels.get(status);
        if(playersUUID==null)
            playersUUID = new ArrayList<>();
        playersUUID.add(player.getUniqueId());
        channels.put(status, playersUUID);
        playerChannel.put(player.getUniqueId(), status);
    }

    public void leaveChannel(Player player, ChannelStatus status)
    {
        ArrayList<UUID> playersUUID = channels.get(status);
        playersUUID.remove(player.getUniqueId());
        channels.put(status, playersUUID);
        playerChannel.remove(player.getUniqueId());
    }


    public ChannelStatus getChannel(Player player)
    {
        return playerChannel.get(player.getUniqueId());
    }


}
