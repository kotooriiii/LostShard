package com.github.kotooriiii.channels;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.ranks.RankType;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class ChatChannelListener implements Listener {
    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        if (LostShardPlugin.getChannelManager().getChannel(player) == null)
            LostShardPlugin.getChannelManager().joinChannel(player, ChannelStatus.GLOBAL);

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {

        asyncPlayerChatEvent.setCancelled(true);
        if (asyncPlayerChatEvent.isAsynchronous()) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    artificialMessage(asyncPlayerChatEvent);
                }
            }.runTask(LostShardPlugin.plugin);
        } else {
            artificialMessage(asyncPlayerChatEvent);
        }

    }

    public void artificialMessage(AsyncPlayerChatEvent asyncPlayerChatEvent) {

        ShardChatEvent shardChatEvent = new ShardChatEvent(asyncPlayerChatEvent.getPlayer(), asyncPlayerChatEvent.getMessage());
        Bukkit.getPluginManager().callEvent(shardChatEvent);

        if (shardChatEvent.isCancelled()) {
            return;
        }

        Player player = asyncPlayerChatEvent.getPlayer();

        ChannelStatus channelStatus = LostShardPlugin.getChannelManager().getChannel(player);
        Stat stat = Stat.wrap(player);
        String prefix = channelStatus.getPrefix();
        String title = stat.getTitle();
        if (!title.isEmpty())
            title = ChatColor.WHITE + title;
        ChatColor color = StatusPlayer.wrap(player.getUniqueId()).getStatus().getChatColor();

        if (Staff.isStaff(player.getUniqueId())) {
            Staff staff = Staff.wrap(player.getUniqueId());
            color = staff.getType().getChatColor();
            prefix = ChatColor.GOLD + "[" + prefix + ChatColor.GOLD + "]";
        } else {

            RankPlayer rankPlayer = RankPlayer.wrap(player.getUniqueId());
            if (rankPlayer.isDonator()) {
                prefix = rankPlayer.getChannelContent(channelStatus.getPrefix());
            } else {
                prefix = ChatColor.WHITE + "[" + channelStatus.getPrefix() + ChatColor.WHITE + "]";

            }
        }

        String name = color + player.getName();
        String message = asyncPlayerChatEvent.getMessage();

        String[] properties = new String[]{prefix, title, name};
        String builder = HelperMethods.stringBuilder(properties, 0, " ");


        ArrayList<Player> recipients = getRecipients(player);
        if (recipients == null)
            return;
        for (Player recipient : recipients) {
            recipient.sendMessage(builder + ChatColor.WHITE + ": " + message);
        }
    }

    private ArrayList<Player> getRecipients(Player chattingPlayer) {
        ChannelStatus status = LostShardPlugin.getChannelManager().getChannel(chattingPlayer);
        ArrayList<Player> players = new ArrayList<>();
        switch (status) {
            case GLOBAL:
                players.addAll(Bukkit.getOnlinePlayers());
                break;
            case CLAN:
                Clan clan = Clan.getClan(chattingPlayer.getUniqueId());
                if (clan == null) {
                    chattingPlayer.sendMessage(ERROR_COLOR + "You can't chat without belonging to a clan. You are forcibly being moved to global chat.");
                    LostShardPlugin.getChannelManager().joinChannel(chattingPlayer, ChannelStatus.GLOBAL);
                } else {
                    Player[] onlineClanMembers = clan.getOnlinePlayers();
                    for (int i = 0; i < onlineClanMembers.length; i++) {
                        players.add(onlineClanMembers[i]);
                    }
                }
                break;
            case LOCAL:
                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                    double distance = chattingPlayer.getLocation().distance(onlinePlayers.getLocation());
                    if (distance <= 100) {
                        players.add(onlinePlayers);
                    }
                }
                break;
        }
        return players;
    }

}
