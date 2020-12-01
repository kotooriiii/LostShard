package com.github.kotooriiii.scoreboard;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
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
import java.util.logging.Level;

import static com.github.kotooriiii.data.Maps.hudContainer;

public class ShardScoreboardManager {

    private static HashMap<String, String> map = new HashMap<>();

    public final static HashMap<String, Integer> weightMap = new HashMap<>();

    public static void initDefault() {
        weightMap.put(StaffType.OWNER.getName(), 0);
        weightMap.put(StaffType.COOWNER.getName(), 1);
        weightMap.put(StaffType.ADMIN.getName(), 2);
        weightMap.put(StaffType.MODERATOR.getName(), 3);
        weightMap.put(Status.MURDERER.getName(), 7);
        weightMap.put(Status.CRIMINAL.getName(), 8);
        weightMap.put(Status.LAWFUL.getName(), 9);

        for (Map.Entry entry : weightMap.entrySet())
            LostShardPlugin.logger.log(Level.WARNING, entry.getKey() + " -> " + entry.getValue());

    }


    public static void registerScoreboard(Player player) {

        //Creates a new scoreboard for the player.
        Scoreboard registerScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();


        Team lawful = registerScoreboard.registerNewTeam(weightMap.get(Status.LAWFUL.getName()) + Status.LAWFUL.getName());
        lawful.setColor(Status.LAWFUL.getChatColor());
        lawful.setCanSeeFriendlyInvisibles(false);

        Team criminal = registerScoreboard.registerNewTeam(weightMap.get(Status.CRIMINAL.getName()) + Status.CRIMINAL.getName());
        criminal.setColor(Status.CRIMINAL.getChatColor());
        criminal.setCanSeeFriendlyInvisibles(false);

        Team murderer = registerScoreboard.registerNewTeam(weightMap.get(Status.MURDERER.getName()) + Status.MURDERER.getName());
        murderer.setColor(Status.MURDERER.getChatColor());
        murderer.setCanSeeFriendlyInvisibles(false);

        Team owner = registerScoreboard.registerNewTeam(weightMap.get(StaffType.OWNER.getName()) + StaffType.OWNER.getName());
        owner.setColor(StaffType.OWNER.getChatColor());
        owner.setCanSeeFriendlyInvisibles(false);

        Team coowner = registerScoreboard.registerNewTeam(weightMap.get(StaffType.COOWNER.getName()) + StaffType.COOWNER.getName());
        coowner.setColor(StaffType.COOWNER.getChatColor());
        coowner.setCanSeeFriendlyInvisibles(false);

        Team admin = registerScoreboard.registerNewTeam(weightMap.get(StaffType.ADMIN.getName()) + StaffType.ADMIN.getName());
        admin.setColor(StaffType.ADMIN.getChatColor());
        admin.setCanSeeFriendlyInvisibles(false);

        Team moderator = registerScoreboard.registerNewTeam(weightMap.get(StaffType.MODERATOR.getName()) + StaffType.MODERATOR.getName());
        moderator.setColor(StaffType.MODERATOR.getChatColor());
        moderator.setCanSeeFriendlyInvisibles(false);

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
                    if (scoreboard == null)
                        continue;

                    //If player does not want a hud
                    if (hudContainer.contains(player.getUniqueId())) {
                        //If profile is active
                        if (player.getScoreboard().getObjective("profile").getDisplaySlot() != null)
                            //Remove profile
                            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                        continue;
                    }

                    if (player.getScoreboard().getObjective("profile").getDisplaySlot() == null)
                        player.getScoreboard().getObjective("profile").setDisplaySlot(DisplaySlot.SIDEBAR);

                    Bank bank = LostShardPlugin.getBankManager().wrap(player.getUniqueId());
                    if (bank == null)
                        continue;
                    Stat stat = Stat.wrap(player);
                    if (stat == null)
                        continue;
                    StatusPlayer statusPlayer = StatusPlayer.wrap(player.getUniqueId());
                    if (statusPlayer == null)
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
                    team.removeEntry(playerName);
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

        map.put(playerName, weightMap.get(teamName) + teamName);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            Scoreboard scoreboard = onlinePlayer.getScoreboard();
            scoreboard.getTeam(weightMap.get(teamName) + teamName).addEntry(playerName);
        }
    }

    public static void updateCache(Player player) {
        StatusPlayer statusplayer = StatusPlayer.wrap(player.getUniqueId());

        if (statusplayer != null) {
            String name = statusplayer.getStatus().getName();
            add(player.getName(), name);
        }
        map.remove(null);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String playerName = entry.getKey();
            String teamName = entry.getValue();

            Scoreboard scoreboard = player.getScoreboard();
            scoreboard.getTeam(teamName).addEntry(playerName);
        }
    }
}
