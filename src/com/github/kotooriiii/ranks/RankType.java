package com.github.kotooriiii.ranks;

import org.bukkit.ChatColor;

public enum RankType {
    DEFAULT("Default", ChatColor.WHITE, "def[" , ChatColor.WHITE  + "]", 27, 3),SUBSCRIBER("Subscriber", ChatColor.DARK_RED, ChatColor.GOLD + "[" , ChatColor.GOLD  + "]", 27, 6),SUBSCRIBER_PLUS("Subscriber+", ChatColor.DARK_RED, ChatColor.GOLD + "[" , ChatColor.GOLD  + "]*", 54, 9);


    private String name;
    private ChatColor prefixNameColor;
    private String prefixContent;
    private String suffixContent;
    private int bankInventorySize;
    private int warpsNum;

    private RankType(String name, ChatColor prefixNameColor, String prefixContent, String suffixContent, int bankInventorySize, int warpsNum)
    {
        this.name = name;
        this.prefixNameColor = prefixNameColor;
        this.prefixContent = prefixContent;
        this.suffixContent = suffixContent;
        this.bankInventorySize = bankInventorySize;
        this.warpsNum = warpsNum;
    }

    public static RankType matchRankType(String name) {
        for(RankType rankType : RankType.values())
        {
            if(rankType.getName().equalsIgnoreCase(name))
                return rankType;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getPrefixContent() {
        return prefixContent;
    }

    public String getSuffixContent() {
        return suffixContent;
    }

    public ChatColor getPrefixNameColor() {
        return prefixNameColor;
    }

    public int getBankInventorySize() {
        return bankInventorySize;
    }

    public int getMaxMarksNum() {
        return warpsNum;
    }

}
