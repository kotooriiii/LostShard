package com.github.kotooriiii.files;

import com.github.kotooriiii.LostShardK;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.clans.ClanRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.UUID;
import java.util.logging.Level;

import static com.github.kotooriiii.data.Maps.clans;

public final class FileManager {
    private static File plugin_folder = LostShardK.plugin.getDataFolder();
    private static File clans_folder = new File(plugin_folder + File.separator + "clans");
    private static File clans_warning_file = new File(clans_folder + File.separator + "readme.txt");

    private FileManager() {
    }

    public static void init() {
        plugin_folder.mkdir();
        clans_folder.mkdir();
        saveResource("com" + File.separator + "github" + File.separator + "kotooriiii" + File.separator + "files" + File.separator + "readme.txt", clans_folder, true);
        load();

    }

    public static void load() {
        for (File file : clans_folder.listFiles()) {

            if (!file.getName().endsWith(".yml"))
                continue;

            Clan clan = read(file);
            if (clan == null) {
                LostShardK.logger.info("\n\n" + "There was a clan file that was not able to be read!\nFile name: " + file.getName() + "\n\n");
                continue;
            }
            clan.forceCreate();
        }
    }

    private static Clan read(File clanFile) {
        final String delimiter = ", ";


        UUID clanID = UUID.fromString(clanFile.getName().substring(0, clanFile.getName().indexOf('.')));
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(clanFile);
        String clanName = yaml.getString("Name");
        String clanTag = yaml.getString("Tag");
        String clanStringColor = yaml.getString("Color");
        String clanStringBoolean = yaml.getString("FriendlyFire");
        if (clanName == null || clanTag == null || clanStringColor == null || clanStringBoolean == null || clanID == null) {
            LostShardK.logger.info("There was an error reading the clan in file \"" + clanFile.getName() + "\". The name, tag, color, friendlyfire, or id of the clan is corrupted/missing.");
            return null;
        }
        ChatColor clanColor = ChatColor.getByChar(clanStringColor.replace('&', ChatColor.COLOR_CHAR));

        boolean clanFriendlyFire = Boolean.valueOf(clanStringBoolean);

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

    public static void write(Clan clan) {
        UUID clanID = clan.getID();
        String fileName = clanID + ".yml";
        File clanFile = new File(clans_folder + File.separator + fileName);

        try {
            if(!clanFile.exists())
            clanFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(clanFile);
        yaml.set("Name", clan.getName());
        yaml.set("Tag", clan.getTag());
        yaml.set("Color", clan.getColor().toString().replace("§", "&") + "");
        yaml.set("FriendlyFire", clan.isFriendlyFire() + "");

        ClanRank[] ranks = ClanRank.values();

        //starts w member then goes all the way to leader
        for (ClanRank rank : ranks) {
            String list = "";

            UUID[] uuids = clan.getPlayerUUIDSBy(rank);
            for (int i = 0; i < uuids.length; i++) {
                Player player = Bukkit.getPlayer(uuids[i]);

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


    public static void removeFile(Clan clan) {
        UUID clanID = clan.getID();
        String fileName = clanID + ".yml";
        File clanFile = new File(clans_folder + File.separator + fileName);

        if(clanFile.exists())
        clanFile.delete();

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
