package com.github.kotooriiii.stats;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class Stat {

    public static final double BASE_MAX_STAMINA = 100;
    public static final double BASE_MAX_MANA = 100;

    public static final double HOST_MAX_STAMINA = 115;
    public static final double HOST_MAX_MANA = 115;

    public double maxStamina = BASE_MAX_STAMINA;
    public double maxMana = BASE_MAX_MANA;

    private double stamina;
    private double mana;

    private String title;
    private boolean isGold;

    private UUID playerUUID;

    private boolean isPrivate;
    private Location spawn;

    private long millisInit;

    private static HashMap<UUID, Stat> statMap = new HashMap<>();

    private static HashSet<UUID> restingPlayers = new HashSet<>();
    private static HashSet<UUID> meditatingPlayers = new HashSet<>();

    public Stat(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.stamina = maxStamina;
        this.mana = maxMana;
        this.spawn = null;
        this.title = "";
        this.isGold = false;
        this.isPrivate = false;
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

    public String getTitle() {
        return isGold ? ChatColor.GOLD + title : title;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setStamina(double value) {
        stamina = value;
    }

    public void setMana(double value) {
        mana = value;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Location getSpawn() {
        return spawn;
    }

    public boolean hasSpawn()
    {
        return  spawn != null;
    }

    public long getMillisInit() {
        return millisInit;
    }

    public void setMillisInit(long millisInit) {
        this.millisInit = millisInit;
    }

    public void setMaxStamina(double maxStamina) {
        this.maxStamina = maxStamina;
        if(stamina > maxStamina)
            stamina = maxStamina;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;

        if(mana > maxMana)
            mana = maxMana;

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

    public void add() {
        statMap.put(this.getPlayerUUID(), this);
    }

    public void setGold(boolean b) {
        isGold = b;
    }

    public boolean isGold() {
        return isGold;
    }
}
