package com.github.kotooriiii.npc.type.banker;

import com.github.kotooriiii.npc.Skin;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.

public class BankerTrait extends Trait {

    //Static
    private final int socialDistance = 5;

    // see the 'Persistence API' section
    @Persist
    private String name = "";
    @Persist
    private Location bankerLocation = null;

    public BankerTrait()
    {
        super("BankerTrait");
    }


    public BankerTrait(String name, Location bankerLocation) {
        super("BankerTrait");
        this.name = name;
        this.bankerLocation = bankerLocation;
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
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event) {
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        if (!event.getNPC().equals(this.getNPC()))
            return;

        Player clicker = event.getClicker();

        clicker.performCommand("bank help");
    }

    // Called every tick
    @Override
    public void run() {
        if(getNPC() == null)
            return;
        if(!getNPC().isSpawned())
            return;
    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.GOLDEN_HELMET, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.GOLDEN_LEGGINGS, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.GOLDEN_BOOTS, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.IRON_INGOT, 1));

        npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, Skin.BANKER.getTexture());
        npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, Skin.BANKER.getSignature());
        npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, "Nickolov");
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

    public String getBankerName() {
        return name;
    }

    public void setBankerName(String name) {
        this.name = name;
    }

    public Location getBankerLocation() {
        return bankerLocation;
    }

    public void setBankerLocation(Location bankerLocation) {
        this.bankerLocation = bankerLocation;
        getNPC().teleport(bankerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    //END OF BASIC GETTERS/SETTERS

    public boolean isSocialDistance(Location loc)
    {
        int xIntDiff = loc.getBlockX() - getNPC().getStoredLocation().getBlockX();
        int yIntDiff = loc.getBlockY() - getNPC().getStoredLocation().getBlockY();
        int zIntDiff = loc.getBlockZ() - getNPC().getStoredLocation().getBlockZ();
        return Math.abs(xIntDiff) <= socialDistance && Math.abs(yIntDiff) <= socialDistance && Math.abs(zIntDiff) <= socialDistance;
    }
}
