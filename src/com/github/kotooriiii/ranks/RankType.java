package com.github.kotooriiii.ranks;

import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.ChatColor;

public enum RankType {
    DEFAULT("Default", "def[" , ChatColor.WHITE  + "]", 27, 5, 1, true),
    SUBSCRIBER("Subscriber",  ChatColor.GOLD + "[" , ChatColor.GOLD  + "]", 27, 8, 1,true),
    SUBSCRIBER_PLUS("Subscriber+", ChatColor.GOLD + "[" , ChatColor.GOLD  + "]*", 54, 15, 2,false);


    private String name;
    private String prefixContent;
    private String suffixContent;
    private int bankInventorySize;
    private int warpsNum;
    private int dungeonsNum;
    private boolean obligatedRent;

    private RankType(String name, String prefixContent, String suffixContent, int bankInventorySize, int warpsNum, int dungeonsNum, boolean obligatedRent)
    {
        this.name = name;
        this.prefixContent = prefixContent;
        this.suffixContent = suffixContent;
        this.bankInventorySize = bankInventorySize;
        this.warpsNum = warpsNum;
        this.dungeonsNum = dungeonsNum;
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

    public int getDungeonsNum() {
        return dungeonsNum;
    }

    public boolean isObligatedRent() {
        return obligatedRent;
    }
}
