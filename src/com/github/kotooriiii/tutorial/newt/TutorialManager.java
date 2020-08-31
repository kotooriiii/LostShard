package com.github.kotooriiii.tutorial.newt;

import com.github.kotooriiii.LostShardPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class manages all the players' progressions on the tutorial.
 */
public class TutorialManager {

    private HashMap<UUID, TProgression> playerProgressions;
    private ChapterManager chapterManager;

    public TutorialManager() {
        playerProgressions = new HashMap<>();
        chapterManager = new ChapterManager();
    }

    /**
     * The chapter manager works to register a chapter in the story and return the storyline.
     * @return the mangaging class of the progression in the tutorial.
     */
    public ChapterManager getChapterManager() {
        return chapterManager;
    }

    /**
     * Add a player to this tutorial.
     * @param uuid The player's UUID.
     */
    public void addTutorial(UUID uuid) {
        playerProgressions.put(uuid, new TProgression(uuid, getChapterManager().getStory()));
    }

    /**
     * Remove a player from this tutorial.
     * @param uuid The player's UUID.
     * @return true if removed, false otherwise.
     */
    public boolean removeTutorial(UUID uuid) {
        return playerProgressions.remove(uuid) != null;
    }

    /**
     * Returns a boolean to check whether a player is in a tutorial.
     * @param uuid The player's UUID.
     * @return true if is in tutorial, false otherwise (this would usually mean the tutorial is completed).
     */
    public boolean hasTutorial(UUID uuid) {
        return playerProgressions.containsKey(uuid);
    }

    /**
     * Wraps the Player's UUID to the TProgression.
     * @param uuid
     * @return
     */
    public TProgression wrap(UUID uuid)
    {
        return playerProgressions.get(uuid);
    }
}
