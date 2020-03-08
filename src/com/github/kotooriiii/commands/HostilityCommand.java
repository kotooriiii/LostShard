package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.hostility.HostilityMatch;
import com.github.kotooriiii.hostility.HostilityPlatform;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.*;

public class HostilityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "ff" command
            if (cmd.getName().equalsIgnoreCase("hostility")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    sendHelp(playerSender);
                    // cmd is /host
                }
                //This statement refers to: /host <argument 0> <argument 1> ... <argument n>
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
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/host staff create (name of hostility map)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    } else {
                                        // /host <arg 0/staff> <arg 1/create> ......... <arg n>
                                        String name = stringBuilder(args, 2);

                                        for (HostilityPlatform platform : platforms) {
                                            if (platform.getName().equalsIgnoreCase(name)) {
                                                playerSender.sendMessage(ERROR_COLOR + "This platform name shares a name with another. Try assigning a new name.");
                                                return true;
                                            }
                                        }

                                        if (hostilityPlatformCreator.containsKey(playerUUID)) {
                                            playerSender.sendMessage(ERROR_COLOR + "You are already creating a Hostility Platform for " + ((HostilityPlatform) hostilityPlatformCreator.get(playerUUID)).getName() + ".");
                                            playerSender.sendMessage(ERROR_COLOR + "To cancel the creation of this platform type: /host staff cancel.\nTo get your tools back type: /host staff tools.");
                                            return true;
                                        }

                                        if (!hostilityCreatorConfirmation.contains(playerUUID)) {
                                            playerSender.sendMessage(STANDARD_COLOR + "Are you sure you want to create a new Hostility Platform? Your inventory will be cleared after confirming. To confirm execute the command again: " + COMMAND_COLOR + "/host staff create" + STANDARD_COLOR + ". You have 60 seconds to confirm.");

                                            hostilityCreatorConfirmation.add(playerUUID);

                                            Bukkit.getScheduler().scheduleSyncDelayedTask(LostShardK.plugin, new Runnable() {
                                                public void run() {
                                                    if (!hostilityCreatorConfirmation.contains(playerUUID)) //If he was removed from the confirmation list, don't do anything.
                                                        return;

                                                    //Else, the time expired.
                                                    if (playerSender.isOnline())
                                                        playerSender.sendMessage(STANDARD_COLOR + "The time to create a new Hostility Platform has expired.");
                                                    hostilityCreatorConfirmation.remove(playerUUID);
                                                }
                                            }, 60 * 20L);
                                            return true;
                                        }


                                        playerSender.sendMessage(STANDARD_COLOR + "You have enabled editing mode.");

                                        giveTools(playerSender);
                                        hostilityCreatorConfirmation.remove(playerUUID);

                                        //add to hostility platform creator
                                        HostilityPlatform platform = new HostilityPlatform(name);
                                        hostilityPlatformCreator.put(playerUUID, platform);
                                    }
                                    break;
                                case "delete":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/host staff delete (name of hostility map)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    } else {
                                        // /host <arg 0/staff> <arg 1/delete> ......... <arg n>
                                        String name = stringBuilder(args, 2);

                                        for (HostilityPlatform platform : platforms) {
                                            if (platform.getName().equalsIgnoreCase(name)) {
                                                if (!hostilityRemoverConfirmation.contains(playerUUID)) {
                                                    playerSender.sendMessage(STANDARD_COLOR + "Are you sure you want to delete " + platform.getName() + "? Type: " + COMMAND_COLOR + "/host staff delete " + name + ERROR_COLOR + " to confirm the permanent deletion of the Hostility Platform. You have 60 seconds to confirm.");
                                                    hostilityRemoverConfirmation.add(playerUUID);


                                                    Bukkit.getScheduler().scheduleSyncDelayedTask(LostShardK.plugin, new Runnable() {
                                                        public void run() {
                                                            if (!hostilityRemoverConfirmation.contains(playerUUID)) //If he was removed from the confirmation list, don't do anything.
                                                                return;

                                                            //Else, the time expired.
                                                            if (playerSender.isOnline())
                                                                playerSender.sendMessage(STANDARD_COLOR + "The time to delete a Hostility Platform has expired.");
                                                            hostilityRemoverConfirmation.remove(playerUUID);
                                                        }
                                                    }, 60 * 20L);

                                                } else {
                                                    hostilityRemoverConfirmation.remove(playerUUID);
                                                    platforms.remove(platform);
                                                    FileManager.removeFile(platform);
                                                    playerSender.sendMessage(STANDARD_COLOR + "You have removed " + platform.getName() + ".");
                                                }
                                                return true;

                                            }
                                        }

                                        playerSender.sendMessage(ERROR_COLOR + "We could not find " + name + " in our records of Hostility Platforms.");
                                    }
                                    break;
                                case "edit":
                                    playerSender.sendMessage("This sub-command is currently a work-in-progress.");
                                    break;
                                case "show":
                                    playerSender.sendMessage(STANDARD_COLOR + "-=[Hostility Platform(s)]=-");
                                    for (HostilityPlatform platform : platforms) {
                                        playerSender.sendMessage(STANDARD_COLOR + platform.getName());
                                    }
                                    playerSender.sendMessage(STANDARD_COLOR + "-----------------");
                                    break;
                                case "cancel":
                                    if (!hostilityPlatformCreator.containsKey(playerUUID)) {
                                        playerSender.sendMessage(ERROR_COLOR + "We cannot cancel a Hostility Platform if you are not in the process of creating one.");
                                    } else {
                                        hostilityPlatformCreator.remove(playerUUID);
                                        playerSender.sendMessage(STANDARD_COLOR + "You canceled the creation of a new Hostility Platform!");

                                    }
                                    break;
                                case "tools":
                                    if (!hostilityPlatformCreator.containsKey(playerUUID)) {
                                        playerSender.sendMessage(ERROR_COLOR + "We cannot give you tools if you haven't confirmed. Type: /clan create (name).");
                                    } else {
                                        giveTools(playerSender);
                                        playerSender.sendMessage(STANDARD_COLOR + "You were given tools to create a Hostility Platform!");
                                    }
                                    break;
                                case "start":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/host staff start (name of hostility map)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    } else {
                                        // /host <arg 0/staff> <arg 1/delete> ......... <arg n>
                                        String name = stringBuilder(args, 2);
                                        for (HostilityPlatform platform : platforms) {

                                            if (platform.getName().equalsIgnoreCase(name)) {

                                                for (HostilityMatch match : activeHostilityGames) {
                                                    if (match.getPlatform().getName().equalsIgnoreCase(name)) {
                                                        playerSender.sendMessage(ERROR_COLOR + "This platform is currently being played in a match. You must cancel the ongoing playing match in order to create a new one.");
                                                        return true;
                                                    }
                                                }
                                                HostilityMatch match = new HostilityMatch(platform);
                                                match.startGame();
                                                return true;
                                            }
                                        }
                                        playerSender.sendMessage(ERROR_COLOR + "We could not find " + name + " in our records of Hostility Platforms.");
                                    }
                                    break;
                                case "end":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/host staff end (name of hostility map)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    } else {
                                        // /host <arg 0/staff> <arg 1/delete> ......... <arg n>
                                        String name = stringBuilder(args, 2);

                                        for (HostilityMatch match : activeHostilityGames) {
                                            if (match.getPlatform().getName().equalsIgnoreCase(name)) {
                                                match.endGame(true);
                                                return true;
                                            }
                                        }
                                        playerSender.sendMessage(ERROR_COLOR + "" + name + " isn't being played as an active map or it isn't found in our records of Hostility Platforms.");
                                    }
                                    break;
                                default:
                                    sendStaffUnknownCommand(playerSender);
                                    break;
                            }
                            break;
                        case "":
                            break;
                        default:
                            sendUnknownCommand(playerSender);
                            break;
                    }
                }
            }
        }

        //end of commands
        return true;
    }

    private void giveTools(Player playerSender) {
        //Create AREA AXE item
        ItemStack areaItemStack = new ItemStack(Material.WOOD_AXE, 1);
        ItemMeta areaItemMeta = areaItemStack.getItemMeta();
        areaItemMeta.setDisplayName(STANDARD_COLOR + "Area Axe");
        List<String> areaLoreList = new ArrayList<>(4);
        areaLoreList.add(STANDARD_COLOR + "Takes the area of a square.");
        areaLoreList.add(STANDARD_COLOR + "Left-Click to mark Position 1.");
        areaLoreList.add(STANDARD_COLOR + "Right-Click to mark Position 2.");
        areaLoreList.add(STANDARD_COLOR + "You must add the height when creating the area!");
        areaLoreList.add("ID:AREA_AXE");
        areaItemMeta.setLore(areaLoreList);
        areaItemStack.setItemMeta(areaItemMeta);

        //Create SINGLE SWORD item
        ItemStack singleItemStack = new ItemStack(Material.WOOD_SWORD, 1);
        ItemMeta singleItemMeta = singleItemStack.getItemMeta();
        singleItemMeta.setDisplayName(STANDARD_COLOR + "Single Sword");
        List<String> singleLoreList = new ArrayList<>(3);
        singleLoreList.add(STANDARD_COLOR + "Takes a single block.");
        singleLoreList.add(STANDARD_COLOR + "Left-Click to mark a block.");
        areaLoreList.add(STANDARD_COLOR + "Automatically adds 2 blocks of height!");
        singleLoreList.add("ID:SINGLE_SWORD");
        singleItemMeta.setLore(singleLoreList);
        singleItemStack.setItemMeta(singleItemMeta);

        //Create FINALIZE FLOWER item
        ItemStack finalizeItemStack = new ItemStack(Material.DOUBLE_PLANT, 1);
        ItemMeta finalizeItemMeta = finalizeItemStack.getItemMeta();
        finalizeItemMeta.setDisplayName(STANDARD_COLOR + "Finalize Flower");
        List<String> finalizeLoreList = new ArrayList<>(3);
        finalizeLoreList.add(STANDARD_COLOR + "Finalizes the zones created and creates a Hostility Platform.");
        finalizeLoreList.add(STANDARD_COLOR + "Left-Click to create Hostility Platform.");
        finalizeLoreList.add("ID:FINALIZE_FLOWER");
        finalizeItemMeta.setLore(finalizeLoreList);
        finalizeItemStack.setItemMeta(finalizeItemMeta);

        //Create UNDO RED item
        ItemStack undoItemStack = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
        ItemMeta undoItemMeta = undoItemStack.getItemMeta();
        undoItemMeta.setDisplayName(STANDARD_COLOR + "Undo Red");
        List<String> undoLoreList = new ArrayList<>(3);
        undoLoreList.add(STANDARD_COLOR + "Undo the most recent zone created by any tool.");
        undoLoreList.add(STANDARD_COLOR + "Left-Click to undo the most recent zone.");
        undoLoreList.add("ID:UNDO_RED");
        undoItemMeta.setLore(undoLoreList);
        undoItemStack.setItemMeta(undoItemMeta);

        //inventory player
        PlayerInventory inv = playerSender.getInventory();
        inv.clear();
        inv.setItem(1, areaItemStack);
        inv.setItem(2, singleItemStack);
        inv.setItem(4, finalizeItemStack);
        inv.setItem(6, undoItemStack);

    }

    private void sendHelp(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "------Hostility Help------");
