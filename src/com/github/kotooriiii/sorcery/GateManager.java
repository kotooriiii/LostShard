package com.github.kotooriiii.sorcery;

import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.status.Staff;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;

public class GateManager {


    private static HashMap<UUID, LinkedList<Gate>> playerGateHashMap;
    private static HashMap<GateBlock, Gate> locationGateHashMap;
    private int maxGate = 5;

    public GateManager() {
        playerGateHashMap = new HashMap<>();
        locationGateHashMap = new HashMap<>();
    }

    public void addTemporaryGate(Gate gate) {

        locationGateHashMap.put(new GateBlock(gate.getFrom()), gate);
        locationGateHashMap.put(new GateBlock(gate.getTo()), gate);
        gate.build();
    }

    public void addGate(Gate gate, boolean saveToFile) {
        UUID uuid = gate.getSource();

        if (playerGateHashMap.get(uuid) == null)
            playerGateHashMap.put(uuid, new LinkedList<Gate>());

        LinkedList<Gate> gateLinkedList = playerGateHashMap.get(uuid);

        ArrayList<Gate> removal = new ArrayList<>();
        for (Gate igate : gateLinkedList) {
            if (!igate.isBuilt()) {
                removal.add(igate);
            }
        }

        for (Gate igate : removal) {
            removeGate(igate);
        }

        if (gateLinkedList.size() == maxGate && !Staff.isStaff(gate.getSource())) {
            Gate removedGate = gateLinkedList.peek(); //does not remove, only peeks
            removeGate(removedGate);
        }

        gateLinkedList.offer(gate);
        locationGateHashMap.put(new GateBlock(gate.getFrom()), gate);
        locationGateHashMap.put(new GateBlock(gate.getTo()), gate);
        gate.build();

        if (saveToFile)
            saveGate(gate);
    }

    public void deleteExistingGateIfAny(Gate tryingGate) {


        if (isYourOwnExistingGate(tryingGate)) {
            Gate alreadyGate = getGate(tryingGate.getTo());
            if (alreadyGate != null)
                if (alreadyGate.getSource().equals(tryingGate.getSource()))
                    removeGate(alreadyGate);
            Gate alreadyGate2 = getGate(tryingGate.getFrom());
            if (alreadyGate2 != null)
                if (alreadyGate2.getSource().equals(tryingGate.getSource()))
                    removeGate(alreadyGate2);
        }

    }

    public boolean isYourOwnExistingGate(Gate tryingGate) {

        if (isGate(tryingGate.getFrom())) {
            Gate alreadyGate = getGate(tryingGate.getFrom());
            if (alreadyGate.getSource().equals(tryingGate.getSource()))
                return true;
        }

        if (isGate(tryingGate.getTo())) {
            Gate alreadyGate = getGate(tryingGate.getTo());
            if (alreadyGate.getSource().equals(tryingGate.getSource()))
                return true;
        }

        return false;
    }

    public void saveGate(Gate gate) {
        FileManager.write(gate);
    }

    public void removeTemporaryGate(Gate removedGate) {

        //get map- > gets list ->
        removedGate.destroy();

        locationGateHashMap.remove(new GateBlock(removedGate.getFrom()), removedGate);
        locationGateHashMap.remove(new GateBlock(removedGate.getTo()), removedGate);
    }

    public void removeGate(Gate removedGate) {

        //get map- > gets list ->
        removedGate.destroy();

        LinkedList<Gate> gates = playerGateHashMap.get(removedGate.getSource());
        if (gates != null)
            gates.remove(removedGate);
        locationGateHashMap.remove(new GateBlock(removedGate.getFrom()), removedGate);
        locationGateHashMap.remove(new GateBlock(removedGate.getTo()), removedGate);
        FileManager.write(removedGate);
    }

    public boolean isGate(Location location) {

        Gate gate = locationGateHashMap.get(new GateBlock(location));

        Location loc2 = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
        Gate gate2 = locationGateHashMap.get(new GateBlock(loc2));

        if (gate == null && gate2 == null)
            return false;
        return true;
    }

