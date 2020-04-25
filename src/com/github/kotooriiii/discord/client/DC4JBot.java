package com.github.kotooriiii.discord.client;


import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.discord.events.DiscordCommandEvent;
import com.github.kotooriiii.discord.events.DiscordMessageCreateEvent;
import com.github.kotooriiii.discord.listeners.LinkListener;
import com.github.kotooriiii.util.HelperMethods;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import net.minecraft.server.v1_15_R1.Packet;
import org.bukkit.Bukkit;

public class DC4JBot {

    private final String token = TokenHolder.DISCORD_TOKEN;
    private final String[] acceptedGuilds = new String[] {"637114121568911373", "334226525316579328"};

    private  final String prefix = "/mc";

    private GatewayDiscordClient client;

    public DC4JBot()
    {
        client = DiscordClientBuilder.create(token).build().login().block();
        registerEvents();
    }

    public GatewayDiscordClient getClient()
    {
        return client;
    }

    public String getPrefix() {
        return prefix;
    }

    public void registerEvents()
    {
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(event -> {
                    Message message = event.getMessage();
                    String content = message.getContent();

                    boolean exists = false;
                    for(String guildSnowflake : acceptedGuilds)
                    {
                        if(guildSnowflake.equalsIgnoreCase(message.getGuild().block().getId().asString()))
                        {
                            exists = true;
                            break;
                        }
                    }

                    if(!exists)
                        return;

                    //If it doesn't start with the command prefix, stop. this must be a message
                    if(!content.toLowerCase().startsWith(prefix.toLowerCase()))
                        return;

                    //Split all arguments from a space
                    String[] tempArgs =content.split(" ");

                    //This is the possibility where the command somehow was split as; /mc test hi there i.e in minecraft this is like: / test, notice the space between / and test
                   if(tempArgs[0].equalsIgnoreCase(prefix))
                       return;
                    tempArgs[0] = tempArgs[0].substring(0, prefix.length());

                    String command = tempArgs[0];
                    String[] args = HelperMethods.stringBuilder(tempArgs, 1, " ").split(" ");

                    DiscordCommandEvent discordCommandEvent = new DiscordCommandEvent(message, command, args);
                    Bukkit.getPluginManager().callEvent(discordCommandEvent);

                    if(!discordCommandEvent.isSuccessful()) {
                        LostShardPlugin.getDiscord().sendAllHelpMessage(event.getMessage().getAuthor().get().getPrivateChannel().block());
                       discordCommandEvent.setCancelled(true);
                    }
                    if(discordCommandEvent.isCancelled())
                        message.delete();


                });

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(event -> {
                    Message message = event.getMessage();

                    boolean exists = false;
                    for(String guildSnowflake : acceptedGuilds)
                    {
                        if(guildSnowflake.equalsIgnoreCase(message.getGuild().block().getId().asString()))
                        {
                            exists = true;
                            break;
                        }
                    }

                    if(!exists)
                        return;

                    //If it starts with a command prefix, this must be an attempt for a command.
                    if(message.getContent().toLowerCase().startsWith(prefix.toLowerCase()))
                        return;


                    DiscordMessageCreateEvent discordMessageCreateEvent = new DiscordMessageCreateEvent(message);
                    Bukkit.getPluginManager().callEvent(discordMessageCreateEvent);

                    if(discordMessageCreateEvent.isCancelled())
                        message.delete();
                });
    }

    public void sendAllHelpMessage(MessageChannel channel)
    {
        channel.createMessage("-Help-");
        channel.createMessage("");
        channel.createMessage("Type '" + this.getPrefix() + LinkListener.LINK_COMMAND + "' to receive help regarding anything between linking your discord account to your minecraft account.");
    }
}
