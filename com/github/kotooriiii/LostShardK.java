package com.github.kotooriiii;

import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.commands.ClanCommand;
import com.github.kotooriiii.commands.FriendlyFireCommand;
import com.github.kotooriiii.files.FileManager;
import com.github.kotooriiii.listeners.ClanCreateTagListener;
import com.github.kotooriiii.listeners.PlayerHitEvent;
import com.github.kotooriiii.listeners.PlayerLeaveListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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

        FileManager.init();

        //Registers the com.github.kotooriiii.commands and com.github.kotooriiii.events from this plugin
        registerCommands();
        registerEvents();

        loop();
        //All was successfully enabled
        logger.info(pluginDescriptionFile.getName() + " has been successfully enabled on the server.");
    }

    @Override
    public void onDisable() {

        saveData();
        logger.info(pluginDescriptionFile.getName() + " has been successfully disabled on the server.");
        plugin = null;
        logger = null;
        pluginDescriptionFile = null;

    }

    private void saveData() {

        for (Clan clan : clans) {
            FileManager.write(clan);
        }
    }

    private void loop() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(LostShardK.plugin, new Runnable() {
            public void run() {
                saveData();
            }
        }, 5*60*20L, 6 * 60 * 60 * 20L);//every 6hrs
    }


    public void registerCommands() {
        getCommand("clan").setExecutor(new ClanCommand());
        getCommand("ff").setExecutor(new FriendlyFireCommand());
    }

    public void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ClanCreateTagListener(), this);
        pm.registerEvents(new PlayerLeaveListener(), this);
        pm.registerEvents(new PlayerHitEvent(), this);


    }
}
