package com.github.kotooriiii.ranks.animation;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.plots.PlotManager;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.ranks.RankType;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.CharArrayReader;
import java.text.DecimalFormat;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.stringBuilder;

public class TrailCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            Player playerSender = (Player) sender;
            UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "clan" command
            if (cmd.getName().equalsIgnoreCase("trail")) {


                //No arguments regarding this command
                if (args.length == 0) {

                    playerSender.sendMessage(ChatColor.DARK_PURPLE + "-------[Trails]-------");
                    playerSender.sendMessage(ChatColor.RED + "/trail NONE");
                    playerSender.sendMessage(ChatColor.LIGHT_PURPLE + "/trail FIRE");
                    playerSender.sendMessage(ChatColor.LIGHT_PURPLE + "/trail GREEN");
                    playerSender.sendMessage(ChatColor.LIGHT_PURPLE + "/trail HEART");
                    playerSender.sendMessage(ChatColor.LIGHT_PURPLE + "/trail BLUE");
                    playerSender.sendMessage(ChatColor.LIGHT_PURPLE + "/trail END");
                    return false;
                } else if (args.length >= 1) {
                    //Sub-commands again however with proper argument.

                    final RankPlayer wrap = RankPlayer.wrap(playerUUID);
                    if(wrap.getRankType() != RankType.SUBSCRIBER_PLUS)
                    {
                        playerSender.sendMessage(ChatColor.DARK_RED + "You must be a Subscriber+ in order to use Trails.");
                        return false;
                    }

                    String supply = stringBuilder(args, 0, " ");

                    try {
                        final AnimationManager.Trail trail = AnimationManager.Trail.valueOf(supply.toUpperCase());
                        LostShardPlugin.getAnimatorPackage().setTrail(playerUUID, trail);
                        playerSender.sendMessage(ChatColor.LIGHT_PURPLE + "You've set the trail to \"" + trail.name() + "\".");

                    }
                    catch (Exception e)
                    {
                        playerSender.sendMessage(ERROR_COLOR + "We cannot find a trail by that name.");
                        return false;
                    }

                }
            }

        }
        return false;
    }//end of commands
}
