package com.github.kotooriiii;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.commands.ClanCommand;
import com.github.kotooriiii.listeners.ClanCreateTagListener;
import com.github.kotooriiii.listeners.PlayerHitEvent;
import com.github.kotooriiii.listeners.PlayerLeaveListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import static com.github.kotooriiii.data.Maps.*;

public class LostShardK extends JavaPlugin {

    public static Plugin plugin;
    public static Logger logger;
    public static PluginDescriptionFile pluginDescriptionFile;

    @Override
    public void onEnable() {

        //Console logger, plugin, and description file are all ready for public use
        logger = Logger.getLogger("Minecraft");
        plugin = this;
        pluginDescriptionFile = this.getDescription();

        //Registers the com.github.kotooriiii.commands and com.github.kotooriiii.events from this plugin
        registerCommands();
        registerEvents();

        //All was successfully enabled
        logger.info(pluginDescriptionFile.getName() + " has been successfully enabled on the server.");
    }

    @Override
    public void onDisable() {

        plugin = null;
        logger = null;
        pluginDescriptionFile = null;


        saveData();

        logger.info(pluginDescriptionFile.getName() + " has been successfully disabled on the server.");
    }

    private void saveData() {
        clanTagCreators = new HashMap<>();
        clanColorCreators = new HashMap<>();
        clanDisbandTimer = new ArrayList<>();
        leaderConfirmation = new ArrayList<>();
        invitationConfirmation = new HashMap<>();
        //todo SAVE FILE for CLANS

    }


    public void registerCommands() {
        getCommand("clan").setExecutor(new ClanCommand());
        getCommand("ff").setExecutor(new ClanCommand());
    }

    public void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ClanCreateTagListener(), this);
        pm.registerEvents(new PlayerLeaveListener(), this);
        pm.registerEvents(new PlayerHitEvent(), this);


    }
}
