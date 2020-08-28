package com.github.kotooriiii.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TutorialProgression {
    private UUID uuid;

    private Location location;

    private final TutorialProgressionType[] orderedTypeList;
    private int index;

    private boolean isSucessful;

    public TutorialProgression(UUID uuid) {
        this.uuid = uuid;
        orderedTypeList = TutorialProgressionType.values();
        index = -1;
        isSucessful=false;
    }


    /**
     * Proceeds to the next progression for the tutorial steps
     *
     * @param location The location to update the marker
     * @return true if next progression was sent,
     */
    public boolean nextProgression(Location location) {
        if (this.index + 1 == orderedTypeList.length) {
            isSucessful=true;
            return false;
        }
        this.location = location;
        this.index++;
        if (!execute()) {
            return false;
        }
        return true;
    }

    private boolean execute() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return false;
        getProgressionType().getFunction().apply(player);
        return true;
    }

    /**
     * Returns if player has successfully finished the tutorial.
     * @return true to send to server
     */
    public boolean isSuccessful() {
        return isSucessful;
    }

    public Location getLocation() {
        return location;
    }

    public TutorialProgressionType getProgressionType() {
        if (index == -1)
            return null;
        return orderedTypeList[index];
    }

    public UUID getUUID() {
        return uuid;
    }
}
