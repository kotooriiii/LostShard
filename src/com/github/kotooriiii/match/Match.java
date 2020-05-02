package com.github.kotooriiii.match;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.struct.ArenaPlot;
import com.github.kotooriiii.plots.listeners.PlayerStatusRespawnListener;
import com.github.kotooriiii.plots.struct.Plot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.sendToAll;

public class Match {

    public static String NAME = "Match";

    private UUID fighterA;
    private UUID fighterB;

    private Material armorType;
    private int protection;
    private Material swordType;
    private int sharpness;
    private int fireAspect;
    private int power;

    private int beginCountdown;
    private BukkitTask task;

    private boolean hasGameStarted = false;

    private static HashMap<UUID, Match> matchCreatorMap = new HashMap<>();

    private static Match activeMatch;

    public Match(UUID fighterA, UUID fighterB) {
        this.fighterA = fighterA;
        this.fighterB = fighterB;

        armorType = null;
        protection = -1;

        swordType = null;
        sharpness = -1;
        fireAspect = -1;

        power = -1;

        beginCountdown = -1;
        task = null;
    }

    public void initializer() {

    }

    public void deinitializer() {

    }

    public BukkitTask getTask() {
        return task;
    }

    public void end(UUID loser) {

        activeMatch = null;
        hasGameStarted = false;

        UUID winner = null;

        if (fighterA.equals(loser)) {
            winner = fighterB;
        } else if (fighterB.equals(loser)) {
            winner = fighterA;
        }

        win(Bukkit.getOfflinePlayer(winner));
        lose(Bukkit.getOfflinePlayer(loser));
    }

    public boolean isFighter(UUID candidateUUID) {
        return fighterA.equals(candidateUUID) || fighterB.equals(candidateUUID);
    }

