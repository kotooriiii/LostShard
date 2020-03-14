package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.guards.ShardGuard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.COMMAND_COLOR;
import static com.github.kotooriiii.util.HelperMethods.stringBuilder;

public class GuardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("guard")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    final Location playerLocation = playerSender.getLocation();
                    if ("bad guy is near".isEmpty() == false) {
                        ShardGuard guard = ShardGuard.getNearestGuard(playerLocation);
                        guard.teleport(playerLocation);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (guard.isBusy())
                                    return;
                                int curX = guard.getCurrentLocation().getBlockX();
                                int postX = guard.getGuardPost().getBlockX();
                                int curY = guard.getCurrentLocation().getBlockY();
                                int postY = guard.getGuardPost().getBlockY();
                                int curZ = guard.getCurrentLocation().getBlockZ();
                                int postZ = guard.getGuardPost().getBlockZ();

                                if (curX != postX && curY != postY && curZ != postZ) {
                                    guard.getCurrentLocation().getWorld().playSound(guard.getCurrentLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 0);

                                }
                            }
                        }.runTaskLater(LostShardK.plugin, 40);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (guard.isBusy())
                                    return;
                                int curX = guard.getCurrentLocation().getBlockX();
                                int postX = guard.getGuardPost().getBlockX();
                                int curY = guard.getCurrentLocation().getBlockY();
                                int postY = guard.getGuardPost().getBlockY();
                                int curZ = guard.getCurrentLocation().getBlockZ();
                                int postZ = guard.getGuardPost().getBlockZ();

                                if (curX != postX && curY != postY && curZ != postZ) {
                                    guard.teleport(guard.getGuardPost());
                                }
                            }

                        }.runTaskLater(LostShardK.plugin, 60);

                    }
                }
                //This statement refers to: /guards <argument 0> <argument 1> ... <argument n>
                else if (args.length >= 1) {
                    switch (args[0].toLowerCase()) {
                        case "staff":
                            if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                                playerSender.sendMessage(ERROR_COLOR + "You do not have access to staff commands.");
                                return true;
                            }

                            if (args.length == 1) //host <argument 0>
                            {
                                sendStaffHelp(playerSender);
                                return true;
                            }

                            switch (args[1].toLowerCase()) { //host <arg 0> <arg 1> ... <arg n>
                                case "create":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/guard staff create (name)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    }
                                    // /host <arg 0/staff> <arg 1/create> ......... <arg n>
                                    String nameCreate = stringBuilder(args, 2, " ");
                                    for (ShardGuard iteratingGuard : ShardGuard.getActiveShardGuards()) {
                                        if (iteratingGuard.getName().equalsIgnoreCase(nameCreate)) {
                                            playerSender.sendMessage(ERROR_COLOR + "The name you chose is already taken by another Guard.");
                                            return true;
                                        }
                                    }
                                    playerSender.sendMessage(STANDARD_COLOR + "You have hired " + nameCreate + "to stand in this position.");
                                    ShardGuard guard = new ShardGuard(nameCreate);
                                    guard.spawn(playerSender.getLocation());
                                    FileManager.write(guard);
                                    break;
                                case "delete":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/guard staff delete (name)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    }
                                    // /host <arg 0/staff> <arg 1/create> ......... <arg n>
                                    String nameDelete = stringBuilder(args, 2, " ");
                                    for (ShardGuard iteratingGuard : ShardGuard.getActiveShardGuards()) {
                                        if (iteratingGuard.getName().equalsIgnoreCase(nameDelete)) {
                                            playerSender.sendMessage(STANDARD_COLOR + "You have relieved " + iteratingGuard.getName() + " from his duty.");
                                            iteratingGuard.destroy();
                                            FileManager.removeFile(iteratingGuard);
                                            return true;
                                        }
                                    }
                                    playerSender.sendMessage(ERROR_COLOR + "We could not find " + nameDelete + " in our records of Guards.");
                                    break;
                                case "setguardpost":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/guard staff setguardpost (name)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    }
                                    // /host <arg 0/staff> <arg 1/create> ......... <arg n>
                                    String nameSetGuardPost = stringBuilder(args, 2, " ");
                                    for (ShardGuard iteratingGuard : ShardGuard.getActiveShardGuards()) {
                                        if (iteratingGuard.getName().equalsIgnoreCase(nameSetGuardPost)) {
                                            playerSender.sendMessage(STANDARD_COLOR + "You have set a new location for the guard to.. well uh, guard. Thanks, " + iteratingGuard.getName() + "!");
                                            iteratingGuard.setGuardPost(playerSender.getLocation());
                                            FileManager.write(iteratingGuard);
                                            return true;
                                        }
                                    }
                                    playerSender.sendMessage(ERROR_COLOR + "We could not find " + nameSetGuardPost + " in our records of Guards.");
                                    break;
//                                case "setname":
//                                    if (args.length == 2 || args.length == 3) {
//                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/guard staff setguardpost (name)" + ERROR_COLOR + "."); //clan staff uuid
//                                        return true;
//                                    }
//                                    // /host <arg 0/staff> <arg 1/create> ......... <arg n>
//                                    String setName = stringBuilder(args, 2, " ");
//                                    for (ShardGuard iteratingGuard : ShardGuard.getActiveShardGuards()) {
//                                        if (iteratingGuard.getName().equalsIgnoreCase(setName)) {
//                                            String oldName = iteratingGuard.getName();
//                                            playerSender.sendMessage(STANDARD_COLOR + "You forcibly changed the name of the guard from " + oldName + " to " + setName + ".");
//                                            iteratingGuard.setName(iteratingGuard.getPrefix(), setName);
//                                            FileManager.write(iteratingGuard, oldName);
//                                            return true;
//                                        }
//                                    }
//                                    playerSender.sendMessage(ERROR_COLOR + "We could not find " + setName + " in our records of Guards.");
//                                    break;
                                case "show":
//todo
                                    break;
                                default:
                                    sendStaffUnknownCommand(playerSender);
                                    break;
                            }
                            break;
                        default:
                            sendUnknownCommand(playerSender);
                            break;
                    }
                }
            }
        }
        return true;
    }

    public void createGuard(Player player) {

    }

    public void sendStaffHelp(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "------Guard Staff Help------");

        playerSender.sendMessage(COMMAND_COLOR + "/guard staff create " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/guard staff delete " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/guard staff edit " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/guard staff show " + ChatColor.YELLOW + "");
    }

    private void sendUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Guard. Use " + "/guard" + ERROR_COLOR + " to have a guard protect you momentarily.");
    }

    private void sendStaffUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Guard's Staff. Use " + "/guard staff" + ERROR_COLOR + " for help.");
    }
}
