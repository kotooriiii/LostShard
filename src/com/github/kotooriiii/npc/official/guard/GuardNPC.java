package com.github.kotooriiii.npc.official.guard;

import com.github.kotooriiii.channels.events.ChatChannelListener;
import com.github.kotooriiii.npc.Skin;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class GuardNPC implements Listener {

    private String name;

    public GuardNPC()
    {

    }

    public GuardNPC(String name) {
        this.name = name;
    }

    public void spawn(Location location) {


        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "[Guard] " + name);
        npc.spawn(location);
        npc.setProtected(false);

        GuardTrait guardTrait = new GuardTrait();
        npc.addTrait(guardTrait);


    }

    @EventHandler
    public void onCrouch(PlayerToggleSneakEvent event) {

        Location location = event.getPlayer().getLocation();
        GuardNPC guardNPC = new GuardNPC(event.getPlayer().getName());
        guardNPC.spawn(location);

    }




}
