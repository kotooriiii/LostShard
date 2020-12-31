package com.github.kotooriiii.tutorial.default_chapters.volume2;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.Zone;
import com.github.kotooriiii.tutorial.AbstractChapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IntroMiningChapter extends AbstractChapter {

    private int goldCounter, ironCounter;
    private static int GOLD_MAX = 20, IRON_MAX = 12;
    private Zone zone;
    private boolean isComplete, hasAnnouncedGold, hasAnnouncedIron;


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

        player.removePotionEffect(PotionEffectType.SPEED);

        LostShardPlugin.getSkillManager().getSkillPlayer(player.getUniqueId()).getActiveBuild().getMining().setLevel(49.0f);

        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);
        LostShardPlugin.getTutorialManager().getHologramManager().next(getUUID(), false);

        sendMessage(player, "Wow, this mine has a lot of gold in it. It's probably a good idea to mine it all...\nGrab the pickaxe out of the chest.", ChapterMessageType.HOLOGRAM_TO_TEXT);

    }

    @EventHandler
    public void onListen(InventoryOpenEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;

        if (event.isCancelled())
            return;

        final String name = ChatColor.MAGIC + "" + ChatColor.LIGHT_PURPLE + "l" + ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "Super Iron Pickaxe Holder" + ChatColor.RESET + "" + ChatColor.MAGIC + ChatColor.LIGHT_PURPLE + "l";

        if (event.getView().getTitle().equals(name))
            return;


        event.setCancelled(true);

        Inventory inventory = Bukkit.createInventory(event.getPlayer(), 9, name);
        ItemStack itemStack = new ItemStack(Material.IRON_PICKAXE, 1);
        itemStack.addEnchantment(Enchantment.DIG_SPEED, 5);
        itemStack.addEnchantment(Enchantment.DURABILITY, 1);
        inventory.addItem(itemStack);
        event.getPlayer().openInventory(inventory);
    }

    @EventHandler
    public void onHit(BlockBreakEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if(hasAnnouncedGold && event.getBlock().getType() == Material.GOLD_ORE)
        {
            sendMessage(event.getPlayer(), "You won't need all this gold...", ChapterMessageType.HELPER);
            event.setCancelled(true);
            return;
        }

        if(hasAnnouncedIron && event.getBlock().getType() == Material.IRON_ORE)
        {
            sendMessage(event.getPlayer(), "You won't need all this iron...", ChapterMessageType.HELPER);
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onGold(EntityPickupItemEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!player.getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (hasAnnouncedGold)
            return;

        ItemStack itemStack = event.getItem().getItemStack();

        if (itemStack.getType() != Material.GOLD_INGOT)
            return;


        goldCounter += itemStack.getAmount();

        if (goldCounter >= GOLD_MAX && !player.getInventory().contains(Material.GOLD_INGOT, GOLD_MAX)) {
            hasAnnouncedGold = true;

            if (ironCounter < IRON_MAX)
                sendMessage(player, "Get the iron ore as well!", ChapterMessageType.HELPER);
            else
                sendMessage(player, "Let's continue exploring.", ChapterMessageType.HELPER);
            return;
        }

        if (goldCounter > GOLD_MAX) {
            hasAnnouncedGold = true;

            sendMessage(player, "You won't need all this gold...", ChapterMessageType.HELPER);
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onIron(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!player.getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (hasAnnouncedIron)
            return;

        ItemStack itemStack = event.getItem().getItemStack();

        if (itemStack.getType() != Material.IRON_INGOT)
            return;

        ironCounter += itemStack.getAmount();

        if (ironCounter >= IRON_MAX && !player.getInventory().contains(Material.IRON_INGOT, IRON_MAX)) {
            hasAnnouncedIron = true;
            if (goldCounter < GOLD_MAX)
                sendMessage(player, "Get the gold ore as well!", ChapterMessageType.HELPER);
            else
                sendMessage(player, "Let's continue exploring.", ChapterMessageType.HELPER);

            return;
        }

        if (ironCounter > IRON_MAX) {
            hasAnnouncedIron = true;
            sendMessage(player, "You won't need all this iron...", ChapterMessageType.HELPER);
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getUUID()))
            return;
        if (!isActive())
            return;
        if (event.getItemDrop().getItemStack().getType() == Material.IRON_INGOT)
            hasAnnouncedIron = false;

        else if (event.getItemDrop().getItemStack().getType() == Material.GOLD_INGOT)
            hasAnnouncedGold = false;


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
                s += ironNeeded * 2 + " more iron.";
            if (goldNeeded > 0)
                if (startLen == s.length())
                    s += goldNeeded * 2 + " more gold.";
                else
                    s = s.substring(0, s.length() - 1) + " and " + goldNeeded * 2 + " more gold.";


            sendMessage(event.getPlayer(), s, ChapterMessageType.HELPER);
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
            sendMessage(event.getPlayer(), "Your pickaxe is getting low. Find a place to fix it!", ChapterMessageType.HELPER);
        }
    }

    @Override
    public void onDestroy() {

    }
}
