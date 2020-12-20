package com.github.kotooriiii.files;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.bank.Sale;
import com.github.kotooriiii.bannedplayer.BannedPlayer;
import com.github.kotooriiii.channels.IgnorePlayer;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.clans.ClanRank;
import com.github.kotooriiii.discord.links.LinkPlayer;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.muted.MutedPlayer;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.listeners.SignChangeListener;
import com.github.kotooriiii.plots.struct.*;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.ranks.RankType;
import com.github.kotooriiii.skills.Skill;
import com.github.kotooriiii.skills.SkillBuild;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.skills.SkillType;
import com.github.kotooriiii.sorcery.Gate;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.wands.Glow;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.status.shrine.Shrine;
import com.github.kotooriiii.status.shrine.ShrineType;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;

import static com.github.kotooriiii.data.Maps.*;

public final class FileManager {
    private static File plugin_folder = LostShardPlugin.plugin.getDataFolder();
    private static File clans_folder = new File(plugin_folder + File.separator + "clans");
    private static File hostility_platform_folder = new File(plugin_folder + File.separator + "hostility" + File.separator + "platforms");
    private static File bank_folder = new File(plugin_folder + File.separator + "bank");
    private static File status_folder = new File(plugin_folder + File.separator + "statuses");
    private static File sales_folder = new File(plugin_folder + File.separator + "sales");
    private static File stats_folder = new File(plugin_folder + File.separator + "stats");
    private static File plots_folder = new File(plugin_folder + File.separator + "plots");
    private static File plots_staff_folder = new File(plots_folder + File.separator + "staff");
    private static File plots_players_folder = new File(plots_folder + File.separator + "players");

    private static File skills_folder = new File(plugin_folder + File.separator + "skills");
    private static File marks_folder = new File(plugin_folder + File.separator + "marks");
    private static File gates_folder = new File(plugin_folder + File.separator + "gates");

    private static File muted_folder = new File(plugin_folder + File.separator + "muted");
    private static File ranks_folder = new File(plugin_folder + File.separator + "ranks");
    private static File discord_folder = new File(plugin_folder + File.separator + "discord");
    private static File links_folder = new File(discord_folder + File.separator + "links");
    private static File shrines_folder = new File(plugin_folder + File.separator + "shrines");
    private static File buildchanger_folder = new File(plugin_folder + File.separator + "buildchanger");
    private static File ignoredPlayer_folder = new File(plugin_folder + File.separator + "ignored_player");

    private static File config = new File(plugin_folder + File.separator + "config.yml");


    private static File banned_folder = new File(plugin_folder + File.separator + "banned-players");


    private FileManager() {
    }

