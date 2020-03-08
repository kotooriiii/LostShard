package com.github.kotooriiii.hostility;

import com.github.kotooriiii.clans.Clan;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import sun.text.bidi.BidiLine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class HostilityPlatform implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private ArrayList<HostilityZone> zones;

    public HostilityPlatform(String name) {
        this.name = name;
        this.zones = new ArrayList<>();
    }

    public boolean contains(int x, int y, int z) {
        for (HostilityZone zone : getZones()) {
            if (zone.contains(x, y, z)) {
                return true;
            }
        }
        return false;
    }

    public Player[] getPlayers() {
        ArrayList<Player> playersInRegion = new ArrayList<Player>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline() && this.contains(player)) {
                playersInRegion.add(player);
            }
        }

        return playersInRegion.toArray(new Player[playersInRegion.size()]);
    }

    public boolean hasPlayers() {
        return this.getPlayers().length > 0;
    }

    public Player[] getClanlessPlayers() {
        Player[] players = getPlayers();
        ArrayList<Player> clanlessPlayers = new ArrayList<>();
        for (Player player : players) {
            if (Clan.getClan(player.getUniqueId()) == null) {
                clanlessPlayers.add(player);
            }
        }
        return clanlessPlayers.toArray(new Player[clanlessPlayers.size()]);
    }

    public Clan getUniqueClan() {
        Player[] players = getPlayers();

        Clan uniqueClan = null;
        for (int i = 0; i < players.length; i++) {
            if (uniqueClan == null) {
                uniqueClan = Clan.getClan(players[i].getUniqueId());
                if (uniqueClan == null)
                    continue;
            }


            if (!uniqueClan.isInThisClan(players[i].getUniqueId())) {
                Clan clan = Clan.getClan(players[i].getUniqueId());
                if (clan != null)
                    return null;
            }
        }
        return uniqueClan;
    }

    public Player[] getUniqueClanPlayers() {
        Player[] players = getPlayers();
        ArrayList<Player> uniquePlayersInClan = new ArrayList<>();
        Clan uniqueClan = null;
        for (int i = 0; i < players.length; i++) {
            if (uniqueClan == null) {
                uniqueClan = Clan.getClan(players[i].getUniqueId());
                if (uniqueClan == null)
                    continue;
            }

            if (!uniqueClan.isInThisClan(players[i].getUniqueId())) {
                Clan clan = Clan.getClan(players[i].getUniqueId());
                if (clan != null)
                    return null;
            } else {
                uniquePlayersInClan.add(players[i]);
            }
        }
        return uniquePlayersInClan.toArray(new Player[uniquePlayersInClan.size()]);
    }

    public boolean hasUniqueClan() {
        return getUniqueClan() != null;
    }

    public boolean contains(Block block) {
        for (HostilityZone zone : getZones()) {
            if (zone.contains(block)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Player player) {
        return contains(player.getLocation().getBlock());
    }

    //START BASIC GETTER AND SETTER

    public HostilityZone[] getZones() {
        return this.zones.toArray(new HostilityZone[this.zones.size()]);
    }

    public void addZone(HostilityZone zone) {
        this.zones.add(zone);
    }

    public boolean undo() {
        if (this.zones.isEmpty())
            return false;
        this.zones.remove(this.zones.size() - 1);
        return true;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
