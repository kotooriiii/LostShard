package com.github.kotooriiii.bank.commands;

import com.github.kotooriiii.npc.type.banker.BankerNPC;
import com.github.kotooriiii.npc.type.banker.BankerTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.stringBuilder;

public class BankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("bank")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    playerSender.performCommand("chest");
                    return true;
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
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/bank staff create (name)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    }
                                    // /host <arg 0/staff> <arg 1/create> ......... <arg n>
                                    String nameCreate = stringBuilder(args, 2, " ");
                                    if(nameCreate.length() > 14)
                                    {
                                        playerSender.sendMessage(ERROR_COLOR + "You can't have a name that big! Shrink it or else the game crashes.");
                                        return true;
                                    }
                                    for (NPC createBankerNPC : BankerNPC.getAllBankerNPC()) {
                                        BankerTrait bankerTrait = createBankerNPC.getTrait(BankerTrait.class);
                                        if (bankerTrait.getBankerName().equalsIgnoreCase(nameCreate)) {
                                            playerSender.sendMessage(ERROR_COLOR + "The name you chose is already taken by another Banker.");
                                            return true;
                                        }
                                    }
                                    playerSender.sendMessage(STANDARD_COLOR + "You have hired " + BANKER_COLOR + nameCreate + STANDARD_COLOR + " to handle finances in this location.");
                                    BankerNPC banker = new BankerNPC(nameCreate);
                                    banker.spawn(playerSender.getLocation());
                                    break;
                                case "delete":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/bank delete (name)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    }
                                    // /host <arg 0/staff> <arg 1/create> ......... <arg n>
                                    String nameDelete = stringBuilder(args, 2, " ");
                                    for (NPC deleteBankerNPC : BankerNPC.getAllBankerNPC()) {
                                        BankerTrait bankerTrait = deleteBankerNPC.getTrait(BankerTrait.class);

                                        if (bankerTrait.getBankerName().equalsIgnoreCase(nameDelete)) {
                                            playerSender.sendMessage(STANDARD_COLOR + "You have fired " + BANKER_COLOR + bankerTrait.getBankerName() + STANDARD_COLOR + " for stealing gold from players' chests.");
                                            CitizensAPI.getNPCRegistry().deregister(deleteBankerNPC);
                                            return true;
                                        }
                                    }
                                    playerSender.sendMessage(ERROR_COLOR + "We could not find " + BANKER_COLOR + nameDelete + ERROR_COLOR + " in our records of Bankers.");
                                    break;
                                case "setspawn":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/bank staff setspawn (name)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    }
                                    // /host <arg 0/staff> <arg 1/create> ......... <arg n>
                                    String nameSetSpawn = stringBuilder(args, 2, " ");
                                    for (NPC setspawnBankerNPC : BankerNPC.getAllBankerNPC()) {
                                        BankerTrait bankerTrait = setspawnBankerNPC.getTrait(BankerTrait.class);

                                        if (bankerTrait.getBankerName().equalsIgnoreCase(nameSetSpawn)) {
                                            playerSender.sendMessage(STANDARD_COLOR + "You have set a new location for the banker to take transactions. Make some money, " + BANKER_COLOR + bankerTrait.getBankerName() + STANDARD_COLOR + "!");
                                            bankerTrait.setBankerLocation(playerSender.getLocation());
                                            return true;
                                        }
                                    }
                                    playerSender.sendMessage(ERROR_COLOR + "We could not find " + BANKER_COLOR + nameSetSpawn + ERROR_COLOR + " in our records of Bankers.");
                                    break;
                                case "show":
                                    playerSender.sendMessage(STANDARD_COLOR + "-=[Bankers Active]=-");
                                    for (NPC showBankerNPC : BankerNPC.getAllBankerNPC()) {
                                        BankerTrait bankerTrait = showBankerNPC.getTrait(BankerTrait.class);

                                        int x = showBankerNPC.getStoredLocation().getBlockX();
                                        int y = showBankerNPC.getStoredLocation().getBlockY();
                                        int z = showBankerNPC.getStoredLocation().getBlockZ();
                                        BaseComponent[] tc = new ComponentBuilder(BANKER_COLOR + "" + bankerTrait.getBankerName() + STANDARD_COLOR + " is positioned at x:" + STANDARD_COLOR + x + ", y:" + y + ", z:" + z + ".")
                                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "Teleport to " + BANKER_COLOR + bankerTrait.getBankerName()+ STANDARD_COLOR + ".").create()))
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
                        case "help":
                            sendHelp(playerSender);
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

    public void sendHelp(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "------Bank Help------");
        playerSender.sendMessage(COMMAND_COLOR + "/chest " + ChatColor.YELLOW + "");
        playerSender.sendMessage(COMMAND_COLOR + "/balance" + ChatColor.YELLOW + "");
        playerSender.sendMessage(COMMAND_COLOR + "/withdraw " + ChatColor.YELLOW + "(amount)");
        playerSender.sendMessage(COMMAND_COLOR + "/deposit " + ChatColor.YELLOW + "(amount)");
    }

    public void sendStaffHelp(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "------Bank Staff Help------");

        playerSender.sendMessage(COMMAND_COLOR + "/bank staff create " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/bank staff delete " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/bank staff setspawn " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/bank staff show " + ChatColor.YELLOW + "");
    }

    private void sendUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Bank. Use " + "/bank" + ERROR_COLOR + " to show a list of commands used near a banking agent.");
    }

    private void sendStaffUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Bank's Staff. Use " + "/bank staff" + ERROR_COLOR + " for help.");
    }
}


