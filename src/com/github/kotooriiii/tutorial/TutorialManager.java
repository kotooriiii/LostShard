package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bungee.BungeeTutorialCompleteChannel;
import com.github.kotooriiii.google.TutorialSheet;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
    public TutorialManager(boolean shouldRestartTutorialOnQuit, boolean givePotion) {
        playerProgressions = new HashMap<>();
        hologramManager = new THologramManager();
        chapterManager = new ChapterManager();
        isRestartWhenLoggedOff = shouldRestartTutorialOnQuit;
        if(givePotion)
            givePotion();
    }
    public void givePotion()
    {

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers())
                {
                    if(CitizensAPI.getNPCRegistry().isNPC(player))
                        continue;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20*60*5, 1, false, false, false));
                }
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 20*60*1);
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
     * @return TutorialBook of the UUID
     */
    public TutorialBook addTutorial(UUID uuid) {
        TutorialBook book = new TutorialBook(uuid, getChapterManager().getStory());
        book.addObserver(this);
        playerProgressions.put(uuid, book);
        book.advance();
        book.setInitDate(ZonedDateTime.now());
        return book;
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
        book.getBossBar().removeAll();
        Bukkit.removeBossBar(book.getBossBarKey());

        ZonedDateTime timeMoved = ZonedDateTime.now().minus(book.getInitDate().toInstant().toEpochMilli(), ChronoUnit.MILLIS);

        Player player = Bukkit.getPlayer(uuid);
        TutorialSheet.getInstance().append(uuid, player != null ? player.getName() : Bukkit.getOfflinePlayer(uuid).getName(), book.isComplete(), (book.getCurrentChapter() == null ? "N/A" : book.getCurrentChapter().toString()), (player != null ? player.getLocation() : new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 0, 0, 0)), book.hasPlot(), book.hasMark(), timeMoved.toEpochSecond());

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
