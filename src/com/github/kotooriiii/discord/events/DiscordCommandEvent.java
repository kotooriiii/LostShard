package com.github.kotooriiii.discord.events;

import discord4j.core.object.entity.Message;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DiscordCommandEvent extends DiscordBaseCommandEvent implements Cancellable {
    private Message message;
    private String command;
    private String[] args;

    boolean cancelled;

    public DiscordCommandEvent(Message message, String command, String[] args)
    {
        this.message = message;
        this.command = command;
        this.args = args;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }



    public Message getMessage() {return  message;}

    public String getCommand()
    {
        return command;
    }

    public String[] getArguments()
    {
        return this.args;
    }
}
