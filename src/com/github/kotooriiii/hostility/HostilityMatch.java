package com.github.kotooriiii.hostility;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static com.github.kotooriiii.data.Maps.*;

public class HostilityMatch {


    /**
     * The platform that players have to capture.
     */
    private final HostilityPlatform platform;

    /**
     * Win streak of the clan
     */
    private int winStreak;
    /**
     * The clan capturing the platform
     */
    private Clan capturingClan;
    /**
     * The player who SHOULD be in the clan
     */
    private Player capturingPlayer;
    /**
     * Old clan winner
     */
    private Clan lastClan;
    /**
     * Old win streak
     */
    private int lastWinStreak;

    /**
     * The current ticks counting down from the max ticks
     */
    private int currentTicks;
    /**
     * The max ticks. The beginning of the countdown
     */
    private final int maxTicks;
    /**
     * grace period for capturing point after win
     */
    private final int gracePeriod = 20 * 30;
    /**
     * The looping task counting down for the capturing player
     */
    private BukkitTask capturingCountdownTask;
    /**
     * The looping task looking for a player to capture
     */
    private BukkitTask checkForCapturerTask;

    public HostilityMatch(HostilityPlatform platform) {
        this.platform = platform;
        this.maxTicks = toTicks(8);//toTicks(0, 5);
        this.lastClan = null;
        this.lastWinStreak = 0;
        init();
    }

    private void init() {
        this.winStreak = 0;
        this.capturingClan = null;
        this.capturingPlayer = null;

        this.currentTicks = maxTicks;

        this.capturingCountdownTask = null;
        this.checkForCapturerTask = null;
    }

    public void startGame() {
        //If an active match is playing an instance of this match, stop. (Most likely, staff forcibly started it.)
        for (HostilityMatch match : activeHostilityGames) {
            if (match.getPlatform().getName().equalsIgnoreCase(this.getPlatform().getName())) {
                return;
            }
        }
        broadcast(ChatColor.GOLD + platform.getName() + " is now available for capture.", null);
        activeHostilityGames.add(this);
        checkForCapturer();
    }

    public void endGame(boolean isAbrupt) {
        if (isAbrupt)
            broadcast(ChatColor.GOLD + platform.getName() + " has forcibly been canceled.", null);

        if (capturingCountdownTask != null)
            capturingCountdownTask.cancel();
        if (checkForCapturerTask != null)
            checkForCapturerTask.cancel();
        activeHostilityGames.remove(this);

    }

