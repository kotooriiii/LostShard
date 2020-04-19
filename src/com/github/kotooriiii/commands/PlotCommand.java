package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.channels.ChannelManager;
import com.github.kotooriiii.plots.ArenaPlot;
import com.github.kotooriiii.plots.Plot;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
                ChannelManager channelManager = LostShardPlugin.getChannelManager();
                //No arguments regarding this command
                if (args.length == 0) {

                    //Send the help page and return.
                    sendHelp(playerSender);
                    return true;
                } else if (args.length >= 1) {
                    //Sub-commands again however with proper argument.
                    final String supply = stringBuilder(args, 1, " ");
                    final Bank bank = Bank.wrap(playerUUID);
                    final double currentCurrency = bank.getCurrency();
                    final DecimalFormat df = new DecimalFormat("#.##");

                    switch (args[0].toLowerCase()) {
                        case "create":
                            if (args.length == 1)
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to create your own plot? /plot create (name)");
                            else if (Plot.hasPlot(playerSender))
                                playerSender.sendMessage(ERROR_COLOR + "You can't create more than one plot.");
                            else if (Plot.hasNearbyPlots(playerSender))
                                playerSender.sendMessage(ERROR_COLOR + "There are other plot(s) nearby. You must be a minimum of " + Plot.MINIMUM_PLOT_CREATE_RANGE + " block(s) away to create your own plot.");
                            else if (supply.length() > 16)
                                playerSender.sendMessage(ERROR_COLOR + "The name can not exceed 16 characters.");
                            else if (Plot.isStaffPlot(supply))
                                playerSender.sendMessage(ERROR_COLOR + "This plot name has its place in history already. Create your own history!");
                            else if (Plot.hasPlotName(supply))
                                playerSender.sendMessage(ERROR_COLOR + "That plot name has already been taken.");
                            else if (!hasCreatePlotCost(playerSender)) {
                                //The message is already taken care of.
                            } else {
                                createPlot(playerSender, supply);
                            }
                            break;
                        case "disband":
                            if (args.length != 1)
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to disband your plot? /plot disband");
                            else if (!Plot.hasPlot(playerSender))
                                playerSender.sendMessage(ERROR_COLOR + "You don't own a plot. You can't remove something that isn't yours.");
                            else {
                                disbandPlot(playerSender);
                            }
                            break;
                        case "friend":
                        case "f":
                            if (args.length == 1) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to friend a player to your plot? /plot friend (username)");
                                return false;
                            } else if (!Plot.hasPlot(playerSender)) {
                                playerSender.sendMessage(ERROR_COLOR + "You don't own a plot. You can't friend a player without a plot.");
                                return false;
                            } else if (supply.length() > 16) {
                                playerSender.sendMessage(ERROR_COLOR + "The name can not exceed 16 characters.");
                                return false;
                            }

                            Plot plotFriend = Plot.wrap(playerUUID);
                            OfflinePlayer friendPlayer = Bukkit.getOfflinePlayer(supply);
                            UUID friendUUID = friendPlayer.getUniqueId();
                            if (!friendPlayer.hasPlayedBefore()) {
                                playerSender.sendMessage(ERROR_COLOR + "That player does not exist.");
                                return false;
                            }

                            if (plotFriend.isFriend(friendUUID)) {
                                playerSender.sendMessage(ChatColor.GOLD + "You have removed " + friendPlayer.getName() + " from being a plot friend.");
                                plotFriend.removeFriend(friendUUID);
                            } else {
                                playerSender.sendMessage(ChatColor.GOLD + "You have added " + friendPlayer.getName() + " to your plot as a friend.");
                                plotFriend.addFriend(friendUUID);
                            }
                            break;
                        case "co-owner":
                        case "co-own":
                        case "co":
                            if (args.length == 1) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to promote a player as co-owner to your plot? /plot co (username)");
                                return false;
                            } else if (!Plot.hasPlot(playerSender)) {
                                playerSender.sendMessage(ERROR_COLOR + "You don't own a plot. You can't set a player as a co-owner without a plot.");
                                return false;
                            } else if (supply.length() > 16) {
                                playerSender.sendMessage(ERROR_COLOR + "The name can not exceed 16 characters.");
                                return false;
                            }

                            Plot jointOwnerPlot = Plot.wrap(playerUUID);
                            OfflinePlayer jointPlayer = Bukkit.getOfflinePlayer(supply);
                            UUID jointOwnerUUID = jointPlayer.getUniqueId();
                            if (!jointPlayer.hasPlayedBefore()) {
                                playerSender.sendMessage(ERROR_COLOR + "That player does not exist.");
                                return false;
                            }

                            if (jointOwnerPlot.isJointOwner(jointOwnerUUID)) {
                                playerSender.sendMessage(ChatColor.GOLD + "You have removed " + jointPlayer.getName() + " from being a plot co-owner.");
                                jointOwnerPlot.removeJointOwner(jointOwnerUUID);
                            } else {
                                playerSender.sendMessage(ChatColor.GOLD + "You have added " + jointPlayer.getName() + " to your plot as a co-owner.");
                                jointOwnerPlot.addJointOwner(jointOwnerUUID);
                            }
                            break;
                        case "expand":
                            if (args.length != 1) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to expand your plot? /plot expand");
                                return false;
                            } else if (!Plot.hasPlot(playerSender)) {
                                playerSender.sendMessage(ERROR_COLOR + "You don't own a plot. You can't expand something that isn't yours.");
                                return false;
                            }

                            Plot expandPlot = Plot.wrap(playerUUID);

                            if (!expandPlot.isExpandable()) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't expand onto other plots.");
                                return false;
                            }

                            if (expandPlot.getBalance() < expandPlot.getExpandCost()) {
                                playerSender.sendMessage(ERROR_COLOR + "You don't have the money to expand your plot. You need " + df.format(expandPlot.getExpandCost()) + " to expand to the next size.");
                                return false;
                            }

                            expandPlot.expand();
                            playerSender.sendMessage(ChatColor.GOLD + "You have expanded the plot to size " + expandPlot.getSize() + ".");
                            break;
                        case "deposit":
                            if (args.length != 2) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to deposit currency into your plot? /plot deposit (amount)");
                                return false;
                            }
                            if (!Plot.hasPlot(playerSender)) {
                                playerSender.sendMessage(ERROR_COLOR + "You don't own a plot. You can't deposit into something you don't own.");
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

                            Plot plotDeposit = Plot.wrap(playerUUID);
                            bank.setCurrency(currentCurrency - deposit.doubleValue());
                            plotDeposit.deposit(deposit.doubleValue());

                            playerSender.sendMessage(ChatColor.GOLD + "You have deposited " + deposit.doubleValue() + " gold into the plot funds.");
                            break;
                        case "withdraw":
                            if (args.length != 2) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to withdraw currency from your plot? /plot withdraw (amount)");
                                return false;
                            }
                            if (!Plot.hasPlot(playerSender)) {
                                playerSender.sendMessage(ERROR_COLOR + "You don't own a plot. You can't withdraw from something you don't own.");
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

                            if (currentCurrency < withdraw.doubleValue()) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't withdraw more than your current balance.");
                                return false;
                            }

                            Plot plotWithdraw = Plot.wrap(playerUUID);
                            bank.setCurrency(currentCurrency + withdraw.doubleValue());
                            plotWithdraw.withdraw(withdraw.doubleValue());

                            playerSender.sendMessage(ChatColor.GOLD + "You have withdrawn " + withdraw.doubleValue() + " gold from the plot funds.");
                            break;
                        case "info":
                            if (args.length != 1)
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to check info on the possible plot you are standing on? /plot info");
                            else if (!Plot.isStandingOnPlot(playerSender))
                                playerSender.sendMessage(ERROR_COLOR + "You are not currently standing in a plot.");
                            else {
                                Plot infoPlot = Plot.getStandingOnPlot(playerSender);
                                String info = infoPlot.info(playerSender);
                                playerSender.sendMessage(info);
                            }
                            break;
                        case "staff":

                            if(!playerSender.hasPermission(STAFF_PERMISSION))
                            {
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
                                case "setspawn":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You didn't provide enough arguments. /plot staff setspawn (name)");
                                        return false;
                                    }

                                    if (!Plot.isStandingOnPlot(playerSender)) {
                                        playerSender.sendMessage(ERROR_COLOR + "You must be standing on a plot in order to set a spawn.");
                                        return false;
                                    }

                                    Plot plot = Plot.getStandingOnPlot(playerSender);

                                    if (!plot.isStaff() && !plot.getName().equalsIgnoreCase("order") && !plot.getName().equalsIgnoreCase("chaos")) {
                                        playerSender.sendMessage(ERROR_COLOR + "This must be a staff-owned plot with the name(s): Order or Chaos. (For the banmatch/moneymatch refer to: setspawnA and setspawnB)");
                                        return false;
                                    }

                                    plot.setSpawn(playerSender.getLocation());
                                    playerSender.sendMessage(ChatColor.GOLD + "The spawn for " + plot.getName() + " has been set to where you are standing.");

                                    break;
                                case "setspawna":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You didn't provide enough arguments. /plot staff setspawnA (name)");
                                        return false;
                                    }

                                    if (!Plot.isStandingOnPlot(playerSender)) {
                                        playerSender.sendMessage(ERROR_COLOR + "You must be standing on a plot in order to set a spawn.");
                                        return false;
                                    }

                                    Plot plotA = Plot.getStandingOnPlot(playerSender);

                                    if (!(plotA instanceof ArenaPlot))
                                    {
                                        playerSender.sendMessage(ERROR_COLOR + "This must be a staff-owned arena plot with the name(s): Arena. (For the chaos/order refer to: setspawn)");
                                        return false;
                                    }

                                    ArenaPlot arenaPlotA = (ArenaPlot) plotA;

                                    arenaPlotA.setSpawnA(playerSender.getLocation());
                                    playerSender.sendMessage(ChatColor.GOLD + "The spawn A for " + arenaPlotA.getName() + " has been set to where you are standing.");
                                    break;
                                case "setspawnb":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You didn't provide enough arguments. /plot staff setspawnB (name)");
                                        return false;
                                    }

                                    if (!Plot.isStandingOnPlot(playerSender)) {
                                        playerSender.sendMessage(ERROR_COLOR + "You must be standing on a plot in order to set a spawn.");
                                        return false;
                                    }

                                    Plot plotB = Plot.getStandingOnPlot(playerSender);

                                    if (!(plotB instanceof ArenaPlot))
                                    {
                                        playerSender.sendMessage(ERROR_COLOR + "This must be a staff-owned arena plot with the name(s): Arena. (For the chaos/order refer to: setspawn)");
                                        return false;
                                    }

                                    ArenaPlot arenaPlotB = (ArenaPlot) plotB;

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
                            sendUnknownCommand(playerSender);
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
        player.sendMessage(ChatColor.GOLD + prefix + " " + "create (name)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "setspawn (name)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "tools");
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
        Bank bank = Bank.wrap(playerSender.getUniqueId());
        Plot plot = Plot.wrap(playerSender.getUniqueId());

        double currentCurrency = bank.getCurrency();

        DecimalFormat df = new DecimalFormat("#.##");
        double refund = plot.disband();
        double funds = plot.getBalance();

        bank.setCurrency(currentCurrency + refund + funds);
        playerSender.sendMessage(ChatColor.GOLD + "You have disbanded " + plot.getName() + ".");
        playerSender.sendMessage(ChatColor.GOLD + "You have been refunded " + df.format(refund) + " gold for " + (Plot.REFUND_RATE * 100) + "% of the plot's value.");
        playerSender.sendMessage(ChatColor.GOLD + "You have been given the remaining funds from your plot's balance (" + df.format(funds) + ").");

    }

    public boolean hasCreatePlotCost(Player player) {
        Bank bank = Bank.wrap(player.getUniqueId());
        double currentCurrency = bank.getCurrency();

        if (currentCurrency < Plot.CREATE_COST) {
            player.sendMessage(ERROR_COLOR + "You do not have enough funds in the plot’s balance to expand your plot any further. You need " + Plot.CREATE_COST + " gold to expand to the next size.");
            return false;
        }

        ItemStack[] ingredients = new ItemStack[]{new ItemStack(Material.DIAMOND, 1)};
        if (!hasIngredients(player, ingredients)) {
            return false;
        }

        return true;
    }

    public void createPlot(Player player, String name) {
        Bank bank = Bank.wrap(player.getUniqueId());
        ItemStack[] ingredients = new ItemStack[]{new ItemStack(Material.DIAMOND, 1)};

        bank.setCurrency(bank.getCurrency() - Plot.CREATE_COST);
        removeIngredients(player, ingredients);

        Plot plot = new Plot(player, name);
        player.sendMessage(ChatColor.GOLD + "You have created the plot \"" + plot.getName() + "\", it cost $" + Plot.CREATE_COST + " and 1 diamond.");
    }

    private void sendHelp(Player player) {
        String prefix = "/plot";
        player.sendMessage(ChatColor.GOLD + "-Plot Help-");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "create (name)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "disband");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "friend (username)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "co (name)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "deposit (amount)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "withdraw (amount)");
        player.sendMessage(ChatColor.GOLD + prefix + " " + "info");


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
}
