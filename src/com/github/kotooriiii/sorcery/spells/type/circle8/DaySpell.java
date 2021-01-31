package com.github.kotooriiii.sorcery.spells.type.circle8;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellChanneleable;
import com.github.kotooriiii.sorcery.spells.SpellToggleable;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class DaySpell extends SpellChanneleable {

    //Cooldown map
    private final static HashMap<UUID, Double> daySpellCooldownMap = new HashMap<UUID, Double>();

    private final static HashMap<UUID, DayCast> castMap= new HashMap<>();
    private final static float CAST_TIME = 2;

    private DaySpell() {
        super(SpellType.DAY, 2, 30, -1,
                "Changes the time from night to day. Must be channeled, meaning two people must cast this spell at the same time for it to work.",
                8,
                ChatColor.YELLOW,
                new ItemStack[]{new ItemStack(Material.GLOWSTONE, 1), new ItemStack(Material.REDSTONE, 1), new ItemStack(Material.OBSIDIAN)},
                2.0d,
                50,
                true, true, false,
                new SpellMonsterDrop(new EntityType[]{}, 0.00));
    }

    public class DayCast
    {
        private HashSet<UUID> set;
        private BukkitTask task;

        public DayCast(HashSet<UUID> set, BukkitTask task) {
            this.set = set;
            this.task = task;
        }

        public BukkitTask getTask() {
            return task;
        }

        public HashSet<UUID> getSet() {
            return set;
        }
    }


    //todo switch to ur name
    private  static DaySpell instance;
    public static DaySpell getInstance() {
        if (instance == null) {
            synchronized (DaySpell.class) {
                if (instance == null)
                    instance = new DaySpell();
            }
        }
        return instance;
    }

    @Override
    public boolean executeSpell(Player player) {

        player.sendMessage(ChatColor.GOLD + "You begin to channel Day...");
        return true;
    }

    @Override
    public void executeSuccessfulChannelSpell(Player player, UUID... values) {
        UUID uuid = player.getUniqueId();

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if(isCancelled())
                    return;

                final DayCast dayCast = castMap.get(uuid);
                if(dayCast == null)
                    return;

                day(player.getWorld(), values);
                displayMessage(ChatColor.DARK_GRAY + "A storm begins.", values);
                castMap.remove(uuid);
            }
        }.runTaskLater(LostShardPlugin.plugin, (long) (20 * CAST_TIME));

        removeMembers(player.getLocation());

        ArrayList<UUID> uuids = new ArrayList<>();
        for(UUID iterating_uuid : values) {
            Player player1 =Bukkit.getPlayer(iterating_uuid);
            if(player1!=null)
                player1.sendMessage(ChatColor.GOLD + "You begin to cast Day.");
            uuids.add(iterating_uuid);
        }


        castMap.put(uuid, new DayCast(new HashSet<UUID>(uuids), task));
    }

    private void day(World world, UUID[] values) {

        final int LIGHTNING_STORM_DURATION = 10, LIGHTNING_STORM_FREQUENCY = 5;
        final int[] timer = {0};

        new BukkitRunnable() {
            @Override
            public void run() {

                if(timer[0]++ * LIGHTNING_STORM_FREQUENCY >= 20*LIGHTNING_STORM_DURATION)
                {
                    this.cancel();
                    world.setTime(0);
                    displayMessage(ChatColor.GOLD + "The sun shines.", values);
                    return;
                }

                for(Player player : Bukkit.getOnlinePlayers())
                    lightning(player.getLocation(), 1);


            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, LIGHTNING_STORM_FREQUENCY);
    }

    private void lightning(Location location, int times)
    {
        if(times <= 0)
            return;

        for(int i = 0 ; i < times; i++) {
            int x = new Random().nextInt(51);
            int z = new Random().nextInt(51);

            final Block highestBlockAt = location.getWorld().getHighestBlockAt(x, z);
            highestBlockAt.getWorld().strikeLightningEffect(highestBlockAt.getLocation());
        }
    }

    @Override
    public void executeFailedChannelSpell(Player player, UUID... values) {
    }

    private void displayMessage(String message, UUID... values)
    {
        for(UUID uuid : values) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                player.sendMessage(message);
        }
    }

    public static void remove(UUID uuid, String message)
    {

        //todo debuggers
        final Iterator<Map.Entry<UUID, DayCast>> iterator = castMap.entrySet().iterator();
        while(iterator.hasNext())
        {


            final Map.Entry<UUID, DayCast> next = iterator.next();
            if(next.getValue().getSet().contains(uuid))
            {

                next.getValue().getTask().cancel();
                iterator.remove();

                if(!message.isEmpty())
                {

                    for(UUID uuid1 : next.getValue().getSet()) {
                        final Player player = Bukkit.getPlayer(uuid1);

                        if (player != null)
                        {
                            player.sendMessage(message);
                        }
                    }

                }
            }
        }

    }

    @Override
    public void updateCooldown(Player player) {
        daySpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    daySpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                daySpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (daySpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = daySpellCooldownMap.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double cooldownTimeSeconds = cooldownTimeTicks / 20;
            BigDecimal bd = new BigDecimal(cooldownTimeSeconds).setScale(1, RoundingMode.HALF_UP);
            float value = bd.floatValue();
            if (value == 0)
                value = 0.1f;

            String time = "seconds";
            if (value <= 1) {
                time = "second";
            }

            player.sendMessage(ERROR_COLOR + "You must wait " + value + " " + time + " before you can cast another spell.");
            return true;
        }
        return false;
    }

    public static HashMap<UUID, DayCast> getCastMap() {
        return castMap;
    }
}
