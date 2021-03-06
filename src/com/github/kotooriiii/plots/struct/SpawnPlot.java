package com.github.kotooriiii.plots.struct;

import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.npc.type.banker.BankerNPC;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.plots.listeners.SignChangeListener;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SpawnPlot extends StaffPlot {

    public SpawnPlot(World world, Zone zone, String name) {
        super(world, zone, name);
        this.plotType = PlotType.STAFF_SPAWN;
    }

    @Override
    public String info(Player perspectivePlayer) {
        String header = ChatColor.GOLD + "-" + getName() + "'s Plot Info-";
        String owner = ChatColor.YELLOW + "Owner: " + ChatColor.WHITE + "Nickolov";
        String size = ChatColor.YELLOW + "Size: " + ChatColor.WHITE + 10;
        String center = "The spawn has not been created yet.";
        if (getSpawn() != null)
            center = ChatColor.YELLOW + "Center: " + ChatColor.WHITE + "(" + getSpawn().getBlockX() + ", " + getSpawn().getBlockY() + ", " + getSpawn().getBlockZ() + ") " + ChatColor.YELLOW + "Distance from center: " + ChatColor.WHITE + 5;
        Location signBuildLoc = SignChangeListener.getSignBuilder(perspectivePlayer.getLocation());
        String signBuilder = "";
        if (signBuildLoc != null)
            signBuilder = ChatColor.YELLOW + "Sign Builder: " + ChatColor.WHITE + "(" + signBuildLoc.getBlockX() + ", " + signBuildLoc.getBlockY() + ", " + signBuildLoc.getBlockZ() + ")";
        else
            signBuilder = ChatColor.YELLOW + "Sign Builder: " + ChatColor.WHITE + "NONE";
        String bankers = "";
        for (NPC shardBanker : BankerNPC.getAllBankerNPC()) {
            Location location = shardBanker.getStoredLocation();

            if(location==null)
                continue;

            if (this.contains(location)) {
                bankers += ChatColor.YELLOW + "Banker: " + ChatColor.WHITE + "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")";
            }
        }
        String result = "";
        result += "\n" + header;
        result += "\n" + owner;
        result += "\n" + size;
        result += "\n" + center;
        result += "\n" + signBuilder;
        result += "\n" + bankers;
        return result;
    }

}
