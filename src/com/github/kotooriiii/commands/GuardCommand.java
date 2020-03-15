package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.guards.ShardGuard;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
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
                        if(guard==null)
                        {
                            playerSender.sendMessage(ERROR_COLOR + "No guard nearby!!!");
                            return true;
                        }
                        guard.teleport(playerLocation);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (guard.isBusy())
                                    return;
                                int curX = guard.getCurrentLocation().getBlockX();
                                int postX = guard.getSpawnLocation().getBlockX();
                                int curY = guard.getCurrentLocation().getBlockY();
                                int postY = guard.getSpawnLocation().getBlockY();
                                int curZ = guard.getCurrentLocation().getBlockZ();
                                int postZ = guard.getSpawnLocation().getBlockZ();

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
                                int postX = guard.getSpawnLocation().getBlockX();
                                int curY = guard.getCurrentLocation().getBlockY();
                                int postY = guard.getSpawnLocation().getBlockY();
                                int curZ = guard.getCurrentLocation().getBlockZ();
                                int postZ = guard.getSpawnLocation().getBlockZ();

                                if (curX != postX && curY != postY && curZ != postZ) {
                                    guard.teleport(guard.getSpawnLocation());
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
                                    playerSender.sendMessage(STANDARD_COLOR + "You have hired " + GUARD_COLOR + nameCreate + STANDARD_COLOR + " to stand in this position.");
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
                                            playerSender.sendMessage(STANDARD_COLOR + "You have relieved " + GUARD_COLOR + iteratingGuard.getName() + STANDARD_COLOR + " from his duty.");
                                            iteratingGuard.destroy();
                                            FileManager.removeFile(iteratingGuard);
                                            return true;
                                        }
                                    }
                                    playerSender.sendMessage(ERROR_COLOR + "We could not find " + GUARD_COLOR + nameDelete + ERROR_COLOR+  " in our records of Guards.");
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
                                            playerSender.sendMessage(STANDARD_COLOR + "You have set a new location for the guard to.. well uh, guard. Thanks, " + GUARD_COLOR + iteratingGuard.getName() + STANDARD_COLOR + "!");
                                            iteratingGuard.setSpawnLocation(playerSender.getLocation());
                                            FileManager.write(iteratingGuard);
                                            return true;
                                        }
                                    }
                                    playerSender.sendMessage(ERROR_COLOR + "We could not find " + GUARD_COLOR + nameSetGuardPost + ERROR_COLOR + " in our records of Guards.");
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
                                    playerSender.sendMessage(STANDARD_COLOR + "-=[Guards Active]=-");
                                    for (ShardGuard iteratingGuard : ShardGuard.getActiveShardGuards()) {
                                        int x = iteratingGuard.getCurrentLocation().getBlockX();
                                        int y = iteratingGuard.getCurrentLocation().getBlockY();
                                        int z = iteratingGuard.getCurrentLocation().getBlockZ();
                                        BaseComponent[] tc = new ComponentBuilder(GUARD_COLOR + "" + iteratingGuard.getName() + STANDARD_COLOR + " is positioned at x:" +STANDARD_COLOR +  x + ", y:" + y + ", z:" + z + ".")
                                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "Teleport to " + GUARD_COLOR + iteratingGuard.getName() + STANDARD_COLOR + ".").create()))
                                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleport " + playerSender.getName() + " " + x + " " + y + " " + z)).create();

                                       playerSender.spigot().sendMessage(ChatMessageType.CHAT, tc);
                                    }
                                    playerSender.sendMessage(STANDARD_COLOR + "-----------------");
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
        playerSender.sendMessage(COMMAND_COLOR + "/guard staff setguardpost " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/guard staff show " + ChatColor.YELLOW + "");
    }

    private void sendUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Guard. Use " + "/guard" + ERROR_COLOR + " to have a guard protect you momentarily.");
    }

    private void sendStaffUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Guard's Staff. Use " + "/guard staff" + ERROR_COLOR + " for help.");
    }
}
