package com.github.kotooriiii.sorcery.spells.type;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.clans.Clan;
import com.github.kotooriiii.sorcery.marks.MarkPlayer;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class ClanTPSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> clanTpSpellCooldownMap = new HashMap<>();
    private final static HashMap<UUID, Integer> waitingToRecallMap = new HashMap<>();


    public ClanTPSpell() {
        super(SpellType.CLANTP, ChatColor.GREEN,  new ItemStack[]{new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.FEATHER, 1)}, 2.0f , 15, true,  true, false);
    }

    @EventHandler
    public void onChatArg(ShardChatEvent event)
    {
        Player player = event.getPlayer();

        SpellType type = waitingForArgumentMap.get(player.getUniqueId());
        if (type == null)
            return;
        if (!type.equals(getType()))
            return;

        waitingForArgumentMap.remove(player.getUniqueId());
        receiveArgument(player, event.getMessage());
        event.setCancelled(true);

    }

    @EventHandler
    public void onWaitToRecall(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        //not casting spell
        if (!waitingToRecallMap.containsKey(player.getUniqueId()))
            return;


        int fX = event.getFrom().getBlockX();
        int fY = event.getFrom().getBlockY();
        int fZ = event.getFrom().getBlockZ();

        int tX = event.getTo().getBlockX();
        int tY = event.getTo().getBlockY();
        int tZ = event.getTo().getBlockZ();

        if (fX == tX && fY == tY && fZ == tZ)
            return;

        //Is casting a spell and moved a block

        player.sendMessage(ERROR_COLOR + "Your spell was interrupted due to movement.");
        waitingToRecallMap.remove(player.getUniqueId());
    }

    @EventHandler
    public void onWaitToRecall(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if(!(entity instanceof Player))
            return;
        Player player = (Player) entity;



        //not casting spell
        if (!waitingToRecallMap.containsKey(player.getUniqueId()))
            return;


        //Is casting a spell and moved a block

        player.sendMessage(ERROR_COLOR + "Your spell was interrupted due to damage.");
        waitingToRecallMap.remove(player.getUniqueId());
    }


    @Override
    public void updateCooldown(Player player)
    {
        clanTpSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    clanTpSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                clanTpSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (clanTpSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = clanTpSpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(0, RoundingMode.UP);
            int value = bd.intValue();
            if (value == 0)
                value = 1;

            String time = "seconds";
            if (value == 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }

    @Override
    public boolean executeSpell(Player player) {
        waitingForArgumentMap.put(player.getUniqueId(), this.getType());
        player.sendMessage(ChatColor.AQUA + "Who would you like to teleport to?");
        return true;
    }


    /**
     * after the successful tp
     * @return
     */
    private void postCast(Player playerSender, UUID clanMemberPlayerUUID, String justInCaseNameTheyLogOut)
    {

        Player clanMemberPlayer = Bukkit.getPlayer(clanMemberPlayerUUID);

        if(clanMemberPlayer == null || !clanMemberPlayer.isOnline())
        {
            playerSender.sendMessage(ERROR_COLOR + "Player has logged out, can not clan teleport to " + justInCaseNameTheyLogOut);
            return;
        }

        playerSender.teleport(clanMemberPlayer.getLocation());
        playerSender.sendMessage(ChatColor.GOLD + "You have recalled to \"" + clanMemberPlayer.getName() + "\".");
        clanMemberPlayer.getLocation().getWorld().strikeLightningEffect(clanMemberPlayer.getLocation());
        Stat stat = Stat.wrap(playerSender.getUniqueId());
        stat.setMana(0);
        stat.setStamina(0);

    }

    /**
     * recalls a player to a mark
     */
    private void receiveArgument(Player playerSender, String message) {
        if (playerSender == null || playerSender.isDead() || !playerSender.isOnline())
            return;

        if (!hasClanTPRequirements(playerSender, message))
            return;

        Player clanMemberPlayer = Bukkit.getPlayer(message);

        //success
        clantp(playerSender, clanMemberPlayer);
    }


    /**
     *
     */
    private void clantp(Player player, Player clanMemberPlayer) {

        final int WAITING_TO_RECALL_PERIOD = 3;
        player.sendMessage(ChatColor.GOLD + "You begin to clan teleport to \"" + clanMemberPlayer.getName() + "\"...");
        waitingToRecallMap.put(player.getUniqueId(), WAITING_TO_RECALL_PERIOD);

        new BukkitRunnable() {
            int counter = WAITING_TO_RECALL_PERIOD;

            @Override
            public void run() {


                if (!waitingToRecallMap.containsKey(player.getUniqueId())) {
                    this.cancel();
                    return;
                }


                if (counter == 0) {
                    this.cancel();
                    waitingToRecallMap.remove(player.getUniqueId());
                    postCast(player, clanMemberPlayer.getUniqueId(), clanMemberPlayer.getName());
                    return;
                }

                counter--;
                waitingToRecallMap.put(player.getUniqueId(), counter);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 20);
    }


    /**
     * checks if you are able to teleport to this supposed player
     * @param playerSender
     * @return
     */
    private boolean hasClanTPRequirements(Player playerSender, String name) {

        final UUID playerUUID = playerSender.getUniqueId();

        Clan clan = LostShardPlugin.getClanManager().getClan(playerUUID);

        Player clanMemberPlayer = Bukkit.getPlayer(name);

        if (clan == null) {
            playerSender.sendMessage(ERROR_COLOR + "You are not in a clan.");
            return false;
        }


        if (clanMemberPlayer == null || !clanMemberPlayer.isOnline()) {
            playerSender.sendMessage(ERROR_COLOR + "The player is not online.");
            return false;
        }

        if (!clan.isInThisClan(clanMemberPlayer.getUniqueId())) {
            playerSender.sendMessage(ERROR_COLOR + "The player is not in your clan.");
            return false;
        }

        if(Stat.wrap(clanMemberPlayer.getUniqueId()).isPrivate())
        {
            playerSender.sendMessage(ChatColor.GOLD + "Can't teleport to that player, they are set to private");
            return false;
        }

        return true;
    }

}
