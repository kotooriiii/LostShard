package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class IntroMiningChapter extends AbstractChapter {

    private int goldCounter, ironCounter;
    private static int GOLD_MAX = 10, IRON_MAX = 3;
    private Zone zone;
    private boolean isComplete;


    public IntroMiningChapter() {
        goldCounter = ironCounter = 0;
        isComplete = false;

        this.zone = new Zone(370, 341, 42, 38, 723, 715);
    }

    @Override
    public void onBegin() {

        Player player = Bukkit.getPlayer(getUUID());
        if (player == null)
            return;

        LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getMining().setLevel(49.0f);

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        sendMessage(player, "Wow, this mine has a lot of gold in it. It's probably a good idea to mine it all...\nGrab the pickaxe out of the chest.");

    }

    @EventHandler
    public void onListen(InventoryOpenEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if (event.isCancelled())
            return;

        final String name = ChatColor.GRAY + "Iron Pickaxe Holder";

        if (event.getView().getTitle().equals(name))
            return;


        event.setCancelled(true);

        Inventory inventory = Bukkit.createInventory(event.getPlayer(), 9, name);
        inventory.addItem(new ItemStack(Material.IRON_PICKAXE, 1));
        event.getPlayer().openInventory(inventory);
    }


    @EventHandler
    public void onGold(BlockBreakEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if (!event.getBlock().getType().equals(Material.GOLD_ORE))
            return;

        goldCounter++;
        if (goldCounter == GOLD_MAX) {
            if (ironCounter < IRON_MAX)
                sendMessage(event.getPlayer(), "Get the iron ore as well!");
            else
                sendMessage(event.getPlayer(), "Let's continue exploring.");
            return;
        }

        if (goldCounter >= GOLD_MAX + 5) {
            sendMessage(event.getPlayer(), "You won't need all this gold...");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onIron(BlockBreakEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if (!event.getBlock().getType().equals(Material.IRON_ORE))
            return;

        ironCounter++;
        if (ironCounter == IRON_MAX) {
            if (goldCounter < GOLD_MAX)
                sendMessage(event.getPlayer(), "Get the gold ore as well!");
            else
                sendMessage(event.getPlayer(), "Let's continue exploring.");

            return;
        }

        if (ironCounter >= IRON_MAX + 5) {
            sendMessage(event.getPlayer(), "You won't need all this iron...");
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void onProximity(PlayerMoveEvent event) {
        if (isComplete)
            return;
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (zone == null)
            return;

        Location to = event.getTo();
        if (!zone.contains(to))
            return;

        if (!(ironCounter >= IRON_MAX && goldCounter >= GOLD_MAX)) {
            int ironNeeded = IRON_MAX - ironCounter;
            int goldNeeded = GOLD_MAX - goldCounter;
            String s = "You do not have enough to continue, you need ";
            final int startLen = s.length();
            if (ironNeeded > 0)
                s += ironNeeded + " more iron.";
            if (goldNeeded > 0)
                if (startLen == s.length())
                    s += goldNeeded + " more gold.";
                else
                    s = s.substring(0, s.length() - 1) + " and " + goldNeeded + " more gold.";


            sendMessage(event.getPlayer(), s);
            event.setCancelled(true);
            return;
        }

        isComplete = true;
        setComplete();
    }

    @EventHandler
    public void itemDmg(PlayerItemDamageEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        int dmg = event.getDamage() + 15;

        if (event.getItem().getType().getMaxDurability() <= dmg) {
            sendMessage(event.getPlayer(), "Your pickaxe is getting low. Find a place to fix it!");
        }
    }

    @Override
    public void onDestroy() {

    }
}
