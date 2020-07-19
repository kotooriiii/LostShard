package com.github.kotooriiii.status.shrine;

import com.github.kotooriiii.plots.struct.PlayerPlot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class AtoneCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("atone"))
            return false;

        Player player = (Player) commandSender;

        if (args.length == 0) {
            // .atone
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "staff":
                if (!player.hasPermission(STAFF_PERMISSION)) {
                    player.sendMessage(ERROR_COLOR + "No permission to access these commands.");
                    return false;
                }

                if (args.length != 2) {
                    return false;
                }
                switch (args[1].toLowerCase()) {
                    case "create":
                        break;
                    case "delete":
                        break;
                    default:
                        player.sendMessage(ERROR_COLOR + "You can only /atone staff [create/delete].");
                        break;
                }
                break;
            default:
                player.sendMessage(ERROR_COLOR + "Did you mean /atone staff?");
                break;
        }
        return true;
    }
}
