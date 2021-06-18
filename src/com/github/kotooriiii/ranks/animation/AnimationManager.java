package com.github.kotooriiii.ranks.animation;

import com.github.kotooriiii.LostShardPlugin;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AnimationManager {
    private HashSet<UUID> animators;
    private HashMap<UUID, Trail> trailers;
    private HashMap<Location, IntegerTracker> blockMap;
    private boolean isTicking;



    public enum Trail
    {
        FIRE(Particle.FLAME), GREEN(Particle.TOTEM), HEART(Particle.HEART), BLUE(Particle.SOUL_FIRE_FLAME), END(Particle.DRAGON_BREATH), NONE(null);

        private Particle particle;

        private Trail(Particle particle)
        {
            this.particle = particle;
        }

        public Particle getParticle() {
            return particle;
        }

    }

    public class IntegerTracker
    {
        double value;

        public IntegerTracker(int countdownTimer)
        {
            this.value = countdownTimer;
        }

        public void minus()
        {
            this.value--;
        }

        public double getValue()
        {
            return value;
        }
    }

    public AnimationManager()
    {
        animators = new HashSet<>();
        blockMap = new HashMap<>();
        trailers = new HashMap<>();
        isTicking = false;
    }

    public void setTrail(UUID uuid, Trail trail)
    {
        trailers.put(uuid, trail);
    }

    public Trail getTrail(UUID uuid)
    {
        final Trail trail = trailers.get(uuid);
        if(trail == null)
            return Trail.NONE;
        return trail;
    }

    /**
     * Adds a block to the map. It will keep it in memory so we can replace it back to the actual block
     * @param location The location of the block
     * @param countdownTimer The count down timer in ticks.
     */
    public void blockAdd(Location location, int countdownTimer)
    {
        this.blockMap.put(location, new IntegerTracker(countdownTimer));
    }

    public void tick()
    {
        if(isTicking)
            return;

        isTicking = true;

        new BukkitRunnable() {
            @Override
            public void run()
            {
                final Iterator<Map.Entry<Location, IntegerTracker>> iterator = blockMap.entrySet().iterator();
                while(iterator.hasNext())
                {
                    Map.Entry<Location,IntegerTracker> entry = iterator.next();

                    Location blockLocation = entry.getKey();
                    IntegerTracker timeLeft = entry.getValue();

                    timeLeft.minus();

                    if(timeLeft.getValue() == 0)
                    {
                        blockRemove(blockLocation);
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public void blockRemove(Location location)
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
           player.sendBlockChange(location, location.getBlock().getBlockData());
        }
    }


    //Animators

    public HashSet<UUID> getAnimators() {
        return animators;
    }

    public void setAnimators(HashSet<UUID> animators) {
        this.animators = animators;
    }

    public boolean isAnimating(UUID uuid)
    {
        return this.animators.contains(uuid);
    }

    public void toggleAnimate(Player player) {
        UUID uniqueId = player.getUniqueId();
        if(isAnimating(uniqueId))
        {
            player.sendMessage(ChatColor.GREEN + "Your effects have been toggled off.");
            deanimate(uniqueId);
        }
        else
        {
            player.sendMessage(ChatColor.GREEN + "Your effects have been toggled on.");
            animate(uniqueId);
        }
    }

    public boolean animate(UUID uuid)
    {
        return this.animators.add(uuid);
    }

    public boolean deanimate(UUID uuid)
    {
        return this.animators.remove(uuid);
    }

    public String toString()
    {
        String res = "";

        for(UUID uuid : animators)
        {
            res += uuid.toString() + ", " + getTrail(uuid).name() + "\n";
        }

        return res;
    }

    public void fromString(String res)
    {
        Scanner scanner = new Scanner(res);
        while(scanner.hasNextLine())
        {
            final String[] result = scanner.nextLine().split(", ");

            if(result.length != 2)
                continue;

            final String uuidString = result[0];
            final String trailType = result[1];

            if(uuidString.isEmpty())
            {
                continue;
            }

            Trail trail = Trail.valueOf(trailType);

            final UUID uuid = UUID.fromString(uuidString);

            animators.add(uuid);
            if(trail != Trail.NONE)
                trailers.put(uuid, trail);
        }
    }
}
