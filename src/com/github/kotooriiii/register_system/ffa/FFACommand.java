package com.github.kotooriiii.register_system.ffa;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.register_system.Gathering;
import com.github.kotooriiii.register_system.GatheringType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class FFACommand implements CommandExecutor, Listener {

    final HashMap<UUID, FFAMode> map = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (!cmd.getName().equalsIgnoreCase("ffa"))
            return false;

        if (!(sender instanceof Player)) {
            sender.sendMessage(ERROR_COLOR + "must be a player");
            return false;
        }

        Player playerSender = (Player) sender;

        if (args.length == 0) {
            return false;
        }

        arg0:
        switch (args[0].toLowerCase()) {
            case "staff":
                if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                    playerSender.sendMessage(ERROR_COLOR + "You don't have staff permission.");
                    return false;
                }

                if (args.length == 1) {
                    playerSender.sendMessage(STANDARD_COLOR + "Did you mean /ffa staff [create/end]");
                    return false;
                }

                arg1:
                switch (args[1].toLowerCase()) {
                    case "create":
                        if (LostShardPlugin.getPlotManager().getPlot("FFA") == null) {
                            playerSender.sendMessage(ERROR_COLOR + "The FFA plot has not been created.");
                            return false;
                        }
                        map.put(playerSender.getUniqueId(), new FFAMode());
                        playerSender.sendMessage(STANDARD_COLOR + "What is the armor loadout? Type anything.");
                        break arg0;
                    case "end":
                        Gathering gathering = LostShardPlugin.getGatheringManager().getGathering();
                        if (gathering == null)
                            return false;
                        if (gathering.getType() != GatheringType.FFA)
                            return false;
                        map.clear();
                        gathering.endGame();
                        for (Player player : gathering.getRegisterManager().getRegisteredPlayers())
                            player.sendMessage(STANDARD_COLOR + "Abruptly ended the match.");
                        playerSender.sendMessage(STANDARD_COLOR + "Abruptly ended the match.");
                        break arg0;
                    default:
                        sender.sendMessage(ERROR_COLOR + "Invalid command syntax.");
                        break;
                }

                break;
            default:
                sender.sendMessage(ERROR_COLOR + "Invalid command syntax.");
                break;
        }

        return true;
    }

    @EventHandler
    public void onListen(ShardChatEvent event) {
        String msg = event.getMessage();
        Player player = event.getPlayer();


        FFAMode ffa = map.get(player.getUniqueId());

        if (ffa == null)
            return;

        event.setCancelled(true);


        if (ffa.getArmorLoadout().isEmpty()) {
            ffa.setArmorLoadout(msg);
            player.sendMessage(STANDARD_COLOR + "What is the sword loadout? Type anything.");
            return;
        }

        if (ffa.getSwordLoadout().isEmpty()) {
            ffa.setSwordLoadout(msg);
            player.sendMessage(STANDARD_COLOR + "What is the bow loadout? Type anything.");
            return;
        }

        if (ffa.getBowLoadout().isEmpty()) {
            ffa.setBowLoadout(msg);
            player.sendMessage(STANDARD_COLOR + "What is the spell loadout? Type anything.");
            return;
        }

        if (ffa.getSpellsLoadout().isEmpty()) {
            ffa.setSpellsLoadout(msg);
            player.sendMessage(STANDARD_COLOR + "Creating FFA.");
        }

        LostShardPlugin.getGatheringManager().setGathering(ffa);
        ffa.sendIntroduction();
        map.clear();

    }
}
