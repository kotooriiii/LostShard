package com.github.kotooriiii.sorcery.spells;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.nio.channels.Channel;
import java.util.*;

public abstract class SpellChanneleable extends Spell {

    final private HashMap<Location, HashMap<UUID, BukkitTask>> membersMap = new HashMap<>();

    final private int DISTANCE;
    final private int REQUIRED;
    final private float SECONDS;

    public SpellChanneleable(SpellType type, int required, float graceSeconds, int distance, String desc, int circle, ChatColor color, ItemStack[] ingredients, double cooldown, int manaCost, boolean isCastable, boolean isWandable, boolean isScrollable) {
        super(type, desc, circle, color, ingredients, cooldown, manaCost, isCastable, isWandable, isScrollable);
        this.REQUIRED = required;
        this.SECONDS = graceSeconds;
        this.DISTANCE = distance;
    }


    public abstract void executeSuccessfulChannelSpell(Player player, UUID... value);

    public abstract void executeFailedChannelSpell(Player player, UUID... value);

    public final int getRequired() {
        return REQUIRED;
    }

    public float getGraceTimeSeconds() {
        return SECONDS;
    }

    public final boolean hasRequiredMembers(Location location) {

        for (Map.Entry<Location, HashMap<UUID, BukkitTask>> entry : membersMap.entrySet()) {
            Location entryLocation = entry.getKey();
            if (DISTANCE >= 0) {
                if (!entryLocation.getWorld().equals(location.getWorld()))
                    continue;
                if (entryLocation.distance(location) > DISTANCE)
                    continue;
            }
            HashMap<UUID, BukkitTask> value = entry.getValue();
            if (value == null)
                continue;
            if (value.size() < REQUIRED)
                continue;
            return true;
        }
        return false;
    }

