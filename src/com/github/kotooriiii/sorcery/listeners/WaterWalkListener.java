package com.github.kotooriiii.sorcery.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.type.circle6.FireWalkSpell;
import com.github.kotooriiii.sorcery.spells.type.circle6.WaterWalkSpell;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WaterWalkListener implements Listener {

    private final static Material[] mats = new Material[]
            {
              Material.WATER
                    //, Material.LAVA
            };
    private final static HashMap<UUID, ArrayList<Block>> blocks = new HashMap<>();


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

        final Block relativeDown = event.getTo().getBlock().getRelative(BlockFace.DOWN);

        boolean existsNext = false;

        for(int i = 0; i < mats.length; i++)
        {
            if(mats[i] == relativeDown.getType()) {
                existsNext = true;
                break;
            }
        }


        if(!WaterWalkSpell.getWaterWalkActiveSet().contains(player.getUniqueId()))
            return;

        if(existsNext) {
            player.sendBlockChange(relativeDown.getLocation(), Material.CYAN_STAINED_GLASS.createBlockData());

            ArrayList<Block> list = getBlocks().get(player.getUniqueId());

            if(list == null)
            {
                list = new ArrayList<Block>();
            }

            list.add(relativeDown);
        }

    }

    public static HashMap<UUID, ArrayList<Block>> getBlocks() {
        return blocks;
    }
}
