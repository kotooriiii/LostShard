package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MOTDListener implements Listener {
    @EventHandler
    public void onMOTD(ServerListPingEvent event) {

        event.setMotd(ChatColor.DARK_PURPLE + "                  LostShard " + ChatColor.GOLD + "[Patch: 1.0.6]\n" +
                ChatColor.GOLD + "      " + LostShardPlugin.getPatchUpdateVersion("New Host and Havok rewards"));
    }}
