package com.github.kotooriiii.hostility.events;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.hostility.HostilityPlatform;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlatformContestEvent extends Event  {

    private HostilityPlatform  platform;
    private Clan[] contestingClans;

    private static final HandlerList handlers = new HandlerList();

    public PlatformContestEvent(HostilityPlatform platform, Clan[] contestingClans) {

        this.platform = platform;
        this.contestingClans = contestingClans;
    }



    public HostilityPlatform getPlatform() {
        return platform;
    }

    public Clan[] getContestingClans() {
        return contestingClans;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
