package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.channels.ChannelManager;
import com.github.kotooriiii.plots.PlotManager;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.listeners.SignChangeListener;
import com.github.kotooriiii.plots.struct.ArenaPlot;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.plots.struct.StaffPlot;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Staff;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.stringBuilder;

public class PlotCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            Player playerSender = (Player) sender;
            UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "clan" command
            if (cmd.getName().equalsIgnoreCase("plot")) {

                ShardPlotPlayer plotSenderPlayer = ShardPlotPlayer.wrap(playerUUID);
                PlayerPlot[] playerPlots = plotSenderPlayer.getPlotsOwned();

                //No arguments regarding this command
                if (args.length == 0) {
                    sendPage(1, playerSender);
                    return false;
                } else if (args.length >= 1) {
                    //Sub-commands again however with proper argument.
                    String supply = stringBuilder(args, 1, " ");
                    Bank bank = LostShardPlugin.getBankManager().wrap(playerUUID);
                    double currentCurrency = bank.getCurrency();
                    DecimalFormat df = new DecimalFormat("#.##");

                    PlotManager plotManager = LostShardPlugin.getPlotManager();
                    Plot standingOnPlot = plotManager.getStandingOnPlot(playerSender.getLocation());

                    switch (args[0].toLowerCase()) {
                        case "help":
                            //Send the help page and return.
                            sendHelp(playerSender);
                            break;
                        case "create":
                            if (args.length == 1)
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to create your own plot? /plot create (name)");
                            else if (plotSenderPlayer.hasReachedMaxPlots())
                                playerSender.sendMessage(ERROR_COLOR + "You've reached the max amount of plots you can own.");
                            else if (plotManager.hasNearbyPlots(playerSender))
                                playerSender.sendMessage(ERROR_COLOR + "There are other plot(s) nearby. \nYou must be a minimum of " + Plot.MINIMUM_PLOT_CREATE_RANGE + " block(s) away from player plots and " + Plot.MINIMUM_PLOT_STAFF_CREATE_RANGE + " block(s) away from staff plots.");
                            else if (supply.length() > 16)
                                playerSender.sendMessage(ERROR_COLOR + "The name can not exceed 16 characters.");
                            else if (plotManager.isStaffPlotName(supply))
                                playerSender.sendMessage(ERROR_COLOR + "This plot name has its place in history already. Create your own history!");
                            else if (plotManager.isPlot(supply))
                                playerSender.sendMessage(ERROR_COLOR + "That plot name has already been taken.");
                            else if (!hasCreatePlotCost(playerSender)) {
                                //The message is already taken care of.
                            } else {
                                createPlot(playerSender, supply);
                            }
                            break;
                        case "disband":
                            if (args.length != 1) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to disband your plot? /plot disband");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You are not standing on any plot.");
                                return false;
                            }

                            if (!standingOnPlot.getType().equals(PlotType.PLAYER)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't disband staff plots.");
                                return false;
                            }

                            PlayerPlot disbandPlot = (PlayerPlot) standingOnPlot;

                            if (!disbandPlot.isOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't disband a plot that you don't own.");
                                return false;
                            }

                            disbandPlot(playerSender);

                            break;
                        case "friend":
                        case "f":
                            if (args.length == 1) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to friend a player to your plot? /plot friend (username)");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You are not standing on any plot.");
                                return false;
                            }

                            if (!standingOnPlot.getType().equals(PlotType.PLAYER)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't friend players to staff plots.");
                                return false;
                            }

                            PlayerPlot friendPlot = (PlayerPlot) standingOnPlot;

                            if (!friendPlot.isOwner(playerUUID) && !friendPlot.isJointOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't friend players to this plot.");
                                return false;
                            }

                            OfflinePlayer friendPlayer = Bukkit.getOfflinePlayer(supply);
                            UUID friendUUID = friendPlayer.getUniqueId();

                            if (!friendPlayer.hasPlayedBefore() && !friendPlayer.isOnline()) {
                                playerSender.sendMessage(ERROR_COLOR + "That player does not exist.");
                                return false;
                            }

                            if (friendPlot.isFriend(friendUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + friendPlayer.getName() + " is already a friend. " + "Did you mean to unfriend a player to your plot? /plot unfriend (username)");
                            } else {
                                friendPlot.sendToMembers(ChatColor.GOLD + playerSender.getName() + " added " + friendPlayer.getName() + " to your plot as a friend.");
                                friendPlot.addFriend(friendUUID);
                            }
                            break;
                        case "unfriend":
                        case "unf":
                            if (args.length == 1) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to unfriend a player to your plot? /plot unfriend (username)");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You are not standing on any plot.");
                                return false;
                            }

                            if (!standingOnPlot.getType().equals(PlotType.PLAYER)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't unfriend players to staff plots.");
                                return false;
                            }

                            PlayerPlot unfriendPlot = (PlayerPlot) standingOnPlot;

                            if (!unfriendPlot.isOwner(playerUUID) && !unfriendPlot.isJointOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't unfriend players to this plot.");
                                return false;
                            }

                            OfflinePlayer unfriendPlayer = Bukkit.getOfflinePlayer(supply);
                            UUID unfriendUUID = unfriendPlayer.getUniqueId();

                            if (!unfriendPlayer.hasPlayedBefore() && !unfriendPlayer.isOnline()) {
                                playerSender.sendMessage(ERROR_COLOR + "That player does not exist.");
                                return false;
                            }

                            if (unfriendPlot.isFriend(unfriendUUID)) {
                                unfriendPlot.sendToMembers(ChatColor.GOLD + playerSender.getName() + " removed " + unfriendPlayer.getName() + " from being a plot friend.");
                                unfriendPlot.removeFriend(unfriendUUID);
                            } else {
                                playerSender.sendMessage(ERROR_COLOR + unfriendPlayer.getName() + " is not a friend. " + "Did you mean to friend a player to your plot? /plot friend (username)");
                            }
                            break;
                        case "co-owner":
                        case "coowner":
                        case "coown":
                        case "co-own":
                        case "co":
                            if (args.length == 1) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to promote a player to co-owner on your plot? /plot co (username)");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You are not standing on any plot.");
                                return false;
                            }

                            if (!standingOnPlot.getType().equals(PlotType.PLAYER)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't co-owner players on staff plots.");
                                return false;
                            }

                            PlayerPlot jointOwnerPlot = (PlayerPlot) standingOnPlot;

                            if (!jointOwnerPlot.isOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't co-own players on this plot.");
                                return false;
                            }

                            OfflinePlayer jointPlayer = Bukkit.getOfflinePlayer(supply);
                            UUID jointOwnerUUID = jointPlayer.getUniqueId();

                            if (!jointPlayer.hasPlayedBefore() && !jointPlayer.isOnline()) {
                                playerSender.sendMessage(ERROR_COLOR + "That player does not exist.");
                                return false;
                            }

                            if (jointOwnerPlot.isJointOwner(jointOwnerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + jointPlayer.getName() + " is already a co-owner. " + "Did you mean to demote a player from co-owner on your plot? /plot unco (username)");

                            } else {
                                jointOwnerPlot.addJointOwner(jointOwnerUUID);
                                jointOwnerPlot.sendToMembers(ChatColor.GOLD + playerSender.getName() + " added " + jointPlayer.getName() + " to your plot as a co-owner.");
                            }
                            break;
                        case "unco-owner":
                        case "uncoowner":
                        case "uncoown":
                        case "unco-own":
                        case "unco":
                            if (args.length == 1) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to demote a player from co-owner on your plot? /plot unco (username)");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You are not standing on any plot.");
                                return false;
                            }

                            if (!standingOnPlot.getType().equals(PlotType.PLAYER)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't unco-owner players on staff plots.");
                                return false;
                            }

                            PlayerPlot unjointOwnerPlot = (PlayerPlot) standingOnPlot;

                            if (!unjointOwnerPlot.isOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't unco-own players on this plot.");
                                return false;
                            }

                            OfflinePlayer unjointPlayer = Bukkit.getOfflinePlayer(supply);
                            UUID unjointOwnerUUID = unjointPlayer.getUniqueId();

                            if (!unjointPlayer.hasPlayedBefore() && !unjointPlayer.isOnline()) {
                                playerSender.sendMessage(ERROR_COLOR + "That player does not exist.");
                                return false;
                            }

                            if (unjointOwnerPlot.isJointOwner(unjointOwnerUUID)) {
                                unjointOwnerPlot.removeJointOwner(unjointOwnerUUID);
                                unjointOwnerPlot.sendToMembers(ChatColor.GOLD + playerSender.getName() + " removed " + unjointPlayer.getName() + " from being a plot co-owner.");
                            } else {
                                playerSender.sendMessage(ERROR_COLOR + unjointPlayer.getName() + " is not a co-owner. " + "Did you mean to promote a player to co-owner on your plot? /plot co (username).");
                            }
                            break;
                        case "expand":
                            if (args.length != 1) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to expand your plot? /plot expand");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You are not standing on any plot.");
                                return false;
                            }

                            if (!standingOnPlot.getType().equals(PlotType.PLAYER)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't expand staff plots.");
                                return false;
                            }

                            PlayerPlot expandPlot = (PlayerPlot) standingOnPlot;

                            if (!expandPlot.isOwner(playerUUID) && !expandPlot.isJointOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't expand this plot.");
                                return false;
                            }

                            if (!expandPlot.isExpandable()) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't expand onto other plots or be too close to staff plots.");
                                return false;
                            }

                            if (expandPlot.getBalance() < expandPlot.getExpandCost()) {
                                playerSender.sendMessage(ERROR_COLOR + "You don't have the money to expand your plot. You need " + df.format(expandPlot.getExpandCost()) + " to expand to the next size.");
                                return false;
                            }

                            expandPlot.expand();
                            expandPlot.sendToMembers(ChatColor.GOLD + playerSender.getName() + " expanded the plot to size " + expandPlot.getRadius() + ".");
                            break;
                        case "deposit":
                            if (args.length != 2) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to deposit currency into your plot? /plot deposit (amount)");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You are not standing on any plot.");
                                return false;
                            }

                            if (!standingOnPlot.getType().equals(PlotType.PLAYER)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't deposit onto staff plots.");
                                return false;
                            }

                            PlayerPlot depositPlot = (PlayerPlot) standingOnPlot;

                            if (!depositPlot.isOwner(playerUUID) && !depositPlot.isJointOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't deposit into this plot's funds.");
                                return false;
                            }

                            //Is this a number
                            String possibleMoney = args[1];
                            if (!NumberUtils.isNumber(possibleMoney)) {
                                playerSender.sendMessage(ERROR_COLOR + "You need to provide a number to deposit into the plot funds.");
                                return false;
                            }

                            double depositRaw = Double.parseDouble(args[1]);
                            BigDecimal deposit = new BigDecimal(depositRaw).setScale(2, BigDecimal.ROUND_HALF_UP);

                            if (currentCurrency < deposit.doubleValue()) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't deposit more than your current balance.");
                                return false;
                            }

                            bank.setCurrency(currentCurrency - deposit.doubleValue());
                            depositPlot.deposit(deposit.doubleValue());
                            depositPlot.sendToMembers(ChatColor.GOLD + playerSender.getName() + " deposited " + deposit.doubleValue() + " gold into the plot funds.");
                            break;
                        case "withdraw":
                            if (args.length != 2) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to withdraw currency from your plot? /plot withdraw (amount)");
                                return false;
                            }
                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You are not standing on any plot.");
                                return false;
                            }

                            if (!standingOnPlot.getType().equals(PlotType.PLAYER)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't withdraw from staff plots.");
                                return false;
                            }

                            PlayerPlot withdrawPlot = (PlayerPlot) standingOnPlot;

                            if (!withdrawPlot.isOwner(playerUUID) && !withdrawPlot.isJointOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't withdraw from this plot's funds.");
                                return false;
                            }

                            //Is this a number
                            String possibleWithdraw = args[1];
                            if (!NumberUtils.isNumber(possibleWithdraw)) {
                                playerSender.sendMessage(ERROR_COLOR + "You need to provide a number to withdraw from the plot funds.");
                                return false;
                            }

                            double withdrawRaw = Double.parseDouble(args[1]);
                            BigDecimal withdraw = new BigDecimal(withdrawRaw).setScale(2, BigDecimal.ROUND_HALF_UP);

                            if (withdrawPlot.getBalance() < withdraw.doubleValue()) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't withdraw more than your current funds.");
                                return false;
                            }

                            bank.setCurrency(currentCurrency + withdraw.doubleValue());
                            withdrawPlot.withdraw(withdraw.doubleValue());

                            withdrawPlot.sendToMembers(ChatColor.GOLD + playerSender.getName() + " withdrew " + withdraw.doubleValue() + " gold from the plot funds.");
                            break;
                        case "info":
                            if (args.length != 1) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to check info on the possible plot you are standing on? /plot info");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You are not standing on any plot.");
                                return false;
                            }

                            String info = standingOnPlot.info(playerSender);
                            playerSender.sendMessage(info);

                            break;
                        case "upgrade":
                            if (args.length != 2) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to upgrade the plot you're standing on? /plot upgrade [town/dungeon]");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You are not standing on any plot.");
                                return false;
                            }

                            if (!standingOnPlot.getType().equals(PlotType.PLAYER)) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't upgrade staff plots.");
                                return false;
                            }

                            PlayerPlot upgradePlot = (PlayerPlot) standingOnPlot;

                            if (!upgradePlot.getOwnerUUID().equals(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be the owner of the plot in order to upgrade.");
                                return false;
                            }

                            if (args[1].equalsIgnoreCase("town")) {
                                if (plotSenderPlayer.hasTownPlot()) {
                                    playerSender.sendMessage(ERROR_COLOR + "You may only own up to 1 town.");
                                    return false;
                                }

                                if (upgradePlot.getRadius() < 50) {
                                    playerSender.sendMessage(ERROR_COLOR + "Your plot must be at least size 50 to upgrade to a town.");
                                    return false;

                                }


                                if (upgradePlot.getBalance() < 1000) {
                                    playerSender.sendMessage(ERROR_COLOR + "You need 1,000 gold to upgrade your plot to a Town.”.");
                                    return false;
                                }
                                upgradePlot.withdraw(1000);
                                upgradePlot.setTown(true);
                                playerSender.sendMessage(ChatColor.GOLD + "Your plot is now a Town.");
                            } else if (args[1].equalsIgnoreCase("dungeon")) {
                                RankPlayer rank = RankPlayer.wrap(playerUUID);
                                int dungeonsNum = rank.getRankType().getDungeonsNum();
                                if (plotSenderPlayer.getDungeonPlots().length == dungeonsNum) {
                                    playerSender.sendMessage(ERROR_COLOR + "You may only own up to " + dungeonsNum + " dungeon(s).");
                                    return false;
                                }

                                if (upgradePlot.getBalance() < 200) {
                                    playerSender.sendMessage(ERROR_COLOR + "You need 200 gold to upgrade your plot to a Dungeon.");
                                    return false;
                                }
                                upgradePlot.withdraw(200);
                                upgradePlot.setDungeon(true);
                                playerSender.sendMessage(ChatColor.GOLD + "Your plot is now a Dungeon. Mob spawners are now enabled on your plot.");
                            } else {
                                playerSender.sendMessage(ERROR_COLOR + args[1] + " is not a valid upgrade option.");
                                return false;
                            }


                            break;
                        case "staff":

                            if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be a staff member in order to execute admin-level plot commands.");
                                return false;
                            }

                            if (args.length == 1) {
                                sendStaffHelp(playerSender);
                                return false;
                            }

                            String staffSupply = stringBuilder(args, 2, " ");

                            switch (args[1]) {
                                case "create":

                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You didn't provide enough arguments. /plot staff create (name)");
                                        return false;
                                    }

                                    if (staffPlotCreator.containsKey(playerUUID)) {
                                        playerSender.sendMessage(ERROR_COLOR + "You are already editing!");
                                        return false;
                                    }
                                    playerSender.sendMessage(STANDARD_COLOR + "You have enabled editing mode.");
                                    staffPlotCreator.put(playerUUID, new Object[]{null, staffSupply});
                                    giveTools(playerSender);
                                    break;
                                case "disband":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You didn't provide enough arguments. /plot staff disband (name)");
                                        return false;
                                    }

                                    Plot disbandingPlot = plotManager.getPlot(staffSupply);

                                    if (disbandingPlot == null) {
                                        playerSender.sendMessage(ERROR_COLOR + "The plot you are looking for does not exist.");
                                        return false;
                                    }

                                    playerSender.sendMessage(STANDARD_COLOR + "You have disbanded " + disbandingPlot.getName() + ".");
                                    if (disbandingPlot.getType().equals(PlotType.PLAYER)) {
                                        playerSender.sendMessage(STANDARD_COLOR + "The plot was a player-made plot.");
                                    }
                                    plotManager.removePlot(disbandingPlot);
                                    break;

                                case "setspawn":
                                    if (args.length != 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You didn't provide enough arguments. /plot staff setspawn");
                                        return false;
                                    }

                                    if (!plotManager.isStandingOnPlot(playerSender)) {
                                        playerSender.sendMessage(ERROR_COLOR + "You must be standing on a plot in order to set a spawn.");
                                        return false;
                                    }

                                    if (!standingOnPlot.getType().isStaff()) {
                                        playerSender.sendMessage(ERROR_COLOR + "This must be a staff-owned plot. (For the banmatch/moneymatch spawns refer to: setspawnA and setspawnB)");
                                        return false;
                                    }


                                    StaffPlot setspawnPlot = (StaffPlot) standingOnPlot;

                                    setspawnPlot.setSpawn(playerSender.getLocation());

                                    if (setspawnPlot.getName().equalsIgnoreCase("order"))
                                        setspawnPlot.getSpawn().getWorld().setSpawnLocation(setspawnPlot.getSpawn());

                                    playerSender.sendMessage(ChatColor.GOLD + "The spawn for " + setspawnPlot.getName() + " has been set to where you are standing.");

                                    break;
                                case "setspawna":
                                    if (args.length != 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You didn't provide enough arguments. /plot staff setspawnA (name)");
                                        return false;
                                    }

                                    if (!plotManager.isStandingOnPlot(playerSender)) {
                                        playerSender.sendMessage(ERROR_COLOR + "You must be standing on a plot in order to set a spawnA.");
                                        return false;
                                    }

                                    if (!standingOnPlot.getType().equals(PlotType.STAFF_ARENA)) {
                                        playerSender.sendMessage(ERROR_COLOR + "This must be a staff-owned arena plot with the name(s): Arena. (For the chaos/order spawn refer to: setspawn)");
                                        return false;
                                    }

                                    ArenaPlot arenaPlotA = (ArenaPlot) standingOnPlot;

                                    arenaPlotA.setSpawnA(playerSender.getLocation());
                                    playerSender.sendMessage(ChatColor.GOLD + "The spawn A for " + arenaPlotA.getName() + " has been set to where you are standing.");
                                    break;
                                case "setspawnb":
                                    if (args.length != 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You didn't provide enough arguments. /plot staff setspawnB (name)");
                                        return false;
                                    }

                                    if (!plotManager.isStandingOnPlot(playerSender)) {
                                        playerSender.sendMessage(ERROR_COLOR + "You must be standing on a plot in order to set a spawnB.");
                                        return false;
                                    }

                                    if (!standingOnPlot.getType().equals(PlotType.STAFF_ARENA)) {
                                        playerSender.sendMessage(ERROR_COLOR + "This must be a staff-owned arena plot with the name(s): Arena. (For the chaos/order spawn refer to: setspawn)");
                                        return false;
                                    }
                                    ArenaPlot arenaPlotB = (ArenaPlot) standingOnPlot;

                                    arenaPlotB.setSpawnB(playerSender.getLocation());
                                    playerSender.sendMessage(ChatColor.GOLD + "The spawn B for " + arenaPlotB.getName() + " has been set to where you are standing.");
                                    break;
                                case "tools":
                                    if (staffPlotCreator.containsKey(playerUUID))
                                        giveTools(playerSender);
                                    else
                                        playerSender.sendMessage(ERROR_COLOR + "You are not creating a staff plot. Visit: /plot staff");
                                    break;
                                default:
                                    sendStaffUnknownCommand(playerSender);
                                    break;
                            }
                            break;
                        default: //Not a premade command. This means something like: /clans chocolatebunnies white

                            String numberString = args[0];
                            if (!NumberUtils.isNumber(numberString) || numberString.contains(".")) {

                                playerSender.sendMessage(ERROR_COLOR + "You must use a positive integer for a page.");
                                return false;
                            }

                            int page = Integer.parseInt(numberString);

                            if (page <= 0) {
                                playerSender.sendMessage(ERROR_COLOR + "You must use a positive integer for a page.");
                                return false;
                            }
                            sendPage(page, playerSender);
                            break;
                    }

                }


            }

        }
        return false;
    }//end of commands

    private void sendStaffHelp(Player player) {
        String prefix = "/plot staff";
        player.sendMessage(ChatColor.GOLD + "-Plot Staff Help-");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "disband (name)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "create (name)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "setspawn");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "tools");
        player.sendMessage(ChatColor.GOLD + "For Arena:");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "setspawna");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "setspawnb");
    }

    public static void giveTools(Player playerSender) {
        //Create AREA AXE item
        ItemStack areaItemStack = new ItemStack(Material.DIAMOND_AXE, 1);
        ItemMeta areaItemMeta = areaItemStack.getItemMeta();
        areaItemMeta.setDisplayName(STANDARD_COLOR + "Plot Area Axe");
        List<String> areaLoreList = new ArrayList<>(4);
        areaLoreList.add(STANDARD_COLOR + "Takes the area of a square.");
        areaLoreList.add(STANDARD_COLOR + "Left-Click to mark Position 1.");
        areaLoreList.add(STANDARD_COLOR + "Right-Click to mark Position 2.");
        areaLoreList.add(STANDARD_COLOR + "Height is prematurely constructed between 0-256!");
        areaLoreList.add("ID:PLOT_AREA_AXE");
        areaItemMeta.setLore(areaLoreList);
        areaItemStack.setItemMeta(areaItemMeta);

        //Create FINALIZE FLOWER item
        ItemStack finalizeItemStack = new ItemStack(Material.SUNFLOWER, 1);
        ItemMeta finalizeItemMeta = finalizeItemStack.getItemMeta();
        finalizeItemMeta.setDisplayName(STANDARD_COLOR + "Plot Finalize Flower");
        List<String> finalizeLoreList = new ArrayList<>(3);
        finalizeLoreList.add(STANDARD_COLOR + "Finalizes the area of the plot.");
        finalizeLoreList.add(STANDARD_COLOR + "Left-Click to create an admin plot.");
        finalizeLoreList.add("ID:PLOT_FINALIZE_FLOWER");
        finalizeItemMeta.setLore(finalizeLoreList);
        finalizeItemStack.setItemMeta(finalizeItemMeta);

        //inventory player
        PlayerInventory inv = playerSender.getInventory();
        inv.clear();
        inv.setItem(1, areaItemStack);
        inv.setItem(4, finalizeItemStack);
    }

    private void sendStaffUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Plots Staff. For more help use: /plot staff");
    }

    private void sendUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "Unknown command.");
    }

    private void disbandPlot(Player playerSender) {
        Bank bank = LostShardPlugin.getBankManager().wrap(playerSender.getUniqueId());
        PlayerPlot plot = (PlayerPlot) LostShardPlugin.getPlotManager().getStandingOnPlot(playerSender.getLocation());

        for (Stat stat : Stat.getStatMap().values()) {
            Location loc = stat.getSpawn();
            if (loc == null)
                continue;

            if (plot.contains(loc)) {

                stat.setSpawn(null);
                Player player = Bukkit.getPlayer(stat.getPlayerUUID());
                if (player == null)
                    continue;
                player.sendMessage(ERROR_COLOR + "Your spawnpoint has been reset because your bed has been broken.");
            }
        }

        for(Location loc : SignChangeListener.getBuildChangeLocations())
        {
            if(loc==null)
                continue;

            if(plot.contains(loc))
            {
                SignChangeListener.remove(loc);
            }
        }

        double currentCurrency = bank.getCurrency();

        DecimalFormat df = new DecimalFormat("#.##");
        double refund = plot.disband();
        double funds = plot.getBalance();

        bank.setCurrency(currentCurrency + refund + funds);
        plot.sendToMembers(ChatColor.GOLD + playerSender.getName() + " disbanded " + plot.getName() + ".");
        playerSender.sendMessage(ChatColor.GOLD + "You have been refunded " + df.format(refund) + " gold for " + (PlayerPlot.REFUND_RATE * 100) + "% of the plot's value.");
        playerSender.sendMessage(ChatColor.GOLD + "You have been given the remaining funds from your plot's balance (" + df.format(funds) + ").");

    }

    public boolean hasCreatePlotCost(Player player) {
        Bank bank = LostShardPlugin.getBankManager().wrap(player.getUniqueId());
        double currentCurrency = bank.getCurrency();

        if (currentCurrency < PlayerPlot.CREATE_COST) {
            player.sendMessage(ERROR_COLOR + "You do not have enough funds to create a plot.");
            return false;
        }

        ItemStack[] ingredients = new ItemStack[]{new ItemStack(Material.DIAMOND, 1)};
        if (!hasIngredients(player, ingredients)) {
            return false;
        }

        return true;
    }

    public void createPlot(Player player, String name) {
        Bank bank = LostShardPlugin.getBankManager().wrap(player.getUniqueId());
        ItemStack[] ingredients = new ItemStack[]{new ItemStack(Material.DIAMOND, 1)};

        bank.setCurrency(bank.getCurrency() - PlayerPlot.CREATE_COST);
        removeIngredients(player, ingredients);


        PlayerPlot playerPlot = new PlayerPlot(name, player.getUniqueId(), player.getLocation());
        LostShardPlugin.getPlotManager().addPlot(playerPlot, true);
        player.sendMessage(ChatColor.GOLD + "You have created the plot \"" + playerPlot.getName() + "\", it cost $" + PlayerPlot.CREATE_COST + " and 1 diamond.");
    }

    private void sendHelp(Player player) {
        String prefix = "/plot";
        player.sendMessage(ChatColor.GOLD + "-Plot Help-");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "create" + " " + ChatColor.YELLOW + "(name)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "disband");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "friend" + " " + ChatColor.YELLOW + "(username)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "co" + " " + ChatColor.YELLOW + "(username)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "deposit" + " " + ChatColor.YELLOW + "(amount)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "withdraw" + " " + ChatColor.YELLOW + "(amount)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "upgrade" + " " + ChatColor.YELLOW + "town");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "upgrade" + " " + ChatColor.YELLOW + "dungeon");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "info");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "help");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "[page]");


    }

    public boolean hasIngredients(Player player, ItemStack[] ingredients) {
        ArrayList<HashMap<Integer, Integer>> pendingRemovalItems = new ArrayList<>();

        boolean hasIngredients = true;
        for (ItemStack ingredient : ingredients) {
            HashMap<Integer, Integer> indeces = hasIngredient(player, ingredient);
            if (indeces == null) {
                hasIngredients = false;
                continue;
            }
            pendingRemovalItems.add(indeces);
        }

        return hasIngredients;
    }

    public boolean removeIngredients(Player player, ItemStack[] ingredients) {
        ArrayList<HashMap<Integer, Integer>> pendingRemovalItems = new ArrayList<>();
        PlayerInventory currentInventory = player.getInventory();

        boolean hasIngredients = true;
        for (ItemStack ingredient : ingredients) {
            HashMap<Integer, Integer> indeces = hasIngredient(player, ingredient);
            if (indeces == null) {
                hasIngredients = false;
                continue;
            }
            pendingRemovalItems.add(indeces);
        }

        if (hasIngredients) {
            for (HashMap<Integer, Integer> indeces : pendingRemovalItems) {
                for (Map.Entry entry : indeces.entrySet()) {
                    int key = (Integer) entry.getKey();
                    ItemStack itemStack = currentInventory.getItem(key);
                    int value = (Integer) entry.getValue();
                    if (value == 0)
                        currentInventory.setItem(key, null);
                    else {
                        itemStack.setAmount(value);
                        currentInventory.setItem(key, itemStack);
                    }
                }
            }
        }

        return hasIngredients;
    }

    public HashMap<Integer, Integer> hasIngredient(Player player, ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        int amountRequired = itemStack.getAmount();
        Material materialType = itemStack.getType();

        int counterMoney = 0;

        //Inventory slot index , int amount
        HashMap<Integer, Integer> hashmap = new HashMap<>();

        for (int i = 0; i < contents.length; i++) {
            ItemStack iteratingItem = contents[i];
            if (iteratingItem == null || !materialType.equals(iteratingItem.getType()))
                continue;
            int iteratingCount = iteratingItem.getAmount();
            int tempTotal = iteratingCount + counterMoney;

            if (amountRequired >= tempTotal) {
                counterMoney += iteratingCount;
                hashmap.put(new Integer(i), 0);
            } else if (amountRequired < tempTotal) {
                int tempLeftover = tempTotal - amountRequired;
                hashmap.put(new Integer(i), tempLeftover);
                counterMoney = amountRequired;
                break;
            }
        }

        if (counterMoney < amountRequired) {
            player.sendMessage(ERROR_COLOR + "You need " + amountRequired + " " + materialType.getKey().getKey().toLowerCase() + " to create a plot.");
            return null;
        }
        return hashmap;
    }

    public void sendPage(int page, Player playerSender) {

        UUID playerUUID = playerSender.getUniqueId();
        ShardPlotPlayer shardPlotPlayer = ShardPlotPlayer.wrap(playerUUID);
        final int amtOfPlotsPerPage = 3;
        final int MAX_PLOTS = shardPlotPlayer.getMaxPlots();

        int size;
        PlayerPlot[] plots = ShardPlotPlayer.wrap(playerUUID).getPlotsOwned();
        size = plots.length;


        int pages = (int) Math.ceil((double) size / amtOfPlotsPerPage);
        if (pages == 0)
            pages = 1;
        int pageCounter = page;
        int markCounter = 0;


        if (page > pages) {
            playerSender.sendMessage(ERROR_COLOR + "You do not have any more plot pages.");
            return;
        }

        playerSender.sendMessage(ChatColor.GOLD + "-" + playerSender.getName() + "'s Plots-");

        playerSender.sendMessage(ChatColor.GOLD + "Pg " + pageCounter + " of " + pages + " (" + size + " of " + MAX_PLOTS + " plots used)");

        for (int i = (page - 1) * amtOfPlotsPerPage; i < size; i++) {
            if (markCounter == amtOfPlotsPerPage) {
                return;
            }

            if (plots == null)
                break;

            playerSender.sendMessage(ChatColor.WHITE + "- " + plots[i].getName() + " - " + "(" + plots[i].getCenter().getBlockX() + "," + plots[i].getCenter().getBlockY() + "," + plots[i].getCenter().getBlockZ() + ")");

            markCounter++;
        }
    }
}
