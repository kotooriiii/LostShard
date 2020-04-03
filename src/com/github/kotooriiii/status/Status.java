package com.github.kotooriiii.status;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerRespawnEvent;

public enum Status {
    WORTHY("Worthy", ChatColor.BLUE), CORRUPT("Corrupt", ChatColor.GRAY), EXILED("Exiled", ChatColor.RED);

    private String name;
    private ChatColor chatColor;

    private Status(String name, ChatColor chatColor)
    {
        this.name = name;
        this.chatColor = chatColor;
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public static Status matchStatus(String name)
    {
        for(Status status : Status.values())
        {
            if(status.getName().equals(name))
            {
               return status;
            }
        }
        return null;
    }

    public String getOrganization()
    {
        switch (this.getName().toLowerCase())
        {
            case "worthy":
                return "Order";
            case "corrupt":
            case "exiled":
                return "Chaos";
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }


}
