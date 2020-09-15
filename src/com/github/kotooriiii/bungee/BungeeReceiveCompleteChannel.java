package com.github.kotooriiii.bungee;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.UUID;

/*
    Intended for this Main class to receive a tutorial complete message from Bungee.

    It is expected for Bungee to send this Main class a message stating a player is ready to play.

    It is expected for the structure to be:

    tutorial#sendTutorialComplete()
    bungee#receive
    bungee#send
    this#receive

 */
public class BungeeReceiveCompleteChannel implements PluginMessageListener {

    /*
    Receive
    BungeeCord->LostShard:Complete [Params: UUID playerUUID, boolean isAuthentic]
     */

    @Override
    public void onPluginMessageReceived(String string, Player player, byte[] bytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            String uuidString = in.readUTF(); // Read the UUID
            UUID playerUUID = UUID.fromString(uuidString);

            String isAuthenticString = in.readUTF();
            boolean isAuthentic= Boolean.valueOf(isAuthenticString);

            LostShardPlugin.getTutorialReader().completeTutorial(playerUUID, isAuthentic);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static BungeeReceiveCompleteChannel instance;

    public static BungeeReceiveCompleteChannel getInstance() {
        if (instance == null) {
            synchronized (BungeeReceiveCompleteChannel.class) {
                if (instance == null) {
                    instance = new BungeeReceiveCompleteChannel();
                }
            }
        }
        return instance;
    }


}