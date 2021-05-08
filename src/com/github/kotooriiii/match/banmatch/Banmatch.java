package com.github.kotooriiii.match.banmatch;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bannedplayer.BannedPlayer;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.match.Match;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.PLAYER_COLOR;
import static com.github.kotooriiii.util.HelperMethods.sendToAll;

public class Banmatch extends Match {

    private ZonedDateTime unbannedTime;
    private String consequentMessage;

    private final static String INDEFINITE_BAN_IDENTIFIER = "Indefinite";
    private final static String BAN_MESSAGE = "You have been banned.";
    private ZonedDateTime zonedDateTime;

    public Banmatch(UUID fighterA, UUID fighterB) {
        super(fighterA, fighterB);
        super.NAME = "Banmatch";
        unbannedTime = null;
        consequentMessage = null;
    }

    @Override
    public void initializer() {
        super.initializer();
    }

    @Override
    public void win(OfflinePlayer offlinePlayer) {
    }

    @Override
    public void lose(OfflinePlayer offlinePlayer) {

        super.lose(offlinePlayer);

        LostShardPlugin.getCombatLogManager().remove(offlinePlayer.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                ZonedDateTime unbannedDate = ZonedDateTime.now().plus(getUnbannedTime().minus(getInitTime().toInstant().toEpochMilli(), ChronoUnit.MILLIS).toInstant().toEpochMilli(), ChronoUnit.MILLIS);
                BannedPlayer bannedPlayer = new BannedPlayer(offlinePlayer.getUniqueId(),unbannedDate, BAN_MESSAGE);
                LostShardPlugin.getBanManager().ban(bannedPlayer, true, true);
                sendToAll(PLAYER_COLOR + offlinePlayer.getName() + ChatColor.GREEN + " is banned.");
            }
        }.runTaskLater(LostShardPlugin.plugin, 20*15);



        sendToAll(PLAYER_COLOR + offlinePlayer.getName() + ChatColor.GREEN + " is banned in 15 seconds.");
    }

    public ZonedDateTime getUnbannedTime() {
        return unbannedTime;
    }

    public void setUnbannedTime(ZonedDateTime unbannedTime) {
        this.unbannedTime = unbannedTime;
    }

    public void setInitTime(ZonedDateTime zonedDateTime)
    {
        this.zonedDateTime = zonedDateTime;
    }

    public ZonedDateTime getInitTime() {
        return zonedDateTime;
    }

    public String getConsequentMessage() {
        return consequentMessage;
    }

    public void setConsequentMessage(String consequentMessage) {
        this.consequentMessage = consequentMessage;
    }

    @Override
    public void inform() {
        String information = "";

        ChatColor DEFAULT = ChatColor.GOLD;
        ChatColor IDENTITY = ChatColor.BLUE;

        String banTermLift = "";
        if (getUnbannedTime().getYear() == 0) {
            banTermLift = Banmatch.getIndefiniteBanIdentifier();
        } else {
            banTermLift = HelperMethods.until(getUnbannedTime());
        }


        information += IDENTITY + getName() + " in session.";
        information += "\n" + DEFAULT + Bukkit.getOfflinePlayer(getFighterA()).getName() + IDENTITY + " vs " + DEFAULT + Bukkit.getOfflinePlayer(getFighterB()).getName();
        information += "\n" + IDENTITY + "Terms:";
        information += "\n" + DEFAULT + "Protection " + toRoman(getProtection()) + " " + proper(getArmorType().getKey().getKey()) + " Armor";
        information += "\n" + DEFAULT + "Sharpness " + toRoman(getSharpness()) + " Fire " + toRoman(getFireAspect()) + " " + proper(getSwordType().getKey().getKey()) + " Sword";
        information += "\n" + DEFAULT + "Power " + toRoman(getPower()) + " Bow";
        information += "\n" + IDENTITY + "Ban term: " + DEFAULT + consequentMessage;
        information += "\n" + IDENTITY + "Lifted on: " + DEFAULT + banTermLift;
        information += "\n" + IDENTITY + "Event will begin in: " + DEFAULT + getBeginCountdown() + " min";


        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(information);
        }
    }

    public static String getIndefiniteBanIdentifier() {
        return INDEFINITE_BAN_IDENTIFIER;
    }

    public static String getBanMessage() {
        return BAN_MESSAGE;
    }
}
