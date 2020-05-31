package com.github.kotooriiii.channels.events;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.ChannelStatus;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.ranks.RankPlayer;
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

        if (shardChatEvent.isCancelled())
            return;

        //Player sending message
        Player player = asyncPlayerChatEvent.getPlayer();

        //Channel player belongs to
        ChannelStatus channelStatus = LostShardPlugin.getChannelManager().getChannel(player);
        String prefix = channelStatus.getPrefix();

        //Grab stat to later grab title
        Stat stat = Stat.wrap(player);
        String title = stat.getTitle();

        //Clan
        Clan clan = Clan.getClan(player.getUniqueId());
        String clanTag = "";
        if (clan != null)
            clanTag = ChatColor.GREEN + clan.getTag().toUpperCase();

        //If has a title
        if (!title.isEmpty())
            title = ChatColor.WHITE + title;

        //Color of status player is in [worthy,exiled,etc]
        ChatColor color = StatusPlayer.wrap(player.getUniqueId()).getStatus().getChatColor();

        //Staff prefix
        if (Staff.isStaff(player.getUniqueId())) {
            Staff staff = Staff.wrap(player.getUniqueId());
            color = staff.getType().getChatColor();
            prefix = ChatColor.GOLD + "[" + prefix + ChatColor.GOLD + "]";
        } else {
            //Not staff
            RankPlayer rankPlayer = RankPlayer.wrap(player.getUniqueId());

            //Donator maybe?
            if (rankPlayer.isDonator()) {
                prefix = rankPlayer.getChannelContent(channelStatus.getPrefix());
            } else {
                prefix = ChatColor.WHITE + "[" + channelStatus.getPrefix() + ChatColor.WHITE + "]";

            }
        }

        //Color name of player
        String name = color + player.getName();
        //Get message
        String message = asyncPlayerChatEvent.getMessage();

        //Message Color
        ChatColor messageColor = ChatColor.WHITE;

        if(LostShardPlugin.getChannelManager().isAdminChat())
        {
            if(!player.hasPermission(STAFF_PERMISSION))
            {
                //todo maybe a message saying u cant speak during admin chat ?
                player.sendMessage(ChatColor.RED + "Chat is muted.");
                return;
            }

            prefix = ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "Admin" + ChatColor.WHITE + "]";
        }


        String[] properties = new String[]{prefix, clanTag, title, name};
        String builder = HelperMethods.stringBuilder(properties, 0, " ");



        ArrayList<Player> recipients = getRecipients(player);
        if (recipients == null)
            return;
        for (Player recipient : recipients) {
            recipient.sendMessage(builder + messageColor + ": " + message);
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