    public boolean hasGateNearby(Location location) {
        final int blocksRange = Gate.PORTAL_DISTANCE;
        for (int i = 0; i < blocksRange * 2 + 1; i++) {
            for (int j = 0; j < blocksRange * 2 + 2; j++) {
                for (int k = 0; k < blocksRange * 2 + 1; k++) {
                    Location tempLocation = new Location(location.getWorld(), location.getBlockX() + 0.5 - blocksRange + i, location.getBlockY() + 0.5 - blocksRange + j, location.getBlockZ() + 0.5 - blocksRange + k);
                    if (isGate(tempLocation)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Gate getGateNearby(Location location) {
        final int blocksRange = Gate.PORTAL_DISTANCE;

        Location nearbyLocation = null;
        double d = Double.MAX_VALUE;

        for (int i = 0; i < blocksRange * 2 + 1; i++) {
            for (int j = 0; j < blocksRange * 2 + 2; j++) {
                for (int k = 0; k < blocksRange * 2 + 1; k++) {
                    Location tempLocation = new Location(location.getWorld(), location.getBlockX() + 0.5 - blocksRange + i, location.getBlockY() + 0.5 - blocksRange + j, location.getBlockZ() + 0.5 - blocksRange + k);
                    Gate gate = getGate(tempLocation);

                    if (gate != null) {


                        if (nearbyLocation == null) {
                            nearbyLocation = tempLocation;
                            d = nearbyLocation.distance(location);
                        } else {
                            if (tempLocation.distance(location) < d) {
                                nearbyLocation = tempLocation;
                                d = tempLocation.distance(location);
                            }
                        }
                    }
                }
            }
        }
        return getGate(nearbyLocation);
    }

    public Location getGateNearbyUpdatedLocation(Location location) {

        Location nearbyLocation = null;
        double d = Double.MAX_VALUE;

        final int blocksRange = Gate.PORTAL_DISTANCE;
        for (int i = 0; i < blocksRange * 2 + 1; i++) {
            for (int j = 0; j < blocksRange * 2 + 2; j++) {
                for (int k = 0; k < blocksRange * 2 + 1; k++) {
                    Location tempLocation = new Location(location.getWorld(), location.getBlockX() + 0.5 - blocksRange + i, location.getBlockY() + 0.5 - blocksRange + j, location.getBlockZ() + 0.5 - blocksRange + k);
                    Gate gate = getGate(tempLocation);

                    if (gate != null) {

                        if (nearbyLocation == null) {
                            nearbyLocation = tempLocation;
                            d = nearbyLocation.distance(location);
                        } else {
                            if (tempLocation.distance(location) < d) {
                                nearbyLocation = tempLocation;
                                d = tempLocation.distance(location);
                            }
                        }
                    }
                }
            }
        }
        return nearbyLocation;
    }


    public Gate getGate(Location location) {

        Gate gate = locationGateHashMap.get(new GateBlock(location));

        Location loc2 = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
        Gate gate2 = locationGateHashMap.get(new GateBlock(loc2));

        if (gate != null)
            return gate;
        if (gate2 != null)
            return gate2;
        return null;
    }

    public LinkedList<Gate> getGatesOf(UUID player) {
        LinkedList<Gate> gates = playerGateHashMap.get(player);
        if (gates == null)
            return new LinkedList<Gate>();
        else return gates;
    }

    public void setGatesOf(UUID player, LinkedList<Gate> gates) {
        playerGateHashMap.put(player, gates);
        for (Gate gate : gates) {
            locationGateHashMap.put(new GateBlock(gate.getFrom()), gate);
            locationGateHashMap.put(new GateBlock(gate.getTo()), gate);
        }
    }

    public void rotateGate(Gate gate, Block block) {
        gate.rotate(block);
    }
}



