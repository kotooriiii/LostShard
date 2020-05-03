package com.github.kotooriiii.farming;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Farmland;

import java.util.Random;

public class CustomCrop {
        /*

    Minecraft gamepedia mentions:
     - the average tick occurs every 68.27 seconds.
     - at each tick it runs a chance to grow to the next stage
        - the growth probability formula is : 1/(floor(25/points)+1)
            - points is defined as:
                - farmland
                    - dry: 2
                    - hydrated: 4
                - for each of the 8 blocks around the block in which the crop is planted,
                  dry farmland gives 0.25 points, and hydrated farmland gives 0.75.
                - (if the crop is on a diagonal) OR (if the crop is in west-east AND if the crop is in north-south) the points
                  total is reduced to half of its value.

       If we follow these rules we can mimic minecraft's ticking process.

    */

    private Block groundBlock;
    private Block cropBlock;

    private int ticksPassedSinceUpdate;

    private final int maxRandomTick = 1365; // floor(68.27seconds * 20ticks/1seconds)

    public CustomCrop(Block cropBlock) {
        this.groundBlock = cropBlock.getWorld().getBlockAt(cropBlock.getX(), cropBlock.getY() - 1, cropBlock.getZ());
        this.cropBlock = cropBlock;
        this.ticksPassedSinceUpdate = LostShardPlugin.getGameTicks();
    }

    /**
     * The blocks saved are still farmland and a crop.
     *
     * @return
     */
    public boolean isCrop() {
        return groundBlock.getType().equals(Material.FARMLAND) && cropBlock.getBlockData() instanceof Ageable;
    }

    public boolean isMaxed()
    {
        Ageable ageable = (Ageable) cropBlock.getState().getBlockData();

        int max = ageable.getMaximumAge();
        int cur = ageable.getAge();
        return max == cur;
    }


    /**
     * Grows the crop the missed amount of phases.
     *
     * @return true if the crop has fully grown, false otherwise
     */
    public boolean grow() {

        if (!isCrop())
            return false;

        //Calculates a random tick
        Random random = new Random();
        int avg = random.nextInt(maxRandomTick*2) + 1; //avoid division by zero later.

        double differenceTicks = LostShardPlugin.getGameTicks() - ticksPassedSinceUpdate;

        double timesMissedChecking = differenceTicks / avg;
        int timesToAddAge = (int) Math.floor(getProbability() * timesMissedChecking);
        Ageable ageable = (Ageable) cropBlock.getState().getBlockData();

        int max = ageable.getMaximumAge();
        int cur = ageable.getAge();

        int newTotal = cur + timesToAddAge;

        if (cur + timesToAddAge > max)
            ageable.setAge(max);
        else
            ageable.setAge(newTotal);

        this.ticksPassedSinceUpdate = LostShardPlugin.getGameTicks();
        return ageable.getAge() == ageable.getMaximumAge();
    }

    private double getProbability() {
        return 1 / (Math.floor(25 / getPoints()) + 1);
    }

    private double getPoints() {
        double hydratedPoints = getHydratedPoints();
        double neighborPoints = getNeighborHydratedPoints();

        double total = hydratedPoints + neighborPoints;
        return getHalvedTotalPoints(total);
    }

    private double getHydratedPoints() {
        Farmland farmland = (Farmland) groundBlock.getState().getBlockData();
        return farmland.getMoisture() == farmland.getMaximumMoisture() ? 4 : 2;
    }

    private double getNeighborHydratedPoints() {
        int cX = groundBlock.getX();
        int cY = groundBlock.getY();
        int cZ = groundBlock.getZ();

        double points = 0;

        //The loop is in the x-axis
        for (int x = cX - 1; x <= cX + 1; x++) {
            //The loop is in the z-axis
            for (int z = cZ - 1; z <= cZ + 3; z++) {
                if (cX == x && z == cZ)
                    continue;

                Block adjacentBlock = groundBlock.getWorld().getBlockAt(x, cY, z);
                if (!adjacentBlock.getType().equals(Material.FARMLAND))
                    continue;

                //The block isn't the center block AND the block is a farmland
                Farmland adjacentFarmland = (Farmland) groundBlock.getState().getBlockData();
                if (adjacentFarmland.getMoisture() == adjacentFarmland.getMaximumMoisture())
                    points += 0.75;
                else
                    points += 0.25;
            }
        }
        return points;
    }

    private double getHalvedTotalPoints(double total) {
        int cX = cropBlock.getX();
        int cY = cropBlock.getY();
        int cZ = cropBlock.getZ();
        Material cropType = cropBlock.getType();

        Block left = cropBlock.getWorld().getBlockAt(cX - 1, cY, cZ);
        Block right = cropBlock.getWorld().getBlockAt(cX + 1, cY, cZ);

        Block top = cropBlock.getWorld().getBlockAt(cX, cY, cZ + 1);
        Block bottom = cropBlock.getWorld().getBlockAt(cX, cY, cZ - 1);

        if (left.getType().equals(cropType) && right.getType().equals(cropType)
                && top.getType().equals(cropType) && bottom.getType().equals(cropType))
            return total / 2;

        Block topleft = cropBlock.getWorld().getBlockAt(cX - 1, cY, cZ + 1);
        Block bottomright = cropBlock.getWorld().getBlockAt(cX + 1, cY, cZ - 1);
        if (topleft.getType().equals(cropType) && bottomright.getType().equals(cropType))
            return total / 2;

        Block topright = cropBlock.getWorld().getBlockAt(cX + 1, cY, cZ + 1);
        Block bottomleft = cropBlock.getWorld().getBlockAt(cX - 1, cY, cZ - 1);
        if (topright.getType().equals(cropType) && bottomleft.getType().equals(cropType))
            return total / 2;

        return total;
    }

    public Block getCropBlock() {
        return cropBlock;
    }

    public Block getGroundBlock() {
        return groundBlock;
    }
}
