package com.github.kotooriiii.sorcery.marks;

import com.github.kotooriiii.files.FileManager;
import com.google.common.collect.Lists;
import com.mojang.datafixers.types.templates.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class MarkPlayer {


    private UUID playerUUID;
    private ArrayList<Mark> marks;

    private static HashMap<UUID, MarkPlayer> markPlayerHashMap = new HashMap<>();

    public static class Mark {

        private String name;
        private Location location;

        public Mark(String name, Location location) {
            this.name = name;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {

            return location;
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
        marks.add(new Mark(name, loc));
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
