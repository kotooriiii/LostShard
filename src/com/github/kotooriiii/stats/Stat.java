package com.github.kotooriiii.stats;

import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class Stat {

    public double maxStamina = 100;
    public double maxMana = 100;

    private double stamina;
    private double mana;

    private String title;

    private UUID playerUUID;

    private static HashMap<UUID, Stat> statMap = new HashMap<>();

    private static HashSet<UUID> restingPlayers = new HashSet<>();
    private static HashSet<UUID> meditatingPlayers = new HashSet<>();

    public Stat(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.stamina = maxStamina;
        this.mana = maxMana;
        this.title = "";
        statMap.put(playerUUID, this);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public double getMaxStamina() {
        return maxStamina;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public double getStamina() {
        return stamina;
    }

    public double getMana() {
        return mana;
    }

    public String getTitle() {return  title;}

    public void setStamina(double value) {
        stamina = value;
    }

    public void setMana(double value) {
        mana = value;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMaxStamina(double maxStamina) {
        this.maxStamina = maxStamina;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }

    public String getStaminaString() {
        DecimalFormat df = new DecimalFormat("##.#");
    //    return df.format(this.getStamina()) + "/" + df.format(this.getMaxStamina());
        return String.valueOf((int) this.getStamina() + "/" + (int) this.getMaxStamina());

    }

    public String getManaString() {
        DecimalFormat df = new DecimalFormat("##.#");
        //return df.format(this.getMana()) + "/" + df.format(this.getMaxMana());
        return String.valueOf((int) this.getMana() + "/" + (int) this.getMaxMana());
    }

    public static HashMap<UUID, Stat> getStatMap() {
        return statMap;
    }

    public static Stat wrap(Player player) {
        return statMap.get(player.getUniqueId());
    }
    public static Stat wrap(UUID playerUUID) {
        return statMap.get(playerUUID);
    }

    public static HashSet<UUID> getMeditatingPlayers() {
        return meditatingPlayers;
    }

    public static HashSet<UUID> getRestingPlayers() {
        return restingPlayers;
    }
}
