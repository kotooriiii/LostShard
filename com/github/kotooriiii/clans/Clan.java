package com.github.kotooriiii.clans;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class Clan {

    private String name;
    private String tag;
    private ChatColor color;
    private boolean friendlyFire;

    private UUID leader;
    private UUID[] coleaders;
    private UUID[] members;

    private int maxCapacity;

    //constructor
    public Clan(String name, UUID leaderUUID) {
        this.name = name;
        this.tag = "null";
        this.color = ChatColor.WHITE;
        this.friendlyFire = false;

        this.maxCapacity = 15;

        //init
        this.coleaders = new UUID[0];
        this.members = new UUID[0];
    }

    public UUID[] getAllClanUsers() {

        UUID[] clanmembers = new UUID[this.getClanSize()]; //todo FIX THIS

        int counter = 0;
        for (ClanRank rank : ClanRank.values()) {
            for (UUID uuid : this.getClanRanksBy(rank)) {
                clanmembers[counter++] = uuid;
            }
        }

        return clanmembers;
    }

    public UUID[] getAllOnlineClanUsers() {
        UUID[] allClanmates = this.getAllClanUsers();
        ArrayList<UUID> onlineClanmates = new ArrayList<>(allClanmates.length);
        for (UUID uuid : allClanmates) {
            if (Bukkit.getPlayer(uuid).isOnline()) {
                onlineClanmates.add(uuid);
            }
        }
        return onlineClanmates.toArray(new UUID[0]);
    }

    //GETTERS AND SETTERS
    public String getName() {
        return name;
    }

    /**
     * Set a name for the clan
     *
     * @param clanName the clan name
     * @return 0 if successfully renames the name of the clan,
     * 1 if not in this clan
     * 4 if has editing permission
     * 10 if less than 3 characters,
     * 11 if more than 20 characters,
     * 12 if regex does not match,
     * 79 if gold insufficiency
     * <p>
     * Priority of error return codes (ascending): 1, 4, 79, 10, 11, 12
     */
    public int setName(UUID playerUUID, String clanName) {

        if (!this.isInThisClan(playerUUID))
            return 1;

        else if (this.hasEditingPermission(playerUUID)) {
            return 4;
        }
        //todo if you have enough gold, and make these all else if
        if (clanName.length() < 3) {
            return 10;
        } else if (clanName.length() > 20) {
            return 11;
        } else if (!clanName.matches("[A-Z]|[a-z]|[0-9]|\\s")) {
            return 12;
        }

        this.name = name;
        return 0;
    }

    public void setForceName(String clanName) {
        this.name = clanName;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    /**
     * Sets a tag for the clan
     *
     * @param tag the clan name
     * @return 0 if successfully renames the name of the clan,
     * 1 if not in this clan
     * 4 if has editing permission
     * 10 if less than 3 characters,
     * 11 if more than 20 characters,
     * 12 if regex does not match,
     * 79 if gold insufficiency
     * <p>
     * Priority of error return codes (ascending): 1, 4, 79, 10, 12
     */
    public int setTag(UUID playerUUID, String tag) {

        if (!this.isInThisClan(playerUUID))
            return 1;

        else if (this.hasEditingPermission(playerUUID)) {
            return 4;
        }
        //todo if you have enough gold, and make these all else if
        if (tag.length() != 3) {
            return 10;
        } else if (!StringUtils.isAlphanumeric(tag)) {
            return 12;
        }
        this.tag = tag;
        return 0;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getClanSize() {

        int counter = 0;
        for (ClanRank rank : ClanRank.values()) {
            for (UUID uuid : this.getClanRanksBy(rank)) {
                counter++;
            }
        }
        return counter;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public boolean getFriendlyFire() {
        return friendlyFire;
    }

    /**
     * Tries to assign a new leader. doesnt account perms
     *
     * @param newLeader the new leader being assigned
     * @return 0 if successful,
     * 1 not in clan
     * 6 same person
     */
    public int setLeader(UUID newLeader) {

        if (!isInThisClan(newLeader)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (this.leader.equals(newLeader)) {
            return 6;
        }


        ClanRank newLeaderRank = this.getClanRank(newLeader);
        UUID[] newLeaderLeaving = this.getClanRanksBy(newLeaderRank);
        UUID[] currLeaderJoining = new UUID[newLeaderLeaving.length];
        for (int i = 0; i < currLeaderJoining.length; i++) {
            if (newLeader.equals(newLeaderLeaving[i])) {
                currLeaderJoining[i] = this.leader;
            } else {
                currLeaderJoining[i] = newLeaderLeaving[i];
            }
        }

        this.leader = newLeader;
        updateRankUUIDS(newLeaderRank, currLeaderJoining);

        return 0;
    }

    /**
     * Tries to assign a new leader. doesnt account perms
     *
     * @param newLeader the new leader being assigned
     * @return 0 if successful,
     * 1 not in clan
     * 4 currLeader isnt the current leader
     * 6 same person
     * 20 ask for confirmation
     */
    public int setLeader(UUID currLeader, UUID newLeader) {

        if (!isInThisClan(newLeader) || !isInThisClan(currLeader)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (!this.leader.equals(currLeader)) {
            return 4;
        }

        if (this.leader.equals(newLeader)) {
            return 6;
        }

        if (!leaderConfirmation.contains(currLeader))
            return 20;


        ClanRank newLeaderRank = this.getClanRank(newLeader);
        UUID[] newLeaderLeaving = this.getClanRanksBy(newLeaderRank);
        UUID[] currLeaderJoining = new UUID[newLeaderLeaving.length];
        for (int i = 0; i < currLeaderJoining.length; i++) {
            if (newLeader.equals(newLeaderLeaving[i])) {
                currLeaderJoining[i] = this.leader;
            } else {
                currLeaderJoining[i] = newLeaderLeaving[i];
            }
        }

        this.leader = newLeader;
        updateRankUUIDS(newLeaderRank, currLeaderJoining);

        return 0;
    }

    /**
     * Demotes a player to the previous rank.
     *
     * @param demotedUUID The player being demoted.
     * @return Returns 0 if successfully demoted the player to the previous rank,
     * 1 if the demoted player does not belong in this clan,
     * 2 if the rank is impossible to demote into,
     * <p>
     * Priority of error return codes (ascending): 1, 2
     */
    public int demote(UUID demotedUUID) {

        if (!isInThisClan(demotedUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        ClanRank currRank = this.getClanRank(demotedUUID);
        ClanRank[] ranks = ClanRank.values();
        if (currRank.equals(ranks[ranks.length - 1]) || currRank.equals(ranks[0])) {
            //highest cant be overthrown
            //lowest cant go lower
            return 2;
        }

        int currIndex = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(currRank)) {
                currIndex = i;
                break;
            }
        }

        int newIndex = currIndex - 1;
        ClanRank newRank = ranks[newIndex];

        UUID[] leavingUUIDS = this.getClanRanksBy(currRank);
        UUID[] joiningUUIDS = this.getClanRanksBy(newRank);

        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];
        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            if (demotedUUID.equals(leavingUUIDS[i]))
                continue;
            newLeavingUUIDS[i] = leavingUUIDS[i];
        }
        for (int i = 0; i < newJoiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = demotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;
    }

    /**
     * Demotes a player to a given rank.
     *
     * @param demotedUUID The player being demoted.
     * @param demotedRank The rank attempting to demote into
     * @return Returns 0 if successfully demoted the player to the demotedRank supplied,
     * 1 if the demoted player does not belong in this clan,
     * 2 if the rank is impossible to demote into,
     * 5 if the current rank is equal or lower than the new rank. (You are trying to promote or move to same rank)
     * <p>
     * Priority of error return codes (ascending): 1, 5, 2
     */
    public int demote(UUID demotedUUID, ClanRank demotedRank) {

        if (!isInThisClan(demotedUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        ClanRank currRank = this.getClanRank(demotedUUID);
        ClanRank[] ranks = ClanRank.values();


        int currIndex = -1;
        int newIndex = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(currRank))
                currIndex = i;
            if (ranks[i].equals(demotedRank))
                newIndex = i;

            if (currIndex != -1 && newIndex != -1)
                break;

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

        UUID[] leavingUUIDS = this.getClanRanksBy(currRank);
        UUID[] joiningUUIDS = this.getClanRanksBy(newRank);

        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];
        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            if (demotedUUID.equals(leavingUUIDS[i]))
                continue;
            newLeavingUUIDS[i] = leavingUUIDS[i];
        }
        for (int i = 0; i < newJoiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = demotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;
    }

    /**
     * Demotes a player to a given rank. Takes into account that a player is attempting to do so.
     *
     * @param senderUUID  The player attempting to demote another player.
     * @param demotedUUID The player being demoted.
     * @param demotedRank The rank attempting to demote into
     * @return Returns 0 if successfully demoted the player to the demotedRank supplied,
     * 1 if the sender or demoted player do not belong in this clan,
     * 2 if the rank is impossible to demote into,
     * 3 if the sending player is the same or lower rank of the demoting player.
     * 4 if the sender does not have permission to manage ranks.
     * 5 if the current rank is equal or lower than the new rank. (You are trying to promote or move to same rank)
     * <p>
     * Priority of error return codes (ascending): 1, 4, 3, 5, 2
     */
    public int demote(UUID senderUUID, UUID demotedUUID, ClanRank demotedRank) {

        if (!isInThisClan(demotedUUID) || !isInThisClan(senderUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (this.hasRankManagingPermission(senderUUID)) {
            return 4;
        }

        ClanRank currRank = this.getClanRank(demotedUUID);
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

        UUID[] leavingUUIDS = this.getClanRanksBy(currRank);
        UUID[] joiningUUIDS = this.getClanRanksBy(newRank);

        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];
        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            if (demotedUUID.equals(leavingUUIDS[i]))
                continue;
            newLeavingUUIDS[i] = leavingUUIDS[i];
        }
        for (int i = 0; i < newJoiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = demotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;

    }

    /**
     * Demotes a player to the previous rank if possible. Takes into account that a player is attempting to do so.
     *
     * @param senderUUID  The player attempting to demote another player.
     * @param demotedUUID The player being demoted.
     * @return Returns 0 if successfully demoted the player to the previous rank,
     * 1 if the sender or demoted player do not belong in this clan,
     * 2 if the rank is impossible to promote into,
     * 3 if the sending player is the same or lower rank of the demoting player.
     * 4 if the sender does not have permission to manage ranks.
     * <p>
     * Priority of error return codes (ascending): 1, 4, 3, 2
     */
    public int demote(UUID senderUUID, UUID demotedUUID) {

        if (!isInThisClan(demotedUUID) || !isInThisClan(senderUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (this.hasRankManagingPermission(senderUUID)) {
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

        UUID[] leavingUUIDS = this.getClanRanksBy(currRank);
        UUID[] joiningUUIDS = this.getClanRanksBy(newRank);

        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];
        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            if (demotedUUID.equals(leavingUUIDS[i]))
                continue;
            newLeavingUUIDS[i] = leavingUUIDS[i];
        }
        for (int i = 0; i < newJoiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = demotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;

    }

    /**
     * Promotes a player to a given rank.
     *
     * @param promotedUUID The player being promoted.
     * @return Returns 0 if successfully promoted the player to the promotedRank supplied,
     * 1 if the promoted player does not belong in this clan,
     * 2 if the rank is impossible to promote into,
     * <p>
     * Priority of error return codes (ascending): 1, 2
     */
    public int promote(UUID promotedUUID) {

        if (!isInThisClan(promotedUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        ClanRank currRank = this.getClanRank(promotedUUID);
        ClanRank[] ranks = ClanRank.values();
        if (currRank.equals(ranks[ranks.length - 1]) || currRank.equals(ranks[ranks.length - 2])) {
            //highest cant be overthrown
            //highest cant go any higher
            return 2;
        }

        int currIndex = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(currRank)) {
                currIndex = i;
                break;
            }
        }

        int newIndex = currIndex + 1;
        ClanRank newRank = ranks[newIndex];

        UUID[] leavingUUIDS = this.getClanRanksBy(currRank);
        UUID[] joiningUUIDS = this.getClanRanksBy(newRank);

        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];
        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            if (promotedUUID.equals(leavingUUIDS[i]))
                continue;
            newLeavingUUIDS[i] = leavingUUIDS[i];
        }
        for (int i = 0; i < newJoiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = promotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;
    }

    /**
     * Promotes a player to a given rank.
     *
     * @param promotedUUID The player being promoted.
     * @param promotedRank The rank attempting to promote into
     * @return Returns 0 if successfully promoted the player to the promotedRank supplied,
     * 1 if the promoted player does not belong in this clan,
     * 2 if the rank is impossible to promote into,
     * 5 if the current rank is equal or higher than the new rank. (You are trying to demote or move to same rank)
     * <p>
     * Priority of error return codes (ascending): 1, 5, 2
     */
    public int promote(UUID promotedUUID, ClanRank promotedRank) {

        if (!isInThisClan(promotedUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        ClanRank currRank = this.getClanRank(promotedUUID);
        ClanRank[] ranks = ClanRank.values();


        int currIndex = -1;
        int newIndex = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(currRank))
                currIndex = i;
            if (ranks[i].equals(promotedRank))
                newIndex = i;

            if (currIndex != -1 && newIndex != -1)
                break;

        }

        //The new index means this is OF lower rank or same rank. The player is not being promoted. Error
        if (currIndex >= newIndex)
            return 5;

        if (promotedRank.equals(ranks[ranks.length - 1])) {
            // leader cant be promoted
            return 2;
        }
        //How many times you jumped. Not really needed but nice to visualize.
        int differenceRank = newIndex - currIndex;

        ClanRank newRank = ranks[newIndex];

        UUID[] leavingUUIDS = this.getClanRanksBy(currRank);
        UUID[] joiningUUIDS = this.getClanRanksBy(newRank);

        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];
        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            if (promotedUUID.equals(leavingUUIDS[i]))
                continue;
            newLeavingUUIDS[i] = leavingUUIDS[i];
        }
        for (int i = 0; i < newJoiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = promotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;
    }

    //G

    /**
     * Promotes a player to a given rank. Takes into account that a player is attempting to do so.
     *
     * @param senderUUID   The player attempting to promote another player.
     * @param promotedUUID The player being promoted.
     * @param promotedRank The rank attempting to promote into
     * @return Returns 0 if successfully promoted the player to the promotedRank supplied,
     * 1 if the sender or promoted player do not belong in this clan,
     * 2 if the rank is impossible to promote into,
     * 3 if the sending player is the same or lower rank of the promoting player.
     * 4 if the sender does not have permission to manage ranks.
     * 5 if the current rank is equal or higher than the new rank. (You are trying to demote or move to same rank)
     * <p>
     * Priority of error return codes (ascending): 1, 4, 3, 5, 2
     */
    public int promote(UUID senderUUID, UUID promotedUUID, ClanRank promotedRank) {

        if (!isInThisClan(promotedUUID) || !isInThisClan(senderUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (this.hasRankManagingPermission(senderUUID)) {
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

        int differenceRank = currIndex - newIndex;

        ClanRank newRank = ranks[newIndex];

        UUID[] leavingUUIDS = this.getClanRanksBy(currRank);
        UUID[] joiningUUIDS = this.getClanRanksBy(newRank);

        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];
        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            if (promotedUUID.equals(leavingUUIDS[i]))
                continue;
            newLeavingUUIDS[i] = leavingUUIDS[i];
        }
        for (int i = 0; i < newJoiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = promotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;

    }

    //G

    /**
     * Promotes a player to the next rank if possible. Takes into account that a player is attempting to do so.
     *
     * @param senderUUID   The player attempting to promote another player.
     * @param promotedUUID The player being promoted.
     * @return Returns 0 if successfully promoted the player to the next rank,
     * 1 if the sender or promoted player do not belong in this clan,
     * 2 if the rank is impossible to promote into,
     * 3 if the sending player is the same or lower rank of the promoting player.
     * 4 if the sender does not have permission to manage ranks.
     * <p>
     * Priority of error return codes (ascending): 1, 4, 3, 2
     */
    public int promote(UUID senderUUID, UUID promotedUUID) {

        if (!isInThisClan(promotedUUID) || !isInThisClan(senderUUID)) {
            return 1;
            //not in a clan or 'this' clan
        }

        if (this.hasRankManagingPermission(senderUUID)) {
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

        UUID[] leavingUUIDS = this.getClanRanksBy(currRank);
        UUID[] joiningUUIDS = this.getClanRanksBy(newRank);

        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];
        UUID[] newJoiningUUIDS = new UUID[joiningUUIDS.length + 1];
        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            if (promotedUUID.equals(leavingUUIDS[i]))
                continue;
            newLeavingUUIDS[i] = leavingUUIDS[i];
        }
        for (int i = 0; i < newJoiningUUIDS.length; i++) {
            newJoiningUUIDS[i] = joiningUUIDS[i];
        }
        newJoiningUUIDS[newJoiningUUIDS.length - 1] = promotedUUID;

        updateRankUUIDS(currRank, newLeavingUUIDS);
        updateRankUUIDS(newRank, newJoiningUUIDS);

        return 0;

    }

    private void updateRankUUIDS(ClanRank rank, UUID[] uuids) {
        switch (rank) {
            case LEADER:
                //Intentionally left blank to indicate this method cannot update a leader unless made explictly by
                //the #setLeader method
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
     * Sends a message to everyone in the clan
     *
     * @param message the message being delivered to the clan
     */
    public void broadcast(String message) {
        UUID[] clanmates = this.getAllOnlineClanUsers();
        for (int i = 0; i < clanmates.length; i++) {
            Bukkit.getPlayer(clanmates[i]).sendMessage(message);
        }
        return;
    }

    /**
     * Sends a message to everyone in the clan with the exclusion of the supplied array
     *
     * @param message      the message being delivered to the clan
     * @param restrictions the players who will not receive the message
     */
    public void broadcast(String message, UUID[] restrictions) {
        UUID[] clanmates = this.getAllOnlineClanUsers();
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
            Bukkit.getPlayer(clanmates[i]).sendMessage(message);
        }
        return;
    }

    /**
     * Tries to add a player to the clan
     *
     * @param recruitUUID player being added to clan
     * @return 0 if successful,
     * 1 member is already part of clan,
     * <p>
     * priority 5 1
     */

    //todo to add later
    public int forceAdd(UUID recruitUUID) {
        return -1;
    }


    /**
     * Creates the clan.
     */
    public int create(UUID playerUUID, String clanName) {
        Clan potentialClan = Clan.getClan(playerUUID);

        if (potentialClan != null) {
            return 1;
        } else if ("NOT ENOUGH GOLD FOR THIS ".length() > 100) {
            return 79;
        } else if (clanName.length() < 3) {
            return 10;
        } else if (clanName.length() > 20) {
            return 11;
        } else if (!clanName.matches("[A-Z]|[a-z]|[0-9]|\\s")) {
            return 12;
        }
        this.leader = playerUUID;
        clans.add(this);
        return 0;
    }

    /**
     * Disbands the clan.
     */
    public int disband(UUID playerUUID) {

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
     * Invites a player to the clan if possible.
     *
     * @param inviterUUID the player trying to invite the other player
     * @param inviteeUUID the player being invited to the clan
     * @return 0 if successfully sends invite to player,
     * 1 if the inviter is not in a clan
     * 2 if the invitee already has a clan
     * 3 if in same clan
     * 4 no permission to invite
     * 5 can't invite @ max capacity
     * 20 already invited waiting on confirmation
     * <p>
     * 1, 4, 5, 3, 2, 20
     */
    public int invite(UUID inviterUUID, UUID inviteeUUID) {
        if (!isInThisClan(inviterUUID))
            return 1;

        if (!hasModeratingPermission(inviterUUID))
            return 4;

        if (this.getAllClanUsers().length == maxCapacity) {
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
     * @param inviteeUUID
     * @return 0 if successful,
     * 2 is in a clan
     * 3 if in same clan
     * 4 no permission to join
     * 5 @ max capacity
     * <p>
     * priority 4 5 3 2
     */
    public int acceptInvitation(UUID inviteeUUID) {
        if (!invitationConfirmation.containsKey(inviteeUUID) || !invitationConfirmation.get(inviteeUUID).contains(this))
            return 4;

        if (this.getAllClanUsers().length == maxCapacity) {
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
        UUID[] lowestRankUUIDS = getClanRanksBy(lowestRank);
        UUID[] tempLowestRank = new UUID[lowestRankUUIDS.length + 1];

        for (int i = 0; i < tempLowestRank.length; i++) {
            tempLowestRank[i] = lowestRankUUIDS[i];
        }
        tempLowestRank[tempLowestRank.length - 1] = inviteeUUID;
        updateRankUUIDS(lowestRank, tempLowestRank);

        return 0;
    }

    public int denyInvitation(UUID inviteeUUID) {
        if (!invitationConfirmation.containsKey(inviteeUUID) || !invitationConfirmation.get(inviteeUUID).contains(this))
            return 4;

        return 0;
    }

    /**
     * Attempts to kick another player accounting a player
     *
     * @param kickerUUID
     * @param kickedUUID
     * @return 0 if successfully kicked the player,
     * 1 if kicker is not in this clan
     * 2 if kicked is not in this clan
     * 3 leader being overthrown
     * 4 no moderating permission
     * 5 no authority to kick someone of equal or higher rank than you
     */
    public int kick(UUID kickerUUID, UUID kickedUUID) {
        if (!isInThisClan(kickerUUID))
            return 1;
        if (!isInThisClan(kickedUUID))
            return 2;
        if (!hasModeratingPermission(kickedUUID))
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
            return 3;
        }


        UUID[] leavingUUIDS = this.getClanRanksBy(kickedRank);
        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];

        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            if (kickedUUID.equals(leavingUUIDS[i]))
                continue;
            newLeavingUUIDS[i] = leavingUUIDS[i];
        }

        updateRankUUIDS(kickedRank, newLeavingUUIDS);
        return 0;
    }

    /**
     * Attempts to leave clan
     *
     * @param leaverUUID
     * @return 0 if successfully left the clan,
     * 1 if leaver is not in this clan
     * 3 leader cant leave!!
     */
    public int leave(UUID leaverUUID) {
        if (!isInThisClan(leaverUUID))
            return 1;

        ClanRank leaverRank = this.getClanRank(leaverUUID);

        ClanRank[] ranks = ClanRank.values();
        if (leaverRank.equals(ranks[ranks.length - 1])) {
            // leader cant be kicked
            return 3;
        }


        UUID[] leavingUUIDS = this.getClanRanksBy(leaverRank);
        UUID[] newLeavingUUIDS = new UUID[leavingUUIDS.length - 1];

        for (int i = 0; i < newLeavingUUIDS.length; i++) {
            if (leaverUUID.equals(leavingUUIDS[i]))
                continue;
            newLeavingUUIDS[i] = leavingUUIDS[i];
        }

        updateRankUUIDS(leaverRank, newLeavingUUIDS);
        return 0;
    }

    public UUID[] getClanRanksBy(ClanRank rank) {
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
     * Gets the clan of the supplied Player UUID
     *
     * @param uuid
     * @return
     */
    public static Clan getClan(UUID uuid) {
        for (Clan clan : clans) {
            for (UUID clanUUID : clan.getAllClanUsers()) {
                if (clanUUID.equals(uuid)) {
                    return clan;
                }
            }
        }
        return null;
    }

    /**
     * Gets the clan of the supplied clan name
     *
     * @param clanName
     * @return
     */
    public static Clan getClan(String clanName) {
        for (Clan clan : clans) {
            String iteratingClanName = clan.getName().toUpperCase();
            if (iteratingClanName.equals(clanName.toUpperCase()))
                return clan;
        }

        return null;
    }

    public static boolean isClan(String clanName) {
        for (Clan clan : clans) {
            String iteratingClanName = clan.getName().toUpperCase();
            if (iteratingClanName.equals(clanName.toUpperCase()))
                return true;
        }

        return false;
    }

    /**
     * Gets the clan of the supplied Player UUID
     *
     * @param uuid
     * @return
     */
    public static boolean hasClan(UUID uuid) {
        for (Clan clan : clans) {
            for (UUID clanUUID : clan.getAllClanUsers()) {
                if (clanUUID.equals(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the clan rank of a clansman.
     *
     * @param uuid The UUID of the player
     * @return ClanRank. null if clan does not include player uuid
     */
    public ClanRank getClanRank(UUID uuid) {

        for (ClanRank clanRank : ClanRank.values()) {
            for (UUID clanmateUUID : this.getClanRanksBy(clanRank)) {
                if (clanmateUUID.equals(uuid))
                    return clanRank;
            }
        }


        return null;
    }

    public boolean isInThisClan(UUID playerUUID) {
        for (UUID clanUUID : this.getAllClanUsers()) {
            if (clanUUID.equals(playerUUID)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRankManagingPermission(UUID uuid) {
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

    public boolean hasEditingPermission(UUID uuid) {
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

    public boolean hasModeratingPermission(UUID uuid) {
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

    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;
        if (!obj.getClass().equals(this.getClass()))
            return false;

        Clan clan = (Clan) obj;
        return clan.getName().equals(this.getName());
    }

    @Override
    public String toString() {
        return this.getTag() + " " + this.getName() + "\nLeader:" + this.leader + "\nColeaders: " + new ArrayList<UUID>(Arrays.asList(this.coleaders)).toString() + "\nMembers: " + new ArrayList<UUID>(Arrays.asList(this.members)).toString();
    }


}
