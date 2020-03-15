package com.github.kotooriiii.files;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.bank.DonorTitle;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.clans.ClanRank;
import com.github.kotooriiii.guards.ShardBanker;
import com.github.kotooriiii.guards.ShardGuard;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import static com.github.kotooriiii.data.Maps.platforms;
import static com.github.kotooriiii.data.Maps.playerUUIDClanMap;

public final class FileManager {
    private static File plugin_folder = LostShardK.plugin.getDataFolder();
    private static File clans_folder = new File(plugin_folder + File.separator + "clans");
    private static File hostility_platform_folder = new File(plugin_folder + File.separator + "hostility" + File.separator + "platforms");
    private static File guards_folder = new File(plugin_folder + File.separator + "npc" + File.separator + "guards");
    private static File bankers_folder = new File(plugin_folder + File.separator + "npc" + File.separator + "bankers");
    private static File bank_folder = new File(plugin_folder + File.separator + "bank");


    private FileManager() {
    }

    public static void init() {
        plugin_folder.mkdir();
        clans_folder.mkdir();
        hostility_platform_folder.mkdirs();
        guards_folder.mkdirs();
        bankers_folder.mkdirs();
        bank_folder.mkdirs();
        saveResource("com" + File.separator + "github" + File.separator + "kotooriiii" + File.separator + "files" + File.separator + "clanREADME.txt", clans_folder, true);
        saveResource("com" + File.separator + "github" + File.separator + "kotooriiii" + File.separator + "files" + File.separator + "hostilityREADME.txt", hostility_platform_folder, true);
        saveResource("com" + File.separator + "github" + File.separator + "kotooriiii" + File.separator + "files" + File.separator + "guardREADME.txt", guards_folder, true);
        saveResource("com" + File.separator + "github" + File.separator + "kotooriiii" + File.separator + "files" + File.separator + "bankerREADME.txt", bankers_folder, true);
        saveResource("com" + File.separator + "github" + File.separator + "kotooriiii" + File.separator + "files" + File.separator + "bankREADME.txt", bank_folder, true);

        load();

    }

