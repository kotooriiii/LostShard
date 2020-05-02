package com.github.kotooriiii.scoreboard;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.ranks.RankType;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StaffType;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

import static com.github.kotooriiii.data.Maps.hudContainer;

public class ShardScoreboardManager {

    private static HashMap<String, String> map = new HashMap<>();

    public static void registerScoreboard(Player player) {

        //Creates a new scoreboard for the player.
        Scoreboard registerScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();


        Team worthy = registerScoreboard.registerNewTeam(Status.WORTHY.getName());
        worthy.setColor(Status.WORTHY.getChatColor());
        Team corrupt = registerScoreboard.registerNewTeam(Status.CORRUPT.getName());
        corrupt.setColor(Status.CORRUPT.getChatColor());
        Team exiled = registerScoreboard.registerNewTeam(Status.EXILED.getName());
        exiled.setColor(Status.EXILED.getChatColor());

        Team owner = registerScoreboard.registerNewTeam(StaffType.OWNER.getName());
        owner.setColor(StaffType.OWNER.getChatColor());
        Team coowner = registerScoreboard.registerNewTeam(StaffType.COOWNER.getName());
        coowner.setColor(StaffType.COOWNER.getChatColor());
        Team admin = registerScoreboard.registerNewTeam(StaffType.ADMIN.getName());
        admin.setColor(StaffType.ADMIN.getChatColor());
        Team moderator = registerScoreboard.registerNewTeam(StaffType.MODERATOR.getName());
        moderator.setColor(StaffType.MODERATOR.getChatColor());

        player.setScoreboard(registerScoreboard);
        registerProfileObjective(player);
        updateCache(player);
    }

    private static void registerProfileObjective(Player player) {
        //Scoreboard get from player
        Scoreboard scoreboard = player.getScoreboard();

        //Objective register
        Objective objective = scoreboard.registerNewObjective("profile", "dummy", "", RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GOLD + "-" + player.getName() + "'s Stats-");



        Score manaScore = objective.getScore(ChatColor.BLUE + "Mana");
        manaScore.setScore(16);

        Team manaValue = scoreboard.registerNewTeam("mana");
        manaValue.addEntry(ChatColor.AQUA + "" + ChatColor.BLUE);
        objective.getScore(ChatColor.AQUA + "" + ChatColor.BLUE).setScore(15);

        Score staminaScore = objective.getScore(ChatColor.RED + "Stamina");
        staminaScore.setScore(14);

        Team staminaValue = scoreboard.registerNewTeam("stamina");
        staminaValue.addEntry(ChatColor.BLUE + "" + ChatColor.DARK_BLUE);
        objective.getScore(ChatColor.BLUE + "" + ChatColor.DARK_BLUE).setScore(13);

      Score balanceScore = objective.getScore(ChatColor.GOLD + "Balance");
       balanceScore.setScore(12);

        Team balanceValue = scoreboard.registerNewTeam("balance");
        balanceValue.addEntry(ChatColor.GREEN + "" + ChatColor.DARK_GREEN);
        objective.getScore(ChatColor.GREEN + "" + ChatColor.DARK_GREEN).setScore(11);

     Score murderCountScore = objective.getScore(ChatColor.DARK_RED + "Murder count");
        murderCountScore.setScore(10);

        Team murderCountValue = scoreboard.registerNewTeam("murderCount");
        murderCountValue.addEntry(ChatColor.RED + "" + ChatColor.DARK_RED);
        objective.getScore(ChatColor.RED + "" + ChatColor.DARK_RED).setScore(9);

    }

    public static void updateScoreboard() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (Player player : Bukkit.getOnlinePlayers()) {

                    Scoreboard scoreboard = player.getScoreboard();
                    if(scoreboard == null)
                        continue;

                    //If player does not want a hud
                    if (hudContainer.contains(player.getUniqueId())) {
                        //If profile is active
                        if (player.getScoreboard().getObjective("profile").getDisplaySlot()  != null)
                            //Remove profile
                            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                        continue;
                    }

                    if (player.getScoreboard().getObjective("profile").getDisplaySlot() == null)
                        player.getScoreboard().getObjective("profile").setDisplaySlot(DisplaySlot.SIDEBAR);

                    Bank bank = Bank.wrap(player.getUniqueId());
                    if(bank == null)
                        continue;
                    Stat stat = Stat.wrap(player);
                    if(stat == null)
                        continue;
                    StatusPlayer statusPlayer = StatusPlayer.wrap(player.getUniqueId());
                    if(statusPlayer == null)
                        continue;

                    scoreboard.getTeam("mana").setPrefix(ChatColor.WHITE + "" + (int) stat.getMana() + "/" + (int) stat.getMaxMana());
                    scoreboard.getTeam("stamina").setPrefix(ChatColor.WHITE + "" + (int) stat.getStamina() + "/" + (int) stat.getMaxStamina());

                    //scoreboard.getTeam("balance").setPrefix(ChatColor.GOLD + "Balance: ");
                    scoreboard.getTeam("balance").setSuffix(ChatColor.WHITE + "" + bank.getCurrency() + "");

                 //   scoreboard.getTeam("murderCount").setPrefix(ChatColor.RED + "Murder count: ");
                    scoreboard.getTeam("murderCount").setSuffix(ChatColor.WHITE + "" + statusPlayer.getKills() + "");

                }
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 5);
    }

    private static void remove(Player player) {
        remove(player.getName());
    }

    private static void remove(OfflinePlayer player) {
        remove(player.getName());
    }

    private static void remove(String playerName) {

        map.remove(playerName);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = onlinePlayer.getScoreboard();
            for (Team team : scoreboard.getTeams()) {
                if (team.hasEntry(playerName))
                    scoreboard.getTeam(team.getName()).removeEntry(playerName);
            }
        }
    }

    public static void add(Player player, String teamName) {
        add(player.getName(), teamName);
    }

    public static void add(OfflinePlayer player, String teamName) {
        add(player.getName(), teamName);
    }

    public static void add(String playerName, String teamName) {
        remove(playerName);
        map.put(playerName,teamName);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            Scoreboard scoreboard = onlinePlayer.getScoreboard();
            scoreboard.getTeam(teamName).addEntry(playerName);

        }
    }

    public static void updateCache(Player player)
    {
        for(Map.Entry<String,String> entry : map.entrySet())
        {
            String playerName = entry.getKey();
            String teamName = entry.getValue();

            Scoreboard scoreboard = player.getScoreboard();
            scoreboard.getTeam(teamName).addEntry(playerName);
        }
    }
}
