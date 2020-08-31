package com.github.kotooriiii.tutorial.newt;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public abstract class AbstractChapter implements Listener {

    /**
     * Executes this code when the chapter has started.
     */
    public abstract void onBegin(Player player);

    /**
     * Executes this code when the chapter is destroyed
     */
    public abstract void onDestroy(Player player);

    /**
     * If a player dies, retrieves the location of this chapter's spawn.
     *
     * @return location of the chapter
     */
    public abstract Location getLocation();

    /**
     * Sets the location of this chapter
     *
     * @param location chapter's location.
     */
    public abstract void setLocation(Location location);

    /**
     * Attempts to complete the chapter to advance to the next.
     * @param progression the player's tutorial progression
     * @return true if advanced, false if the progression's current chapter is not 'this' one
     */
    public final boolean completeChapter(TProgression progression) {
        if (progression.getCurrentChapter() != this)
            return false;
        progression.advance();
        return true;
    }

}


