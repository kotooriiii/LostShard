package com.github.kotooriiii.npc.type.guard;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.Skin;
import com.github.kotooriiii.npc.type.tutorial.murderer.MurdererTrait;
import com.github.kotooriiii.status.Staff;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.tutorial.events.TutorialMurdererDeathEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.GUARD_COLOR;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.

public class GuardTrait extends Trait {

    //Static
    private final int warningRadius = 7;
    private final int alertRadius = warningRadius - 2;

    // see the 'Persistence API' section
    @Persist
    private String name = "";
    @Persist
    private Location guardingLocation = null;

    private boolean isBusy = false;
    private boolean isCalled = false;
    private UUID owner;

    public GuardTrait() {
        super("GuardTrait");
    }


    public GuardTrait(String name, Location guardingLocation) {
        super("GuardTrait");
        this.name = name;
        this.guardingLocation = guardingLocation;
    }


    // Here you should load up any values you have previously saved (optional).
    // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
    // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
    // This is called BEFORE onSpawn, npc.getBukkitEntity() will return null.
    public void load(DataKey key) {
        //isBusy = key.getBoolean("isBusy", false);
    }

    // Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
    public void save(DataKey key) {
        //key.setBoolean("isBusy", isBusy);
    }

    // An example event handler. All traits will be registered automatically as Bukkit Listeners.
    @EventHandler
    public void click(NPCRightClickEvent event) {
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        if (!event.getNPC().equals(this.getNPC()))
            return;

        Player clicker = event.getClicker();

        StatusPlayer statusPlayer = StatusPlayer.wrap(clicker.getUniqueId());

        if (!statusPlayer.getStatus().equals(Status.WORTHY))
            return;

        sendMessage(clicker, event.getNPC());
    }

