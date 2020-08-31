package com.github.kotooriiii;

import com.github.kotooriiii.bank.*;
import com.github.kotooriiii.bank.commands.*;
import com.github.kotooriiii.bannedplayer.BanManager;
import com.github.kotooriiii.bannedplayer.commands.BanCommand;
import com.github.kotooriiii.bannedplayer.commands.UnbanCommand;
import com.github.kotooriiii.bannedplayer.listeners.BannedJoinListener;
import com.github.kotooriiii.channels.IgnoreManager;
import com.github.kotooriiii.channels.IgnorePlayer;
import com.github.kotooriiii.channels.commands.*;
import com.github.kotooriiii.clans.listeners.ClanCreatorListener;
import com.github.kotooriiii.clans.ClanManager;
import com.github.kotooriiii.clans.listeners.PlayerJoinCheckClanIfBuffListener;
import com.github.kotooriiii.clans.commands.ClanCommand;
import com.github.kotooriiii.clans.commands.FriendlyFireCommand;
import com.github.kotooriiii.combatlog.CombatLogListener;
import com.github.kotooriiii.combatlog.CombatLogManager;
import com.github.kotooriiii.discord.client.DC4JBot;
import com.github.kotooriiii.events.PlayerStrengthPotionEffectEvent;
import com.github.kotooriiii.hostility.commands.HostilityCommand;
import com.github.kotooriiii.hostility.listeners.HostilityCreateListener;
import com.github.kotooriiii.hostility.listeners.HostilityNamePreprocessListener;
import com.github.kotooriiii.match.MatchCheatingListener;
import com.github.kotooriiii.match.MatchCreatorListener;
import com.github.kotooriiii.match.MatchDefeatListener;
import com.github.kotooriiii.match.banmatch.*;
import com.github.kotooriiii.channels.ChannelManager;
import com.github.kotooriiii.channels.listeners.ChatChannelListener;
import com.github.kotooriiii.match.moneymatch.MoneymatchCommand;
import com.github.kotooriiii.muted.listeners.MuteListener;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.commands.*;
import com.github.kotooriiii.crafting.CraftingRecipes;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.hostility.listeners.HostilityTimeCreatorListener;
import com.github.kotooriiii.instaeat.InstaEatListener;
import com.github.kotooriiii.listeners.*;
import com.github.kotooriiii.npc.type.banker.BankerTrait;
import com.github.kotooriiii.npc.type.guard.GuardTrait;
import com.github.kotooriiii.npc.reworked.PacketListener;
import com.github.kotooriiii.plots.*;
import com.github.kotooriiii.plots.commands.BuildCommand;
import com.github.kotooriiii.plots.listeners.*;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.register_system.GatheringManager;
import com.github.kotooriiii.register_system.JoinCommand;
import com.github.kotooriiii.register_system.LeaveCommand;
import com.github.kotooriiii.register_system.ffa.FFACommand;
import com.github.kotooriiii.register_system.ffa.FFAListener;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import com.github.kotooriiii.skills.SkillManager;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.plots.listeners.MobSpawnerCancelListener;
import com.github.kotooriiii.plots.listeners.SignChangeListener;
import com.github.kotooriiii.skills.commands.CampCommand;
import com.github.kotooriiii.skills.commands.PetsCommand;
import com.github.kotooriiii.skills.commands.TrackCommand;
import com.github.kotooriiii.skills.commands.blacksmithy.*;
import com.github.kotooriiii.skills.skill_listeners.*;
import com.github.kotooriiii.sorcery.GateManager;
import com.github.kotooriiii.sorcery.listeners.*;
import com.github.kotooriiii.sorcery.scrolls.ScrollListener;
import com.github.kotooriiii.sorcery.spells.type.*;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.stats.StatRegenRunner;
import com.github.kotooriiii.status.*;
import com.github.kotooriiii.sorcery.wands.Glow;
import com.github.kotooriiii.sorcery.wands.WandListener;
import com.github.kotooriiii.status.shrine.AtoneCommand;
import com.github.kotooriiii.status.shrine.ShrineManager;
import com.github.kotooriiii.tutorial.newt.TutorialManager;
import com.github.kotooriiii.weather.WeatherManager;
import com.github.kotooriiii.weather.WeatherManagerListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.kotooriiii.data.Maps.*;

public class LostShardPlugin extends JavaPlugin {

