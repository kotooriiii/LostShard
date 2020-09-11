package com.github.kotooriiii.bungee;

import com.github.kotooriiii.LostShardPlugin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.UUID;

/*
    Designed for this Tutorial class to speak to Bungee. Bungee tells MainLostShard that the Player is ready to play on main server.

    It is expected for the structure to be:

    this#sendTutorialComplete()
    bungee#receive
    bungee#send
    main#receive

 */
public class BungeeTutorialCompleteChannel {

    /*
    Send
    TutorialLostShard->BungeeCord:Complete
     */
    public void sendTutorialComplete(Player player, boolean isAuthentic) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(player.getUniqueId().toString()); // Write the UUID
        out.writeUTF(String.valueOf(isAuthentic)); //Write if authentic
        Bukkit.getServer().sendPluginMessage(LostShardPlugin.plugin, "TutorialLostShard->BungeeCord:Complete", out.toByteArray());
    }

    private BungeeTutorialCompleteChannel instance;

    public BungeeTutorialCompleteChannel getInstance() {
        if (instance == null) {
            synchronized (BungeeTutorialCompleteChannel.class) {
                if (instance == null)
                    instance = new BungeeTutorialCompleteChannel();
            }
        }
        return instance;
    }

}