    // Called every tick
    @Override
    public void run() {
        if (getNPC() == null)
            return;
        if (!getNPC().isSpawned())
            return;
        if (isBusy)
            return;
        for (Entity entity : getNPC().getStoredLocation().getWorld().getNearbyEntities(getNPC().getStoredLocation(), warningRadius, warningRadius, warningRadius)) {

            NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);


            //NPC exists and is tutorial.
            if (npc != null && LostShardPlugin.isTutorial()) {
                if(!npc.hasTrait(MurdererTrait.class))
                    continue;
                if(getOwner() == null || !npc.getTrait(MurdererTrait.class).getTargetTutorial().getUniqueId().equals(getOwner()))
                    continue;

                isCalled = false;
                isBusy = true;
                teleportKill(npc);
                return;
            }

            //Player AND not a "madeup" player from Citizens
            else if (entity instanceof Player && !CitizensAPI.getNPCRegistry().isNPC(entity)){

                Player player = (Player) entity;
                if (player.isDead())
                    continue;
                if (Staff.isStaff(player.getUniqueId()))
                    continue;
                if (StatusPlayer.wrap(player.getUniqueId()).getStatus().equals(Status.WORTHY))
                    continue;

                Location loc = entity.getLocation();


                int xIntDiff = loc.getBlockX() - getNPC().getStoredLocation().getBlockX();
                int yIntDiff = loc.getBlockY() - getNPC().getStoredLocation().getBlockY();
                int zIntDiff = loc.getBlockZ() - getNPC().getStoredLocation().getBlockZ();

                if (Math.abs(xIntDiff) <= alertRadius && Math.abs(yIntDiff) <= alertRadius && Math.abs(zIntDiff) <= alertRadius) {
                    isCalled = false;
                    isBusy = true;
                    teleportKill(player);
                    return;
                } else {

                    getNPC().faceLocation(player.getLocation());
                    //Graphics
                    int randomInt = new Random().nextInt(3);
                    double randomChance = Math.random();
                    if (randomInt != 0)
                        if (randomChance <= 0.1)
                            player.spawnParticle(Particle.VILLAGER_ANGRY, getNPC().getStoredLocation().getBlockX() + 0.5, getNPC().getStoredLocation().getBlockY() + 2, getNPC().getStoredLocation().getBlockZ() + 0.5, randomInt, 0.25, 0, 0.25);


                }
            }

        }
    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.IRON_HELMET, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.IRON_CHESTPLATE, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.IRON_LEGGINGS, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.IRON_BOOTS, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.OFF_HAND, new ItemStack(Material.SHIELD, 1));

        npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, Skin.GUARD.getTexture());
        npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, Skin.GUARD.getSignature());
        npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, "Shelvie");
    }

    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getBukkitEntity() is still valid.
    @Override
    public void onDespawn() {
    }

    //Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
    //This is called AFTER onAttach and AFTER Load when the server is started.
    @Override
    public void onSpawn() {
    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }

    //BASIC GETTERS/SETTERS

    public String getGuardName() {
        return name;
    }

    public void setGuardName(String name) {
        this.name = name;
    }

    public Location getGuardingLocation() {
        return guardingLocation;
    }

    public void setGuardingLocation(Location guardingLocation) {
        this.guardingLocation = guardingLocation;
        getNPC().teleport(guardingLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public boolean isCalled() {
        return isCalled;
    }

    public void setCalled(boolean called) {
        isCalled = called;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public UUID getOwner()
    {
        return this.owner;
    }

    //END OF BASIC GETTERS/SETTERS

    private void sendMessage(Player clicker, NPC clicked) {

        Iterable<NPC> iterable = GuardNPC.getAllGuardNPC();
        Iterator<NPC> iterator = iterable.iterator();

        for (int i = 0; iterator.hasNext(); i++) {

            NPC guardNPC = iterator.next();
            if (!clicked.equals(guardNPC))
                continue;


            //Add what happens when rightclick with playerInteractShardNPC
            String[] positiveMessages = new String[]{
                    "What do you need?",
                    "Have any questions?",
                    "Can I help you?",
                    "Let me know if there's anything I can do for you.",
                    "Are you interesting in becoming a guard?",
                    "Welcome to the Order."};

            String[] negativeMessages = new String[]
                    {
                            "I'm workin' here!",
                            "Leave me alone.",
                            "I'm not interested in whatever you have to offer.",
                            "I have no opportunities for you.",
                            "You are wasting my time.",
                            "I don't have any time to talk, sorry."
                    };

            String[] messages;
            if (i % 2 == 0) {
                messages = positiveMessages;
            } else {
                messages = negativeMessages;
            }
            String message = messages[new Random().nextInt(messages.length)];
            clicker.sendMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "MSG" + ChatColor.WHITE + "] " + GUARD_COLOR + guardNPC.getTrait(GuardTrait.class).getGuardName() + ChatColor.WHITE + ": " + message);
            break;
        }
    }

    private void teleportKill(Player player) {
        if (!npc.isSpawned())
            return;
        //Get the world
        World w = player.getWorld();

        w.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 20, 0.3, 0.3, 0.3);

        //Get location of player in case of logging out
        final Location playerLocation = player.getLocation();

        npc.teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        w.spawnParticle(Particle.CRIT, playerLocation, 50, 0.3, 0.3, 0.3);
        w.spawnParticle(Particle.DRAGON_BREATH, playerLocation, 50, 2, 2, 2);

        if (player.isOnline() && !player.isDead())
            player.setHealth(0);


        new BukkitRunnable() {
            @Override
            public void run() {
                if (!npc.isSpawned())
                    return;
                npc.teleport(guardingLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                isBusy = false;
            }
        }.runTaskLater(LostShardPlugin.plugin, 20);
    }

    private void teleportKill(NPC npcParam) {
        if (!this.npc.isSpawned())
            return;
        //Get the world
        World w = npcParam.getStoredLocation().getWorld();

        w.spawnParticle(Particle.FIREWORKS_SPARK, npcParam.getStoredLocation(), 20, 0.3, 0.3, 0.3);

        //Get location of player in case of logging out
        final Location playerLocation = npcParam.getStoredLocation();

        this.npc.teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        w.spawnParticle(Particle.CRIT, playerLocation, 50, 0.3, 0.3, 0.3);
        w.spawnParticle(Particle.DRAGON_BREATH, playerLocation, 50, 2, 2, 2);

        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(new TutorialMurdererDeathEvent(npcParam));

        npcParam.getEntity().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, npcParam.getStoredLocation(), 3, 0, 0f, 0f);
        npcParam.getEntity().getWorld().playSound(npcParam.getStoredLocation(), Sound.ENTITY_PLAYER_DEATH, 10.0f, 3.0f);
        npcParam.setProtected(false);
        if(npcParam.getEntity() instanceof LivingEntity)
        ((LivingEntity) npcParam.getEntity()).damage(20.0f);

        new BukkitRunnable() {
            @Override
            public void run() {
                npcParam.despawn();
                npcParam.getOwningRegistry().deregister(npcParam);
                if (!npc.isSpawned())
                    return;
                npc.teleport(guardingLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                isBusy = false;
            }
        }.runTaskLater(LostShardPlugin.plugin, 20);
    }


}