//        Currently active: Hostility, Havoc
//        Current captor of Host: (clan name)
//        Current captor of Havoc: (clan name)
//        Next Hostility: 6PM EST
//        Next Havoc:  8PM EST
        String active = ChatColor.DARK_RED + "NONE";
        if(activeHostilityGames.size()>0)
            active = "";
       HostilityMatch[] matches = activeHostilityGames.toArray(new HostilityMatch[activeHostilityGames.size()]);
        for(int i = 0; i < matches.length; i++)
        {
            if(i != matches.length-1)
            active += matches[i].getPlatform().getName() + ", ";
            else
                active += matches[i].getPlatform().getName();
        }

        playerSender.sendMessage(COMMAND_COLOR + "Currently active: " + ChatColor.YELLOW + active);

        for(int i = 0; i < matches.length; i++)
        {
            if(matches[i].getCapturingClan() == null)
            {
                playerSender.sendMessage(COMMAND_COLOR + "Current Captor of " + matches[i].getPlatform().getName() + ": " + ChatColor.DARK_RED + "NONE");

            } else {
                playerSender.sendMessage(COMMAND_COLOR + "Current Captor of " + matches[i].getPlatform().getName() + ": " + ChatColor.YELLOW + matches[i].getCapturingClan().getName());

            }

        }

        playerSender.sendMessage(COMMAND_COLOR + "Next Hostility: " + ChatColor.YELLOW + "6PM PST");
    }

    private void sendStaffHelp(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "------Hostility Staff Help------");

        playerSender.sendMessage(COMMAND_COLOR + "/hostility staff create " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/hostility staff delete " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/hostility staff edit " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/hostility staff show " + ChatColor.YELLOW + "");
        playerSender.sendMessage(COMMAND_COLOR + "/hostility staff cancel " + ChatColor.YELLOW + "");
        playerSender.sendMessage(COMMAND_COLOR + "/hostility staff tools " + ChatColor.YELLOW + "");
        playerSender.sendMessage(COMMAND_COLOR + "/hostility staff start " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/hostility staff end " + ChatColor.YELLOW + "(name)");
    }

    private void sendUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Hostility. Use " + "/host" + ERROR_COLOR + " for help.");
    }

    private void sendStaffUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Hostility's Staff. Use " + "/host staff" + ERROR_COLOR + " for help.");
    }
}