    public static JavaPlugin plugin;
    public static Logger logger;
    public static PluginDescriptionFile pluginDescriptionFile;
    public static boolean isTutorial;

    public static LuckPerms luckPerms;

    public static FileConfiguration config;

    private static ChannelManager channelManager;
    private static DC4JBot dc4JBot;
    private static CombatLogManager combatLogManager;
    private static PlotManager plotManager;
    private static WeatherManager weatherManager;
    private static SaleManager saleManager;
    private static BankManager bankManager;
    private static BanManager banManager;
    private static ClanManager clanManager;
    private static GateManager gateManager;
    private static SkillManager skillManager;
    private static ShrineManager shrineManager;
    private static GatheringManager gatheringManager;
    private static IgnoreManager ignoreManager;
    private static TutorialManager tutorialManager;

    private final static FFACommand FFA_COMMAND = new FFACommand();

    private static int gameTicks = 0;

    private static boolean isResetting = false;

    public static void setReset(boolean tempIsReset) {
        isResetting = tempIsReset;
    }

    public static boolean getReset() {
        return isResetting;
    }


    public static class LSBorder {
        public int cX;
        public int cZ;
        public int radiusX;
        public int radiusZ;

        public LSBorder(int cX, int cZ, int radiusX, int radiusZ) {
            this.cX = cX;
            this.cZ = cZ;
            this.radiusX = radiusX;
            this.radiusZ = radiusZ;
        }

        public int getX() {
            return cX;
        }

        public int getZ() {
            return cZ;
        }

        public int getRadiusX() {
            return radiusX;
        }

        public int getRadiusZ() {
            return radiusZ;
        }
    }

    private static LSBorder fetchBorder(String worldName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldBorder");
        if (plugin == null)
            return null;
        File wbFolder = plugin.getDataFolder();
        if (!wbFolder.exists())
            return null;
        File wbConfig = new File(wbFolder, "config.yml");
        if (!wbConfig.exists())
            return null;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(wbConfig);

        int cX = (int) yaml.getDouble("worlds." + worldName + ".x");
        int cZ = (int) yaml.getDouble("worlds." + worldName + ".z");
        int rX = yaml.getInt("worlds." + worldName + ".radiusX");
        int rZ = yaml.getInt("worlds." + worldName + ".radiusZ");


        if (rX == 0 && rZ == 0)
            return null;

        return new LSBorder(cX, cZ, rX, rZ);
    }

    private boolean checkDependency() {

        if (getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return false;
        }

        registerTrait();
        return true;
    }

