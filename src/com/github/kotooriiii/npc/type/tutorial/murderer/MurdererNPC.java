package com.github.kotooriiii.npc.type.tutorial.murderer;

import com.github.kotooriiii.npc.type.tutorial.TutorialTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MurdererNPC {

    private Player target;

    public MurdererNPC(Player target) {
        this.target = target;
    }

    public NPC spawn(Location location) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.DARK_RED + "[Murderer] Colton");
        npc.spawn(location);
        npc.setProtected(true);
        MurdererTrait trait = new MurdererTrait(target);
        npc.addTrait(trait);
        npc.addTrait(new TutorialTrait());

        return npc;
    }

    public static Iterable<NPC> getAllMurdererNPC() {
        Iterable<NPC> allNPCS = CitizensAPI.getNPCRegistry().sorted();
        ArrayList<NPC> murdererNPC = new ArrayList<>();
        for (NPC npc : allNPCS) {
            if (!npc.hasTrait(MurdererTrait.class))
                continue;

            //Has GuardTrait
            murdererNPC.add(npc);
        }
        return murdererNPC;
    }
}