    public void startCountdown() {

        activeMatch = this;
        initializer();
        task = new BukkitRunnable() {


            @Override
            public void run() {

                if (isCancelled())
                    return;

                significantTime(beginCountdown);

                if (beginCountdown == 0) {
                    sendToAll(ChatColor.GREEN + getName() + " is in progress.");
                    start();
                    this.cancel();
                    return;
                }

                beginCountdown--;
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 60 * 20 * 1);
    }

    private void start() {
        OfflinePlayer fighterA = Bukkit.getOfflinePlayer(getFighterA());
        OfflinePlayer fighterB = Bukkit.getOfflinePlayer(getFighterB());

        //Both not online.
        if (!fighterA.isOnline() && !fighterB.isOnline()) {
            sendToAll(ChatColor.RED + "Both players did not participate in the battle.");
            return;
        } else if (!fighterA.isOnline()) {
            sendToAll(ChatColor.YELLOW + fighterA.getName() + ChatColor.RED + " did not participate in battle.");
            win(fighterB);
            lose(fighterA);
            return;
        } else if (!fighterB.isOnline()) {
            sendToAll(ChatColor.YELLOW + fighterB.getName() + ChatColor.RED + " did not participate in battle.");
            win(fighterA);
            lose(fighterB);
            return;
        }

        Player playerA = fighterA.getPlayer();
        Player playerB = fighterB.getPlayer();

        ArenaPlot arenaPlot = (ArenaPlot) LostShardPlugin.getPlotManager().getPlot("Arena");

        if (arenaPlot == null) {
            sendToAll(ERROR_COLOR + "The arena plot has not been set.");
            return;
        }

        if (arenaPlot.getSpawnA() == null)
            sendToAll(ERROR_COLOR + "The arena's spawn A has not been set. " + getName() + " did not start.");
        if (arenaPlot.getSpawnB() == null)
            sendToAll(ERROR_COLOR + "The arena's spawn B has not been set. " + getName() + " did not start.");

        if (arenaPlot.getSpawnA() == null || arenaPlot.getSpawnB() == null)
            return;


        playerA.teleport(arenaPlot.getSpawnA());
        playerB.teleport(arenaPlot.getSpawnB());
        hasGameStarted = true;
    }

    public void win(OfflinePlayer offlinePlayer) {
        sendToAll(ChatColor.YELLOW + offlinePlayer.getName() + ChatColor.BLUE + " is the winner of the " + getName() + "!");
    }

    public void lose(OfflinePlayer offlinePlayer) {
        sendToAll(ChatColor.YELLOW + offlinePlayer.getName() + ChatColor.BLUE + " was defeated in a " + getName() + ".");

    }

    private void significantTime(int counter) {
        int[] significant = new int[]
                {5, 4, 3, 2, 1};

        for (int sig : significant) {
            if (counter == sig) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (counter == 1)
                        player.sendMessage(ChatColor.GREEN + getName() + " in session. ETA " + counter + " min");
                    else
                        player.sendMessage(ChatColor.GREEN + getName() + " in session. ETA " + counter + " mins");

                }
                break;
            }
        }
    }


    //Getters /setters
    public boolean hasGameStarted() {
        return hasGameStarted;
    }

    public static Match getActiveMatch() {
        return activeMatch;
    }

    public static boolean hasActiveMatch() {
        return getActiveMatch() != null;
    }

    public UUID getFighterA() {
        return fighterA;
    }

    public UUID getFighterB() {
        return fighterB;
    }

    public Material getArmorType() {
        return armorType;
    }

    public void setArmorType(Material armorType) {
        this.armorType = armorType;
    }

    public int getProtection() {
        return protection;
    }

    public void setProtection(int protection) {
        this.protection = protection;
    }

    public Material getSwordType() {
        return swordType;
    }

    public void setSwordType(Material swordType) {
        this.swordType = swordType;
    }

    public int getSharpness() {
        return sharpness;
    }

    public void setSharpness(int sharpness) {
        this.sharpness = sharpness;
    }

    public int getFireAspect() {
        return fireAspect;
    }

    public void setFireAspect(int fireAspect) {
        this.fireAspect = fireAspect;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getBeginCountdown() {
        return beginCountdown;
    }

    public void setBeginCountdown(int beginCountdown) {
        this.beginCountdown = beginCountdown;
    }

    public static boolean isCreatingMatch(UUID uuid) {
        return matchCreatorMap.get(uuid) != null;
    }

    public static Match wrap(UUID uuid) {
        return matchCreatorMap.get(uuid);
    }

    public static HashMap<UUID, Match> getMatchCreatorMap() {
        return matchCreatorMap;
    }

    public String proper(String msg) {

        if (msg.contains("_"))
            msg = msg.substring(0, msg.indexOf("_"));
        return msg.substring(0, 1).toUpperCase() + msg.substring(1).toLowerCase();
    }

    public String toRoman(int num) {
        switch (num) {
            case 0:
                return "NONE";
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return "null";
        }
    }

    public void inform() {

        String information = "";

        ChatColor DEFAULT = ChatColor.YELLOW;
        ChatColor IDENTITY = ChatColor.BLUE;

        information += IDENTITY + getName() + " in session.";
        information += "\n" + DEFAULT + Bukkit.getOfflinePlayer(getFighterA()).getName() + IDENTITY + " vs " + DEFAULT + Bukkit.getOfflinePlayer(getFighterB()).getName();
        information += "\n" + IDENTITY + "Terms:";
        information += "\n" + DEFAULT + "Protection " + toRoman(getProtection()) + " " + proper(getArmorType().getKey().getKey()) + " Armor";
        information += "\n" + DEFAULT + "Sharpness " + toRoman(getSharpness()) + " Fire " + toRoman(getFireAspect()) + " " + proper(getSwordType().getKey().getKey()) + " Sword";
        information += "\n" + DEFAULT + "Power " + toRoman(getPower()) + " Bow";
        information += "\n" + IDENTITY + "Event will begin in: " + DEFAULT + getBeginCountdown() + " min";


        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(information);
        }

    }

    public void cancel(Player player) {
        getMatchCreatorMap().remove(player.getUniqueId());
        cancel(false);
    }

    public void cancel(boolean returnToSpawn) {

        sendToAll(ChatColor.GREEN + this.getName() + " has been canceled.");


        if (activeMatch != null) {
            deinitializer();
        }

        this.getTask().cancel();

        if (hasGameStarted) {
            OfflinePlayer fighterA = Bukkit.getOfflinePlayer(getFighterA());
            OfflinePlayer fighterB = Bukkit.getOfflinePlayer(getFighterB());

            if (returnToSpawn) {
                if (fighterA.isOnline()) {
                    fighterA.getPlayer().teleport(PlayerStatusRespawnListener.getSpawnLocation(fighterA.getPlayer()));
                }

                if (fighterB.isOnline()) {
                    fighterB.getPlayer().teleport(PlayerStatusRespawnListener.getSpawnLocation(fighterB.getPlayer()));
                }
            }
        }

        hasGameStarted = false;
        activeMatch = null;
    }

    public static String getName() {
        return NAME;
    }


}

