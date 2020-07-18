package com.github.kotooriiii.status;

import com.github.kotooriiii.plots.struct.PlayerPlot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AtoneCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;

        if(!command.getName().equalsIgnoreCase("atone"))
            return false;

        Player player = (Player) commandSender;

    }
}
