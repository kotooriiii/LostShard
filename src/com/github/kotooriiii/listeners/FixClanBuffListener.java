package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FixClanBuffListener implements Listener {
    @EventHandler (priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e)
    {
        Stat stat = Stat.wrap(e.getPlayer().getUniqueId());
        final Clan clan = LostShardPlugin.getClanManager().getClan(stat.getPlayerUUID());

        if(clan==null)
        {
            stat.setMaxStamina(Stat.BASE_MAX_STAMINA);
            stat.setMaxMana(Stat.BASE_MAX_MANA);
            return;
        }

        if(!clan.hasStaminaBuff())
            stat.setMaxStamina(Stat.BASE_MAX_STAMINA);
        if(!clan.hasManaBuff())
            stat.setMaxMana(Stat.BASE_MAX_MANA);
    }
}
