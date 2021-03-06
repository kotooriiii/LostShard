package com.github.kotooriiii.channels.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.CensorManager;
import com.github.kotooriiii.channels.ChannelStatus;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.commands.NotificationCommand;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.sorcery.spells.type.circle9.GluttonySpell;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.STAFF_PERMISSION;

public class ChatChannelListener implements Listener {
    public static final Sound PING_SOUND = Sound.ENTITY_ARROW_HIT_PLAYER;
    public static final float PING_PITCH = 3;

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
                    this.cancel();
                }
            }.runTask(LostShardPlugin.plugin);
        } else {
            artificialMessage(asyncPlayerChatEvent);
        }

    }

    public void artificialMessage(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        //Message Color
        ChatColor messageColor = ChatColor.WHITE;
        if(LostShardPlugin.getChannelManager().getChannel(asyncPlayerChatEvent.getPlayer()) == ChannelStatus.STAFF)
            messageColor=ChatColor.RED;
        ShardChatEvent shardChatEvent = new ShardChatEvent(asyncPlayerChatEvent.getPlayer(), asyncPlayerChatEvent.getMessage(), getFormattedPing(asyncPlayerChatEvent.getMessage(), messageColor));
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
        Clan clan = LostShardPlugin.getClanManager().getClan(player.getUniqueId());
        String clanTag = "";
        if (clan != null)
            clanTag = ChatColor.GREEN + clan.getTag().toUpperCase();

        //If has a title
        if (!title.isEmpty())
            title = ChatColor.WHITE + title;

        //Color of status player is in [lawful,murderer,etc]
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
        String translatedMessage = asyncPlayerChatEvent.getMessage();
        translatedMessage = CensorManager.getInstance().apply(translatedMessage);
        String message = getFormattedPing(translatedMessage, messageColor);
        ArrayList<Player> pingedPlayers = getPingedPlayers(asyncPlayerChatEvent.getMessage());


        if (LostShardPlugin.getChannelManager().isAdminChat()) {
            if (!player.hasPermission(STAFF_PERMISSION)) {
                //todo maybe a message saying u cant speak during admin chat ?
                player.sendMessage(ChatColor.RED + "Chat is muted.");
                return;
            }

            prefix = ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "Admin" + ChatColor.WHITE + "]";
        }


        String[] properties;

        boolean isCake = GluttonySpell.isCake(player.getUniqueId());
        String cakeText = isCake ?  ChatColor.LIGHT_PURPLE + "(Cake)" : "";

        switch (LostShardPlugin.getChannelManager().getChannel(player)) {

            case GLOBAL:
            case CLAN:
                properties = new String[]{prefix, clanTag, title, name, cakeText};
                break;
            case SHOUT:
                properties = new String[]{name,cakeText, ChatColor.WHITE + "shouts"};
                break;
            case WHISPER:
                properties = new String[]{name,cakeText, ChatColor.WHITE + "whispers"};
                break;
            case STAFF:
                properties = new String[]{prefix, name, cakeText};
                break;
            default:
                properties = new String[]{"nullChannel"};
                break;

        }


        String builder = HelperMethods.stringBuilder(properties, 0, " ");


        ArrayList<Player> recipients = getRecipients(player);
        if (recipients == null)
            return;



        for (Player recipient : recipients) {
            if (pingedPlayers.contains(recipient))
                if (NotificationCommand.isPingable(recipient))
                    recipient.playSound(recipient.getLocation(), ChatChannelListener.PING_SOUND, 10, PING_PITCH);

            recipient.sendMessage(builder + messageColor + ": " + message);
        }

        LostShardPlugin.plugin.getServer().getConsoleSender().sendMessage(builder + messageColor + ": " + message);


        if (recipients.size() == 1) {
            switch (LostShardPlugin.getChannelManager().getChannel(player)) {
                case SHOUT:
                case WHISPER:
                    player.sendMessage(ChatColor.GRAY + "No one hears you...");
                    break;
            }
        }
    }



    public String getFormattedPing(String message, ChatColor messageColor) {
        /*

        Message is sent
        Split into arguments
        Check each word if it starts with @
        make sure the length of anything after the @ is not 0
        check if that string, now named the player name, is online

        if online, gold
        if offline, gray
        if doesnt exist, nothing

        ping sound to other player
         */


        String[] args = message.split(" ");

        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("@"))
                continue;

            String name = args[i].substring(1);

            Player targetPlayer = Bukkit.getPlayer(name);

            if (targetPlayer != null) {
                name = ChatColor.GOLD + "@" + targetPlayer.getName() + messageColor;
            } else {
                name = ChatColor.GRAY + "@" + name + messageColor;
            }

            args[i] = name;
        }

        return HelperMethods.stringBuilder(args, 0, " ");
    }

    public ArrayList<Player> getPingedPlayers(String message) {
        /*

        Message is sent
        Split into arguments
        Check each word if it starts with @
        make sure the length of anything after the @ is not 0
        check if that string, now named the player name, is online

        if online, gold
        if offline, gray
        if doesnt exist, nothing

        ping sound to other player
         */


        String[] args = message.split(" ");

        ArrayList<Player> pingedPlayers = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("@"))
                continue;

            String name = args[i].substring(1);

            Player targetPlayer = Bukkit.getPlayer(name);

            if (targetPlayer != null) {
                pingedPlayers.add(targetPlayer);
            }
        }

        return pingedPlayers;
    }

    private ArrayList<Player> getRecipients(Player chattingPlayer) {
        ChannelStatus status = LostShardPlugin.getChannelManager().getChannel(chattingPlayer);
        ArrayList<Player> players = new ArrayList<>();
        switch (status) {
            case GLOBAL:
                players.addAll(Bukkit.getOnlinePlayers());
                break;
            case CLAN:
                Clan clan = LostShardPlugin.getClanManager().getClan(chattingPlayer.getUniqueId());
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

//            case LOCAL:
//                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
//                    if (!onlinePlayers.getWorld().equals(chattingPlayer.getWorld()))
//                        continue;
//                    double distance = chattingPlayer.getLocation().distance(onlinePlayers.getLocation());
//                    if (distance <= 100) {
//                        players.add(onlinePlayers);
//                    }
//                }
//                break;
            case STAFF:
                for(Player player : Staff.getOnlineStaffPlayers())
                {
                    if(!chattingPlayer.equals(player))
                    players.add(player);
                }

            case WHISPER:
                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayers.getWorld().equals(chattingPlayer.getWorld()))
                        continue;
                    double distance = chattingPlayer.getLocation().distance(onlinePlayers.getLocation());
                    if (distance <= 5) {
                        players.add(onlinePlayers);
                    }
                }
                break;
            case SHOUT:
                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayers.getWorld().equals(chattingPlayer.getWorld()))
                        continue;
                    double distance = chattingPlayer.getLocation().distance(onlinePlayers.getLocation());
                    if (distance <= 50) {
                        players.add(onlinePlayers);
                    }
                }
                break;
        }

        //check ignore
        Iterator<Player> iterator = players.iterator();
        while(iterator.hasNext())
        {
            Player player = iterator.next();
            if(LostShardPlugin.getIgnoreManager().wrap(player.getUniqueId()).isIgnoring(chattingPlayer.getUniqueId()))
                iterator.remove();
        }
        return players;
    }

}
