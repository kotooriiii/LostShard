package com.github.kotooriiii.discord.listeners;


import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.discord.events.DiscordCommandEvent;
import com.github.kotooriiii.discord.events.DiscordMessageCreateEvent;
import com.github.kotooriiii.discord.links.LinkPlayer;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

import static com.github.kotooriiii.data.Maps.*;

public class LinkListener implements Listener, CommandExecutor {

    //Map OF ID to Snowflake
    private static final HashMap<String, LinkPlayer> linkIDS = new HashMap<>();
    private static final ArrayList<String> snowflakeCreating = new ArrayList<>();

    private static final String CANCEL_COMMAND = "cancel";
    private static final String OPT_TOGGLE_COMMAND = "opt"; //Remember to switch this one in plugin.yml & registerCommand on main
    public static final String LINK_COMMAND = "link";
    private static final String UNLINK_COMMAND = "unlink";

    private static final String CHANNEL_NAME = "link-to-mc";

    /**
     * Cancel commmand in Discord.
     * @param e Event being passed.
     */
    @EventHandler
    public void onCancelCommand(DiscordCommandEvent e) {
        Message message = e.getMessage();
        MessageChannel channel = message.getChannel().block();

        //If not on DM disregard this.
        if (!channel.getType().equals(Channel.Type.DM))
            return;

        //If not the cancel command
        String cmd = e.getCommand();
        if (!cmd.equalsIgnoreCase(CANCEL_COMMAND))
            return;


        User user = message.getAuthor().get();
        String userSnowflake = user.getId().asString();

        //If this user is not trying to link
        if (!snowflakeCreating.contains(userSnowflake)) {
            channel.createMessage("You are not currently trying to link your minecraft account.");
            sendHelpMessage(channel);
            return;
        }

        /*
        The channel is in DMs
        The command is the cancel command
        The user is trying to link
         */

        //remove entries
        snowflakeCreating.remove(userSnowflake);
        linkIDS.remove(userSnowflake);

        channel.createMessage("You have canceled the old linking request.");
        e.setSuccessful(true);
    }

    @EventHandler
    public void onUnlinkCommand(DiscordCommandEvent e) {
        Message message = e.getMessage();
        MessageChannel channel = message.getChannel().block();

        //If not on DM disregard this.
        if (!channel.getType().equals(Channel.Type.DM))
            return;

        //If not the unlink command
        String cmd = e.getCommand();
        if (!cmd.equalsIgnoreCase(UNLINK_COMMAND))
            return;

        User user = message.getAuthor().get();
        String userSnowflake = user.getId().asString();

        if (!LinkPlayer.isLinked(userSnowflake)) {
            channel.createMessage("You are not currently linked to any minecraft account.");
            sendHelpMessage(channel);
            return;
        }

        LinkPlayer linkPlayer = LinkPlayer.ofSnowflake(userSnowflake);

        //This WILL NOT happen since snowflakes are null in this case but will be left here to understand the logic.
        if (!linkPlayer.isOpted()) {
            channel.createMessage("You are not currently linked to any minecraft account.");
            sendHelpMessage(channel);
            return;
        }

        /*
        The channel is in DMs
        The user is typing the unlink command
        The user is linked
         */

        //remove entries
        linkPlayer.remove();
        channel.createMessage("You have removed the link.");
        e.setSuccessful(true);
    }

    @EventHandler
    public void onAuthenticatorMessageReceived(DiscordMessageCreateEvent e) {
        Message message = e.getMessage();
        if (message == null)
            return;

        MessageChannel channel = message.getChannel().block();

        //If not on DM disregard this.
        if (!channel.getType().equals(Channel.Type.DM))
            return;

        User user = message.getAuthor().get();
        String userSnowflake = user.getId().asString();

        //If this user is trying to link
        if (!snowflakeCreating.contains(userSnowflake))
            return;

        //Linked already
        if (LinkPlayer.isLinked(userSnowflake)) {
            snowflakeCreating.remove(userSnowflake);
            linkIDS.remove(userSnowflake);
            return;
        }

        String content = message.getContent();

        //If this is correct code
        LinkPlayer linkPlayer = linkIDS.get(content);

        //CODE IS WRONG
        if (linkPlayer == null) {
            channel.createMessage("The code was invalid. If you wish to cancel the linking phase type '" + LostShardPlugin.getDiscord().getPrefix() + CANCEL_COMMAND + "' in discord.");
            sendHelpMessage(channel);
            return;
        }

        //If snowflakes are NOT SAME this is someone else that hijacked it.
        if (!linkPlayer.getUserSnowflake().equalsIgnoreCase(userSnowflake)) {
            channel.createMessage("The code is valid but it doesn't match the discord user. If you wish to cancel the linking phase type '" + LostShardPlugin.getDiscord().getPrefix() + CANCEL_COMMAND + "' on the correct discord user.");
            sendHelpMessage(channel);
            return;
        }

        /*

        The channel is DM
        The user is trying to link their minecraft
        The code is correct
        The accounts both are equal.
         */

        //successful link

        linkPlayer.save();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(linkPlayer.getPlayerUUID());
        if (offlinePlayer.isOnline())
            offlinePlayer.getPlayer().sendMessage(DISCORD_COLOR + "The discord account, " + user.getUsername() + ", has been linked to this minecraft account.");
        channel.createMessage("You have successfully linked the minecraft account, " + offlinePlayer.getName() + ".");
    }

