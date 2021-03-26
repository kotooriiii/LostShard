package com.github.kotooriiii.plots.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.npc.type.vendor.VendorItemStack;
import com.github.kotooriiii.npc.type.vendor.VendorNPC;
import com.github.kotooriiii.npc.type.vendor.VendorTrait;
import com.github.kotooriiii.plots.PlotManager;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.type.circle9.GreedSpell;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.stringBuilder;

public class VendorCommand implements CommandExecutor {

    public static final DecimalFormat df = new DecimalFormat("0.00");

    private static final HashMap<UUID, VendorTrait> newListingMap = new HashMap<>();
    private static final HashMap<UUID, VendorTrait> choiceListingMap = new HashMap<>();

    public static final String STOCK_INDEX_COMMAND_ARGUMENT = "stockindex";
    public static final String BUY_INDEX_COMMAND_ARGUMENT = "buyindex";

    public static HashMap<UUID, VendorTrait> getNewListingMap() {
        return newListingMap;
    }

    public static HashMap<UUID, VendorTrait> getChoiceListingMap() {
        return choiceListingMap;
    }

    public static boolean isStocking(UUID uuid) {
        return getChoiceListingMap().containsKey(uuid) || getNewListingMap().containsKey(uuid);
    }

    public static boolean isBeingStocked(VendorTrait trait) {
        return getChoiceListingMap().containsValue(trait) || getNewListingMap().containsValue(trait);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            Player playerSender = (Player) sender;
            UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "clan" command
            if (cmd.getName().equalsIgnoreCase("vendor")) {

                //No arguments regarding this command
                if (args.length == 0) {
                    sendHelp(playerSender);
                    return false;
                } else if (args.length >= 1) {
                    //Sub-commands again however with proper argument.
                    String supply = stringBuilder(args, 1, " ");
                    PlotManager plotManager = LostShardPlugin.getPlotManager();
                    Plot standingOnPlot = plotManager.getStandingOnPlot(playerSender.getLocation());
                    StatusPlayer statusPlayer = StatusPlayer.wrap(playerUUID);

                    switch (args[0].toLowerCase()) {
                        case "help":
                            sendHelp(playerSender);
                            return true;
                        case "create":
                            if (args.length < 2) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to create a vendor? Here's an example: \"/plot create James Scott\".");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be on a plot to use this command.");
                                return false;
                            }

                            if (!(standingOnPlot instanceof PlayerPlot)) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be on a player made plot to use this command.");
                                return false;
                            }

                            PlayerPlot playerPlot = (PlayerPlot) standingOnPlot;

                            if (!playerPlot.isVendor()) {
                                playerSender.sendMessage(ERROR_COLOR + "You must upgrade your plot to Vendor to use this command. Type: \"/plot upgrade Vendor\".");
                                return false;
                            }

                            if (!playerPlot.isJointOwner(playerUUID) && !playerPlot.isOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be a co-owner or owner of the plot to use this command.");
                                return false;
                            }


                            if (supply.length() > 20) {
                                playerSender.sendMessage(ERROR_COLOR + "The name of the Vendor is too long! Try shortening it by " + (supply.length() - 20) + " character(s).");
                                return false;
                            }

                            for (NPC npc : playerPlot.getVendors()) {
                                if (npc.getTrait(VendorTrait.class).getVendorName().equalsIgnoreCase(supply)) {
                                    playerSender.sendMessage(ERROR_COLOR + "Another Vendor in this plot shares the same name.. Be more unique!");
                                    return false;
                                }
                            }

                            int nextVendorCost = playerPlot.getCostOfNextVendor();

                            if (nextVendorCost == -1) {
                                playerSender.sendMessage(ERROR_COLOR + "You have reached the maximum amount of Vendors you can place on this plot.");
                                return false;
                            }
                            if (playerPlot.getBalance() < nextVendorCost) {
                                playerSender.sendMessage(ERROR_COLOR + "You need at least " + nextVendorCost + "g to add another Vendor.");
                                return false;
                            }

                            if (VendorNPC.isWithinDistanceOfOtherVendor(playerSender.getLocation())) {
                                playerSender.sendMessage(ERROR_COLOR + "This is too close to another Vendor. You must be at least " + VendorNPC.getWithinDistance() + " blocks away from the nearest Vendor.");
                                return false;
                            }


                            playerSender.sendMessage(ChatColor.GOLD + "You have successfully added Vendor \"" + supply + "\".");
                            playerPlot.withdraw(nextVendorCost);
                            VendorNPC npc = new VendorNPC(supply, playerPlot.getPlotUUID());
                            npc.spawn(HelperMethods.getCenterWithDirection(playerSender.getLocation()));
                            return true;
                        case "remove":
                            if (args.length < 2) {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to remove a vendor? Here's an example: \"/plot remove James Scott\".");
                                return false;
                            }

                            if (standingOnPlot == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be on a plot to use this command.");
                                return false;
                            }

                            if (!(standingOnPlot instanceof PlayerPlot)) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be on a player made plot to use this command.");
                                return false;
                            }

                            PlayerPlot playerPlotRemove = (PlayerPlot) standingOnPlot;


                            if (!playerPlotRemove.isJointOwner(playerUUID) && !playerPlotRemove.isOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be a co-owner or owner of the plot to use this command.");
                                return false;
                            }


                            for (NPC removeNPC : playerPlotRemove.getVendors()) {
                                VendorTrait vendorTrait = removeNPC.getTrait(VendorTrait.class);
                                if (vendorTrait.getVendorName().equalsIgnoreCase(supply)) {
                                    playerSender.sendMessage(ChatColor.GOLD + "You have successfully removed Vendor \"" + vendorTrait.getVendorName() + "\".");

                                    vendorTrait.dieSomehow();
                                    removeNPC.destroy();
                                    return true;
                                }
                            }

                            playerSender.sendMessage(ERROR_COLOR + "No vendor was found with the name \"" + supply + "\".");
                            return false;
                        case STOCK_INDEX_COMMAND_ARGUMENT:

                            final VendorTrait traitRemoved = choiceListingMap.remove(playerUUID);
                            if (traitRemoved == null)
                                return false;


                            Integer indexToAdd;

                            try {
                                indexToAdd = Integer.valueOf(supply);
                            } catch (NumberFormatException e) {
                                playerSender.sendMessage(ERROR_COLOR + "You must enter one of the numbers provided.");
                                return false;
                            }


                            //If the Vendor isn't in a Plot, STOP (THIS IS A BAD ERROR, THIS SHOULD NEVER  HAPPEN!
                            final Plot stockIndexPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(traitRemoved.getNPC().getStoredLocation());
                            if (!(stockIndexPlot instanceof PlayerPlot)) {
                                playerSender.sendMessage(ERROR_COLOR + "The Vendor is not in a player plot. Contact staff.");
                                return false;
                            }

                            //If this vendor is already being stocked by another person, STOP
                            if (isBeingStocked(traitRemoved)) {
                                playerSender.sendMessage(ERROR_COLOR + "The Vendor is currently being stocked by another player.");
                                return false;
                            }

                            PlayerPlot stockIndexPlayerPlot = (PlayerPlot) stockIndexPlot;

                            //Plot permission missing?, STOP
                            if (!stockIndexPlayerPlot.isJointOwner(playerUUID) && !stockIndexPlayerPlot.isOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be a co-owner or owner of the plot to use this command.");
                                return false;
                            }

                            ItemStack handItemStack = playerSender.getInventory().getItemInMainHand();

                            //Nothing to sell?, STOP
                            if (handItemStack == null || handItemStack.getType() == Material.AIR) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be holding an item to stock.");
                                return false;
                            }

