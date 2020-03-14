package com.github.kotooriiii.guards;

import com.github.kotooriiii.LostShardK;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ShardNPC extends ShardNMS {
    //NPC Skin Cache
    static private HashMap<String, String[]> skinProperties = new HashMap<>(10);

    //State variables
    /**
     * If the ShardNPC still exists
     */
    private boolean isDestroyed = false;
    /**
     * If the ShardNPC is spawned.
     */
    private boolean isSpawned = false;

    /**
     * The current location of the NPC
     */
    private Location currentLocation;

    /**
     * The NPC entity we are working with
     */
    private final EntityPlayer npc;
    /**
     * The hitboxes the npc has
     */
    private EntityArmorStand[] armorStands;

    /**
     * Name of NPC
     */
    private String name;
    private String prefix;

    /**
     * Creates a new NPC with the given name and following skin.
     *
     * @param name The name of the NPC
     * @param skin The skin of the NPC
     */
    public ShardNPC(String prefix, String name, Skin skin) {

        //Get the NMS server
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        //Get the BUKKIT world
        World world = Bukkit.getWorld("world");

        //Get the NMS world
        WorldServer minecraftWorld = ((CraftWorld) world).getHandle();
        //Name the Guard: GUARD=5 []=2 2colors=4 total =11
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), " ");
        //Don't spam it if we have it cached, tell player that if it has been spammed.

        /*Old code for http requests
        String skinName = skin.getValue();

        //ShardNPC.addSkin(skinName);
        //LostShardK.logger.info(skinProperties.get(skinName)[0] + " " +skinProperties.get(skinName)[1]);
        //Set the skin
        */
        gameProfile.getProperties().put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));


        //Save object
        npc = new EntityPlayer(minecraftServer, minecraftWorld, gameProfile, new PlayerInteractManager(minecraftWorld)) {
            @Override
            public void setMot(Vec3D vec3d) {

            }

            @Override
            public void setMot(double d0, double d1, double d2) {

            }

            @Override
            public void collide(Entity e) {
                if (e instanceof Player) {
                    return;
                }
                super.collide(e);
            }
        };

        //Save the location as just the world with init coords.
        setCurrentLocation(new Location(world, 0, 0, 0));
        //Freeze the NPC
        freezeEntity(npc);
        //Do not let anyone touch Guard
        npc.setInvulnerable(true);

        setName(prefix, name);
    }


    public boolean setName(String prefix, String name) {

        //Name
        ScoreboardTeam team = new ScoreboardTeam(new Scoreboard(), "arbitrary");
        IChatBaseComponent prefixComponent = new ChatMessage(prefix);
        IChatBaseComponent suffixComponent = new ChatMessage(ChatColor.YELLOW + name);
        team.setPrefix(prefixComponent);
        team.setSuffix(suffixComponent);
        this.name = ChatColor.stripColor(name);
        this.prefix = prefix;

        //Adding to Scoreboard
        PacketPlayOutScoreboardTeam scoreboardTeamPacketInit = new PacketPlayOutScoreboardTeam(team, 0);
        this.sendPacket(scoreboardTeamPacketInit);
        ArrayList<String> playerToAdd = new ArrayList<>();
        playerToAdd.add(npc.getName());
        PacketPlayOutScoreboardTeam scoreboardTeamPacketUpdate = new PacketPlayOutScoreboardTeam(team, playerToAdd, 3);
        this.sendPacket(scoreboardTeamPacketUpdate);
        return true;
    }

    /**
     * Spawns the Guard on given Location
     *
     * @param location The given location for the Guard to spawn
     */
    public boolean spawn(Location location) {
        return spawn(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
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
    public boolean spawn(int x, int y, int z, float yaw, float pitch) {
        if (isSpawned() || isDestroyed())
            return false;
        //Set spawn to true
        setSpawned(true);
        //Set location to the center of a block.
        setCurrentLocation(new Location(getCurrentLocation().getWorld(), x + 0.5, y, z + 0.5, yaw, pitch));
        npc.setLocation(x + 0.5, y, z + 0.5, yaw, pitch);
        //Rotate the head. Fix it since for some reason nms has it only working of yaw
        if (!rotateHead(yaw, pitch))
            return false;
        //Packet for playing out a player
        PacketPlayOutPlayerInfo playerDeclarePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc);
        //Spawn the player
        PacketPlayOutNamedEntitySpawn playerSpawnPacket = new PacketPlayOutNamedEntitySpawn(npc);

        //Send packets
        sendPacket(playerDeclarePacket);
        sendPacket(playerSpawnPacket);

        //Delay and then remove from Tablist
        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutPlayerInfo playerRedeclarePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc);
                sendPacket(playerRedeclarePacket);
            }
        }.runTaskLater(LostShardK.plugin, 40);

        //Points to all corners
        this.armorStands = new EntityArmorStand[4];

        armorStands[0] = new EntityArmorStand(((CraftWorld) getCurrentLocation().getWorld()).getHandle(), getCurrentLocation().getBlockX() + 0.75, getCurrentLocation().getBlockY(), getCurrentLocation().getBlockZ() + 0.75); //max max,max
        armorStands[1] = new EntityArmorStand(((CraftWorld) getCurrentLocation().getWorld()).getHandle(), getCurrentLocation().getBlockX() + 0.75, getCurrentLocation().getBlockY(), getCurrentLocation().getBlockZ() + 0.25); //combo max,min
        armorStands[2] = new EntityArmorStand(((CraftWorld) getCurrentLocation().getWorld()).getHandle(), getCurrentLocation().getBlockX() + 0.25, getCurrentLocation().getBlockY(), getCurrentLocation().getBlockZ() + 0.75); //combo min,max
        armorStands[3] = new EntityArmorStand(((CraftWorld) getCurrentLocation().getWorld()).getHandle(), getCurrentLocation().getBlockX() + 0.25, getCurrentLocation().getBlockY(), getCurrentLocation().getBlockZ() + 0.25); //min min,min

        //EntityArmorStand init
        for (EntityArmorStand armorStand : armorStands) {
            armorStand.setNoGravity(true);//float
            armorStand.setInvulnerable(false);//make sure its false so events are called
            armorStand.setInvisible(false); //FIX THIS AT COMPILETIME
            freezeEntity(armorStand); //FREEZE entity cannot move this.
            ((CraftWorld) getCurrentLocation().getWorld()).addEntity(armorStand, CreatureSpawnEvent.SpawnReason.CUSTOM); //Add entity to the world. get out of packets and put to server
        }

        return true;
    }

    /**
     * Destroys the Guard.
     */
    public boolean destroy() {
        if (!isSpawned() || isDestroyed())
            return false;

        PacketPlayOutEntityDestroy playerDestroyPacket = new PacketPlayOutEntityDestroy(npc.getId()); //Create packet to destroy guard
        sendPacket(playerDestroyPacket); //Send packet

        for (EntityArmorStand armorStand : armorStands) //loop all target points
        {
            PacketPlayOutEntityDestroy playerDestroyPacket2 = new PacketPlayOutEntityDestroy(armorStand.getId()); //Create destroy packet
            sendPacket(playerDestroyPacket2); //Destroy
            armorStand.killEntity(); //Since it's also alive in memory on the server, kill it.
        }
        setDestroyed(true);
        return true;
    }

    /**
     * Teleports the Guard to desired location.
     *
     * @param location The location where the Guard will teleport to.
     */
    public boolean teleport(Location location) {

        if (isDestroyed || !isSpawned)
            return false;
        //Teleport the target points
        this.currentLocation = location;
        if (!rotateHead(location.getYaw(), location.getPitch()))
            return false;

        //Teleport the player
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        setField(packet, "a", npc.getId());
        setField(packet, "b", location.getBlockX() + 0.5);
        setField(packet, "c", location.getBlockY());
        setField(packet, "d", location.getBlockZ() + 0.5);
        setField(packet, "e", (byte) location.getYaw());
        setField(packet, "f", (byte) location.getPitch());
        setField(packet, "g", true);
        sendPacket(packet);

        armorStands[0].setLocation(location.getBlockX() + 0.75, location.getBlockY(), location.getBlockZ() + 0.75, location.getYaw(), location.getPitch()); //max max,max
        armorStands[1].setLocation(location.getBlockX() + 0.75, location.getBlockY(), location.getBlockZ() + 0.25, location.getYaw(), location.getPitch()); //combo max,min
        armorStands[2].setLocation(location.getBlockX() + 0.25, location.getBlockY(), location.getBlockZ() + 0.75, location.getYaw(), location.getPitch()); //combo min,max
        armorStands[3].setLocation(location.getBlockX() + 0.25, location.getBlockY(), location.getBlockZ() + 0.25, location.getYaw(), location.getPitch()); //min min,min
        return true;
    }

    /**
     * Checks if an Entity id matches this EntityPlayer
     *
     * @param id The Entity ID we are checking for
     * @return if same entity
     */
    public boolean isId(int id) {
        if (isDestroyed || !isSpawned)
            return false;
        for (EntityArmorStand armorStand : armorStands) {
            if (armorStand.getId() == id)
                return true;
        }
        return false;
    }

    /**
     * Fix rotation of the Guard's head
     *
     * @param yaw   The new yaw of the guard
     * @param pitch The new pitch of the guard
     */
    public boolean rotateHead(float yaw, float pitch) {
        if (isDestroyed || !isSpawned)
            return false;
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(this.npc.getId(), getFixRotation(yaw), (byte) pitch, true);
        PacketPlayOutEntityHeadRotation packet_1 = new PacketPlayOutEntityHeadRotation();
        this.setField(packet_1, "a", this.npc.getId());
        this.setField(packet_1, "b", getFixRotation(yaw));
        this.sendPacket(packet);
        this.sendPacket(packet_1);
        return true;
    }

    /**
     * Fixes the yaw of the guard because minecraft's native code has it set to something else
     *
     * @param yawpitch The yaw we are setting to
     * @return the byte representing the calculated yaw
     */
    private byte getFixRotation(float yawpitch) {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }

    public boolean setEquipment(Material head, Material chest, Material leggings, Material boots, Material mainHand, Material offHand) {
        if (isDestroyed || !isSpawned)
            return false;
        PacketPlayOutEntityEquipment headPacket = new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(head)));
        PacketPlayOutEntityEquipment chestPacket = new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(new ItemStack(chest)));
        PacketPlayOutEntityEquipment legsPacket = new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(new ItemStack(leggings)));
        PacketPlayOutEntityEquipment feetPacket = new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.FEET, CraftItemStack.asNMSCopy(new ItemStack(boots)));
        PacketPlayOutEntityEquipment mainHandPacket = new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(new ItemStack(mainHand)));
        PacketPlayOutEntityEquipment offHandPacket = new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(new ItemStack(offHand)));


        this.sendPacket(headPacket);
        this.sendPacket(chestPacket);
        this.sendPacket(legsPacket);
        this.sendPacket(feetPacket);
        this.sendPacket(mainHandPacket);
        this.sendPacket(offHandPacket);

        return true;

    }

    /**
     * Forces the Player to look at this Entity
     *
     * @param player The player being forced to look at (this) entity.
     */
    public boolean lookAtThis(Player player) {
        if (isDestroyed || !isSpawned)
            return false;

        Location loc = player.getLocation();

        double xDiff = loc.getX() - getCurrentLocation().getX();
        double yDiff = loc.getY() - getCurrentLocation().getY();
        double zDiff = loc.getZ() - getCurrentLocation().getZ();

        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
        double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI - 180;
        double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
        if (zDiff < 0.0)
            newYaw = newYaw + Math.abs(180 - newYaw) * 2;
        newYaw = (newYaw - 90);

        float yaw = (float) newYaw;
        float pitch = (float) newPitch;

        player.teleport(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), yaw, pitch));
        return true;

    }

    /**
     * Returns an array of String containing the texture and signature of the player respectively in index order.
     *
     * @param playerBukkit The player being searched for.
     * @return A String array containing texture in index 0 and signature in index 1
     */
    public static String[] getFromPlayer(Player playerBukkit) {
        EntityPlayer playerNMS = ((CraftPlayer) playerBukkit).getHandle();
        GameProfile profile = playerNMS.getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();
        return new String[]{texture, signature};
    }

    /**
     * Returns an array of String containing the texture and signature of the player's name respectively in index order.
     *
     * @param name The player name being searched for.
     * @return A String array containing texture in index 0 and signature in index 1
     */
    public static String[] getFromName(String name) {
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
            LostShardK.logger.info("Error occurred: Could not apply skin due to high traffic requests being sent to the HTTP server.");
            return null;
        }
    }

    //BASIC GETTER/SETTER

    public boolean isDestroyed() {
        return isDestroyed;
    }

    private void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }

    public boolean isSpawned() {
        return isSpawned;
    }

    private void setSpawned(boolean spawned) {
        isSpawned = spawned;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    private void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public EntityPlayer getNPC() {
        return npc;
    }

    public EntityArmorStand[] getArmorStands() {
        return armorStands;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public static String[] getSkinProperties(String name) {
        return skinProperties.get(name);
    }

    public static boolean addSkin(String name) {
        if (!skinProperties.containsKey(name)) {
            String[] localSkinProperties = getFromName(name);
            if (localSkinProperties == null)

                return false;

            skinProperties.put(name, localSkinProperties);
        }
        return true;
    }


}
