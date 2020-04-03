package com.github.kotooriiii.status;

import org.bukkit.ChatColor;

public enum StaffType {
    OWNER("Owner", ChatColor.GOLD), COOWNER("Co-Owner", ChatColor.GRAY),ADMIN("Admin", ChatColor.RED), MODERATOR("Moderator", ChatColor.BLUE);

    private String name;
    private ChatColor chatColor;

    private StaffType(String name, ChatColor chatColor)
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

    public static StaffType matchStaffType(String name)
    {
        for(StaffType staffType : StaffType.values())
        {
            if(staffType.getName().equalsIgnoreCase(name))
            {
                return staffType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
