package com.github.kotooriiii.match.banmatch;

import com.github.kotooriiii.bannedplayer.BannedPlayer;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.match.Match;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.UUID;

public class Banmatch extends Match {

    private ZonedDateTime unbannedTime;

    private final static String INDEFINITE_BAN_IDENTIFIER = "Indefinite";
    private final static String BAN_MESSAGE = "You were defeated in a " + getName() + ".";

    public Banmatch(UUID fighterA, UUID fighterB) {
      super(fighterA, fighterB);
      super.NAME = "Banmatch";
      unbannedTime = null;
    }

    @Override
    public void initializer()
    {super.initializer();}

    @Override
    public void win(OfflinePlayer offlinePlayer) {
        sendToAll(ChatColor.YELLOW + offlinePlayer.getName() + ChatColor.BLUE + " is the winner of the " + getName() + "!");
    }

    @Override
    public void lose(OfflinePlayer offlinePlayer) {
        if (offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().kickPlayer(BAN_MESSAGE);
        }
        BannedPlayer bannedPlayer = new BannedPlayer(offlinePlayer.getUniqueId(), getUnbannedTime(), "You were defeated in a " + getName() + ".");
        FileManager.write(bannedPlayer);

        if(getUnbannedTime().getYear() == 0)
        sendToAll(ChatColor.YELLOW + offlinePlayer.getName() + ChatColor.RED + " has been banned indefinitely.");
        else
            sendToAll(ChatColor.YELLOW + offlinePlayer.getName() + ChatColor.RED + " has been banned until " + HelperMethods.until(getUnbannedTime()));
    }

    public ZonedDateTime getUnbannedTime() {
        return unbannedTime;
    }

    public void setUnbannedTime(ZonedDateTime unbannedTime) {
        this.unbannedTime = unbannedTime;
    }

    @Override
    public void inform() {
        String information = "";

        ChatColor DEFAULT = ChatColor.YELLOW;
        ChatColor IDENTITY = ChatColor.BLUE;

        String banTermLift = "";
        if(getUnbannedTime().getYear() == 0)
        {
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
        information += "\n" + IDENTITY + "Ban term lifted on: " + DEFAULT + banTermLift;
        information += "\n" + IDENTITY + "Event will begin in: " + DEFAULT + getBeginCountdown() + " min";


        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(information);
        }
    }

    public static String getIndefiniteBanIdentifier() {
        return INDEFINITE_BAN_IDENTIFIER;
    }

    public static String getBanMessage()
    {
        return BAN_MESSAGE;
    }
}
