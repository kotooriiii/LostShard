package com.github.kotooriiii.combatlog;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class CombatTaggedPlayer {
    private UUID defenderUUID;
    private ArrayList<UUID> attackersUUIDSet;

    public CombatTaggedPlayer(UUID defenderUUID) {
        this.defenderUUID = defenderUUID;
        this.attackersUUIDSet = new ArrayList<>();
    }

    public boolean addAttacker(Player attacker) {
        return addAttacker(attacker.getUniqueId());
    }

    public boolean addAttacker(UUID attackerUUID) {
        return this.attackersUUIDSet.add(attackerUUID);
    }

    public OfflinePlayer[] getAttackers() {
        ArrayList<OfflinePlayer> attackersOffline = new ArrayList();
        for (UUID uuid : attackersUUIDSet) {
            if (uuid == null)
                continue;
            attackersOffline.add(Bukkit.getOfflinePlayer(uuid));
        }

        return attackersOffline.toArray(new OfflinePlayer[attackersOffline.size()]);
    }

    public Player[] getOnlineAttackers() {
        ArrayList<Player> attackersOnline = new ArrayList();
        for (UUID uuid : attackersUUIDSet) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.isOnline())
                attackersOnline.add(offlinePlayer.getPlayer());
        }

        return attackersOnline.toArray(new Player[attackersOnline.size()]);
    }

    public boolean isAttacker(UUID attackerUUID) {
        return attackersUUIDSet.contains(attackerUUID);
    }
}
