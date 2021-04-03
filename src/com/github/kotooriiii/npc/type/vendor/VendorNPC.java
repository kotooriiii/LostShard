package com.github.kotooriiii.npc.type.vendor;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.type.banker.BankerTrait;
import com.github.kotooriiii.plots.struct.Plot;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.UUID;

import static com.github.kotooriiii.npc.type.banker.BankerNPC.getAllBankerNPC;

public class VendorNPC {

    private static final int WITHIN_DISTANCE = 5;
    private String name;
    private UUID plotUUID;
    private boolean isStaff;
    private final static int MAX_HISTORY = 5, MAX_SLOTS = 5, MAX_INNER_SLOTS = 3;

    public VendorNPC() {

    }

    public VendorNPC(String name, UUID plotUUID) {
        this.name = name;
        this.plotUUID = plotUUID;
    }
    public VendorNPC(String name, boolean isStaff) {
        this.name = name;
        this.plotUUID = null;
        this.isStaff = isStaff;
    }

    public static void checkLives() {
        for (NPC npc : getAllVendorNPC()) {
            if (!LostShardPlugin.getPlotManager().isStandingOnPlot(npc.getStoredLocation())) {
                final VendorTrait trait = npc.getTrait(VendorTrait.class);
                if (trait.isStaff())
                {
                    continue;
                }

                    npc.getTrait(VendorTrait.class).dieSomehow();
            }
        }

        for (NPC npc : getAllBankerNPC()) {
            if (!LostShardPlugin.getPlotManager().isStandingOnPlot(npc.getStoredLocation())) {
                final BankerTrait trait = npc.getTrait(BankerTrait.class);
                if (trait.isStaffBanker())
                    continue;
                trait.dieSomehow();
            }
        }
    }

    public static int getWithinDistance() {
        return WITHIN_DISTANCE;
    }

    public void spawn(Location location) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.GRAY + "[Vendor] " + ChatColor.DARK_PURPLE + name);
        npc.spawn(location);
        npc.setProtected(true);
        VendorTrait vendorTrait = new VendorTrait(name, plotUUID, location);
        if(isStaff)
            vendorTrait.setStaff(true);
        npc.addTrait(vendorTrait);
    }

    public static Iterable<NPC> getAllVendorNPC() {
        Iterable<NPC> allNPCS = CitizensAPI.getNPCRegistry().sorted();
        ArrayList<NPC> vendorNPC = new ArrayList<>();
        for (NPC npc : allNPCS) {
            if (!npc.hasTrait(VendorTrait.class))
                continue;

            //Has trait
            vendorNPC.add(npc);
        }
        return vendorNPC;
    }

    public static NPC getNearestVendor(final Location location) {
        NPC nearestVendor = null;
        double nearestDistance = Double.MAX_VALUE;
        for (NPC npc : getAllVendorNPC()) {

            if (!npc.getStoredLocation().getWorld().equals(location.getWorld()))
                continue;

            double distance = npc.getStoredLocation().distance(location);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestVendor = npc;
            }
        }

        return nearestVendor;
    }

    public static boolean isWithinDistanceOfOtherVendor(final Location location) {
        final NPC nearestVendor = getNearestVendor(location);

        if (nearestVendor == null)
            return false;

        return nearestVendor.getStoredLocation().distance(location) < WITHIN_DISTANCE;
    }


    public static ArrayList<NPC> getVendorInPlot(Plot plot) {
        ArrayList<NPC> list = new ArrayList<>();
        for (NPC npc : getAllVendorNPC()) {
            if (plot.contains(npc.getStoredLocation()))
                list.add(npc);
        }
        return list;
    }

    public static int getMaxHistory() {
        return MAX_HISTORY;
    }

    public static int getMaxSlots() {
        return MAX_SLOTS;
    }

    public static int getMaxInnerSlots() {
        return MAX_INNER_SLOTS;
    }
}