                            final int originalHandAmt = handItemStack.getAmount();
                            final String materialName = HelperMethods.materialName(handItemStack.getType());
                            int leftoverItemsInHand;


                            VendorItemStack vendorItemStackForStockIndex = traitRemoved.getVendorItems().get(indexToAdd.intValue());

                            //If more than can fit in vendors
                            if (vendorItemStackForStockIndex.amount + handItemStack.getAmount() >= vendorItemStackForStockIndex.getMaxAmount()) {

                                leftoverItemsInHand = (vendorItemStackForStockIndex.amount + originalHandAmt) - vendorItemStackForStockIndex.getMaxAmount();
                                vendorItemStackForStockIndex.amount = vendorItemStackForStockIndex.getMaxAmount();

                                //If leftover is 0, remove item
                                if (leftoverItemsInHand == 0) {
                                    playerSender.getInventory().setItemInMainHand(null);
                                }
                                //Otherwise, set item with leftover.
                                else {
                                    handItemStack.setAmount(leftoverItemsInHand);
                                    playerSender.getInventory().setItemInMainHand(handItemStack);
                                }

                            } else {
                                //If it fits perfectly in a listing
                                leftoverItemsInHand = 0;
                                vendorItemStackForStockIndex.amount += originalHandAmt;
                                playerSender.getInventory().setItemInMainHand(null);

                            }

                            int stockIndexAmount = originalHandAmt - leftoverItemsInHand;
                            stockIndexPlayerPlot.sendToMembers(statusPlayer.getStatus().getChatColor() + playerSender.getName() + ChatColor.GOLD + " stocked " + stockIndexAmount + " " + materialName + " for " + df.format(vendorItemStackForStockIndex.getSelectPrice(stockIndexAmount)) + "g. (" + "Stack Price: " + df.format(vendorItemStackForStockIndex.getStackPrice()) + "g)");

