package com.github.kotooriiii.guards;

import com.github.kotooriiii.LostShardK;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Guard {

    static private String[] skinProperties;
    static private boolean isInit = false;
    static public ArrayList<Guard> activeGuards = new ArrayList<>();

    private final int warningRadius = 4;
    private final int alertRadius = warningRadius - 2;

    boolean isDestroyed=false;

    private EntityPlayer npc;
    private EntityArmorStand[] armorStands;
    private Location currentLocation;
    private Location guardDedicatedLocation;

    private List<org.bukkit.block.Block> boundingBlocks = new ArrayList<>();

    private BukkitTask task;

    public Guard(String name) {

        //Get the NMS server
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        //Get the BUKKIT world
        World world = Bukkit.getWorld("world");

        //Get the NMS world
        WorldServer minecraftWorld = ((CraftWorld) world).getHandle();
        //Name the Guard: GUARD=5 []=2 2colors=4 total =11
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), " ");
        //Don't spam it if we have it cached
        if (!isInit)
            skinProperties = getFromName("Shelvie");
        //Set the skin
        gameProfile.getProperties().put("textures", new Property("textures", skinProperties[0], skinProperties[1]));
        //Save object
        npc = new EntityPlayer(minecraftServer, minecraftWorld, gameProfile, new PlayerInteractManager(minecraftWorld)) {
            @Override
            public void setMot(Vec3D vec3d) {

            }

            @Override
            public void setMot(double d0, double d1, double d2) {

            }
        };

        ScoreboardTeam team = new ScoreboardTeam(new Scoreboard(), "teamname");
        IChatBaseComponent prefix = new ChatMessage(ChatColor.GRAY + "[" + "Guard" + "]");
        IChatBaseComponent suffix = new ChatMessage(ChatColor.YELLOW + name);
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        PacketPlayOutScoreboardTeam scoreboardTeamPacketInit = new PacketPlayOutScoreboardTeam(team, 0);
        this.sendPacket(scoreboardTeamPacketInit);
        ArrayList<String> playerToAdd = new ArrayList<>();
        playerToAdd.add(npc.getName());
        PacketPlayOutScoreboardTeam scoreboardTeamPacketUpdate = new PacketPlayOutScoreboardTeam(team, playerToAdd, 3);
        this.sendPacket(scoreboardTeamPacketUpdate);

        //Save the location as just the world with init coords.
        currentLocation = new Location(world, 0, 0, 0);
        //Freeze the NPC
        freezeEntity(npc);
        //Do not let anyone touch Guard
        npc.setInvulnerable(true);
        //Set as active Guard
        activeGuards.add(this);
        //Static init is true
        isInit = true;
    }

    /**
     * Spawns the Guard on given Location
     *
     * @param location The given location for the Guard to spawn
     */
    public void spawn(Location location) {
        spawn(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Spawns the Guard on the given parameters
     *
     * @param x     The block on the x coordinate
     * @param y     The block on the y coordinate
     * @param z     The block on the z coordinate
     * @param yaw   The yaw of the guard
     * @param pitch The pitch of the guard
     */
    public void spawn(int x, int y, int z, float yaw, float pitch) {
        //Set location to the center of a block.
        npc.setLocation(x + 0.5, y, z + 0.5, yaw, pitch);
        //Rotate the head. Fix it since for some reason nms has it only working of yaw
        rotateHead(yaw, pitch);
        //Packet for playing out a player
        PacketPlayOutPlayerInfo playerDeclarePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc);
        //Spawn the player
        PacketPlayOutNamedEntitySpawn playerSpawnPacket = new PacketPlayOutNamedEntitySpawn(npc);


        //Send packets
        sendPacket(playerDeclarePacket);
        sendPacket(playerSpawnPacket);


        //Update location, remember the world is here already!
        guardDedicatedLocation = new Location(currentLocation.getWorld(), x + 0.5, y, z + 0.5, yaw, pitch);
        currentLocation = guardDedicatedLocation;

        //Points to all corners
        this.armorStands = new EntityArmorStand[4];

        armorStands[0] = new EntityArmorStand(((CraftWorld) currentLocation.getWorld()).getHandle(), currentLocation.getBlockX() + 0.75, currentLocation.getBlockY(), currentLocation.getBlockZ() + 0.75); //max max,max
        armorStands[1] = new EntityArmorStand(((CraftWorld) currentLocation.getWorld()).getHandle(), currentLocation.getBlockX() + 0.75, currentLocation.getBlockY(), currentLocation.getBlockZ() + 0.25); //combo max,min
        armorStands[2] = new EntityArmorStand(((CraftWorld) currentLocation.getWorld()).getHandle(), currentLocation.getBlockX() + 0.25, currentLocation.getBlockY(), currentLocation.getBlockZ() + 0.75); //combo min,max
        armorStands[3] = new EntityArmorStand(((CraftWorld) currentLocation.getWorld()).getHandle(), currentLocation.getBlockX() + 0.25, currentLocation.getBlockY(), currentLocation.getBlockZ() + 0.25); //min min,min

        //EntityArmorStand init
        for (EntityArmorStand armorStand : armorStands) {
            armorStand.setNoGravity(true);//float
            armorStand.setInvulnerable(false);//make sure its false so events are called
            armorStand.setInvisible(false); //FIX THIS AT COMPILETIME
            freezeEntity(armorStand); //FREEZE entity cannot move this.
            ((CraftWorld) currentLocation.getWorld()).addEntity(armorStand, CreatureSpawnEvent.SpawnReason.CUSTOM); //Add entity to the world. get out of packets and put to server
        }

        //Delay and then remove from Tablist
        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutPlayerInfo playerRedeclarePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc);
                sendPacket(playerRedeclarePacket);
            }
        }.runTaskLater(LostShardK.plugin, 40);
    }

    /**
     * Fix rotation of the Guard's head
     *
     * @param yaw
     * @param pitch
     */
    public void rotateHead(float yaw, float pitch) {
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(this.npc.getId(), getFixRotation(yaw), (byte) pitch, true);
        PacketPlayOutEntityHeadRotation packet_1 = new PacketPlayOutEntityHeadRotation();
        this.setField(packet_1, "a", this.npc.getId());
        this.setField(packet_1, "b", getFixRotation(yaw));
        this.sendPacket(packet);
        this.sendPacket(packet_1);
    }

    private byte getFixRotation(float yawpitch) {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }

    /**
     * Destroys the Guard
     */
    public void destroy() {

        //Clear last ones
        for (Block block : boundingBlocks) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
            }
        }

        PacketPlayOutEntityDestroy playerDestroyPacket = new PacketPlayOutEntityDestroy(npc.getId()); //Create packet to destroy guard
        sendPacket(playerDestroyPacket); //Send packet
        for (EntityArmorStand armorStand : armorStands) //loop all target points
        {
            PacketPlayOutEntityDestroy playerDestroyPacket2 = new PacketPlayOutEntityDestroy(armorStand.getId()); //Create destroy packet
            sendPacket(playerDestroyPacket2); //Destroy
            armorStand.killEntity(); //Since it's also alive in memory on the server, kill it.
        }

        isDestroyed=true;
        if (task != null)
            task.cancel();
        activeGuards.remove(this); //Remove as active guard
    }

    /**
     * Teleports the Guard
     *
     * @param location The location where the Guard will teleport to
     * @param onGround Whether is onGround when teleport
     */
    public void teleport(Location location, boolean onGround) {

        //Teleport the target points
        this.currentLocation = location;
        rotateHead(location.getYaw(), location.getPitch());

        //Teleport the player
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        setField(packet, "a", npc.getId());
        setField(packet, "b", location.getBlockX() + 0.5);
        setField(packet, "c", location.getBlockY());
        setField(packet, "d", location.getBlockZ() + 0.5);
        setField(packet, "e", (byte) location.getYaw());
        setField(packet, "f", (byte) location.getPitch());
        setField(packet, "g", onGround);
        sendPacket(packet);

        armorStands[0].setLocation(location.getBlockX() + 0.75, location.getBlockY(), location.getBlockZ() + 0.75, location.getYaw(), location.getPitch());
        armorStands[1].setLocation(location.getBlockX() + 0.75, location.getBlockY(), location.getBlockZ() + 0.25, location.getYaw(), location.getPitch()); //combo max,min
        armorStands[2].setLocation(location.getBlockX() + 0.25, location.getBlockY(), location.getBlockZ() + 0.75, location.getYaw(), location.getPitch()); //combo min,max
        armorStands[3].setLocation(location.getBlockX() + 0.25, location.getBlockY(), location.getBlockZ() + 0.25, location.getYaw(), location.getPitch()); //min min,min
        showBounds();
    }

    public void teleportKill(final Player player, Location loc, boolean onGround) {
        World w = player.getWorld();
        w.playSound(loc, Sound.ENTITY_ENDERMAN_SCREAM, 10, 0);
        w.spawnParticle(Particle.TOTEM, this.currentLocation, 20, 0.3, 0.3, 0.3);
        Vector previousVelocity = player.getVelocity();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15, 127, false, false, false));

        final Location playerLocation = player.getLocation();

        new BukkitRunnable() {
            @Override
            public void run() {
                teleport(loc, onGround);
                w.spawnParticle(Particle.CRIT, playerLocation, 50, 0.3, 0.3, 0.3);
                w.spawnParticle(Particle.DRAGON_BREATH, playerLocation, 50, 2, 2, 2);

                w.playSound(playerLocation, Sound.ENTITY_DROWNED_DEATH, 10, 0);
                if (player.isOnline())
                    player.setHealth(1);
                player.setVelocity(previousVelocity);
            }
        }.runTaskLater(LostShardK.plugin, 15);

        new BukkitRunnable() {
            @Override
            public void run() {
                w.playSound(playerLocation, Sound.ENTITY_PLAYER_BURP, 10, 0);
            }
        }.runTaskLater(LostShardK.plugin, 35);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(isDestroyed)
                    return;
                teleport(guardDedicatedLocation, true);
                checkForEnemy();
            }
        }.runTaskLater(LostShardK.plugin, 70);
    }

    private Object getField(Object obj, String field_name) {
        try {
            Field field = obj.getClass().getDeclaredField(field_name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setField(Object obj, String field_name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(field_name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private void sendPacket(Packet<?> packet) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.sendPacket(packet, p);
        }
    }

    private String[] getFromPlayer(Player playerBukkit) {
        EntityPlayer playerNMS = ((CraftPlayer) playerBukkit).getHandle();
        GameProfile profile = playerNMS.getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();
        return new String[]{texture, signature};
    }

    private String[] getFromName(String name) {
        try {
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
            String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new String[]{texture, signature};
        } catch (IOException e) {
            System.err.println("Could not get skin data from session servers!");
            e.printStackTrace();
            return null;
        }
    }

    private List<org.bukkit.block.Block> getAlertBlocks() {
        List<org.bukkit.block.Block> blocks = new ArrayList<>();

        for (int x = currentLocation.getBlockX() - alertRadius; x <= currentLocation.getBlockX() + alertRadius; x++) {
            for (int z = currentLocation.getBlockZ() - alertRadius; z <= currentLocation.getBlockZ() + alertRadius; z++) {
                yLoop:
                for (int y = currentLocation.getBlockY() + 3; y >= warningRadius; y--) {
                    Block currentBlock = new Location(currentLocation.getWorld(), x, y, z).getBlock();
                    Block blockBelow = new Location(currentLocation.getWorld(), x, y - 1, z).getBlock();

                    if (currentBlock.getType().equals(Material.AIR) && !blockBelow.getType().equals(Material.AIR)) {
                        blocks.add(currentBlock);
                        break yLoop;
                    }
                }
            }
        }

        return blocks;
    }

    private List<org.bukkit.block.Block> getWarningBlocks() {
        List<org.bukkit.block.Block> blocks = new ArrayList<>();

        //top
        for (int x = currentLocation.getBlockX() - warningRadius; x <= currentLocation.getBlockX() + warningRadius; x++) {
            for (int z = currentLocation.getBlockZ() + alertRadius + 1; z <= currentLocation.getBlockZ() + warningRadius; z++) {
                yLoop:
                for (int y = currentLocation.getBlockY() + 3; y >= warningRadius; y--) {
                    Block currentBlock = new Location(currentLocation.getWorld(), x, y, z).getBlock();
                    Block blockBelow = new Location(currentLocation.getWorld(), x, y - 1, z).getBlock();

                    if (currentBlock.getType().equals(Material.AIR) && !blockBelow.getType().equals(Material.AIR)) {
                        blocks.add(currentBlock);
                        break yLoop;
                    }
                }
            }
        }


        //left block
        for (int x = currentLocation.getBlockX() - warningRadius; x < currentLocation.getBlockX() - alertRadius; x++) {
            for (int z = currentLocation.getBlockZ() - alertRadius; z <= currentLocation.getBlockZ() + alertRadius; z++) {
                yLoop:
                for (int y = currentLocation.getBlockY() + 3; y >= warningRadius; y--) {
                    Block currentBlock = new Location(currentLocation.getWorld(), x, y, z).getBlock();
                    Block blockBelow = new Location(currentLocation.getWorld(), x, y - 1, z).getBlock();

                    if (currentBlock.getType().equals(Material.AIR) && !blockBelow.getType().equals(Material.AIR)) {
                        blocks.add(currentBlock);
                        break yLoop;
                    }
                }
            }
        }

        //right block
        for (int x = currentLocation.getBlockX() + alertRadius + 1; x <= currentLocation.getBlockX() + warningRadius; x++) {
            for (int z = currentLocation.getBlockZ() - alertRadius; z <= currentLocation.getBlockZ() + alertRadius; z++) {
                yLoop:
                for (int y = currentLocation.getBlockY() + 3; y >= warningRadius; y--) {
                    Block currentBlock = new Location(currentLocation.getWorld(), x, y, z).getBlock();
                    Block blockBelow = new Location(currentLocation.getWorld(), x, y - 1, z).getBlock();

                    if (currentBlock.getType().equals(Material.AIR) && !blockBelow.getType().equals(Material.AIR)) {
                        blocks.add(currentBlock);
                        break yLoop;
                    }
                }
            }
        }

        for (int x = currentLocation.getBlockX() - warningRadius; x <= currentLocation.getBlockX() + warningRadius; x++) {
            for (int z = currentLocation.getBlockZ() - warningRadius; z < currentLocation.getBlockZ() - alertRadius; z++) {
                yLoop:
                for (int y = currentLocation.getBlockY() + 3; y >= warningRadius; y--) {
                    Block currentBlock = new Location(currentLocation.getWorld(), x, y, z).getBlock();
                    Block blockBelow = new Location(currentLocation.getWorld(), x, y - 1, z).getBlock();

                    if (currentBlock.getType().equals(Material.AIR) && !blockBelow.getType().equals(Material.AIR)) {
                        blocks.add(currentBlock);
                        break yLoop;
                    }
                }
            }
        }

        return blocks;
    }

    private void freezeEntity(Entity nmsEn) {
        NBTTagCompound compound = new NBTTagCompound();
        nmsEn.c(compound);
        compound.setByte("NoAI", (byte) 1);
        nmsEn.f(compound);
    }

    public void showBounds() {

        //Clear last ones
        for (Block block : boundingBlocks) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
            }
        }

        List<org.bukkit.block.Block> alertingBlocks = getAlertBlocks();
        List<org.bukkit.block.Block> warningBlocks = getWarningBlocks();

        List<org.bukkit.block.Block> all = new ArrayList<>();
        all.addAll(getAlertBlocks());
        all.addAll(getWarningBlocks());
        boundingBlocks = all;

        for (Player players : Bukkit.getOnlinePlayers()) {
            for (Block block : (alertingBlocks)) {
                double ran = Math.random();
                if (ran <= 0.20)
                    players.spawnParticle(Particle.FLAME, block.getX(), block.getY(), block.getZ(), 1);
                players.sendBlockChange(block.getLocation(), Material.RED_CARPET.createBlockData());

            }

            for (Block block : warningBlocks) {
                // players.spawnParticle(Particle.TOTEM, block.getX(), block.getY(), block.getZ(), 1);
                players.sendBlockChange(block.getLocation(), Material.YELLOW_CARPET.createBlockData());
            }
        }
    }

    public boolean isId(int id) {
        for (EntityArmorStand armorStand : armorStands) {
            Bukkit.broadcastMessage("iterating: " + armorStand.getId() + " against " + id);
            if (armorStand.getId() == id)
                return true;
        }
        return false;
    }

    public void checkForEnemy() {
        task = new BukkitRunnable() {
            @Override
            public void run() {

                for (org.bukkit.entity.Entity entity : currentLocation.getWorld().getNearbyEntities(currentLocation, warningRadius, warningRadius, warningRadius)) {
                    if (entity instanceof Player) {
                        if (!"is not in order".isEmpty()) {
                            Player player = (Player) entity;

                            Location loc = entity.getLocation();

                            double xDiff = loc.getX() - currentLocation.getX();
                            double yDiff = loc.getY() - currentLocation.getY();
                            double zDiff = loc.getZ() - currentLocation.getZ();

                            int xIntDiff = loc.getBlockX() - currentLocation.getBlockX();
                            int yIntDiff = loc.getBlockY() - currentLocation.getBlockY();
                            int zIntDiff = loc.getBlockZ() - currentLocation.getBlockZ();

                            double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
                            double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
                            double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI;
                            double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
                            if (zDiff < 0.0)
                                newYaw = newYaw + Math.abs(180 - newYaw) * 2;
                            newYaw = (newYaw - 90);

                            float yaw = (float) newYaw;
                            float pitch = (float) newPitch;
                            if (Math.abs(xIntDiff) <= alertRadius && Math.abs(yIntDiff) <= alertRadius && Math.abs(zIntDiff) <= alertRadius) {
                                Bukkit.broadcastMessage("inAlertZone");
                                teleportKill(player, new Location(entity.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), yaw, pitch), true);
                                this.cancel();
                                return;
                            } else {
                                Bukkit.broadcastMessage("inWarningZone");
                                rotateHead(yaw, pitch);
                                int randomInt = new Random().nextInt(3);
                                double randomChance = Math.random();
                                if (randomInt != 0) {
                                    if (randomChance <= 0.03)
                                        player.spawnParticle(Particle.VILLAGER_ANGRY, currentLocation.getBlockX() + 0.5, currentLocation.getBlockY() + 2, currentLocation.getBlockZ() + 0.5, randomInt, 0.25, 0, 0.25);
                                    //player.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, currentLocation.getBlockX()+0.5, currentLocation.getBlockY(), currentLocation.getBlockZ()+0.5, 1, 1, 1, 1);
                                    player.spawnParticle(Particle.SMOKE_NORMAL, currentLocation.getBlockX() + 0.5, currentLocation.getBlockY() + 1, currentLocation.getBlockZ() + 0.5, 1, 0, 0, 0);
                                    //   player.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, currentLocation.getBlockX()+0.5, currentLocation.getBlockY(), currentLocation.getBlockZ()+0.5, 1, -1, 1, 1);
                                }

                            }
                        }
                    }
                }

            }
        }.runTaskTimer(LostShardK.plugin, 5, 1);
    }
}