    public static void reset() {
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "* * * \nThe server has been ordered to reset all LostShard content.\nShutting down server to finalize deleting content.\n* * *");
        LostShardPlugin.setReset(true);
        Bukkit.getServer().shutdown();
    }

    public static void init() {

        plugin_folder.mkdir();
        clans_folder.mkdir();
        hostility_platform_folder.mkdirs();
        bank_folder.mkdirs();
        status_folder.mkdirs();
        sales_folder.mkdirs();
        stats_folder.mkdirs();
        plots_folder.mkdirs();
        plots_staff_folder.mkdirs();
        plots_players_folder.mkdirs();
        gates_folder.mkdirs();

        skills_folder.mkdirs();
        marks_folder.mkdirs();
        muted_folder.mkdirs();
        banned_folder.mkdirs();
        ranks_folder.mkdirs();
        discord_folder.mkdirs();
        links_folder.mkdirs();
        shrines_folder.mkdirs();
        buildchanger_folder.mkdirs();
        ignoredPlayer_folder.mkdirs();

        saveResource("resources" + File.separator + "clanREADME.txt", clans_folder, true);
        saveResource("resources" + File.separator + "hostilityREADME.txt", hostility_platform_folder, true);
        saveResource("resources" + File.separator + "bankREADME.txt", bank_folder, true);

        load();

    }

    public static void load() {

        for (File file : clans_folder.listFiles()) {

            if (!file.getName().endsWith(".yml"))
                continue;

            Clan clan = readClanFile(file);
            if (clan == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a clan file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
            LostShardPlugin.getClanManager().addClan(clan, false);
            for (UUID playerUUID : clan.getAllUUIDS()) {
                LostShardPlugin.getClanManager().joinClan(playerUUID, clan);
            }

        }

        //DONE loading all clans to arraylist! We should now map players!!

        HashSet<Location> locations = new HashSet<>();
        for (File file : buildchanger_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;
            locations = readBuildChangers(file);
            break;
        }
        SignChangeListener.setBuildChangers(locations);


        for (File file : hostility_platform_folder.listFiles()) {
            if (!file.getName().endsWith(".obj"))
                continue;

            HostilityPlatform platform = readPlatformFile(file);
            if (platform == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a hostility file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
            platform.runCountdown();
            platforms.add(platform);
        }


        for (File file : status_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            StatusPlayer statusPlayer = readStatusPlayer(file);

            if (statusPlayer == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a status file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
        }

        for (File file : ranks_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            RankPlayer rankPlayer = readRankPlayer(file);

            if (rankPlayer == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a rank file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
        }

        for (File file : bank_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            Bank bank = FileManager.readBankFile(file);
            if (bank == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a bank file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
            LostShardPlugin.getBankManager().addBank(bank, false);
        }


        for (File file : sales_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            Sale sale = readSale(file);

            if (sale == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a sale file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
        }

        for (File file : stats_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            Stat stat = readStat(file);

            if (stat == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a stat file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
        }


        //Created a method since it was a long process
        readPlots();

        for (File file : skills_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            SkillPlayer skillPlayer = readSkill(file);
            if (skillPlayer == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a skill file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }

            LostShardPlugin.getSkillManager().addSkillPlayer(skillPlayer, false);
        }

        for (File file : marks_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            MarkPlayer markPlayer = readMarks(file);
            if (markPlayer == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a marks file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
            MarkPlayer.add(markPlayer);
        }

        for (File file : muted_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            MutedPlayer mutedPlayer = readMutedPlayer(file);
            if (mutedPlayer == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a muted file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
            mutedPlayer.add();
        }

        for (File file : links_folder.listFiles()) {
            if (!file.getName().endsWith(".obj"))
                continue;

            LinkPlayer linkPlayer = readLinkPlayer(file);
            if (linkPlayer == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a link file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
            linkPlayer.addToMap();
        }

        for (File file : shrines_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            readShrine(file);
        }


        for (File file : gates_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;

            readGate(file);
        }

        for (File file : ignoredPlayer_folder.listFiles()) {
            if (!file.getName().endsWith(".yml"))
                continue;
            readIgnoredPlayer(file);
        }

        HashMap<UUID, BannedPlayer> bannedPlayers = new HashMap<>();
        for (File file : banned_folder.listFiles()) {
            if (!file.getName().endsWith(".obj"))
                continue;

            BannedPlayer bannedPlayer = readBannedPlayer(file);
            if (bannedPlayer == null) {
                LostShardPlugin.logger.info("\n\n" + "There was a banned player file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
            bannedPlayers.put(bannedPlayer.getPlayerUUID(), bannedPlayer);
        }
        LostShardPlugin.getBanManager().
                setBannedPlayers(bannedPlayers);
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
        int manaTimer = yaml.getInt("ManaTimer");
        int staminaTimer = yaml.getInt("StaminaTimer");
        int enhanceTimer = yaml.getInt("EnhanceTimer");
        int igniteTimer = yaml.getInt("IgniteTimer");

        String clanStringHostilityWinsInt = yaml.getString("HostilityWins");
        if (clanName == null || clanTag == null || clanStringColor == null || clanStringFriendlyFireBoolean == null || clanID == null || clanStringHostilityWinsInt == null) {
            LostShardPlugin.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". The name, tag, color, friendlyfire, hostilitybuff, hostilitywins or id of the clan is corrupted/missing.");
            return null;
        }


        ChatColor clanColor = ChatColor.getByChar(clanStringColor.substring(1));

        boolean clanFriendlyFire = Boolean.valueOf(clanStringFriendlyFireBoolean);
        int clanHostilityWins = Integer.parseInt(clanStringHostilityWinsInt);

        Clan clan = new Clan(clanID);
        switch (clan.forceName(clanName)) {
            case 0:
                //we could use a logger
                break;
            case 30:
                LostShardPlugin.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". The name of the clan was unable to be read.");
                return null;
            case 21:
                LostShardPlugin.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". There is already a clan with that name.");
                return null;
        }

        switch (clan.forceTag(clanTag)) {
            case 0:
                //we could use a logger
                break;
            case 30:
                LostShardPlugin.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". The tag of the clan was unable to be read.");
                return null;
            case 21:
                LostShardPlugin.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". There is already a clan with that tag.");
                return null;
        }
        clan.setColor(clanColor);
        clan.setFriendlyFire(clanFriendlyFire);
        clan.setManaBuffTimer(manaTimer);
        clan.setStaminaBuffTimer(staminaTimer);
        clan.setEnhanceTimer(enhanceTimer);
        clan.setIgniteTimer(igniteTimer);
        clan.setHostilityWins(clanHostilityWins);

        ClanRank[] ranks = ClanRank.values();

        //starts w member then goes all the way to leader
        for (int i = 0; i < ranks.length; i++) {
            //get list
            String clanRankList = yaml.getString(ranks[i].toString());
            if (clanRankList == null) {
                LostShardPlugin.logger.info("There was an error reading the clan rank list of: " + ranks[i]);
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
                    LostShardPlugin.logger.info("There was an error reading a player uuid");
                    return null;
                }
                uuids[j] = tempUUID;
            }
            clan.updateRankUUIDS(ranks[i], uuids);
        }

        return clan;
    }


    public static Bank readBankFile(File bankFile) {

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(bankFile);
        String bankerName = bankFile.getName().substring(0, bankFile.getName().indexOf('.'));

        Inventory inventory = null;

        UUID uuid = UUID.fromString(bankerName);
        RankPlayer rankPlayer = RankPlayer.wrap(uuid);
        inventory = Bukkit.createInventory(Bukkit.getPlayer(uuid), rankPlayer.getRankType().getBankInventorySize(), Bank.NAME);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = yaml.getItemStack("chest." + i);
            if (item == null)
                continue;
            if (item.getType().equals(Material.AIR))
                continue;

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> list = meta.getLore();
                if (list != null && !list.isEmpty()) {
                    String lastLine = list.get(list.size() - 1);
                    if (lastLine.equals(Glow.NAME)) {
                        list.remove(list.size() - 1);
                        if (list.isEmpty())
                            meta.setLore(null);
                        else
                            meta.setLore(list);
                        Glow glow = new Glow(new NamespacedKey(LostShardPlugin.plugin, Glow.NAME));
                        meta.addEnchant(glow, 1, true);
                        item.setItemMeta(meta);

                    }
                }

            }

            inventory.setItem(i, item);
        }


        String currencyString = yaml.getString("Currency");
        double currencyNum = 0.0F;
        if (currencyString == null) {

        } else {
            currencyNum = Double.parseDouble(currencyString);
        }

        Bank bank = new Bank(uuid, inventory, currencyNum);
        return bank;
    }

    public static Bank readBankFile(UUID uuid) {

        File bankFile = new File(bank_folder + File.separator + uuid + ".yml");
        return readBankFile(bankFile);
    }

    public static StatusPlayer readStatusPlayer(File statusFile) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(statusFile);
        String uuid = statusFile.getName().substring(0, statusFile.getName().indexOf('.'));

        String name = yaml.getString("Status");
        int kills = yaml.getInt("MurderCount");
        long atoneMillis = yaml.getLong("Atone");

        Instant instant = Instant.ofEpochMilli(atoneMillis);
        ZonedDateTime lastAtoneDate = ZonedDateTime.ofInstant(instant, ZoneId.of("America/New_York"));

        Status status = Status.matchStatus(name);
        status = Status.newStatuses(status);
        StatusPlayer statusPlayer = new StatusPlayer(UUID.fromString(uuid), status, kills);
        statusPlayer.setLastAtoneDate(lastAtoneDate);
        return statusPlayer;
    }

    public static RankPlayer readRankPlayer(File rankFile) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(rankFile);
        String uuid = rankFile.getName().substring(0, rankFile.getName().indexOf('.'));

        String name = yaml.getString("RankType");
        RankType rankType = RankType.matchRankType(name);
        RankPlayer rankPlayer = new RankPlayer(UUID.fromString(uuid), rankType);
        return rankPlayer;
    }

    public static Sale readSale(File saleFile) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(saleFile);
        String idString = saleFile.getName().substring(0, saleFile.getName().indexOf('.'));

        String uuidString = yaml.getString("SellerUUID");
        ItemStack item = yaml.getItemStack("Item");
        int amount = yaml.getInt("Amount");
        double price = yaml.getDouble("Price");
        if (item == null || uuidString == null || idString == null)
            return null;
        UUID id = UUID.fromString(idString);
        UUID sellerUUID = UUID.fromString(uuidString);

        if (id == null || sellerUUID == null)
            return null;

        Sale sale = new Sale(id, UUID.fromString(uuidString), item, amount, price);
        LostShardPlugin.getSaleManager().addSale(sale, false);
        return sale;
    }

    public static Stat readStat(File statFile) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(statFile);
        String uuidString = statFile.getName().substring(0, statFile.getName().indexOf('.'));

        double stamina = yaml.getDouble("Stamina");
        double maxStamina = yaml.getDouble("MaxStamina");
        if (maxStamina == 0)
            maxStamina = 100;

        double mana = yaml.getDouble("Mana");
        double maxMana = yaml.getDouble("MaxMana");
        if (maxMana == 0)
            maxMana = 100;

        String title = yaml.getString("Title");
        boolean isGold = yaml.getBoolean("isGold");
        boolean isPrivate = yaml.getBoolean("Private");
        Location spawn = yaml.getLocation("Spawn");

        UUID playerUUID = UUID.fromString(uuidString);

        if (playerUUID == null || title == null)
            return null;

        Stat stat = new Stat(playerUUID);
        stat.setGold(isGold);
        stat.setStamina(stamina);
        stat.setMana(mana);
        stat.setMaxStamina(maxStamina);
        stat.setMaxMana(maxMana);
        stat.setPrivate(isPrivate);
        stat.setTitle(title);
        stat.setSpawn(spawn);
        return stat;
    }

    public static void readPlots() {
        for (File playerFolder : plots_players_folder.listFiles()) {
            if (!playerFolder.isDirectory())
                continue;

            String ownerUUIDString = playerFolder.getName();

            ShardPlotPlayer shardPlotPlayer = new ShardPlotPlayer(UUID.fromString(ownerUUIDString));
            shardPlotPlayer.add();


            for (File plotFile : playerFolder.listFiles()) {
                PlayerPlot playerPlot = readPlayerPlot(plotFile, UUID.fromString(ownerUUIDString));

                LostShardPlugin.getPlotManager().addPlot(playerPlot, false);
            }
        }

        for (File file : plots_staff_folder.listFiles()) {
            StaffPlot staffPlot = readStaffPlot(file);
            LostShardPlugin.getPlotManager().addPlot(staffPlot, false);

        }
    }


    public static PlayerPlot readPlayerPlot(File playerPlotFile, UUID ownerUUID) {

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(playerPlotFile);

        String plotIDString = playerPlotFile.getName().substring(0, playerPlotFile.getName().indexOf('.'));

        UUID plotID = UUID.fromString(plotIDString);

        //BASIC PLOT YAML
        String plotName = yaml.getString("Name");

        //The "center" field returns a world as well
        String worldUUIDString = yaml.getString("World");
        UUID worldUID = UUID.fromString(worldUUIDString);

        //This is automatically calculated when constructor initializes.
        String plotTypeName = yaml.getString("Type");
        PlotType plotType = PlotType.valueOf(plotTypeName);


        //This is automatically calculated when radius is calculated.
        int x1 = yaml.getInt("Zone.X1");
        int x2 = yaml.getInt("Zone.X2");
        int y1 = yaml.getInt("Zone.Y1");
        int y2 = yaml.getInt("Zone.Y2");
        int z1 = yaml.getInt("Zone.Z1");
        int z2 = yaml.getInt("Zone.Z2");
        //END

        int radius = yaml.getInt("Radius");
        double balance = yaml.getDouble("Balance");
        Location center = yaml.getLocation("Center");
        boolean isTown = yaml.getBoolean("Town");
        boolean isDungeon = yaml.getBoolean("Dungeon");
        long millis = yaml.getLong("CreationDateEpochMillis", ZonedDateTime.of(2020, 10, 29, 12 + 3, 0, 0, 0, ZoneId.of("America/New_York")).toInstant().toEpochMilli());

        List<String> friendsList = yaml.getStringList("Friends");
        ArrayList<UUID> friendsUUIDList = new ArrayList<UUID>();

        if (friendsList != null) {
            for (int i = 0; i < friendsList.size(); i++) {
                UUID friendUUID = UUID.fromString(friendsList.get(i));
                friendsUUIDList.add(friendUUID);
            }
        }

        List<String> jointOwnerList = yaml.getStringList("JointOwners");
        ArrayList<UUID> jointOwnerUUIDList = new ArrayList<UUID>();
        if (jointOwnerList != null) {
            for (int i = 0; i < jointOwnerList.size(); i++) {
                UUID jointOwnerUUID = UUID.fromString(jointOwnerList.get(i));
                jointOwnerUUIDList.add(jointOwnerUUID);
            }
        }

        PlayerPlot playerPlot = new PlayerPlot(plotName, ownerUUID, center);
        playerPlot.setID(plotID);
        playerPlot.setRadius(radius);
        playerPlot.setBalance(balance);
        playerPlot.setFriends(friendsUUIDList);
        playerPlot.setJointOwners(jointOwnerUUIDList);
        playerPlot.setTown(isTown);
        playerPlot.setDungeon(isDungeon);
        playerPlot.setCreationMillisecondsDate(millis);


        return playerPlot;
    }

    public static StaffPlot readStaffPlot(File staffPlotFile) {

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(staffPlotFile);

        String plotIDString = staffPlotFile.getName().substring(0, staffPlotFile.getName().indexOf('.'));

        UUID plotID = UUID.fromString(plotIDString);

        //BASIC PLOT YAML
        String plotName = yaml.getString("Name");
        String worldUUIDString = yaml.getString("World");
        UUID worldUID = UUID.fromString(worldUUIDString);

        //This is automatically calculated when constructor initializes.
        String plotTypeName = yaml.getString("Type");
        PlotType plotType = PlotType.valueOf(plotTypeName);


        //This is automatically calculated when radius is calculated.
        int x1 = yaml.getInt("Zone.X1");
        int x2 = yaml.getInt("Zone.X2");
        int y1 = yaml.getInt("Zone.Y1");
        int y2 = yaml.getInt("Zone.Y2");
        int z1 = yaml.getInt("Zone.Z1");
        int z2 = yaml.getInt("Zone.Z2");
        Zone zone = new Zone(x1, x2, y1, y2, z1, z2);
        //END

        Location spawn = yaml.getLocation("Spawn");

        switch (plotType) {
            case PLAYER:
            case DEFAULT:
            case STAFF_DEFAULT:
                return null;
            case STAFF_ARENA:
                Location spawnA = yaml.getLocation("SpawnA");
                Location spawnB = yaml.getLocation("SpawnB");

                ArenaPlot arenaPlot = new ArenaPlot(Bukkit.getWorld(worldUID), zone, plotName);
                arenaPlot.setID(plotID);
                if (spawn != null)
                    arenaPlot.setSpawn(spawn);
                if (spawnA != null)
                    arenaPlot.setSpawnA(spawnA);
                if (spawnB != null)
                    arenaPlot.setSpawnB(spawnB);
                return arenaPlot;
            case STAFF_HOSTILITY:
                HostilityPlot hostilityPlot = new HostilityPlot(Bukkit.getWorld(worldUID), zone, plotName);
                hostilityPlot.setID(plotID);
                if (spawn != null)
                    hostilityPlot.setSpawn(spawn);
                return hostilityPlot;
            case STAFF_SPAWN:
                SpawnPlot spawnPlot = new SpawnPlot(Bukkit.getWorld(worldUID), zone, plotName);
                spawnPlot.setID(plotID);
                if (spawn != null)
                    spawnPlot.setSpawn(spawn);
                return spawnPlot;

            case STAFF_ATONE:
                AtonePlot atonePlot = new AtonePlot(Bukkit.getWorld(worldUID), zone, plotName);
                atonePlot.setID(plotID);
                if (spawn != null)
                    atonePlot.setSpawn(spawn);
                return atonePlot;
            case STAFF_FFA:
                FFAPlot ffaPlot = new FFAPlot(Bukkit.getWorld(worldUID), zone, plotName);
                ffaPlot.setID(plotID);
                if (spawn != null)
                    ffaPlot.setSpawn(spawn);
                return ffaPlot;
            case STAFF_BRACKET:
                BracketPlot bracketPlot = new BracketPlot(Bukkit.getWorld(worldUID), zone, plotName);
                bracketPlot.setID(plotID);
                if (spawn != null)
                    bracketPlot.setSpawn(spawn);
                return bracketPlot;
        }
        return null;
    }

    public static SkillPlayer readSkill(File file) {

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String uuidString = file.getName().substring(0, file.getName().indexOf('.'));
        if (uuidString == null)
            return null;

        UUID uuid = UUID.fromString(uuidString);
        SkillPlayer skillPlayer = new SkillPlayer(uuid);
        int buildIndex = yaml.getInt("ActiveBuildIndex");


        ArrayList<SkillBuild> list = new ArrayList();
        for (int i = 0; i < SkillPlayer.MAX_BUILDS; i++) {

            SkillBuild skillBuild = new SkillBuild(skillPlayer);
            Skill[] skills = new Skill[SkillType.values().length];
            int skillIndex = 0;
            for (SkillType type : SkillType.values()) {
                float level = (float) yaml.getDouble(i + "." + type.name() + ".Level");
                float xp = (float) yaml.getDouble(i + "." + type.name() + ".XP");
                boolean isLocked = (boolean) yaml.getBoolean(i + "." + type.getName() + ".isLocked", false);
                Skill skill = new Skill(skillBuild, type);
                skill.setLevel(level, xp);
                skill.setLocked(isLocked);
                skills[skillIndex++] = skill;
            }
            skillBuild.setSkills(skills);
            list.add(skillBuild);
        }

        SkillBuild[] builds = list.toArray(new SkillBuild[0]);
        skillPlayer.setSkillBuilds(builds);
        skillPlayer.setActiveBuild(buildIndex);
        return skillPlayer;

    }

    public static HashSet<Location> readBuildChangers(File file) {

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        HashSet<Location> locations = new HashSet<>();

        ConfigurationSection section = yaml.getConfigurationSection("locations");
        if (section == null)
            return locations;

        for (String path : section.getKeys(false)) {
            Location location = yaml.getLocation("locations." + path);
            locations.add(location);
        }
        return locations;
    }


    public static MarkPlayer readMarks(File markFile) {

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(markFile);
        String uuidString = markFile.getName().substring(0, markFile.getName().indexOf('.'));

        MarkPlayer markPlayer = new MarkPlayer(UUID.fromString(uuidString));

        final Set<String> keys = yaml.getKeys(false);
        for (String markName : keys) {
            Location markLocation = yaml.getLocation(markName);
            markPlayer.addMark(markName, markLocation);
        }

        return markPlayer;
    }

    public static MutedPlayer readMutedPlayer(File mutedFile) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(mutedFile);
        String uuidString = mutedFile.getName().substring(0, mutedFile.getName().indexOf('.'));

        ZonedDateTime bannedTime = (ZonedDateTime) yaml.get("ZoneDateTime");

        UUID playerUUID = UUID.fromString(uuidString);

        if (playerUUID == null || bannedTime == null)
            return null;

        MutedPlayer mutedPlayer = new MutedPlayer(playerUUID, bannedTime);
        return mutedPlayer;
    }

    public static BannedPlayer readBannedPlayer(File bannedPlayerFile) {
        try {
            FileInputStream fis = new FileInputStream(bannedPlayerFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            BannedPlayer bannedPlayer = (BannedPlayer) ois.readObject();
            return bannedPlayer;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LinkPlayer readLinkPlayer(File linkPlayerFile) {
        try {
            FileInputStream fis = new FileInputStream(linkPlayerFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            LinkPlayer linkPlayer = (LinkPlayer) ois.readObject();
            return linkPlayer;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void readGate(File gateFile) {

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(gateFile);
        String uuidString = gateFile.getName().substring(0, gateFile.getName().indexOf('.'));
        UUID playerUUID = UUID.fromString(uuidString);

        LinkedList<Gate> gateLinkedList = new LinkedList<>();


        for (int i = 0; true; i++) {
            Location fromLocation = yaml.getLocation("Gates." + i + ".From");
            Location toLocation = yaml.getLocation("Gates." + i + ".To");

            if (fromLocation == null && toLocation == null)
                break;

            gateLinkedList.offer(new Gate(playerUUID, fromLocation, toLocation));
        }


        LostShardPlugin.getGateManager().setGatesOf(playerUUID, gateLinkedList);
    }

    public static void readShrine(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        String typeName = file.getName().substring(0, file.getName().indexOf('.'));
        ShrineType shrineType = ShrineType.valueOf(typeName);

        Set<String> paths = yaml.getConfigurationSection("shrines").getKeys(false);


        for (String path : paths) {
            Shrine shrine = Shrine.of(shrineType);
            shrine.setLocation(yaml.getLocation("shrines." + path));
            shrine.setUUID(UUID.fromString(path));
            LostShardPlugin.getShrineManager().addShrine(shrine, false);
        }
    }

    public static void readIgnoredPlayer(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        String fileName = file.getName().substring(0, file.getName().indexOf('.'));

        if (!fileName.equalsIgnoreCase(ignoredPlayer_folder.getName()))
            return;

        Set<String> paths = yaml.getConfigurationSection("ignoredList").getKeys(false);


        for (String path : paths) {
            IgnorePlayer ignorePlayer = new IgnorePlayer(UUID.fromString(path));
            List<String> uuidsString = yaml.getStringList("ignoredList." + path);
            HashSet<UUID> uuids = new HashSet<>();
            for (String uuidString : uuidsString)
                uuids.add(UUID.fromString(uuidString));
            ignorePlayer.setIgnoredPlayers(uuids);
            LostShardPlugin.getIgnoreManager().addIgnorePlayer(ignorePlayer, false);
        }
    }


    public static synchronized void write(Clan clan) {
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
        yaml.set("ManaTimer", clan.getManaTimer());
        yaml.set("StaminaTimer", clan.getStaminaTimer());
        yaml.set("EnhanceTimer", clan.getEnhanceTimer());
        yaml.set("IgniteTimer", clan.getIgniteTimer());


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

    public static synchronized void write(HostilityPlatform platform) {
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

    public static synchronized void write(Bank bank) {
        String fileName = bank.getPlayerUUID() + ".yml";
        File bankFile = new File(bank_folder + File.separator + fileName);

        try {
            if (!bankFile.exists())
                bankFile.createNewFile();
            else
                ;
            //Can always remove the file and work from anew to REMOVE ENTIRE contents from file. The bank in memory holds the
            // new information anyways so it will be fine. If they donate again then they get their items back.

        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(bankFile);

        for (int i = 0; i < bank.getInventory().getSize(); i++) {
            ItemStack item = bank.getInventory().getItem(i);

            if (item == null)
                yaml.set("chest." + i, new ItemStack(Material.AIR, 1));
            else {

                ItemStack enchantedItem = null;

                for (Enchantment enchantment : item.getEnchantments().keySet()) {

                    //Custom ench
                    if (enchantment.getKey().getKey().toLowerCase().equals(Glow.NAME.toLowerCase())) {
                        enchantedItem = new ItemStack(item.getType(), item.getAmount());

                        ItemMeta meta = item.getItemMeta();
                        if (meta != null) {
                            enchantedItem.setItemMeta(meta);
                        }
                        ItemMeta enchantedItemMeta = enchantedItem.getItemMeta();

                        List<String> list = enchantedItemMeta.getLore();
                        if (list == null) {
                            list = new ArrayList<>();
                        }

                        list.add(Glow.NAME);

                        enchantedItemMeta.setLore(list);
                        enchantedItem.setItemMeta(enchantedItemMeta);
                        break;
                    }
                }

                if (enchantedItem == null)
                    yaml.set("chest." + i, item);
                else
                    yaml.set("chest." + i, enchantedItem);

            }
        }
        yaml.set("Currency", bank.getCurrency());
        try {
            yaml.save(bankFile);
        } catch (IOException | NullPointerException e) {
            LostShardPlugin.plugin.getLogger().severe("Error: Check the bank of player uuid '" + bank.getPlayerUUID() + "'.");
            e.printStackTrace();
        }
    }

    public static synchronized void write(StatusPlayer statusPlayer) {
        String fileName = statusPlayer.getPlayerUUID() + ".yml";
        File statusFile = new File(status_folder + File.separator + fileName);

        try {
            if (!statusFile.exists())
                statusFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(statusFile);
        yaml.set("Status", statusPlayer.getStatus().getName());
        yaml.set("MurderCount", statusPlayer.getKills());
        yaml.set("Atone", statusPlayer.getLastAtoneDate().toInstant().toEpochMilli());

        try {
            yaml.save(statusFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void write(RankPlayer rankPlayer) {
        String fileName = rankPlayer.getPlayerUUID() + ".yml";
        File ranksFile = new File(ranks_folder + File.separator + fileName);

        try {
            if (!ranksFile.exists())
                ranksFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(ranksFile);
        yaml.set("RankType", rankPlayer.getRankType().getName());
        try {
            yaml.save(ranksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void write(Sale sale) {
        String fileName = sale.getID() + ".yml";
        File saleFile = new File(sales_folder + File.separator + fileName);

        try {
            if (!saleFile.exists())
                saleFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(saleFile);
        yaml.set("SellerUUID", sale.getSellerUUID().toString());
        yaml.set("Item", sale.getItemStack());
        yaml.set("Amount", sale.getAmount());
        yaml.set("Price", sale.getPrice());
        try {
            yaml.save(saleFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void write(Stat stat) {
        String fileName = stat.getPlayerUUID() + ".yml";
        File statFile = new File(stats_folder + File.separator + fileName);

        try {
            if (!statFile.exists())
                statFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(statFile);
        yaml.set("Stamina", stat.getStamina());
        yaml.set("MaxStamina", stat.getMaxStamina());
        yaml.set("Mana", stat.getMana());
        yaml.set("MaxMana", stat.getMaxMana());
        yaml.set("Title", stat.getTitle());
        yaml.set("isGold", stat.isGold());
        yaml.set("Private", stat.isPrivate());
        yaml.set("Spawn", stat.getSpawn());
        try {
            yaml.save(statFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void write(HashSet<Location> buildChangersLocation) {

        String fileName = buildchanger_folder.getName() + ".yml";
        File locations = new File(buildchanger_folder + File.separator + fileName);

        try {
            if (!locations.exists())
                locations.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(locations);
        Iterator<Location> locationIterator = buildChangersLocation.iterator();
        for (int i = 0; locationIterator.hasNext(); i++) {
            yaml.set("locations." + i, locationIterator.next());
        }

        try {
            yaml.save(locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static synchronized void write(Plot plot) {
        if (!plot.getType().isStaff()) {
            write((PlayerPlot) plot);
        } else {
            write((StaffPlot) plot);
        }
    }

    public static synchronized void write(PlayerPlot playerPlot) {
        File file;

        file = new File(plots_players_folder + File.separator + playerPlot.getOwnerUUID() + File.separator + playerPlot.getID() + ".yml");

        if (file.exists()) {
            file.delete();
        }

        int radius = playerPlot.getRadius();
        double balance = playerPlot.getBalance();
        Location center = playerPlot.getCenter();

        UUID[] friends = playerPlot.getFriends();
        UUID[] jointOwners = playerPlot.getJointOwners();

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        yaml.set("Name", playerPlot.getName());
        yaml.set("World", playerPlot.getWorld().getUID() + "");
        yaml.set("Type", playerPlot.getType().name());
        yaml.set("Zone.X1", playerPlot.getZone().getX1());
        yaml.set("Zone.X2", playerPlot.getZone().getX2());
        yaml.set("Zone.Y1", playerPlot.getZone().getY1());
        yaml.set("Zone.Y2", playerPlot.getZone().getY2());
        yaml.set("Zone.Z1", playerPlot.getZone().getZ1());
        yaml.set("Zone.Z2", playerPlot.getZone().getZ2());


        yaml.set("Radius", radius);
        yaml.set("Balance", balance);
        yaml.set("Center", center);
        yaml.set("Town", playerPlot.isTown());
        yaml.set("Dungeon", playerPlot.isDungeon());

        yaml.set("CreationDateEpochMillis", playerPlot.getCreationMillisecondsDate());

        ArrayList<String> friendsList = new ArrayList<>(friends.length);
        for (UUID uuid : friends)
            friendsList.add(uuid.toString());

        ArrayList<String> jointOwnerList = new ArrayList<>(jointOwners.length);
        for (UUID uuid : jointOwners)
            jointOwnerList.add(uuid.toString());


        yaml.set("Friends", friendsList);
        yaml.set("JointOwners", jointOwnerList);


        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static synchronized void write(StaffPlot staffPlot) {

        File file;

        file = new File(plots_staff_folder + File.separator + staffPlot.getID() + ".yml");
        if (file.exists()) {
            file.delete();
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        yaml.set("Name", staffPlot.getName());
        yaml.set("World", staffPlot.getWorld().getUID() + "");
        yaml.set("Type", staffPlot.getType().name());
        yaml.set("Zone.X1", staffPlot.getZone().getX1());
        yaml.set("Zone.X2", staffPlot.getZone().getX2());
        yaml.set("Zone.Y1", staffPlot.getZone().getY1());
        yaml.set("Zone.Y2", staffPlot.getZone().getY2());
        yaml.set("Zone.Z1", staffPlot.getZone().getZ1());
        yaml.set("Zone.Z2", staffPlot.getZone().getZ2());

        yaml.set("Spawn", staffPlot.getSpawn());

        if (staffPlot.getType().equals(PlotType.STAFF_ARENA)) {
            ArenaPlot arenaPlot = (ArenaPlot) staffPlot;
            yaml.set("SpawnA", arenaPlot.getSpawnA());
            yaml.set("SpawnB", arenaPlot.getSpawnB());

        }

        yaml.set("CreationDateEpochMillis", staffPlot.getCreationMillisecondsDate());


        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void write(Gate gate) {

        File file;
        file = new File(gates_folder + File.separator + gate.getSource() + ".yml");
        if (file.exists()) {
            file.delete();
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        LinkedList<Gate> gates = LostShardPlugin.getGateManager().getGatesOf(gate.getSource());

        for (int i = 0; i < gates.size(); i++) {
            yaml.set("Gates." + i + ".From", gates.get(i).getFrom());
            yaml.set("Gates." + i + ".To", gates.get(i).getTo());
        }

        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static synchronized void write(SkillPlayer skillPlayer) {
        try {
            File file = new File(skills_folder + File.separator + skillPlayer.getPlayerUUID() + ".yml");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();


            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.set("ActiveBuildIndex", skillPlayer.getActiveIndex());

            SkillBuild[] skillBuilds = skillPlayer.getSkillBuilds();
            for (int i = 0; i < skillBuilds.length; i++) {
                SkillBuild skillBuild = skillBuilds[i];
                Skill[] skills = skillBuild.getSkills();
                for (int j = 0; j < skills.length; j++) {
                    Skill skill = skills[j];
                    yaml.set(i + "." + skill.getType().name() + ".Level", (double) skill.getLevel());
                    yaml.set(i + "." + skill.getType().name() + ".XP", (double) skill.getXP());
                    yaml.set(i + "." + skill.getType().name() + ".isLocked", skill.isLocked());
                }
            }

            yaml.save(file);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void write(MarkPlayer markPlayer) {
        String fileName = markPlayer.getPlayerUUID() + ".yml";
        File markPlayerFile = new File(marks_folder + File.separator + fileName);

        try {
            if (markPlayerFile.exists()) {
                markPlayerFile.delete();
            }
            markPlayerFile.createNewFile();


        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(markPlayerFile);
        MarkPlayer.Mark[] marks = markPlayer.getMarks();

        for (int i = 0; i < marks.length; i++)
            yaml.set(marks[i].getName(), marks[i].getLocation());

        try {
            yaml.save(markPlayerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void write(MutedPlayer mutedPlayer) {
        String fileName = mutedPlayer.getMutedUUID() + ".yml";
        File mutedPlayerFile = new File(muted_folder + File.separator + fileName);

        try {
            if (!mutedPlayerFile.exists())
                mutedPlayerFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(mutedPlayerFile);
        yaml.set("ZoneDateTime", mutedPlayer.getBannedTime());
        try {
            yaml.save(mutedPlayerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void write(BannedPlayer bannedPlayer) {
        String fileName = bannedPlayer.getPlayerUUID() + ".obj";
        File file = new File(banned_folder + File.separator + fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(bannedPlayer);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void write(LinkPlayer linkPlayer) {
        String fileName = linkPlayer.getUserSnowflake() + ".obj";
        File file = new File(links_folder + File.separator + fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(linkPlayer);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static synchronized void write(ShardPlotPlayer shardPlotPlayer) {
        String fileName = shardPlotPlayer.getOwnerUUID() + "";
        File file = new File(plots_players_folder + File.separator + fileName);
        file.mkdirs();
        for (PlayerPlot playerPlot : shardPlotPlayer.getPlotsOwned()) {
            write(playerPlot);
        }

    }

    public static synchronized void write(Shrine shrine) {
        String fileName = shrine.getType().name() + ".yml";
        File shrineFile = new File(shrines_folder + File.separator + fileName);
        if (!shrineFile.exists()) {
            try {
                shrineFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(shrineFile);
        yaml.set("shrines." + shrine.getUUID().toString(), shrine.getLocation());

        try {
            yaml.save(shrineFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void write(IgnorePlayer ignorePlayer) {
        String fileName = ignoredPlayer_folder.getName() + ".yml";
        File ignorePlayerFile = new File(ignoredPlayer_folder + File.separator + fileName);
        if (!ignorePlayerFile.exists()) {
            try {
                ignorePlayerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> uuidsStringList = new ArrayList<>();
        for (UUID uuid : ignorePlayer.getIgnoredUUIDS())
            uuidsStringList.add(uuid.toString());

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(ignorePlayerFile);
        yaml.set("ignoredList." + ignorePlayer.getSource().toString(), uuidsStringList);

        try {
            yaml.save(ignorePlayerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void removeFile(Shrine shrine) {
        String fileName = shrine.getType().name() + ".yml";
        File shrineFile = new File(shrines_folder + File.separator + fileName);
        if (!shrineFile.exists())
            return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(shrineFile);
        yaml.set("shrines." + shrine.getUUID().toString(), null);
        try {
            yaml.save(shrineFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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


    public static void removeFile(Bank bank) {
        String fileName = bank.getPlayerUUID() + ".yml";
        File bankerFile = new File(bank_folder + File.separator + fileName);

        if (bankerFile.exists())
            bankerFile.delete();

    }

    public static void removeFile(Sale sale) {
        String fileName = sale.getID() + ".yml";
        File bankerFile = new File(sales_folder + File.separator + fileName);

        if (bankerFile.exists())
            bankerFile.delete();

    }

    public static void removeFile(Stat stat) {
        String fileName = stat.getPlayerUUID() + ".yml";
        File statFile = new File(stats_folder + File.separator + fileName);

        if (statFile.exists())
            statFile.delete();

    }

    public static void removeFile(Plot plot) {
        File plotFile;
        if (!plot.getType().isStaff()) {
            PlayerPlot playerPlot = ((PlayerPlot) plot);
            plotFile = new File(plots_players_folder + File.separator + playerPlot.getOwnerUUID() + File.separator + playerPlot.getID() + ".yml");
        } else {
            plotFile = new File(plots_staff_folder + File.separator + plot.getID() + ".yml");
        }

        if (plotFile.exists())
            plotFile.delete();

    }

    public static void removeFile(SkillPlayer skillPlayer) {
        File skillsFile = new File(skills_folder + File.separator + skillPlayer.getPlayerUUID() + ".obj");

        if (skillsFile.exists())
            skillsFile.delete();

    }

    public static void removeFile(MarkPlayer markPlayer) {
        File marksFile = new File(marks_folder + File.separator + markPlayer.getPlayerUUID() + ".yml");

        if (marksFile.exists())
            marksFile.delete();

    }

    public static void removeFile(MutedPlayer mutedPlayer) {
        File mutedPlayerFile = new File(muted_folder + File.separator + mutedPlayer.getMutedUUID() + ".yml");

        if (mutedPlayerFile.exists())
            mutedPlayerFile.delete();

    }

    public static void removeFile(BannedPlayer bannedPlayer) {
        File bannedPlayerFile = new File(banned_folder + File.separator + bannedPlayer.getPlayerUUID() + ".obj");

        if (bannedPlayerFile.exists())
            bannedPlayerFile.delete();

    }

    public static void removeFile(LinkPlayer linkPlayer) {
        File linkPlayerFile = new File(links_folder + File.separator + linkPlayer.getUserSnowflake() + ".obj");

        if (linkPlayerFile.exists())
            linkPlayerFile.delete();

    }

    @Deprecated
    public static void removeFile(ShardPlotPlayer shardPlotPlayer) {
        File shardPlotPlayerFolder = new File(plots_players_folder + File.separator + shardPlotPlayer.getOwnerUUID());

        if (!shardPlotPlayerFolder.isDirectory())
            return;
        if (shardPlotPlayerFolder.exists()) {
            for (File file : shardPlotPlayerFolder.listFiles()) {
                file.delete();
            }
        }
    }


    private static void saveResource(String resourcePath, File out_to_folder, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = LostShardPlugin.plugin.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + LostShardPlugin.plugin.getName());
            } else {
                int lastIndex = resourcePath.lastIndexOf(47) + 1;


                File outFile = new File(out_to_folder, resourcePath.substring(lastIndex >= 0 ? lastIndex : 0));
                File outDir = out_to_folder; //new File(out_to_folder); //, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        LostShardPlugin.plugin.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
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
                    LostShardPlugin.plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, var10);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }


}
