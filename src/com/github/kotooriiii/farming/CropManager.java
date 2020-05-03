package com.github.kotooriiii.farming;

import com.sun.scenario.effect.Crop;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class CropManager {

    private HashMap<Location, CustomCrop> crops;

    public CropManager()
    {
        crops = new HashMap<>();
    }

    public void addCrop(CustomCrop crop) {
        crops.put(crop.getCropBlock().getLocation(), crop);
    }

    public void removeCrop(CustomCrop crop) {
        crops.remove(crop.getCropBlock().getLocation(), crop);
    }

    public boolean isCrop(CustomCrop crop) {
        return crops.containsKey(crop.getCropBlock().getLocation());
    }

    public void addCrop(Block cropBlock) {

        crops.put(cropBlock.getLocation(), new CustomCrop(cropBlock));
    }

    public void removeCrop(Block cropBlock) {
        crops.remove(cropBlock.getLocation());
    }

    public boolean isCrop(Block cropBlock) {
        return crops.containsKey(cropBlock.getLocation());
    }
}