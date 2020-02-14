package com.github.kotooriiii.clans;

import com.github.kotooriiii.LostShardK;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

/**
 * <h1>Clan</h1>
 * The following class is responsible for the presentation of a collection of players using their unique id.
 * <p>
 * The class holds properties that make a clan be able to invite, kick, accept, deny, promote, demote, etc.
 *
 * @author kotooriiii
 * @version 1.0
 * @since 2019-02-05
 */
public class Clan {

    /**
     * The unique ID of the clan.
     */
    private final UUID id;

    /**
     * The name of the clan
     */
    private String name;
    /**
     * The tag of the clan
     */
    private String tag;
    /**
     * The color of the clan
     */
    private ChatColor color;
    /**
     * Whether players are able to hurt each other.
     */
    private boolean isFriendlyFire;

    /**
     * The leader of the clan
     */
    private UUID leader;
    /**
     * The coleaders of the clan
     */
    private UUID[] coleaders;
    /**
     * The members of the clan
     */
    private UUID[] members;

    /**
     * Max capacity of a clan
     */
    private final int maxCapacity = 15;

    /**
     * Creates a clan using the name of the clan while also assigning a leader.
     *
     * @param clanName   The name of the clan
     * @param leaderUUID The {@link Player}'s {@link UUID}
     * @since 1.0
     */
    public Clan(String clanName, UUID leaderUUID) {

        //Checks whether the unique id of the clan is truly unique. Most cases are true, if false then repeats.
        boolean isUnique = false;
        UUID tempID = null;
        while (!isUnique) {
            tempID = UUID.randomUUID();
            boolean isFound = false;
            for (Clan clan : clans) {
                if (clan.getID().equals(tempID)) {
                    isFound = true;
                }
            }

            isUnique = !isFound;

        }
        this.id = tempID;

        //If the name was somehow null
        if (clanName == null)
            this.name = "null";
        else
            this.name = clanName;

        //Default properties if tag, color, and friendlyfire were not set.
        this.tag = "null";
        this.color = ChatColor.WHITE;
        this.isFriendlyFire = false;

        //Assign a leader
        if (leaderUUID == null)
            this.leader = null;
        else
            this.leader = leaderUUID;

        //Initialize values of ranks
        this.coleaders = new UUID[0];
        this.members = new UUID[0];
    }

    /**
     * Assigns the {@link UUID} (unique id) of the clan.<p><strong>This should only be used when certain the given unique id belongs to the clan.</strong></p>
     *
     * @param id The unique id of the clan
     * @since 1.0
     */
    public Clan(UUID id) {
        this.id = id;
    }

    //START OF STATIC METHODS
    /**
     * Gets the clan of a given Player UUID.
     * @param uuid The UUID of a Player.
     * @return the clan of that player, null if not in a clan
     */
    public static Clan getClan(UUID uuid) {
        if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore())
            return null;