                            if (leftoverItemsInHand > 0) {

                                String haveOtherSlots = "";
                                if (traitRemoved.emptySlots() > 0) {
                                    haveOtherSlots += "However, you still have " + traitRemoved.emptySlots() + " slots. Type \"/vendor stock\" to create a new listing.";
                                }

                                playerSender.sendMessage(ChatColor.RED + "You did not have room to stock " + stockIndexAmount + " " + materialName + ".");
                                playerSender.sendMessage(ChatColor.RED + haveOtherSlots);
                            }


                            return true;//todo END maybe some graphics? or smthn

                        case "stock":

                            /*
                            If the command is either /vendor stock OR /vendor stock (double)
                             */
                            if (args.length == 1 || args.length == 2) {

                                //If the player is stocking, STOP
                                if (isStocking(playerUUID)) {
                                    playerSender.sendMessage(ERROR_COLOR + "You are currently stocking a Vendor.");
                                    return false;
                                }

                                //If there is no nearby Vendor, STOP
                                NPC stockNPC = VendorNPC.getNearestVendor(playerSender.getLocation());
                                if (stockNPC == null) {
                                    playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                    return false;
                                }

                                final VendorTrait trait = stockNPC.getTrait(VendorTrait.class);

                                //If the most nearby Vendor is farther, STOP
                                if (!trait.isSocialDistance(playerSender.getLocation())) {
                                    playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                    return false;
                                }

                                //If the Vendor isn't in a Plot, STOP (THIS IS A BAD ERROR, THIS SHOULD NEVER  HAPPEN!
                                final Plot stockPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(stockNPC.getStoredLocation());
                                if (!(stockPlot instanceof PlayerPlot)) {
                                    playerSender.sendMessage(ERROR_COLOR + "The Vendor is not in a player plot. Contact staff.");
                                    return false;
                                }

                                //If this vendor is already being stocked by another person, STOP
                                if (isBeingStocked(trait)) {
                                    playerSender.sendMessage(ERROR_COLOR + "The Vendor is currently being stocked by another player.");
                                    return false;
                                }

                                PlayerPlot stockPlayerPlot = (PlayerPlot) stockPlot;

                                //Plot permission missing?, STOP
                                if (!stockPlayerPlot.isJointOwner(playerUUID) && !stockPlayerPlot.isOwner(playerUUID)) {
                                    playerSender.sendMessage(ERROR_COLOR + "You must be a co-owner or owner of the plot to use this command.");
                                    return false;
                                }

                                ItemStack hand = playerSender.getInventory().getItemInMainHand();

                                //Nothing to sell?, STOP
                                if (hand == null || hand.getType() == Material.AIR) {
                                    playerSender.sendMessage(ERROR_COLOR + "You must be holding an item to stock.");
                                    return false;
                                }

                                if (GreedSpell.isSoulbound(hand)) {
                                    playerSender.sendMessage(ERROR_COLOR + "You cannot stock a soulbounded item.");
                                    return false;
                                }

                                final int originalHandAmount = hand.getAmount();
                                final String material = HelperMethods.materialName(hand.getType());


                                //  If "/vendor stock"
                                if (args.length == 1) {

                                    //Vendor items from Vendor
                                    final ArrayList<VendorItemStack> vendorItems = trait.getVendorItems();

                                    //Checks to see if AT LEAST ONE item was able to fit in a slot
                                    boolean isFilled = false;
                                    //Checks to see if ALL ITEMS are SAME PRICE by stack
                                    double samePrice = -13;
                                    //How many items were left over after filling
                                    int leftover = -1;
                                    //The price of a stack
                                    //The vendor item in the listing
                                    VendorItemStack vendorItem = null;

                                    //Checks for multiple items with similar meta
                                    ArrayList<Integer> indecesFound = new ArrayList<>();

                                    //Loop all the items the Vendor is selling
                                    for (int i = 0; i < vendorItems.size(); i++) {
                                        VendorItemStack vendorItemStack = vendorItems.get(i);

                                        //There must be enough items to FIT IN
                                        if (vendorItemStack.amount == vendorItemStack.getMaxAmount())
                                            continue;

                                        //Make sure the item stacks are similar
                                        if (vendorItemStack.itemStack.isSimilar(hand)) {

                                            //Add each item that fits criteria
                                            indecesFound.add(i++);
                                            //Keep updating vendor item
                                            vendorItem = vendorItemStack;

                                            //If not equal, then they must not be same price.
                                            if (samePrice != vendorItem.stackPrice) {
                                                //However, we need a default value which will be -13.
                                                if (samePrice == -13) {
                                                    samePrice = vendorItem.stackPrice;
                                                    continue;
                                                }

                                                //NOT SAME PRICE
                                                samePrice = -1;
                                            }

                                        }
                                    }

                                    //If there is only ONE UNIQUE listing that can fit in.
                                    if (indecesFound.size() == 1) {

                                        isFilled = true;

                                        //If more than can fit in vendors
                                        if (vendorItem.amount + hand.getAmount() >= vendorItem.getMaxAmount()) {

                                            leftover = (vendorItem.amount + originalHandAmount) - vendorItem.getMaxAmount();
                                            vendorItem.amount = vendorItem.getMaxAmount();

                                            //If leftover is 0, remove item
                                            if (leftover == 0) {
                                                playerSender.getInventory().setItemInMainHand(null);
                                            }
                                            //Otherwise, set item with leftover.
                                            else {
                                                hand.setAmount(leftover);
                                                playerSender.getInventory().setItemInMainHand(hand);
                                            }

                                        } else {
                                            //If it fits perfectly in a listing
                                            leftover = 0;
                                            vendorItem.amount += originalHandAmount;
                                            playerSender.getInventory().setItemInMainHand(null);

                                        }

                                    } else if (indecesFound.size() > 1) {//If multiple meta found in there, ask which one to put in?


                                        //Same price, by stating they have the same price we say that it is a positive number. That means, there must be at least TWO index.
                                        //CHECK IF SAME PRICING, IF SO, JUST CHOOSE SMALLEST INDEX
                                        if (samePrice != -1) {
                                            isFilled = true;
                                            vendorItem = vendorItems.get(indecesFound.get(0));

                                            //If more than can fit in vendors
                                            if (vendorItem.amount + hand.getAmount() >= vendorItem.getMaxAmount()) {

                                                leftover = (vendorItem.amount + originalHandAmount) - vendorItem.getMaxAmount();
                                                vendorItem.amount = vendorItem.getMaxAmount();

                                                //If leftover is 0, remove item
                                                if (leftover == 0) {
                                                    playerSender.getInventory().setItemInMainHand(null);
                                                }
                                                //Otherwise, set item with leftover.
                                                else {
                                                    hand.setAmount(leftover);
                                                    playerSender.getInventory().setItemInMainHand(hand);
                                                }

                                            } else {
                                                //If it fits perfectly in a listing
                                                leftover = 0;
                                                vendorItem.amount += originalHandAmount;
                                                playerSender.getInventory().setItemInMainHand(null);

                                            }

                                        } else {
                                            choiceListingMap.put(playerUUID, trait);
                                            playerSender.sendMessage(ChatColor.GOLD + "Multiple pricing of " + material + " were found. Which would you like to place it in?\nClick one of the entries OR type the number in the brackets.");

                                            net.md_5.bungee.api.ChatColor chatColor = net.md_5.bungee.api.ChatColor.DARK_PURPLE;
                                            TextComponent base = new TextComponent();
                                            for (int index : indecesFound) {
                                                TextComponent tc = new TextComponent("[" + index + "] " + material + " - Stack Price: " + vendorItems.get(index) + "g");
                                                tc.setColor(chatColor);
                                                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Selecting: " + index).color(chatColor).create()));
                                                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vendor " + VendorCommand.STOCK_INDEX_COMMAND_ARGUMENT + " " + index));
                                                chatColor = chatColor == net.md_5.bungee.api.ChatColor.DARK_PURPLE ? net.md_5.bungee.api.ChatColor.LIGHT_PURPLE : net.md_5.bungee.api.ChatColor.DARK_PURPLE;
                                                base.addExtra(tc);
                                            }
                                            playerSender.spigot().sendMessage(base);


                                            return false;
                                        }

                                    }

                                    int stockAmount = originalHandAmount - leftover;

                                    //IsFilled will return true if items were filled in. Otherwise, if no matches in the slots, we create new one
                                    if (isFilled) {

                                        stockPlayerPlot.sendToMembers(statusPlayer.getStatus().getChatColor() + playerSender.getName() + ChatColor.GOLD + " stocked " + stockAmount + " " + material + " for " + df.format(vendorItem.getSelectPrice(stockAmount)) + "g. (" + "Stack Price: " + df.format(vendorItem.getStackPrice()) + "g)");

                                        if (leftover > 0) {

                                            String haveOtherSlots = "";
                                            if (trait.emptySlots() > 0) {
                                                haveOtherSlots += "However, you still have " + trait.emptySlots() + " slots. Type \"/vendor stock\" to create a new listing.";
                                            }

                                            playerSender.sendMessage(ChatColor.RED + "You did not have room to stock " + stockAmount + " " + material + ".");
                                            playerSender.sendMessage(ChatColor.RED + haveOtherSlots);
                                        }

                                        return true;//todo END maybe some graphics? or smthn
                                    } else {
                                        newListingMap.put(playerSender.getUniqueId(), trait);
                                        playerSender.sendMessage(ChatColor.GOLD + "What should the total price of " + originalHandAmount + " " + material + " be?");
                                        return false;
                                    }

                                } else if (args.length == 2) {

                                    Double selectPrice;
                                    try {
                                        selectPrice = NumberUtils.createDouble(args[1]);
                                        if (selectPrice == null || selectPrice < 0) {
                                            playerSender.sendMessage(ERROR_COLOR + "The total price must be a positive number.");
                                            return false;
                                        }
                                    } catch (NumberFormatException e) {
                                        playerSender.sendMessage(ERROR_COLOR + "The total price must be a positive number.");
                                        return false;
                                    }

                                    if (trait.isFull()) {
                                        playerSender.sendMessage(ERROR_COLOR + "The vendor is full " + slotsFilled(trait) + ".");
                                        return false;
                                    }

                                    VendorItemStack vendorItemStack = new VendorItemStack();
                                    vendorItemStack.setStackPriceBy(hand, originalHandAmount, selectPrice);
                                    trait.getVendorItems().add(vendorItemStack);

                                    stockPlayerPlot.sendToMembers(statusPlayer.getStatus().getChatColor() + playerSender.getName() + ChatColor.GOLD + " stocked " + originalHandAmount + " " + material + " for " + df.format(vendorItemStack.getSelectPrice(originalHandAmount)) + "g. (" + "Stack Price: " + df.format(vendorItemStack.getStackPrice()) + "g)");
                                    stockPlayerPlot.sendToMembers(ChatColor.GOLD + trait.getVendorName() + " has filled a slot " + slotsFilled(trait) + ".");
                                    playerSender.getInventory().setItemInMainHand(null);

                                    //todo DO SOMETHJINGG COOL?
                                }
                                break;


                            } else {
                                playerSender.sendMessage(ERROR_COLOR + "Did you mean to stock the vendor? Try \"/vendor stock\" for easy steps.");
                            }
                            break;
                        case "withdraw":
                        case "withdrawal":
                            //If there is no nearby Vendor, STOP
                            NPC stockNPC = VendorNPC.getNearestVendor(playerSender.getLocation());
                            if (stockNPC == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                return false;
                            }

