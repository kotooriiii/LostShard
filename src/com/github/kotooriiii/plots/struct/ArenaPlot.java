package com.github.kotooriiii.plots.struct;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.npc.type.banker.BankerNPC;
import com.github.kotooriiii.plots.PlotType;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ArenaPlot extends StaffPlot {

    private Location spawnA;
    private Location spawnB;

    public ArenaPlot(World world, Zone zone, String name) {
        super(world, zone, name);
        this.plotType = PlotType.STAFF_ARENA;
    }

    public Location getSpawnA() {
        return spawnA;
    }

    public void setSpawnA(Location spawnA) {
        this.spawnA = spawnA;

        LostShardPlugin.getPlotManager().savePlot(this);
    }

    public Location getSpawnB() {
        return spawnB;
    }

    public void setSpawnB(Location spawnB) {
        this.spawnB = spawnB;
        LostShardPlugin.getPlotManager().savePlot(this);
    }

    @Override
    public String info(Player perspectivePlayer) {
        String header = ChatColor.GOLD + "-" + getName() +"'s Plot Info-";
        String owner = ChatColor.YELLOW + "Owner: " + ChatColor.WHITE + "Nickolov";
        String size = ChatColor.YELLOW + "Size: " + ChatColor.WHITE + 10;
        String center = "The spawn has not been created yet.";
        if (getSpawn() != null)
            center = ChatColor.YELLOW + "Center: " + ChatColor.WHITE + "(" + getSpawn().getBlockX() + ", " + getSpawn().getBlockY() + ", " + getSpawn().getBlockZ() + ") " + ChatColor.YELLOW + "Distance from center: " + ChatColor.WHITE + 5;
        String bankers = "";
        for (NPC bankerNPC : BankerNPC.getAllBankerNPC()) {
            Location location = bankerNPC.getStoredLocation();
            if (this.contains(location)) {
                bankers += ChatColor.YELLOW + "Banker location: " + ChatColor.WHITE + "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")";
            }
        }
        String result = "";
        result += "\n" + header;
        result += "\n" + owner;
        result += "\n" + size;
        result += "\n" + center;
        result += "\n" + bankers;
        return result;
    }
}
