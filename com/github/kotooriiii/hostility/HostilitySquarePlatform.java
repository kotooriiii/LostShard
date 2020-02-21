package com.github.kotooriiii.hostility;

import org.bukkit.Location;

import java.util.UUID;

public class HostilitySquarePlatform extends HostilityPlatform{

    private Location loc1;
    private Location loc2;

    private int length;
    private int width;
    private int height;

    public HostilitySquarePlatform(UUID id, String name, Location loc1, Location loc2) {
        super(id, name);

        switch (isPlatform(loc1, loc2)) {
            case 1:
                //REMINDER that height wasn't 2 so setting to 2.
            case 0:
                setPlatform(loc1, loc2);
                break;
            case 2:
                //The length (x) was not 5.
                break;
            case 3:
                //The width (z) was not 5.
                break;
        }
    }

    /**
     * Checks if the area (really a volume) is valid for the platform. The platform will be used as a point in which clans compete. The volume the platform sets is the bounding box where players contest.
     *
     * @param loc1 The location1 of the block
     * @param loc2 The location2 of the block
     * @return '0' if successfully created the platform,
     * '2' if it is not an acceptable length (x difference).
     * '3' if it is not an acceptable width (z difference).
     * '1' if successful but have to remind the creator that the height was not 2, but will be set regardless.
     * <p></p>
     * Priority of error return codes follows from top to bottom (descending order).
     */
    public int isPlatform(Location loc1, Location loc2) {
        double loc1X = loc1.getX();
        double loc1Y = loc1.getY();
        double loc1Z = loc1.getZ();

        double loc2X = loc2.getX();
        double loc2Y = loc2.getY();
        double loc2Z = loc2.getZ();

        double xDifference = Math.abs(loc1X - loc2X);
        double yDifference = Math.abs(loc1Y - loc2Y);
        double zDifference = Math.abs(loc1Z - loc2Z);

        if (!(xDifference >= 5 && xDifference < 6)) {

            return 2;
        }

        if (!(zDifference >= 5 && zDifference < 6)) {
            return 3;
        }

        if (!(yDifference >= 2 && yDifference < 3)) {
            return 1;
        }
        return 0;
    }


    /**
     * Creates an area (really a volume) for the platform. The platform will be used as a point in which clans compete. The volume the platform sets is the bounding box where players contest.
     *
     * @param loc1 The location1 of the block
     * @param loc2 The location2 of the block
     */
    public void setPlatform(Location loc1, Location loc2) {
        double loc1X = loc1.getX();
        double loc1Y = loc1.getY();
        double loc1Z = loc1.getZ();

        double loc2X = loc2.getX();
        double loc2Y = loc2.getY();
        double loc2Z = loc2.getZ();

        double xDifference = Math.abs(loc1X - loc2X);
        double yDifference = Math.abs(loc1Y - loc2Y);
        double zDifference = Math.abs(loc1Z - loc2Z);

        if (xDifference >= 5 && xDifference < 6) {
            this.length = 5;
        }

        if (zDifference >= 5 && zDifference < 6) {
            this.width = 5;
        }


        this.height = 2;
        this.loc1 = loc1;
        this.loc2 = loc2;
    }



}
