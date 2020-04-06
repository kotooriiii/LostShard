package com.github.kotooriiii;

import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.channels.ChannelManager;
import com.github.kotooriiii.channels.ChatChannelListener;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.commands.*;
import com.github.kotooriiii.crafting.CraftingRecipes;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.npc.ShardBanker;
import com.github.kotooriiii.npc.ShardGuard;
import com.github.kotooriiii.hostility.HostilityTimeCreatorListener;
import com.github.kotooriiii.instaeat.InstaEatListener;
import com.github.kotooriiii.listeners.*;
import com.github.kotooriiii.plots.*;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.skills.SkillUpdateListener;
import com.github.kotooriiii.skills.commands.CampCommand;
import com.github.kotooriiii.skills.commands.PetsCommand;
import com.github.kotooriiii.skills.commands.TrackCommand;
import com.github.kotooriiii.skills.listeners.*;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.stats.StatJoinListener;
import com.github.kotooriiii.stats.StatRegenRunner;
import com.github.kotooriiii.status.*;
import com.github.kotooriiii.sorcery.wands.Glow;
import com.github.kotooriiii.sorcery.wands.MedAndRestCancelListener;
import com.github.kotooriiii.sorcery.wands.WandListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.logging.Logger;

import static com.github.kotooriiii.data.Maps.*;

public class LostShardPlugin extends JavaPlugin {

    public static JavaPlugin plugin;
    public static Logger logger;
    public static PluginDescriptionFile pluginDescriptionFile;

    public static LuckPerms luckPerms;

    public static FileConfiguration config;

    private static ChannelManager channelManager;
    private static Scoreboard scoreboard;


    @Override
    public void onEnable() {



        //Console logger, plugin, and description file are all ready for public use
        logger = Logger.getLogger("Minecraft");
        plugin = this;
        pluginDescriptionFile = this.getDescription();
        luckPerms = loadLuckPerms();
        FileManager.init();
        channelManager = new ChannelManager();

        newDayScheduler();

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
        registerStaff();

        //All was successfully enabled
        logger.info(pluginDescriptionFile.getName() + " has been successfully enabled on the server.");

        loadConfig();
        ShardScoreboardManager.updateScoreboard();
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
        Stat.getStatMap().clear();
        Plot.getPlayerPlots().clear();



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

        for (Stat stat : Stat.getStatMap().values()) {
            FileManager.write(stat);
        }

        for (Plot plot : Plot.getPlayerPlots().values()) {
            FileManager.write(plot);
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
        getCommand("plot").setExecutor(new PlotCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("addtitle").setExecutor(new AddTitleCommand());
        getCommand("hud").setExecutor(new HUDCommand());
        getCommand("mark").setExecutor(new MarkCommand());
        getCommand("cast").setExecutor(new CastCommand());
        getCommand("pets").setExecutor(new PetsCommand());
        getCommand("camp").setExecutor(new CampCommand());
        getCommand("track").setExecutor(new TrackCommand());


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
        pm.registerEvents(new PlayerEnterExitPlotRedirectListener(), this);
        pm.registerEvents(new BlockChangePlotListener(), this);
        pm.registerEvents(new EntityInteractPlotListener(), this);
        pm.registerEvents(new PlayerStatusRespawnListener(), this);
        pm.registerEvents(new PlotStaffCreateListener(), this);
        pm.registerEvents(new PlayerSpawnMoveListener(), this);
        pm.registerEvents(new StaffUpdateListener(getLuckPerms()), this);
        pm.registerEvents(new SkillUpdateListener(), this);
        pm.registerEvents(new CastListener(), this);

        pm.registerEvents(new StunListener(), this);

        pm.registerEvents(new ArcheryListener(), this);
        pm.registerEvents(new BlacksmithyListener(), this);
        pm.registerEvents(new BrawlingListener(), this);
        pm.registerEvents(new FishingListener(), this);
        pm.registerEvents(new LumberjackingListener(), this);
        pm.registerEvents(new MiningListener(), this);
        pm.registerEvents(new SorceryListener(), this);
        pm.registerEvents(new SurvivalismListener(), this);
        pm.registerEvents(new SwordsmanshipListener(), this);
        pm.registerEvents(new TamingListener(), this);


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

    public void registerCorrupts() {

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

    public void registerStaff() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
            StaffType type = StaffType.matchStaffType(user.getPrimaryGroup());
            if (type == null) return;

            Staff staff = new Staff(user.getUniqueId(), type);

            ShardScoreboardManager.add(player, staff.getType().getName());
        }
    }

    public void newDayScheduler() {

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        if (now.compareTo(nextRun) >= 0)
            nextRun = nextRun.plusDays(1);

        Duration duration = Duration.between(now, nextRun);
        long initalDelay = duration.getSeconds() * 20;

        new BukkitRunnable() {
            @Override
            public void run() {

                //On every day, do all this code. 12:00am EST

                //Set murder count less than one
                for (StatusPlayer statusPlayer : StatusPlayer.getPlayerStatus().values()) {
                    if (statusPlayer.getKills() > 0)
                        statusPlayer.setKills(statusPlayer.getKills() - 1);
                }

                //Taxes every day
                for (Plot plot : Plot.getPlayerPlots().values()) {

                    if (!plot.rent()) {
                        OfflinePlayer owner = Bukkit.getOfflinePlayer(plot.getOwnerUUID());
                        if (owner.isOnline()) {
                            if (plot.getRadius() == 1)
                                owner.getPlayer().sendMessage(STANDARD_COLOR + "You did not pay the rent today. You have one day left before your plot is removed.");
                            else
                                owner.getPlayer().sendMessage(STANDARD_COLOR + "You did not pay the rent today. Your plot has shrunk by one block radius.");

                        }
                    }
                }

                //Save all skills
                for(SkillPlayer skillPlayer : SkillPlayer.getPlayerSkills().values())
                {
                    skillPlayer.save();
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        newDayScheduler();
                    }
                }.runTaskLater(LostShardPlugin.plugin, 20);
            }
        }.runTaskLater(this.plugin, initalDelay);

    }

//    public WorldGuardPlugin getWorldGuard() {
//        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
//        // WorldGuard may not be loaded
//        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
//            return null; // Maybe you want throw an exception instead
//        }
//        return (WorldGuardPlugin) plugin;
//    }
//
//    public ICombatLogX getCombatLogX() {
//        Plugin plugin = getServer().getPluginManager().getPlugin("CombatLogX");
//        // WorldGuard may not be loaded
//        if (plugin == null || !(plugin instanceof ICombatLogX)) {
//            return null; // Maybe you want throw an exception instead
//        }
//        return (ICombatLogX) plugin;
//    }

    private LuckPerms loadLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            System.out.println("\n\n\n\n\n\nThe plugin LuckPerms was loaded.");
            return provider.getProvider();
        } else {
            System.out.println("\n\n\n\n\n\nThe plugin LuckPerms was NOT loaded.");
            return null;
        }


    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
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

}
