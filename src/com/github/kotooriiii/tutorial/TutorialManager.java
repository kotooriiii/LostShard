package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.bungee.BungeeTutorialCompleteChannel;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * This class manages all the players' progressions on the tutorial. Progressions are known as {@link TutorialBook}s
 */
public class TutorialManager implements Observer {

    /**
     * Maps the Player UUID to the Progression
     */
    private HashMap<UUID, TutorialBook> playerProgressions;
    /**
     * Manages the story line
     */
    private ChapterManager chapterManager;
    private THologramManager hologramManager;
    private boolean isRestartWhenLoggedOff;

    /**
     * Base constructor. Instantiates the object.
     */
    public TutorialManager(boolean shouldRestartTutorialOnQuit) {
        playerProgressions = new HashMap<>();
        hologramManager = new THologramManager();
        chapterManager = new ChapterManager();
        isRestartWhenLoggedOff = shouldRestartTutorialOnQuit;
    }

    /**
     * The chapter manager works to register a chapter in the story and return the storyline.
     *
     * @return the mangaging class of the progression in the tutorial.
     */
    public ChapterManager getChapterManager() {
        return chapterManager;
    }

    /**
     * The hologram manager works to register holograms for each chapter.
     * @return the managing class for the holograms
     */
    public THologramManager getHologramManager() {
        return hologramManager;
    }

    /**
     * Restarts the tutorial when the Player is logged off.
     *
     * @return true if set to restart, false otherwise.
     */
    public boolean isRestartWhenLoggedOff() {
        return isRestartWhenLoggedOff;
    }

    /**
     * Sets the value for the tutorial to restart when the Player is logged off.
     *
     * @param restartWhenLoggedOff True for restart, false otherwise
     */
    public void setRestartWhenLoggedOff(boolean restartWhenLoggedOff) {
        isRestartWhenLoggedOff = restartWhenLoggedOff;
    }

    /**
     * Add a player to this tutorial.
     *
     * @param uuid The player's UUID.
     */
    public void addTutorial(UUID uuid) {
        TutorialBook book = new TutorialBook(uuid, getChapterManager().getStory());
        book.addObserver(this);
        playerProgressions.put(uuid, book);
        book.advance();
    }

    /**
     * Checks to see if a player's UUID is in the tutorial.
     *
     * @param uuid The player's UUID.
     * @return true if the player is currently in tutorial, false otherwise.
     */
    public boolean hasTutorial(UUID uuid) {
        return wrap(uuid) != null;
    }

    /**
     * Remove a player from this tutorial. If the player is currently in a chapter, calls the {@link AbstractChapter#onDestroy()} method before being removed from the tutorial.
     *
     * @param uuid The player's UUID.
     * @return true if removed, false if the player was not found in the tutorial mapping.
     */
    public boolean removeTutorial(UUID uuid, TutorialCompleteType type) {
        TutorialBook book = wrap(uuid);
        if (book == null)
            return false;
        if (book.getCurrentChapter() != null && book.getCurrentChapter().isActive()) {
            HandlerList.unregisterAll(book.getCurrentChapter());
            book.getCurrentChapter().onDestroy();
        }
        book.deleteObserver(this);

        for(Hologram hologram : getHologramManager().getList())
        {
            getHologramManager().hideHologram(hologram, uuid);

        }

        switch (type) {
            case RESET:
                break;
            case SKIP:
                BungeeTutorialCompleteChannel.getInstance().sendTutorialComplete(uuid, false);
                break;
            case COMPLETE:
                BungeeTutorialCompleteChannel.getInstance().sendTutorialComplete(uuid, true);
                break;
        }

        getHologramManager().clear(uuid);
        return playerProgressions.remove(uuid, book);
    }

    public World getTutorialWorld() {
        return Bukkit.getWorld("world");
    }

    /**
     * Wraps the Player's UUID to the TProgression.
     *
     * @param uuid
     * @return
     */
    public TutorialBook wrap(UUID uuid) {
        return playerProgressions.get(uuid);
    }

    /**
     * Is invoked when the Book is finished.
     *
     * @param o   The book
     * @param arg The object that changed, if necessary.
     */
    @Override
    public void update(Observable o, Object arg) {

        if (!(arg instanceof TutorialCompleteType))
            return;

        if (o instanceof TutorialBook) {
            TutorialBook book = (TutorialBook) o;
            if (book.isTutorialComplete())
                removeTutorial(book.getUUID(), (TutorialCompleteType) arg);
        }
    }
}
