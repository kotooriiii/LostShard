package com.github.kotooriiii.register_system;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class RegisterManager {

    private HashSet<UUID> registeredPlayers;

    public RegisterManager() {
        registeredPlayers = new HashSet<>();
    }

    public void addPlayer(UUID uuid) {
        registeredPlayers.add(uuid);
    }

    public void removePlayer(UUID uuid) {
        registeredPlayers.remove(uuid);
    }

    public void addPlayer(Player player) {
        addPlayer(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
    }

    public boolean hasPlayer(UUID uuid)
    {
        return registeredPlayers.contains(uuid);
    }

    public boolean hasPlayer(Player player)
    {
        return hasPlayer(player.getUniqueId());
    }

    public Player[] getRegisteredPlayers()
    {
        ArrayList<Player> list = new ArrayList<>();

        for(UUID uuid : registeredPlayers)
        {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline())
                continue;

            list.add(player);
        }
        return list.toArray(new Player[list.size()]);
    }


}
