package com.github.kotooriiii.data;

import com.github.kotooriiii.clans.Clan;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Maps {
    //This hash map targets the clan creators who are making a clan and need to specify a tag. they will not be able to chat until the message they send is an acceptable tag.
    public static HashMap<UUID, Clan> clanTagCreators = new HashMap<>();

    //This hash map targets the clan creators who are making a clan and need to specify a color. they will not be able to chat until the message they send is an acceptable tag.
    public static HashMap<UUID, Clan> clanColorCreators = new HashMap<>();

    //The array list targets the active clans on the server
    public static ArrayList<Clan> clans = new ArrayList<>();

    //The array list targets the clan leader attempting to disbanding the clan. They have a set timer in order to confirm their request.
    public static ArrayList<UUID> clanDisbandTimer = new ArrayList<>();

    //The array list targets the clan leader attempting to assign new leadership of the clan. They have a set timer in order to confirm their request.
    public static ArrayList<UUID> leaderConfirmation = new ArrayList<>();

    //The array list targets the non-clan user deciding if they'd like to join a clan. They have a set timer in order to confirm their request.
    public static HashMap<UUID, ArrayList<Clan>> invitationConfirmation = new HashMap<>();

    public final static ChatColor ERROR_COLOR = ChatColor.DARK_RED;
    public final static ChatColor CLAN_COLOR = ChatColor.GOLD;
    public final static ChatColor STANDARD_COLOR = ChatColor.DARK_GREEN;
    public final static ChatColor PLAYER_COLOR = ChatColor.YELLOW;
    public final static ChatColor COMMAND_COLOR = ChatColor.GOLD;
    public final static ChatColor RANK_COLOR = ChatColor.GOLD;

}
