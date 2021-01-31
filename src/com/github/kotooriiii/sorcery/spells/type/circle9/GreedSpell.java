package com.github.kotooriiii.sorcery.spells.type.circle9;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.sorcery.spells.drops.SpellMonsterDrop;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;

public class GreedSpell extends Spell implements Listener {

    private final static HashMap<UUID, Double> greedSpellCooldownMap = new HashMap<>();
    private final static ChatColor COLOR = ChatColor.GRAY;
    private final static String ID = "ID:SOULBOUND", OWNER = COLOR + "Soulbound Owner: ";
    private final static HashMap<UUID, ItemStack> soulbindedMapToReturn = new HashMap<>();
    private final static HashSet<UUID> soulbindSelectSet = new HashSet<>();

    private final static int DURATION = 20;


    private GreedSpell() {
        super(SpellType.GREED,
                "Soulbind an item that is kept upon death. Cast greed, then open your inventory and the first item you pick up will be soulbound. Only viable for 1 death.",
                9, ChatColor.GOLD, new ItemStack[]{new ItemStack(Material.DRAGON_EGG, 1), new ItemStack(Material.GOLD_BLOCK, 1)}, 2.0f, 50, true, true, false,                new SpellMonsterDrop(new EntityType[]{EntityType.ENDER_DRAGON}, 0.1111111111));

    }

    private static GreedSpell instance;

    public static GreedSpell getInstance() {
        if (instance == null) {
            synchronized (GreedSpell.class) {
                if (instance == null)
                    instance = new GreedSpell();
            }
        }
        return instance;
    }


