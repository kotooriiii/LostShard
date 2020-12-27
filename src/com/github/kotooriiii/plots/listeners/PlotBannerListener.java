package com.github.kotooriiii.plots.listeners;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.channels.events.ShardChatEvent;
import com.github.kotooriiii.commands.DiscordCommand;
import com.github.kotooriiii.commands.DocCommand;
import com.github.kotooriiii.commands.WikiCommand;
import com.github.kotooriiii.commands.YoutubeCommand;
import com.github.kotooriiii.plots.PlotBanner;
import com.github.kotooriiii.plots.PlotManager;
import com.github.kotooriiii.plots.ShardPlotPlayer;
import com.github.kotooriiii.plots.events.PlotCreateEvent;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.SpellType;
import com.github.kotooriiii.util.HelperMethods;
import com.mysql.jdbc.SocketMetadata;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCAddTraitEvent;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.github.kotooriiii.data.Maps.*;

public class PlotBannerListener implements Listener {

    private final HashMap<UUID, Location[]> map = new HashMap<>();

    private void remove(Block block, Player player, boolean giveBanner) {
        if (!block.getType().getKey().getKey().toLowerCase().endsWith("_banner"))
            return;

        block.setType(Material.AIR);

        if (giveBanner)
            player.getInventory().addItem(PlotBanner.getInstance().getItem());

    }

    @EventHandler
    public void onPlotBannerNameListen(ShardChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!map.containsKey(player.getUniqueId()))
            return;
        event.setCancelled(true);

        PlotManager plotManager = LostShardPlugin.getPlotManager();
        Block bannerBlock = map.get(player.getUniqueId())[1].getBlock();


        if (message.length() > 16) {
            player.sendMessage(ERROR_COLOR + "The name can not exceed 16 characters.");
            remove(bannerBlock, player, true);
            return;
        } else if (plotManager.isStaffPlotName(message)) {
            player.sendMessage(ERROR_COLOR + "This plot name has its place in history already. Create your own history!");
            remove(bannerBlock, player, true);
            return;
        } else if (plotManager.isPlot(message)) {
            player.sendMessage(ERROR_COLOR + "That plot name has already been taken.");
            remove(bannerBlock, player, true);
            return;
        }

        /* NO COST
        else if (!hasCreatePlotCost(playerSender)) {
           //The message is already taken care of.
        }
        */
        else {
            createPlot(player, message, map.get(player.getUniqueId()));
        }


