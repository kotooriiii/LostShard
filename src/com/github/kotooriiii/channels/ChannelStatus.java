package com.github.kotooriiii.channels;

import org.bukkit.ChatColor;

public enum ChannelStatus {
    LOCAL("Local", ChatColor.YELLOW), GLOBAL("Global", ChatColor.YELLOW), CLAN("Clan", ChatColor.GREEN);

    private ChatColor chatColor;
    private String name;

    private ChannelStatus(String name, ChatColor chatColor)
    {
        this.name = name;
        this.chatColor = chatColor;
    }

    public String getPrefix()
    {
        return this.getChatColor() + this.getName();
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
