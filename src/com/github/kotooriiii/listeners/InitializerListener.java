package com.github.kotooriiii.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.channels.IgnorePlayer;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.ranks.RankPlayer;
import com.github.kotooriiii.ranks.RankType;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import com.github.kotooriiii.skills.SkillPlayer;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.spells.SorceryPlayer;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class InitializerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoinInit(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        register(player);
    }

    public void register(Player player) {

        registerScoreboard(player);

        registerRank(player);
        registerPlot(player);
        registerBank(player);
        registerSkill(player);
        registerMark(player);
        registerSorcery(player);

        registerStatus(player);
        registerStat(player);
        registerIgnore(player);
    }

    private void registerSorcery(Player player) {
        SorceryPlayer sorceryPlayer = LostShardPlugin.getSorceryManager().wrap(player.getUniqueId());
        if (sorceryPlayer == null) {
            sorceryPlayer = new SorceryPlayer(player.getUniqueId());
            sorceryPlayer.addSpell(SpellType.TELEPORT);
            sorceryPlayer.addSpell(SpellType.MARK);
            sorceryPlayer.addSpell(SpellType.RECALL);
            LostShardPlugin.getSorceryManager().addSorceryPlayer(sorceryPlayer, true);
            return;
        }
    }

    private void registerPlot(Player player) {
        ShardPlotPlayer shardPlotPlayer = ShardPlotPlayer.wrap(player.getUniqueId());
        if (shardPlotPlayer == null) {
            shardPlotPlayer = new ShardPlotPlayer(player.getUniqueId());
            shardPlotPlayer.add();
            shardPlotPlayer.save();
        }
    }

    public void registerBank(Player player) {
        Bank bank = LostShardPlugin.getBankManager().wrap(player.getUniqueId());
        if (bank == null) {
            bank = new Bank(player.getUniqueId(), Bukkit.createInventory(player, RankPlayer.wrap(player.getUniqueId()).getRankType().getBankInventorySize(), Bank.NAME), 0);
            LostShardPlugin.getBankManager().addBank(bank, true);
            return;
        }
    }

    public void registerRank(Player player) {
        UUID uuid = player.getUniqueId();

        if (RankPlayer.getRankPlayerMap().get(uuid) == null) {
            RankPlayer rankPlayer = new RankPlayer(uuid, RankType.DEFAULT);
            rankPlayer.save();
        }
    }

    public void registerScoreboard(Player player) {
        ShardScoreboardManager.registerScoreboard(player);
    }

    public void registerStatus(Player player) {
        UUID uuid = player.getUniqueId();

        if (StatusPlayer.getPlayerStatus().get(uuid) == null) {
            StatusPlayer statusPlayer = new StatusPlayer(uuid, Status.LAWFUL, 0);
            ShardScoreboardManager.add(player.getName(), statusPlayer.getStatus().getName());
            statusPlayer.save();
        }
    }

    public void registerMark(Player player) {
        UUID uuid = player.getUniqueId();
        MarkPlayer markPlayer = MarkPlayer.wrap(uuid);
        if (markPlayer == null) {
            markPlayer = new MarkPlayer(uuid);
            markPlayer.add();
            markPlayer.save();
        }
    }

    public void registerSkill(Player player) {
        UUID uuid = player.getUniqueId();
        SkillPlayer skillPlayer = LostShardPlugin.getSkillManager().getSkillPlayer(uuid);
        if (skillPlayer == null) {
            skillPlayer = new SkillPlayer(uuid);
            LostShardPlugin.getSkillManager().addSkillPlayer(skillPlayer, true);
        }
    }

    public void registerStat(Player player) {
        UUID uuid = player.getUniqueId();

        Stat stat = Stat.wrap(player);
        if (stat == null) {
            stat = new Stat(uuid);
            stat.add();
        }
    }

    public void registerIgnore(Player player) {
        UUID uuid = player.getUniqueId();

        IgnorePlayer ignorePlayer = LostShardPlugin.getIgnoreManager().wrap(uuid);
        if (ignorePlayer== null) {
            ignorePlayer = new IgnorePlayer(uuid);
            LostShardPlugin.getIgnoreManager().addIgnorePlayer(ignorePlayer, true);
        }
    }
}