    public final void removeMembers(Location location) {


        Iterator<Map.Entry<Location, HashMap<UUID, BukkitTask>>> iterator = membersMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, HashMap<UUID, BukkitTask>> next = iterator.next();
            Location entryLocation = next.getKey();
            if (DISTANCE >= 0) {
                if (!entryLocation.getWorld().equals(location.getWorld()))
                    continue;
                if (entryLocation.distance(location) > DISTANCE)
                    continue;
            }
            HashMap<UUID, BukkitTask> value = next.getValue();
            if (value == null)
                continue;
            for (BukkitTask task : value.values()) {
                task.cancel();
            }
            iterator.remove();
            return;
        }

    }

    public final boolean addMember(Player player, BukkitTask task) {

        for (Map.Entry<Location, HashMap<UUID, BukkitTask>> entry : membersMap.entrySet()) {
            Location entryLocation = entry.getKey();
            if (DISTANCE >= 0) {
                if (!entryLocation.getWorld().equals(player.getLocation()))
                    continue;
                if (entryLocation.distance(player.getLocation()) > DISTANCE)
                    continue;
            }

            HashMap<UUID, BukkitTask> value = entry.getValue();
            if (value == null)
                continue;
            value.put(player.getUniqueId(), task);
            return true;
        }
        HashMap<UUID, BukkitTask> value = new HashMap<UUID, BukkitTask>();
        value.put(player.getUniqueId(), task);
        membersMap.put(player.getLocation(), value);
        return true;
    }

    public final boolean hasMember(Player player) {

        for (Map.Entry<Location, HashMap<UUID, BukkitTask>> entry : membersMap.entrySet()) {
            Location entryLocation = entry.getKey();
            if (DISTANCE >= 0) {
                if (!entryLocation.getWorld().equals(player.getLocation()))
                    continue;
                if (entryLocation.distance(player.getLocation()) > DISTANCE)
                    continue;
            }

            HashMap<UUID, BukkitTask> value = entry.getValue();
            if (value == null)
                continue;
            if (!value.containsKey(player.getUniqueId()))
                continue;
            return true;
        }

        return false;
    }

    public final boolean hasMember(UUID uuid) {

        for (Map.Entry<Location, HashMap<UUID, BukkitTask>> entry : membersMap.entrySet()) {

            HashMap<UUID, BukkitTask> value = entry.getValue();
            if (value == null)
                continue;
            if (!value.containsKey(uuid))
                continue;
            return true;
        }

        return false;
    }

    public final boolean removeMember(Player player) {

        Iterator<Map.Entry<Location, HashMap<UUID, BukkitTask>>> iterator = membersMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, HashMap<UUID, BukkitTask>> next = iterator.next();
            Location entryLocation = next.getKey();
            if (DISTANCE >= 0) {
                if (!entryLocation.getWorld().equals(player.getLocation()))
                    continue;
                if (entryLocation.distance(player.getLocation()) > DISTANCE)
                    continue;
            }

            HashMap<UUID, BukkitTask> value = next.getValue();
            if (value == null)
                continue;
            if (!value.containsKey(player.getUniqueId()))
                continue;
            value.remove(player.getUniqueId()).cancel();

            if (value.isEmpty()) {
                iterator.remove();
            }

            return true;
        }
        return false;
    }

    public final boolean removeMember(UUID uuid) {
        Iterator<Map.Entry<Location, HashMap<UUID, BukkitTask>>> iterator = membersMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, HashMap<UUID, BukkitTask>> next = iterator.next();

            HashMap<UUID, BukkitTask> value = next.getValue();
            if (value == null)
                continue;
            if (!value.containsKey(uuid))
                continue;

            value.remove(uuid).cancel();

            if (value.isEmpty()) {
                iterator.remove();
            }

            return true;
        }
        return false;
    }

    public final UUID[] getMembers(Location location) {
        for (Map.Entry<Location, HashMap<UUID, BukkitTask>> entry : membersMap.entrySet()) {
            Location entryLocation = entry.getKey();
            if (DISTANCE >= 0) {
                if (!entryLocation.getWorld().equals(location))
                    continue;
                if (entryLocation.distance(location) > DISTANCE)
                    continue;
            }

            HashMap<UUID, BukkitTask> value = entry.getValue();
            if (value == null)
                continue;
            UUID[] uuids = new UUID[value.size()];

            int i = 0;
            for (UUID uuid : value.keySet()) {
                if (i < uuids.length)
                    break;
                uuids[i++] = uuid;
            }

            return uuids;
        }
        return null;
    }

    public final Player[] getOnlineMembers(Location location) {
        ArrayList<Player> players = new ArrayList<>();
        for (Map.Entry<Location, HashMap<UUID, BukkitTask>> entry : membersMap.entrySet()) {
            Location entryLocation = entry.getKey();
            if (DISTANCE >= 0) {
                if (!entryLocation.getWorld().equals(location))
                    continue;
                if (entryLocation.distance(location) > DISTANCE)
                    continue;
            }

            HashMap<UUID, BukkitTask> value = entry.getValue();
            if (value == null)
                continue;

            UUID[] uuids = new UUID[value.size()];

            int i = 0;
            for (UUID uuid : value.keySet()) {
                if (i < uuids.length)
                    break;
                uuids[i++] = uuid;
            }

            for (UUID uuid : uuids) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    players.add(player);
                }
            }

            return players.toArray(new Player[players.size()]);
        }
        return null;
    }

    public Location getCenter(Location location) {
        Player[] players = getOnlineMembers(location);

        if (players.length == 0)
            return null;
        Vector finalVector = players[0].getLocation().toVector().clone();

        for (int i = 1; i < players.length; i++) {
            Player iteratingPlayer = players[i];
            finalVector.midpoint(iteratingPlayer.getLocation().toVector().clone());
        }
        return finalVector.toLocation(location.getWorld());
    }

    public HashSet<UUID> getALLChanneling()
    {
        HashSet<UUID> uuids = new HashSet<>();
        for(HashMap<UUID, BukkitTask> map : membersMap.values())
        {
            uuids.addAll(map.keySet());
        }
        return uuids;
    }

    public final int size() {
        return membersMap.size();
    }

}
