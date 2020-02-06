package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.clans.ClanRank;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class ClanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            Player playerSender = (Player) sender;
            UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "clan" command
            if (cmd.getName().equalsIgnoreCase("clan")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    //Send the help page and return.
                    sendHelp();
                    return true;
                }
                //This statement refers to: /clans <argument 0>
                else if (args.length == 1) {
                    //Sub-commands
                    switch (args[0].toLowerCase()) {
                        case "create":
                            playerSender.sendMessage("You must make a name for your clan. Try again using /clan create (name).");
                            break;
                        case "disband":
                            disbandClan(playerSender, playerUUID);
                            break;
                        case "tag":
                            playerSender.sendMessage("You must supply a tag for your clan: /clan tag <tagName>");
                            break;
                        case "rename":
                            playerSender.sendMessage("You must supply a name for your clan: /clan rename <clanName>");
                            break;
                        case "invite":
                            playerSender.sendMessage("To invite a player to your clan: /clan invite <playerName>");
                            break;
                        case "kick":
                            playerSender.sendMessage("To kick a player from your clan: /clan kick <playerName>");
                            break;
                        case "leave":
                            clanLeave(playerSender, playerUUID);
                            break;
                        case "leader":
                            playerSender.sendMessage("To assign leadership to another player: /clan leader <playerName>");
                            break;
                        case "promote":
                            playerSender.sendMessage("To promote a member to co-leader: /clan promote <playerName>");
                            break;
                        case "demote":
                            playerSender.sendMessage("To demote a co-leader to member: /clan demote <playerName> [opt: rankName]");
                            break;
                        case "chat": //TODO LATER
                            break;
                        case "accept":
                            clanAccept(playerSender, playerUUID);
                            break;
                        case "deny":
                            clanDeny(playerSender, playerUUID);
                            break;
                        default: //Not a premade command. This means something like: /clans chocolatebunnies
                            sendUnknownCommand(playerSender);
                            break;
                    }

                }
                //This statement refers to: /clans <argument 0> <argument 1> ...
                else if (args.length > 1) {
                    //Sub-commands again however with proper argument.
                    String supply = stringBuilder(args);
                    switch (args[0].toLowerCase()) {
                        case "create":
                            createClan(playerSender, playerUUID, supply);
                            break;
                        case "disband":
                            playerSender.sendMessage("The correct argument syntax is: /clan disband");
                            break;
                        case "tag":
                            editTag(playerSender, playerUUID, supply);
                            break;
                        case "rename":
                            editName(playerSender, playerUUID, supply);
                            break;
                        case "invite":
                            if (args.length == 2)
                                clanInvite(playerSender, playerUUID, args[1]);
                            else
                                playerSender.sendMessage(ChatColor.RED + "You provided too many arguments: /clan invite <playerName>");
                            break;
                        case "kick":
                            if (args.length == 2)
                                clanKick(playerSender, playerUUID, args[1]);
                            else
                                playerSender.sendMessage(ChatColor.RED + "You provided too many arguments: /clan kick <playerName>");
                            break;
                        case "leave":
                            playerSender.sendMessage("You provided too many arguments: /clan leave");
                            break;
                        case "leader":
                            if (args.length == 2)
                                clanLeader(playerSender, playerUUID, args[1]);
                            else
                                playerSender.sendMessage(ChatColor.RED + "You provided too many arguments: /clan leader <playerName>");
                            break;
                        case "promote":
                            if (args.length == 2)
                                clanPromote(playerSender, playerUUID, args[1]);
                            else if (args.length == 3)
                                clanPromote(playerSender, playerUUID, args[1], args[2]);
                            else
                                playerSender.sendMessage(ChatColor.RED + "You provided too many arguments: /clan promote <playerName> [opt: rankName]");

                            break;
                        case "demote":
                            if (args.length == 2)
                                clanDemote(playerSender, playerUUID, args[1]);
                            else if (args.length == 3)
                                clanDemote(playerSender, playerUUID, args[1], args[2]);
                            else
                                playerSender.sendMessage(ChatColor.RED + "You provided too many arguments: /clan demote <playerName> [opt: rankName]");
                            break;
                        case "chat": //TODO LATER
                            break;
                        case "accept":
                            clanAccept(playerSender, playerUUID, supply);
                            break;
                        case "deny": //implementation later
                            clanDeny(playerSender, playerUUID, supply);
                            break;
                        default: //Not a premade command. This means something like: /clans chocolatebunnies white
                            sendUnknownCommand(playerSender);
                            break;
                    }

                }


            }

        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("This command is not yet optimized for the console. Bother the developer to add commands :)");
        } //end of console sending commands
        return false;
    }//end of commands

    private void sendUnknownCommand(Player playerSender) {
        playerSender.sendMessage("The sub-command you provided does not exist in clans. Use '/clans' for help.");
    }

    private void clanLeave(final Player playerSender, final UUID playerUUID) {
        /**
         * Attempts to leave clan
         *
         * @param leaverUUID
         * @return 0 if successfully left the clan,
         * 1 if leaver is not in this clan
         * 3 leader cant leave!!
         */
        final Clan senderClan = Clan.getClan(playerUUID);
        if (senderClan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        }

        switch (senderClan.leave(playerUUID)) {
            case 0:
                senderClan.broadcast(playerSender.getDisplayName() + " has left the clan.", new UUID[]{playerUUID});
                playerSender.sendMessage("You have left the clan.");
                break;
            case 1:
                //rare to happen, must be error since already covered when you GOT player clan
                playerSender.sendMessage("You are not in a clan.");
                break;
            case 3:
                playerSender.sendMessage("You can not leave before promoting someone else to Leader.");
                break;

        }
    }

    private void clanKick(final Player playerSender, final UUID playerUUID, String targetPlayerName) {
        final Clan senderClan = Clan.getClan(playerUUID);
        final Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        } else if (targetPlayer == null) {
            playerSender.sendMessage(ChatColor.RED + "The player with that name was not able to be found.");
            return;
        }

        //At this point, the player exists AND the sender is in a clan

        final UUID targetPlayerUUID = targetPlayer.getUniqueId();

        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         *
         * Error codes found on the definition of clan#demote
         */
        /**
         * Attempts to kick another player accounting a player
         * @param kickerUUID
         * @param kickedUUID
         * @return 0 if successfully kicked the player,
         * 1 if kicker is not in this clan
         * 2 if kicked is not in this clan
         * 3 leader being overthrown
         * 4 no moderating permission
         * 5 no authority to kick someone of equal or higher rank than you
         */
        switch (senderClan.kick(playerUUID, targetPlayerUUID)) {
            case 0:
                senderClan.broadcast(playerSender.getDisplayName() + " has kicked " + targetPlayer.getDisplayName() + " from the clan.", new UUID[]{targetPlayerUUID, playerUUID});
                playerSender.sendMessage("You have kicked " + targetPlayer.getDisplayName() + " from the clan.");
                targetPlayer.sendMessage("You have been kicked from the clan by " + playerSender.getDisplayName() + ".");
                break;
            case 1:
                playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
                break;
            case 2:
                playerSender.sendMessage(ChatColor.RED + "That player is not in your clan.");
            case 3:
            case 5:
                playerSender.sendMessage(ChatColor.RED + "You have no authority to kick someone of equal or higher rank of you.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + "You do not have permission to kick a player from your clan.");
                break;
        }
    }

    private void clanInvite(final Player playerSender, final UUID playerUUID, String targetPlayerName) {
        final Clan senderClan = Clan.getClan(playerUUID);
        final Player targetPlayer = Bukkit.getPlayer(targetPlayerName);


        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        } else if (targetPlayer == null) {
            playerSender.sendMessage(ChatColor.RED + "The player with that name was not able to be found.");
            return;
        } else if (!targetPlayer.isOnline()) {
            playerSender.sendMessage(ChatColor.RED + "The player you are trying to invite is not online.");
            return;
        }

        //At this point, the player exists AND the sender is in a clan

        final UUID targetPlayerUUID = targetPlayer.getUniqueId();

        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         *
         * Error codes found on the definition of clan#demote
         */

        switch (senderClan.invite(playerUUID, targetPlayerUUID)) {
            case 0:
                if (!invitationConfirmation.containsKey(targetPlayerUUID))
                    invitationConfirmation.put(targetPlayerUUID, new ArrayList<Clan>());
                invitationConfirmation.get(targetPlayerUUID).add(senderClan);
                senderClan.broadcast(playerSender.getDisplayName() + " has invited " + targetPlayer.getDisplayName() + " to your clan. The player has 60 seconds to confirm.", new UUID[]{playerUUID});
                playerSender.sendMessage(ChatColor.GREEN + "You have invited " + targetPlayer.getDisplayName() + " to your clan. The player has 60 seconds to confirm.");
                targetPlayer.sendMessage(ChatColor.GREEN + playerSender.getDisplayName() + " has invited you to join " + senderClan.getName() + ". You have 60 seconds to accept the invitation.");

                Bukkit.getScheduler().scheduleSyncDelayedTask(LostShardK.plugin, new Runnable() {
                    public void run() {
                        if (!invitationConfirmation.containsKey(targetPlayerUUID)) //doesnt even exist in the map
                            return;

                        if (!invitationConfirmation.get(targetPlayerUUID).contains(senderClan)) //If he was removed from the confirmation list, don't do anything.)
                            return;

                        //Else, the time expired.
                        senderClan.broadcast(targetPlayer.getDisplayName() + "'s time to join your clan has expired.", new UUID[]{playerUUID});
                        if (targetPlayer.isOnline())
                            targetPlayer.sendMessage("Your invitation to " + senderClan.getName() + " has expired.");
                        invitationConfirmation.get(targetPlayerUUID).remove(senderClan);
                        if (invitationConfirmation.get(targetPlayerUUID).size() == 0)
                            invitationConfirmation.remove(targetPlayerUUID);
                    }
                }, 1200L); // 1200L (ticks) is equal to 60 seconds (20 ticks = 1 second)
                break;
            case 1:
                //Unlikely scenario.
                playerSender.sendMessage(ChatColor.RED + "You are not in the clan.");
                break;
            case 2:
                playerSender.sendMessage(ChatColor.RED + "The player is already in another clan.");
                break;
            case 3:
                playerSender.sendMessage(ChatColor.RED + "The player is already in your clan.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + "You do not have permission to invite a player.");
                break;
            case 5:
                playerSender.sendMessage(ChatColor.RED + "Your clan has reached max capacity. You can not invite any more players.");
                break;
            case 20:
                playerSender.sendMessage(ChatColor.GREEN + "You canceled the invitation for" + targetPlayer.getDisplayName() + " to join your clan.");
                if (targetPlayer.isOnline())
                    targetPlayer.sendMessage(ChatColor.GREEN + senderClan.getName() + " has canceled their invitation for you to join the clan.");
                invitationConfirmation.get(targetPlayerUUID).remove(senderClan);
                if (invitationConfirmation.get(targetPlayerUUID).size() == 0)
                    invitationConfirmation.remove(targetPlayerUUID);
                break;

        }
    }

    private void clanDeny(Player playerSender, UUID playerUUID) {

        if (!invitationConfirmation.containsKey(playerUUID)) {
            playerSender.sendMessage(ChatColor.RED + "No clan has invited you.");
            return;
        }

        ArrayList<Clan> invitedClans = invitationConfirmation.get(playerUUID);
        Clan potentialClan = invitedClans.get(invitedClans.size() - 1);

        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         *
         * Error codes found on the definition of clan#demote
         */


        switch (potentialClan.denyInvitation(playerUUID)) {
            case 0:
                invitationConfirmation.get(playerUUID).remove(potentialClan);
                if (invitationConfirmation.get(playerUUID).size() == 0)
                    invitationConfirmation.remove(playerUUID);
                potentialClan.broadcast(playerSender.getDisplayName() + " has refused to join your clan.", new UUID[]{playerUUID});
                playerSender.sendMessage(" You have refused to join " + potentialClan.getName() + ".");
            case 4:
                playerSender.sendMessage(ChatColor.RED + potentialClan.getName() + " has not invited you to join their clan.");
                break;
        }
    }

    private void clanAccept(Player playerSender, UUID playerUUID) {

        if (!invitationConfirmation.containsKey(playerUUID)) {
            playerSender.sendMessage(ChatColor.RED + "No clan has invited you.");
            return;
        }

        ArrayList<Clan> invitedClans = invitationConfirmation.get(playerUUID);
        Clan potentialClan = invitedClans.get(invitedClans.size() - 1);

        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         *
         * Error codes found on the definition of clan#demote
         */


        switch (potentialClan.acceptInvitation(playerUUID)) {
            case 0:
                invitationConfirmation.get(playerUUID).remove(potentialClan);
                if (invitationConfirmation.get(playerUUID).size() == 0)
                    invitationConfirmation.remove(playerUUID);
                potentialClan.broadcast(playerSender.getDisplayName() + " has joined your clan.", new UUID[]{playerUUID});
                playerSender.sendMessage(" You have joined " + potentialClan.getName() + ".");
            case 2:
                playerSender.sendMessage(ChatColor.RED + "You are already in a clan.");
                break;
            case 3:
                playerSender.sendMessage(ChatColor.RED + "You are already in this clan.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + potentialClan.getName() + " has not invited you to join their clan.");
                break;
            case 5:
                playerSender.sendMessage(ChatColor.RED + potentialClan.getName() + " has reached max capacity.");
                break;
        }
    }

    private void clanDeny(Player playerSender, UUID playerUUID, String clanName) {

        Clan potentialClan = Clan.getClan(clanName);
        if (potentialClan == null) {
            playerSender.sendMessage(ChatColor.RED + clanName + " does not exist.");
            return;
        }


        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         *
         * Error codes found on the definition of clan#demote
         */
        switch (potentialClan.denyInvitation(playerUUID)) {
            case 0:
                invitationConfirmation.get(playerUUID).remove(potentialClan);
                if (invitationConfirmation.get(playerUUID).size() == 0)
                    invitationConfirmation.remove(playerUUID);
                potentialClan.broadcast(playerSender.getDisplayName() + " has refused to join your clan.", new UUID[]{playerUUID});
                playerSender.sendMessage(" You have refused to join " + potentialClan.getName() + ".");
            case 4:
                playerSender.sendMessage(ChatColor.RED + potentialClan.getName() + " has not invited you to join their clan.");
                break;
        }
    }

    private void clanAccept(Player playerSender, UUID playerUUID, String clanName) {

        Clan potentialClan = Clan.getClan(clanName);
        if (potentialClan == null) {
            playerSender.sendMessage(ChatColor.RED + clanName + " does not exist.");
            return;
        }


        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         *
         * Error codes found on the definition of clan#demote
         */
        switch (potentialClan.acceptInvitation(playerUUID)) {
            case 0:
                invitationConfirmation.get(playerUUID).remove(potentialClan);
                if (invitationConfirmation.get(playerUUID).size() == 0)
                    invitationConfirmation.remove(playerUUID);
                potentialClan.broadcast(playerSender.getDisplayName() + " has joined your clan.", new UUID[]{playerUUID});
                playerSender.sendMessage(" You have joined " + potentialClan.getName() + ".");
            case 2:
                playerSender.sendMessage(ChatColor.RED + "You are already in a clan.");
                break;
            case 3:
                playerSender.sendMessage(ChatColor.RED + "You are already in this clan.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + potentialClan.getName() + " has not invited you to join their clan.");
                break;
            case 5:
                playerSender.sendMessage(ChatColor.RED + potentialClan.getName() + " has reached max capacity.");
                break;
        }
    }

    private void clanLeader(final Player playerSender, final UUID playerUUID, String targetPlayerName) {
        Clan senderClan = Clan.getClan(playerUUID);
        Player newLeaderPlayer = Bukkit.getPlayer(targetPlayerName);


        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        } else if (newLeaderPlayer == null) {
            playerSender.sendMessage(ChatColor.RED + "The player with that name was not able to be found.");
            return;
        }

        //At this point, the player exists AND the sender is in a clan

        UUID newLeaderUUID = newLeaderPlayer.getUniqueId();
        ClanRank retiredRank = ClanRank.values()[ClanRank.values().length - 2];
        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         *
         * Error codes found on the definition of clan#demote
         */
        switch (senderClan.setLeader(newLeaderUUID)) {
            case 0:
                leaderConfirmation.remove(playerUUID);
                senderClan.broadcast(ChatColor.GREEN + newLeaderPlayer.getDisplayName() + " has been assigned " + ClanRank.LEADER + ".", new UUID[]{newLeaderUUID});
                newLeaderPlayer.sendMessage(ChatColor.GREEN + "You have been assigned " + ClanRank.LEADER + ".");
                playerSender.sendMessage(ChatColor.GREEN + "You have stepped down to " + retiredRank + ".");
                break;
            case 1:
                playerSender.sendMessage(ChatColor.RED + "The player is not in the same clan as you.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + "You do not have permission to assign a new leader.");
                break;
            case 20:
                //Ask for confirmation
                leaderConfirmation.add(playerUUID);
                playerSender.sendMessage(ChatColor.GREEN + "Are you sure you want to disband the clan? Type /clan disband in chat again to disband your clan. You have 60 seconds to confirm.");

                Bukkit.getScheduler().scheduleSyncDelayedTask(LostShardK.plugin, new Runnable() {
                    public void run() {
                        if (!leaderConfirmation.contains(playerUUID)) //If he was removed from the confirmation list, don't do anything.
                            return;

                        //Else, the time expired.
                        playerSender.sendMessage("Expired time to assign new leadership of your clan.");
                        leaderConfirmation.remove(playerUUID);
                    }
                }, 1200L); // 1200L (ticks) is equal to 60 seconds (20 ticks = 1 second)
                break;
        }
    }

    /**
     * Demotes
     *
     * @param playerSender
     * @param playerUUID
     * @param targetPlayerName
     */
    private void clanDemote(Player playerSender, UUID playerUUID, String targetPlayerName) {
        Clan senderClan = Clan.getClan(playerUUID);
        Player playerDemoted = Bukkit.getPlayer(targetPlayerName);


        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        } else if (playerDemoted == null) {
            playerSender.sendMessage(ChatColor.RED + "The player with that name was not able to be found.");
            return;
        }

        //At this point, the player exists AND the sender is in a clan

        UUID demotedUUID = playerDemoted.getUniqueId();

        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         *
         * Error codes found on the definition of clan#demote
         */
        switch (senderClan.demote(playerUUID, demotedUUID)) {
            case 0:
                ClanRank newRank = senderClan.getClanRank(demotedUUID);
                senderClan.broadcast(ChatColor.GREEN + playerDemoted.getDisplayName() + " has been demoted to " + newRank + ".", new UUID[]{demotedUUID});
                playerDemoted.sendMessage(ChatColor.GREEN + "You have been demoted to " + newRank + ".");
                break;
            case 1:
                playerSender.sendMessage(ChatColor.RED + "The player is not in the same clan as you.");
                break;
            case 2:
            case 3:
                playerSender.sendMessage(ChatColor.RED + "That player can not be demoted any further.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + "You do not have permission to manage ranks.");
                break;

        }
    }

    /**
     * Demotes
     *
     * @param playerSender
     * @param playerUUID
     * @param targetPlayerName
     */
    private void clanDemote(Player playerSender, UUID playerUUID, String targetPlayerName, String rankName) {
        Clan senderClan = Clan.getClan(playerUUID);
        Player playerDemoted = Bukkit.getPlayer(targetPlayerName);

        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        } else if (playerDemoted == null) {
            playerSender.sendMessage(ChatColor.RED + "The player with that name was not able to be found.");
            return;
        }

        boolean rankExists = false;
        ClanRank givenRank = null;
        for (ClanRank iteratingRank : ClanRank.values()) {
            if (rankName.toUpperCase().equals(iteratingRank.toString())) {
                rankExists = true;
                givenRank = iteratingRank;
                break;
            }
        }

        if (!rankExists) {
            playerSender.sendMessage(ChatColor.RED + "The rank you provided does not exist.");
            return;
        }

        //At this point, the player exists AND the sender is in a clan AND rank exists

        UUID demotedUUID = playerDemoted.getUniqueId();

        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         *
         * Error codes found on the definition of clan#demote
         */
        switch (senderClan.demote(playerUUID, demotedUUID, givenRank)) {
            case 0:
                ClanRank newRank = senderClan.getClanRank(demotedUUID);
                senderClan.broadcast(ChatColor.GREEN + playerDemoted.getDisplayName() + " has been demoted to " + newRank + ".", new UUID[]{demotedUUID});
                playerDemoted.sendMessage(ChatColor.GREEN + "You have been demoted to " + newRank + ".");
                break;
            case 1:
                playerSender.sendMessage(ChatColor.RED + "The player is not in the same clan as you.");
                break;
            case 2:
            case 3:
                playerSender.sendMessage(ChatColor.RED + "That player can not be demoted any further.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + "You do not have permission to manage ranks.");
                break;
            case 5:
                playerSender.sendMessage(ChatColor.RED + "You can not demote a player to an equal or higher status.");

        }
    }

    private void clanPromote(Player playerSender, UUID playerUUID, String targetPlayerName) {

        Clan senderClan = Clan.getClan(playerUUID);
        Player playerPromoted = Bukkit.getPlayer(targetPlayerName);


        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        } else if (playerPromoted == null) {
            playerSender.sendMessage(ChatColor.RED + "The player with that name was not able to be found.");
            return;
        }

        //At this point, the player exists AND the sender is in a clan

        UUID promotedUUID = playerPromoted.getUniqueId();

        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         */
        switch (senderClan.promote(playerUUID, promotedUUID)) {
            case 0:
                ClanRank newRank = senderClan.getClanRank(promotedUUID);
                senderClan.broadcast(ChatColor.GREEN + playerPromoted.getDisplayName() + " has been promoted to " + newRank + ".", new UUID[]{promotedUUID});
                playerPromoted.sendMessage(ChatColor.GREEN + "You have been promoted to " + newRank + ".");
                break;
            case 1:
                playerSender.sendMessage(ChatColor.RED + "The player is not in the same clan as you.");
                break;
            case 2:
            case 3:
                playerSender.sendMessage(ChatColor.RED + "That player can not be promoted any further.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + "You do not have permission to manage ranks.");
                break;

        }
    }

    private void clanPromote(Player playerSender, UUID playerUUID, String targetPlayerName, String rankName) {

        Clan senderClan = Clan.getClan(playerUUID);
        Player playerPromoted = Bukkit.getPlayer(targetPlayerName);

        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        } else if (playerPromoted == null) {
            playerSender.sendMessage(ChatColor.RED + "The player with that name was not able to be found.");
            return;
        }

        boolean rankExists = false;
        ClanRank givenRank = null;
        for (ClanRank iteratingRank : ClanRank.values()) {
            if (rankName.toUpperCase().equals(iteratingRank.toString())) {
                rankExists = true;
                givenRank = iteratingRank;
                break;
            }
        }

        if (!rankExists) {
            playerSender.sendMessage(ChatColor.RED + "The rank you provided does not exist.");
            return;
        }

        //At this point, the player exists AND the sender is in a clan AND rank exists

        UUID promotedUUID = playerPromoted.getUniqueId();

        /**
         * Let the clan take care of restrictions or else duplicated code everywhere.
         */
        switch (senderClan.promote(playerUUID, promotedUUID, givenRank)) {
            case 0:
                ClanRank newRank = senderClan.getClanRank(promotedUUID);
                senderClan.broadcast(ChatColor.GREEN + playerPromoted.getDisplayName() + " has been promoted to " + newRank + ".", new UUID[]{promotedUUID});
                playerPromoted.sendMessage(ChatColor.GREEN + "You have been promoted to " + newRank + ".");
                break;
            case 1:
                playerSender.sendMessage(ChatColor.RED + "The player is not in the same clan as you.");
                break;
            case 2:
            case 3:
                playerSender.sendMessage(ChatColor.RED + "That player can not be promoted any further.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + "You do not have permission to manage ranks.");
                break;
            case 5:
                playerSender.sendMessage(ChatColor.RED + "You can not promote a player to an equal or lower status.");
                break;

        }
    }

    private void editName(Player playerSender, UUID playerUUID, String clanName) {

        Clan potentialClan = Clan.getClan(playerUUID);
        if (potentialClan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        }
        switch (potentialClan.setName(playerUUID, clanName)) {
            case 0:
                //todo take gold here
                potentialClan.broadcast("Clan name has been changed to" + ChatColor.YELLOW + clanName + ".");
                break;
            case 1:
                playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + "You do not have permission to edit the clan name.");
                break;
            case 10:
                playerSender.sendMessage(ChatColor.RED + "Your clan name can not be less than 3 characters.");
                break;
            case 11:
                playerSender.sendMessage(ChatColor.RED + "Your clan name can not be longer than 20 characters.");
                break;
            case 12:
                playerSender.sendMessage(ChatColor.RED + "Your clan name can not have special characters or whitespace characters.");
                break;
            case 79:
                playerSender.sendMessage(ChatColor.RED + "You need 20 gold to rename your clan. You currently have (value).");
                break;
        }


    }

    private void editTag(Player playerSender, UUID playerUUID, String tag) {

        Clan potentialClan = Clan.getClan(playerUUID);
        if (potentialClan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        }
        switch (potentialClan.setTag(playerUUID, tag)) {
            case 0:
                //todo take gold here
                potentialClan.broadcast(ChatColor.GREEN + "Clan tag has been set to “" + ChatColor.YELLOW + tag + ChatColor.GREEN + "”.");
                break;
            case 1:
                playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + "You do not have permission to edit the clan tag.");
                break;
            case 10:
                playerSender.sendMessage(ChatColor.RED + "Your clan tag can not be longer or shorter than 3 characters.");
                break;
            case 12:
                playerSender.sendMessage(ChatColor.RED + "Your clan tag can not have special characters.");
                break;
            case 79:
                playerSender.sendMessage(ChatColor.RED + "You need 5 gold to rename your clan's tag. You currently have (value).");
                break;

        }
    }

    private void createClan(Player playerSender, UUID playerUUID, String clanName) {

        Clan clan = new Clan(clanName, playerUUID);

        switch (clan.create(playerUUID, clanName)) {
            case 0:
                //todo take gold here

                clanTagCreators.put(playerUUID, clan);
                playerSender.sendMessage(ChatColor.GREEN + "Your clan has been created."); //todo gold takeaway this statement
                playerSender.sendMessage(ChatColor.GREEN + "What would you like your clan tag to be? It must be 3 characters long.");
                //From here the player will chat the responses and the HashMap will take care of the clan customization.
                break;
            case 1:
                playerSender.sendMessage(ChatColor.RED + "You are already in clan.");
                break;
            case 10:
                playerSender.sendMessage(ChatColor.RED + "Your clan name can not be less than 3 characters.");
                break;
            case 11:
                playerSender.sendMessage(ChatColor.RED + "Your clan name can not be longer than 20 characters.");
                break;
            case 12:
                playerSender.sendMessage(ChatColor.RED + "Your clan name can not have special characters or whitespace characters.");
                break;
            case 79:
                playerSender.sendMessage(ChatColor.RED + "You need 100 gold to create a clan. You currently have (value).");
                break;
        }
    }

    private String stringBuilder(String[] args) {

        String string = "";
        for (int i = 1; i < args.length; i++) {
            if (i == 1)
                string += args[i];
            else
                string += " " + args[i];
        }
        return string;
    }

    private void sendHelp() {
    }

    public void disbandClan(final Player playerSender, final UUID playerUUID) {

        Clan clan = Clan.getClan(playerUUID);

        //The clan does not exist
        if (clan == null) {
            playerSender.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
            //The rank of the player is not equal to leader.
        }

        switch (clan.disband(playerUUID)) {
            case 0:
                //If the player has already typed this command before. Confirms disbanding
                clanDisbandTimer.remove(playerUUID);
                //Message
                //Loop every clan user uuid
                for (UUID uuid : clan.getAllClanUsers()) {
                    //Get the player of the user
                    Player player = Bukkit.getPlayer(uuid);
                    //If this player is online
                    if (player.isOnline()) {
                        //If the player is not the leader
                        if (!clan.getClanRanksBy(ClanRank.LEADER)[0].equals(playerUUID)) {
                            player.sendMessage(ChatColor.GREEN + "You have been kicked from the clan.");
                        }
                        //All online players get this message
                        player.sendMessage(ChatColor.GREEN + "Your has clan has been disbanded.");
                    }
                }
                break;
            case 4:
                playerSender.sendMessage(ChatColor.RED + "Only the leader can disband the clan.");
                break;
            case 20:
                //Ask for confirmation
                clanDisbandTimer.add(playerUUID);
                playerSender.sendMessage(ChatColor.GREEN + "Are you sure you want to disband the clan? Type /clan disband in chat again to disband your clan. You have 60 seconds to confirm.");

                Bukkit.getScheduler().scheduleSyncDelayedTask(LostShardK.plugin, new Runnable() {
                    public void run() {
                        if (!clanDisbandTimer.contains(playerUUID)) //If he was removed from the confirmation list, don't do anything.
                            return;

                        //Else, the time expired.
                        playerSender.sendMessage("Expired time to disband clan.");
                        clanDisbandTimer.remove(playerUUID);
                    }
                }, 1200L); // 1200L (ticks) is equal to 60 seconds (20 ticks = 1 second)
                break;
        }
    }

}
