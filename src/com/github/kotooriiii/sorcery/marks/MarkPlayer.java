package com.github.kotooriiii.sorcery.marks;

import com.github.kotooriiii.files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import sun.text.resources.cldr.ka.FormatData_ka;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class MarkPlayer implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID playerUUID;
    private ArrayList<Mark> marks;

    private static HashMap<UUID, MarkPlayer> markPlayerHashMap = new HashMap<>();

    public class Mark implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private int x;
        private int y;
        private int z;
        private float pitch;
        private float yaw;

        public Mark(String name, Location location) {
            this(name, location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getPitch(), location.getYaw());
        }

        public Mark(String name, int x, int y, int z) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.pitch = 0;
            this.yaw = 0;
        }

        public Mark(String name, int x, int y, int z, float pitch, float yaw) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.pitch = pitch;
            this.yaw = yaw;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public float getPitch() {
            return pitch;
        }

        public float getYaw() {
            return yaw;
        }

        public String getName() {
            return this.name;
        }

        public Location getLocation() {
            return new Location(Bukkit.getWorld("world"), x + 0.5, y, z + 0.5, yaw, pitch);
        }
    }

    public MarkPlayer(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.marks = new ArrayList<>();
        markPlayerHashMap.put(playerUUID, this);
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
        marks.add(new Mark(name, loc));
        save();
    }

    public boolean removeMark(String name) {
        for (Iterator<Mark> markIterator = marks.iterator(); markIterator.hasNext(); ) {
            Mark mark = markIterator.next();
            if (mark.getName().equalsIgnoreCase(name)) {
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

    public void save()
    {
        FileManager.write(this);
    }

    public void remove()
    {
        FileManager.removeFile(this);
        this.markPlayerHashMap.remove(this.playerUUID);

    }

    public Mark[] getMarks() {
        return this.marks.toArray(new Mark[this.marks.size()]);
    }

    //static
    public static HashMap<UUID,MarkPlayer> getMarkPlayers()
    {
        return markPlayerHashMap;
    }

    public static void add(MarkPlayer markPlayer)
    {
        if(!markPlayerHashMap.containsKey(markPlayer.playerUUID))
        markPlayerHashMap.put(markPlayer.playerUUID, markPlayer);
    }

    public static boolean hasMarks(UUID playerUUID)
    {
        return wrap(playerUUID) != null;
    }

    public static MarkPlayer wrap(UUID playerUUID)
    {
        return markPlayerHashMap.get(playerUUID);
    }
}
