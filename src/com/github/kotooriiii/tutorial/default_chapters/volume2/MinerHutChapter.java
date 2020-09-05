package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.skills.commands.blacksmithy.BlacksmithyType;
import com.github.kotooriiii.skills.events.BlacksmithySkillEvent;
import com.github.kotooriiii.tutorial.newt.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import javax.swing.*;

public class MinerHutChapter extends AbstractChapter {
    @Override
    public void onBegin() {
        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        sendMessage(player, "Your pickaxe is considerably low.\nYou can use the Blacksmithy skill to repair it.\nHold it in your hand and type: /repair");
    }

    @Override
    public void onDestroy() {

    }

    @EventHandler
    public void onRepair(BlacksmithySkillEvent event)
    {

        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if(!event.getType().equals(BlacksmithyType.REPAIR))
        {
            sendMessage(event.getPlayer(), "This operation is not supported in the tutorial phase.");
            event.setCancelled(true);
            return;
        }


        sendMessage(event.getPlayer(), "You repaired your pickaxe!\nNow keep moving forward!");

        setComplete();
    }
}
