package com.github.kotooriiii.sorcery.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.netty.ProtocolInjector;
import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.ShardNMS;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.type.circle7.SilentWalkSpell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


import java.util.HashSet;
import java.util.UUID;

public class SilentWalkListener implements Listener {

    public static void initSilentWalkListener() {


        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(LostShardPlugin.plugin, PacketType.Play.Server.NAMED_SOUND_EFFECT) {

                @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() != PacketType.Play.Server.NAMED_SOUND_EFFECT)
                    return;

                PacketContainer packet = event.getPacket();

                Sound sound = packet.getSoundEffects().read(0);




                if (!(sound.name().endsWith("_FALL") || sound.name().endsWith("_STEP") || sound.name().endsWith("_HURT"))) {
                    return;
                }

                double x = (packet.getIntegers().read(0) / 8.0);
                double y = (packet.getIntegers().read(1) / 8.0);
                double z = (packet.getIntegers().read(2) / 8.0);
                Location loc = new Location(event.getPlayer().getWorld(), x, y, z);

                final HashSet<UUID> uuids = Spell.getManaDrainMap().get(SilentWalkSpell.getInstance());
                if (uuids != null) {
                    for (Entity entity : loc.getWorld().getNearbyEntities(loc, 3, 3, 3)) {
                        if (entity.getType() != EntityType.PLAYER)
                            continue;
                        if (!uuids.contains(entity.getUniqueId()))
                            continue;
                        if(!isRadius(loc, entity.getLocation(), 1))
                            continue;
                        event.setCancelled(true);
                        return;
                    }
                }



            }
        });

    }

    private static boolean isRadius(Location loc1 , Location loc2, int radius)
    {
        return Math.abs(loc1.getBlockX() - loc2.getBlockX()) <= radius && Math.abs(loc1.getBlockY() - loc2.getBlockY()) <= radius && Math.abs(loc1.getBlockZ() - loc2.getBlockZ()) <= radius ;
    }
}