    @EventHandler
    public void onLinkCommand(DiscordCommandEvent e) {
        Message msgObject = e.getMessage();

        if (msgObject == null)
            return;
        MessageChannel channel = e.getMessage().getChannel().block();
        PrivateChannel privateChannel = msgObject.getAuthor().get().getPrivateChannel().block();
        if (channel == null || privateChannel == null)
            return;

        String discordUsername = msgObject.getAuthor().get().getUsername();

        String command = e.getCommand();
        String[] args = e.getArguments();

        //Not in DMs and not in guild text channel.
        if (!channel.getRestChannel().getData().block().name().get().equalsIgnoreCase(CHANNEL_NAME) && !channel.getType().equals(Channel.Type.DM))
            return;

        //Not link command
        if (!command.equalsIgnoreCase(LINK_COMMAND))
            return;

        if (args.length != 1) {
            privateChannel.createMessage("-Linking your minecraft account-");
            privateChannel.createMessage(" ");
            privateChannel.createMessage("To link your minecraft account, type: '" + LostShardPlugin.getDiscord().getPrefix() + LINK_COMMAND + "' here.");
            privateChannel.createMessage("To unlink your minecraft account, type: '" + LostShardPlugin.getDiscord().getPrefix() + UNLINK_COMMAND + "' here.");
            privateChannel.createMessage("To opt in/out of receiving linking requests to your minecraft account, type: '/" + OPT_TOGGLE_COMMAND + "' in your minecraft account.");
            privateChannel.createMessage("To cancel linking your minecraft account, type: '" + LostShardPlugin.getDiscord().getPrefix() + CANCEL_COMMAND + "' here.");
            return;
        }

        String playerName = args[0];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (!offlinePlayer.hasPlayedBefore()) {
            privateChannel.createMessage("The player you specified has never played on the server.");
            sendHelpMessage(privateChannel);
            return;
        }
        if (!offlinePlayer.isOnline()) {
            privateChannel.createMessage("The player must be on the server to continue linking.");
            sendHelpMessage(privateChannel);
            return;
        }

        Player targetPlayer = offlinePlayer.getPlayer();

        //Check if link is connected
        if (LinkPlayer.isLinked(targetPlayer.getUniqueId())) {
            LinkPlayer linkPlayer = LinkPlayer.ofUUID(targetPlayer.getUniqueId());
            //Not opted means won't receive this message
            if (!linkPlayer.isOpted()) {
                privateChannel.createMessage("The player is not opted in receiving linking requests.\nTo opt in, type '" + LostShardPlugin.getDiscord().getPrefix() + OPT_TOGGLE_COMMAND + "' in your minecraft account.");
                sendHelpMessage(privateChannel);
                return;
            } else {
                //Opted means already linked.
                privateChannel.createMessage(DISCORD_COLOR + "You are currently linked to an account.\nDid you mean to link to a new account? You must first unlink. Type '" + LostShardPlugin.getDiscord().getPrefix() + UNLINK_COMMAND + "' to unlink your current minecraft account.");
                sendHelpMessage(privateChannel);
                return;
            }
        }

        //Check if trying to link again!
        if (snowflakeCreating.contains(msgObject.getAuthor().get().getId().asString())) {
            privateChannel.createMessage(DISCORD_COLOR + "You are already trying to connect to a minecraft account.\nDid you mean to cancel the old request? Type " + LostShardPlugin.getDiscord().getPrefix() + CANCEL_COMMAND + " to cancel the old request.");
            sendHelpMessage(privateChannel);
            return;
        }

        privateChannel.createMessage("Enter the unique code sent to the minecraft account, " + offlinePlayer.getName() + ".");

        String uniqueCode = generateUniqueID();


        String snowflake = msgObject.getAuthor().get().getId().asString();

        //Add to memory
        linkIDS.put(uniqueCode, new LinkPlayer(snowflake, targetPlayer.getUniqueId()));
        snowflakeCreating.add(snowflake);

        targetPlayer.sendMessage(DISCORD_COLOR + "The user, " + PLAYER_COLOR + discordUsername + DISCORD_COLOR + ", is trying to link their discord account with this minecraft account.");
        targetPlayer.sendMessage(DISCORD_COLOR + "Your unique code is '" + PLAYER_COLOR + uniqueCode + DISCORD_COLOR + "'.");
        targetPlayer.sendMessage(" ");
        targetPlayer.sendMessage(DISCORD_COLOR + "If this is not you, don't worry. You don't need to take any action.");
        targetPlayer.sendMessage(" ");
        targetPlayer.sendMessage(DISCORD_COLOR + "If you want to opt out, type: '/" + OPT_TOGGLE_COMMAND + "' in your minecraft account.");
        if (!channel.getType().equals(Channel.Type.DM))
            e.setCancelled(true); //Deletes message
        e.setSuccessful(true);
    }

