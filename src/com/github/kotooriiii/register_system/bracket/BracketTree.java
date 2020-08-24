package com.github.kotooriiii.register_system.bracket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BracketTree<P> {

    private BracketNode root;
    private int maxLevel;

    public BracketTree(P[] array) {

        if (array == null)
            throw new NullPointerException("Array is null.");
        if (array.length == 0)
            throw new RuntimeException("Array cannot be empty.");


        //Grab first entry to create the root
        P firstEntry = array[0];

        //Set as root
        BracketNode rootNode = new BracketNode(null, firstEntry);
        this.root = rootNode;
        this.maxLevel = 0;

        //Return if done.
        if (array.length == 1)
            return;

        //This array list will contain an iterating tree level's worth of nodes. The size of the next level will increase by a multiple of two so we need to make sure those entries are constructed and have null data within.
        ArrayList<BracketNode> levelNodes = new ArrayList<>();
        //Init
        levelNodes.add(rootNode);
        this.maxLevel = 1;

        int childrenNum;
        while (true) {
            childrenNum = maxLevel * 2;

            //Create new list
            ArrayList<BracketNode> newNodes = new ArrayList<>();
            for (BracketNode inode : levelNodes) {
                inode.left = new BracketNode(inode, null);
                inode.right = new BracketNode(inode, null);
                newNodes.add(inode.left);
                newNodes.add(inode.right);
            }
            //replace list
            levelNodes = newNodes;


            //Enough spaces for data to be filled in children spaces
            if (array.length <= childrenNum) {
                //ready to populate with actual data
                break;
            }


            //maxlevel increase and iterate again to check if enough spaces are there
            this.maxLevel++;
        }
        for (int i = 0; i < array.length; i++) {
            levelNodes.get(i).setData(array[i]);
        }
    }

    private ArrayList<P> toDataArray(List<BracketNode> nodes) {
        final ArrayList<P> newNodes = new ArrayList<>(nodes.size());
        nodes.forEach(n -> newNodes.add(n.getData()));
        return newNodes;
    }

    public ArrayList<P> getBrackets(int level) {
        if (level < 0)
            throw new IndexOutOfBoundsException("The level \"" + level + "\" cannot be negative.");
        if (maxLevel < level)
            throw new IndexOutOfBoundsException("The level \"" + level + "\" cannot be higher than " + maxLevel + ".");

        ArrayList<BracketNode> nodes = new ArrayList<>();
        nodes.add(this.root);
        if (level == 0)
            return toDataArray(nodes);
        for (int i = 0; i < level; i++) {
            ArrayList<BracketNode> newNodes = new ArrayList<>();
            for (BracketNode inode : nodes) {
                newNodes.add(inode.left);
                newNodes.add(inode.right);
            }
            nodes = newNodes;
        }


        return toDataArray(nodes);

    }

    public int getMaxLevel() {
        return maxLevel;
    }


    private class BracketNode {
        private P data;
        private BracketNode parent, left, right;

        private BracketNode(BracketNode parent, P data) {
            this.data = data;
            this.parent = parent;
            this.left = null;
            this.right = null;
        }

        public BracketNode getLeft() {
            return this.left;
        }

        public BracketNode getRight() {
            return this.right;
        }

        public BracketNode getParent() {
            return parent;
        }

        public P getData() {
            return this.data;
        }

        public void setData(P data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Data: " + Objects.toString(this.data, "null") + "\n{Children: " + "\n  Left:" + Objects.toString(this.left, "null") + "\n  Right:" + Objects.toString(this.right, "null") + "\n}";
        }

    }

}
