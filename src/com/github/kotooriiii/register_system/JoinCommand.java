package com.github.kotooriiii.register_system;

import com.github.kotooriiii.LostShardPlugin;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class JoinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!cmd.getName().equalsIgnoreCase("join"))
            return false;
        if(!(sender instanceof Player))
            return false;
        Player player = (Player) sender;

        Gathering gathering = LostShardPlugin.getGatheringManager().getGathering();
        if(gathering == null)
        {
            player.sendMessage(ERROR_COLOR + "There is no event to join right now.");
            return false;
        }
        RegisterManager manager = gathering.getRegisterManager();
        if(manager.hasPlayer(player))
        {
            player.sendMessage(ERROR_COLOR + "You are already registered to this event.");

            TextComponent component1 = new TextComponent("Type /leave to leave, or click here to leave!");
            component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/leave"));
            component1.setColor(ChatColor.GREEN);

            player.spigot().sendMessage(component1);
            return false;
        }

        manager.addPlayer(player);
        player.sendMessage(STANDARD_COLOR + "You have joined this event!");

        return false;
    }
}
