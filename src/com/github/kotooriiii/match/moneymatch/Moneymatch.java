package com.github.kotooriiii.match.moneymatch;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.bank.BankManager;
import com.github.kotooriiii.match.Match;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.PLAYER_COLOR;
import static com.github.kotooriiii.util.HelperMethods.sendToAll;

public class Moneymatch extends Match {

    private double wagerAmount;


    public Moneymatch(UUID fighterA, UUID fighterB) {
        super(fighterA, fighterB);
        super.NAME = "Money match";
        wagerAmount = -1;
    }

    @Override
    public void initializer() {
        BankManager bankManager = LostShardPlugin.getBankManager();
        Bank withdrawalA = bankManager.wrap(getFighterA());
        Bank withdrawalB = bankManager.wrap(getFighterB());
        OfflinePlayer fighterA = Bukkit.getOfflinePlayer(getFighterA());
        OfflinePlayer fighterB = Bukkit.getOfflinePlayer(getFighterB());
        if (fighterA.isOnline())
            fighterA.getPlayer().sendMessage(ChatColor.GOLD + "" + wagerAmount + " has been taken out of your account.");
        if (fighterB.isOnline())
            fighterB.getPlayer().sendMessage(ChatColor.GOLD + "" + wagerAmount + " has been taken out of your account.");
        withdrawalA.removeCurrency(wagerAmount);
        withdrawalB.removeCurrency(wagerAmount);
    }

    @Override
    public void deinitializer() {
        BankManager bankManager = LostShardPlugin.getBankManager();
        Bank depositA = bankManager.wrap(getFighterA());
        Bank depositB = bankManager.wrap(getFighterB());
        OfflinePlayer fighterA = Bukkit.getOfflinePlayer(getFighterA());
        OfflinePlayer fighterB = Bukkit.getOfflinePlayer(getFighterB());
        if (fighterA.isOnline())
            fighterA.getPlayer().sendMessage(ChatColor.GOLD + "" + wagerAmount + " has been returned to your account.");
        if (fighterB.isOnline())
            fighterB.getPlayer().sendMessage(ChatColor.GOLD + "" + wagerAmount + " has been returned to your account.");
        depositA.addCurrency(wagerAmount);
        depositB.addCurrency(wagerAmount);
    }

    @Override
    public void win(OfflinePlayer offlinePlayer) {
        sendToAll(PLAYER_COLOR + offlinePlayer.getName() + ChatColor.GREEN + " has won the money match.");
        BankManager bankManager = LostShardPlugin.getBankManager();
        Bank winnerBank = bankManager.wrap(offlinePlayer.getUniqueId());
        winnerBank.addCurrency(wagerAmount * 2);
        if (offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().sendMessage(ChatColor.GOLD + "" + wagerAmount * 2 + " gold has been deposited into your account.");
        }
    }

    @Override
    public void lose(OfflinePlayer offlinePlayer) {

        super.lose(offlinePlayer);
    }

    public double getWagerAmount() {
        return wagerAmount;
    }

    public void setWagerAmount(double wagerAmount) {
        this.wagerAmount = new BigDecimal(wagerAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public void inform() {
        String information = "";

        ChatColor DEFAULT = ChatColor.GOLD;
        ChatColor IDENTITY = ChatColor.BLUE;

        information += IDENTITY + getName() + " in session.";
        information += "\n" + DEFAULT + Bukkit.getOfflinePlayer(getFighterA()).getName() + IDENTITY + " vs " + DEFAULT + Bukkit.getOfflinePlayer(getFighterB()).getName();
        information += "\n" + IDENTITY + "Terms:";
        information += "\n" + DEFAULT + "Protection " + toRoman(getProtection()) + " " + proper(getArmorType().getKey().getKey()) + " Armor";
        information += "\n" + DEFAULT + "Sharpness " + toRoman(getSharpness()) + " Fire " + toRoman(getFireAspect()) + " " + proper(getSwordType().getKey().getKey()) + " Sword";
        information += "\n" + DEFAULT + "Power " + toRoman(getPower()) + " Bow";
        information += "\n" + IDENTITY + "Wager: " + DEFAULT + wagerAmount * 2;
        information += "\n" + IDENTITY + "Event will begin in: " + DEFAULT + getBeginCountdown() + " min";


        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(information);
        }
    }
}
