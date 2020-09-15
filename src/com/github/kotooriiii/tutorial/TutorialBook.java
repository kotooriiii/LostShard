package com.github.kotooriiii.tutorial;


import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.UUID;

/**
 * A class capturing the progress of a player.
 */
public class TutorialBook extends Observable implements Observer {

    /**
     * The UUID of a player
     */
    private final UUID uuid;
    /**
     * The story line. A collection of chapters of the book.
     */
    private final Queue<AbstractChapter> chapters;
    /**
     * The current chapter of the book.
     */
    private AbstractChapter currentChapter;

    /**
     * Constructs the object with the player's UUID and the specified storyline.
     *
     * @param uuid     The Player UUID
     * @param chapters The story line
     */
    public TutorialBook(UUID uuid, Queue<AbstractChapter> chapters) {
        this.uuid = uuid;
        this.chapters = chapters;
        currentChapter = null;
    }

    /**
     * Advances the storyline by one chapter.
     */
    public void advance() {
        Player player = Bukkit.getPlayer(uuid);

        //Destroys the old chapter and removes the observer.
        if (currentChapter != null) {
            currentChapter.onDestroy();
            currentChapter.deleteObserver(this);
        }

        //Grab the next chapter
        AbstractChapter chapter = chapters.poll();
        //Update chapter
        currentChapter = chapter;
        //If the chapter is null, it means there's no more chapters left. End the tutorial.
        if (currentChapter == null) {
            completeTutorial(TutorialCompleteType.COMPLETE);
            return;
        }

        //There are more chapters so add an observer so we can update this class with the object's changed information.
        currentChapter.addObserver(this);

        //If the player is not null when this method is invoked set the default location to the player's location
        if (player != null)
            currentChapter.setLocation(player.getLocation());

        //Initialize the chapter.
        currentChapter.init(uuid);
        LostShardPlugin.plugin.getServer().getPluginManager().registerEvents(currentChapter, LostShardPlugin.plugin);
        currentChapter.onBegin();
    }

    /**
     * Alerts the manager class that the tutorial has been completed via the {@link TutorialManager#update(Observable, Object)} method.
     */
    public void completeTutorial(TutorialCompleteType type) {
        setChanged();
        notifyObservers(type);
    }

    /**
     * Checks to see if the tutorial is complete.
     *
     * @return true if complete, false otherwise
     */
    public boolean isTutorialComplete() {
        return getCurrentChapter() == null && getNextChapter() == null;
    }

    /**
     * The UUID this Book belongs to.
     *
     * @return
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Peeks the next chapter.
     *
     * @return the next chapter
     */
    public AbstractChapter getNextChapter() {
        return chapters.peek();
    }

    /**
     * Returns the current chapter of the Book
     *
     * @return the current chapter
     */
    public AbstractChapter getCurrentChapter() {
        return currentChapter;
    }

    /**
     * Invoked when the chapter is completed.
     *
     * @param o   The chapter
     * @param arg The changed attribute
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof AbstractChapter) {
            AbstractChapter chapter = (AbstractChapter) o;
            if (chapter.isActive() && getCurrentChapter() == chapter)
                advance();
        }
        return;
    }
}
