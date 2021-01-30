package com.github.kotooriiii.npc.type.tutorial.murderer;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.Skin;
import com.github.kotooriiii.tutorial.events.TutorialPlayerDeathEvent;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.StuckAction;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.

public class MurdererTrait extends Trait {
    private Player entity;

    public MurdererTrait(Player target) {
        super("MurdererTrait");
        entity = target;
    }

    public MurdererTrait() {
        super("MurdererTrait");
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
    public void click(net.citizensnpcs.api.event.NPCClickEvent event) {
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        if (!event.getNPC().equals(this.getNPC()))
            return;

        if (entity == event.getClicker())
            event.getClicker().sendMessage(ChatColor.DARK_RED + "You must type: /guards in order to kill this murderer.");
        else
            event.getClicker().sendMessage(ChatColor.DARK_RED + "Don't waste time with this murderer. He is not going after you.");

    }

    // Called every tick
    @Override
    public void run() {
        if (getNPC() == null)
            return;
        if (!getNPC().isSpawned())
            return;
        if (entity == null)
            return;
        Navigator nav = getNPC().getNavigator();

        while (!nav.isNavigating()) {
            if (!this.entity.isDead() && this.entity.isOnline()) {
                nav.setTarget(entity, true);
            }
            nav.getLocalParameters().range(169);
            nav.getLocalParameters().speedModifier(1.2f);
            nav.getLocalParameters().stuckAction((npc, navigator) -> true);
            nav.getLocalParameters().attackDelayTicks(10);
        }
    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.CHAINMAIL_HELMET, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.CHAINMAIL_BOOTS, 1));

        //important for off hand since hand will do more dmg
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.OFF_HAND, new ItemStack(Material.IRON_SWORD, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD, 1));


        npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, Skin.BANKER.getTexture());
        npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, Skin.BANKER.getSignature());
        npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, "Nickolov");

        SkinTrait skinTrait = null;
        if(!npc.hasTrait(SkinTrait.class)) {
            skinTrait = new SkinTrait();
            npc.addTrait(skinTrait);
        } else {
            skinTrait = npc.getTrait(SkinTrait.class);
        }

        skinTrait.setSkinName(Skin.BANKER.getName());
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

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!npc.isSpawned())
            return;
        if (getTargetTutorial() == null)
            return;
        if (!event.getPlayer().equals(getTargetTutorial()))
            return;

        getNPC().getOwningRegistry().deregister(getNPC());
    }

    @EventHandler
    public void onDeath(TutorialPlayerDeathEvent event) {
        if (!npc.isSpawned())
            return;
        if (getTargetTutorial() == null)
            return;
        if (!event.getPlayer().getUniqueId().equals(getTargetTutorial().getUniqueId()))
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                getNPC().despawn();
                getNPC().getOwningRegistry().deregister(getNPC());
                this.cancel();
            }
        }.runTask(LostShardPlugin.plugin);


    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }

    public LivingEntity getTargetTutorial() {
        return entity;
    }

}
