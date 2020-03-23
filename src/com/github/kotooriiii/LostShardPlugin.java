package com.github.kotooriiii;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.channels.ChannelManager;
import com.github.kotooriiii.channels.ChatChannelListener;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.commands.*;
import com.github.kotooriiii.crafting.CraftingRecipes;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.guards.ShardBanker;
import com.github.kotooriiii.guards.ShardGuard;
import com.github.kotooriiii.hostility.HostilityTimeCreatorListener;
import com.github.kotooriiii.instaeat.InstaEatListener;
import com.github.kotooriiii.listeners.*;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.stats.StatJoinListener;
import com.github.kotooriiii.stats.StatRegenRunner;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.status.StatusUpdateListener;
import com.github.kotooriiii.wands.Glow;
import com.github.kotooriiii.wands.MedAndRestCancelListener;
import com.github.kotooriiii.wands.WandListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Logger;

import static com.github.kotooriiii.data.Maps.*;

public class LostShardPlugin extends JavaPlugin {

    public static JavaPlugin plugin;
    public static Logger logger;
    public static PluginDescriptionFile pluginDescriptionFile;

    public static WorldGuardPlugin worldGuardPlugin;
    public static ICombatLogX combatLogXPlugin;

    public static FileConfiguration config;

    private static ChannelManager channelManager;
    private static Scoreboard scoreboard;


    @Override
    public void onEnable() {

        //Console logger, plugin, and description file are all ready for public use
        logger = Logger.getLogger("Minecraft");
        plugin = this;
        worldGuardPlugin = getWorldGuard();
        combatLogXPlugin = getCombatLogX();
        pluginDescriptionFile = this.getDescription();
        registerScoreboard();
        FileManager.init();
        channelManager = new ChannelManager();

        StatusUpdateListener.listenAtNewDay();
        StatRegenRunner.regen();



        //Registers the com.github.kotooriiii.commands and com.github.kotooriiii.events from this plugin
        registerCommands();
        registerEvents();

        //Init custom recipes
        CraftingRecipes.initRecipes();

        //Register custom enchantment
        registerGlow();
        //Register for crash-related incidents
        registerBuff();
        registerCorrupts();



        //All was successfully enabled
        logger.info(pluginDescriptionFile.getName() + " has been successfully enabled on the server.");

        PluginManager manager = Bukkit.getServer().getPluginManager();
        loadConfig();
    }

    @Override
    public void onDisable() {

        saveData();

        for (int i = 0; i < ShardGuard.getActiveShardGuards().size(); i++) {
            ShardGuard.getActiveShardGuards().get(i).forceDestroy();
        }
        ShardGuard.getActiveShardGuards().clear();

        for (int i = 0; i < ShardBanker.getActiveShardBankers().size(); i++) {
            ShardBanker.getActiveShardBankers().get(i).forceDestroy();
        }

        Stat.getStatMap().clear();

        ShardBanker.getActiveShardBankers().clear();

        Bank.getBanks().clear();

        StatusPlayer.getPlayerStatus().clear();


        //SKILLS


        getServer().getScheduler().cancelTasks(this);


        logger.info(pluginDescriptionFile.getName() + " has been successfully disabled on the server.");
        plugin = null;
        logger = null;
        pluginDescriptionFile = null;

    }

    private void saveData() {

        for (Clan clan : clans) {
            FileManager.write(clan);
        }

        for(Stat stat : Stat.getStatMap().values())
        {
            FileManager.write(stat);
        }
    }


    public void registerCommands() {
        getCommand("clan").setExecutor(new ClanCommand());
        getCommand("ff").setExecutor(new FriendlyFireCommand());
        getCommand("hostility").setExecutor(new HostilityCommand());
        getCommand("guard").setExecutor(new GuardCommand());
        getCommand("chest").setExecutor(new ChestCommand());
        getCommand("bank").setExecutor(new BankCommand());
        getCommand("deposit").setExecutor(new DepositCommand());
        getCommand("withdraw").setExecutor(new WithdrawCommand());
        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("local").setExecutor(new LocalCommand());
        getCommand("global").setExecutor(new GlobalCommand());
        getCommand("meditate").setExecutor(new MeditateCommand());
        getCommand("rest").setExecutor(new RestCommand());
        getCommand("stat").setExecutor(new StatCommand());
        getCommand("bind").setExecutor(new BindCommand());
        getCommand("buy").setExecutor(new BuyCommand());
        getCommand("sell").setExecutor(new SellCommand());
        getCommand("price").setExecutor(new PriceCommand());
        getCommand("msg").setExecutor(new MsgCommand());


    }

