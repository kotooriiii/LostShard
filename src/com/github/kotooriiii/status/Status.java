package com.github.kotooriiii.status;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerRespawnEvent;

public enum Status {
    LAWFUL("Lawful", ChatColor.BLUE, 1), CRIMINAL("Criminal", ChatColor.GRAY, 2), MURDERER("Murderer", ChatColor.RED, 3),
    WORTHY("Worthy", ChatColor.BLUE, 1), CORRUPT("Corrupt", ChatColor.GRAY, 2), EXILED("Exiled", ChatColor.RED, 3);

    private String name;
    private ChatColor chatColor;
    private int weight;

    private Status(String name, ChatColor chatColor, int weight) {
        this.name = name;
        this.chatColor = chatColor;
        this.weight = weight;
    }

    public static Status newStatuses(Status status) {
        switch (status) {
            case EXILED:
                return MURDERER;
            case CORRUPT:
                return CRIMINAL;
            case WORTHY:
                return LAWFUL;
        }
        return status;
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public static Status matchStatus(String name) {
        for (Status status : Status.values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }
        return null;
    }

    public String getOrganization() {
        switch (this.getName().toLowerCase()) {
            case "lawful":
                return "Order";
            case "criminal":
            case "murderer":
                return "Chaos";
            default:
                return "";
        }
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return super.toString();
    }


}
