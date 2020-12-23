package com.github.kotooriiii.sorcery.marks;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.plots.struct.SpawnPlot;
import com.github.kotooriiii.status.StatusPlayer;
import com.google.common.collect.Lists;
import com.mojang.datafixers.types.templates.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.*;

public class MarkPlayer {


    private UUID playerUUID;
    private ArrayList<Mark> marks;


    private static HashMap<UUID, MarkPlayer> markPlayerHashMap = new HashMap<>();

    public static class Mark {

        private String name;
        private Location location;
        private MarkType type;

        public enum MarkType {
            RANDOM,SPAWN,PLAYER
        }

        public Mark(String name, Location location, MarkType type) {
            this.name = name;
            this.type = type;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {

            return location;
        }

        public MarkType getType() {
            return type;
        }
    }

    public MarkPlayer(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.marks = new ArrayList<>();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean hasMark(String name) {
        for (Iterator<Mark> markIterator = marks.iterator(); markIterator.hasNext(); ) {
            Mark mark = markIterator.next();
            if (mark.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void addMark(String name, Location loc) {
        marks.add(new Mark(name, loc, Mark.MarkType.PLAYER));
        save();
    }

    public boolean removeMark(String name) {

        for (Iterator<Mark> markIterator = marks.iterator(); markIterator.hasNext(); ) {
            Mark iteratingMark = markIterator.next();
            if (iteratingMark.getName().equalsIgnoreCase(name)) {
                markIterator.remove();
                save();
                return true;
            }
        }

        return false;
    }

    public Mark getMark(String name) {
        for (Iterator<Mark> markIterator = marks.iterator(); markIterator.hasNext(); ) {
            Mark mark = markIterator.next();
            if (mark.getName().equalsIgnoreCase(name)) {
                return mark;
            }
        }
        return null;
    }

    public boolean hasAnyMark(String name) {
        for (Mark mark : getMarks())
            if (mark.getName().equalsIgnoreCase(name))
                return true;
        for (Mark mark : getPremadeMarks())
            if (mark.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    public Mark getAnyMark(String name) {
        for (Mark mark : getMarks())
            if (mark.getName().equalsIgnoreCase(name))
                return mark;
        for (Mark mark : getPremadeMarks())
            if (mark.getName().equalsIgnoreCase(name))
                return mark;
        return null;
    }

    public Mark getAnyMark(Mark.MarkType type) {
        for (Mark mark : getMarks())
            if (mark.getType() == type)
                return mark;
        for (Mark mark : getPremadeMarks())
            if (mark.getType() == type)
                return mark;
        return null;
    }

    public MarkPlayer.Mark[] getPremadeMarks() {
        return new MarkPlayer.Mark[]{getRandomMark(), getSpawnMark()};
    }

    public boolean isPremadeMark(String name) {
        switch (name.toLowerCase()) {
            case "spawn":
            case "random":
                return true;
            default:
                return false;
        }
    }

    public MarkPlayer.Mark getPremadeMark(String name) {
        switch (name.toLowerCase()) {
            case "spawn":
                return getSpawnMark();
            case "random":
                return getRandomMark();
            default:
                return null;
        }
    }

    public MarkPlayer.Mark getSpawnMark() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        if (!offlinePlayer.isOnline())
            return null;
        return new MarkPlayer.Mark("Spawn", ((SpawnPlot) LostShardPlugin.getPlotManager().getPlot(StatusPlayer.wrap(playerUUID).getStatus().getOrganization())).getSpawn(), Mark.MarkType.SPAWN);
    }

    public MarkPlayer.Mark getRandomMark() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        if (!offlinePlayer.isOnline())
            return null;
        Location location = randomRecall(offlinePlayer.getPlayer());
        return new MarkPlayer.Mark("Random", location, Mark.MarkType.RANDOM);
    }

    private Location randomRecall(Player player) {

        World world = getSpawnMark().getLocation().getWorld();
        String worldName = world.getName();

        LostShardPlugin.LSBorder border = LostShardPlugin.getBorder(worldName);
        int cX = border.getX();
        int cZ = border.getZ();
        int rX = border.getRadiusX();
        int rZ = border.getRadiusZ();

        Random random = new Random();

        /*

        Why this crazy magic value?
        rX is the radius.
        Multiply by two to include the whole diameter.
        After random value is set remove the radius to include negative number

        Example: Radius is 3
        random values possible are: 0 1 2. DONT INCLUDE 3. Radius by Worldborder is EXCLUSIVE

        mult by two: 0 1 2 _ 3 4 5
        subtract 1: 0 1 _ 2 _ 3 4
        PICK RANDOM
        add one: 1 2 _ 3 _ 4 5
        subtract radius (3): -2 -1 0 1 2

         */

        int ranX = random.nextInt(((rX) * 2) - 1) + 1 - (rX);
        int ranZ = random.nextInt(((rZ) * 2) - 1) + 1 - (rZ);

        int blockX = ranX + cX;
        int blockZ = ranZ + cZ;


        Location randomLoc = world.getHighestBlockAt(blockX, blockZ).getLocation().add(0.5, 1, 0.5);
        return randomLoc;

    }

    public void add() {
        markPlayerHashMap.put(playerUUID, this);

    }

    public void save() {
        FileManager.write(this);
    }

    public void remove() {
        FileManager.removeFile(this);
        this.markPlayerHashMap.remove(this.playerUUID);
    }

    public void setMarks(Mark[] marks) {
        ArrayList<Mark> newMarks = new ArrayList<>();
        for (int i = 0; i < marks.length; i++) {
            newMarks.add(marks[i]);
        }
        this.marks = newMarks;
        save();
    }

    public Mark[] getMarks() {
        return this.marks.toArray(new Mark[this.marks.size()]);
    }

    //static
    public static HashMap<UUID, MarkPlayer> getMarkPlayers() {
        return markPlayerHashMap;
    }

    public static void add(MarkPlayer markPlayer) {
        if (!markPlayerHashMap.containsKey(markPlayer.playerUUID))
            markPlayerHashMap.put(markPlayer.playerUUID, markPlayer);
    }

    public static boolean hasMarks(UUID playerUUID) {
        return wrap(playerUUID) != null;
    }

    public static MarkPlayer wrap(UUID playerUUID) {
        return markPlayerHashMap.get(playerUUID);
    }
}
