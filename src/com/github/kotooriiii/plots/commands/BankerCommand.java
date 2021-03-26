package com.github.kotooriiii.plots.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.npc.type.banker.BankerNPC;
import com.github.kotooriiii.npc.type.banker.BankerTrait;
import com.github.kotooriiii.plots.PlotManager;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;
import static com.github.kotooriiii.util.HelperMethods.stringBuilder;

public class BankerCommand implements CommandExecutor {

    private final static int BANKER_COST = 500;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player playerSender = (Player) sender;
            UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "clan" command
            if (cmd.getName().equalsIgnoreCase("banker")) {

                ShardPlotPlayer plotSenderPlayer = ShardPlotPlayer.wrap(playerUUID);
                PlayerPlot[] playerPlots = plotSenderPlayer.getPlotsOwned();

                //No arguments regarding this command
                if (args.length == 0) {
                    sendHelp(playerSender);
                    return false;
                } else if (args.length >= 1) {
                    //Sub-commands again however with proper argument.
                    String supply = stringBuilder(args, 1, " ");

                    PlotManager plotManager = LostShardPlugin.getPlotManager();
                    Plot standingOnPlot = plotManager.getStandingOnPlot(playerSender.getLocation());

                    switch (args[0].toLowerCase()) {
                        case "create":
                        case "add":
                            if (args.length < 2) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to add a banker? /banker add (name of banker)");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be standing on a plot in order to add a banker");
                                return false;
                            }

                            if (!(standingOnPlot instanceof PlayerPlot)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't add a banker on a staff plot.");
                                return false;
                            }

                            PlayerPlot playerPlot = (PlayerPlot) standingOnPlot;

                            if (!(playerPlot.isOwner(playerUUID) || playerPlot.isJointOwner(playerUUID))) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be the owner or co-owner in order to add a banker.");
                                return false;
                            }

                            if (!(playerPlot.isVendor() && playerPlot.isTown())) {
                                playerSender.sendMessage(ERROR_COLOR + "Plot must be BOTH a Town and a Vendor plot in order to add a banker.");
                                return false;
                            }

                            String bankerName = supply;

                            if (bankerName.length() > 20) {
                                playerSender.sendMessage(ERROR_COLOR + "The name of the Banker is too long! Try shortening it by " + (bankerName.length() - 20) + " character(s).");
                                return false;
                            }


                                   /*
                            Is staff
                            On player plot
                            bankername appropiate
                             */

                            final ArrayList<NPC> bankers = playerPlot.getBankers();


                            boolean hasBankersAvailable = bankers.size() < playerPlot.getPurchasedBankers();


                            //We have less removeBankersList than we bought, so this is FREE
                            if (hasBankersAvailable) {

                            } else {

                                if(bankers.size() >= playerPlot.getMaxBankers())
                                {
                                    playerSender.sendMessage(ERROR_COLOR + "You have reached the max amount of removeBankersList.");
                                    return false;
                                }

                                if (playerPlot.getBalance() < BANKER_COST) {
                                    playerSender.sendMessage(ERROR_COLOR + "Insufficient funds. You must have at least " + BANKER_COST + "g in your plot’s balance to add a banker.");
                                    return false;
                                }
                            }

                            for (NPC createBankerNPC : bankers) {
                                BankerTrait bankerTrait = createBankerNPC.getTrait(BankerTrait.class);
                                if (bankerTrait.getBankerName().equalsIgnoreCase(bankerName)) {
                                    playerSender.sendMessage(ERROR_COLOR + "The name you chose is already taken by another Banker.");
                                    return false;
                                }
                            }

                            if (!hasBankersAvailable) {
                                playerPlot.withdraw(BANKER_COST);
                                playerPlot.setPurchasedBankers(playerPlot.getPurchasedBankers()+1);
                            }


                            playerSender.sendMessage(ChatColor.GOLD + "You have added the Banker '" + supply + "' to handle finances in your town.");
                            BankerNPC banker = new BankerNPC(supply);
                            banker.spawn(playerPlot, playerSender.getLocation());
                            break;
                        case "remove":
                        case "delete":
                            if (args.length < 2) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to remove a banker? /banker remove (name of banker)");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be standing on a plot in order to remove a banker");
                                return false;
                            }

                            if (!(standingOnPlot instanceof PlayerPlot)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't remove a banker on a staff plot.");
                                return false;
                            }

                            PlayerPlot removeBankerPlot = (PlayerPlot) standingOnPlot;

                            if (!(removeBankerPlot.isOwner(playerUUID) || removeBankerPlot.isJointOwner(playerUUID))) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be the owner or co-owner in order to remove a banker.");
                                return false;
                            }


                            String bankerNameRemove = supply;

                            /*
                            Is staff
                            On player plot
                            bankername appropiate
                             */

                            final ArrayList<NPC> removeBankersList = removeBankerPlot.getBankers();

                            for (NPC removeNPC : removeBankersList) {
                                BankerTrait bankerTrait = removeNPC.getTrait(BankerTrait.class);
                                if (bankerTrait.getBankerName().equalsIgnoreCase(bankerNameRemove)) {
                                    playerSender.sendMessage(ChatColor.GOLD + "You have removed the Banker '" + supply + "' from your town.");
                                    bankerTrait.dieSomehow();
                                    CitizensAPI.getNPCRegistry().deregister(removeNPC);
                                    return true;
                                }
                            }

                            playerSender.sendMessage(ERROR_COLOR + "We could not find a Banker by the name of '" + supply + "' in your town.");
                            return false;
                        default:
                            sendHelp(playerSender);
                            break;

                    }
                }
            }
        }
        return true;
    }

    private void sendHelp(Player player) {
        String prefix = "/banker";
        player.sendMessage(ChatColor.GOLD + "-Banker Help-");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "add" + " " + ChatColor.YELLOW + "(banker name)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "remove" + " " + ChatColor.YELLOW + "(banker name)");
    }
}
