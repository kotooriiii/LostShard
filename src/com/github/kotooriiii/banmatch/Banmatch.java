package com.github.kotooriiii.banmatch;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.plots.ArenaPlot;
import com.github.kotooriiii.plots.PlayerStatusRespawnListener;
import com.github.kotooriiii.plots.Plot;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class Banmatch {

    private UUID fighterA;
    private UUID fighterB;

    private Material armorType;
    private int protection;

    private Material swordType;
    private int sharpness;
    private int fireAspect;

    private int power;

    private String unbannedTime;

    private int banmatchBegin;
    private BukkitTask task;

    private boolean isActive = false;


    private static HashMap<UUID, Banmatch> banmatchCreator = new HashMap<>();

    private static Banmatch activeMatch;

    public Banmatch(UUID fighterA, UUID fighterB) {
        this.fighterA = fighterA;
        this.fighterB = fighterB;

        armorType = null;
        protection = -1;

        swordType = null;
        sharpness = -1;
        fireAspect = -1;

        power = -1;

        unbannedTime = null;

        banmatchBegin = -1;
        task = null;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void end(UUID loser) {
        UUID winner = null;

        if (fighterA.equals(loser)) {
            winner = fighterB;
        } else if (fighterB.equals(loser)) {
            winner = fighterA;
        }

        win(Bukkit.getOfflinePlayer(winner));
        ban(Bukkit.getOfflinePlayer(loser));

        activeMatch = null;
    }

    public boolean isFighter(UUID candidateUUID) {
        return fighterA.equals(candidateUUID) || fighterB.equals(candidateUUID);
    }

    public static Banmatch getActiveMatch() {
        return activeMatch;
    }

    public static boolean hasActiveMatch() {
        return getActiveMatch() != null;
    }

    public void startCountdown() {

        activeMatch = this;

        task = new BukkitRunnable() {


            @Override
            public void run() {

                if (isCancelled())
                    return;

                significantTime(banmatchBegin);

                if (banmatchBegin == 0) {
                    sendToAll(ChatColor.GREEN + "Banmatch is in progress.");
                    start();
                    this.cancel();
                    return;
                }

                banmatchBegin--;
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 60 * 20 * 1);
    }

    public void start() {
        OfflinePlayer fighterA = Bukkit.getOfflinePlayer(getFighterA());
        OfflinePlayer fighterB = Bukkit.getOfflinePlayer(getFighterB());

        //Both not online.
        if (!fighterA.isOnline() && !fighterB.isOnline()) {
            sendToAll(ChatColor.RED + "Both players did not participate in the battle.");
            return;
        } else if (!fighterA.isOnline()) {
            sendToAll(ChatColor.YELLOW + fighterA.getName() + ChatColor.RED + " did not participate in battle.");
            win(fighterB);
            return;
        } else if (!fighterB.isOnline()) {
            sendToAll(ChatColor.YELLOW + fighterB.getName() + ChatColor.RED + " did not participate in battle.");
            win(fighterA);
            return;
        }

        isActive = true;
        Player playerA = fighterA.getPlayer();
        Player playerB = fighterB.getPlayer();

        ArenaPlot arenaPlot = (ArenaPlot) Plot.getPlot("Arena");

        if (arenaPlot.getSpawnA() == null)
            sendToAll(ERROR_COLOR + "The arena's spawn A has not been set. Banmatch did not start.");
        if (arenaPlot.getSpawnB() == null)
            sendToAll(ERROR_COLOR + "The arena's spawn B has not been set. Banmatch did not start.");

        if (arenaPlot.getSpawnA() == null || arenaPlot.getSpawnB() == null)
            return;


        playerA.teleport(arenaPlot.getSpawnA());
        playerB.teleport(arenaPlot.getSpawnB());
    }

    private void win(OfflinePlayer offlinePlayer) {
        sendToAll(ChatColor.YELLOW + offlinePlayer.getName() + ChatColor.BLUE + " is the winner of the banmatch!");
    }

    private void ban(OfflinePlayer offlinePlayer) {
        if (offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().kickPlayer("You were defeated in a banmatch.");
        }
        BannedPlayer bannedPlayer = new BannedPlayer(offlinePlayer.getUniqueId(), null, "You were defeated in a banmatch.");
        FileManager.ban(bannedPlayer);
        sendToAll(ChatColor.YELLOW + offlinePlayer.getName() + ChatColor.RED + " has been banned.");
    }

    private void sendToAll(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    private void significantTime(int counter) {
        int[] significant = new int[]
                {5, 4, 3, 2, 1};

        for (int sig : significant) {
            if (counter == sig) {
                for (Player player : Bukkit.getOnlinePlayers())
                    player.sendMessage(ChatColor.GREEN + "Banmatch in session. ETA " + counter + " mins");
                break;
            }
        }
    }


    //Getters /setters

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

    public String getUnbannedTime() {
        return unbannedTime;
    }

    public void setUnbannedTime(String time) {
        this.unbannedTime = time;
    }

    public void setUnbannedTime(ZonedDateTime unbannedTime) {
        this.unbannedTime = unbannedTime + ""; //tdo look over
    }

    public void setUnbannedTime(int days, int hours, int minutes, int seconds) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime nextRun = now.plusDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        this.unbannedTime = nextRun + ""; //tdoo lookover
    }

    public int getBanmatchBegin() {
        return banmatchBegin;
    }

    public void setBanmatchBegin(int banmatchBegin) {
        this.banmatchBegin = banmatchBegin;
    }

    public static boolean isCreatingBanmatch(UUID uuid) {
        return banmatchCreator.get(uuid) != null;
    }

    public static Banmatch wrap(UUID uuid) {
        return banmatchCreator.get(uuid);
    }

    public static HashMap<UUID, Banmatch> getBanmatchCreator() {
        return banmatchCreator;
    }

    private String proper(String msg) {

        if (msg.contains("_"))
            msg = msg.substring(0, msg.indexOf("_"));
        return msg.substring(0, 1).toUpperCase() + msg.substring(1).toLowerCase();
    }

    private String toRoman(int num) {
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

        information += IDENTITY + "Banmatch in session.";
        information += "\n" + DEFAULT + Bukkit.getOfflinePlayer(getFighterA()).getName() + IDENTITY + " vs " + DEFAULT + Bukkit.getOfflinePlayer(getFighterB()).getName();
        information += "\n" + IDENTITY + "Terms:";
        information += "\n" + DEFAULT + "Protection " + toRoman(getProtection()) + " " + proper(getArmorType().getKey().getKey()) + " Armor";
        information += "\n" + DEFAULT + "Sharpness " + toRoman(getSharpness()) + " Fire " + toRoman(getFireAspect()) + " " + proper(getSwordType().getKey().getKey()) + " Sword";
        information += "\n" + DEFAULT + "Power " + toRoman(getPower()) + " Bow";
        information += "\n" + IDENTITY + "Ban term: " + DEFAULT + getUnbannedTime();
        information += "\n" + IDENTITY + "Event will begin in: " + DEFAULT + getBanmatchBegin() + " min";


        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(information);
        }

    }

    public void cancel() {
        this.getTask().cancel();
        if (isActive) {
            OfflinePlayer fighterA = Bukkit.getOfflinePlayer(getFighterA());
            OfflinePlayer fighterB = Bukkit.getOfflinePlayer(getFighterB());

            if (fighterA.isOnline()) {
                fighterA.getPlayer().teleport(PlayerStatusRespawnListener.getSpawnLocation(fighterA.getPlayer()));
            }

            if (fighterB.isOnline()) {
                fighterB.getPlayer().teleport(PlayerStatusRespawnListener.getSpawnLocation(fighterB.getPlayer()));
            }
        }
    }
}
