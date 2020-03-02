package com.github.kotooriiii.hostility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import sun.text.bidi.BidiLine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class HostilityPlatform implements Serializable {

    private String name;

    private ArrayList<HostilityZone> zones;

    public HostilityPlatform(String name) {
        this.name = name;
    }

    public boolean contains(int x, int z) {
        for (HostilityZone zone : getZones()) {
            if (zone.contains(x, z)) {
                return true;
            }
        }
        return false;
    }

    public Player[] getPlayers()
    {
        ArrayList<Player> playersInRegion = new ArrayList<Player>();

        for(Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline() && this.contains(player))
            {
                playersInRegion.add(player);
            }
        }

        return playersInRegion.toArray(new Player[playersInRegion.size()]);
    }

    public boolean hasPlayers()
    {
return this.getPlayers().length > 0;
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
