package com.github.kotooriiii.sorcery.wands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.hostility.HostilityMatch;
import com.github.kotooriiii.hostility.HostilityPlatform;
import com.github.kotooriiii.skills.listeners.BrawlingListener;
import com.github.kotooriiii.stats.Stat;
import com.github.kotooriiii.status.Status;
import com.github.kotooriiii.status.StatusPlayer;
import com.google.common.base.Function;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.activeHostilityGames;
import static com.github.kotooriiii.util.HelperMethods.localBroadcast;

public class Wand {

    private WandType type;

    private static HashMap<UUID, Object[]> wandOnCooldown = new HashMap<>();

    public Wand(WandType type) {
        // Set local variables to the given variables
        this.type = type;
    }

    public ItemStack createItem() {

        // Create stick
        ItemStack wandItem = new ItemStack(Material.STICK, 1);

        // Get sticks meta data
        ItemMeta wandMeta = wandItem.getItemMeta();

        // Set stick name based on input
        wandMeta.setDisplayName(type.getChatColor() + type.getNames()[0] + " Wand");

        // Add stick enchantment
        Glow glow = new Glow(new NamespacedKey(LostShardPlugin.plugin, "GlowCustomEnchant"));
        wandMeta.addEnchant(glow, 1, true);

        // Set stick lore
        List<String> lore = new ArrayList<>();
        lore.add(type.getChatColor() + "Left click to use this wand.");
        lore.add(type.getChatColor() + "The wand has a cooldown of " + type.getCooldown() + " seconds.");
        lore.add(type.getChatColor() + "The wand has a mana cost of " + type.getManaCost() + " mana.");
        lore.add("ID:" + type.getNames()[0]);
        wandMeta.setLore(lore);
        wandItem.setItemMeta(wandMeta);
        // Return the finished want item
        return wandItem;
    }

