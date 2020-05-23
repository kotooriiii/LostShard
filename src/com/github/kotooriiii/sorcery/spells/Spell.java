package com.github.kotooriiii.sorcery.spells;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.skills.listeners.BrawlingListener;
import com.github.kotooriiii.sorcery.spells.type.*;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.localBroadcast;

public abstract class Spell {

    private SpellType type;
    private ChatColor chatColor;
    private ItemStack[] ingredients;
    private double cooldown;
    private int manaCost;

    private boolean isWandable;

    protected static HashSet<Location> locationSavedForNoDrop = new HashSet<>();

    public Spell(SpellType type, ChatColor color, ItemStack[] ingredients, double cooldown, int manaCost, boolean isWandable) {
        this.type = type;
        this.chatColor = color;
        this.ingredients = ingredients;
        this.cooldown = cooldown;
        this.manaCost = manaCost;
        this.isWandable = isWandable;
    }

    public static Spell of(SpellType type) {
        switch (type) {

            case FIREBALL:
                return new FireballSpell();
            case HEAL:
                return new HealSpell();
            case ICE:
                return new IceSpell();
            case LIGHTNING:
                return new LightningSpell();
            case TELEPORT:
                return new TeleportSpell();
            case WEB_FIELD:
                return new WebFieldSpell();
            default:
                return null;
        }
    }

    public SpellType getType() {
        return type;
    }

    public String[] getNames() {
        return type.getNames();
    }

    public String getName() {
        return type.getNames()[0];
    }

    public ChatColor getColor() {
        return chatColor;
    }

    public ItemStack[] getIngredients() {
        return ingredients;
    }

    public double getCooldown() {
        return cooldown;
    }

    public int getManaCost() {
        return manaCost;
    }

    public static boolean isLapisNearby(Location location, int range) {

        int xmin = location.getBlockX() - range;
        int xmax = location.getBlockX() + range;
        int ymin = location.getBlockY() - range;
        int ymax = location.getBlockY() + range;
        int zmin = location.getBlockZ() - range;
        int zmax = location.getBlockZ() + range;

        for (int x = xmin; x <= xmax; x++) {
            for (int y = ymin; y <= ymax; y++) {
                for (int z = zmin; z <= zmax; z++) {
                    if (location.getWorld().getBlockAt(x, y, z).getType().equals(Material.LAPIS_BLOCK))
                        return true;
                }
            }
        }
        return false;
    }

    public boolean hasIngredients(Player player) {
        ItemStack[] ingredients = this.getIngredients();
        ArrayList<HashMap<Integer, Integer>> pendingRemovalItems = new ArrayList<>();

        boolean hasIngredients = true;
        for (ItemStack ingredient : ingredients) {
            HashMap<Integer, Integer> indeces = hasIngredient(player, ingredient);
            if (indeces == null) {
                hasIngredients = false;
                continue;
            }
            pendingRemovalItems.add(indeces);
        }

        return hasIngredients;
    }

    public boolean removeIngredients(Player player) {
        ItemStack[] ingredients = this.getIngredients();
        ArrayList<HashMap<Integer, Integer>> pendingRemovalItems = new ArrayList<>();
        PlayerInventory currentInventory = player.getInventory();

        boolean hasIngredients = true;
        for (ItemStack ingredient : ingredients) {
            HashMap<Integer, Integer> indeces = hasIngredient(player, ingredient);
            if (indeces == null) {
                hasIngredients = false;
                continue;
            }
            pendingRemovalItems.add(indeces);
        }

        if (hasIngredients) {
            for (HashMap<Integer, Integer> indeces : pendingRemovalItems) {
                for (Map.Entry entry : indeces.entrySet()) {
                    int key = (Integer) entry.getKey();
                    ItemStack itemStack = currentInventory.getItem(key);
                    int value = (Integer) entry.getValue();
                    if (value == 0)
                        currentInventory.setItem(key, null);
                    else {
                        itemStack.setAmount(value);
                        currentInventory.setItem(key, itemStack);
                    }
                }
            }
        }

        return hasIngredients;
    }

