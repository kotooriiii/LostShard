package com.github.kotooriiii.status;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.Plot;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class HologramCreator {

    private Location location;
    private ArrayList<StatusPlayer> sortedList;

    public HologramCreator(Location location) {
        this.location = location;
        sortedList = new ArrayList<>(StatusPlayer.getPlayerStatus().values());
    }

    public void create() {

        for (Hologram hologram : HologramsAPI.getHolograms(LostShardPlugin.plugin)) {
            Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(hologram.getLocation());
            if (plot == null || !plot.getType().isStaff()) {
                hologram.delete();
                continue;
            }
        }

        Hologram hologram = HologramsAPI.createHologram(LostShardPlugin.plugin, location);
    }

    public void update() {
        for (Hologram hologram : HologramsAPI.getHolograms(LostShardPlugin.plugin)) {
            Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(hologram.getLocation());
            if (plot == null || !plot.getType().isStaff()) {
                hologram.delete();
                continue;
            }

            switch (plot.getType()) {
                case STAFF_SPAWN:
                    setMurdercountHologram(hologram);
                    updateMurdercountHologram(hologram);
                    break;
                default:
                    break;
            }

        }
    }

    public void updateMurdercountHologram(Hologram hologram) {

        Collections.sort(sortedList, new Comparator<StatusPlayer>() {
            @Override
            public int compare(StatusPlayer o1, StatusPlayer o2) {
                if (o1.getKills() < o2.getKills())
                    return 1;
                else if (o1.getKills() > o2.getKills())
                    return -1;
                return 0;
            }
        });
        for (int i = 0; i < 10; i++) {
            StatusPlayer statusPlayer = sortedList.get(i);
            hologram.insertTextLine(i + 1, i + ". " + statusPlayer.getStatus().getChatColor() + Bukkit.getOfflinePlayer(statusPlayer.getPlayerUUID()).getName() + " -> " + statusPlayer.getKills());
        }

    }

    public void setMurdercountHologram(Hologram hologram) {
        hologram.clearLines();
        hologram.appendTextLine(ChatColor.DARK_RED + "Murdercount Leaderboard");
    }


}