    private void start() {

        this.capturingCountdownTask = new BukkitRunnable() {
            @Override
            public void run() {

                if (capturingPlayer == null || !capturingPlayer.isOnline() || !platform.contains(capturingPlayer) || !capturingClan.isInThisClan(capturingPlayer.getUniqueId())) {
                    broadcast(ChatColor.YELLOW + capturingClan.getName() + ChatColor.GOLD + " has lost control of " + platform.getName() + ". " + toMinutesSeconds(currentTicks), capturingClan);
                    capturingClan.broadcast(ChatColor.YELLOW + capturingPlayer.getName() + ChatColor.GOLD + " has lost control of " + platform.getName() + ". " + toMinutesSeconds(currentTicks));

                    init();
                    checkForCapturer();
                    this.cancel();
                    return;
                }

                if (currentTicks % 20 == 0) {
                    alert(); //also takes care of winstreak var
                    if (capturingCountdownTask == null) {
                        return;
                    }
                }


                currentTicks--;
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }


    private void checkForCapturer() {

        this.checkForCapturerTask = new BukkitRunnable() {

            final ArrayList<Player> clanlessPlayersCooldown = new ArrayList<>();

            @Override
            public void run() {

                if (platform.hasPlayers()) {
                    Clan uniqueClan = platform.getUniqueClan();
                    if (uniqueClan != null) {
                        Player[] players = platform.getUniqueClanPlayers(); //by definition, all of these players must be in the unique clan
                        int random = new Random().nextInt(players.length);
                        capturingClan = uniqueClan;
                        capturingPlayer = players[random];
                        if (lastClan != null && capturingClan.equals(lastClan))
                            winStreak = lastWinStreak;
                        start();
                        this.cancel();
                        return;
                    } else {
                        //contesting
                    }
                }

                for (final Player player : platform.getClanlessPlayers()) {
                    if (player.isOnline() && !clanlessPlayersCooldown.contains(player)) {
                        player.sendMessage(ERROR_COLOR + "You need to be in a clan in order to capture " + platform.getName() + ".");
                        clanlessPlayersCooldown.add(player);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(LostShardPlugin.plugin, new Runnable() {
                            @Override
                            public void run() {
                                clanlessPlayersCooldown.remove(player);
                            }
                        }, 5 * 20);
                    }
                }
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    private final int toTicks(int minutes) {
        return minutes * 60 * 20;
    }

    private final int toTicks(int minutes, int seconds) {
        return (minutes * 60 * 20) + (seconds * 20);
    }

    private void alert() {

        int[] timerMinAlert = new int[]
                {
                        8, 6, 4, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0

                };
        int[] timerSecAlert = new int[]
                {
                        0, 0, 0, 0, 0, 30, 15, 10, 5, 4, 3, 2, 1, 0

                };

        for (int i = 0; i < timerMinAlert.length; i++) {

            int tickAlert = toTicks(timerMinAlert[i], timerSecAlert[i]);

            if (tickAlert == this.currentTicks) {
                if (i == 0) {
                    this.broadcast(ChatColor.YELLOW + this.capturingClan.getName() + ChatColor.GOLD + " has begun capturing " + platform.getName() + " for their " + tryingToPlace(winStreak) + " time. " + toMinutesSeconds(this.currentTicks), this.capturingClan);
                    capturingClan.broadcast(ChatColor.YELLOW + this.capturingPlayer.getName() + ChatColor.GOLD + " is capturing " + platform.getName() + " for your clan. " + toMinutesSeconds(this.currentTicks));
                    capturingClan.broadcast(ChatColor.YELLOW + this.capturingPlayer.getName() + ChatColor.GOLD + " has begun capturing " + platform.getName() + " for your clan for the " + tryingToPlace(winStreak) + " time. " + toMinutesSeconds(this.currentTicks));


                } else if (i > 0 && i < timerMinAlert.length - 1) {
                    this.broadcast(ChatColor.YELLOW + this.capturingClan.getName() + ChatColor.GOLD + " is currently capturing " + platform.getName() + " for their " + tryingToPlace(winStreak) + " time. " + toMinutesSeconds(this.currentTicks), this.capturingClan);
                    capturingClan.broadcast(ChatColor.YELLOW + this.capturingPlayer.getName() + ChatColor.GOLD + " is capturing " + platform.getName() + " for your clan for the " + tryingToPlace(winStreak) + " time. " + toMinutesSeconds(this.currentTicks));
                } else if (i == timerMinAlert.length - 1) {
                    winStreak++;
                    if (winStreak == 3) {
                        this.broadcast(ChatColor.YELLOW + this.capturingClan.getName() + ChatColor.GOLD + " has fully captured " + platform.getName() + ".", this.capturingClan);
                        capturingClan.broadcast("Your clan has fully captured " + platform.getName() + ".");
                        capturingClan.setHostilityBuff(true);
                        capturingClan.setHostilityWins(capturingClan.getHostilityWins() + 1);
                        capturingClan.saveFile();


                        //Universal LORE
                        List<String> itemLore = new ArrayList<>();
                        itemLore.add(ChatColor.GOLD + platform.getName() + " Prize");

                        //GOLD ITEM STACK
                        ItemStack goldItemStack = new ItemStack(Material.GOLD_INGOT, 50);
                        ItemMeta goldItemMeta = goldItemStack.getItemMeta();
                        goldItemMeta.setLore(itemLore);
                        goldItemStack.setItemMeta(goldItemMeta);

                        //DRAGON ITEM STACK
                        ItemStack dragonItemStack = new ItemStack(Material.DRAGON_EGG, 1);
                        ItemMeta dragonItemMeta = dragonItemStack.getItemMeta();
                        dragonItemMeta.setLore(itemLore);
                        dragonItemStack.setItemMeta(dragonItemMeta);


                        for (UUID uuid : capturingClan.getAllUUIDS()) {
                            Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();

                            if (player.isOnline()) {
                                HashMap<Integer, ItemStack> unstoredItems;
                                if (player.equals(capturingPlayer)) {
                                    unstoredItems = player.getInventory().addItem(goldItemStack, dragonItemStack);

                                } else {
                                    unstoredItems = player.getInventory().addItem(goldItemStack);

                                }

                                player.sendMessage(STANDARD_COLOR + "Your clan captured " + platform.getName() + ". You have been awarded for your brave efforts!");
                                player.sendMessage(STANDARD_COLOR + "Your clan has gained the hostility buff!");

                                player.sendMessage(STANDARD_COLOR + "Bonuses: ");
                                player.sendMessage(STANDARD_COLOR + "- 50 Gold");
                                if (player.equals(capturingPlayer)) {
                                    player.sendMessage(STANDARD_COLOR + "Personal Bonus:");
                                    player.sendMessage(STANDARD_COLOR + "- 1 Dragon Egg");
                                }

                                if (unstoredItems.keySet().size() > 0)
                                    player.sendMessage(STANDARD_COLOR + "Your clan captured " + platform.getName() + " but some of the item(s) rewarded to you were not able to fit in your inventory. The item(s) have been dropped at your location.");

                                for (Integer integer : unstoredItems.keySet()) {
                                    ItemStack unstoredItemStack = unstoredItems.get(integer);
                                    Bukkit.getWorld("world").dropItem(player.getLocation(), unstoredItemStack);
                                }
                            }

                            final Stat stat = Stat.getStatMap().get(uuid);
                            stat.setMaxStamina(125);
                            stat.setMana(125);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    stat.setMaxStamina(100);
                                    stat.setMaxMana(100);

                                    if (stat.getStamina() > stat.getMaxStamina())
                                        stat.setStamina(stat.getMaxStamina());

                                    if (stat.getMana() > stat.getMaxMana())
                                        stat.setMana(stat.getMana());
                                }
                            }.runTaskLater(LostShardPlugin.plugin, 20 * 60 * 60 * 24);

                        }

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                capturingClan.setHostilityBuff(false);
                            }
                        }.runTaskLater(LostShardPlugin.plugin, 20 * 60 * 60 * 24);

                        endGame(false);


                        //win host
                    } else {
                        this.broadcast(ChatColor.YELLOW + this.capturingClan.getName() + ChatColor.GOLD + " has successfully captured " + platform.getName() + " for their " + tryingToPlace(winStreak - 1) + " time. " + platform.getName() + " will be active for capture in " + toMinutesSeconds(gracePeriod), this.capturingClan);
                        capturingClan.broadcast(ChatColor.YELLOW + this.capturingPlayer.getName() + ChatColor.GOLD + " has successfully captured " + platform.getName() + " for your clan for the " + tryingToPlace(winStreak - 1) + " time. " + platform.getName() + " will be active for capture in " + toMinutesSeconds(gracePeriod));
                        lastClan = capturingClan;
                        lastWinStreak = winStreak;
                        capturingCountdownTask.cancel();
                        checkForCapturerTask.cancel();
                        init();
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(LostShardPlugin.plugin, new Runnable() {
                            @Override
                            public void run() {
                                broadcast(ChatColor.GOLD + platform.getName() + " is now active for capture.", null);

                                //Time is already updated on the #checkForCapture method
                                checkForCapturer();

                            }
                        }, (long) gracePeriod);
                    }

                    HelperMethods.playSound(Bukkit.getOnlinePlayers().toArray(new Player[0]), Sound.ENTITY_LIGHTNING_BOLT_THUNDER);

                }
            }
        }

    }

    private String tryingToPlace(int winStreak) {
        switch (winStreak) {
            case 0:
                return "first";
            case 1:
                return "second";
            case 2:
                return "third";
        }
        return "null";
    }


    private String toMinutesSeconds(int currentTicks) {
        int ticksToSecond = (int) (currentTicks / 20);

        int minutes = ticksToSecond / 60;
        int seconds = ticksToSecond % 60;

        char[] rawMinutes = String.valueOf(minutes).toCharArray();
        char[] rawSeconds = String.valueOf(seconds).toCharArray();

        char[] desiredMinutes = new char[1];
        char[] desiredSeconds = new char[2];

        //get string mins
        int desMinsLen = desiredMinutes.length;
        int rawMinsLen = rawMinutes.length;

        int skippedMinsNum = desMinsLen - rawMinsLen;

        for (int i = 0; i < skippedMinsNum; i++) {
            desiredMinutes[i] = '0';
        }

        for (int i = skippedMinsNum; i < desMinsLen; i++) {
            desiredMinutes[i] = rawMinutes[i - skippedMinsNum];
        }

        //get string secs
        int desSecsLen = desiredSeconds.length;
        int rawSecsLen = rawSeconds.length;

        int skippedSecsNum = desSecsLen - rawSecsLen;

        for (int i = 0; i < skippedSecsNum; i++) {
            desiredSeconds[i] = '0';
        }

        for (int i = skippedSecsNum; i < desSecsLen; i++) {
            desiredSeconds[i] = rawSeconds[i - skippedSecsNum];
        }

        return "[" + String.valueOf(desiredMinutes) + ":" + String.valueOf(desiredSeconds) + "]";
    }

    public void broadcast(String message, Clan clan) {
        if (clan == null) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                player.sendMessage(message);

            }
            return;
        }
        UUID[] clanUUIDS = clan.getOnlineUUIDS();
        Player[] clanPlayers = new Player[clanUUIDS.length];
        for (int i = 0; i < clanUUIDS.length; i++) {
            clanPlayers[i] = Bukkit.getOfflinePlayer(clanUUIDS[i]).getPlayer();
        }

        all:
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            clan:
            for (Player clanPlayer : clanPlayers) {
                if (clanPlayer.getUniqueId().equals(player.getUniqueId()))
                    continue all;
                break clan;
            }
            player.sendMessage(message);
        }

    }


    public HostilityPlatform getPlatform() {
        return this.platform;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public Clan getCapturingClan() {
        return capturingClan;
    }

    public Player getCapturingPlayer() {
        return capturingPlayer;
    }
}