    @Override
    public void updateCooldown(Player player) {
        greedSpellCooldownMap.put(player.getUniqueId(), this.getCooldown() * 20);
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    greedSpellCooldownMap.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Double newCooldown = new Double(cooldown - counter);
                greedSpellCooldownMap.put(player.getUniqueId(), newCooldown);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);
    }

    public boolean isCooldown(Player player) {
        if (greedSpellCooldownMap.containsKey(player.getUniqueId())) {

            Double cooldownTimeTicks = greedSpellCooldownMap.get(player.getUniqueId());
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

        final UUID UNIQUE_UUID = player.getUniqueId();
        player.sendMessage(COLOR + "Open your inventory and select an item to soulbind...");
        soulbindSelectSet.add(UNIQUE_UUID);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (!soulbindSelectSet.contains(UNIQUE_UUID))
                    return;

                if (player.isOnline())
                    player.sendMessage(ERROR_COLOR + "You ran out of time to soulbind an item...");
                soulbindSelectSet.remove(UNIQUE_UUID);
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * DURATION);

        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR)
            return;

        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked.getType() != EntityType.PLAYER)
            return;
        if (!soulbindSelectSet.contains(whoClicked.getUniqueId()))
            return;
        if (currentItem.getAmount() != 1) {
            whoClicked.sendMessage(ERROR_COLOR + "You can only select one item... Make sure you are clicking only one item...");
            return;
        }
        if (CitizensAPI.getNPCRegistry().isNPC(whoClicked))
            return;
        if (isLapisNearby(whoClicked.getLocation(), DEFAULT_LAPIS_NEARBY)) {
            whoClicked.sendMessage(ERROR_COLOR + "Something doesn't let you soulbind here...");
            soulbindSelectSet.remove(whoClicked.getUniqueId());
            return;
        }
        if (isSoulbound(currentItem)) {
            whoClicked.sendMessage(ERROR_COLOR + "The item is already soulbinded...");
            return;
        }
        if (event.getClickedInventory().getType() != InventoryType.PLAYER)
            return;

        Player player = (Player) whoClicked;

        /*

        Item is not null.
        Pick up one item event.
        Player clicked
        It's not an NPC
        Lapis is not nearby
        Item is not soulbound
        Click on player inv

         */

        soulbind(player, currentItem);
        event.setCancelled(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                }
            }
        }.runTask(LostShardPlugin.plugin);
    }

    @EventHandler
    public void onItemFrame(PlayerInteractEntityEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME)
            return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null)
            return;
        if (!isSoulbound(item))
            return;
        soulbreak(item);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;

        Iterator<ItemStack> iterator = event.getDrops().iterator();

        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null || item.getItemMeta().getLore().isEmpty())
                continue;
            if (!isSoulbound(item))
                continue;
            iterator.remove();
            ItemStack clone = item.clone();
            soulbreak(clone, false);
            soulbindedMapToReturn.put(event.getEntity().getUniqueId(), clone);
            break;
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if (!soulbindedMapToReturn.containsKey(event.getPlayer().getUniqueId()))
            return;
        event.getPlayer().getInventory().addItem(soulbindedMapToReturn.get(event.getPlayer().getUniqueId()));
        soulbindedMapToReturn.remove(event.getPlayer().getUniqueId());
        event.getPlayer().sendMessage(COLOR + "Your soulbinded item has been returned...");

        final Location deadLocation = event.getPlayer().getEyeLocation();
        deadLocation.getWorld().playSound(deadLocation, Sound.ENTITY_PARROT_FLY, 6.0f, 5.0f);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (!event.getPlayer().isOnline())
                    return;

                final int[] timer = {0};
                int duration = 3;
                final Location respawnLocation = event.getPlayer().getEyeLocation();


                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (timer[0]++ / 4 >= duration) {

                            respawnLocation.getWorld().playSound(respawnLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 6.0f, 1.0f);
                            deadLocation.getWorld().playSound(deadLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 6.0f, 1.0f);

                            Vector dir = respawnLocation.toVector().clone().subtract(deadLocation.toVector().clone());

                            BlockIterator blockIteratorB = new BlockIterator(respawnLocation.getWorld(), respawnLocation.toVector(), dir.clone().multiply(-1), 0, 7);
                            BlockIterator blockIteratorA = new BlockIterator(deadLocation.getWorld(), deadLocation.toVector(), dir, 0, 7);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (blockIteratorB.hasNext()) {
                                        final Block next = blockIteratorB.next();
                                        next.getWorld().spawnParticle(Particle.REDSTONE, next.getLocation(), 10, 0, 0, 0, new Particle.DustOptions(Color.GRAY, 1f));
                                    } else {
                                        this.cancel();
                                    }

                                }
                            }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 10, 4);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (blockIteratorA.hasNext()) {
                                        final Block next = blockIteratorA.next();
                                        next.getWorld().spawnParticle(Particle.REDSTONE, next.getLocation(), 10, 0, 0, 0, new Particle.DustOptions(Color.GRAY, 1f));
                                    } else {
                                        this.cancel();
                                    }

                                }
                            }.runTaskTimerAsynchronously(LostShardPlugin.plugin, 10, 4);

                            this.cancel();
                            return;
                        }


                        deadLocation.getWorld().spawnParticle(Particle.REDSTONE, deadLocation, 10, (float) 1, (float) 0.1f, (float) 1f, new Particle.DustOptions(Color.GRAY, 1f));
                        respawnLocation.getWorld().spawnParticle(Particle.REDSTONE, respawnLocation, 10, (float) 1, (float) 0.1f, (float) 1f, new Particle.DustOptions(Color.GRAY, 1f));

                    }
                }.runTaskTimer(LostShardPlugin.plugin, 0, 5);


                deadLocation.getWorld().playSound(deadLocation, Sound.ENTITY_PARROT_FLY, 6.0f, 1.0f);
                respawnLocation.getWorld().playSound(respawnLocation, Sound.ENTITY_PARROT_FLY, 6.0f, 1.0f);

            }
        }.runTaskLater(LostShardPlugin.plugin, 10);


    }

    /**
     * Dragged on wrong inv
     *
     * @param event
     */
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        ItemStack cursor = event.getOldCursor();

        if (cursor != null) {
            if (cursor != null && cursor.getItemMeta() != null && cursor.getItemMeta().getLore() != null && !cursor.getItemMeta().getLore().isEmpty() && isSoulbound(cursor) && (!(event.getView().getTopInventory().getType() == InventoryType.CRAFTING || event.getView().getBottomInventory() == event.getInventory()))) {
//                Bukkit.broadcastMessage("1DEBUG: Cursor on top inventory with banner on cursor. Canceled. Returned.");
                soulbreak(cursor);
                return;
            }

        }
    }

    /**
     * Unsoulbind if you change inv
     *
     * @param event
     */
    @EventHandler
    public void onInv(InventoryClickEvent event) {

        if (event.getClick() == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            if (hotbarItem != null && hotbarItem.getItemMeta() != null && hotbarItem.getItemMeta().getLore() != null && !hotbarItem.getItemMeta().getLore().isEmpty() && isSoulbound(hotbarItem) && event.getClickedInventory() == event.getView().getTopInventory()) {
                if (event.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
                    soulbreak(hotbarItem);
                }
                return;
            }
        }
//        Bukkit.broadcastMessage("DEBUG: " + event.getRawSlot() + " " + event.getView().getTopInventory().getSize());
//        Bukkit.broadcastMessage("DEBUG: " + event.getClickedInventory().getType());
//        Bukkit.broadcastMessage("DEBUG: " + event.getView().getTopInventory().getType());
//        Bukkit.broadcastMessage("DEBUG: " + event.getClick());
//        Bukkit.broadcastMessage("DEBUG: " + event.getCursor());
//        Bukkit.broadcastMessage("DEBUG: " + event.getCurrentItem());

        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();

        ItemStack cursor = event.getCursor();

        if (cursor != null) {
            if (cursor != null && cursor.getItemMeta() != null && cursor.getItemMeta().getLore() != null && !cursor.getItemMeta().getLore().isEmpty() && isSoulbound(cursor) && event.getClickedInventory() == event.getView().getTopInventory()) {
//                Bukkit.broadcastMessage("DEBUG: Cursor on top inventory with banner on cursor. Canceled. Returned.");
                soulbreak(cursor);
                return;
            }

        }


        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null || item.getItemMeta().getLore().isEmpty())
            return;
        if (!isSoulbound(item))
            return;


        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
