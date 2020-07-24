package com.github.kotooriiii.status.shrine;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.util.HelperMethods;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.*;

public class AtoneCommand implements CommandExecutor {

    final int RESET_COUNT = 4;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return false;

        if (!command.getName().equalsIgnoreCase("atone"))
            return false;

        Player player = (Player) commandSender;

        StatusPlayer statusPlayer = StatusPlayer.wrap(player.getUniqueId());
        Bank bank = LostShardPlugin.getBankManager().wrap(player.getUniqueId());
        Stat stat = Stat.wrap(player.getUniqueId());


        if (args.length == 0) {

            boolean isNearby = false;
            for (Shrine shrine : LostShardPlugin.getShrineManager().getShrines()) {
                if (!(shrine instanceof AtoneShrine))
                    continue;
                AtoneShrine atoneShrine = (AtoneShrine) shrine;
                if (atoneShrine.isNearby(player.getLocation())) {
                    isNearby = true;
                }
            }

            if (!isNearby) {
                player.sendMessage(ERROR_COLOR + "You are not nearby a shrine.");
                return false;
            }

            if(statusPlayer.getStatus() != Status.EXILED)
            {
                player.sendMessage(ERROR_COLOR + "You must be a Murderer to atone for your sins.");
                return false;
            }

            if (!statusPlayer.isAbleToAtone()) {

                player.sendMessage(ERROR_COLOR + "You can not atone so soon again. You must wait " + ChatColor.YELLOW + HelperMethods.getTimeLeft(statusPlayer.getNextAtoneDate()) + ERROR_COLOR + " to make reparations.");
                return false;
            }

            int cost = statusPlayer.getKills() - RESET_COUNT;
            if (bank.getCurrency() < cost) {
                player.sendMessage(ERROR_COLOR + "You must have at least " + cost + " gold in your bank account.");
                return false;
            }


            player.sendMessage(ChatColor.GOLD + "You have begun to atone for your sins...");

            stat.setMana(0);
            stat.setStamina(0);
            bank.removeCurrency(cost);

            new BukkitRunnable(){
                @Override
                public void run()
                {
                    if(player.isDead() || !player.isOnline())
                    {
                        if(player.isOnline())
                            player.sendMessage(ChatColor.GOLD + "You died. We refunded your mana, stamina, and gold.");
                        stat.setMana(100);
                        stat.setStamina(100);
                        bank.addCurrency(cost);
                        return;
                    }
                    statusPlayer.setStatus(Status.WORTHY);
                    statusPlayer.setKills(4);
                    player.sendMessage(ChatColor.GOLD + "You have atoned for your sins. You are now Worthy again.");
                    statusPlayer.setLastAtoneDate(ZonedDateTime.now(ZoneId.of("America/New_York")));

                }
            }.runTaskLater(LostShardPlugin.plugin, 20*3);


            return true;
        }

        switch (args[0].toLowerCase()) {
            case "staff":
                if (!player.hasPermission(STAFF_PERMISSION)) {
                    player.sendMessage(ERROR_COLOR + "No permission to access these commands.");
                    return false;
                }

                if (args.length == 1) {
                    player.sendMessage(ERROR_COLOR + "You can only /atone staff [create/delete].");
                    return false;
                }
                switch (args[1].toLowerCase()) {
                    case "create":
                        AtoneShrine atoneShrine = new AtoneShrine();
                        atoneShrine.setLocation(player.getLocation());
                        boolean isSaved = LostShardPlugin.getShrineManager().addShrine(atoneShrine, true);
                        if (isSaved)
                            player.sendMessage(STANDARD_COLOR + "Shrine was saved to the database.");
                        else
                            player.sendMessage(ERROR_COLOR + "Shrine was not saved because the location is already used.");
                        break;
                    case "removeuuid":
                        LostShardPlugin.getShrineManager().removeShrine(
                                LostShardPlugin.getShrineManager().getShrine(
                                        UUID.fromString(args[2])
                                ));
                        break;
                    case "delete":
                        String lastString = "";
                        for (Map.Entry<String, Shrine> entry : LostShardPlugin.getShrineManager().getMap().entrySet()) {
                            String name = entry.getKey();
                            Shrine shrine = entry.getValue();

                            if (!lastString.equalsIgnoreCase(name)) {
                                lastString = name;
                                player.sendMessage(ChatColor.YELLOW + name);
                            }

                            TextComponent component = new TextComponent("Location: " + shrine.getLocation().getWorld() + ", " + shrine.getLocation().getBlockX() + ", " + shrine.getLocation().getBlockY() + ", " + shrine.getLocation().getBlockZ());
                            component.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/atone staff removeuuid " + shrine.getUUID().toString()));
                            player.spigot().sendMessage(component);
                        }
                        player.sendMessage(STANDARD_COLOR + "To delete a shrine: hover over the desired location and click!");
                        break;
                    default:
                        player.sendMessage(ERROR_COLOR + "You can only /atone staff [create/delete].");
                        break;
                }
                break;
            default:
                player.sendMessage(ERROR_COLOR + "Did you mean /atone staff?");
                break;
        }
        return true;
    }
}