    public void registerTrait() {
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(GuardTrait.class).withName("GuardTrait"));
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(BankerTrait.class).withName("BankerTrait"));

    }

    @Override
    public void onEnable() {

        //Console logger, plugin, and description file are all ready for public use
        if (!checkDependency())
            return;

        isTutorial = false;

        logger = Logger.getLogger("Minecraft");
        plugin = this;
        pluginDescriptionFile = this.getDescription();

        //Register dependency
        luckPerms = loadLuckPerms();

        //Register managers
        ShardScoreboardManager.initDefault();
        plotManager = new PlotManager();
        combatLogManager = new CombatLogManager();
        channelManager = new ChannelManager();
        weatherManager = new WeatherManager();
        weatherManager.setWeatherFrequency(new WeatherManager.WeatherFrequency(8));
        saleManager = new SaleManager();
        bankManager = new BankManager();
        banManager = new BanManager();
        clanManager = new ClanManager();
        gateManager = new GateManager();
        skillManager = new SkillManager();
        shrineManager = new ShrineManager();
        gatheringManager = new GatheringManager();
        ignoreManager = new IgnoreManager();

        if (isTutorial()) {
            tutorialManager = new TutorialManager();
            tutorialManager.getChapterManager().registerDefault();
        }

        //Read files (some onto the managers)
        FileManager.init();

        //Read every day
        newDayScheduler();

        //Run loop to regen stats
        StatRegenRunner.regen();

        //Registers the com.github.kotooriiii.commands and com.github.kotooriiii.events from this plugin
        registerCommands();
        registerEvents();
        // registerDiscord();

        //Init custom recipes
        CraftingRecipes.initRecipes();


        //Register custom enchantment
        registerGlow();
        //Register for crash-related incidents
        registerBuff();
        registerCorrupts();
        registerStaff();

        PacketListener packetListener = new PacketListener();
        packetListener.init();

        //All was successfully enabled
        logger.info(pluginDescriptionFile.getName() + " has been successfully enabled on the server.");

        loadConfig();
        ShardScoreboardManager.updateScoreboard();
    }


    @Override
    public void onDisable() {

        //  LostShardPlugin.getDiscord().getClient().logout().block();

        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryView inventoryView = player.getOpenInventory();
            if (inventoryView == null)
                continue;
            InventoryType inventoryType = inventoryView.getType();
            if (inventoryType == null)
                continue;
            if (!inventoryType.equals(InventoryType.CHEST))
                continue;
            if (!inventoryView.getTitle().equalsIgnoreCase(Bank.NAME))
                continue;
            Bank bank = LostShardPlugin.getBankManager().wrap(player.getUniqueId());
            InventoryView view = player.getOpenInventory();
            if (view == null)
                continue;
            if (view.getTopInventory() == null)
                continue;
            bank.setInventory(view.getTopInventory());
            inventoryView.close();
        }

        saveData();

        //SKILLS


        getServer().getScheduler().cancelTasks(this);


        logger.info(pluginDescriptionFile.getName() + " has been successfully disabled on the server.");


        if (isResetting) {
            try {
                for (File file : LostShardPlugin.plugin.getDataFolder().listFiles()) {
                    if (file.isDirectory()) {

                        if (!file.getName().equals("stats")) {
                            FileUtils.deleteDirectory(file);
                            continue;
                        } else {
                            for (File statFile : file.listFiles()) {
                                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(statFile);
                                if (yaml.getBoolean("isGold")) {
                                    yaml.set("Stamina", 100.0f);
                                    yaml.set("Mana", 100.0f);
                                    yaml.set("MaxMana", 100.0f);
                                    yaml.set("MaxStamina", 100.0f);
                                    yaml.set("Private", false);
                                    yaml.set("Spawn", null);
                                    yaml.save(statFile);
                                    continue;
                                }
                                statFile.delete();
                            }
                        }
                    }

                    if (!file.isDirectory())
                        file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        plugin = null;
        logger = null;
        pluginDescriptionFile = null;
    }

    private void saveData() {
        for (Bank bank : LostShardPlugin.getBankManager().getBanks().values()) {
            LostShardPlugin.getBankManager().saveBank(bank);
        }

        for (Clan clan : LostShardPlugin.getClanManager().getAllClans()) {
            LostShardPlugin.getClanManager().saveClan(clan);
        }

        for (Stat stat : Stat.getStatMap().values()) {
            FileManager.write(stat);
        }

        for (Plot plot : getPlotManager().getAllPlots()) {
            FileManager.write(plot);
        }

        //Save all skills
        for (SkillPlayer skillPlayer : LostShardPlugin.getSkillManager().getSkillPlayers()) {
            getSkillManager().saveSkillPlayer(skillPlayer);
        }

        for (IgnorePlayer ignorePlayer : LostShardPlugin.getIgnoreManager().getIgnorePlayers())
            getIgnoreManager().save(ignorePlayer);

        SignChangeListener.save();
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
        getCommand("shout").setExecutor(new ShoutCommand());
        getCommand("whisper").setExecutor(new WhisperCommand());

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
        getCommand("addtitlegold").setExecutor(new AddTitleGoldCommand());
        getCommand("addrank").setExecutor(new AddRankCommand());

        getCommand("hud").setExecutor(new HUDCommand());
        getCommand("mark").setExecutor(new MarkCommand());
        getCommand("cast").setExecutor(new CastCommand());
        getCommand("pets").setExecutor(new PetsCommand());
        getCommand("camp").setExecutor(new CampCommand());
        getCommand("track").setExecutor(new TrackCommand());
        getCommand("mute").setExecutor(new MuteCommand());

        getCommand("skills").setExecutor(new SkillCommand());
        getCommand("repair").setExecutor(new RepairCommand());
        getCommand("smelt").setExecutor(new SmeltCommand());
        getCommand("harden").setExecutor(new HardenCommand());
        getCommand("enhance").setExecutor(new EnhanceCommand());
        getCommand("sharpen").setExecutor(new SharpenCommand());
        getCommand("power").setExecutor(new PowerCommand());
        getCommand("ignite").setExecutor(new IgniteCommand());

        getCommand("banmatch").setExecutor(new BanmatchCommand());
        getCommand("moneymatch").setExecutor(new MoneymatchCommand());

        getCommand("heal").setExecutor(new HealCommand());

        getCommand("ban").setExecutor(new BanCommand());
        getCommand("unban").setExecutor(new UnbanCommand());

        getCommand("invsee").setExecutor(new InvseeCommand());
        getCommand("murdercount").setExecutor(new MurdercountCommand());

        getCommand("donate").setExecutor(new DonateCommand());
        getCommand("reply").setExecutor(new ReplyCommand());

        getCommand("suicide").setExecutor(new SuicideCommand());

        getCommand("whois").setExecutor(new WhoisCommand());

        getCommand("lostshard").setExecutor(new LostShardCommand());
        getCommand("blacksmithy").setExecutor(new BlacksmithyCommand());

        getCommand("clearchat").setExecutor(new ClearChatCommand());
        getCommand("clearchatall").setExecutor(new ClearChatAllCommand());
        getCommand("adminchat").setExecutor(new AdminChatCommand());
        getCommand("staff").setExecutor(new StaffCommand());

        getCommand("book").setExecutor(new BookCommand());
        getCommand("wiki").setExecutor(new WikiCommand());
        getCommand("youtube").setExecutor(new YoutubeCommand());
        getCommand("doc").setExecutor(new DocCommand());

        getCommand("private").setExecutor(new PrivateCommand());
        getCommand("public").setExecutor(new PublicCommand());

        getCommand("notification").setExecutor(new NotificationCommand());
        getCommand("removeoldentities").setExecutor(new RemoveOldEntitiesCommand());
        getCommand("playtime").setExecutor(new PlaytimeCommand());

        getCommand("scroll").setExecutor(new ScrollCommand());
        getCommand("atone").setExecutor(new AtoneCommand());
        getCommand("ffa").setExecutor(FFA_COMMAND);
        getCommand("join").setExecutor(new JoinCommand());
        getCommand("leave").setExecutor(new LeaveCommand());

        getCommand("build").setExecutor(new BuildCommand());
        getCommand("ignore").setExecutor(new IgnoreCommand());


        //todo to use later -->
        //getCommand("opt").setExecutor(new LinkListener());


    }

    public void registerDiscord() {
        dc4JBot = new DC4JBot();
    }

    public void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(new InitializerListener(), this);

        pm.registerEvents(new EntityDeathTrackerListener(), this);

        pm.registerEvents(new ClanCreatorListener(), this);
        pm.registerEvents(new PlayerLeaveListener(), this);
        pm.registerEvents(new PlayerFriendlyFireHitListener(), this);
        pm.registerEvents(new HostilityCreateListener(), this);
        pm.registerEvents(new GuardChatMessageListener(), this);
        pm.registerEvents(new PlayerBankUpdateInventory(), this);
        pm.registerEvents(new ChatChannelListener(), this);
        pm.registerEvents(new StatusUpdateListener(), this);
        pm.registerEvents(new MedAndRestCancelListener(), this);
        pm.registerEvents(new WandListener(), this);
        pm.registerEvents(new HostilityTimeCreatorListener(), this);
        pm.registerEvents(new InstaEatListener(), this);
        pm.registerEvents(new GoldEquipmentListener(), this);
        pm.registerEvents(new PlayerEnterExitPlotRedirectListener(), this);
        pm.registerEvents(new BlockChangePlotListener(), this);
        pm.registerEvents(new EntityInteractPlotListener(), this);
        pm.registerEvents(new PlayerStatusRespawnListener(), this);
        pm.registerEvents(new PlotStaffCreateListener(), this);
        pm.registerEvents(new PlayerSpawnMoveListener(), this);
        pm.registerEvents(new StaffUpdateListener(getLuckPerms()), this);
        pm.registerEvents(new FireballExplodeListener(), this);

        pm.registerEvents(new MuteListener(), this);

        pm.registerEvents(new PreventRemovalChunkListener(), this);

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

        pm.registerEvents(new ZombieDeathListener(), this);
        pm.registerEvents(new HostilityNamePreprocessListener(), this);

        pm.registerEvents(new MatchCreatorListener(), this);
        pm.registerEvents(new BannedJoinListener(), this);
        pm.registerEvents(new MatchDefeatListener(), this);

        pm.registerEvents(new InventorySeeListener(), this);
        pm.registerEvents(new MatchCheatingListener(), this);
        pm.registerEvents(new CombatLogListener(), this);

        pm.registerEvents(new HelpCommandListener(), this);
        pm.registerEvents(new NoAbuseBlockBreakMaterialListener(), this);

        pm.registerEvents(new PlayerStrengthPotionEffectListener(), this);

        pm.registerEvents(new WeatherManagerListener(), this);
        pm.registerEvents(new PlayerFirstTimeJoinListener(), this);

        pm.registerEvents(new RemovePhantomListener(), this);

        pm.registerEvents(new ScrollListener(), this);

        pm.registerEvents(new ClanTPSpell(), this);
        pm.registerEvents(new MarkSpell(), this);
        pm.registerEvents(new RecallSpell(), this);

        pm.registerEvents(new MovingWhileCastArgumentListener(), this);

        pm.registerEvents(new DropLostShardBookListener(), this);
        pm.registerEvents(new PlayerFirstJoinEvent(), this);
        pm.registerEvents(new SeedCommandListener(), this);
        pm.registerEvents(new VoidDamageListener(), this);
        pm.registerEvents(new EnchantmentListener(), this);
        pm.registerEvents(new PlayerJoinCheckClanIfBuffListener(), this);

        pm.registerEvents(new MOTDListener(), this);
        pm.registerEvents(new NotValidReachBlockListener(), this);
        pm.registerEvents(new NotValidMoveBlockListener(), this);
        pm.registerEvents(new EggListener(), this);
        pm.registerEvents(new PermanentGateTravelSpell(), this);
        pm.registerEvents(new PGTListener(), this);

        pm.registerEvents(new NoMoreOldEnchantsListener(), this);

        pm.registerEvents(new SignChangeListener(), this);
        pm.registerEvents(new BedChangeListener(), this);

        pm.registerEvents(new MobSpawnerCancelListener(), this);

        pm.registerEvents(FFA_COMMAND, this);
        pm.registerEvents(new FFAListener(), this);

        pm.registerEvents(new PlayerConnectServerEvent(), this);

        registerCustomEventListener();

        //todo to use later -->
        // pm.registerEvents(new LinkListener(), this);

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
        for (Clan clan : LostShardPlugin.getClanManager().getAllClans()) {

            if (clan.hasStaminaBuff()) {

                for (UUID uuid : clan.getAllUUIDS()) {
                    Stat.wrap(uuid).setMaxMana(Stat.HOST_MAX_STAMINA);
                }

                new BukkitRunnable() {

                    int counter = clan.getStaminaTimer();


                    @Override
                    public void run() {

                        if (counter == 0) {

                            this.cancel();

                            for (UUID uuid : clan.getAllUUIDS()) {

                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                                if (offlinePlayer.isOnline())
                                    offlinePlayer.getPlayer().sendMessage(ChatColor.GOLD + "Your stamina buff has run its glory.");

                                Stat stat = Stat.wrap(uuid);

                                stat.setMaxStamina(Stat.BASE_MAX_STAMINA);
                                if (stat.getStamina() > stat.getMaxStamina())
                                    stat.setStamina(stat.getMaxStamina());

                            }
                            return;

                        }
                        counter--;
                        clan.setStaminaBuffTimer(counter);

                    }
                }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
            }

            if (clan.hasManaBuff()) {


                for (UUID uuid : clan.getAllUUIDS()) {
                    Stat.wrap(uuid).setMaxMana(Stat.HOST_MAX_MANA);
                }


                new BukkitRunnable() {

                    int counter = clan.getManaTimer();

                    @Override
                    public void run() {

                        if (counter == 0) {

                            this.cancel();

                            for (UUID uuid : clan.getAllUUIDS()) {

                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                                if (offlinePlayer.isOnline())
                                    offlinePlayer.getPlayer().sendMessage(ChatColor.GOLD + "Your mana buff has run its glory.");
                                Stat stat = Stat.wrap(uuid);
                                stat.setMaxMana(Stat.BASE_MAX_MANA);

                                if (stat.getMana() > stat.getMaxMana())
                                    stat.setMana(stat.getMana());
                            }
                            return;

                        }
                        counter--;
                        clan.setManaBuffTimer(counter);

                    }
                }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
            }

            if (clan.hasEnhanceTimer()) {
                new BukkitRunnable() {

                    int counter = clan.getEnhanceTimer();

                    @Override
                    public void run() {

                        if (clan.getEnhanceTimer() == 0 && counter > 0) {
                            this.cancel();
                            return;
                        }
                        if (counter == 0) {

                            this.cancel();

                            for (UUID uuid : clan.getAllUUIDS()) {

                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                                if (offlinePlayer.isOnline())
                                    offlinePlayer.getPlayer().sendMessage(ChatColor.GOLD + "Your enhance buff has run its glory.");

                            }
                            return;

                        }
                        counter--;
                        clan.setEnhanceTimer(counter);


                    }
                }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
            }

            if (clan.hasIgniteTimer()) {
                new BukkitRunnable() {

                    int counter = clan.getIgniteTimer();

                    @Override
                    public void run() {

                        if (clan.getIgniteTimer() == 0 && counter > 0) {
                            this.cancel();
                            return;
                        }

                        if (counter == 0) {

                            this.cancel();

                            for (UUID uuid : clan.getAllUUIDS()) {

                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                                if (offlinePlayer.isOnline())
                                    offlinePlayer.getPlayer().sendMessage(ChatColor.GOLD + "Your ignite buff has run its glory.");

                            }
                            return;

                        }
                        counter--;
                        clan.setIgniteTimer(counter);

                    }
                }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
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

    public void registerCustomEventListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    } else {

                    }

                    if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                        PlayerStrengthPotionEffectEvent playerStrengthPotionEffectEvent = new PlayerStrengthPotionEffectEvent(player);
                        Bukkit.getPluginManager().callEvent(playerStrengthPotionEffectEvent);
                        if (playerStrengthPotionEffectEvent.isCancelled())
                            playerStrengthPotionEffectEvent.getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

                    }
                }
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                gameTicks++;
            }
        }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 0, 1);
    }

    public void newDayScheduler() {

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        if (now.compareTo(nextRun) >= 0)
            nextRun = nextRun.plusDays(1);

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds() * 20;

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
                for (Plot plot : getPlotManager().getAllPlots()) {

                    if (!plot.getType().equals(PlotType.PLAYER))
                        continue;

                    PlayerPlot playerPlot = (PlayerPlot) plot;

                    if (!playerPlot.rent()) {
                        OfflinePlayer owner = Bukkit.getOfflinePlayer(playerPlot.getOwnerUUID());
                        if (owner.isOnline()) {
                            if (playerPlot.getRadius() == 1)
                                ;//owner.getPlayer().sendMessage(STANDARD_COLOR + "You did not pay the rent today. You have one day left before your plot is removed.");
                            else
                                ;//owner.getPlayer().sendMessage(STANDARD_COLOR + "You did not pay the rent today. Your plot has shrunk by one block radius.");

                        }
                    }
                }

                //Save all skills
                for (SkillPlayer skillPlayer : LostShardPlugin.getSkillManager().getSkillPlayers()) {
                    LostShardPlugin.getSkillManager().saveSkillPlayer(skillPlayer);
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        newDayScheduler();
                    }
                }.runTaskLater(LostShardPlugin.plugin, 20 * 10);
            }
        }.runTaskLater(this.plugin, initialDelay);

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

    public static DC4JBot getDiscord() {
        return dc4JBot;
    }

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

    public static int getGameTicks() {
        return gameTicks;
    }

    public static ChannelManager getChannelManager() {
        return channelManager;
    }

    public static CombatLogManager getCombatLogManager() {
        return combatLogManager;
    }

    public static PlotManager getPlotManager() {
        return plotManager;
    }

    public static WeatherManager getWeatherManager() {
        return weatherManager;
    }

    public static SaleManager getSaleManager() {
        return saleManager;
    }

    public static BankManager getBankManager() {
        return bankManager;
    }

    public static BanManager getBanManager() {
        return banManager;
    }

    public static GateManager getGateManager() {
        return gateManager;
    }

    public static ClanManager getClanManager() {
        return clanManager;
    }

    public static SkillManager getSkillManager() {
        return skillManager;
    }

    public static ShrineManager getShrineManager() {
        return shrineManager;
    }

    public static GatheringManager getGatheringManager() {
        return gatheringManager;
    }

    public static IgnoreManager getIgnoreManager() {
        return ignoreManager;
    }

    public static TutorialManager getTutorialManager() {
        return tutorialManager;
    }

    public static LSBorder getBorder(String worldName) {
        return fetchBorder(worldName);
    }

    public static String getPatchUpdateVersion(String majorUpdate) {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "News -> " + majorUpdate;
    }

    public static boolean isTutorial() {
        return isTutorial;
    }


}
