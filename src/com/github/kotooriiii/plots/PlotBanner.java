package com.github.kotooriiii.plots;

import com.github.kotooriiii.google.TutorialSheet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;

public class PlotBanner {

    private final String ID = "ID:PLOT_BANNER";

    private static PlotBanner instance;


    private PlotBanner() {
    }

    public ItemStack getItem() {
        ItemStack itemStack = new ItemStack(Material.RED_BANNER);

        BannerMeta meta = (BannerMeta) itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_PURPLE + "Plot Banner");

        ArrayList<String> list = new ArrayList<>();
        list.add(ChatColor.DARK_PURPLE + "Place this banner to create a plot.");
        list.add(ID);

        meta.setLore(list);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static PlotBanner getInstance() {
        if (instance == null) {
            synchronized (PlotBanner.class) {
                if (instance == null)
                    instance = new PlotBanner();
            }
        }
        return instance;

    }

    public String getID() {
        return ID;
    }
}