    public static boolean isWielding(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty())
            return false;
        for (WandType type : WandType.values()) {
            String lastLine = itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1);
            if (lastLine.equals("ID:" + type.getNames()[0]))
                return true;
        }
        return false;
    }

    public static WandType getWielding(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty())
            return null;
        for (WandType type : WandType.values()) {
            String lastLine = itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1);
            if (lastLine.equals("ID:" + type.getNames()[0]))
                return WandType.matchWandType(type.getNames()[0]);
        }
        return null;
    }

    public boolean hasIngredients(Player player) {
        ItemStack[] ingredients = type.getIngredients();
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
        ItemStack[] ingredients = type.getIngredients();
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

    public boolean isCooldown(Player player) {
        if (wandOnCooldown.containsKey(player.getUniqueId()) && ((WandType) wandOnCooldown.get(player.getUniqueId())[0]).equals(type)) {
            Object[] properties = wandOnCooldown.get(player.getUniqueId());
            DecimalFormat df = new DecimalFormat("##.##");
            double timeOnCooldown = ((Double) properties[1]).doubleValue() / 20;
            BigDecimal bd = new BigDecimal(timeOnCooldown).setScale(0, RoundingMode.UP);
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


    public void cast(Player player) {

        if (BrawlingListener.isStunned(player.getUniqueId())) {
            player.sendMessage(BrawlingListener.getStunMessage(player.getUniqueId()));
            return;
        }

        // Don't execute any action if the player is on cooldown
        if (isCooldown(player))
            return;

        Stat stat = Stat.getStatMap().get(player.getUniqueId());

        if (stat.getMana() < type.getManaCost()) {
            player.sendMessage(ERROR_COLOR + "You don't have enough mana to cast " + this.type.getNames()[0] + ".");
            return;
        }

        // Run the wand action
        if (!executeSpell(player))
            return;
        stat.setMana(stat.getMana() - type.getManaCost());
        removeIngredients(player);

        localBroadcast(player, type.getNames()[0]);

        // Add player to cooldown list
        wandOnCooldown.put(player.getUniqueId(), new Object[]{type, new Double(type.getCooldown() * 20)});
        // This runnable will remove the player from cooldown list after a given time
        BukkitRunnable runnable = new BukkitRunnable() {
            final double cooldown = type.getCooldown() * 20;
            int counter = 0;

            @Override
            public void run() {

                if (counter >= cooldown) {
                    wandOnCooldown.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                counter += 1;
                Object[] properties = wandOnCooldown.get(player.getUniqueId());
                properties[1] = new Double(cooldown - counter);
                wandOnCooldown.put(player.getUniqueId(), properties);
            }
        };
        runnable.runTaskTimer(LostShardPlugin.plugin, 0, 1);


    }

    public boolean executeSpell(Player player) {
        switch (type) {
            case FIREBALL:
                List<Block> lineOfSightFireball = player.getLineOfSight(null, 4);

                // Only spawn fireball if there is some room infront of player
//                if (lineOfSightFireball.size() < 4) {
//                    player.sendMessage(ERROR_COLOR + "You need more space to cast that spell!");
//                    return false;
//                }
                // Get location of block infront of player
                Location targetLocationFireball = lineOfSightFireball.get(lineOfSightFireball.size() - 1).getLocation();

                // Calculate fireball position from block position
                // Take the player rotation so it flies in the correct direction
                Location fireballLocation = new Location(
                        player.getWorld(),
                        targetLocationFireball.getX(),
                        targetLocationFireball.getY(),
                        targetLocationFireball.getZ(),
                        player.getLocation().getYaw(),
                        player.getLocation().getPitch());

                // Spawn fireball
                Fireball fireball = (Fireball) player.launchProjectile(Fireball.class, player.getEyeLocation().getDirection());
                Vector origVector = fireball.getDirection();
                Vector vector = new Vector(origVector.getX(), origVector.getY(), origVector.getZ()).multiply(2);
                fireball.setVelocity(vector);
                fireball.setDirection(vector);
                fireball.setShooter(player);
                break;
            case TELEPORT:
                final int rangeTeleport = 20;
                Location teleportLocation = teleportLocation(player, rangeTeleport);
                if (teleportLocation == null) {
                    player.sendMessage(ERROR_COLOR + "You need more room to cast this spell!");
                    return false;
                }
                if (!new Location(teleportLocation.getWorld(), teleportLocation.getX(), teleportLocation.getY() + 1, teleportLocation.getBlockZ()).getBlock().getType().equals(Material.AIR) && !new Location(teleportLocation.getWorld(), teleportLocation.getX(), teleportLocation.getY() - 1, teleportLocation.getBlockZ()).getBlock().getType().equals(Material.AIR)) {
                    player.sendMessage(ERROR_COLOR + "Invalid target.");
                    return false;
                } else if (new Location(teleportLocation.getWorld(), teleportLocation.getX(), teleportLocation.getY() - 1, teleportLocation.getBlockZ()).getBlock().getType().equals(Material.AIR)) {
                    teleportLocation.add(0, -1, 0);
                }

                player.teleport(new Location(teleportLocation.getWorld(), teleportLocation.getBlockX() + 0.5, teleportLocation.getBlockY(), teleportLocation.getBlockZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch()));
                break;
            case HEALING:
                double health = player.getHealth();
                health += 8;
                if (health > player.getMaxHealth())
                    health = player.getMaxHealth();
                player.setHealth(health);
                break;
            case ICE:
                // Get blocks in line of sight of player
                final int rangeIce = 32;
                final int despawnDelayIce = 7;
                List<Block> lineOfSightBlocks = player.getLineOfSight(null, rangeIce);

                // Get target block (last block in line of sight)
                final Location centerBlock = lineOfSightBlocks.get(lineOfSightBlocks.size() - 1).getLocation();
                //Should make it unable to spawn in the air
                if (centerBlock.getBlock().isEmpty()) {
                    player.sendMessage(ERROR_COLOR + "Invalid target.");
                    return false;
                }

                Material blockType = centerBlock.getBlock().getType();
                if (blockType != Material.LEGACY_LONG_GRASS)
                    centerBlock.add(0, 1, 0);

                if (!hasIceShape(centerBlock, Material.SNOW_BLOCK, block -> block.isEmpty() || block.getType() == Material.LEGACY_LONG_GRASS)) {
                    player.sendMessage(ERROR_COLOR + "You can not cast Ice Ball on hostility platforms.");
                    return false;
                }
                setIceShape(centerBlock, Material.SNOW_BLOCK, block -> block.isEmpty() || block.getType() == Material.LEGACY_LONG_GRASS);

                // This runnable will remove the player from cooldown list after a given time
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setIceShape(centerBlock, Material.AIR, block -> block.getType() == Material.SNOW_BLOCK);
                    }
                }.runTaskLater(LostShardPlugin.plugin, 20 * despawnDelayIce);

                break;
            case WEB:
                final int rangeWeb = 32;
                final int despawnDelayWeb = 7;
                // Get blocks in line of sight of player
                List<Block> lineOfSightWeb = player.getLineOfSight(null, rangeWeb);

                // Get target block (last block in line of sight)
                final Location centerBlockWeb = lineOfSightWeb.get(lineOfSightWeb.size() - 1).getLocation();
                //Should make it unable to spawn in the air
                if (centerBlockWeb.getBlock().isEmpty()) {
                    player.sendMessage(ERROR_COLOR + "Invalid target.");
                    return false;
                }

                Material blockWeb = centerBlockWeb.getBlock().getType();
                if (blockWeb != Material.LEGACY_LONG_GRASS)
                    centerBlockWeb.add(0, 1, 0);

                if (!hasWebShape(centerBlockWeb, Material.COBWEB, bottomLeft -> bottomLeft.isEmpty() || bottomLeft.getType() == Material.LEGACY_LONG_GRASS)) {
                    player.sendMessage(ERROR_COLOR + "You can not cast Web Field on hostility platforms.");
                    return false;
                }

                setWebShape(centerBlockWeb, Material.COBWEB, bottomLeft -> bottomLeft.isEmpty() || bottomLeft.getType() == Material.LEGACY_LONG_GRASS);

                // This runnable will remove the player from cooldown list after a given time
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setWebShape(centerBlockWeb, Material.AIR, block -> block.getType() == Material.COBWEB);
                    }
                }.runTaskLater(LostShardPlugin.plugin, 20 * despawnDelayWeb);
                break;
            case LIGHTNING:
                final int lightningRange = 50;
                List<Block> lineOfSightLightning = player.getLineOfSight(null, lightningRange);

                // Get target block (last block in line of sight)
                Location lightningLocation = lineOfSightLightning.get(lineOfSightLightning.size() - 1).getLocation();
                player.getWorld().strikeLightning(lightningLocation);
                callPlayerEvent(player, lightningLocation);
                break;
        }

        return true;
    }

    private void callPlayerEvent(Player attacker, Location location) {
        for (Player defender : Bukkit.getOnlinePlayers()) {
            if (defender.getLocation().getBlock().equals(location.getBlock()))
                Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(attacker
                        , defender, EntityDamageEvent.DamageCause.CUSTOM, 7));
        }
    }


    private boolean hasWebShape(Location centerBlock, Material material, Function<Block, Boolean> shouldReplace) {
        Location bottomLeft = centerBlock.clone().add(-2, 0, -2);
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());
                Boolean isEdge = (x == 0 && z == 0) || (x == 0 && z == 4) || (x == 4 && z == 0) || (x == 4 && z == 4);

                if (!isEdge && shouldReplaceBlock) {
                    for (HostilityMatch match : activeHostilityGames) {
                        HostilityPlatform platform = match.getPlatform();
                        if (platform.hasAdjacency(bottomLeft))
                            return false;
                    }
                }
                bottomLeft.add(0, 0, 1);

            }
            bottomLeft.add(1, 0, -5);
        }
        return true;
    }

    private void setWebShape(Location centerBlock, Material material, Function<Block, Boolean> shouldReplace) {
        Location bottomLeft = centerBlock.clone().add(-2, 0, -2);
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());
                Boolean isEdge = (x == 0 && z == 0) || (x == 0 && z == 4) || (x == 4 && z == 0) || (x == 4 && z == 4);

                if (!isEdge && shouldReplaceBlock)
                    bottomLeft.getBlock().setType(material);
                bottomLeft.add(0, 0, 1);

            }
            bottomLeft.add(1, 0, -5);
        }
    }

    private boolean hasIceShape(Location centerBlock, Material material, Function<Block, Boolean> shouldReplace) {

        //Layer 0 and 2
        Location bottomLeft = centerBlock.clone().add(-1, 0, -1);
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());
                    Boolean isEdge = (x == 0 && z == 0) || (x == 0 && z == 2) || (x == 2 && z == 0) || (x == 2 && z == 2);

                    if (shouldReplaceBlock && !isEdge) {
                        for (HostilityMatch match : activeHostilityGames) {
                            HostilityPlatform platform = match.getPlatform();
                            if (platform.hasAdjacency(bottomLeft))
                                return false;
                        }
                    }
                    bottomLeft.add(0, 0, 1);

                }
                bottomLeft.add(1, 0, -3);
            }
            bottomLeft = centerBlock.clone().add(-1, 2, -1);
        }

        //Layer 1
        bottomLeft = centerBlock.clone().add(-1, 1, -1);
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());

                if (shouldReplaceBlock) {
                    for (HostilityMatch match : activeHostilityGames) {
                        HostilityPlatform platform = match.getPlatform();
                        if (platform.hasAdjacency(bottomLeft))
                            return false;
                    }
                }
                bottomLeft.add(0, 0, 1);

            }
            bottomLeft.add(1, 0, -3);
        }

        return true;
    }

    private void setIceShape(Location centerBlock, Material material, Function<Block, Boolean> shouldReplace) {

        //Layer 0 and 2
        Location bottomLeft = centerBlock.clone().add(-1, 0, -1);
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());
                    Boolean isEdge = (x == 0 && z == 0) || (x == 0 && z == 2) || (x == 2 && z == 0) || (x == 2 && z == 2);

                    if (shouldReplaceBlock && !isEdge)
                        bottomLeft.getBlock().setType(material);
                    bottomLeft.add(0, 0, 1);

                }
                bottomLeft.add(1, 0, -3);
            }
            bottomLeft = centerBlock.clone().add(-1, 2, -1);
        }

        //Layer 1
        bottomLeft = centerBlock.clone().add(-1, 1, -1);
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Boolean shouldReplaceBlock = shouldReplace.apply(bottomLeft.getBlock());

                if (shouldReplaceBlock)
                    bottomLeft.getBlock().setType(material);
                bottomLeft.add(0, 0, 1);

            }
            bottomLeft.add(1, 0, -3);
        }


    }

    public Location teleportLocation(Player player, final int range) {

        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, range);

        Block targetBlock;
        if (lastTwoTargetBlocks.size() > 1)
            targetBlock = lastTwoTargetBlocks.get(1);
        else
            targetBlock = lastTwoTargetBlocks.get(0);


        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        if (new Location(adjacentBlock.getWorld(), adjacentBlock.getX(), adjacentBlock.getY() + 1, adjacentBlock.getZ()).getBlock().getType() == Material.AIR && new Location(targetBlock.getWorld(), targetBlock.getX(), targetBlock.getY()+1, targetBlock.getZ()).getBlock().getType() == Material.AIR) {
            adjacentBlock = new Location(adjacentBlock.getWorld(), targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ()).getBlock();
        }
        return adjacentBlock.getLocation();
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

    public BlockFace getBlockFace(Player player, final int range) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, range);
        if (lastTwoTargetBlocks.size() != 2) return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }

}
