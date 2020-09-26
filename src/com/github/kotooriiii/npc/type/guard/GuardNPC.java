package com.github.kotooriiii.npc.type.guard;

import com.github.kotooriiii.npc.type.tutorial.TutorialTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;

public class GuardNPC  {

    private String name;

    public GuardNPC()
    {

    }

    public GuardNPC(String name) {
        this.name = name;
    }

    public void spawn(Location location) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.GRAY + "[Guard] " + ChatColor.YELLOW + name);
        npc.spawn(location);
        npc.setProtected(true);
        GuardTrait guardTrait = new GuardTrait(name, location);
        npc.addTrait(guardTrait);
    }

    public static Iterable<NPC> getAllGuardNPC()
    {
        Iterable<NPC> allNPCS = CitizensAPI.getNPCRegistry().sorted();
        ArrayList<NPC> guardNPCS = new ArrayList<>();
        for(NPC npc : allNPCS)
        {
            if(!npc.hasTrait(GuardTrait.class))
                continue;

            //Has GuardTrait
            guardNPCS.add(npc);
        }
        return guardNPCS;
    }

    public static NPC getNearestGuard(final Location location) {
        NPC nearestGuard = null;
        double nearestDistance = Double.MAX_VALUE;
        for (NPC npc : getAllGuardNPC()) {
            GuardTrait guardTrait = npc.getTrait(GuardTrait.class);
            if (guardTrait.isBusy() || guardTrait.isCalled())
                continue;

            if(!npc.getStoredLocation().getWorld().equals(location.getWorld()))
                continue;

            double distance = npc.getStoredLocation().distance(location);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestGuard = npc;
            }
        }

        return nearestGuard;
    }
}
