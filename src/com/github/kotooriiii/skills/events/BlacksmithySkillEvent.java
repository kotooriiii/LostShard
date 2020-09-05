package com.github.kotooriiii.skills.events;

import com.github.kotooriiii.skills.commands.blacksmithy.BlacksmithyType;
import com.github.kotooriiii.sorcery.spells.Spell;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BlacksmithySkillEvent extends Event implements Cancellable {

    private Player player;
    private BlacksmithyType type;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public BlacksmithySkillEvent(Player player, BlacksmithyType type) {
        this.player = player;
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    public BlacksmithyType getType() {
        return type;
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
