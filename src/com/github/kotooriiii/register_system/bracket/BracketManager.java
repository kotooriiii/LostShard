package com.github.kotooriiii.register_system.bracket;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class BracketManager {

    private BracketTree<Bracket> tree;
    private int currentLevel;

    public BracketManager(Player[] players) {
        this(toUUID(players));
    }

    public BracketManager(UUID[] uuids) {
        shuffle(uuids);
        tree = new BracketTree<>(populate(uuids));
        currentLevel = tree.getMaxLevel();
    }

    //STATIC
    private static UUID[] toUUID(Player[] players) {
        ArrayList<UUID> uuids = new ArrayList<>();
        for (Player player : players) {
            if (player == null || !player.isOnline())
                continue;
            uuids.add(player.getUniqueId());
        }
        return uuids.toArray(new UUID[0]);
    }

    //HELPER CREATE
    private void shuffle(UUID[] array) {
        Random rand = new Random();

        for (int i = 0; i < array.length; i++) {
            int randomIndexToSwap = rand.nextInt(array.length);
            UUID temp = array[randomIndexToSwap];
            array[randomIndexToSwap] = array[i];
            array[i] = temp;
        }
    }

    private Bracket[] populate(UUID[] uuids) {
        ArrayList<Bracket> brackets = new ArrayList<>();

        Bracket<UUID> bracket;
        UUID a = null;
        UUID b = null;
        for (int i = 0; i < uuids.length; i++) {
            if (i % 2 == 0) {
                a = uuids[i];
            } else {
                b = uuids[i];
                bracket = new Bracket<>(a, b);
                a = null;
                brackets.add(bracket);
            }
        }

        if(a!=null)
            brackets.add(new Bracket(a));
        return brackets.toArray(new Bracket[0]);
    }




}