//            Bukkit.broadcastMessage("DEBUG: Moved item AND not player inventory. Canceled. Returned.");
            soulbreak(item);
            return;
        }


        if (event.getView().getTopInventory() == null) {
//            Bukkit.broadcastMessage("DEBUG: Top view is null. Canceled. Returned.");
            soulbreak(item);
            return;
        }

        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
//            Bukkit.broadcastMessage("DEBUG: Clicked inventory is player. Returned.");
            return;
        }

        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
//            Bukkit.broadcastMessage("DEBUG: Slot. Returned.");
            return;
        }
        soulbreak(item);
    }

    /**
     * Drop soul binded item
     *
     * @param event
     */
    @EventHandler
    public void onDrop(BlockPlaceEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        ItemStack item = event.getItemInHand();
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null || item.getItemMeta().getLore().isEmpty())
            return;
        if (!isSoulbound(item))
            return;

        soulbreak(item);
    }

    /**
     * Drop soul binded item
     *
     * @param event
     */
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        ItemStack item = event.getItemDrop().getItemStack();
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null || item.getItemMeta().getLore().isEmpty())
            return;
        if (!isSoulbound(item))
            return;

        soulbreak(item);
    }

    @EventHandler
    public void onMoveGreedListener(PlayerMoveEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;

        final int x_initial, y_initial, z_initial,
                x_final, y_final, z_final;

        x_initial = event.getFrom().getBlockX();
        y_initial = event.getFrom().getBlockY();
        z_initial = event.getFrom().getBlockZ();

        x_final = event.getTo().getBlockX();
        y_final = event.getTo().getBlockY();
        z_final = event.getTo().getBlockZ();

        if (x_initial == x_final && y_initial == y_final && z_initial == z_final)
            return;

        if (!soulbindSelectSet.contains(event.getPlayer().getUniqueId()))
            return;
        if (!Spell.isLapisNearby(event.getTo(), Spell.getDefaultLapisNearbyValue()))
            return;
        event.getPlayer().sendMessage(ERROR_COLOR + "Something doesn't seem to let you soulbind an item here...");
        soulbindSelectSet.remove(event.getPlayer().getUniqueId());
    }


    //UTIL METHODS

    private ItemStack getSoulbindedItem(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        for (ItemStack itemStack : contents) {
            if (isSoulbound(itemStack))
                return itemStack;
        }
        return null;
    }

    private boolean hasSoulboundedInventory(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        for (ItemStack itemStack : contents) {
            if (isSoulbound(itemStack))
                return true;
        }
        return false;
    }

    private boolean removeSoulboundedInventory(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        boolean broke = false;
        for (ItemStack itemStack : contents) {
            if (soulbreak(itemStack))
                broke = true;

        }
        return broke;
    }

    private boolean isSoulbound(ItemStack itemStack) {
        if (itemStack == null)
            return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;
        if (itemMeta.getLore() == null || itemMeta.getLore().isEmpty())
            return false;
        List<String> list = itemMeta.getLore();
        if (!list.get(list.size() - 1).equals(ID))
            return false;
        return true;
    }

    private boolean isSoulbound(Player player, ItemStack itemStack) {
        if (itemStack == null)
            return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;
        if (itemMeta.getLore() == null || itemMeta.getLore().size() < 2)
            return false;
        List<String> list = itemMeta.getLore();
        if (!list.get(list.size() - 1).equals(ID))
            return false;
        if (!list.get(list.size() - 2).equals(OWNER + player.getName()))
            return false;
        return true;
    }

    /**
     * Soulbinds an item to a player inventory
     *
     * @param player    Player who is to soulbind an item
     * @param itemStack Item that is to be soulbinded
     * @return true if item is soulbinded, false if is not soulbinded.
     */
    private boolean soulbind(Player player, ItemStack itemStack) {
        if (itemStack == null)
            return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;
        List<String> list = itemMeta.getLore();
        if (list == null || list.size() < 2) {
            list = new ArrayList<>();
        } else {
            if (!list.get(list.size() - 1).equals(ID))
                return false;
            if (!list.get(list.size() - 2).equals(OWNER + player.getName()))
                return false;
        }

        list.add(OWNER + player.getName());
        list.add(ID);
        removeSoulboundedInventory(player.getInventory());
        itemMeta.setLore(list);
        itemStack.setItemMeta(itemMeta);
        player.sendMessage(COLOR + "This item has become soulbound...");
        final Location loc = player.getEyeLocation();

        final int[] timer = {0};
        int duration = 3;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timer[0]++ / 4 >= duration) {
                    this.cancel();
                    return;
                }

                loc.getWorld().spawnParticle(Particle.WHITE_ASH, loc, 30, 1, 0, 1);
            }
        }.runTaskTimer(LostShardPlugin.plugin, 0, 5);
        loc.getWorld().playSound(loc, Sound.AMBIENT_BASALT_DELTAS_MOOD, 5.0f, 7.0f);

        soulbindSelectSet.remove(player.getUniqueId());
        return true;
    }

    /**
     * Breaks the soulbound item from a player inventory
     *
     * @param itemStack Item that is soulbinded
     * @return true if item has broken soulbound, false if still soulbinded or item is not soulbinded already.
     */
    private boolean soulbreak(ItemStack itemStack) {
        return soulbreak(itemStack, true);
    }

    /**
     * Breaks the soulbound item from a player inventory
     *
     * @param itemStack Item that is soulbinded
     * @param isVisible Visible particles and messaging
     * @return true if item has broken soulbound, false if still soulbinded or item is not soulbinded already.
     */
    private boolean soulbreak(ItemStack itemStack, boolean isVisible) {
        if (itemStack == null)
            return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;
        List<String> list = itemMeta.getLore();
        if (list == null || list.size() < 2)
            return false;


        if (!list.get(list.size() - 1).equals(ID))
            return false;
        String name = list.get(list.size() - 2).substring(OWNER.length());
        Player player = Bukkit.getPlayer(name);
        if (player != null && isVisible) {
            player.sendMessage(COLOR + "This item is no longer soulbound...");
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getLocation(), 20, 0.3f, 0.3f, 0.3f);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 5.0f, 7.0f);

        }

        list.remove(list.size() - 1);
        list.remove(list.size() - 1);
        itemMeta.setLore(list);
        itemStack.setItemMeta(itemMeta);
        return true;
    }
}
