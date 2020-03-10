package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.clans.ClanRank;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

import static com.github.kotooriiii.util.HelperMethods.*;


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
                    Clan clan = Clan.getClan(playerUUID);
                    if (clan == null)
                        sendHelp(playerSender);
                    else
                        playerSender.sendMessage(clan.info());
                    return true;
                }
                //This statement refers to: /clans <argument 0>
                else if (args.length == 1) {
                    //Sub-commands
                    switch (args[0].toLowerCase()) {
                        case "create":
                            playerSender.sendMessage(ERROR_COLOR + "You must have a name for your clan: " + COMMAND_COLOR + "/clan create (name)" + ERROR_COLOR + ".");
                            break;
                        case "disband":
                            disbandClan(playerSender, playerUUID);
                            break;
                        case "tag":
                            playerSender.sendMessage(ERROR_COLOR + "You must have a tag for your clan: " + COMMAND_COLOR + " /clan tag (name)" + ERROR_COLOR + ".");
                            break;
                        case "rename":
                            playerSender.sendMessage(ERROR_COLOR + "You must have a name for your clan: " + COMMAND_COLOR + "/clan rename (name)" + ERROR_COLOR + ".");
                            break;
                        case "invite":
                            playerSender.sendMessage(ERROR_COLOR + "To invite a player to your clan: " + COMMAND_COLOR + "/clan invite (username)" + ERROR_COLOR + ".");
                            break;
                        case "kick":
                            playerSender.sendMessage(ERROR_COLOR + "To kick a player from your clan: " + COMMAND_COLOR + "/clan kick (username)" + ERROR_COLOR + ".");
                            break;
                        case "leave":
                            clanLeave(playerSender, playerUUID);
                            break;
                        case "leader":
                            playerSender.sendMessage(ERROR_COLOR + "To assign leadership to another player: " + COMMAND_COLOR + "/clan leader (username)" + ERROR_COLOR + ".");
                            break;
                        case "promote":
                            playerSender.sendMessage(ERROR_COLOR + "To promote a member to co-leader: " + COMMAND_COLOR + "/clan promote (username) [opt: rank]" + ERROR_COLOR + ".");
                            break;
                        case "demote":
                            playerSender.sendMessage(ERROR_COLOR + "To demote a co-leader to member: " + COMMAND_COLOR + "/clan demote (username) [opt: rank]" + ERROR_COLOR + ".");
                            break;
                        case "chat": //TODO LATER
                            break;
                        case "accept":
                            clanAccept(playerSender, playerUUID);
                            break;
                        case "deny":
                            clanDeny(playerSender, playerUUID);
                            break;
                        case "info":
                            clanInfo(playerSender, playerUUID);
                            break;
                        case "who":
                            playerSender.sendMessage(ERROR_COLOR + "To find information about another player's clan: " + COMMAND_COLOR + "/clan who (username)" + ERROR_COLOR + ".");
                            break;
                        case "help":
                            sendHelp(playerSender);
                            break;
                        case "staff":
                            if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                                playerSender.sendMessage(ERROR_COLOR + "You do not have access to staff commands.");
                                return true;
                            }
                            sendStaffHelp(playerSender);
                            break;
                        default: //Not a premade command. This means something like: /clans chocolatebunnies
                            sendUnknownCommand(playerSender);
                            break;
                    }

                }
                //This statement refers to: /clans <argument 0> <argument 1> ...
                else if (args.length > 1) {
                    //Sub-commands again however with proper argument.
                    String supply = stringBuilder(args, 1, " ");
                    switch (args[0].toLowerCase()) {
                        case "create":
                            createClan(playerSender, playerUUID, supply);
                            break;
                        case "disband":
                            playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/clan disband" + ERROR_COLOR + ".");
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
                                playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/clan invite (username)" + ERROR_COLOR + ".");
                            break;
                        case "kick":
                            if (args.length == 2)
                                clanKick(playerSender, playerUUID, args[1]);
                            else
                                playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/clan kick (username)" + ERROR_COLOR + ".");
                            break;
                        case "leave":
                            playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/clan leave" + ERROR_COLOR + ".");
                            break;
                        case "leader":
                            if (args.length == 2)
                                clanLeader(playerSender, playerUUID, args[1]);
                            else
                                playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/clan leader (username)" + ERROR_COLOR + ".");
                            break;
                        case "promote":
                            if (args.length == 2)
                                clanPromote(playerSender, playerUUID, args[1]);
                            else if (args.length == 3)
                                clanPromote(playerSender, playerUUID, args[1], args[2]);
                            else
                                playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/clan promote (username) [opt: rank]" + ERROR_COLOR + ".");

                            break;
                        case "demote":
                            if (args.length == 2)
                                clanDemote(playerSender, playerUUID, args[1]);
                            else if (args.length == 3)
                                clanDemote(playerSender, playerUUID, args[1], args[2]);
                            else
                                playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/clan demote (username) [opt: rank]" + ERROR_COLOR + ".");
                            break;
                        case "chat": //TODO LATER
                            break;
                        case "accept":
                            clanAccept(playerSender, playerUUID, supply);
                            break;
                        case "deny": //implementation later
                            clanDeny(playerSender, playerUUID, supply);
                            break;
                        case "info":
                            clanInfo(playerSender, playerUUID);
                            break;
                        case "who":
                            if (args.length == 2)
                                clanWho(playerSender, args[1]);
                            else
                                playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/clan who (username)" + ERROR_COLOR + ".");
                            break;
                        case "help":
                            sendHelp(playerSender);
                            break;
                        case "staff":

                            if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                                playerSender.sendMessage(ERROR_COLOR + "You do not have access to staff commands.");
                                return true;
                            }

                            if (args.length <= 2) {
                                sendStaffHelp(playerSender);
                            } else {
                                switch (args[1].toLowerCase()) {
                                    case "puuid":
                                        if (args.length == 2)
                                            playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/clan staff puuid <playerName>" + ERROR_COLOR + "."); //clan staff uuid
                                        else if (args.length == 3)
                                            sendPlayerUUID(playerSender, args[2]);
                                            //clan staff uuid name
                                        else
                                            playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/clan staff puuid <playerName>" + ERROR_COLOR + ".");
                                        break;
                                    case "cuuid":
                                        if (args.length == 2)
                                            playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/clan staff cuuid <clanName>" + ERROR_COLOR + "."); //clan staff uuid
                                        else if (args.length > 2)
                                            sendClanUUID(playerSender, stringBuilder(args, 2, " "));
                                        break;
                                    case "disband":
                                        if (args.length == 2)
                                            playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/clan staff disband <clanName>" + ERROR_COLOR + "."); //clan staff uuid
                                            //clan staff disbadn sss
                                        else if (args.length > 2)
                                            forceDisband(playerSender, stringBuilder(args, 2, " "));

                                        break;
                                    case "leader":
                                        if (args.length < 4) //clan staff leader <playerName> <clan>
                                            playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/clan staff leader <playerName> <clanName>" + ERROR_COLOR + "."); //clan staff uuid
                                        else {
                                            forceLeader(playerSender, args[2], stringBuilder(args, 3, " "));
                                        }
                                        break;
                                    case "kick": //clan staff kick <playerName>
                                        if (args.length == 2)
                                            playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/clan staff kick <playerName>" + ERROR_COLOR + "."); //clan staff uuid
                                        else if (args.length == 3)
                                            forceClanKick(playerSender, args[2]);
                                        else
                                            playerSender.sendMessage(ERROR_COLOR + "You provided too many arguments: " + COMMAND_COLOR + "/clan staff kick <playerName>" + ERROR_COLOR + ".");
                                        break;
                                    default:
                                        sendStaffUnknownCommand(playerSender);
                                        break;
                                }
                            }
                            break;
                        default: //Not a premade command. This means something like: /clans chocolatebunnies white
                            sendUnknownCommand(playerSender);
                            break;
                    }

                }


            }

        }
        return false;
    }//end of commands
    private void sendStaffUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Clan's Staff. Use " + "/clan staff" + ERROR_COLOR + " for help.");
    }
    private void forceLeader(Player playerSender, String playerName, String clanName) {
        if (clanName == null) {
            playerSender.sendMessage(ERROR_COLOR + "The clan " + ERROR_COLOR + "\"" + clanName + "\"" + ERROR_COLOR + " was not able to be found.");
            return;
        }

        Clan clan = null;
        for (Clan iclan : clans) {
            if (iclan.getName().toLowerCase().equals(clanName.toLowerCase())) {
                clan = iclan;
            }
        }

        if (clan == null) {
            playerSender.sendMessage(ERROR_COLOR + "The clan " + ERROR_COLOR + "\"" + clanName + "\"" + ERROR_COLOR + " was not able to be found.");
            return;
        }

        if (playerName == null) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);
        if (!targetPlayer.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
            return;
        }

        switch (clan.forceLeader(targetPlayer.getUniqueId())) {

            case 0:
                clan.broadcast(PLAYER_COLOR + targetPlayer.getName() + STANDARD_COLOR + " has forcibly been assigned the new " + STANDARD_COLOR + ClanRank.LEADER + STANDARD_COLOR + " of the clan.");
                playerSender.sendMessage(STANDARD_COLOR + "You have forcibly assigned a new leader to " + STANDARD_COLOR + "\"" +  clan.getName() + "\""  + STANDARD_COLOR + ".");
                if (targetPlayer.isOnline())
                    ((Player) targetPlayer).sendMessage(STANDARD_COLOR + "You have been forcibly assigned " + STANDARD_COLOR + ClanRank.LEADER + STANDARD_COLOR + ".");
                break;
            case 1:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "The player is not in the clan.");
                break;
            case 6:
                playerSender.sendMessage(ERROR_COLOR + "This player is already a leader.");
                break;
        }
    }

    private void forceDisband(Player playerSender, String clanName) {
        if (clanName == null) {
            playerSender.sendMessage(ERROR_COLOR + "The clan " + ERROR_COLOR + "\"" + clanName + "\"" + ERROR_COLOR + " was not able to be found.");
            return;
        }

        Clan clan = null;
        for (Clan iclan : clans) {
            if (iclan.getName().toLowerCase().equals(clanName.toLowerCase())) {
                clan = iclan;
            }
        }

        if (clan == null) {
            playerSender.sendMessage(ERROR_COLOR + "The clan " + ERROR_COLOR + "\"" + clanName + "\"" + ERROR_COLOR + " was not able to be found.");
            return;
        }

        clan.broadcast(STANDARD_COLOR + "You have been forcibly kicked from the clan.");
        clan.broadcast(STANDARD_COLOR + "Your clan has been forcibly disbanded.");
        playerSender.sendMessage(STANDARD_COLOR + "You have forcibly disbanded " + STANDARD_COLOR + "\"" + clan.getName() + "\"" + STANDARD_COLOR + ".");
        clan.forceDisband();
        return;
    }

    private void sendClanUUID(Player playerSender, String clanName) {
        if (clanName == null) {
            playerSender.sendMessage(ERROR_COLOR + "The clan " + ERROR_COLOR + "\"" + clanName + "\"" + ERROR_COLOR + " was not able to be found.");
            return;
        }

        Clan clan = null;
        for (Clan iclan : clans) {
            if (iclan.getName().toLowerCase().equals(clanName.toLowerCase())) {
                clan = iclan;
            }
        }

        if (clan == null) {
            playerSender.sendMessage(ERROR_COLOR + "The clan " + ERROR_COLOR + "\"" + clanName + "\"" + ERROR_COLOR + " was not able to be found.");
            return;
        }

        playerSender.spigot().sendMessage(new ComponentBuilder(STANDARD_COLOR + "The clan " + STANDARD_COLOR + "\"" + clan.getName() + "\"" + STANDARD_COLOR + "'s UUID is " + clan.getID().toString() + ". Hover and click to copy.")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "If you click on this message, the UUID of the clan will be on your text box.\nYou can copy this text and edit the clan files if you so need it to manipulate players and more.").create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, clan.getID().toString()))
                .create());
        return;
    }

    private void sendPlayerUUID(Player playerSender, String playerName) {
        if (playerName == null) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);
        if (!targetPlayer.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
            return;
        }
        //chatcomponent hover click


        playerSender.spigot().sendMessage(new ComponentBuilder(STANDARD_COLOR + "The player " + PLAYER_COLOR + "" + targetPlayer.getName() + "" + STANDARD_COLOR + "'s UUID is " + targetPlayer.getUniqueId().toString() + ". Hover and click to copy.")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "If you click on this message, the UUID of the clan will be on your text box.\nYou can copy this text and edit the clan files if you so need it to manipulate players and more.").create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, targetPlayer.getUniqueId().toString()))
                .create());
        return;
    }

    private void sendUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in clans. Use " + "/clan" + ERROR_COLOR + " for help.");
    }

    private void clanWho(final Player playerSender, String targetPlayerName) {
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

        if (!targetPlayer.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
            return;

        }

        final UUID targetUUID = targetPlayer.getUniqueId();
        final Clan targetClan = Clan.getClan(targetUUID);
        if (targetClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "That player is not in a clan.");
            return;
        }

        playerSender.sendMessage(targetClan.info());
    }

    private void clanInfo(final Player playerSender, final UUID playerUUID) {
        final Clan senderClan = Clan.getClan(playerUUID);
        if (senderClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        }

        playerSender.sendMessage(senderClan.info());
    }

    private void clanLeave(final Player playerSender, final UUID playerUUID) {

        final Clan senderClan = Clan.getClan(playerUUID);
        if (senderClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        }

        switch (senderClan.leave(playerUUID)) {
            case 0:
                senderClan.broadcast(PLAYER_COLOR + playerSender.getName() + STANDARD_COLOR + " has left the clan.", new UUID[]{playerUUID});
                playerSender.sendMessage(STANDARD_COLOR + "You have left the clan.");
                break;
            case 30:
            case 1:
                //rare to happen, must be error since already covered when you GOT player clan
                playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
                break;
            case 3:
                playerSender.sendMessage(ERROR_COLOR + "You can not leave before promoting someone else to " + ERROR_COLOR + ClanRank.LEADER + ERROR_COLOR + ".");
                break;

        }
    }

    private void forceClanKick(final Player playerSender, String targetPlayerName) {
        final OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (!targetPlayer.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
            return;
        }
        final UUID targetPlayerUUID = targetPlayer.getUniqueId();

        Clan clan = Clan.getClan(targetPlayerUUID);
        if (clan == null) {
            playerSender.sendMessage(ERROR_COLOR + "The player is not in a clan.");
            return;
        }

        //At this point, the player exists AND the sender is in a clan


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
        switch (clan.forceKick(targetPlayerUUID)) {
            case 0:
                clan.broadcast(PLAYER_COLOR + playerSender.getName() + STANDARD_COLOR + " has been forcibly kicked " + PLAYER_COLOR + targetPlayer.getName() + STANDARD_COLOR + " from a clan.", new UUID[]{targetPlayerUUID});
                playerSender.sendMessage(STANDARD_COLOR + "You have forcibly kicked " + PLAYER_COLOR + targetPlayer.getName() + STANDARD_COLOR + " from a clan.");
                if (targetPlayer.isOnline())
                    ((Player) targetPlayer).sendMessage(STANDARD_COLOR + "You have been forcibly kicked from the clan.");
                break;

            case 2:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "That player is not in your clan.");
                break;
            case 3:
                playerSender.sendMessage(ERROR_COLOR + "You cannot kick the leader without assigning new leadership.");
                break;
        }
    }

    private void clanKick(final Player playerSender, final UUID playerUUID, String targetPlayerName) {
        final Clan senderClan = Clan.getClan(playerUUID);
        final OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        } else if (!targetPlayer.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
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
                senderClan.broadcast(PLAYER_COLOR + playerSender.getName() + STANDARD_COLOR + " has kicked " + PLAYER_COLOR + targetPlayer.getName() + STANDARD_COLOR + " from the clan.", new UUID[]{targetPlayerUUID, playerUUID});
                playerSender.sendMessage(STANDARD_COLOR + "You have kicked " + PLAYER_COLOR + targetPlayer.getName() + STANDARD_COLOR + " from the clan.");
                if (targetPlayer.isOnline())
                    ((Player) targetPlayer).sendMessage(STANDARD_COLOR + "You have been kicked from the clan by " + PLAYER_COLOR + playerSender.getName() + STANDARD_COLOR + ".");
                break;
            case 1:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
                break;
            case 2:
                playerSender.sendMessage(ERROR_COLOR + "That player is not in your clan.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + "You do not have permission to kick a player from your clan.");
                break;
            case 3:
            case 5:
                playerSender.sendMessage(ERROR_COLOR + "You have no authority to kick someone of equal or higher rank of you.");
                break;

        }
    }

    private void clanInvite(final Player playerSender, final UUID playerUUID, String targetPlayerName) {
        final Clan senderClan = Clan.getClan(playerUUID);
        final OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);


        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        } else if (!targetPlayer.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
            return;
        } else if (!targetPlayer.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "The player you are trying to invite is not online.");
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
                senderClan.broadcast(PLAYER_COLOR + playerSender.getName() + STANDARD_COLOR + " has invited " + PLAYER_COLOR + targetPlayer.getName() + STANDARD_COLOR + " to your clan. The player has 60 seconds to confirm.", new UUID[]{playerUUID});
                playerSender.sendMessage(STANDARD_COLOR + "You have invited " + PLAYER_COLOR + targetPlayer.getName() + STANDARD_COLOR + " to your clan. The player has 60 seconds to confirm.");
                if (targetPlayer.isOnline())
                    ((Player) targetPlayer).sendMessage(PLAYER_COLOR + playerSender.getName() + STANDARD_COLOR + " has invited you to join " + STANDARD_COLOR + senderClan.getName() + STANDARD_COLOR + ". You have 60 seconds to accept the invitation. Type " + STANDARD_COLOR + "/clan accept to join" + STANDARD_COLOR + " or " + STANDARD_COLOR + "/clan deny to deny" + STANDARD_COLOR + ".");

                Bukkit.getScheduler().scheduleSyncDelayedTask(LostShardK.plugin, new Runnable() {
                    public void run() {
                        if (!invitationConfirmation.containsKey(targetPlayerUUID)) //doesnt even exist in the map
                            return;

                        if (!invitationConfirmation.get(targetPlayerUUID).contains(senderClan)) //If he was removed from the confirmation list, don't do anything.)
                            return;

                        //Else, the time expired.
                        senderClan.broadcast(PLAYER_COLOR + targetPlayer.getName() + STANDARD_COLOR + "'s time to join your clan has expired.", new UUID[]{playerUUID});
                        if (targetPlayer.isOnline())
                            ((Player) targetPlayer).sendMessage(STANDARD_COLOR + "Your invitation to " + STANDARD_COLOR + senderClan.getName() + STANDARD_COLOR + " has expired.");
                        invitationConfirmation.get(targetPlayerUUID).remove(senderClan);
                        if (invitationConfirmation.get(targetPlayerUUID).size() == 0)
                            invitationConfirmation.remove(targetPlayerUUID);
                    }
                }, 60 * 20L); // 1200L (ticks) is equal to 60 seconds (20 ticks = 1 second)
                break;
            case 1:
            case 30:
                //Unlikely scenario.
                playerSender.sendMessage(ERROR_COLOR + "You are not in the clan.");
                break;
            case 2:
                playerSender.sendMessage(ERROR_COLOR + "The player is already in another clan.");
                break;
            case 3:
                playerSender.sendMessage(ERROR_COLOR + "The player is already in your clan.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + "You do not have permission to invite a player.");
                break;
            case 5:
                playerSender.sendMessage(ERROR_COLOR + "Your clan has reached max capacity. You can not invite any more players.");
                break;
            case 20:
                playerSender.sendMessage(STANDARD_COLOR + "You canceled the invitation for" + PLAYER_COLOR + targetPlayer.getName() + STANDARD_COLOR + " to join your clan.");
                if (targetPlayer.isOnline())
                    ((Player) targetPlayer).sendMessage(STANDARD_COLOR + senderClan.getName() + STANDARD_COLOR + " has canceled your invitation to join the clan.");
                invitationConfirmation.get(targetPlayerUUID).remove(senderClan);
                if (invitationConfirmation.get(targetPlayerUUID).size() == 0)
                    invitationConfirmation.remove(targetPlayerUUID);
                break;

        }
    }

    private void clanDeny(Player playerSender, UUID playerUUID) {

        if (!invitationConfirmation.containsKey(playerUUID)) {
            playerSender.sendMessage(ERROR_COLOR + "No clan has invited you.");
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
                potentialClan.broadcast(PLAYER_COLOR + playerSender.getName() + STANDARD_COLOR + " has refused to join your clan.", new UUID[]{playerUUID});
                playerSender.sendMessage(STANDARD_COLOR + " You have refused to join " + STANDARD_COLOR + potentialClan.getName() + STANDARD_COLOR + ".");
            case 4:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + potentialClan.getName() + ERROR_COLOR + " has not invited you to join their clan.");
                break;
        }
    }

    private void clanAccept(Player playerSender, UUID playerUUID) {

        if (!invitationConfirmation.containsKey(playerUUID)) {
            playerSender.sendMessage(ERROR_COLOR + "No clan has invited you.");
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
                potentialClan.broadcast(PLAYER_COLOR + playerSender.getName() + STANDARD_COLOR + " has joined your clan.", new UUID[]{playerUUID});
                playerSender.sendMessage(STANDARD_COLOR + "You have joined " + STANDARD_COLOR + potentialClan.getName() + STANDARD_COLOR + ".");
                break;
            case 2:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "You are already in a clan.");
                break;
            case 3:
                playerSender.sendMessage(ERROR_COLOR + "You are already in this clan.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + potentialClan.getName() + ERROR_COLOR + " has not invited you to join their clan.");
                break;
            case 5:
                playerSender.sendMessage(ERROR_COLOR + potentialClan.getName() + ERROR_COLOR + " has reached max capacity.");
                break;
        }
    }

    private void clanDeny(Player playerSender, UUID playerUUID, String clanName) {

        Clan potentialClan = Clan.getClan(clanName);
        if (potentialClan == null) {
            playerSender.sendMessage(ERROR_COLOR + clanName + ERROR_COLOR + " does not exist.");
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
                potentialClan.broadcast(PLAYER_COLOR + playerSender.getName() + STANDARD_COLOR + " has refused to join your clan.", new UUID[]{playerUUID});
                playerSender.sendMessage(STANDARD_COLOR + " You have refused to join " + STANDARD_COLOR + potentialClan.getName() + STANDARD_COLOR + ".");
            case 4:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + potentialClan.getName() + ERROR_COLOR + " has not invited you to join their clan.");
                break;
        }
    }

    private void clanAccept(Player playerSender, UUID playerUUID, String clanName) {

        Clan potentialClan = Clan.getClan(clanName);
        if (potentialClan == null) {
            playerSender.sendMessage(STANDARD_COLOR + clanName + STANDARD_COLOR + " does not exist.");
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
                potentialClan.broadcast(PLAYER_COLOR + playerSender.getName() + STANDARD_COLOR + " has joined your clan.", new UUID[]{playerUUID});
                playerSender.sendMessage(STANDARD_COLOR + "You have joined " + STANDARD_COLOR + potentialClan.getName() + STANDARD_COLOR + ".");
                break;
            case 2:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "You are already in a clan.");
                break;
            case 3:
                playerSender.sendMessage(ERROR_COLOR + "You are already in this clan.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + potentialClan.getName() + ERROR_COLOR + " has not invited you to join their clan.");
                break;
            case 5:
                playerSender.sendMessage(ERROR_COLOR + potentialClan.getName() + ERROR_COLOR + " has reached max capacity.");
                break;
        }
    }

    private void clanLeader(final Player playerSender, final UUID playerUUID, String targetPlayerName) {
        Clan senderClan = Clan.getClan(playerUUID);
        OfflinePlayer newLeaderPlayer = Bukkit.getOfflinePlayer(targetPlayerName);


        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        } else if (!newLeaderPlayer.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
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
        switch (senderClan.leader(playerUUID, newLeaderUUID)) {
            case 0:
                leaderConfirmation.remove(playerUUID);
                senderClan.broadcast(PLAYER_COLOR + newLeaderPlayer.getName() + STANDARD_COLOR + " has been assigned " + STANDARD_COLOR + ClanRank.LEADER + STANDARD_COLOR + ".", new UUID[]{newLeaderUUID});
                if (newLeaderPlayer.isOnline())
                    ((Player) newLeaderPlayer).sendMessage(STANDARD_COLOR + "You have been assigned " + RANK_COLOR + ClanRank.LEADER + STANDARD_COLOR + ".");
                playerSender.sendMessage(STANDARD_COLOR + "You have stepped down to " + RANK_COLOR + retiredRank + STANDARD_COLOR + ".");
                break;
            case 1:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "The player is not in the same clan as you.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + "You do not have permission to assign a new leader.");
                break;
            case 6:
                playerSender.sendMessage(ERROR_COLOR + "You can not yourself as leader when you're already leader.");
                break;
            case 20:
                //Ask for confirmation
                leaderConfirmation.add(playerUUID);
                playerSender.sendMessage(STANDARD_COLOR + "Are you sure you want to assign a new leader to the clan? Type /clan leader (username) in chat again to revoke your leadership. You have 60 seconds to confirm.");

                Bukkit.getScheduler().scheduleSyncDelayedTask(LostShardK.plugin, new Runnable() {
                    public void run() {
                        if (!leaderConfirmation.contains(playerUUID)) //If he was removed from the confirmation list, don't do anything.
                            return;

                        //Else, the time expired.
                        if (playerSender.isOnline())
                            playerSender.sendMessage(STANDARD_COLOR + "The time to assign a new leader has expired.");
                        leaderConfirmation.remove(playerUUID);
                    }
                }, 60 * 20L); // 1200L (ticks) is equal to 60 seconds (20 ticks = 1 second)
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
        OfflinePlayer playerDemoted = Bukkit.getOfflinePlayer(targetPlayerName);


        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        } else if (!playerDemoted.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
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
                senderClan.broadcast(PLAYER_COLOR + playerDemoted.getName() + STANDARD_COLOR + " has been demoted to " + RANK_COLOR + newRank + STANDARD_COLOR + ".", new UUID[]{demotedUUID});
                if (playerDemoted.isOnline())
                    ((Player) playerDemoted).sendMessage(STANDARD_COLOR + "You have been demoted to " + RANK_COLOR + newRank + STANDARD_COLOR + ".");
                break;
            case 1:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "The player is not in the same clan as you.");
                break;
            case 2:
            case 3:
                playerSender.sendMessage(ERROR_COLOR + "That player can not be demoted any further.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + "You do not have permission to manage ranks.");
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
        OfflinePlayer playerDemoted = Bukkit.getOfflinePlayer(targetPlayerName);

        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        } else if (!playerDemoted.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
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
            playerSender.sendMessage(ERROR_COLOR + "The rank you provided does not exist.");
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
                senderClan.broadcast(PLAYER_COLOR + playerDemoted.getName() + STANDARD_COLOR + " has been demoted to " + RANK_COLOR + newRank + STANDARD_COLOR + ".", new UUID[]{demotedUUID});
                if (playerDemoted.isOnline())
                    ((Player) playerDemoted).sendMessage(STANDARD_COLOR + "You have been demoted to " + RANK_COLOR + newRank + STANDARD_COLOR + ".");
                break;
            case 1:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "The player is not in the same clan as you.");
                break;
            case 2:
            case 3:
                playerSender.sendMessage(ERROR_COLOR + "That player can not be demoted any further.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + "You do not have permission to manage ranks.");
                break;
            case 5:
                playerSender.sendMessage(ERROR_COLOR + "You can not demote a player to an equal or higher status.");

        }
    }

    private void clanPromote(Player playerSender, UUID playerUUID, String targetPlayerName) {

        Clan senderClan = Clan.getClan(playerUUID);
        OfflinePlayer playerPromoted = Bukkit.getOfflinePlayer(targetPlayerName);


        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        } else if (!playerPromoted.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
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
                senderClan.broadcast(PLAYER_COLOR + playerPromoted.getName() + STANDARD_COLOR + " has been promoted to " + RANK_COLOR + newRank + STANDARD_COLOR + ".", new UUID[]{promotedUUID});
                if (playerPromoted.isOnline())
                    ((Player) playerPromoted).sendMessage(STANDARD_COLOR + "You have been promoted to " + RANK_COLOR + newRank + STANDARD_COLOR + ".");
                break;
            case 30:
            case 1:
                playerSender.sendMessage(ERROR_COLOR + "The player is not in the same clan as you.");
                break;
            case 2:
            case 3:
                playerSender.sendMessage(ERROR_COLOR + "That player can not be promoted any further.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + "You do not have permission to manage ranks.");
                break;

        }
    }

    private void clanPromote(Player playerSender, UUID playerUUID, String targetPlayerName, String rankName) {

        Clan senderClan = Clan.getClan(playerUUID);
        OfflinePlayer playerPromoted = Bukkit.getOfflinePlayer(targetPlayerName);

        /*
        CRUCIAL checks:
        Check if the clan exists or we get a nullptr exception
        Check if the player even exists.
         */
        if (senderClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        } else if (!playerPromoted.hasPlayedBefore()) {
            playerSender.sendMessage(ERROR_COLOR + "The player with that name was not able to be found.");
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
            playerSender.sendMessage(ERROR_COLOR + "The rank you provided does not exist.");
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
                senderClan.broadcast(PLAYER_COLOR + playerPromoted.getName() + STANDARD_COLOR + " has been promoted to " + RANK_COLOR + newRank + STANDARD_COLOR + ".", new UUID[]{promotedUUID});
                if (playerPromoted.isOnline())
                    ((Player) playerPromoted).sendMessage(STANDARD_COLOR + "You have been promoted to " + RANK_COLOR + newRank + STANDARD_COLOR + ".");
                break;
            case 1:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "The player is not in the same clan as you.");
                break;
            case 2:
            case 3:
                playerSender.sendMessage(ERROR_COLOR + "That player can not be promoted any further.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + "You do not have permission to manage ranks.");
                break;
            case 5:
                playerSender.sendMessage(ERROR_COLOR + "You can not promote a player to an equal or lower status.");
                break;

        }
    }

    private void editName(Player playerSender, UUID playerUUID, String clanName) {

        Clan potentialClan = Clan.getClan(playerUUID);
        if (potentialClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        }
        switch (potentialClan.setName(playerUUID, clanName)) {
            case 0:
                //todo take gold here
                potentialClan.saveFile();
                potentialClan.broadcast(STANDARD_COLOR + "Clan name has been changed to \"" + clanName + "\".");
                break;
            case 30:
            case 1:
                playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + "You do not have permission to edit the clan name.");
                break;
            case 79:
                playerSender.sendMessage(ERROR_COLOR + "You need 20 gold to rename your clan. You currently have (value).");
                break;
            case 10:
                playerSender.sendMessage(ERROR_COLOR + "Your clan name can not be less than 3 characters.");
                break;
            case 11:
                playerSender.sendMessage(ERROR_COLOR + "Your clan name can not be longer than 20 characters.");
                break;
            case 12:
                playerSender.sendMessage(ERROR_COLOR + "Your clan name can not have special characters or whitespace characters.");
                break;
            case 21:
                playerSender.sendMessage(ERROR_COLOR + "There is already a clan with that name.");
                break;
        }
    }

    private void editTag(Player playerSender, UUID playerUUID, String tag) {

        Clan potentialClan = Clan.getClan(playerUUID);
        if (potentialClan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
        }

        if (clanTagCreators.containsKey(playerUUID)) {
            clanTagCreators.remove(playerUUID);
        }

        switch (potentialClan.setTag(playerUUID, tag)) {
            case 0:
                //todo take gold here
                potentialClan.saveFile();
                potentialClan.broadcast(STANDARD_COLOR + "Clan tag has been set to \"" + tag + STANDARD_COLOR + "\".");
                break;
            case 1:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
                break;
            case 4:
                playerSender.sendMessage(ERROR_COLOR + "You do not have permission to edit the clan tag.");
                break;
            case 10:
                playerSender.sendMessage(ERROR_COLOR + "Your clan tag can not be longer or shorter than 3 characters.");
                break;
            case 12:
                playerSender.sendMessage(ERROR_COLOR + "Your clan tag can not have special characters.");
                break;
            case 21:
                playerSender.sendMessage(ERROR_COLOR + "There is already a clan with that tag.");
                break;
            case 79:
                playerSender.sendMessage(ERROR_COLOR + "You need 5 gold to rename your clan's tag. You currently have (value).");
                break;

        }
    }

    private void createClan(Player playerSender, UUID playerUUID, String clanName) {

        Clan clan = new Clan(clanName, playerUUID);

        switch (clan.create(playerUUID, clanName)) {
            case 0:
                //todo take gold here

                clanTagCreators.put(playerUUID, clan);
                playerSender.sendMessage(STANDARD_COLOR + "Your clan has been created."); //todo gold takeaway this statement
                playerSender.sendMessage(STANDARD_COLOR + "What would you like your clan tag to be? It must be 3 characters long.");
                //From here the player will chat the responses and the HashMap will take care of the clan customization.
                break;
            case 1:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "You are already in clan.");
                break;
            case 10:
                playerSender.sendMessage(ERROR_COLOR + "Your clan name can not be less than 3 characters.");
                break;
            case 11:
                playerSender.sendMessage(ERROR_COLOR + "Your clan name can not be longer than 20 characters.");
                break;
            case 12:
                playerSender.sendMessage(ERROR_COLOR + "Your clan name can not have special characters or whitespace characters.");
                break;
            case 79:
                playerSender.sendMessage(ERROR_COLOR + "You need 100 gold to create a clan. You currently have (value).");
                break;
            case 21:
                playerSender.sendMessage(ERROR_COLOR + "There is already a clan with that name.");
                break;
        }
    }

    private void sendStaffHelp(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "------Clan Staff Help------");

        playerSender.sendMessage(COMMAND_COLOR + "/clan staff puuid " + ChatColor.YELLOW + "(playerName)");
        playerSender.sendMessage(COMMAND_COLOR + "/clan staff cuuid " + ChatColor.YELLOW + "(clanName)");

        playerSender.sendMessage(COMMAND_COLOR + "/clan staff kick " + ChatColor.YELLOW + "(playerName)");
        playerSender.sendMessage(COMMAND_COLOR + "/clan staff leader " + ChatColor.YELLOW + "(playerName) (clanName)");

        playerSender.sendMessage(COMMAND_COLOR + "/clan staff disband " + ChatColor.YELLOW + "(clanName)");
    }

    private void sendHelp(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "------Clan Help------");

        playerSender.sendMessage(COMMAND_COLOR + "/clan create " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/clan disband");

        playerSender.sendMessage(COMMAND_COLOR + "/clan rename " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/clan tag " + ChatColor.YELLOW + "(tag)");

        playerSender.sendMessage(COMMAND_COLOR + "/clan invite " + ChatColor.YELLOW + "(username)");
        playerSender.sendMessage(COMMAND_COLOR + "/clan kick " + ChatColor.YELLOW + "(username)");
        playerSender.sendMessage(COMMAND_COLOR + "/clan leave " + ChatColor.YELLOW + "");

        playerSender.sendMessage(COMMAND_COLOR + "/clan leader " + ChatColor.YELLOW + "(username)");
        playerSender.sendMessage(COMMAND_COLOR + "/clan promote " + ChatColor.YELLOW + "(username)");
        playerSender.sendMessage(COMMAND_COLOR + "/clan demote " + ChatColor.YELLOW + "(username)");


        playerSender.sendMessage(COMMAND_COLOR + "/clan chat " + ChatColor.YELLOW + "(switches to clan chat)");

        playerSender.sendMessage(COMMAND_COLOR + "/clan accept " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/clan deny " + ChatColor.YELLOW + "(name)");

        playerSender.sendMessage(COMMAND_COLOR + "/clan info " + ChatColor.YELLOW + "");
        playerSender.sendMessage(COMMAND_COLOR + "/clan who " + ChatColor.YELLOW + "(username)");

        playerSender.sendMessage(COMMAND_COLOR + "/ff " + ChatColor.YELLOW + "(toggles friendlyfire)");


    }

    public void disbandClan(final Player playerSender, final UUID playerUUID) {

        Clan clan = Clan.getClan(playerUUID);

        //The clan does not exist
        if (clan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return;
            //The rank of the player is not equal to leader.
        }

        switch (clan.disband(playerUUID)) {
            case 0:
                //If the player has already typed this command before. Confirms disbanding
                clanDisbandTimer.remove(playerUUID);
                //Message
                //Loop every clan user uuid
                for (UUID uuid : clan.getAllUUIDS()) {
                    //Get the player of the user
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    //If this player is online
                    if (player.isOnline()) {
                        //If the player is not the leader
                        if (!clan.getPlayerUUIDSBy(ClanRank.LEADER)[0].equals(playerUUID)) {
                            ((Player) player).sendMessage(STANDARD_COLOR + "You have been kicked from the clan.");
                        }
                        //All online players get this message
                        ((Player) player).sendMessage(STANDARD_COLOR + "Your clan has been disbanded.");
                    }
                }
                break;
            case 4:
            case 30:
                playerSender.sendMessage(ERROR_COLOR + "Only the leader can disband the clan.");
                break;
            case 20:
                //Ask for confirmation
                clanDisbandTimer.add(playerUUID);
                playerSender.sendMessage(STANDARD_COLOR + "Are you sure you want to disband the clan? Type " + "/clan disband" + STANDARD_COLOR + " in chat again to disband your clan. You have 60 seconds to confirm.");

                Bukkit.getScheduler().scheduleSyncDelayedTask(LostShardK.plugin, new Runnable() {
                    public void run() {
                        if (!clanDisbandTimer.contains(playerUUID)) //If he was removed from the confirmation list, don't do anything.
                            return;

                        //Else, the time expired.
                        if (playerSender.isOnline())
                            playerSender.sendMessage(STANDARD_COLOR + "The time to disband a clan has expired.");
                        clanDisbandTimer.remove(playerUUID);
                    }
                }, 60 * 20L); // 1200L (ticks) is equal to 60 seconds (20 ticks = 1 second)
                break;
        }
    }

}
