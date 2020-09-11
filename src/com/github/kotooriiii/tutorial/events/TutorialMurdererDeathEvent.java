package com.github.kotooriiii.tutorial.events;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TutorialMurdererDeathEvent extends Event {
    private NPC npc;

    public TutorialMurdererDeathEvent(NPC npc)
    {
        this.npc = npc;
    }

    private static final HandlerList handlers = new HandlerList();


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


    public NPC getNPC() {
        return npc;
    }

}