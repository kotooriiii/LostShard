package com.github.kotooriiii.clans;

import com.github.kotooriiii.bank.Sale;
import com.github.kotooriiii.files.FileManager;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;


public class ClanManager {

    private HashSet<Clan> clans;
    private HashMap<UUID,Clan> playerUUIDClanMap;

    public ClanManager()
    {
        this.clans = new HashSet<>();
        this.playerUUIDClanMap = new HashMap<>();
    }

    public void joinClan(UUID uuid, Clan clan)
    {
        playerUUIDClanMap.put(uuid, clan);
    }

    public void leaveClan(UUID uuid)
    {
        playerUUIDClanMap.remove(uuid);
    }

    public void addClan(Clan clan, boolean saveToFile) {
        clans.add(clan);
        if (saveToFile)
            saveClan(clan);
    }

    public void saveClan(Clan clan) {
        FileManager.write(clan);
    }

    public void removeClan(Clan clan) {
        clans.remove(clan);
        FileManager.removeFile(clan);
    }

    /**
     * Gets the clan of a given Player UUID.
     *
     * @param playerUUID The UUID of a Player.
     * @return the clan of that player, null if not in a clan
     */
    public Clan getClan(UUID playerUUID) {
        if (playerUUID == null)
            return null;

        return playerUUIDClanMap.get(playerUUID);
    }

    /**
     * Gets the clan of a given clan name.
     *
     * @param clanName the name of the clan
     * @return the clan that matches that name, null if not found
     */
    public Clan getClan(String clanName) {
        if (clanName == null || clanName.isEmpty())
            return null;

        for (Clan clan : clans) {
            String iteratingClanName = clan.getName().toUpperCase();
            if (iteratingClanName.equals(clanName.toUpperCase()))
                return clan;
        }

        return null;
    }

    /**
     * Checks whether a player has a clan.
     *
     * @param uuid the uuid of the player
     * @return true if the player has a clan, false if no clan was found
     * @see ClanManager#getClan(UUID);
     */
    public  boolean hasClan(UUID uuid) {

        return getClan(uuid) != null;
    }

    public Clan[] getAllClans() {
        return clans.toArray(new Clan[clans.size()]);
    }
}
