package com.github.kotooriiii.hostility.events;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.hostility.HostilityMatch;
import com.github.kotooriiii.hostility.HostilityPlatform;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlatformLoseEvent extends Event implements Cancellable {

    private HostilityMatch match;
    private Clan capturingClan;
    private Player capturingPlayer;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public PlatformLoseEvent(HostilityMatch match, Player capturingPlayer, Clan capturingClan) {

        this.match = match;
        this.capturingPlayer = capturingPlayer;
        this.capturingClan = capturingClan;
    }

    public HostilityMatch getMatch() {
        return match;
    }

    public Player getCapturingPlayer() {
        return capturingPlayer;
    }

    public Clan getCapturingClan() {
        return capturingClan;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
