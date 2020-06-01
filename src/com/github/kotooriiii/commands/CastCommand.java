package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

import static com.github.kotooriiii.data.Maps.*;

public class CastCommand implements CommandExecutor {

    public static HashMap<UUID, Integer> markCreateUUIDCooldown = new HashMap<>();
    public static HashMap<UUID, Integer> markRecallUUIDCooldown = new HashMap<>();
    public static HashMap<UUID, Integer> clantpUUIDCooldown = new HashMap<>();

    public static HashSet<UUID> clantpCommand = new HashSet<>();

    public static HashMap<UUID, Location> markCommand = new HashMap<>();
    public static HashMap<UUID, Location> recallCommand = new HashMap<>();

    public static HashMap<UUID, MarkPlayer.Mark> castRecall = new HashMap<>();
    public static HashMap<UUID, OfflinePlayer> castClanTP = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("cast")) {

                if (Spell.isLapisNearby(playerSender.getLocation(), 5)) {
                    playerSender.sendMessage(ERROR_COLOR + "You cannot seem to cast a spell here...");
                    return false;
                }

                if (args.length == 0) {

                    playerSender.sendMessage("-Cast Help-");
                    playerSender.sendMessage(COMMAND_COLOR + "/cast clantp " + ChatColor.YELLOW + "(clan member's name)");
                    playerSender.sendMessage(COMMAND_COLOR + "/cast mark " + ChatColor.YELLOW + "(name)");
                    playerSender.sendMessage(COMMAND_COLOR + "/cast recall " + ChatColor.YELLOW + "(mark's name)");

                    return false;
                } else if (args.length >= 1) {
                    switch (args[0].toLowerCase()) {
                        case "clantp":
                            //rest
                            if (args.length == 1) {
                                clantpCommand.add(playerUUID);
                                HelperMethods.localBroadcast(playerSender, "Clan Teleport");
                                playerSender.sendMessage(ChatColor.AQUA + "Who would you like to teleport to?");
                            } else {
                                String name = HelperMethods.stringBuilder(args, 1, " ");
                                //Check if name is already taken

                                boolean isDirect = true;
                                if (!clantpCommand.contains(playerUUID))
                                    isDirect = false;

                                clantpCommand.remove(playerUUID);

                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);

                                if (!hasClanTPRequirements(playerSender, offlinePlayer)) {
                                    return false;
                                }

                                //At this point, the playerSender is in clan, the teleportee is online AND in the same clan.

//
//                                ItemStack[] ingredients = new ItemStack[]{new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1)};
//
//                                if (!hasIngredients(playerSender, ingredients)) {
//                                    //   playerSender.sendMessage(ERROR_COLOR + "You don't have the ingredients to cast \"Mark\".");
//                                    return true;
//                                }

                                if (clantpUUIDCooldown.containsKey(playerUUID)) {
                                    Integer timeInt = clantpUUIDCooldown.get(playerUUID);
                                    String time = "seconds";
                                    if (timeInt.intValue() == 1)
                                        time = "second";
                                    playerSender.sendMessage(ERROR_COLOR + "You must wait " + timeInt.intValue() + " " + time + " before you can cast another spell.");
                                    return false;
                                }


                                if (!isDirect) {
                                    HelperMethods.localBroadcast(playerSender, "Clan Teleport");
                                }

                                playerSender.sendMessage(ChatColor.GOLD + "You begin to cast Clan Teleport...");
                                castClanTP.put(playerUUID, offlinePlayer);

                                new BukkitRunnable() {
                                    int clantpCastTime = 3; // in seconds

                                    @Override
                                    public void run() {
                                        if (isCancelled())
                                            return;
                                        if (!castClanTP.containsKey(playerUUID)) {
                                            this.cancel();
                                            return;
                                        }

                                        if (clantpCastTime == 0) {
                                            clanTP(playerSender);
                                            castClanTP.remove(playerUUID);
                                            this.cancel();
                                            return;
                                        }

                                        clantpCastTime--;
                                    }
                                }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
                            }
                            break;
                        case "mark":
                        case "marks":
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

                                MarkPlayer markPlayer = MarkPlayer.wrap(playerUUID);
                                MarkPlayer.Mark mark = null;

                                if (!markPlayer.hasMark(name)) {

                                    if (name.equalsIgnoreCase("random")) {

                                        Location randomLoc = randomRecall(playerSender);
                                        if (randomLoc == null) {
                                            playerSender.sendMessage(ERROR_COLOR + "There is no world border set or the shape is not a square. The recall feature has been disabled, please notify staff.");
                                            return false;
                                        }

                                        mark = new MarkPlayer.Mark("random", randomLoc);
                                    } else {

                                        playerSender.sendMessage(ERROR_COLOR + "You do not have a mark by this name.");
                                        recallCommand.remove(playerUUID);
                                        return true;
                                    }
                                } else {
                                    if (name.equalsIgnoreCase("random")) {
                                        playerSender.sendMessage(STANDARD_COLOR + "You've overriden the 'random' premade mark.");
                                    }
                                }

                                if (!hasRecallRequirements(playerSender)) {
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


                                if (mark == null)
                                    mark = markPlayer.getMark(name);
                                playerSender.sendMessage(ChatColor.GOLD + "You begin to cast to the mark, \"" + mark.getName() + "\"...");
                                castRecall.put(playerUUID, mark);

                                new BukkitRunnable() {
                                    int recallCastTime = 3; // in seconds

                                    @Override
                                    public void run() {
                                        if (isCancelled())
                                            return;
                                        if (!castRecall.containsKey(playerUUID)) {
                                            this.cancel();
                                            return;
                                        }

                                        if (recallCastTime == 0) {
                                            recall(playerSender);
                                            castRecall.remove(playerUUID);
                                            this.cancel();
                                            return;
                                        }


                                        recallCastTime--;
                                    }
                                }.runTaskTimer(LostShardPlugin.plugin, 0, 20);


                            }
                            break;
                        default:
                            SpellType type = SpellType.matchSpellType(args[0].toLowerCase());
                            if (type != null) {
                                Spell spell = Spell.of(type);
                                if (spell != null) {
                                    if(Spell.of(type).isCastable()) {
                                        spell.cast(playerSender);
                                        return true;
                                    }
                                }
                            }
                            playerSender.sendMessage(ERROR_COLOR + "The spell does not exist.");
                            break;
                    }
                }

            }
        }
        return true;
    }

    public Location randomRecall(Player player) {

        World world = player.getLocation().getWorld();
        String worldName = world.getName();

        LostShardPlugin.LSBorder border = LostShardPlugin.getBorder(worldName);
        int cX = border.getX();
        int cZ = border.getZ();
        int rX = border.getRadiusX();
        int rZ = border.getRadiusZ();

        Random random = new Random();
        int ranX = random.nextInt(((rX) * 2) + 1) - (rX);
        int ranZ = random.nextInt(((rZ) * 2) + 1) - (rZ);

        int blockX = ranX + cX;
        int blockZ = ranZ + cZ;


        Location randomLoc = world.getHighestBlockAt(blockX, blockZ).getLocation().add(0.5, 1, 0.5);
        return randomLoc;

    }

    public static void recall(Player playerSender) {
        if (playerSender == null || playerSender.isDead() || !playerSender.isOnline())
            return;
        if (!hasRecallRequirements(playerSender))
            return;


        Stat stat = Stat.wrap(playerSender.getUniqueId());
        stat.setMana(stat.getMana() - getRecallManaCost());
        removeIngredients(playerSender, getIngredientCost());


        MarkPlayer.Mark mark = getCastRecall().get(playerSender.getUniqueId());
        playerSender.teleport(mark.getLocation());

        UUID playerUUID = playerSender.getUniqueId();

        playerSender.sendMessage(ChatColor.GOLD + "You have recalled to the mark \"" + mark.getName() + "\".");
        mark.getLocation().getWorld().strikeLightningEffect(mark.getLocation());
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

    public void clanTP(Player playerSender) {
        if (playerSender == null || playerSender.isDead() || !playerSender.isOnline())
            return;

        OfflinePlayer offlinePlayer = getCastClanTP().get(playerSender.getUniqueId());

        if (!hasClanTPRequirements(playerSender, offlinePlayer))
            return;

        UUID playerUUID = playerSender.getUniqueId();

        Stat stat = Stat.wrap(playerSender.getUniqueId());
        stat.setMana(0);
        stat.setStamina(0);
        removeIngredients(playerSender, getIngredientCostClanTP());

        Player player = offlinePlayer.getPlayer();

        playerSender.teleport(player.getLocation());

        playerSender.sendMessage(ChatColor.GOLD + "You have recalled to \"" + player.getName() + "\".");
        player.getLocation().getWorld().strikeLightningEffect(player.getLocation());

        clantpUUIDCooldown.put(playerUUID, new Integer(2));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!clantpUUIDCooldown.containsKey(playerUUID)) {
                    this.cancel();
                    return;
                }
                Integer cooldownTimer = clantpUUIDCooldown.get(playerUUID);
                Integer newCooldowntimer = cooldownTimer - 1;
                if (newCooldowntimer == 0) {
                    clantpUUIDCooldown.remove(playerUUID);
                    this.cancel();
                    return;
                }
                clantpUUIDCooldown.put(playerUUID, newCooldowntimer);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 20, 20);
    }

    public static int getRecallManaCost() {
        return 15;
    }

    public static int getClanTPManaCost() {
        return 15;
    }

    public static ItemStack[] getIngredientCost() {
        return new ItemStack[]{new ItemStack(Material.FEATHER, 1)};
    }

    public static ItemStack[] getIngredientCostClanTP() {
        return new ItemStack[]{new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.FEATHER, 1)};
    }

    public static boolean hasRecallRequirements(Player playerSender) {

        final UUID playerUUID = playerSender.getUniqueId();

        ItemStack[] ingredients = getIngredientCost();

        if (!hasIngredients(playerSender, ingredients)) {
            //   playerSender.sendMessage(ERROR_COLOR + "You don't have the ingredients to cast \"Recall\".");
            recallCommand.remove(playerUUID);
            return false;
        }


        Stat stat = Stat.wrap(playerUUID);
        if (stat.getMana() < getRecallManaCost()) {
            playerSender.sendMessage(ERROR_COLOR + "You do not have enough mana to cast \"" + "Recall" + "\".");
            recallCommand.remove(playerUUID);
            return false;
        }


        return true;
    }

    public static boolean hasClanTPRequirements(Player playerSender, OfflinePlayer offlinePlayer) {

        final UUID playerUUID = playerSender.getUniqueId();

        Clan clan = Clan.getClan(playerUUID);

        if (clan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return false;
        }


        if (!offlinePlayer.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "The player is not online.");
            return false;
        }

        if (!clan.isInThisClan(offlinePlayer.getUniqueId())) {
            playerSender.sendMessage(ERROR_COLOR + "The player is not in your clan.");
            return false;
        }

        ItemStack[] ingredients = getIngredientCostClanTP();

        if (!hasIngredients(playerSender, ingredients)) {
            //   playerSender.sendMessage(ERROR_COLOR + "You don't have the ingredients to cast \"Recall\".");
            clantpCommand.remove(playerUUID);
            return false;
        }

        Stat stat = Stat.wrap(playerUUID);
        if (stat.getMana() < getClanTPManaCost()) {
            playerSender.sendMessage(ERROR_COLOR + "You do not have enough mana to cast \"" + "Clan Teleport" + "\".");
            clantpCommand.remove(playerUUID);
            return false;
        }


        return true;
    }

    public static boolean hasIngredients(Player player, ItemStack[] ingredients) {
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

    public static boolean removeIngredients(Player player, ItemStack[] ingredients) {
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

    public static HashMap<Integer, Integer> hasIngredient(Player player, ItemStack itemStack) {
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

    public static HashMap<UUID, MarkPlayer.Mark> getCastRecall() {
        return castRecall;
    }

    public static boolean isCastingRecall(UUID uuid) {
        return castRecall.get(uuid) != null;
    }

    public static HashMap<UUID, OfflinePlayer> getCastClanTP() {
        return castClanTP;
    }

    public static boolean isCastingClanTP(UUID uuid) {
        return castClanTP.get(uuid) != null;
    }

}
