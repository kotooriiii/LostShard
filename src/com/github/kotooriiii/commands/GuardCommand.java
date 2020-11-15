package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.type.guard.GuardNPC;
import com.github.kotooriiii.npc.type.guard.GuardTrait;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;
import static com.github.kotooriiii.data.Maps.COMMAND_COLOR;
import static com.github.kotooriiii.util.HelperMethods.stringBuilder;

public class GuardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            final Player playerSender = (Player) sender;
            final UUID playerUUID = playerSender.getUniqueId();
            //If the command is the "guards" command
            if (cmd.getName().equalsIgnoreCase("guard")) {
                //No arguments regarding this command
                if (args.length == 0) {
                    final Location playerLocation = playerSender.getLocation();

                    Plot plot = LostShardPlugin.getPlotManager().getStandingOnPlot(playerLocation);
                    //plot doesnt exist
                    if (plot == null) {
                        return false;
                    }

                    //must be in order;
                    if (!plot.getName().equalsIgnoreCase("order")) {
                        return false;
                    }


                    StatusPlayer statusPlayer = StatusPlayer.wrap(playerUUID);
                    if (!statusPlayer.getStatus().equals(Status.WORTHY)) {
                        playerSender.sendMessage(ERROR_COLOR + "You must be a Worthy player to call a guard.");
                        return true;
                    }

                    NPC guardNPC = GuardNPC.getNearestGuard(playerLocation);
                    GuardTrait guardTrait = guardNPC.getTrait(GuardTrait.class);
                    if (guardNPC == null) {
                        playerSender.sendMessage(ERROR_COLOR + "No guard nearby!");
                        return true;
                    }

                    if (!statusPlayer.hasNearbyEnemyRange(5)) {
                        playerSender.sendMessage(ERROR_COLOR + "No enemies nearby. Don't waste the guards' time.");
                        return true;
                    }


                    guardTrait.setCalled(true);
                    guardTrait.setOwner(playerUUID);
                    guardNPC.teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);

                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            if (guardTrait.isBusy())
                                return;
                            int curX = guardNPC.getStoredLocation().getBlockX();
                            int postX = guardTrait.getGuardingLocation().getBlockX();
                            int curY = guardNPC.getStoredLocation().getBlockY();
                            int postY = guardTrait.getGuardingLocation().getBlockY();
                            int curZ = guardNPC.getStoredLocation().getBlockZ();
                            int postZ = guardTrait.getGuardingLocation().getBlockZ();

                            guardNPC.getStoredLocation().getWorld().spawnParticle(Particle.BARRIER, new Location(guardNPC.getStoredLocation().getWorld(), guardNPC.getStoredLocation().getBlockX() + 0.5, guardNPC.getStoredLocation().getBlockY() + 3, guardNPC.getStoredLocation().getBlockZ() + 0.5), 1);
                            if (curX != postX && curY != postY && curZ != postZ) {
                                guardNPC.getStoredLocation().getWorld().playSound(guardNPC.getStoredLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 0);
                            }
                        }
                    }.runTaskLater(LostShardPlugin.plugin, 10);

                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            int curX = guardNPC.getStoredLocation().getBlockX();
                            int postX = guardTrait.getGuardingLocation().getBlockX();
                            int curY = guardNPC.getStoredLocation().getBlockY();
                            int postY = guardTrait.getGuardingLocation().getBlockY();
                            int curZ = guardNPC.getStoredLocation().getBlockZ();
                            int postZ = guardTrait.getGuardingLocation().getBlockZ();

                            if (curX != postX && curY != postY && curZ != postZ) {
                                guardNPC.teleport(guardTrait.getGuardingLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                guardTrait.setCalled(false);
                                guardTrait.setOwner(null);
                            }
                        }

                    }.runTaskLater(LostShardPlugin.plugin, 20*2);


                }
                //This statement refers to: /guards <argument 0> <argument 1> ... <argument n>
                else if (args.length >= 1) {
                    switch (args[0].toLowerCase()) {
                        case "staff":
                            if (!playerSender.hasPermission(STAFF_PERMISSION)) {
                                playerSender.sendMessage(ERROR_COLOR + "You do not have access to staff commands.");
                                return true;
                            }

                            if (args.length == 1) //host <argument 0>
                            {
                                sendStaffHelp(playerSender);
                                return true;
                            }

                            switch (args[1].toLowerCase()) { //host <arg 0> <arg 1> ... <arg n>
                                case "create":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/guard staff create (name)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    }
                                    // /host <arg 0/staff> <arg 1/create> ......... <arg n>
                                    String nameCreate = stringBuilder(args, 2, " ");

                                    if (nameCreate.length() > 14) {
                                        playerSender.sendMessage(ERROR_COLOR + "You can't have a name that big! Shrink it or else the game crashes.");
                                        return true;
                                    }

                                    for (NPC guardNPC : GuardNPC.getAllGuardNPC()) {
                                        GuardTrait guardTrait = guardNPC.getTrait(GuardTrait.class);
                                        if (guardTrait.getGuardName().equalsIgnoreCase(nameCreate)) {
                                            playerSender.sendMessage(ERROR_COLOR + "A guard already has this name!");
                                            return true;
                                        }
                                    }

                                    playerSender.sendMessage(STANDARD_COLOR + "You have hired " + GUARD_COLOR + nameCreate + STANDARD_COLOR + " to stand in this position.");
                                    GuardNPC guardNPC = new GuardNPC(nameCreate);
                                    guardNPC.spawn(playerSender.getLocation());
                                    break;
                                case "delete":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/guard staff delete (name)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    }
                                    // /host <arg 0/staff> <arg 1/create> ......... <arg n>
                                    String nameDelete = stringBuilder(args, 2, " ");
                                    for (NPC deleteGuardNPC : GuardNPC.getAllGuardNPC()) {
                                        GuardTrait guardTrait = deleteGuardNPC.getTrait(GuardTrait.class);
                                        if (guardTrait.getGuardName().equalsIgnoreCase(nameDelete)) {
                                            playerSender.sendMessage(STANDARD_COLOR + "You have relieved " + GUARD_COLOR + guardTrait.getGuardName() + STANDARD_COLOR + " from his duty.");
                                            CitizensAPI.getNPCRegistry().deregister(deleteGuardNPC);
                                            return true;
                                        }
                                    }
                                    playerSender.sendMessage(ERROR_COLOR + "We could not find " + GUARD_COLOR + nameDelete + ERROR_COLOR + " in our records of Guards.");
                                    break;
                                case "setspawn":
                                    if (args.length == 2) {
                                        playerSender.sendMessage(ERROR_COLOR + "You provided too few arguments: " + COMMAND_COLOR + "/guard staff setspawn (name)" + ERROR_COLOR + "."); //clan staff uuid
                                        return true;
                                    }
                                    // /host <arg 0/staff> <arg 1/create> ......... <arg n>
                                    String nameSetGuardPost = stringBuilder(args, 2, " ");
                                    for (NPC setspawnGuardNPC : GuardNPC.getAllGuardNPC()) {
                                        GuardTrait guardTrait = setspawnGuardNPC.getTrait(GuardTrait.class);
                                        if (guardTrait.getGuardName().equalsIgnoreCase(nameSetGuardPost)) {
                                            playerSender.sendMessage(STANDARD_COLOR + "You have set a new location for the guard to.. well uh, guard. Thanks, " + GUARD_COLOR + guardTrait.getGuardName() + STANDARD_COLOR + "!");
                                            guardTrait.setGuardingLocation(playerSender.getLocation());
                                            return true;
                                        }
                                    }
                                    playerSender.sendMessage(ERROR_COLOR + "We could not find " + GUARD_COLOR + nameSetGuardPost + ERROR_COLOR + " in our records of Guards.");
                                    break;
                                case "show":
                                    playerSender.sendMessage(STANDARD_COLOR + "-=[Guards Active]=-");
                                    for (NPC showGuardNPC : GuardNPC.getAllGuardNPC()) {
                                        GuardTrait guardTrait = showGuardNPC.getTrait(GuardTrait.class);
                                        int x = guardTrait.getGuardingLocation().getBlockX();
                                        int y = guardTrait.getGuardingLocation().getBlockY();
                                        int z = guardTrait.getGuardingLocation().getBlockZ();

                                        BaseComponent[] tc = new ComponentBuilder(GUARD_COLOR + "" + guardTrait.getGuardName() + STANDARD_COLOR + " is positioned at x:" + STANDARD_COLOR + x + ", y:" + y + ", z:" + z + ".")
                                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(STANDARD_COLOR + "Teleport to " + GUARD_COLOR + guardTrait.getGuardName() + STANDARD_COLOR + ".").create()))
                                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleport " + playerSender.getName() + " " + x + " " + y + " " + z)).create();

                                        playerSender.spigot().sendMessage(ChatMessageType.CHAT, tc);
                                    }
                                    playerSender.sendMessage(STANDARD_COLOR + "-----------------");
                                    break;
                                default:
                                    sendStaffUnknownCommand(playerSender);
                                    break;
                            }
                            break;
                        default:
                            sendUnknownCommand(playerSender);
                            break;
                    }
                }
            }
        }
        return true;
    }

    public void createGuard(Player player) {

    }

    public void sendStaffHelp(Player playerSender) {
        playerSender.sendMessage(ChatColor.GOLD + "------Guard Staff Help------");

        playerSender.sendMessage(COMMAND_COLOR + "/guard staff create " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/guard staff delete " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/guard staff setspawn " + ChatColor.YELLOW + "(name)");
        playerSender.sendMessage(COMMAND_COLOR + "/guard staff show " + ChatColor.YELLOW + "");
    }

    private void sendUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Guard. Use " + "/guard" + ERROR_COLOR + " to have a guard protect you momentarily.");
    }

    private void sendStaffUnknownCommand(Player playerSender) {
        playerSender.sendMessage(ERROR_COLOR + "The sub-command you provided does not exist in Guard's Staff. Use " + "/guard staff" + ERROR_COLOR + " for help.");
    }
}
