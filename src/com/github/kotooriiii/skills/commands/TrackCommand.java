package com.github.kotooriiii.skills.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.scoreboard.ShardScoreboardManager;
import com.github.kotooriiii.skills.Skill;
import com.github.kotooriiii.skills.events.EntityTrackEvent;
import com.github.kotooriiii.skills.events.PlayerTrackEvent;
import com.github.kotooriiii.skills.skill_listeners.SurvivalismListener;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.util.HelperMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.xezard.glow.data.glow.Glow;

import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class TrackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player))
            return false;

        final Player playerSender = (Player) sender;
        final UUID playerUUID = playerSender.getUniqueId();

        if (cmd.getName().equalsIgnoreCase("track")) {
            if (args.length == 0) {
                // .track
                playerSender.sendMessage(ERROR_COLOR + "You must enter either a mobname or playername to track.");
                return false;
            }

            String mobName = HelperMethods.stringBuilder(args, 0, "_").toUpperCase();
            EntityType type = null;
            try {
                type = EntityType.valueOf(mobName);
            } catch (IllegalArgumentException e) {
                //Not a mob, it's okay we nullified by default.
            }

            Player trackedPlayer = null;
            //No mob name
            if (type == null) {

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(mobName);
                if (!offlinePlayer.isOnline()) {
                    playerSender.sendMessage(ERROR_COLOR + "The mob name or player name could not be found.");
                    return false;
                } else {
                    type = EntityType.PLAYER;
                    trackedPlayer = offlinePlayer.getPlayer();
                }
            }

            Skill survivalism = LostShardPlugin.getSkillManager().getSkillPlayer(playerUUID).getActiveBuild().getSurvivalism();
            int level = (int) survivalism.getLevel();


            if (type == EntityType.PLAYER) {

                if (level < 100) {
                    playerSender.sendMessage(ERROR_COLOR + "You must be at least level 100 for this perk.");
                    return false;
                }

                final int STAMINA_COST = 25;

                if (!hasStamina(playerSender, STAMINA_COST)) {
                    playerSender.sendMessage(ERROR_COLOR + "You need at least " + STAMINA_COST + " stamina to track.");
                    return false;
                }

                removeStamina(playerSender, STAMINA_COST);

                //Random chance
                double playerSuccessfullyTracksRandom = Math.random();
                //Get tracked player's level
                int trackedPlayerLevel = (int) LostShardPlugin.getSkillManager().getSkillPlayer(trackedPlayer.getUniqueId()).getActiveBuild().getSurvivalism().getLevel();
                //Chance from level
                double requirementChance = -1;
                if (trackedPlayerLevel >= 100)
                    requirementChance = 0.25;
                else if (75 <= trackedPlayerLevel && trackedPlayerLevel < 100)
                    requirementChance = 0.35;
                else if (50 <= trackedPlayerLevel && trackedPlayerLevel < 75)
                    requirementChance = 0.45;
                else if (25 <= trackedPlayerLevel && trackedPlayerLevel < 50)
                    requirementChance = 0.6;
                else if (trackedPlayerLevel < 25)
                    requirementChance = 0.75;

                survivalism.addXP(SurvivalismListener.TRACKING_XP);

                if (playerSuccessfullyTracksRandom > requirementChance) {
                    playerSender.sendMessage(ERROR_COLOR + "You have failed to track the player.");
                    return false;
                }

                if (trackedPlayer.isDead()) {
                    playerSender.sendMessage(ERROR_COLOR + "The player is dead...");
                    return false;
                }


                double chanceToBeTracked = -1;
                if (trackedPlayerLevel >= 100)
                    chanceToBeTracked = 0.8;
                else if (75 <= trackedPlayerLevel && trackedPlayerLevel < 100)
                    chanceToBeTracked = 0.6;
                else if (50 <= trackedPlayerLevel && trackedPlayerLevel < 75)
                    chanceToBeTracked = 0.4;
                else if (25 <= trackedPlayerLevel && trackedPlayerLevel < 50)
                    chanceToBeTracked = 0.2;
                else if (trackedPlayerLevel < 25)
                    chanceToBeTracked = 0;

                double playerKnowsRandom = Math.random();

                if (playerKnowsRandom <= chanceToBeTracked) {
                    trackedPlayer.sendMessage(ChatColor.GRAY + "The hairs on the back of your neck stand up.");
                }


                PlayerTrackEvent event = new PlayerTrackEvent(playerSender, trackedPlayer);
                LostShardPlugin.plugin.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled())
                    return false;


                Glow glow = Glow.builder()
                        .animatedColor(ChatColor.YELLOW)
                        .name("Track")
                        .build();
                glow.addHolders(trackedPlayer);
                glow.display(playerSender);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        glow.destroy();
                        if(playerSender.isOnline())
                            ShardScoreboardManager.registerScoreboard(playerSender);

                    }
                }.runTaskLater(LostShardPlugin.plugin, 20*1);


                String direction = getCompassDirection(playerSender, trackedPlayer.getLocation());
                if (direction == null) {
                    playerSender.sendMessage(ChatColor.GOLD + trackedPlayer.getName() + " seems to be in another dimension...");
                } else {
                    direction = direction.substring(0, 1).toUpperCase() + direction.substring(1).toLowerCase();
                    playerSender.sendMessage(ChatColor.GOLD + "You see tracks leading off to the " + direction + "...");
                    playerSender.sendMessage(ChatColor.GOLD + howClose(trackedPlayer.getLocation().distance(playerSender.getLocation())));
                }

            } else {
                if (level < 50) {
                    playerSender.sendMessage(ERROR_COLOR + "You must be at least level 50 for this perk.");
                    return false;
                }

                final int stamina = 25;

                if (!hasStamina(playerSender, stamina)) {
                    playerSender.sendMessage(ERROR_COLOR + "You need at least " + stamina + " stamina to track.");
                    return false;
                }

                double distance = Double.MAX_VALUE;
                Entity closestEntity = null;

                boolean isTutorialTrack = LostShardPlugin.isTutorial() && type == EntityType.SPIDER;
                Location spiderLoc = null;

                if (isTutorialTrack) {
                    spiderLoc = new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), 300, 53, 682);
                    distance = spiderLoc.distance(playerSender.getLocation());
                } else {
                    for (Entity entity : playerSender.getLocation().getWorld().getNearbyEntities(playerSender.getLocation(), 2000, 256, 2000)) {
                        if (entity.getType().equals(type)) {
                            double entityDistance = entity.getLocation().distance(playerSender.getLocation());
                            if (entityDistance < distance) {
                                distance = entityDistance;
                                closestEntity = entity;
                            }
                        }
                    }
                }


                //if its null we got to make sure its tutorial. if its not tutorial and its null then we dont know
                if (closestEntity == null && !isTutorialTrack) {
                    String name = type.getKey().getKey().toLowerCase().replace("_", " ");
                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                    playerSender.sendMessage(ERROR_COLOR + "We could not find a " + name + " close to your location.");
                    return false;
                }

                EntityTrackEvent event;
                if (LostShardPlugin.isTutorial())
                    event = new EntityTrackEvent(playerSender, null, EntityType.SPIDER);
                else
                    event = new EntityTrackEvent(playerSender, closestEntity, closestEntity.getType());
                LostShardPlugin.plugin.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled())
                    return false;

                survivalism.addXP(SurvivalismListener.TRACKING_XP);
                removeStamina(playerSender, stamina);
                String direction;
                if (LostShardPlugin.isTutorial())
                    direction = getCompassDirection(playerSender, spiderLoc);
                else
                    direction = getCompassDirection(playerSender, closestEntity.getLocation());
                direction = direction.substring(0, 1).toUpperCase() + direction.substring(1).toLowerCase();
                playerSender.sendMessage(ChatColor.GOLD + "You see tracks leading off to the " + direction + "...");
                playerSender.sendMessage(ChatColor.GOLD + howClose(distance));
                //Successful look up
            }


        }

        return true;
    }

    public String howClose(double distance) {
        String proximity = "null";
        if (distance >= 1000)

            proximity = "The tracks are very faint.";
        else if (500 <= distance && distance < 1000)
            proximity = "The tracks aren't very fresh.";
        else if (200 <= distance && distance < 500)
            proximity = "The tracks are somewhat fresh.";
        else if (distance < 200)
            proximity = "The tracks are very fresh.";
        return proximity;
    }

    public String getCompassDirection(Player player, Location location) {

        if (!player.getLocation().getWorld().equals(location.getWorld())) {
            return null;
        }

        Vector playerVector = player.getLocation().toVector();
        Vector targetVector = location.toVector();
        Vector angleVector = targetVector.subtract(playerVector);

        double angle = Math.atan2(angleVector.getX(), angleVector.getZ()); // Math.atan(angleVector.getX()/(angleVector.getZ()*-1));

        double yaw = (angle * 180) / Math.PI;

        yaw += 270;

        if (yaw > 360) {
            yaw = yaw - 360;
        }


        String compassDir = "null";


        if (0 <= yaw && yaw < 22.5) {
            compassDir = "EAST";
        } else if (22.5 <= yaw && yaw < 67.5) {
            compassDir = "NORTH-EAST";
        } else if (67.5 <= yaw && yaw < 112.5) {
            compassDir = "NORTH";
        } else if (112.5 <= yaw && yaw < 157.5) {
            compassDir = "NORTH-WEST";
        } else if (157.5 <= yaw && yaw < 202.5) {
            compassDir = "WEST";
        } else if (202.5 <= yaw && yaw < 247.5) {
            compassDir = "SOUTH-WEST";
        } else if (247.5 <= yaw && yaw < 292.5) {
            compassDir = "SOUTH";
        } else if (292.5 <= yaw && yaw < 337.5) {
            compassDir = "SOUTH-EAST";
        } else if (337.5 <= yaw && yaw <= 360) {
            compassDir = "EAST";
        }

        return compassDir.replace("-", "");
    }

    public boolean hasStamina(Player player, double stamina) {
        return Stat.wrap(player.getUniqueId()).getStamina() >= stamina;
    }

    public void removeStamina(Player player, double stamina) {
        Stat stat = Stat.wrap(player.getUniqueId());
        stat.setStamina(stat.getStamina() - stamina);
    }

}