        map.remove(player.getUniqueId());
    }


    @EventHandler
    public void onPlotBannerPlaced(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (event.isCancelled())
            return;
        if (block == null || event.getItemInHand() == null)
            return;

        if (!(event.getBlockPlaced().getState() instanceof Banner))
            return;


        /*
        Block is a banner at this point.
         */

        if (event.getItemInHand().getItemMeta() == null)
            return;

        BannerMeta meta = (BannerMeta) event.getItemInHand().getItemMeta();

        List<String> lore = meta.getLore();

        if (lore == null || lore.isEmpty())
            return;

        /*
        Banner meta variable declared.
        Lore exists.
         */


        if (!PlotBanner.getInstance().getID().equals(lore.get(lore.size() - 1)))
            return;

        /*
        Lore is equal to the ID.

        At this point, we know it's a Plot Banner.
         */

        Player playerSender = event.getPlayer();
        PlotManager plotManager = LostShardPlugin.getPlotManager();
        ShardPlotPlayer plotSenderPlayer = ShardPlotPlayer.wrap(playerSender.getUniqueId());

        if (plotSenderPlayer.hasReachedMaxPlots()) {
            playerSender.sendMessage(ERROR_COLOR + "You've reached the max amount of plots you can own.");
            event.setCancelled(true);
            return;
        } else if (plotManager.hasNearbyPlots(playerSender.getLocation())) {
            playerSender.sendMessage(ERROR_COLOR + "There are other plot(s) nearby. \nYou must be a minimum of " + Plot.MINIMUM_PLOT_CREATE_RANGE + " block(s) away from player plots and " + Plot.MINIMUM_PLOT_STAFF_CREATE_RANGE + " block(s) away from staff plots.");
            event.setCancelled(true);
            return;
        }

        playerSender.sendMessage(STANDARD_COLOR + "You placed a Plot Banner!\n" + ChatColor.YELLOW + "What would you like to name your plot?");
        map.put(playerSender.getUniqueId(), new Location[]{playerSender.getLocation(), block.getLocation()});
    }


    public void createPlot(Player player, String name, Location[] locations) {

        final Location initPlace = locations[0];
        final Location location = locations[1];

        PlayerPlot playerPlot = new PlayerPlot(name, player.getUniqueId(), location);
        PlotCreateEvent event = new PlotCreateEvent(player, playerPlot);
        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        LostShardPlugin.getPlotManager().addPlot(playerPlot, true);
        player.sendMessage(ChatColor.GOLD + "You have successfully created the plot \"" + playerPlot.getName() + "\".");
        build(location, player);

        BlockFace face = HelperMethods.getClosestFace(initPlace, location);

        Block chestBlock = null;
        BlockFace blockToChest = null;
        BlockFace chestFacing;

        switch (face) {

            case NORTH:
            case UP:
            case DOWN:
            case NORTH_EAST:
            case NORTH_WEST:
            case NORTH_NORTH_WEST:
            case NORTH_NORTH_EAST:
            case SELF:
            default:
                blockToChest = BlockFace.EAST;
                chestFacing = BlockFace.NORTH;
                break;
            case EAST:
            case EAST_NORTH_EAST:
            case EAST_SOUTH_EAST:
                blockToChest = BlockFace.SOUTH;
                chestFacing = BlockFace.EAST;
                break;
            case SOUTH:
            case SOUTH_EAST:
            case SOUTH_WEST:
            case SOUTH_SOUTH_EAST:
            case SOUTH_SOUTH_WEST:
                blockToChest = BlockFace.WEST;
                chestFacing = BlockFace.SOUTH;
                break;
            case WEST:
            case WEST_NORTH_WEST:
            case WEST_SOUTH_WEST:
                blockToChest = BlockFace.NORTH;
                chestFacing = BlockFace.WEST;
                break;
        }
        chestBlock = location.getBlock().getRelative(blockToChest);

        chestBlock.breakNaturally();
        if (!LostShardPlugin.isTutorial()) {
            chestBlock.setType(Material.CHEST);

            Chest chest = (Chest) chestBlock.getState();
            chest.getBlockInventory().addItem(new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1), getChestBook());

            Directional directional = (Directional) chestBlock.getState().getBlockData();
            directional.setFacing(chestFacing.getOppositeFace());
            chestBlock.setBlockData(directional);
        } else {
            BlockData data = Material.CHEST.createBlockData();

            Directional directional = (Directional) data;
            directional.setFacing(chestFacing.getOppositeFace());

            event.getPlayer().sendBlockChange(chestBlock.getLocation(), data);


        }
    }

    public static Inventory getInventory(Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.CHEST, "Tutorial Plot Chest");
        inv.addItem(new ItemStack(Material.FEATHER, 1), new ItemStack(Material.REDSTONE, 1), getChestBook());
        return inv;
    }


    public static ItemStack getChestBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        bookMeta.setAuthor(net.md_5.bungee.api.ChatColor.DARK_PURPLE + "Nickolov");
        bookMeta.setTitle(net.md_5.bungee.api.ChatColor.DARK_PURPLE + "LostShard");
        bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);

        TextComponent tc = new TextComponent("");
        tc.setBold(false);
        tc.setColor(net.md_5.bungee.api.ChatColor.BLACK);

        TextComponent suggestions = new TextComponent("Suggestions:\n");
        suggestions.setColor(net.md_5.bungee.api.ChatColor.RED);
        suggestions.setBold(true);
        tc.addExtra(suggestions);

        TextComponent beginning = new TextComponent("1) Escape spawn\n");
        tc.addExtra(beginning);
        tc.addExtra(new TextComponent("2) Build a base\n"));
        tc.addExtra(new TextComponent("3) Claim your base\n"));
        tc.addExtra(new TextComponent("4) Make a clan\n"));
        tc.addExtra(new TextComponent("5) Capture Hostility\n\n"));

        TextComponent helpComponent = new TextComponent("For help:\n");
        helpComponent.setColor(net.md_5.bungee.api.ChatColor.RED);
        helpComponent.setBold(true);
        tc.addExtra(helpComponent);

        TextComponent youtubeComponent = new TextComponent("Click: Youtube" + "\n\n");
        youtubeComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official LostShard Youtube Channel").create()));
        youtubeComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, YoutubeCommand.LINK));
        tc.addExtra(youtubeComponent);

        TextComponent wikiDocComponent = new TextComponent("Click: Doc" + "\n\n");
        wikiDocComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official LostShard Google Doc").create()));
        wikiDocComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DocCommand.LINK));
        tc.addExtra(wikiDocComponent);

        TextComponent wikiComponent = new TextComponent("Click: Wiki" + "\n\n");
        wikiComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official LostShard Wiki").create()));
        wikiComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WikiCommand.LINK));
        tc.addExtra(wikiComponent);

        TextComponent discordComponent = new TextComponent("Click: Discord" + "\n\n");
        discordComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Redirects to the official Discord server").create()));
        discordComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DiscordCommand.LINK));


        bookMeta.spigot().addPage(new BaseComponent[]{tc}, new BaseComponent[]{discordComponent});
        book.setItemMeta(bookMeta);
        return book;
    }

    /**
     * Builds redstone 5 blocks distance
     *
     * @param location Location of Banner block
     */
    public void build(final Location location, final Player player) {
        if (location == null)
            return;

        Location cloneLoc = location.clone();

        cloneLoc.setZ(location.getBlockZ() + PlayerPlot.getDefaultRadius());
        for (int x = location.getBlockX() - PlayerPlot.getDefaultRadius(); x <= location.getBlockX() + PlayerPlot.getDefaultRadius(); x++) {
            cloneLoc.setX(x);

            int y = getRedstoneY(cloneLoc);
            if (y == -1)
                continue;

            cloneLoc.setY(y);

            if (x == location.getBlockX() - PlayerPlot.getDefaultRadius() || x == location.getBlockX() + PlayerPlot.getDefaultRadius()) {
                if (!LostShardPlugin.isTutorial())
                    cloneLoc.getBlock().setType(Material.REDSTONE_TORCH);
                else
                    player.sendBlockChange(cloneLoc, Material.REDSTONE_TORCH.createBlockData());

            } else {
                if (!LostShardPlugin.isTutorial())
                    cloneLoc.getBlock().setType(Material.REDSTONE_WIRE);
                else
                    player.sendBlockChange(cloneLoc, Material.REDSTONE_WIRE.createBlockData());
            }
            cloneLoc.setY(location.getBlockY());

        }

        cloneLoc.setZ(location.getBlockZ() - PlayerPlot.getDefaultRadius());

        for (int x = location.getBlockX() - PlayerPlot.getDefaultRadius(); x <= location.getBlockX() + PlayerPlot.getDefaultRadius(); x++) {
            cloneLoc.setX(x);
            int y = getRedstoneY(cloneLoc);
            if (y == -1)
                continue;

            cloneLoc.setY(y);

            if (x == location.getBlockX() - PlayerPlot.getDefaultRadius() || x == location.getBlockX() + PlayerPlot.getDefaultRadius()) {
                if (!LostShardPlugin.isTutorial())
                    cloneLoc.getBlock().setType(Material.REDSTONE_TORCH);
                else
                    player.sendBlockChange(cloneLoc, Material.REDSTONE_TORCH.createBlockData());
            } else {
                if (!LostShardPlugin.isTutorial())
                    cloneLoc.getBlock().setType(Material.REDSTONE_WIRE);
                else
                    player.sendBlockChange(cloneLoc, Material.REDSTONE_WIRE.createBlockData());
            }
            cloneLoc.setY(location.getBlockY());

        }

        cloneLoc.setX(location.getBlockX() + PlayerPlot.getDefaultRadius());

        for (int z = location.getBlockZ() - PlayerPlot.getDefaultRadius(); z <= location.getBlockZ() + PlayerPlot.getDefaultRadius(); z++) {
            cloneLoc.setZ(z);

            if (z == location.getBlockZ() - PlayerPlot.getDefaultRadius() || z == location.getBlockZ() + PlayerPlot.getDefaultRadius())
                continue;

            int y = getRedstoneY(cloneLoc);
            if (y == -1)
                continue;

            cloneLoc.setY(y);
            if (!LostShardPlugin.isTutorial())
                cloneLoc.getBlock().setType(Material.REDSTONE_WIRE);
            else
                player.sendBlockChange(cloneLoc, Material.REDSTONE_WIRE.createBlockData());
            cloneLoc.setY(location.getBlockY());
        }
        cloneLoc.setX(location.getBlockX() - PlayerPlot.getDefaultRadius());

        for (int z = location.getBlockZ() - PlayerPlot.getDefaultRadius(); z <= location.getBlockZ() + PlayerPlot.getDefaultRadius(); z++) {
            cloneLoc.setZ(z);

            if (z == location.getBlockZ() - PlayerPlot.getDefaultRadius() || z == location.getBlockZ() + PlayerPlot.getDefaultRadius())
                continue;

            int y = getRedstoneY(cloneLoc);
            if (y == -1)
                continue;

            cloneLoc.setY(y);
            if (!LostShardPlugin.isTutorial())
                cloneLoc.getBlock().setType(Material.REDSTONE_WIRE);
            else
                player.sendBlockChange(cloneLoc, Material.REDSTONE_WIRE.createBlockData());
            cloneLoc.setY(location.getBlockY());
        }

        if (LostShardPlugin.isTutorial()) {
            player.spawnParticle(Particle.TOTEM, location, 100, 5, 10, 5);
            player.playSound(location, Sound.BLOCK_NOTE_BLOCK_CHIME, 10.0f, 0f);
        } else {
            location.getWorld().spawnParticle(Particle.TOTEM, location, 100, 5, 10, 5);
            location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_CHIME, 10.0f, 0f);
        }

    }

    public int getRedstoneY(Location location) {
        Location cloneLoc = location.clone();

        if (cloneLoc.getBlock().getType().isAir()) {
            for (int y = cloneLoc.getBlockY(); y != 0 && (!cloneLoc.getBlock().getType().isAir() || cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType().isAir()); y--) {
                cloneLoc.setY(y);
                if (y >= 1 && y < cloneLoc.getWorld().getMaxHeight() && cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.REDSTONE_TORCH)
                    return -1;
            }

        } else {
            if (cloneLoc.getBlock().getType() == Material.REDSTONE_TORCH)
                return -1;
            for (int y = cloneLoc.getBlockY(); y != cloneLoc.getWorld().getMaxHeight() && (!cloneLoc.getBlock().getType().isAir() || cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType().isAir()); y++) {
                cloneLoc.setY(y);
                if (y >= 1 && y < cloneLoc.getWorld().getMaxHeight() && cloneLoc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.REDSTONE_TORCH)
                    return -1;
            }

        }

        return cloneLoc.getBlockY();
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
        if (!PlotBanner.getInstance().getID().equals(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1)))
            return;
        event.setCancelled(true);
    }

    private final HashSet<UUID> setToGivePlotBanner = new HashSet<>();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if (isException(event.getPlayer()))
            return;
        if (!map.containsKey(event.getPlayer().getUniqueId()))
            return;
        Block bannerBlock = map.get(event.getPlayer().getUniqueId())[1].getBlock();
        map.remove(event.getPlayer().getUniqueId());
        remove(bannerBlock, event.getPlayer(), true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (isException(event.getEntity()))
            return;
        if (map.containsKey(event.getEntity().getUniqueId())) {
            Block bannerBlock = map.get(event.getEntity().getUniqueId())[1].getBlock();
            map.remove(event.getEntity().getUniqueId());
            setToGivePlotBanner.add(event.getEntity().getUniqueId());
            remove(bannerBlock, event.getEntity(), false);
            return;
        }
        Iterator<ItemStack> iterator = event.getDrops().iterator();

        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null || item.getItemMeta().getLore().isEmpty())
                continue;
            if (!PlotBanner.getInstance().getID().equals(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1)))
                continue;
            iterator.remove();
            setToGivePlotBanner.add(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if (isException(event.getPlayer()))
            return;
        if (!setToGivePlotBanner.contains(event.getPlayer().getUniqueId()))
            return;
        event.getPlayer().getInventory().addItem(PlotBanner.getInstance().getItem());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        ItemStack cursor = event.getOldCursor();

        if (cursor != null) {
            if (cursor != null && cursor.getItemMeta() != null && cursor.getItemMeta().getLore() != null && !cursor.getItemMeta().getLore().isEmpty() && PlotBanner.getInstance().getID().equals(cursor.getItemMeta().getLore().get(cursor.getItemMeta().getLore().size() - 1)) && (!(event.getView().getTopInventory().getType() == InventoryType.CRAFTING || event.getView().getBottomInventory() == event.getInventory()))) {
//                Bukkit.broadcastMessage("1DEBUG: Cursor on top inventory with banner on cursor. Canceled. Returned.");

                event.setCancelled(true);
                return;
            }

        }
    }

    @EventHandler
    public void onInv(InventoryClickEvent event) {

        if (event.getClick() == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            if (hotbarItem != null && hotbarItem.getItemMeta() != null && hotbarItem.getItemMeta().getLore() != null && !hotbarItem.getItemMeta().getLore().isEmpty() && PlotBanner.getInstance().getID().equals(hotbarItem.getItemMeta().getLore().get(hotbarItem.getItemMeta().getLore().size() - 1)) && event.getClickedInventory() == event.getView().getTopInventory()) {
                if (event.getView().getTopInventory().getType() != InventoryType.CRAFTING)
                    event.setCancelled(true);
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

        if (isException(player))
            return;


        ItemStack cursor = event.getCursor();

        if (cursor != null) {
            if (cursor != null && cursor.getItemMeta() != null && cursor.getItemMeta().getLore() != null && !cursor.getItemMeta().getLore().isEmpty() && PlotBanner.getInstance().getID().equals(cursor.getItemMeta().getLore().get(cursor.getItemMeta().getLore().size() - 1)) && event.getClickedInventory() == event.getView().getTopInventory()) {
//                Bukkit.broadcastMessage("DEBUG: Cursor on top inventory with banner on cursor. Canceled. Returned.");
                event.setCancelled(true);
                return;
            }

        }


        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null || item.getItemMeta().getLore().isEmpty())
            return;
        if (!PlotBanner.getInstance().getID().equals(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1)))
            return;


        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
//            Bukkit.broadcastMessage("DEBUG: Moved item AND not player inventory. Canceled. Returned.");
            event.setCancelled(true);
            return;
        }


        if (event.getView().getTopInventory() == null) {
//            Bukkit.broadcastMessage("DEBUG: Top view is null. Canceled. Returned.");
            event.setCancelled(true);
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
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        if (isException(event.getPlayer()))
            return;
        ItemStack item = event.getItemDrop().getItemStack();
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null || item.getItemMeta().getLore().isEmpty())
            return;
        if (!PlotBanner.getInstance().getID().equals(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1)))
            return;
        event.setCancelled(true);
    }

    private boolean isException(Player player) {
        return player.getGameMode() == GameMode.CREATIVE;
    }

}
