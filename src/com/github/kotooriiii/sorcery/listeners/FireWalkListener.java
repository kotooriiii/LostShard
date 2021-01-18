package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.type.circle6.FireWalkSpell;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FireWalkListener implements Listener {

    private final static Material[] mats = new Material[]
            {
              Material.FIRE,
              Material.WATER,
              Material.LAVA
            };

    @EventHandler
    public void onMoveFromBlock(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if(!CitizensAPI.getNPCRegistry().isNPC(player))
            return;

        int fX = event.getFrom().getBlockX();
        int fY = event.getFrom().getBlockY();
        int fZ = event.getFrom().getBlockZ();

        int tX = event.getTo().getBlockX();
        int tY = event.getTo().getBlockY();
        int tZ = event.getTo().getBlockZ();

        if(fX == tX && fY == tY && fZ == tZ)
            return;
        for(int i = 0; i < mats.length; i++)
        {
            if(mats[i] == event.getFrom().getBlock().getRelative(BlockFace.DOWN).getType())
                return;
        }

        if(!FireWalkSpell.getFireWalkActiveSet().contains(player.getUniqueId()))
            return;

        Plot standingOnPlot = LostShardPlugin.getPlotManager().getStandingOnPlot(event.getFrom());
        if(standingOnPlot != null && standingOnPlot.getType().isStaff())
            return;



        event.getFrom().getBlock().setType(Material.FIRE);

    }
}
