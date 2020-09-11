package com.github.kotooriiii.hostility.events;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.hostility.HostilityPlatform;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlatformStartEvent extends Event implements Cancellable {

    private HostilityPlatform  platform;

    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public PlatformStartEvent( HostilityPlatform platform) {

        this.platform = platform;
    }



    public HostilityPlatform getPlatform() {
        return platform;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