    @EventHandler
    public void onOptToggleCommand(DiscordCommandEvent e) {
        Message message = e.getMessage();
        MessageChannel channel = message.getChannel().block();

        //If not on DM disregard this.
        if (!channel.getType().equals(Channel.Type.DM))
            return;

        //If not the opt out command
        String cmd = e.getCommand();
        if (!cmd.equalsIgnoreCase(OPT_TOGGLE_COMMAND))
            return;

        User user = message.getAuthor().get();
        String userSnowflake = user.getId().asString();

        //If this user is not linked
        if (!LinkPlayer.isLinked(userSnowflake)) {
            channel.createMessage("You are not linked to any minecraft account.");
            sendHelpMessage(channel);
            return;
        }

        /*
        At this point we know a few things:
        - This is a DM channel
        - Using the opt toggle command
        -
         */

        //remove entries
        snowflakeCreating.remove(userSnowflake);
        linkIDS.remove(userSnowflake);

        channel.createMessage("You have canceled the linking phase.");
        e.setSuccessful(true);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        //Not a player
        if (!(sender instanceof Player))
            return false;

        //Not Opt toggle command
        if (!cmd.getName().equalsIgnoreCase(OPT_TOGGLE_COMMAND))
            return false;

        //So far, it is a player and is typing the opt toggle command in minecraft.

        Player playerSender = (Player) sender;

        //LinkPlayer associated with minecraft id
        if (LinkPlayer.isLinked(playerSender.getUniqueId())) {
            //Get link player
            LinkPlayer linkPlayer = LinkPlayer.ofUUID(playerSender.getUniqueId());
            //A minecraft player has complete discord+minecraft link.
            if (linkPlayer.isOpted()) {
                playerSender.sendMessage(DISCORD_COLOR + "You are currently linked to an account. You already don't receive messages.\nDid you mean to unlink? Type '" + LostShardPlugin.getDiscord().getPrefix() + LINK_COMMAND + "' on the #" + CHANNEL_NAME + " channel to receive unlinking help.");
                return false;
            } else {
                //A minecraft player is currently opted out but wants to opt back in.
                linkPlayer.remove();
                playerSender.sendMessage(DISCORD_COLOR + "You have opted in to receive linking requests.\nTo revert this change type this command again.");
                return true;
            }
        }

        //A player is trying the opt out command AND is not linked to an account yet.

        LinkPlayer linkPlayer = new LinkPlayer(null, playerSender.getUniqueId());
        linkPlayer.save();
        playerSender.sendMessage(DISCORD_COLOR + "You have opted out of receiving linking requests.\nTo revert this change type this command again.");
        return true;
    }

    /**
     * Sends a help message to a channel when an user-related error occurs.
     * @param channel Make sure this is a private channel.
     */
    private void sendHelpMessage(MessageChannel channel) {
        channel.createMessage(" ");
        channel.createMessage("Are you stuck? Try visiting the '" + LostShardPlugin.getDiscord().getPrefix() + LINK_COMMAND + "' command here for help regarding anything to do with linking your discord account to your minecraft account.");
        return;
    }

    /**
     * Generates a unique ID to link discord+minecraft
     * @return A unique ID
     */
    private String generateUniqueID() {

        char[] uniqueCodeCharacters = new char[6];

        String upperCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbersCharacters = "0123456789";
        String acceptedString = upperCharacters + numbersCharacters;
        char[] acceptedCharacters = acceptedString.toCharArray();

        for (int i = 0; uniqueCodeCharacters.length < 0; i++) {
            Random random = new Random();
            int randomNumber = random.nextInt(acceptedCharacters.length);

            char codeChar = acceptedCharacters[randomNumber];
            uniqueCodeCharacters[i] = codeChar;
        }

        String uniqueID = String.valueOf(uniqueCodeCharacters);


        if (linkIDS.containsKey(uniqueID))
            uniqueID = generateUniqueID();


        return uniqueID;
    }

}