    public void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ClanCreatorListener(), this);
        pm.registerEvents(new PlayerLeaveListener(), this);
        pm.registerEvents(new PlayerFriendlyFireHitListener(), this);
        pm.registerEvents(new HostilityCreateListener(), this);
        pm.registerEvents(new GuardChatMessageListener(), this);
        pm.registerEvents(new NPCInteractRedirectListener(), this);
        pm.registerEvents(new UpdatePacketOnJoinListener(), this);
        pm.registerEvents(new PlayerBankUpdateInventory(), this);
        pm.registerEvents(new ChatChannelListener(), this);
        pm.registerEvents(new StatusUpdateListener(), this);
        pm.registerEvents(new StatJoinListener(), this);
        pm.registerEvents(new MedAndRestCancelListener(), this);
        pm.registerEvents(new WandListener(), this);
        pm.registerEvents(new HostilityTimeCreatorListener(), this);
        pm.registerEvents(new InstaEatListener(), this);
        pm.registerEvents(new GoldArmorListener(), this);



    }

    public void registerScoreboard() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Team worthy = scoreboard.registerNewTeam(Status.WORTHY.getName());
        worthy.setColor(Status.WORTHY.getChatColor());
        Team corrupt = scoreboard.registerNewTeam(Status.CORRUPT.getName());
        corrupt.setColor(Status.CORRUPT.getChatColor());
        Team exiled = scoreboard.registerNewTeam(Status.EXILED.getName());
        exiled.setColor(Status.EXILED.getChatColor());

        this.scoreboard = scoreboard;
    }

    public void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Glow glow = new Glow(new NamespacedKey(LostShardPlugin.plugin, "GlowCustomEnchant"));
            Enchantment.registerEnchantment(glow);
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerBuff() {
        for (Clan clan : clans) {
            if (clan.hasHostilityBuff()) {
                for (UUID uuid : clan.getAllUUIDS()) {
                    final Stat stat = Stat.getStatMap().get(uuid);
                    stat.setMaxStamina(125);
                    stat.setMaxMana(125);

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
            }
        }
    }

    public void registerCorrupts()
    {

        for (StatusPlayer statusPlayer : StatusPlayer.getCorrupts()) {
            UUID uuid = statusPlayer.getPlayerUUID();

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.isOnline())
                offlinePlayer.getPlayer().sendMessage(STANDARD_COLOR + "The server crashed while you were Corrupt. The timer has reset to 5 minutes.");

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (isCancelled())
                        return;
                    if (StatusUpdateListener.getPlayersCorrupt().get(uuid) == null)
                        return;
                    StatusPlayer.wrap(uuid).setStatus(Status.WORTHY);
                    StatusUpdateListener.getPlayersCorrupt().remove(uuid);
                }
            }.runTaskLater(LostShardPlugin.plugin, 20 * 60 * 5);

            StatusUpdateListener.getPlayersCorrupt().put(uuid, task);
        }
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }
        return (WorldGuardPlugin) plugin;
    }

    public ICombatLogX getCombatLogX() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CombatLogX");
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof ICombatLogX)) {
            return null; // Maybe you want throw an exception instead
        }
        return (ICombatLogX) plugin;
    }

    void loadConfig() {
        // Get the config
        config = this.getConfig();

        // Save all the settings to the config
        config.options().copyDefaults(true);
        this.saveConfig();
    }

    public static ChannelManager getChannelManager() {
        return channelManager;
    }

    public static Scoreboard getScoreboard() {
        return scoreboard;
    }


}
