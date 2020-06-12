package com.github.kotooriiii.npc.type.clone;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class CloneNPC {

    private Player player;
    private int x;
    private int z;

    public CloneNPC() {

    }

    public CloneNPC(Player player, int x, int z) {
        this.player = player;
        this.x = x;
        this.z = z;
    }

    public void spawn(Location location) {

        final double INVIS_SECONDS = 1;

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int) (20.0f*INVIS_SECONDS), 1, false, false, false));
        location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, location, 20, 0.3, 0.3, 0.3);

        new BukkitRunnable()
        {
            @Override
            public void run() {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                final Location anotherLocation = location.add(x,0,z);
                NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, StatusPlayer.wrap(player.getUniqueId()).getStatus().getChatColor() + player.getName());
                npc.spawn(anotherLocation);
                npc.setProtected(true);
                CloneTrait cloneTrait = new CloneTrait(player, x, z);
                npc.addTrait(cloneTrait);
            }
        }.runTaskLater(LostShardPlugin.plugin, (long) (20.0f*INVIS_SECONDS));

    }
//
//    public static Iterable<NPC> getAllBankerNPC()
//    {
//        Iterable<NPC> allNPCS = CitizensAPI.getNPCRegistry().sorted();
//        ArrayList<NPC> bankerNPCS = new ArrayList<>();
//        for(NPC npc : allNPCS)
//        {
//            if(!npc.hasTrait(CloneTrait.class))
//                continue;
//
//            //Has GuardTrait
//            bankerNPCS.add(npc);
//        }
//        return bankerNPCS;
//    }
//
//    public static NPC getNearestBanker(final Location location) {
//        NPC nearestBanker = null;
//        double nearestDistance = Double.MAX_VALUE;
//        for (NPC npc : getAllBankerNPC()) {
//
//            if(!npc.getStoredLocation().getWorld().equals(location.getWorld()))
//                continue;
//
//            double distance = npc.getStoredLocation().distance(location);
//            if (distance < nearestDistance) {
//                nearestDistance = distance;
//                nearestBanker = npc;
//            }
//        }
//
//        return nearestBanker;
//    }
}
