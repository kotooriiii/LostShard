package com.github.kotooriiii.status;

import org.bukkit.ChatColor;

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

    @Override
    public String toString() {
        return super.toString();
    }


}
