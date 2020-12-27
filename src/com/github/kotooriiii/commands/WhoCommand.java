package com.github.kotooriiii.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.status.StaffType;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static com.github.kotooriiii.data.Maps.*;

public class WhoCommand implements CommandExecutor {
    final String prefix = StaffType.OWNER.getChatColor() + "Admin" + ChatColor.WHITE + ", " + Status.MURDERER.getChatColor() + Status.MURDERER.getName() + ChatColor.WHITE + ", " + Status.CRIMINAL.getChatColor() + Status.CRIMINAL.getName() + ChatColor.WHITE + ", " + Status.LAWFUL.getChatColor() + Status.LAWFUL.getName();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!command.getName().equalsIgnoreCase("who"))
            return false;


        final ArrayList<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        Collections.sort(onlinePlayers, new Comparator<Player>() {
            @Override
            public int compare(Player playerA, Player playerB) {

                if (Staff.isStaff(playerA.getUniqueId()) && Staff.isStaff(playerB.getUniqueId()))
                    return 0;
                if (Staff.isStaff(playerA.getUniqueId()) && !Staff.isStaff(playerB.getUniqueId()))
                    return 1;
                if (!Staff.isStaff(playerA.getUniqueId()) && Staff.isStaff(playerB.getUniqueId()))
                    return -1;

                //else
                if (StatusPlayer.wrap(playerA.getUniqueId()).getStatus().getWeight() > StatusPlayer.wrap(playerB.getUniqueId()).getStatus().getWeight())
                    return 1;
                if (StatusPlayer.wrap(playerA.getUniqueId()).getStatus().getWeight() < StatusPlayer.wrap(playerB.getUniqueId()).getStatus().getWeight())
                    return -1;
                return 0;
            }
        }.reversed());

        String finalResult = prefix + "\n" + ChatColor.WHITE + "(" + onlinePlayers.size() + "/" + Bukkit.getServer().getMaxPlayers() + ChatColor.WHITE + ") [";
        for (int i = 0; i < onlinePlayers.size(); i++) {
            Player player = onlinePlayers.get(i);

            if(player.hasMetadata("vanished"))
                continue;

            if (!Staff.isStaff(player.getUniqueId()))
                finalResult += StatusPlayer.wrap(player.getUniqueId()).getStatus().getChatColor() + player.getName();
            else
                finalResult += Staff.wrap(player.getUniqueId()).getType().getChatColor() + player.getName();

            if (i != onlinePlayers.size() - 1)
                finalResult += ChatColor.WHITE + ", ";
        }
        finalResult += ChatColor.WHITE + "]";
        commandSender.sendMessage(finalResult);


        return true;
    }
}