        for (Clan clan : clans) {
            for (UUID clanUUID : clan.getAllUUIDS()) {
                if (clanUUID.equals(uuid)) {
                    return clan;
                }
            }
        }
        return null;
    }

    /**
     * Gets the clan of a given clan name.
     * @param clanName the name of the clan
     * @return the clan that matches that name, null if not found
     */
    public static Clan getClan(String clanName) {
        if (clanName == null || clanName.isEmpty())
            return null;

        for (Clan clan : clans) {
            String iteratingClanName = clan.getName().toUpperCase();
            if (iteratingClanName.equals(clanName.toUpperCase()))
                return clan;
        }

        return null;
    }

    /**
     * Checks if there is a clan with that name.
     * @param clanName the name of the clan
     * @return true if clan is found, false if no clan matches that name
     * @see Clan#getClan(String);
     */
    public static boolean isClan(String clanName) {
        return getClan(clanName) != null;
    }

    /**
     * Checks whether a player has a clan.
     * @param uuid the uuid of the player
     * @return true if the player has a clan, false if no clan was found
     * @see Clan#getClan(UUID);
     */
    public static boolean hasClan(UUID uuid) {

        return getClan(uuid) != null;
    }

    //END OF STATIC METHODS
    //START OF UTIL

    /**
     * Sends a message to everyone in the clan.
     *
     * @param message the message being delivered to the clan
     */
    public void broadcast(String message) {
        if (message == null || message.isEmpty())
            return;
        UUID[] clanmates = this.getOnlineUUIDS();
        for (int i = 0; i < clanmates.length; i++) {
            Player player = Bukkit.getPlayer(clanmates[i]);
            if (player.isOnline())
                player.sendMessage(message);
        }
        return;
    }

    /**
     * Sends a message to everyone in the clan with the exclusion of the supplied array.
     *
     * @param message      the message being delivered to the clan
     * @param restrictions the players who will not receive the message
     */
    public void broadcast(String message, UUID[] restrictions) {
        if (message == null || message.isEmpty() || restrictions == null)
            return;

        UUID[] clanmates = this.getOnlineUUIDS();
        clanmates:
        //iterating all members
        for (int i = 0; i < clanmates.length; i++) {
            restrictions:
            //iterating all restrictions
            for (int j = 0; j < restrictions.length; j++) {
                if (clanmates[i].equals(restrictions[j])) {
                    continue clanmates;
                }
            }
            Player player = Bukkit.getPlayer(clanmates[i]);
            if (player.isOnline())
                player.sendMessage(message);
        }
        return;
    }

    /**
     * Gets all the {@link Player}s of the clan in {@link UUID} format.
     *
     * @return an array of {@link UUID}s that belong to each player in the clan.
     */
    public UUID[] getAllUUIDS() {
        UUID[] clanmates = new UUID[this.getClanSize()]; //Create an array the size of the clan.
        int counter = 0;
        for (ClanRank rank : ClanRank.values()) { //For every rank in ascending order.
            for (UUID uuid : this.getPlayerUUIDSBy(rank)) { //For every player uuid in that given rank.
                clanmates[counter++] = uuid; //Update the array with that player uuid
            }
        }
        return clanmates;
    }

    /**
     * Gets the online {@link Player}s of the clan in {@link UUID} format.
     *
     * @return an array of {@link UUID}s that belong to each player in the clan.
     */
    public UUID[] getOnlineUUIDS() {
        UUID[] allClanmates = this.getAllUUIDS(); //Get all the clan users of the clan.
        ArrayList<UUID> onlineClanmates = new ArrayList<>(allClanmates.length); //We are not aware of the size of the clan so we will use an ArrayList with a max capacity.
        for (UUID uuid : allClanmates) { //Iterate through every clan player's uuid
            if (Bukkit.getOfflinePlayer(uuid).isOnline()) { //If the player is online
                onlineClanmates.add(uuid); //Add to arraylist
            }
        }
        return onlineClanmates.toArray(new UUID[onlineClanmates.size()]);
    }

    /**
     * Gets all the {@link Player}s of the clan.
     *
     * @return an array of {@link OfflinePlayer}s in the clan.
     */
    public OfflinePlayer[] getPlayers() {
        OfflinePlayer[] clanmates = new OfflinePlayer[this.getClanSize()]; //Create an array the size of the clan.
        int counter = 0;
        for (ClanRank rank : ClanRank.values()) { //For every rank in ascending order.
            for (UUID uuid : this.getPlayerUUIDSBy(rank)) {//For every player in that given rank.
                clanmates[counter++] = Bukkit.getOfflinePlayer(uuid); //Update array with that player
            }
        }
        return clanmates;
    }

    /**
     * Gets the players in a given rank.
     * @param rank the rank you are searching a list players for
     * @return a Player UUID array containing all the players in that rank, null if not a valid rank
     */
    public UUID[] getPlayerUUIDSBy(ClanRank rank) {
        if (rank == null) return null;

        UUID[] clanRanks = null;
        switch (rank) {
            case LEADER:
                clanRanks = new UUID[]{this.leader};
                break;
            case COLEADER:
                clanRanks = this.coleaders;
                break;
            case MEMBER:
                clanRanks = this.members;
                break;
        }
        return clanRanks;
    }

    /**
     * Checks whether a player is in this clan.
     * @param playerUUID the player being checked for existence
     * @return true if the player is in the clan, false if not in the clan
     */
    public boolean isInThisClan(UUID playerUUID) {
        if (playerUUID == null || !Bukkit.getOfflinePlayer(playerUUID).hasPlayedBefore())
            return false;
        for (UUID clanUUID : this.getAllUUIDS()) {
            if (clanUUID.equals(playerUUID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the size of the clan.
     *
     * @return the size of the clan by counting every member in each rank
     */
    public int getClanSize() {

        int counter = 0;
        for (ClanRank rank : ClanRank.values()) {
            for (UUID uuid : this.getPlayerUUIDSBy(rank)) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Gets the clan rank of a player.
     *
     * @param uuid The UUID of the player
     * @return the ClanRank associated with that player. null, if clan does not have the player in the clan.
     */
    public ClanRank getClanRank(UUID uuid) {
        if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore())
            return null;

        for (ClanRank clanRank : ClanRank.values()) {
            for (UUID clanmateUUID : this.getPlayerUUIDSBy(clanRank)) {
                if (clanmateUUID.equals(uuid))
                    return clanRank;
            }
        }


        return null;
    }

    /**
     * Updates all the ranks' player uuid lists.
     *
     * @param rank  the rank being updated
     * @param uuids the new player uuids taking place
     */
    public void updateRankUUIDS(ClanRank rank, UUID[] uuids) {
        if (rank == null || uuids == null)
            return;
        switch (rank) {
            case LEADER:
                //Intentionally left blank to indicate this method cannot update a leader unless made explictly by
                //the #setLeader method
                this.leader = uuids[0];
                break;
            case COLEADER:
                this.coleaders = uuids;
                break;
            case MEMBER:
                this.members = uuids;
                break;
        }
    }

    /**
     * Returns a friendly string representation of the clan
     * @return the properties of the clan
     */
    public String info() {
        String basic = ChatColor.GOLD + "---------------------\"" + this.getName() + "\"---------------------\n";

        /**
         String dash = "---------------------";
         char[] dashCharArray = dash.toCharArray();


         String result = "";

         for (int i = 0; i < dash.length(); i++) {
         if (i % 2 == 0)
         result += ChatColor.GOLD + "" + dashCharArray[i];
         else
         result += this.getColor() + "" + dashCharArray[i]
         }
         */

        String result = "";

        /** SORT RANKS **/
        ClanRank[] ranks = ClanRank.values();
        ClanRank[] sortedRanks = new ClanRank[ClanRank.values().length];
        for (int i = 0; i < sortedRanks.length; i++) {
            sortedRanks[sortedRanks.length - i - 1] = ranks[i];
        }
        /** END OF SORT RANKS **/

        /** build rank lists **/
        for (int i = 0; i < sortedRanks.length; i++) {

            String rankList = "";
            ArrayList<UUID> list = new ArrayList<UUID>(Arrays.asList(this.getPlayerUUIDSBy(sortedRanks[i])));
            Collections.sort(list);
            UUID[] rankListUUIDList = list.toArray(new UUID[list.size()]);
            for (int j = 0; j < rankListUUIDList.length; j++) {
                LostShardK.logger.info(rankListUUIDList[j].toString());

                OfflinePlayer player = Bukkit.getOfflinePlayer(rankListUUIDList[j]);

                if (j == 0)
                    rankList += ChatColor.YELLOW + "" + player.getName();
                else
                    rankList += ChatColor.YELLOW + "" + ", " + player.getName();
            }

            result += ChatColor.GOLD + "" + sortedRanks[i] + ": " + rankList + "\n";


        }
        /** end of rank lists */

        result += ChatColor.GOLD + "Number of Hostility’s captured: " + ChatColor.YELLOW + "" + 326 + "\n" +
                ChatColor.GOLD + "Current Host buff: " + ChatColor.YELLOW + "true\n" +
                ChatColor.GOLD + "Clan Tag: " + ChatColor.YELLOW + this.getTag();
        return basic + result;
    }


    //END OF UTIL
    //START OF BASIC SETTERS

    /**
     * Sets a name for the clan. <p>Takes into account the player attempting to do so.
     *
     * @param playerSenderUUID the UUID of the player trying to change the name of the clan
     * @param clanName         the new name of the clan.
     * @return '0' if the player successfully renames the name of the clan,
     * <p>ELSE
     * '30' if the player is null or the player has never played,
     * '1' if the player is not in this clan
     * '4' if the player does not have editing permission
     * '79' if the player has insufficient gold to change the name of the clan.
     * '10' if the name of the clan is less than 3 characters,
     * '11' if the name of the clan is more than 20 characters,
     * '12' if the regular expression does not match,
     * '21' if another clan already has that name.
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int setName(UUID playerSenderUUID, String clanName) {
        if (playerSenderUUID == null || !Bukkit.getOfflinePlayer(playerSenderUUID).hasPlayedBefore() || clanName == null || clanName.isEmpty())
            return 30;
        if (!this.isInThisClan(playerSenderUUID))
            return 1;

        else if (!this.hasEditingPermission(playerSenderUUID)) {
            return 4;
        } else if ("not have 20 gold".isEmpty()) {
            return 79;
        } else if (clanName.length() < 3) {
            return 10;
        } else if (clanName.length() > 20) {
            return 11;
        } else if (!clanName.matches("[A-Za-z0-9\\s]+")) {
            return 12;
        }

        for (Clan clan : clans) {
            if (clan.getName().toLowerCase().equals(clanName.toLowerCase()))
                return 21;
        }

        this.name = clanName;
        return 0;
    }

    /**
     * Sets a tag for the clan. <p>Takes into account the player attempting to do so.
     *
     * @param tag the new clan tag
     * @return '0' if successfully edits the tag of the clan,
     * <p>ELSE</p>
     * '30' if the player is null or the player has never played,
     * '1' if the player is not in this clan
     * '4' if the player does not have editing permission
     * '79' if the player has insufficient gold to change the name of the clan.
     * '10' if the name of the clan is not equal to 3 characters,
     * '12' if the regular expression does not match,
     * '21' if another clan already has that tag.
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int setTag(UUID senderPlayerUUID, String tag) {
        if (senderPlayerUUID == null || !Bukkit.getOfflinePlayer(senderPlayerUUID).hasPlayedBefore() || tag == null || tag.isEmpty())
            return 30;
        if (!this.isInThisClan(senderPlayerUUID))
            return 1;

        else if (!this.hasEditingPermission(senderPlayerUUID)) {
            return 4;
        } else if ("need 5 gold".isEmpty()) {
            return 79;
        } else if (tag.length() != 3) {
            return 10;
        } else if (!StringUtils.isAlphanumeric(tag)) {
            return 12;
        }

        for (Clan clan : clans) {
            if (clan.getTag().toLowerCase().equals(tag.toLowerCase()))
                return 21;
        }
        this.tag = tag;
        return 0;
    }

    /**
     * Sets the color of the clan
     *
     * @param color the new color of the clan
     */
    public void setColor(ChatColor color) {
        this.color = color;
    }

    /**
     * Sets the friendly fire property of the clan.
     *
     * @param friendlyFire whether friendly fire should be true or false
     */
    public void setFriendlyFire(boolean friendlyFire) {
        this.isFriendlyFire = friendlyFire;
    }

    //END OF BASIC SETTERS
    //START OF BASIC GETTERS

    /**
     * Returns the unique id of the clan
     * @return the unique id of the clan
     */
    public UUID getID() {
        return id;
    }

    /**
     * Gets the name of the clan
     *
     * @return name of the clan
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the tag of the clan
     *
     * @return the tag of the clan
     */
    public String getTag() {
        return tag;
    }

    /**
     * Gets the color of the clan.
     *
     * @return the color of the clan
     */
    public ChatColor getColor() {
        return color;
    }

    /**
     * Gets the clan's friendly fire property of the clan.
     *
     * @return whether friendly fire is true or false
     */
    public boolean isFriendlyFire() {
        return isFriendlyFire;
    }

    //END OF BASIC GETTERS
    //START OF ADMIN CLAN COMMANDS

    /**
     * Forcibly renames the clan. Ignores restrictions of name such as the length of the name and the limited characters of use. However, clans can not have the same name.<p>Administrative purposes</p>
     *
     * @param clanName the new name of the clan
     * @return '0' if successfully renames the clan,
     * <p>ELSE</p>
     * '30' if the name of the clan does not exist,
     * '21' if another clan already has the same name.
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int forceName(String clanName) {
        if (clanName == null || clanName.isEmpty())
            return 30;

        for (Clan clan : clans) {
            if (clan.getName().toLowerCase().equals(clanName.toLowerCase()))
                return 21;
        }

        this.name = clanName;
        return 0;
    }

    /**
     * Forcibly edits the tag of the clan. <p>Administrative purposes</p>
     *
     * @param tag the new tag of the clan
     * @return '0' if successfully edits the tag of the clan,
     * <p>ELSE</p>
     * '30' if the tag does not exist,
     * '21' if another clan already has that tag
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int forceTag(String tag) {
        if (tag == null || tag.isEmpty())
            return 30;
        for (Clan clan : clans) {
            if (clan.getTag().toLowerCase().equals(tag.toLowerCase()))
                return 21;
        }
        this.tag = tag;
        return 0;
    }

    /**
     * Forcibly assigns a new leader to the clan.<p>Administrative purposes</p>
     *
     * @param candidateLeader the new leader being assigned in format of Player's UUID
     * @return 0 if successfully forcibly assigns a new leader to the clan,
     * <p>ELSE</p>
     * '30' if not a valid Player's UUID,
     * '1' if the player is not in this clan,
     * '6' you are assigning leadership to the current leader.
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int forceLeader(UUID candidateLeader) {
        if (candidateLeader == null || !Bukkit.getOfflinePlayer(candidateLeader).hasPlayedBefore())
            return 30;

        if (!isInThisClan(candidateLeader)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (this.leader.equals(candidateLeader)) {
            return 6;
        }


        //Get the current rank of the candidate leader.
        ClanRank candidateRank = this.getClanRank(candidateLeader);

        //Get all the players in that rank.
        UUID[] candidateLeaderLeaving = this.getPlayerUUIDSBy(candidateRank);
        //Create an array of that rank minus the player who is leaving
        UUID[] newCandidateLeaderLeaving = new UUID[candidateLeaderLeaving.length - 1];

        ClanRank currLeaderNextRank = ClanRank.values()[ClanRank.values().length - 2];
        //Get all the players in the second to last rank. LEADER being last.
        UUID[] currentLeaderJoining = this.getPlayerUUIDSBy(currLeaderNextRank);
        //The current leader will be joining this group so add an element.
        UUID[] newCurrentLeaderJoining = new UUID[currentLeaderJoining.length + 1];

        //Looping the length of the small array because we could get IndexOutOfBounds exception
        for (int i = 0; i < newCandidateLeaderLeaving.length; i++) {
            //This remover is in charge of adding one to the element so we can skip!
            int remover = 0;

            //If the leader is found in the array then add the remover.
            if (candidateLeader.equals(candidateLeaderLeaving[i]))
                remover++; //

            //Skips the candidate and updates array.
            newCandidateLeaderLeaving[i] = candidateLeaderLeaving[i + remover];
        }

        for (int i = 0; i < currentLeaderJoining.length; i++) {
            newCurrentLeaderJoining[i] = currentLeaderJoining[i];
        }
        newCurrentLeaderJoining[newCurrentLeaderJoining.length - 1] = this.leader;

        this.leader = candidateLeader;
        updateRankUUIDS(candidateRank, newCandidateLeaderLeaving);
        updateRankUUIDS(currLeaderNextRank, newCurrentLeaderJoining);
        return 0;
    }

    /**
     * Forcefully kicks a player from the clan. <p>Admistrative purposes.</p>
     * @param kickedUUID The player being kicked from the clan.
     * @return '0' if successfully removes the player from the clan,
     * <p>ELSE</p>
     * '30' if the player does not exist,
     * '2' if the player is not in the clan,
     * '3' if the player is the leader,
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int forceKick(UUID kickedUUID) {
        if (kickedUUID == null || !Bukkit.getOfflinePlayer(kickedUUID).hasPlayedBefore())
            return 30;
        if (!isInThisClan(kickedUUID))
            return 2;


        ClanRank kickedRank = this.getClanRank(kickedUUID);
        ClanRank[] ranks = ClanRank.values();

        if (kickedRank.equals(ranks[ranks.length - 1])) {
            // leader cant be kicked
            return 3;
        }

        UUID[] leavingUUIDS = this.getPlayerUUIDSBy(kickedRank);
        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];

        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            int remover = 0;
            if (kickedUUID.equals(leavingUUIDS[i]))
                remover++;

            newLeavingUUIDS[i] = leavingUUIDS[i+remover];
        }

        updateRankUUIDS(kickedRank, newLeavingUUIDS);
        return 0;
    }

    /**
     * Forcefully creates the clan. <p>Administrative purposes</p>
     */
    public void forceCreate() {
        clans.add(this);
        ;
    }

    /**
     * Disbands the clan forcibly. <p>Administrative purposes</p>
     */
    public void forceDisband() {
        clans.remove(this);
    }

    //END OF ADMIN CLAN COMMANDS
    //START OF CLAN COMMANDS

    /**
     * Assigns a new leader to the clan. <p>Takes into account the player attempting to do so.
     *
     * @param currLeader      the uuid of the player trying to assign leadership
     * @param candidateLeader the new leader being assigned
     * @return 0 if successful,
     * '30' if not a valid current leader Player UUID or not a valid candidate leader Player UUID,
     * '1' if either player is not in this clan,
     * '4' The currLeader isn't the current leader,
     * '6' you are assigning leadership to the current leader,
     * '20' must have confirmation before assigning leadership.
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int leader(UUID currLeader, UUID candidateLeader) {
        if (currLeader == null || !Bukkit.getOfflinePlayer(currLeader).hasPlayedBefore() || candidateLeader == null || !Bukkit.getOfflinePlayer(candidateLeader).hasPlayedBefore())
            return 30;
        if (!isInThisClan(candidateLeader) || !isInThisClan(currLeader)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (!this.leader.equals(currLeader)) {
            return 4;
        }

        if (this.leader.equals(candidateLeader)) {
            return 6;
        }

        if (!leaderConfirmation.contains(currLeader))
            return 20;


        //Get the current rank of the candidate leader.
        ClanRank candidateRank = this.getClanRank(candidateLeader);

        //Get all the players in that rank.
        UUID[] candidateLeaderLeaving = this.getPlayerUUIDSBy(candidateRank);
        //Create an array of that rank minus the player who is leaving
        UUID[] newCandidateLeaderLeaving = new UUID[candidateLeaderLeaving.length - 1];

        ClanRank currLeaderNextRank = ClanRank.values()[ClanRank.values().length - 2];
        //Get all the players in the second to last rank. LEADER being last.
        UUID[] currentLeaderJoining = this.getPlayerUUIDSBy(currLeaderNextRank);
        //The current leader will be joining this group so add an element.
        UUID[] newCurrentLeaderJoining = new UUID[currentLeaderJoining.length + 1];

        //Looping the length of the small array because we could get IndexOutOfBounds exception
        for (int i = 0; i < newCandidateLeaderLeaving.length; i++) {
            //This remover is in charge of adding one to the element so we can skip!
            int remover = 0;

            //If the leader is found in the array then add the remover.
            if (candidateLeader.equals(candidateLeaderLeaving[i]))
                remover++; //

            //Skips the candidate and updates array.
            newCandidateLeaderLeaving[i] = candidateLeaderLeaving[i + remover];
        }

        for (int i = 0; i < currentLeaderJoining.length; i++) {
            newCurrentLeaderJoining[i] = currentLeaderJoining[i];
        }
        newCurrentLeaderJoining[newCurrentLeaderJoining.length - 1] = this.leader;

        this.leader = candidateLeader;
        updateRankUUIDS(candidateRank, newCandidateLeaderLeaving);
        updateRankUUIDS(currLeaderNextRank, newCurrentLeaderJoining);
        return 0;
    }

    /**
     * Demotes a player to a given rank.<p>Takes into account that a player is attempting to do so.
     *
     * @param senderUUID  The player attempting to demote the (demotedUUID) player.
     * @param demotedUUID The player being demoted.
     * @param demotedRank The rank attempting to demote to
     * @return '0' if successfully demoted the player to the demotedRank,
     * <p>ELSE</p>
     * '30' if not a valid sender Player UUID or not a valid demoted Player UUID or demotedRank is null,
     * '1' if either player is not in this clan,
     * '4' if the sending player does not have permission to manage ranks,
     * '3' if the sending player is the same or lower rank of the demoting player,
     * '5' if the current rank is equal or lower than the new rank. (You are trying to promote or move to same rank),
     * '2' if the rank is impossible to demote into (in this case: choosing leader would assume there is a higher rank and choosing the rank before leader would assume you can overthrow the leader).
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int demote(UUID senderUUID, UUID demotedUUID, ClanRank demotedRank) {
        if (senderUUID == null || !Bukkit.getOfflinePlayer(senderUUID).hasPlayedBefore() || demotedUUID == null || !Bukkit.getOfflinePlayer(demotedUUID).hasPlayedBefore() || demotedRank == null)
            return 30;
        if (!isInThisClan(demotedUUID) || !isInThisClan(senderUUID))
            return 1;

        if (!this.hasRankManagingPermission(senderUUID))
            return 4;

        ClanRank demotedCurrRank = this.getClanRank(demotedUUID);
        ClanRank senderRank = this.getClanRank(senderUUID);

        ClanRank[] ranks = ClanRank.values();

        int senderIndex = -1;
        int currIndex = -1;
        int newIndex = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(senderRank))
                senderIndex = i;
            if (ranks[i].equals(demotedCurrRank))
                currIndex = i;
            if (ranks[i].equals(demotedRank))
                newIndex = i;

            if (currIndex != -1 && newIndex != -1 && senderIndex != -1)
                break;

        }
        if (senderIndex <= currIndex) {
            return 3;
        }

        //The new index means this is OF higher rank or same rank. The player is not being demoted. Error
        if (currIndex <= newIndex)
            return 5;

        if (demotedRank.equals(ranks[ranks.length - 1]) || demotedRank.equals(ranks[ranks.length - 2])) {
            // cant demote to highest (doesnt make sense)
            //highest cant be overthrown
            return 2;
        }

        int differenceRank = currIndex - newIndex;

        ClanRank newRank = ranks[newIndex];

        //The demoted player's rank's list
        UUID[] leavingUUIDS = this.getPlayerUUIDSBy(demotedCurrRank);
        //The demoted player's new list
        UUID[] joiningUUIDS = this.getPlayerUUIDSBy(newRank);

        //Since leaving, we subtract one
        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        //Since joining, wer add one
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];

        //Loop the new list which is leaving
        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            int remover = 0;
            //If demoted is equal to leaving list skip and add one to index.
            if (demotedUUID.equals(leavingUUIDS[i]))
                remover++;

            newLeavingUUIDS[i] = leavingUUIDS[i + remover];
        }

        //Copy and add one
        for (int i = 0; i < joiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = demotedUUID;

        updateRankUUIDS(demotedCurrRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;
    }

    /**
     * Demotes a player to the previous rank.<p>Takes into account that a player is attempting to do so.
     *
     * @param senderUUID  The player attempting to demote the (demotedUUID) player.
     * @param demotedUUID The player being demoted.
     * @return '0' if successfully demoted the player to the previous rank,
     * <p>ELSE</p>
     * '30' if not a valid sender Player UUID or not a valid demoted Player UUID or demotedRank is null,
     * '1' if either player is not in this clan,
     * '4' if the sending player does not have permission to manage ranks,
     * '3' if the sending player is the same or lower rank of the demoting player,
     * '2' if the rank is impossible to demote into (in this case: choosing the leader would mean there is another rannk higher and choosing the lowest rank would mean there is a rank lower than that).
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int demote(UUID senderUUID, UUID demotedUUID) {
        if (senderUUID == null || !Bukkit.getOfflinePlayer(senderUUID).hasPlayedBefore() || demotedUUID == null || !Bukkit.getOfflinePlayer(demotedUUID).hasPlayedBefore())
            return 30;
        if (!isInThisClan(demotedUUID) || !isInThisClan(senderUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (!this.hasRankManagingPermission(senderUUID)) {
            return 4;
        }

        ClanRank currRank = this.getClanRank(demotedUUID);
        ClanRank senderRank = this.getClanRank(senderUUID);

        ClanRank[] ranks = ClanRank.values();

        int senderIndex = -1;
        int currIndex = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(senderRank))
                senderIndex = i;
            if (ranks[i].equals(currRank))
                currIndex = i;

            if (currIndex != -1 && senderIndex != -1)
                break;

        }

        if (senderIndex <= currIndex) {
            return 3;
        }

        int newIndex = currIndex - 1;


        if (currRank.equals(ranks[ranks.length - 1]) || currRank.equals(ranks[0])) {
            //highest cant be overthrown
            //lowest cant go lower
            return 2;
        }

        ClanRank newRank = ranks[newIndex];

        //The demoted player's rank's list
        UUID[] leavingUUIDS = this.getPlayerUUIDSBy(currRank);
        //The demoted player's new list
        UUID[] joiningUUIDS = this.getPlayerUUIDSBy(newRank);

        //Since leaving, we subtract one
        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        //Since joining, wer add one
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];

        //Loop the new list which is leaving
        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            int remover = 0;
            //If demoted is equal to leaving list skip and add one to index.
            if (demotedUUID.equals(leavingUUIDS[i]))
                remover++;

            newLeavingUUIDS[i] = leavingUUIDS[i + remover];
        }

        //Copy and add one
        for (int i = 0; i < joiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = demotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;

    }

    /**
     * Promotes a player to a given rank.<p>Takes into account that a player is attempting to do so.
     *
     * @param senderUUID   The player attempting to promote the (promotedUUID) player.
     * @param promotedUUID The player being promoted.
     * @param promotedRank The rank attempting to promote to
     * @return '0' if successfully promoted the player to the promotedRank,
     * <p>ELSE</p>
     * '30' if not a valid sender Player UUID or not a valid promoted Player UUID or promotedRank is null,
     * '1' if either player is not in this clan,
     * '4' if the sending player does not have permission to manage ranks,
     * '3' if the sending player is the same or lower rank of the promoting player,
     * '5' if the current rank is equal or higher than the new rank. (You are trying to demote or move to same rank),
     * '2' if the rank is impossible to demote into (in this case: choosing leader would mean overthrowing).
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int promote(UUID senderUUID, UUID promotedUUID, ClanRank promotedRank) {
        if (senderUUID == null || !Bukkit.getOfflinePlayer(senderUUID).hasPlayedBefore() || promotedUUID == null || !Bukkit.getOfflinePlayer(promotedUUID).hasPlayedBefore() || promotedRank == null)
            return 30;
        if (!isInThisClan(promotedUUID) || !isInThisClan(senderUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (!this.hasRankManagingPermission(senderUUID)) {
            return 4;
        }

        ClanRank currRank = this.getClanRank(promotedUUID);
        ClanRank senderRank = this.getClanRank(senderUUID);

        ClanRank[] ranks = ClanRank.values();

        int senderIndex = -1;
        int currIndex = -1;
        int newIndex = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(senderRank))
                senderIndex = i;
            if (ranks[i].equals(currRank))
                currIndex = i;
            if (ranks[i].equals(promotedRank))
                newIndex = i;

            if (currIndex != -1 && newIndex != -1 && senderIndex != -1)
                break;

        }
        if (senderIndex <= currIndex) {
            return 3;
        }

        //The new index means this is OF higher rank or same rank. The player is not being demoted. Error
        //The new index means this is OF lower rank or same rank. The player is not being promoted. Error
        if (currIndex >= newIndex)
            return 5;

        if (promotedRank.equals(ranks[ranks.length - 1])) {
            // leader cant be promoted
            return 2;
        }

        int differenceRank = newIndex - currIndex;

        ClanRank newRank = ranks[newIndex];

        UUID[] leavingUUIDS = this.getPlayerUUIDSBy(currRank);
        UUID[] joiningUUIDS = this.getPlayerUUIDSBy(newRank);

        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];

        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            int remover = 0;
            if (promotedUUID.equals(leavingUUIDS[i]))
                remover++;

            newLeavingUUIDS[i] = leavingUUIDS[i + remover];
        }

        for (int i = 0; i < joiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = promotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;

    }

    /**
     * Promotes a player to the next rank.<p>Takes into account that a player is attempting to do so.
     *
     * @param senderUUID   The player attempting to promote the (promotedUUID) player.
     * @param promotedUUID The player being promoted.
     * @return '0' if successfully promoted the player to the next rank,
     * <p>ELSE</p>
     * '30' if not a valid sender Player UUID or not a valid promoted Player UUID or promotedRank is null,
     * '1' if either player is not in this clan,
     * '4' if the sending player does not have permission to manage ranks,
     * '3' if the sending player is the same or lower rank of the promoting player,
     * '5' if the current rank is equal or higher than the new rank. (You are trying to demote or move to same rank),
     * '2' if the rank is impossible to demote into (in this case: choosing leader would mean overthrowing).
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int promote(UUID senderUUID, UUID promotedUUID) {

        if (senderUUID == null || !Bukkit.getOfflinePlayer(senderUUID).hasPlayedBefore() || promotedUUID == null || !Bukkit.getOfflinePlayer(promotedUUID).hasPlayedBefore())
            return 30;

        if (!isInThisClan(promotedUUID) || !isInThisClan(senderUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (!this.hasRankManagingPermission(senderUUID)) {
            return 4;
        }

        ClanRank currRank = this.getClanRank(promotedUUID);
        ClanRank senderRank = this.getClanRank(senderUUID);

        ClanRank[] ranks = ClanRank.values();

        int senderIndex = -1;
        int currIndex = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(senderRank))
                senderIndex = i;
            if (ranks[i].equals(currRank))
                currIndex = i;

            if (currIndex != -1 && senderIndex != -1)
                break;

        }

        if (senderIndex <= currIndex) {
            return 3;
        }


        int newIndex = currIndex + 1;


        if (currRank.equals(ranks[ranks.length - 1]) || currRank.equals(ranks[ranks.length - 2])) {
            //highest cant be overthrown
            //highest cant go any higher
            return 2;
        }

        ClanRank newRank = ranks[newIndex];

        UUID[] leavingUUIDS = this.getPlayerUUIDSBy(currRank);
        UUID[] joiningUUIDS = this.getPlayerUUIDSBy(newRank);

        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];

        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            int remover = 0;
            if (promotedUUID.equals(leavingUUIDS[i]))
                remover++;
            newLeavingUUIDS[i] = leavingUUIDS[i + remover];
        }
        for (int i = 0; i < joiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = promotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);
        return 0;

    }

    /**
     * Creates a clan by initializing the leader uuid and the name of the clan.
     *
     * @param playerUUID The leader's Player UUID
     * @param clanName   The name of the clan
     * @return '0' if successfully creates a clan,
     * <p>ELSE</p>
     * '30' if the player does not exist or the name of the clan does not exist,
     * '1' if the leader is already in a clan,
     * '79' if there is insufficient gold to create a clan,
     * '10' if the name of the clan is less than 3 characters,
     * '11' if the name of the clan is more than 20 characters,
     * '12' if the regular expressions don't match,
     * '21' if there is a name collision while creating the clan.
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int create(UUID playerUUID, String clanName) {
        if (playerUUID == null || !Bukkit.getOfflinePlayer(playerUUID).hasPlayedBefore())
            return 30;
        Clan potentialClan = Clan.getClan(playerUUID);

        if (potentialClan != null) {
            return 1;
        } else if ("NOT ENOUGH GOLD FOR THIS ".length() > 100) {
            return 79;
        } else if (clanName.length() < 3) {
            return 10;
        } else if (clanName.length() > 20) {
            return 11;
        } else if (!clanName.matches("[A-Za-z0-9\\s]+")) {
            return 12;
        }

        for (Clan clan : clans) {
            if (clan.getName().toLowerCase().equals(clanName.toLowerCase()))
                return 21;
        }

        clans.add(this);
        return 0;
    }

    /**
     * Disbands the clan.
     *
     * @param playerUUID The player disbanding the clan.
     * @return '0' if successfully disbands the clan,
     * <p>ELSE</p>
     * '30' if the player does not exist,
     * '4' if the player is not the leader,
     * '20' if confirmation is required.
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int disband(UUID playerUUID) {
        if (playerUUID == null || !Bukkit.getOfflinePlayer(playerUUID).hasPlayedBefore())
            return 30;
        if (!this.leader.equals(playerUUID)) {
            return 4;
        }

        if (!clanDisbandTimer.contains(playerUUID)) {
            return 20;
        }

        //Remove cache clan
        clans.remove(this);
        //todo call an event and maybe listen to the event to call a save event?
        return 0;
    }

    /**
     * Leaves the clan.
     *
     * @param leaverUUID The player leaving the clan
     * @return '0' if successfully leaves the clan,
     * <p>ELSE</p>
     * '30' if the leaver does not exist,
     * '1' if the leaver is not in this clan,
     * '3' if the leaver is the leader.
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int leave(UUID leaverUUID) {
        if (leaverUUID == null || !Bukkit.getOfflinePlayer(leaverUUID).hasPlayedBefore())
            return 30;
        if (!isInThisClan(leaverUUID))
            return 1;

        ClanRank leaverRank = this.getClanRank(leaverUUID);

        ClanRank[] ranks = ClanRank.values();
        if (leaverRank.equals(ranks[ranks.length - 1])) {
            // leader cant be kicked
            return 3;
        }


        UUID[] leavingUUIDS = this.getPlayerUUIDSBy(leaverRank);
        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];

        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            int remover = 0;
            if (leaverUUID.equals(leavingUUIDS[i]))
                remover++;

            newLeavingUUIDS[i] = leavingUUIDS[i+remover];
        }

        updateRankUUIDS(leaverRank, newLeavingUUIDS);
        return 0;
    }

    /**
     * Accepts the invitation to the clan.
     * @param inviteeUUID The player accepting the invitation
     * @return '0' if successfully joins the clan,
     * <p>ELSE</p>
     * '30' if the invitee does not exist,
     * '4' if the invitee has not been invited to this clan,
     * '5' the clan has reached max capacity of players,
     * '3' the invitee is already in this clan,
     * '2' the invitee is in another clan.
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int acceptInvitation(UUID inviteeUUID) {
        if (inviteeUUID == null || !Bukkit.getOfflinePlayer(inviteeUUID).hasPlayedBefore())
            return 30;
        if (!invitationConfirmation.containsKey(inviteeUUID) || !invitationConfirmation.get(inviteeUUID).contains(this))
            return 4;

        if (this.getAllUUIDS().length == maxCapacity) {
            return 5;
        }

        Clan inviteeClan = Clan.getClan(inviteeUUID);
        if (inviteeClan != null) {
            if (this.equals(inviteeClan)) {
                return 3;
            } else {
                return 2;
            }
        }

        ClanRank lowestRank = ClanRank.values()[0];
        UUID[] lowestRankUUIDS = getPlayerUUIDSBy(lowestRank);
        UUID[] tempLowestRank = new UUID[lowestRankUUIDS.length + 1];

        for (int i = 0; i < lowestRankUUIDS.length; i++) {
            tempLowestRank[i] = lowestRankUUIDS[i];
        }
        tempLowestRank[tempLowestRank.length - 1] = inviteeUUID;
        updateRankUUIDS(lowestRank, tempLowestRank);
        return 0;
    }

    /**
     * Denies the invitation to the clan.
     * @param inviteeUUID The player denying the invitation
     * @return '0' if successfully denies the invitation to the clan,
     * <p>ELSE</p>
     * '30' if the invitee does not exist,
     * '4' if the invitee has not been invited to this clan,
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int denyInvitation(UUID inviteeUUID) {
        if (inviteeUUID == null || !Bukkit.getOfflinePlayer(inviteeUUID).hasPlayedBefore())
            return 30;

        if (!invitationConfirmation.containsKey(inviteeUUID) || !invitationConfirmation.get(inviteeUUID).contains(this))
            return 4;

        return 0;
    }

    /**
     * Invites a player to the clan.
     *
     * @param inviterUUID the player trying to invite the other player
     * @param inviteeUUID the player being invited to the clan
     * @return '0' if successfully sends invite to player,
     * <p>ELSE</p>
     * '30' if the inviter or the invitee does not exist,
     * '1' if the inviter is not in this clan,
     * '4' if the inviter does not have moderating permission,
     * '5' if the clan has already reached max capacity of players,
     * '3' if the invitee is already in this clan,
     * '2' if the invitee is in another clan,
     * '20' if you have already invited the player
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int invite(UUID inviterUUID, UUID inviteeUUID) {
        if (inviterUUID == null || !Bukkit.getOfflinePlayer(inviterUUID).hasPlayedBefore() || inviteeUUID == null || !Bukkit.getOfflinePlayer(inviteeUUID).hasPlayedBefore())
            return 30;
        if (!isInThisClan(inviterUUID))
            return 1;

        if (!hasModeratingPermission(inviterUUID))
            return 4;

        if (this.getAllUUIDS().length == maxCapacity) {
            return 5;
        }
        Clan inviteeClan = Clan.getClan(inviteeUUID);
        if (inviteeClan != null) {
            if (this.equals(inviteeClan)) {
                return 3;
            } else {
                return 2;
            }
        }


        if (invitationConfirmation.containsKey(inviteeUUID) && invitationConfirmation.get(inviteeUUID).contains(this))
            return 20;


        return 0;
    }

    /**
     * Kicks a player from the clan.
     *
     * @param kickerUUID The player kicking the other
     * @param kickedUUID The player being removed
     * @return '0' if successfully kicked the player,
     * <p>ELSE</p>
     * '30' if the kicked player or the kicker player does not exist
     * '1' if the kicker is not in this clan,
     * '2' if the kicked player is not in this clan,
     * '4' if the kicker player does not have moderating permission,
     * '3' if the kicker is attempting to kick the leader,
     * '5' if the kicker does not have authority to kick someone of equal or higher rank.
     * <p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int kick(UUID kickerUUID, UUID kickedUUID) {
        if (kickerUUID == null || !Bukkit.getOfflinePlayer(kickerUUID).hasPlayedBefore() || kickedUUID == null || !Bukkit.getOfflinePlayer(kickedUUID).hasPlayedBefore())
            return 30;
        if (!isInThisClan(kickerUUID))
            return 1;
        if (!isInThisClan(kickedUUID))
            return 2;
        if (!hasModeratingPermission(kickerUUID))
            return 4;

        ClanRank kickerRank = this.getClanRank(kickerUUID);
        ClanRank kickedRank = this.getClanRank(kickedUUID);
        ClanRank[] ranks = ClanRank.values();

        if (kickedRank.equals(ranks[ranks.length - 1])) {
            // leader cant be kicked
            return 3;
        }

        int kickerIndex = -1;
        int kickedIndex = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(kickerRank))
                kickerIndex = i;
            if (ranks[i].equals(kickedRank))
                kickedIndex = i;
            if (kickedIndex != -1 && kickerIndex != -1)
                break;
        }

        if (kickerIndex <= kickedIndex) {
            return 5;
        }


        UUID[] leavingUUIDS = this.getPlayerUUIDSBy(kickedRank);
        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];

        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            int remover = 0;
            if (kickedUUID.equals(leavingUUIDS[i]))
                remover++;

            newLeavingUUIDS[i] = leavingUUIDS[i+remover];
        }

        updateRankUUIDS(kickedRank, newLeavingUUIDS);
        return 0;
    }

    //END OF CLAN COMMANDS

    //START OF PERMISSIONS

    /**
     * Checks whether a player has rank managing permissions. i.e: (promote/demote)
     * @param uuid the player in the clan
     * @return true if permissions are found, false if not permission or the player is not in this clan.
     */
    public boolean hasRankManagingPermission(UUID uuid) {
        if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore())
            return false;
        if (!isInThisClan(uuid))
            return false;
        ClanRank rank = getClanRank(uuid);
        switch (rank) {
            case LEADER:
            case COLEADER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks whether a player has editing permissions. i.e: (rename clan/edit tag of clan)
     * @param uuid the player in the clan
     * @return true if permissions are found, false if not permission or the player is not in this clan.
     */
    public boolean hasEditingPermission(UUID uuid) {
        if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore())
            return false;
        if (!isInThisClan(uuid))
            return false;
        ClanRank rank = getClanRank(uuid);
        switch (rank) {
            case LEADER:
            case COLEADER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks whether a player has moderating permissions. i.e: (invite/kick)
     * @param uuid the player in the clan
     * @return true if permissions are found, false if not permission or the player is not in this clan.
     */
    public boolean hasModeratingPermission(UUID uuid) {
        if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore())
            return false;

        if (!isInThisClan(uuid))
            return false;
        ClanRank rank = getClanRank(uuid);
        switch (rank) {
            case LEADER:
            case COLEADER:
                return true;
            default:
                return false;
        }
    }

    //END OF PERMISSIONS

    //OVERRIDES

    /**
     * Checks if an object is equal to this by comparing their unique id.
     * @param obj the clan you are comparing to
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;
        if (!obj.getClass().equals(this.getClass()))
            return false;

        Clan clan = (Clan) obj;
        return clan.getName().equals(this.getID());
    }

    /**
     * Returns the {@link Clan#info()} representation of the clan
     * @return a friendly readable string of the clan's properties
     */
    @Override
    public String toString() {
        return this.info();
    }


}