                            final VendorTrait trait = stockNPC.getTrait(VendorTrait.class);

                            //If the most nearby Vendor is farther, STOP
                            if (!trait.isSocialDistance(playerSender.getLocation())) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                return false;
                            }

                            //If the Vendor isn't in a Plot, STOP (THIS IS A BAD ERROR, THIS SHOULD NEVER  HAPPEN!
                            final Plot stockPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(stockNPC.getStoredLocation());
                            if (!(stockPlot instanceof PlayerPlot)) {
                                playerSender.sendMessage(ERROR_COLOR + "The Vendor is not in a player plot. Contact staff.");
                                return false;
                            }

                            PlayerPlot stockPlayerPlot = (PlayerPlot) stockPlot;

                            //Plot permission missing?, STOP
                            if (!stockPlayerPlot.isJointOwner(playerUUID) && !stockPlayerPlot.isOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be a co-owner or owner of the plot to use this command.");
                                return false;
                            }

                            //Nothing to sell?, STOP
                            if (!trait.canWithdraw()) {
                                playerSender.sendMessage(ERROR_COLOR + "Vendor " + trait.getVendorName() + ERROR_COLOR + " has an empty balance.");
                                return false;
                            }


                            stockPlayerPlot.sendToMembers(statusPlayer.getStatus().getChatColor() + playerSender.getName() + ChatColor.GOLD + " withdrew " +
                                    (int) trait.getBalance() + "g from Vendor \"" + trait.getVendorName() + ChatColor.GOLD + ".");

                            trait.withdraw();
                            break;
                        case "sold":
                        case "history":
                        case "hist":

                            //If there is no nearby Vendor, STOP
                            NPC historyNPC = VendorNPC.getNearestVendor(playerSender.getLocation());
                            if (historyNPC == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                return false;
                            }

                            final VendorTrait histNPCTrait = historyNPC.getTrait(VendorTrait.class);

                            //If the most nearby Vendor is farther, STOP
                            if (!histNPCTrait.isSocialDistance(playerSender.getLocation())) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                return false;
                            }

                            //If the Vendor isn't in a Plot, STOP (THIS IS A BAD ERROR, THIS SHOULD NEVER  HAPPEN!
                            final Plot histPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(historyNPC.getStoredLocation());
                            if (!(histPlot instanceof PlayerPlot)) {
                                playerSender.sendMessage(ERROR_COLOR + "The Vendor is not in a player plot. Contact staff.");
                                return false;
                            }

                            PlayerPlot listPlayerPlot = (PlayerPlot) histPlot;

                            //Plot permission missing?, STOP
                            if (!listPlayerPlot.isJointOwner(playerUUID) && !listPlayerPlot.isOwner(playerUUID)) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be a co-owner or owner of the plot to use this command.");
                                return false;
                            }

                            playerSender.sendMessage(histNPCTrait.getPurchaseHistory());
                            break;
                        case "list":
                        case "listing":
                            //If there is no nearby Vendor, STOP
                            NPC listNPC = VendorNPC.getNearestVendor(playerSender.getLocation());
                            if (listNPC == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                return false;
                            }

                            final VendorTrait listNPCTrait = listNPC.getTrait(VendorTrait.class);

                            //If the most nearby Vendor is farther, STOP
                            if (!listNPCTrait.isSocialDistance(playerSender.getLocation())) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                return false;
                            }

                            //If the Vendor isn't in a Plot, STOP (THIS IS A BAD ERROR, THIS SHOULD NEVER  HAPPEN!
                            final Plot listPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(listNPC.getStoredLocation());
                            if (!(listPlot instanceof PlayerPlot)) {
                                playerSender.sendMessage(ERROR_COLOR + "The Vendor is not in a player plot. Contact staff.");
                                return false;
                            }
                            playerSender.sendMessage(listNPCTrait.getListings());
                            break;
                        case "buy":
                            NPC buyNPC = VendorNPC.getNearestVendor(playerSender.getLocation());
                            if (buyNPC == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                return false;
                            }

                            final VendorTrait buyNPCTrait = buyNPC.getTrait(VendorTrait.class);

                            //If the most nearby Vendor is farther, STOP
                            if (!buyNPCTrait.isSocialDistance(playerSender.getLocation())) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                return false;
                            }

                            //If the Vendor isn't in a Plot, STOP (THIS IS A BAD ERROR, THIS SHOULD NEVER  HAPPEN!
                            final Plot buyPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(buyNPC.getStoredLocation());
                            if (!(buyPlot instanceof PlayerPlot)) {
                                playerSender.sendMessage(ERROR_COLOR + "The Vendor is not in a player plot. Contact staff.");
                                return false;
                            }
                            sendPage(1, playerSender, buyNPCTrait);
                            break;
                        case BUY_INDEX_COMMAND_ARGUMENT:
                            int index = Integer.parseInt(args[1]);
                            int qty = Integer.parseInt(args[2]);
                            int hashCode = Integer.parseInt(args[3]);

                            NPC buyIndexNPC = VendorNPC.getNearestVendor(playerSender.getLocation());
                            if (buyIndexNPC == null) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                return false;
                            }

                            final VendorTrait buyIndexTrait = buyIndexNPC.getTrait(VendorTrait.class);

                            //If the most nearby Vendor is farther, STOP
                            if (!buyIndexTrait.isSocialDistance(playerSender.getLocation())) {
                                playerSender.sendMessage(ERROR_COLOR + "You must be within " + VendorTrait.getSocialDistance() + " blocks of a Vendor.");
                                return false;
                            }

                            //If the Vendor isn't in a Plot, STOP (THIS IS A BAD ERROR, THIS SHOULD NEVER  HAPPEN!
                            final Plot buyIndexPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(buyIndexNPC.getStoredLocation());
                            if (!(buyIndexPlot instanceof PlayerPlot)) {
                                playerSender.sendMessage(ERROR_COLOR + "The Vendor is not in a player plot. Contact staff.");
                                return false;
                            }
                            PlayerPlot indexPlayerPlot = (PlayerPlot) buyIndexPlot;

                            if(buyIndexTrait.getVendorItems().size() <= index)
                            {
                                playerSender.sendMessage(ERROR_COLOR + "The item no longer exists.");
                                return false;
                            }

                            final VendorItemStack vendorItemStack = buyIndexTrait.getVendorItems().get(index);

                            if(hashItemStack(vendorItemStack.itemStack) != hashCode)
                            {
                                playerSender.sendMessage(ERROR_COLOR + "The item no longer exists");
                                return false;
                            }

                            final String materialNameBuy = HelperMethods.materialName(vendorItemStack.itemStack.getType());
                            final double totalPrice = vendorItemStack.getRealityPriceOf(qty);

                            final Bank bank = LostShardPlugin.getBankManager().wrap(playerUUID);
                            if (bank.getCurrency() < totalPrice) {
                                playerSender.sendMessage(ERROR_COLOR + "You can't afford to buy " + qty + " " + materialNameBuy + " (" + df.format(bank.getCurrency()) + "/" + df.format(totalPrice) + ").");
                                return false;
                            }

                            if (qty >= vendorItemStack.amount) {

                                String moreStacks = "";

                                for (int i = 0; i < buyIndexTrait.getVendorItems().size(); i++) {
                                    if (i == index)
                                        continue;

                                    VendorItemStack iteratedVendorItemStack = buyIndexTrait.getVendorItems().get(index);
                                    if (iteratedVendorItemStack.itemStack.isSimilar(vendorItemStack.itemStack)) {
                                        moreStacks += ChatColor.GOLD + "However, the Vendor also sells more " + materialNameBuy + "! Possibly priced differently...";
                                        break;
                                    }
                                }

                                playerSender.sendMessage(ChatColor.GRAY + "You have bought " + vendorItemStack.amount + " " + materialNameBuy + " for " + df.format(totalPrice) + "g.");
                                indexPlayerPlot.sendToMembers(StatusPlayer.wrap(playerUUID).getStatus().getChatColor() + playerSender.getName() + ChatColor.GOLD + " bought " + vendorItemStack.amount + " " + materialNameBuy + " for " + df.format(totalPrice) + "g.");

                                if (!moreStacks.isEmpty())
                                    playerSender.sendMessage(moreStacks);

                                final HashMap<Integer, ItemStack> integerItemStackHashMap = playerSender.getInventory().addItem(getItemStacks(vendorItemStack.itemStack, vendorItemStack.amount));

                                if (!integerItemStackHashMap.isEmpty()) {
                                    playerSender.sendMessage(ChatColor.RED + "The items you bought did not fit in your inventory and were dropped on to the ground.");
                                    for (ItemStack itemStack : integerItemStackHashMap.values()) {
                                        playerSender.getWorld().dropItemNaturally(playerSender.getLocation(), itemStack);
                                    }
                                }

                                bank.removeCurrency(totalPrice);
                                buyIndexTrait.deposit(totalPrice);
                                buyIndexTrait.getVendorItems().remove(index);

                                indexPlayerPlot.sendToMembers(ChatColor.GOLD + buyIndexTrait.getVendorName() + " has freed a slot " + slotsFilled(buyIndexTrait) + ".");
                                buyIndexTrait.addEntry(playerSender.getName(), vendorItemStack.amount, vendorItemStack.itemStack, ZonedDateTime.now(ZoneId.of("America/New_York")).toInstant().toEpochMilli());


                            } else {

                                playerSender.sendMessage(ChatColor.GRAY + "You have bought " + qty + " " + materialNameBuy + " for " + df.format(totalPrice) + "g.");

                                final HashMap<Integer, ItemStack> integerItemStackHashMap = playerSender.getInventory().addItem(getItemStacks(vendorItemStack.itemStack, qty));

                                if (!integerItemStackHashMap.isEmpty()) {
                                    playerSender.sendMessage(ChatColor.RED + "The items you bought did not fit in your inventory and were dropped on to the ground.");
                                    for (ItemStack itemStack : integerItemStackHashMap.values()) {
                                        playerSender.getWorld().dropItemNaturally(playerSender.getLocation(), itemStack);
                                    }
                                }
                                indexPlayerPlot.sendToMembers(StatusPlayer.wrap(playerUUID).getStatus().getChatColor() + playerSender.getName() + ChatColor.GOLD + " bought " + qty + " " + materialNameBuy + " for " + df.format(totalPrice) + "g.");

                                buyIndexTrait.addEntry(playerSender.getName(),qty, vendorItemStack.itemStack, ZonedDateTime.now(ZoneId.of("America/New_York")).toInstant().toEpochMilli());
                                bank.removeCurrency(totalPrice);
                                buyIndexTrait.deposit(totalPrice);
                                vendorItemStack.amount -= qty;
                            }

                            break;
                        default:
                            sendHelp(playerSender);
                            break;
                    }
                }
            }
        }
        return true;
    }

    private ItemStack[] getItemStacks(ItemStack itemStack, int amount) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();

        int stacks = amount / itemStack.getType().getMaxStackSize();
        int leftover = amount % itemStack.getType().getMaxStackSize();

        while (stacks > 0) {
            itemStack.setAmount(itemStack.getType().getMaxStackSize());
            itemStacks.add(itemStack);
            stacks--;
        }

        if(leftover != 0) {
            itemStack.setAmount(leftover);
            itemStacks.add(itemStack);
        }
        return itemStacks.toArray(new ItemStack[itemStacks.size()]);
    }

    public void sendPage(int page, Player playerSender, VendorTrait trait) {

        final int amtOfItemsPerPage = 5;
        ArrayList<VendorItemStack> listing = trait.getVendorItems();
        final int SIZE = listing.size();


        int pages = (int) Math.ceil((double) SIZE / amtOfItemsPerPage);
        if (pages == 0)
            pages = 1;
        int pageCounter = page;
        int counter = 0;


        if (page > pages) {
            playerSender.sendMessage(ERROR_COLOR + "There are not that many items on sale.");
            return;
        }

        final TextComponent completeComponent = new TextComponent("-" + trait.getPlotName() + "'s Vendor " + trait.getVendorName() + "-\n");
        completeComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);


        if (listing.isEmpty()) {
            final TextComponent textComponent = new TextComponent("The vendor isn't selling any items.");
            textComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            completeComponent.addExtra(textComponent);
            playerSender.spigot().sendMessage(textComponent);
            return;

        } else {
            final TextComponent hoverComponent = new TextComponent("Hover over for more info\n");
            hoverComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);

            final TextComponent pagesComponent = new TextComponent("Pg " + pageCounter + " of " + pages + "\n");
            pagesComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);

            completeComponent.addExtra(hoverComponent);
            completeComponent.addExtra(pagesComponent);

        }

        final TextComponent ADDER = new TextComponent();

        for (int i = (page - 1) * amtOfItemsPerPage; i < SIZE; i++) {
            if (counter == amtOfItemsPerPage) {
                break;
            }

            final VendorItemStack vendorItemStack = listing.get(i);


            final TextComponent itemComponent = new TextComponent(vendorItemStack.amount + "x");
            itemComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            itemComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Amount in stock.").color(net.md_5.bungee.api.ChatColor.GOLD).create()));

            final TextComponent spaceComponent = new TextComponent(" ");

            final String materialName = HelperMethods.materialName(vendorItemStack.itemStack.getType());
            final TextComponent materialComponent = new TextComponent(materialName);
            materialComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            materialComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, VendorTrait.getNiceMeta(vendorItemStack.itemStack)));

            final TextComponent dashComponent = new TextComponent(" - ");
            dashComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);


            ADDER.addExtra(itemComponent);
            ADDER.addExtra(spaceComponent);
            ADDER.addExtra(materialComponent);
            ADDER.addExtra(dashComponent);
            ADDER.addExtra(getPriceComponent(vendorItemStack, i, 1, 5, 16, 32));

            counter++;

            ADDER.addExtra(new TextComponent("\n"));
        }


        final List<BaseComponent> extra = ADDER.getExtra();
        extra.remove(extra.size()-1);

        ArrayList<BaseComponent> arrayList = new ArrayList();
        arrayList.add(completeComponent);
        arrayList.addAll(extra);


        playerSender.spigot().sendMessage(arrayList.toArray(new BaseComponent[arrayList.size()]));

    }


    private TextComponent getPriceComponent(VendorItemStack vendorItemStack, int index, int... numbers) {
        TextComponent finalComponent = new TextComponent();
        final TextComponent spaceComponent = new TextComponent(" ");

        for (int i = 0; i < numbers.length; i++) {

            final int quantity = numbers[i];

            final TextComponent purchaseA = new TextComponent("" + quantity);
            purchaseA.setColor(net.md_5.bungee.api.ChatColor.GOLD);


            String canOnlyBuy = quantity <=  vendorItemStack.amount ? "" :  ChatColor.RED + "\nMax. Items Available: " + vendorItemStack.amount;

            purchaseA.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Price: " + df.format(vendorItemStack.getRealityPriceOf(quantity))).color(net.md_5.bungee.api.ChatColor.GOLD).append(canOnlyBuy).color(net.md_5.bungee.api.ChatColor.RED).append("\n\nClick to buy!").color(net.md_5.bungee.api.ChatColor.GOLD).create()));
            purchaseA.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vendor " + VendorCommand.BUY_INDEX_COMMAND_ARGUMENT + " " + index + " " + quantity + " " + hashItemStack(vendorItemStack.itemStack)));


            finalComponent.addExtra(purchaseA);
            finalComponent.addExtra(spaceComponent);
        }
        return finalComponent;
    }

    private String slotsFilled(VendorTrait trait) {


        return "(" + trait.filledSlots() + "/" + VendorNPC.getMaxSlots() + ")";
    }

    private void sendHelp(Player player) {
        String prefix = "/vendor";

        player.sendMessage(ChatColor.DARK_PURPLE + "-Vendor Help (Hover for description)-");

        sendMessage(player, prefix + " create ", "(name of vendor)", "Creates a vendor with a given name.");
        sendMessage(player, prefix + " remove ", "(name of vendor)", "Removes a vendor with a given name.");
        sendMessage(player, prefix + " stock ", "", "Stocks the nearest vendor with the items in your hand.\nIf the vendor has this item already, it will stock it up fast and easy.");
        sendMessage(player, prefix + " stock ", "(total price)", "Stocks the nearest vendor with a unique listing of the items in your hand.");
        sendMessage(player, prefix + " withdraw ", "", "If a vendor is nearby, all the money the vendor made will be dropped on the floor.");
        sendMessage(player, prefix + " list ", "", "Lists all the items a vendor nearby is selling.");
        sendMessage(player, prefix + " sold ", "", "Shows the history of what was sold in the last 48 hours and the total balance the vendor.\nYou can also use '/vendor history' for the same effect.");
        sendMessage(player, "/buy ", "", "Buys an item from a nearby vendor.");

    }

    private void sendMessage(Player player, String message, String args, String description) {
        TextComponent tc = new TextComponent(message);
        tc.setColor(net.md_5.bungee.api.ChatColor.DARK_PURPLE);

        TextComponent tc2 = new TextComponent(args);
        tc2.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);

        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(description).color(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE).create()));
        tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(description).color(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE).create()));


        tc.addExtra(tc2);

        player.spigot().sendMessage(tc);
    }

    public int hashItemStack(ItemStack itemStack) {
        int hash = 1;
        hash = hash * 31 + itemStack.getType().getKey().getKey().hashCode();
        hash = hash * 31 + (itemStack.getDurability() & '\uffff');
        hash = hash * 31 + (itemStack.hasItemMeta() ? (itemStack.getItemMeta() == null ? itemStack.getItemMeta().hashCode() : 1) : 0);
        return hash;
    }
}