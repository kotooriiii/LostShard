package com.github.kotooriiii.plots.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.listeners.SignChangeListener;
import com.github.kotooriiii.skills.SkillBuild;
import com.github.kotooriiii.skills.SkillPlayer;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

public class BuildCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player))
            return false;
        if (!cmd.getName().equalsIgnoreCase("build"))
            return false;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ERROR_COLOR + "You can switch builds by typing: /build [0/1]");
            return false;
        }

        if(!SignChangeListener.isNearbySign(player.getLocation()))
        {
            player.sendMessage(ERROR_COLOR + "You must be near a build changer location to switch your build.");
            return false;
        }

        String numString = args[0];

        if (!NumberUtils.isNumber(numString) || numString.contains(".")) {
            player.sendMessage(ERROR_COLOR + "Must be a positive integer to indicate what build to select.");
            return false;
        }

        int num = Integer.parseInt(numString);

        if (num < 0) {
            player.sendMessage(ERROR_COLOR + "Must be a positive integer to indicate what build to select.");
            return false;
        }

        SkillPlayer skillPlayer = LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId());
        SkillBuild[] skillBuilds = skillPlayer.getSkillBuilds();
        if (num >= skillBuilds.length) {
            player.sendMessage(ERROR_COLOR + "There are only " + (skillBuilds.length - 1) + " choices.");
            return false;
        }

        if (num == skillPlayer.getActiveIndex()) {
            player.sendMessage(ERROR_COLOR + "This build is already selected.");
            return false;
        }

        player.sendMessage(STANDARD_COLOR + "You've selected Build #" + num + ".");
        skillPlayer.setActiveBuild(num);
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.setHealth(0);

        return true;
    }
}
