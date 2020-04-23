package com.github.kotooriiii.status;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Staff {
    private UUID playerUUID;
    private StaffType type;

    private static HashMap<UUID,Staff> staffMap = new HashMap<>();

    public Staff(UUID playerUUID, StaffType type) {
        this.playerUUID = playerUUID;
        this.type = type;
        add(this);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public StaffType getType() {
        return type;
    }

    public void setType(StaffType type) {
        this.type = type;
    }

    public static void add(Staff staff)
    {
        if(staffMap.containsKey(staff))
            staffMap.remove(staff.getPlayerUUID());

        staffMap.put(staff.getPlayerUUID(), staff);

    }


    public static void remove(Staff staff)
    {
      staffMap.remove(staff.playerUUID);
    }

    public static Staff[] getStaff()
    {
        return staffMap.values().toArray(new Staff[staffMap.values().size()]);
    }

    public static Player[] getOnlineStaffPlayers()
    {
        ArrayList<Player> players = new ArrayList<Player>();
        for(Staff staff : getStaff())
        {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(staff.getPlayerUUID());
            if(offlinePlayer.isOnline())
                players.add(offlinePlayer.getPlayer());
        }
        return players.toArray(new Player[players.size()]);
    }

    public static HashMap<UUID, Staff> getStaffMap() {
        return staffMap;
    }

    public static boolean isStaff(UUID playerUUID)
    {
        return staffMap.containsKey(playerUUID);
    }

    public static Staff wrap(UUID playerUUID)
    {
        return staffMap.get(playerUUID);
    }
}
