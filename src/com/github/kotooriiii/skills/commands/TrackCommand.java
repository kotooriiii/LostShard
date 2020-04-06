package com.github.kotooriiii.skills.commands;

import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class TrackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player))
            return false;

        final Player playerSender = (Player) sender;
        final UUID playerUUID = playerSender.getUniqueId();

        if (cmd.getName().equalsIgnoreCase("track")) {
            if (args.length == 0) {
                // .track
                playerSender.sendMessage("The correct usage of the command is???");
                return false;
            }

            String mobName = HelperMethods.stringBuilder(args, 0, "_").toUpperCase();
            EntityType type = null;
            try {
                type = EntityType.valueOf(mobName);
            } catch (IllegalArgumentException e) {
                //Not a mob, it's okay we nullified by default.
            }

            Player player = null;
            //No mob name
            if (type == null) {

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(mobName);
                if ((!offlinePlayer.hasPlayedBefore() || !offlinePlayer.isOnline())) {
                    playerSender.sendMessage(ERROR_COLOR + "The mob name or player name could not be found.");
                    return false;
                } else {
                    type = EntityType.PLAYER;
                    player = offlinePlayer.getPlayer();
                }
            }

            int level = (int) SkillPlayer.wrap(playerUUID).getSurvivalism().getLevel();

            if (type == EntityType.PLAYER) {

                if(level < 100)
                {
                    playerSender.sendMessage(ERROR_COLOR + "You must be at least level 100 for this perk.");
                    return false;
                }

                //todo

            } else {

                double distance = Double.MAX_VALUE;
                Entity closestEntity = null;

                for(Entity entity : playerSender.getLocation().getWorld().getNearbyEntities(playerSender.getLocation(), 100, 256, 100))
                {
                    if(entity.getType().equals(type))
                    {
                        double entityDistance = entity.getLocation().distance(playerSender.getLocation());
                        if(entityDistance < distance)
                        {
                            distance = entityDistance;
                            closestEntity = entity;
                        }
                    }
                }

                if(closestEntity == null)
                {
                    String name = type.getKey().getKey().toLowerCase().replace("_", " ");
                    name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
                    playerSender.sendMessage(ERROR_COLOR + "We could not find a " + name + " close to your location.");
                }
            }
            return false;
        }

        return true;
    }

    public String getCompassDirection(Player player, Location location)
    {

        Vector vector = player.getLocation().getDirection();



    }

}
