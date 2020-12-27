package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.tutorial.default_chapters.volume1.*;
import com.github.kotooriiii.tutorial.default_chapters.volume2.*;
import com.github.kotooriiii.tutorial.default_chapters.volume3.*;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * The class manages the storyline of a tutorial. It is instantiated by the {@link TutorialManager} class and can be retrieved using the {@link TutorialManager#getChapterManager()} method.
 */
public class ChapterManager {

    /**
     * The list of chapters kept in insertion order purposely.
     */
    private ArrayList<Class<? extends AbstractChapter>> chapters;

    /**
     * Base constructor
     */
    public ChapterManager() {
        chapters = new ArrayList<>();
    }

    /**
     * Registers the default story line of the tutorial.
     */
    public void registerDefault() {

        //Volume initialization
        //Volume 1
        register(IntroChapter.class);
        register(TitleChapter.class);
        register(IntroWandChapter.class);
        register(GrabStickFromChestChapter.class);
        register(WandInstructionChapter.class);
        register(RavineChapter.class);

        //Volume 2
        register(SkillTitleChapter.class);
        register(TrackSpiderChapter.class);
        register(IntroMiningChapter.class);
        register(DeadEndChapter.class);
        register(MinerHutChapter.class);
        register(FreedomChapter.class);
        register(CraftingChapter.class);

        //volume3
        register(EnterOrderChapter.class);
        register(MurdererChapter.class);
        register(StatusInstructionChapter.class);
        register(BankerIntroChapter.class);
        register(BankChestChapter.class);
        register(PlotIntroChapter.class);

        register(PlotGrabChestChapter.class);
      // register(PlotDepositChapter.class);
       // register(PlotExpandChapter.class);

        register(IntroMarkChapter.class);
        register(PathToEventChapter.class);
        register(FallChapter.class);
        register(ZombieChapter.class);
        register(GorpsEnterChapter.class);
        register(RecallChapter.class);
        register(FinaleChapter.class);
    }


    /**
     * Registers a chapter for the tutorial. Useful if you want to make your own storyline.
     **/
    public void register(Class<? extends AbstractChapter> clazz) {
        chapters.add(clazz);
    }

    /**
     * The storyline stored in a Queue. The method constructs new objects for the classes and registers them to the {@link org.bukkit.event.Listener} interface.
     *
     * @return an array of chapters. strict on insertion order to preserve the order of the story.
     */
    public Queue<AbstractChapter> getStory() {
        Queue<AbstractChapter> chaptersArray = new ArrayBlockingQueue<>(chapters.size());
        for (Class<? extends AbstractChapter> clazz : this.chapters) {
            try {
                AbstractChapter chapter = clazz.getDeclaredConstructor().newInstance();
                chaptersArray.offer(chapter);
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
