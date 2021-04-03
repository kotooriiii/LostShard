package com.github.kotooriiii.ranks;

import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.ChatColor;

public enum RankType {
    DEFAULT("Default", "def[" , ChatColor.WHITE  + "]", 27, 5, 1, 1,3,true),
    SUBSCRIBER("Subscriber",  ChatColor.GOLD + "[" , ChatColor.GOLD  + "]", 27, 8, 1, 2, 3,true),
    SUBSCRIBER_PLUS("Subscriber+", ChatColor.GOLD + "[" , ChatColor.GOLD  + "]*", 54, 15, 2,3,3, false);


    private int vendorsPerPlot;
    private String name;
    private String prefixContent;
    private String suffixContent;
    private int bankInventorySize;
    private int warpsNum;
    private int dungeonsNum;
    private int vendorsNum;
    private boolean obligatedRent;

    private RankType(String name, String prefixContent, String suffixContent, int bankInventorySize, int warpsNum, int dungeonsNum, int vendorsNum, int vendorsPerPlot, boolean obligatedRent)
    {
        this.name = name;
        this.prefixContent = prefixContent;
        this.suffixContent = suffixContent;
        this.bankInventorySize = bankInventorySize;
        this.warpsNum = warpsNum;
        this.dungeonsNum = dungeonsNum;
        this.vendorsNum = vendorsNum;
        this.vendorsPerPlot = vendorsPerPlot;
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

    public int getVendorsNum() {
        return vendorsNum;
    }

    public int getVendorsPerPlot() {
        return vendorsPerPlot;
    }
}