    public HashMap<Integer, Integer> hasIngredient(Player player, ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        int amountRequired = itemStack.getAmount();
        Material materialType = itemStack.getType();

        int counterMoney = 0;

        //Inventory slot index , int amount
        HashMap<Integer, Integer> hashmap = new HashMap<>();

        for (int i = 0; i < contents.length; i++) {
            ItemStack iteratingItem = contents[i];
            if (iteratingItem == null || !materialType.equals(iteratingItem.getType()))
                continue;
            int iteratingCount = iteratingItem.getAmount();
            int tempTotal = iteratingCount + counterMoney;

            if (amountRequired >= tempTotal) {
                counterMoney += iteratingCount;
                hashmap.put(new Integer(i), 0);
            } else if (amountRequired < tempTotal) {
                int tempLeftover = tempTotal - amountRequired;
                hashmap.put(new Integer(i), tempLeftover);
                counterMoney = amountRequired;
                break;
            }
        }

        if (counterMoney < amountRequired) {
            player.sendMessage(ERROR_COLOR + "You don't have " + amountRequired + " " + materialType.getKey().getKey().toLowerCase() + " to cast this spell!");
            return null;
        }
        return hashmap;
    }

    private Block[] getCornerBlocks(Block block) {
        Location targetLoc = block.getLocation();
        Block[] blocks = new Block[8];
        blocks[0] = new Location(targetLoc.getWorld(), targetLoc.getBlockX() + 1, targetLoc.getBlockY() + 1, targetLoc.getBlockZ() - 1).getBlock();
        blocks[1] = new Location(targetLoc.getWorld(), targetLoc.getBlockX() + 1, targetLoc.getBlockY() + 1, targetLoc.getBlockZ()).getBlock();
        blocks[2] = new Location(targetLoc.getWorld(), targetLoc.getBlockX() + 1, targetLoc.getBlockY() + 1, targetLoc.getBlockZ() + 1).getBlock();
        blocks[3] = new Location(targetLoc.getWorld(), targetLoc.getBlockX(), targetLoc.getBlockY() + 1, targetLoc.getBlockZ() - 1).getBlock();
        blocks[4] = new Location(targetLoc.getWorld(), targetLoc.getBlockX(), targetLoc.getBlockY() + 1, targetLoc.getBlockZ() + 1).getBlock();
        blocks[5] = new Location(targetLoc.getWorld(), targetLoc.getBlockX() - 1, targetLoc.getBlockY() + 1, targetLoc.getBlockZ() - 1).getBlock();
        blocks[6] = new Location(targetLoc.getWorld(), targetLoc.getBlockX() - 1, targetLoc.getBlockY() + 1, targetLoc.getBlockZ()).getBlock();
        blocks[7] = new Location(targetLoc.getWorld(), targetLoc.getBlockX() - 1, targetLoc.getBlockY() + 1, targetLoc.getBlockZ() + 1).getBlock();

        return blocks;
    }



    public BlockFace getInvertBlockFace(Player player, final int range){
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, range);
        if (lastTwoTargetBlocks.size() != 2) return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return adjacentBlock.getFace(targetBlock);
    }


    public static HashSet<Location> getLocationsForNonBlockBreak() {
        return locationSavedForNoDrop;
    }

    public boolean hasCastingRequirements(Player player) {
        if (BrawlingListener.isStunned(player.getUniqueId())) {
            player.sendMessage(BrawlingListener.getStunMessage(player.getUniqueId()));
            return false;
        }

        if (this.isLapisNearby(player.getLocation(), 5)) {
            player.sendMessage(ERROR_COLOR + "You cannot seem to cast a spell here...");
            return false;
        }

        if(!this.hasIngredients(player))
            return false;

        // Don't execute any action if the player is on cooldown
        if (isCooldown(player))
            return false;

        Stat stat = Stat.getStatMap().get(player.getUniqueId());

        if (stat.getMana() < this.getManaCost()) {
            player.sendMessage(ERROR_COLOR + "You don't have enough mana to cast " + this.getName() + ".");
            return false;
        }
        return true;
    }

    public abstract boolean isCooldown(Player player);

    public abstract boolean executeSpell(Player player);

    public abstract void updateCooldown(Player player);

    public void cast(Player player)
    {

        if(!hasCastingRequirements(player))
            return;

        // Run the wand action
        if (!executeSpell(player))
            return;

        Stat stat = Stat.wrap(player.getUniqueId());
        stat.setMana(stat.getMana() - this.getManaCost());
        removeIngredients(player);

        localBroadcast(player, this.getName());
        updateCooldown(player);
    }

}
