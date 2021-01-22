package com.github.kotooriiii.sorcery.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.xezard.glow.data.glow.manager.GlowsManager;

public class GlowListener implements Listener {
    @EventHandler
    public void onGlowJoin(PlayerJoinEvent event)
    {
        GlowsManager.getInstance().getGlows().forEach(e ->
                {
                    boolean exists=false;
                    for(Entity entity : e.getHolders())
                    {
                        if(entity == event.getPlayer())
                        {
                            exists=true;
                            break;
                        }
                    }

                    if(!exists)
                        return;

                    e.removeHolders(event.getPlayer());
                    e.addHolders(event.getPlayer());
                }
        );
    }
}
