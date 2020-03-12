package com.github.kotooriiii.data;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.hostility.HostilityMatch;
import com.github.kotooriiii.hostility.HostilityPlatform;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Maps {

    private Maps(){}
    //This hash map targets the clan creators who are making a clan and need to specify a tag. they will not be able to chat until the message they send is an acceptable tag.
    public static HashMap<UUID, Clan> clanTagCreators = new HashMap<>();

    //This hash map targets the clan creators who are making a clan and need to specify a color. they will not be able to chat until the message they send is an acceptable tag.
    public static HashMap<UUID, Clan> clanColorCreators = new HashMap<>();

    //The array list targets the active clans on the server. ID OF CLAN
    public static ArrayList<Clan> clans = new ArrayList<>();

    //The array list targets the clan leader attempting to disbanding the clan. They have a set timer in order to confirm their request.
    public static ArrayList<UUID> clanDisbandTimer = new ArrayList<>();

    //The array list targets the clan leader attempting to assign new leadership of the clan. They have a set timer in order to confirm their request.
    public static ArrayList<UUID> leaderConfirmation = new ArrayList<>();

    //The array list targets the non-clan user deciding if they'd like to join a clan. They have a set timer in order to confirm their request.
    public static HashMap<UUID, ArrayList<Clan>> invitationConfirmation = new HashMap<>();

    //The most ESSENTIAL HASHMAP!! This makes an organized pair between player and clan. It updates too! More efficient than checking by iterating all clans.
    public static HashMap<UUID, Clan> playerUUIDClanMap = new HashMap<>(199);

    //END CLANS //
    //START HOST//

    //The arraylist targets a staff member trying to create a hostility platform. Confirm to understand that it will clear inventory.
    public static ArrayList<UUID> hostilityCreatorConfirmation = new ArrayList<>();
    //The arraylist targets a staff member trying to remove a hostility platform. Confirm to understand that it is permanent deletion.
    public static ArrayList<UUID> hostilityRemoverConfirmation = new ArrayList<>();

    //The hashmap targets the staff member creating hostility platform. They have tools and are in process of creating
    public static HashMap<UUID, HostilityPlatform> hostilityPlatformCreator = new HashMap<>();

    //ALL platforms in an arraylist that are ready to be used. Loaded from files
    public static ArrayList<HostilityPlatform> platforms = new ArrayList<>();
    //ACTIVE GAMES OF HOSTILITY. These are the matches players are playing currently.
    public static ArrayList<HostilityMatch> activeHostilityGames = new ArrayList<>();


    public final static ChatColor ERROR_COLOR = ChatColor.DARK_RED;
    public final static ChatColor CLAN_COLOR = ChatColor.DARK_GREEN;
    public final static ChatColor STANDARD_COLOR = ChatColor.DARK_GREEN;
    public final static ChatColor PLAYER_COLOR = ChatColor.YELLOW;
    public final static ChatColor COMMAND_COLOR = ChatColor.GOLD;
    public final static ChatColor RANK_COLOR = ChatColor.DARK_GREEN;

    public final static Permission STAFF_PERMISSION = new Permission("lostshardsk.staff");

}
