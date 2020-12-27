package com.github.kotooriiii.tutorial;


import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.commands.HealCommand;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.time.ZonedDateTime;
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
     * The Boss Bar
     */
    private final BossBar bar;
    /**
     * Namespaced boss bar key
     */
    private final NamespacedKey key;
    /**
     * The story line. A collection of chapters of the book.
     */
    private final Queue<AbstractChapter> chapters;
    private final int maxChapterSize;
    /**
     * The current chapter of the book.
     */
    private AbstractChapter currentChapter;

    private boolean hasMark, hasPlot;
    private ZonedDateTime initDate;
    private boolean isComplete;


    /**
     * Constructs the object with the player's UUID and the specified storyline.
     *
     * @param uuid     The Player UUID
     * @param chapters The story line
     */
    public TutorialBook(UUID uuid, Queue<AbstractChapter> chapters) {
        this.uuid = uuid;
        this.chapters = chapters;
        this.maxChapterSize = this.chapters.size();
        this.key = new NamespacedKey(LostShardPlugin.plugin, uuid.toString() + "_BossBar");
        this.bar = Bukkit.createBossBar(key, "Tutorial Progress", BarColor.PURPLE, BarStyle.SEGMENTED_12);
        currentChapter = null;
    }

    /**
     * Advances the storyline by one chapter.
     */
    public void advance() {
        Player player = Bukkit.getPlayer(uuid);

        //Destroys the old chapter and removes the observer.
        if (currentChapter != null) {
            HandlerList.unregisterAll(currentChapter);
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
        if (player != null) {
            currentChapter.setLocation(player.getLocation());

            if (this.getCurrentChapter().isUsingHeal())
                HealCommand.heal(player, false);
            else {
                player.setHealth(this.getCurrentChapter().getDefaultHealth());
                player.setFoodLevel(this.getCurrentChapter().getDefaultFoodLevel());
            }
        }

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
     * Boss bar of the tutorial progress
     *
     * @return boss bar
     */
    public BossBar getBossBar() {
        return bar;
    }

    /**
     * NamedSpaceKey of boss bar
     * @return key
     */
    public NamespacedKey getBossBarKey() {
        return key;
    }

    public boolean hasMark() {
        return hasMark;
    }

    public void setMark(boolean hasMark) {
        this.hasMark = hasMark;
    }

    public boolean hasPlot() {
        return hasPlot;
    }

    public void setPlot(boolean hasPlot) {
        this.hasPlot = hasPlot;
    }

    public ZonedDateTime getInitDate() {
        return initDate;
    }

    public void setInitDate(ZonedDateTime initDate) {
        this.initDate = initDate;
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
            getBossBar().setProgress((1.0d - ((double) chapters.size()/maxChapterSize)));
        }
        return;
    }

    public void setComplete(boolean b) {
        this.isComplete = b;
    }

    public boolean isComplete() {
        return isComplete;
    }
}
