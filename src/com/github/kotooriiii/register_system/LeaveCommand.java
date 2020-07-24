package com.github.kotooriiii.register_system;

import com.github.kotooriiii.LostShardPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class LeaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!cmd.getName().equalsIgnoreCase("leave"))
            return false;
        if(!(sender instanceof Player))
            return false;
        Player player = (Player) sender;

        Gathering gathering = LostShardPlugin.getGatheringManager().getGathering();
        if(gathering == null)
        {
            player.sendMessage(ERROR_COLOR + "There is no event to leave right now.");
            return false;
        }
        RegisterManager manager = gathering.getRegisterManager();
        if(!manager.hasPlayer(player))
        {
            player.sendMessage(ERROR_COLOR + "You are not registered to this event.");
            return false;
        }

        manager.removePlayer(player);
        player.sendMessage(STANDARD_COLOR + "You have left this event!");

        return false;
    }
}
