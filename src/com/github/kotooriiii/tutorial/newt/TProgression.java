package com.github.kotooriiii.tutorial.newt;


import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.UUID;

/**
 * A class capturing the progress of a player.
 */
public class TProgression {

    private final UUID uuid;
    private final Queue<AbstractChapter> chapters;
    private AbstractChapter currentChapter;

    public TProgression(UUID uuid, Queue<AbstractChapter> chapters) {
        this.uuid = uuid;
        this.chapters = chapters;
        currentChapter = null;
    }

    public void advance()
    {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null)
            return;

        if(currentChapter != null)
            currentChapter.onDestroy(player);

        AbstractChapter chapter = chapters.poll();
        currentChapter = chapter;
        if(currentChapter == null) {
            completeTutorial(LostShardPlugin.getTutorialManager());
            return;
        }
        currentChapter.onBegin(player);
    }

    public void completeTutorial(TutorialManager manager)
    {
        manager.removeTutorial(uuid);
    }

    public boolean isComplete()
    {
        return getNextChapter() == null;
    }

    public UUID getUUID() {
        return uuid;
    }

    public AbstractChapter getNextChapter()
    {
        return chapters.peek();
    }

    public AbstractChapter getCurrentChapter()
    {
        return currentChapter;
    }
}
