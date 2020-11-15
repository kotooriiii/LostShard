package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Observable;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.STANDARD_COLOR;

/**
 * An abstract class that represents an unfinished chapter.
 * <p>
 * The class contains the method: {@link #setComplete()} which notifies the {@link TutorialBook} class that the chapter is complete.
 * <p>
 * When using the class make sure to invoke the method to count the chapter as complete.
 * <p>
 * The class also implements {@link Listener} so feel free to create events to further specify what is needed to complete the chapter. HOWEVER, URGENTLY NOTE THAT YOU SHOULD CHECK IF THE CHAPTER IS ACTIVE BEFORE EXECUTING ANY CODE {@link #isActive}
 * <p>
 * Note: Make sure the Player exists before using {@link org.bukkit.Bukkit#getPlayer(UUID)}'s methods. The player could have logged off.
 */
public abstract class AbstractChapter extends Observable implements Listener {

    /**
     * The Player's UUID
     */
    private UUID uuid;
    /**
     * The location for the chapter spawn.
     */
    private Location location;
    /**
     * Checks if the chapter is active.
     */
    private boolean isActive;

    /**
     * A delay tick to inform the
     */
    public final static  int DELAY_TICK = 120;
    public final static int TIP_DELAY = 20*10;

    public enum ChapterMessageType {
        HOLOGRAM_TO_TEXT, HELPER
    }

    /**
     * The default constructor for the chapter.
     */
    public AbstractChapter() {
        this.uuid = null;
        this.location = null;
        this.isActive = false;
    }

    /**
     * Initializes the chapter at runtime with the player object and marks it as available.
     *
     * @param uuid The Player's UUID
     * @return Returns immediately if the chapter has already been initialized.
     */
    public final void init(UUID uuid) {
        if (isActive && this.uuid != null)
            return;
        this.uuid = uuid;
        isActive = true;
    }

    /**
     * {@inheritDoc}
     * Executes this code when the chapter has started.
     * <p>
     * When the method is invoked these instance variables have already been declared:
     * {@link #getLocation()}
     * {@link #getUUID()}
     * <p>
     * It is reasonable and appropriate to {@link #setLocation(Location)} if you would like set your own custom {@link Location}. The default {@link Location} is set to the Player's current location right before the {@link #onBegin()} is called.
     * <p>
     * Note: Make sure the Player exists before using {@link org.bukkit.Bukkit#getPlayer(UUID)}'s methods. The player could have logged off.
     */
    public abstract void onBegin();

    /**
     * {@inheritDoc}
     * Executes this code when the chapter is destroyed.
     * <p>
     * This method is for you to use to clean up your chapter's mess if needed.
     * <p>
     * When the method is invoked these instance variables are still declared:
     * <p>
     * Note: Make sure the Player exists before using {@link org.bukkit.Bukkit#getPlayer(UUID)}'s methods. The player could have logged off.
     * </p>
     * {@link #getLocation()}
     * {@link #getUUID()}
     */
    public abstract void onDestroy();

    /**
     * If a player dies, retrieves the location of this chapter's spawn.
     *
     * @return location of the chapter
     */
    public final Location getLocation() {
        return this.location;
    }

    /**
     * The Player UUID for this Chapter
     *
     * @return Player's UUID
     */
    public final UUID getUUID() {
        return this.uuid;
    }

    /**
     * Sets the location of this chapter
     *
     * @param location chapter's location.
     */
    public final void setLocation(Location location) {
        this.location = location;
    }

    public double getDefaultHealth()
    {
        return 20.0f;
    }

    public int getDefaultFoodLevel()
    {
        return 20;
    }

    public boolean isUsingHeal()
    {
        return true;
    }

    /**
     * Returns to check if the player is currently playing this chapter. It is extremely crucial for the programmer to use this method when using the {@link Listener} interface.
     *
     * @return true if the Player is currently on this chapter, false otherwise.
     */
    public final boolean isActive() {
        return this.isActive;
    }

    /**
     * Completes the chapter.
     *
     * @return This method will always advance to the next one. However, it will immediately return if the chapter has not been initialized or if it has already been completed.
     */
    public final void setComplete()  {
        if (this.uuid == null)
            return;
        //throw new RuntimeException("The chapter has not been initialized.");

        if (!isActive)
            return;
        //throw new RuntimeException("The chapter has already been completed.");

        if(LostShardPlugin.getTutorialManager().wrap(uuid) == null)
            return; //throw new RuntimeException("The player's uuid is no longer mapped");

        setChanged();
        notifyObservers();
        isActive = false;

    }

    /**
     * @deprecated
     */
    public void sendMessage(Player player, String message, ChapterMessageType type) {
        if(type == ChapterMessageType.HOLOGRAM_TO_TEXT)
            return;
        if (player.isOnline()) {
            player.sendMessage(STANDARD_COLOR + message);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 10.0f, 0.0f);
        }
    }

}