package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class CastCommand implements CommandExecutor {

    public static HashMap<UUID, Integer> markCreateUUIDCooldown = new HashMap<>();
    public static HashMap<UUID, Integer> markRecallUUIDCooldown = new HashMap<>();

    public static HashMap<UUID, Location> markCommand = new HashMap<>();
    public static HashMap<UUID, Location> recallCommand = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("cast")) {

                if (args.length == 0) {
                    playerSender.sendMessage(ERROR_COLOR + "You don't have any marks.");
                    return false;
                } else if (args.length >= 1) {
                    switch (args[0].toLowerCase()) {
                        case "mark":

                            if (!MarkPlayer.hasMarks(playerUUID)) {
                                MarkPlayer markPlayer = new MarkPlayer(playerUUID);
                                //saves it
                            }


                            //rest
                            if (args.length == 1) {
                                markCommand.put(playerUUID, playerSender.getLocation());
                                HelperMethods.localBroadcast(playerSender, "Mark");
                                playerSender.sendMessage(ChatColor.AQUA + "What would you like to name your Mark?");
                                //Make a chat name
                            } else {
                                String name = HelperMethods.stringBuilder(args, 1, " ");
                                //Check if name is already taken


                                Location location = playerSender.getLocation();
                                boolean isDirect = true;
                                if (markCommand.containsKey(playerUUID)) {
                                    location = markCommand.get(playerUUID);
                                } else {
                                    isDirect = false;
                                }
                                markCommand.remove(playerUUID);

                                MarkPlayer markPlayer = MarkPlayer.wrap(playerUUID);
                                MarkPlayer.Mark[] marks = markPlayer.getMarks();

                                RankPlayer rankPlayer = RankPlayer.wrap(playerUUID);

                                if (marks.length == rankPlayer.getRankType().getMaxMarksNum()) {
                                    playerSender.sendMessage(ERROR_COLOR + "You have reached the maximum limit of marks.");
                                    return true;
                                }

                                ItemStack[] ingredients = new ItemStack[]{new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1)};

                                if (!hasIngredients(playerSender, ingredients)) {
                                    //   playerSender.sendMessage(ERROR_COLOR + "You don't have the ingredients to cast \"Mark\".");
                                    return true;
                                }

                                Stat stat = Stat.wrap(playerUUID);
                                if (stat.getMana() < 15) {
                                    playerSender.sendMessage(ERROR_COLOR + "You do not have enough mana to cast \"" + "Mark" + "\".");
                                    return false;
                                }

                                if (markCreateUUIDCooldown.containsKey(playerUUID)) {
                                    Integer timeInt = markCreateUUIDCooldown.get(playerUUID);
                                    String time = "seconds";
                                    if (timeInt.intValue() == 1)
                                        time = "second";
                                    playerSender.sendMessage(ERROR_COLOR + "You must wait " + timeInt.intValue() + " " + time + " before you can cast another spell.");
                                    return false;
                                }

                                if (markPlayer.hasMark(name)) {
                                    playerSender.sendMessage(ERROR_COLOR + "You already have a mark by this name.");
                                    return true;
                                }


                                if (!isDirect) {
                                    HelperMethods.localBroadcast(playerSender, "Mark");
                                }


                                //mana
                                stat.setMana(stat.getMana() - 15);
                                //message
                                playerSender.sendMessage(ChatColor.GOLD + "You have created a mark for \"" + name + "\".");
                                //add mark
                                markPlayer.addMark(name, location);
                                //remove ingredients
                                removeIngredients(playerSender, ingredients);
                                //cooldown
                                markCreateUUIDCooldown.put(playerUUID, new Integer(2));
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (!markCreateUUIDCooldown.containsKey(playerUUID)) {
                                            this.cancel();
                                            return;
                                        }
                                        Integer cooldownTimer = markCreateUUIDCooldown.get(playerUUID);
                                        Integer newCooldowntimer = cooldownTimer - 1;
                                        if (newCooldowntimer == 0) {
                                            markCreateUUIDCooldown.remove(playerUUID);
                                            this.cancel();
                                            return;
                                        }
                                        markCreateUUIDCooldown.put(playerUUID, newCooldowntimer);
                                    }
                                }.runTaskTimer(LostShardPlugin.plugin, 20, 20);

                            }
                            break;
                        case "recall":

                            if (args.length == 1) {
                                //Make a chat name
                                recallCommand.put(playerUUID, playerSender.getLocation());
                                HelperMethods.localBroadcast(playerSender, "Recall");
                                playerSender.sendMessage(ChatColor.YELLOW + "Where would you like to recall to?");

                            } else {
                                String name = HelperMethods.stringBuilder(args, 1, " ");
                                //Check if name is already taken


                                    if (!MarkPlayer.hasMarks(playerUUID)) {
                                        playerSender.sendMessage(ERROR_COLOR + "You do not have any marks.");
                                        recallCommand.remove(playerUUID);
                                        return false;
                                    }

                                    MarkPlayer markPlayer = MarkPlayer.wrap(playerUUID);

                                    if (!markPlayer.hasMark(name)) {
                                        playerSender.sendMessage(ERROR_COLOR + "You do not have a mark by this name.");
                                        recallCommand.remove(playerUUID);
                                        return true;
                                    }


                                ItemStack[] ingredients = new ItemStack[]{new ItemStack(Material.FEATHER, 1)};

                                if (!hasIngredients(playerSender, ingredients)) {
                                    //   playerSender.sendMessage(ERROR_COLOR + "You don't have the ingredients to cast \"Recall\".");
                                    recallCommand.remove(playerUUID);
                                    return true;
                                }


                                Stat stat = Stat.wrap(playerUUID);
                                if (stat.getMana() < 15) {
                                    playerSender.sendMessage(ERROR_COLOR + "You do not have enough mana to cast \"" + "Recall" + "\".");
                                    recallCommand.remove(playerUUID);
                                    return false;
                                }

                                if (markRecallUUIDCooldown.containsKey(playerUUID)) {
                                    Integer timeInt = markRecallUUIDCooldown.get(playerUUID);
                                    String time = "seconds";
                                    if (timeInt.intValue() == 1)
                                        time = "second";
                                    playerSender.sendMessage(ERROR_COLOR + "You must wait " + timeInt.intValue() + " " + time + " before you can cast another spell.");
                                    recallCommand.remove(playerUUID);
                                    return false;
                                }


                                if (recallCommand.containsKey(playerUUID)) {


                                } else {
                                    HelperMethods.localBroadcast(playerSender, "Recall");
                                }
                                recallCommand.remove(playerUUID);

                                MarkPlayer.Mark mark = markPlayer.getMark(name);
                                stat.setMana(stat.getMana() - 15);
                                playerSender.sendMessage(ChatColor.GOLD + "You have recalled to the mark \"" + mark.getName() + "\".");
                                playerSender.teleport(mark.getLocation());
                                mark.getLocation().getWorld().strikeLightningEffect(mark.getLocation());
                                removeIngredients(playerSender, ingredients);
                                markRecallUUIDCooldown.put(playerUUID, new Integer(2));
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (!markRecallUUIDCooldown.containsKey(playerUUID)) {
                                            this.cancel();
                                            return;
                                        }
                                        Integer cooldownTimer = markRecallUUIDCooldown.get(playerUUID);
                                        Integer newCooldowntimer = cooldownTimer - 1;
                                        if (newCooldowntimer == 0) {
                                            markRecallUUIDCooldown.remove(playerUUID);
                                            this.cancel();
                                            return;
                                        }
                                        markRecallUUIDCooldown.put(playerUUID, newCooldowntimer);
                                    }
                                }.runTaskTimer(LostShardPlugin.plugin, 20, 20);

                            }
                            break;
                        default:
                            playerSender.sendMessage(ERROR_COLOR + "Invalid command.");
                            break;
                    }
                }

            }
        }
        return true;
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
            player.sendMessage(ERROR_COLOR + "You don't have " + amountRequired + " " + materialType.getKey().getKey().toLowerCase() + " to cast this spell!");
            return null;
        }
        return hashmap;
    }
}
