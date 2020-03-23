package com.github.kotooriiii.channels;

import org.bukkit.ChatColor;

public enum ChannelStatus {
    LOCAL("Local", ChatColor.YELLOW), GLOBAL("Global", ChatColor.BLUE), CLAN("Clan", ChatColor.GREEN);

    private ChatColor chatColor;
    private String name;

    private ChannelStatus(String name, ChatColor chatColor)
    {
        this.name = name;
        this.chatColor = chatColor;
    }


    public String getPrefix()
    {
        return ChatColor.WHITE + "[" + this.getChatColor() + this.getName() + ChatColor.WHITE + "]";
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
