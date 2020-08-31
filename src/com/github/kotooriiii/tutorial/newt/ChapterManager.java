package com.github.kotooriiii.tutorial.newt;

import com.github.kotooriiii.LostShardPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ChapterManager {
    private ArrayList<Class<? extends AbstractChapter>> chapters;

    public ChapterManager() {
        chapters = new ArrayList<>();
    }

    /**
     * Registers the default story line of the tutorial.
     */
    public void registerDefault() {
        //register(TChapter);
    }

    /**
     * Register your own story line for the tutorial.
     **/
    public void register(AbstractChapter chapter) {
        LostShardPlugin.plugin.getServer().getPluginManager().registerEvents(chapter, LostShardPlugin.plugin);
        chapters.add(chapter.getClass());
    }

    /**
     * The constructed progression of the tutorial.
     * @return an array of chapters. strict on insertion order to preserve the order of the story.
     */
    public Queue<AbstractChapter> getStory()
    {
        Queue<AbstractChapter> chaptersArray = new ArrayBlockingQueue<AbstractChapter>(chapters.size());
        for(Class<? extends AbstractChapter> clazz : this.chapters)
        {
            try {
               chaptersArray.offer(clazz.getDeclaredConstructor().newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return chaptersArray;
    }
}