    public static void load() {

        for (File file : clans_folder.listFiles()) {

            if (!file.getName().endsWith(".yml"))
                continue;

            Clan clan = readClanFile(file);
            if (clan == null) {
                LostShardK.logger.info("\n\n" + "There was a clan file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
            clan.forceCreate();
            for (UUID playerUUID : clan.getAllUUIDS()) {
                playerUUIDClanMap.put(playerUUID, clan);
            }

        }

        //DONE loading all clans to arraylist! We should now map players!!


        for (File file : hostility_platform_folder.listFiles()) {
            if (!file.getName().endsWith(".obj"))
                continue;

            HostilityPlatform platform = readPlatformFile(file);
            if (platform == null) {
                LostShardK.logger.info("\n\n" + "There was a hostility file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
            platforms.add(platform);
        }

        for (File file : guards_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            ShardGuard guard = readShardGuardFile(file);
            if (guard == null) {
                LostShardK.logger.info("\n\n" + "There was a guard file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
        }

        for (File file : bankers_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            ShardBanker banker = readShardBankerFile(file);
            if (banker == null) {
                LostShardK.logger.info("\n\n" + "There was a banker file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
        }

        for (File file : bank_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            ShardBanker banker = readShardBankerFile(file);
            if (banker == null) {
                LostShardK.logger.info("\n\n" + "There was a banker file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
        }
    }

    public static HostilityPlatform readPlatformFile(File platformFile) {
        try {
            FileInputStream fis = new FileInputStream(platformFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HostilityPlatform platform = (HostilityPlatform) ois.readObject();
            return platform;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Clan readClanFile(File clanFile) {

        final String delimiter = ", ";

        UUID clanID = UUID.fromString(clanFile.getName().substring(0, clanFile.getName().indexOf('.')));
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(clanFile);
        String clanName = yaml.getString("Name");
        String clanTag = yaml.getString("Tag");
        String clanStringColor = yaml.getString("Color");
        String clanStringFriendlyFireBoolean = yaml.getString("FriendlyFire");
        String clanStringHostilityBuffBoolean = yaml.getString("HostilityBuff");
        String clanStringHostilityWinsInt = yaml.getString("HostilityWins");
        if (clanName == null || clanTag == null || clanStringColor == null || clanStringFriendlyFireBoolean == null || clanID == null || clanStringHostilityBuffBoolean == null || clanStringHostilityWinsInt == null) {
            LostShardK.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". The name, tag, color, friendlyfire, hostilitybuff, hostilitywins or id of the clan is corrupted/missing.");
            return null;
        }


        ChatColor clanColor = ChatColor.getByChar(clanStringColor.substring(1));

        boolean clanFriendlyFire = Boolean.valueOf(clanStringFriendlyFireBoolean);
        boolean clanHostilityBuff = Boolean.valueOf(clanStringHostilityBuffBoolean);
        int clanHostilityWins = Integer.parseInt(clanStringHostilityWinsInt);

        Clan clan = new Clan(clanID);
        switch (clan.forceName(clanName)) {
            case 0:
                //we could use a logger
                break;
            case 30:
                LostShardK.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". The name of the clan was unable to be read.");
                return null;
            case 21:
                LostShardK.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". There is already a clan with that name.");
                return null;
        }

        switch (clan.forceTag(clanTag)) {
            case 0:
                //we could use a logger
                break;
            case 30:
                LostShardK.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". The tag of the clan was unable to be read.");
                return null;
            case 21:
                LostShardK.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". There is already a clan with that tag.");
                return null;
        }
        clan.setColor(clanColor);
        clan.setFriendlyFire(clanFriendlyFire);
        clan.setHostilityBuff(clanHostilityBuff);
        clan.setHostilityWins(clanHostilityWins);

        ClanRank[] ranks = ClanRank.values();

        //starts w member then goes all the way to leader
        for (int i = 0; i < ranks.length; i++) {
            //get list
            String clanRankList = yaml.getString(ranks[i].toString());
            if (clanRankList == null) {
                LostShardK.logger.info("There was an error reading the clan rank list of: " + ranks[i]);
                return null;
            }

            if (clanRankList.isEmpty()) {
                clan.updateRankUUIDS(ranks[i], new UUID[0]);
                continue;
            }
            //get uuid(playerName)
            String[] clanRankUsers = clanRankList.split(delimiter);


            UUID[] uuids = new UUID[clanRankUsers.length];

            //update to string uuid
            for (int j = 0;
                 j < clanRankUsers.length; j++) {
                clanRankUsers[j] = clanRankUsers[j].substring(0, clanRankUsers[j].indexOf("("));
            }

            //update string to uuid
            for (int j = 0; j < uuids.length; j++) {
                UUID tempUUID = UUID.fromString(clanRankUsers[j]);
                if (tempUUID == null) {
                    LostShardK.logger.info("There was an error reading a player uuid");
                    return null;
                }
                uuids[j] = tempUUID;
            }
            clan.updateRankUUIDS(ranks[i], uuids);
        }

        return clan;
    }

    public static ShardGuard readShardGuardFile(File shardGuardFile) {

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(shardGuardFile);
        String guardName = shardGuardFile.getName().substring(0, shardGuardFile.getName().indexOf('.'));
        Location guardPostLocation = yaml.getLocation("GuardPostLocation");


        if (guardPostLocation == null) {
            LostShardK.logger.info("There was an error reading the clan in file \"" + shardGuardFile.getName() + "\". The guard post location in the file might be corrupted or missing.");
            return null;
        }

        ShardGuard guard = new ShardGuard(guardName);
        guard.spawn(guardPostLocation);


        return guard;
    }

    public static ShardBanker readShardBankerFile(File shardBankerFile) {

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(shardBankerFile);
        String bankerName = shardBankerFile.getName().substring(0, shardBankerFile.getName().indexOf('.'));
        Location guardPostLocation = yaml.getLocation("BankerPostLocation");


        if (guardPostLocation == null) {
            LostShardK.logger.info("There was an error reading the clan in file \"" + shardBankerFile.getName() + "\". The banker post location in the file might be corrupted or missing.");
            return null;
        }

        ShardBanker banker = new ShardBanker(bankerName);
        banker.spawn(guardPostLocation);

        return banker;
    }

    public static Bank readBankFile(File bankFile) {

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(bankFile);
        String bankerName = bankFile.getName().substring(0, bankFile.getName().indexOf('.'));

        Inventory inventory = null;
        ArrayList<ItemStack> itemStackArrayList = new ArrayList<>();
        for (int i = 0; i != -1; i++) {
            ItemStack itemStack = yaml.getItemStack("" + i);
            if (itemStack == null)
                break;
            itemStackArrayList.add(itemStack);
        }
        ItemStack[] itemStacks = itemStackArrayList.toArray(new ItemStack[itemStackArrayList.size()]);

        //todo get rank to see slots
        inventory = Bukkit.createInventory(Bukkit.getPlayer(UUID.fromString(bankerName)), DonorTitle.getMaxSize(), Bank.NAME);
        inventory.addItem(itemStacks);

        Bank bank = new Bank(UUID.fromString(bankerName), inventory);
        return bank;
    }

    public static Bank readBankFile(UUID uuid) {

        File bankFile = new File(bank_folder + File.separator + uuid + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(bankFile);
        String bankerName = bankFile.getName().substring(0, bankFile.getName().indexOf('.'));

        Inventory inventory = null;
        ArrayList<ItemStack> itemStackArrayList = new ArrayList<>();
        for (int i = 0; i != -1; i++) {
            ItemStack itemStack = yaml.getItemStack("" + i);
            if (itemStack == null)
                break;
            itemStackArrayList.add(itemStack);
        }
        ItemStack[] itemStacks = itemStackArrayList.toArray(new ItemStack[itemStackArrayList.size()]);

        //todo get rank to see slots
        inventory = Bukkit.createInventory(Bukkit.getPlayer(UUID.fromString(bankerName)), DonorTitle.getMaxSize(), Bank.NAME);
        inventory.addItem(itemStacks);

        Bank bank = new Bank(UUID.fromString(bankerName), inventory);
        return bank;
    }

    public static void write(Clan clan) {
        UUID clanID = clan.getID();
        String fileName = clanID + ".yml";
        File clanFile = new File(clans_folder + File.separator + fileName);

        try {
            if (!clanFile.exists())
                clanFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(clanFile);
        yaml.set("Name", clan.getName());
        yaml.set("Tag", clan.getTag());
        yaml.set("Color", clan.getColor().toString().replace(ChatColor.COLOR_CHAR + "", "&") + "");
        yaml.set("FriendlyFire", clan.isFriendlyFire() + "");
        yaml.set("HostilityWins", clan.getHostilityWins() + "");
        yaml.set("HostilityBuff", clan.hasHostilityBuff() + "");


        ClanRank[] ranks = ClanRank.values();

        //starts w member then goes all the way to leader
        for (ClanRank rank : ranks) {
            String list = "";

            UUID[] uuids = clan.getPlayerUUIDSBy(rank);
            for (int i = 0; i < uuids.length; i++) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuids[i]);

                if (i == uuids.length - 1)
                    list += uuids[i].toString() + "(" + player.getName() + ")";
                else
                    list += uuids[i].toString() + "(" + player.getName() + "), ";

            }
            yaml.set(rank.toString(), list);
        }
        try {
            yaml.save(clanFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(HostilityPlatform platform) {
        try {
            File file = new File(hostility_platform_folder + File.separator + platform.getName() + ".obj");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(platform);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(ShardGuard guard) {
        String name = guard.getName();
        String fileName = name + ".yml";
        File guardFile = new File(guards_folder + File.separator + fileName);

        try {
            if (!guardFile.exists())
                guardFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(guardFile);
        yaml.set("GuardPostLocation", guard.getSpawnLocation());

        try {
            yaml.save(guardFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(ShardBanker banker) {
        String name = banker.getName();
        String fileName = name + ".yml";
        File bankerFile = new File(bankers_folder + File.separator + fileName);

        try {
            if (!bankerFile.exists())
                bankerFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(bankerFile);
        yaml.set("BankerPostLocation", banker.getSpawnLocation());

        try {
            yaml.save(bankerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(Bank bank) {
        String fileName = bank.getPlayerUUID() + ".yml";
        File bankFile = new File(bank_folder + File.separator + fileName);

        try {
            if (!bankFile.exists())
                bankFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(bankFile);
        for (int i = 0; i < bank.getInventory().getSize(); i++) {
            yaml.set("" + i, bank.getInventory().getItem(i));
        }
        try {
            yaml.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void write(ShardGuard guard, String oldName) {
//        String oldFileName = oldName + ".yml";
//        File oldGuardFile = new File(guards_folder + File.separator + oldFileName);
//
//        if (oldGuardFile.exists())
//            oldGuardFile.delete();
//
//        String name = guard.getName();
//        String fileName = name + ".yml";
//        File guardFile = new File(guards_folder + File.separator + fileName);
//
//        try {
//            if (!guardFile.exists())
//                guardFile.createNewFile();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(guardFile);
//        yaml.set("GuardPostLocation", guard.getGuardPost());
//
//        try {
//            yaml.save(guardFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void removeFile(Clan clan) {
        UUID clanID = clan.getID();
        String fileName = clanID + ".yml";
        File clanFile = new File(clans_folder + File.separator + fileName);

        if (clanFile.exists())
            clanFile.delete();

    }

    public static void removeFile(HostilityPlatform platform) {
        File clanFile = new File(clans_folder + File.separator + platform.getName() + ".obj");

        if (clanFile.exists())
            clanFile.delete();
    }

    public static void removeFile(ShardGuard guard) {
        String name = guard.getName();
        String fileName = name + ".yml";
        File guardFile = new File(guards_folder + File.separator + fileName);

        if (guardFile.exists())
            guardFile.delete();

    }

    public static void removeFile(ShardBanker banker) {
        String name = banker.getName();
        String fileName = name + ".yml";
        File bankerFile = new File(bankers_folder + File.separator + fileName);

        if (bankerFile.exists())
            bankerFile.delete();

    }

    public static void removeFile(Bank bank) {
        String fileName = bank.getPlayerUUID() + ".yml";
        File bankerFile = new File(bank_folder + File.separator + fileName);

        if (bankerFile.exists())
            bankerFile.delete();

    }


    private static void saveResource(String resourcePath, File out_to_folder, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = LostShardK.plugin.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + LostShardK.plugin.getName());
            } else {
                int lastIndex = resourcePath.lastIndexOf(47) + 1;


                File outFile = new File(out_to_folder, resourcePath.substring(lastIndex >= 0 ? lastIndex : 0));
                File outDir = out_to_folder; //new File(out_to_folder); //, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        LostShardK.plugin.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[4096];

                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException var10) {
                    LostShardK.plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, var10);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

}
