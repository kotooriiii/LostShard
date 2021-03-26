package com.github.kotooriiii.npc.type.banker;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;

public class BankerNPC {

    private String name;

    public BankerNPC() {

    }

    public BankerNPC(String name) {
        this.name = name;
    }

    public void spawn(Location location) {
        spawn(null, location);
    }

    public void spawn(Plot plot, Location location) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.GRAY + "[Banker] " + ChatColor.YELLOW + name);
        npc.spawn(location);
        npc.setProtected(true);
        BankerTrait bankerTrait = new BankerTrait(name, plot == null ? null : plot.getPlotUUID(), location);
        npc.addTrait(bankerTrait);
    }

    public static Iterable<NPC> getAllBankerNPC() {
        Iterable<NPC> allNPCS = CitizensAPI.getNPCRegistry().sorted();
        ArrayList<NPC> bankerNPCS = new ArrayList<>();
        for (NPC npc : allNPCS) {
            if (!npc.hasTrait(BankerTrait.class))
                continue;

            //Has GuardTrait
            bankerNPCS.add(npc);
        }
        return bankerNPCS;
    }


    public static ArrayList<NPC> getBankersInPlot(Plot plot) {
        ArrayList<NPC> list = new ArrayList<>();
        for (NPC npc : getAllBankerNPC()) {
            if (plot.contains(npc.getStoredLocation()))
                list.add(npc);
        }
        return list;
    }

    public static NPC getNearestBanker(final Location location) {
        NPC nearestBanker = null;
        double nearestDistance = Double.MAX_VALUE;
        for (NPC npc : getAllBankerNPC()) {

            if (!npc.getStoredLocation().getWorld().equals(location.getWorld()))
                continue;

            double distance = npc.getStoredLocation().distance(location);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestBanker = npc;
            }
        }

        return nearestBanker;
    }
}
