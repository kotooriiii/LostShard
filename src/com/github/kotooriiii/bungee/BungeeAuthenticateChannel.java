package com.github.kotooriiii.bungee;

import com.github.kotooriiii.LostShardPlugin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.UUID;

/*
    Intended for this Main class to communicate with Bungee.

    It is expected for Bungee to ask the server if the player has completed the tutorial.

    bungee#send
    this#onPluginMessageReceive
    this#authenticate
    bungee#receive

 */
public class BungeeAuthenticateChannel implements PluginMessageListener {

    public static void debug(String... strings) {
        for (String s : strings)
            LostShardPlugin.plugin.getLogger().info(s);
    }

    /*
    Sending
    LostShard->BungeeCord:Authenticate [Params: UUID playerUUID, boolean isTutorialComplete]
     */
    public void authenticate(UUID uuid, boolean isTutorialComplete) {
        String channelOut = "ls-b:Authenticate".toLowerCase();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(uuid.toString()); // Write the UUID
        out.writeUTF(String.valueOf(isTutorialComplete)); // Write the UUID
        Bukkit.getServer().sendPluginMessage(LostShardPlugin.plugin, channelOut.toLowerCase(), out.toByteArray());
        debug("Sending",channelOut, uuid.toString(), String.valueOf(isTutorialComplete));
    }

    /*
    Receive
    BungeeCord->LostShard:Authenticate [Params: UUID playerUUID]
     */
    @Override
    public void onPluginMessageReceived(String string, Player player, byte[] bytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            String uuidString = in.readUTF(); // Read the UUID
            UUID playerUUID = UUID.fromString(uuidString);

            boolean isTutorialComplete = LostShardPlugin.getTutorialReader().hasCompletedTutorial(playerUUID);

            debug("Receiving","b-ls:Authenticate".toLowerCase(), uuidString, "" +isTutorialComplete);

            authenticate(playerUUID, isTutorialComplete);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static BungeeAuthenticateChannel instance;

    public static BungeeAuthenticateChannel getInstance() {
        if (instance == null) {
            synchronized (BungeeAuthenticateChannel.class) {
                if (instance == null) {
                    instance = new BungeeAuthenticateChannel();
                }
            }
        }
        return instance;
    }


}