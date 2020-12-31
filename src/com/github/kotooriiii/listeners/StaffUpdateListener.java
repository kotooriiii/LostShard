package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.status.StaffType;
import com.github.kotooriiii.status.StatusPlayer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.log.LogPublishEvent;
import net.luckperms.api.event.user.UserLoadEvent;
import net.luckperms.api.event.user.track.UserDemoteEvent;
import net.luckperms.api.event.user.track.UserPromoteEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.material.Banner;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class StaffUpdateListener implements Listener {

    private static ArrayList<EventSubscription> subscriptions = new ArrayList<>();


    public StaffUpdateListener(LuckPerms api) {

        // get the LuckPerms event bus
        EventBus eventBus = api.getEventBus();
        // subscribe to an event using a lambda
        eventBus.subscribe(LogPublishEvent.class, e -> e.setCancelled(true));

        // subscribe to an event using a method reference
        eventBus.subscribe(UserPromoteEvent.class, this::staffPromote);
        eventBus.subscribe(UserDemoteEvent.class, this::staffDemote);
        eventBus.subscribe(UserLoadEvent.class, this::staffJoin);

    }

    @EventHandler
    public void staffPromote(UserPromoteEvent event) {
        User user = event.getUser();
        UUID playerUUID = user.getUniqueId();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);


        Optional<String> group = event.getGroupTo();
        StaffType type = StaffType.matchStaffType(group.get());
        if (type == null) return;
        Staff staff = new Staff(user.getUniqueId(), type);

        ShardScoreboardManager.add(offlinePlayer, type.getName());
    }

    @EventHandler
    public void staffDemote(UserDemoteEvent event) {
        User user = event.getUser();
        UUID playerUUID = user.getUniqueId();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);

        Optional<String> group = event.getGroupTo();
        Optional<String> groupFrom = event.getGroupFrom();

        StaffType type = StaffType.matchStaffType(group.get());
        StaffType typeFrom = StaffType.matchStaffType(groupFrom.get());

        if (type == null && typeFrom != null) {
            Staff staff = Staff.wrap(playerUUID);
            Staff.remove(staff);
            StatusPlayer statusPlayer = StatusPlayer.wrap(playerUUID);

            ShardScoreboardManager.add(offlinePlayer, statusPlayer.getStatus().getName());

        }
    }

    @EventHandler
    public void staffJoin(UserLoadEvent event) {
        User user = event.getUser();
        UUID playerUUID = user.getUniqueId();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        if (offlinePlayer.isOnline())
            return;
        String name = user.getPrimaryGroup();
        StaffType type = StaffType.matchStaffType(name);
        if (type == null) return;

        Staff staff = new Staff(playerUUID, type);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void staffOverwrite(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            if (Staff.isStaff(event.getPlayer().getUniqueId()))
                event.allow();
            if (RankPlayer.wrap(event.getPlayer().getUniqueId()) != null && RankPlayer.wrap(event.getPlayer().getUniqueId()).isDonator())
                event.allow();
        }
    }


    @EventHandler
    public void staffLoad(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();


        if (!Staff.isStaff(playerUUID))
            return;
        Staff staff = Staff.wrap(playerUUID);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);


        ShardScoreboardManager.add(offlinePlayer, staff.getType().getName());
    }

    public static ArrayList<EventSubscription> getSubscriptions() {
        return subscriptions;
    }

    public static void removeSubscriptions(LuckPerms api) {
    }
}
