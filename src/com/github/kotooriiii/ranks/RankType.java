package com.github.kotooriiii.ranks;

import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.ChatColor;

public enum RankType {
    DEFAULT("Default", "def[" , ChatColor.WHITE  + "]", 27, 3, true),SUBSCRIBER("Subscriber",  ChatColor.GOLD + "[" , ChatColor.GOLD  + "]", 27, 6, true),SUBSCRIBER_PLUS("Subscriber+", ChatColor.GOLD + "[" , ChatColor.GOLD  + "]*", 54, 9, false);


    private String name;
    private String prefixContent;
    private String suffixContent;
    private int bankInventorySize;
    private int warpsNum;
    private boolean obligatedRent;

    private RankType(String name, String prefixContent, String suffixContent, int bankInventorySize, int warpsNum, boolean obligatedRent)
    {
        this.name = name;
        this.prefixContent = prefixContent;
        this.suffixContent = suffixContent;
        this.bankInventorySize = bankInventorySize;
        this.warpsNum = warpsNum;
        this.obligatedRent = obligatedRent;
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

    public int getBankInventorySize() {
        return bankInventorySize;
    }

    public int getMaxMarksNum() {
        return warpsNum;
    }

    public boolean isObligatedRent() {
        return obligatedRent;
    }
}
