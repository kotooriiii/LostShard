package com.github.kotooriiii.sorcery.spells;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.events.SpellCastEvent;
import com.github.kotooriiii.skills.skill_listeners.BrawlingListener;
import com.github.kotooriiii.sorcery.spells.type.circle1.*;
import com.github.kotooriiii.sorcery.spells.type.circle2.*;
import com.github.kotooriiii.sorcery.spells.type.circle3.*;
import com.github.kotooriiii.sorcery.spells.type.circle4.HealSpell;
import com.github.kotooriiii.sorcery.spells.type.circle4.LightningSpell;
import com.github.kotooriiii.sorcery.spells.type.circle4.ScreechSpell;
import com.github.kotooriiii.sorcery.spells.type.circle5.FireFieldSpell;
import com.github.kotooriiii.sorcery.spells.type.circle5.GateTravelSpell;
import com.github.kotooriiii.sorcery.spells.type.circle5.RespirateSpell;
import com.github.kotooriiii.sorcery.spells.type.circle5.WebFieldSpell;
import com.github.kotooriiii.sorcery.spells.type.circle6.*;
import com.github.kotooriiii.sorcery.spells.type.circle7.ClanTPSpell;
import com.github.kotooriiii.sorcery.spells.type.circle7.CleanseSpell;
import com.github.kotooriiii.sorcery.spells.type.circle7.RadiateSpell;
import com.github.kotooriiii.sorcery.spells.type.circle7.SilentWalkSpell;
import com.github.kotooriiii.sorcery.spells.type.circle8.*;
import com.github.kotooriiii.sorcery.spells.type.circle9.*;
import com.github.kotooriiii.stats.Stat;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.localBroadcast;

public abstract class Spell {

    private SpellType type;
    private String description;
    private int circle;
    private ChatColor chatColor;
    private ItemStack[] ingredients;
    private double cooldown;
    private int manaCost;

    private boolean isCastable;
    private boolean isWandable;
    private boolean isScrollable;

    protected final static HashSet<Location> locationSavedForNoDrop = new HashSet<>();
    protected final static HashMap<UUID, SpellType> waitingForArgumentMap = new HashMap<>();

    protected final static HashMap<SpellToggleable, HashSet<UUID>> getManaDrainMap = new HashMap<>();

    protected static final int DEFAULT_LAPIS_NEARBY = 5;

    public static HashMap<UUID, SpellType> getWaitingForArgumentMap() {
        return waitingForArgumentMap;
    }

    public Spell(SpellType type, String desc, int circle, ChatColor color, ItemStack[] ingredients, double cooldown, int manaCost, boolean isCastable, boolean isWandable, boolean isScrollable) {
        this.type = type;
        this.description = desc;
        this.circle = circle;
        this.chatColor = color;
        this.ingredients = ingredients;
        this.cooldown = cooldown;
        this.manaCost = manaCost;

        this.isCastable = isCastable;
        this.isWandable = isWandable;
        this.isScrollable = isScrollable;
    }

    public static Spell of(SpellType type) {
        switch (type) {

            case FIREBALL:
                return FireballSpell.getInstance();
            case HEAL:
                return HealSpell.getInstance();
            case ICE:
                return IceSpell.getInstance();
            case LIGHTNING:
                return LightningSpell.getInstance();
            case TELEPORT:
                return TeleportSpell.getInstance();
            case WEB_FIELD:
                return WebFieldSpell.getInstance();
//            case DARKNESS:
//                return new DarknessSpell();
//            case CLONE:
//                return new CloneSpell();
            case CLANTP:
                return ClanTPSpell.getInstance();
            case MARK:
                return MarkSpell.getInstance();
            case RECALL:
                return RecallSpell.getInstance();
            case PERMANENT_GATE_TRAVEL:
                return PermanentGateTravelSpell.getInstance();
            case CHRONOPORT:
                return ChronoportSpell.getInstance();
            case GRASS:
                return GrassSpell.getInstance();
            case LIGHT:
                return LightSpell.getInstance();
            case CREATE_FOOD:
                return CreateFoodSpell.getInstance();
            case BRIDGE:
                return BridgeSpell.getInstance();
            case WALL:
                return WallSpell.getInstance();
            case FLOWER:
                return FlowerSpell.getInstance();
            case MOON_JUMP:
                return MoonJumpSpell.getInstance();
            case MAGIC_ARROW:
                return MagicArrowSpell.getInstance();
            case SCREECH:
                return ScreechSpell.getInstance();
            case GATE_TRAVEL:
                return GateTravelSpell.getInstance();
            case HEAL_OTHER:
                return HealOtherSpell.getInstance();
            case RESPIRATE:
                return RespirateSpell.getInstance();
            case FIRE_FIELD:
                return FireFieldSpell.getInstance();
            case FIRE_WALK:
                return FireWalkSpell.getInstance();
            case WATER_WALK:
                return WaterWalkSpell.getInstance();
            case FORCE_PULL:
                return ForcePullSpell.getInstance();
            case FORCE_PUSH:
                return ForcePushSpell.getInstance();
            case CLEANSE:
                return CleanseSpell.getInstance();
            case RADIATE:
                return RadiateSpell.getInstance();
            case SILENT_WALK:
                return SilentWalkSpell.getInstance();
            case SOAR:
                return SoarSpell.getInstance();
            case PERCEPTION:
                return PerceptionSpell.getInstance();
            case DAY:
                return DaySpell.getInstance();
            case UNVEIL:
                return UnveilSpell.getInstance();
            case WRATH:
                return WrathSpell.getInstance();
            case ENVY:
                return EnvySpell.getInstance();
            case LUST:
                return LustSpell.getInstance();
            case GREED:
                return GreedSpell.getInstance();
            case SLOTH:
                return SlothSpell.getInstance();
            case PRIDE:
                return PrideSpell.getInstance();
            case GLUTTONY:
                return GluttonySpell.getInstance();
            default:
                return null;
        }
    }

    public static Spell[] getWandableSpells() {
        ArrayList<Spell> spells = new ArrayList<>();
        for (SpellType types : SpellType.oldMapValues()) {
            Spell spell = Spell.of(types);
            if (spell.isWandable()) {
                spells.add(spell);
            }
        }
        return spells.toArray(new Spell[spells.size()]);
    }

    public static Spell[] getCastableSpells() {
        ArrayList<Spell> spells = new ArrayList<>();
        for (SpellType types : SpellType.oldMapValues()) {
            Spell spell = Spell.of(types);
            if (spell.isCastable()) {
                spells.add(spell);
            }
        }
        return spells.toArray(new Spell[spells.size()]);
    }

    public static Spell[] getScrollableSpells() {
        ArrayList<Spell> spells = new ArrayList<>();
        for (SpellType types : SpellType.values()) {
            Spell spell = Spell.of(types);
            if (spell.isScrollable()) {
                spells.add(spell);
            }
        }
        return spells.toArray(new Spell[spells.size()]);
    }

    public static Spell[] getSpells() {
        ArrayList<Spell> spells = new ArrayList<>();
        for (SpellType types : SpellType.oldMapValues()) {
            Spell spell = Spell.of(types);

            spells.add(spell);

        }
        return spells.toArray(new Spell[spells.size()]);
    }

    public static Spell[] getCircleSpells(int circle) {
        ArrayList<Spell> spells = new ArrayList<>();
        for (SpellType types : SpellType.values()) {
            Spell spell = Spell.of(types);
            if (spell.getCircle() == circle)
                spells.add(spell);
        }
        return spells.toArray(new Spell[spells.size()]);
    }

    public static Spell[] getToggleableSpells() {
        ArrayList<Spell> spells = new ArrayList<>();
        for (SpellType types : SpellType.values()) {
            Spell spell = Spell.of(types);
            if (spell instanceof SpellToggleable)
                spells.add(spell);
        }
        return spells.toArray(new Spell[spells.size()]);
    }

    public static Spell[] getChanneleableSpells() {
        ArrayList<Spell> spells = new ArrayList<>();
        for (SpellType types : SpellType.values()) {
            Spell spell = Spell.of(types);
            if (spell instanceof SpellChanneleable)
                spells.add(spell);
        }
        return spells.toArray(new Spell[spells.size()]);
    }

    public static int getDefaultLapisNearbyValue() {
        return DEFAULT_LAPIS_NEARBY;
    }


    public SpellType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getCircle() {
        return circle;
    }

    public boolean isCastable() {
        return isCastable;
    }

    public boolean isWandable() {
        return isWandable;
    }

    public boolean isScrollable() {
        return isScrollable;
    }

    public String[] getNames() {
        return type.getNames();
    }

    public String getName() {
        return type.getNames()[0];
    }

    public String getLatin() {
        return type.getLatin();
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

    private HashMap<Integer, Integer> getIndeces(Player player, ItemStack ingredient) {
        HashMap<Integer, Integer> indeces;
        switch (ingredient.getType()) {
            case OAK_LEAVES:
                indeces = hasAnyEndMatchingIngredient(player, "_LEAVES", ingredient.getAmount(), "leaves");
                break;
            case WHITE_WOOL:
                indeces = hasAnyEndMatchingIngredient(player, "_WOOL", ingredient.getAmount(), "wool");
                break;
            default:
                indeces = hasIngredient(player, ingredient);
                break;
        }

        return indeces;
    }

    public boolean hasIngredients(Player player) {
        ItemStack[] ingredients = this.getIngredients();
        ArrayList<HashMap<Integer, Integer>> pendingRemovalItems = new ArrayList<>();

        boolean hasIngredients = true;
        for (ItemStack ingredient : ingredients) {

            HashMap<Integer, Integer> indeces = getIndeces(player, ingredient);


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

            HashMap<Integer, Integer> indeces = getIndeces(player, ingredient);

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

    public HashMap<Integer, Integer> hasAnyEndMatchingIngredient(Player player, String endsWith, int amountRequired, String outOfStockItemName) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        int counterMoney = 0;

        //Inventory slot index , int amount
        HashMap<Integer, Integer> hashmap = new HashMap<>();

        for (int i = 0; i < contents.length; i++) {
            ItemStack iteratingItem = contents[i];
            if (iteratingItem == null || !iteratingItem.getType().name().endsWith(endsWith))
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
            player.sendMessage(ERROR_COLOR + "You don't have " + amountRequired + " " + outOfStockItemName + " to cast this spell!");
            return null;
        }
        return hashmap;
    }

    public static HashSet<Location> getLocationsForNonBlockBreak() {
        return locationSavedForNoDrop;
    }

    public boolean hasCastingRequirements(Player player) {


        if (waitingForArgumentMap.containsKey(player.getUniqueId())) {
            player.sendMessage(ERROR_COLOR + "You are currently casting a spell.");
            return false;
        }

        //if a player is in creative, don't check other stuff.
        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }

        if (BrawlingListener.isStunned(player.getUniqueId())) {
            player.sendMessage(BrawlingListener.getStunMessage(player.getUniqueId()));
            return false;
        }

        if (this.isLapisNearby(player.getLocation(), DEFAULT_LAPIS_NEARBY)) {
            player.sendMessage(ERROR_COLOR + "You cannot seem to cast a spell here...");
            return false;
        }


        if (!this.hasIngredients(player))
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

    public final void refund(Player player) {
        player.getInventory().addItem(this.getIngredients());
        final Stat wrap = Stat.wrap(player.getUniqueId());
        wrap.setMana(wrap.getMana() + this.getManaCost());
    }

    public boolean cast(Player player) {

        if (this instanceof SpellToggleable) {
            if (hasDraining(player.getUniqueId())) {
                player.sendMessage(ERROR_COLOR + "You are already toggled on to this spell. Toggle off with /toggle " + this.getType().getName());
                return false;
            }

            if (Stat.getMeditatingPlayers().contains(player.getUniqueId())) {
                player.sendMessage(ERROR_COLOR + "You have stopped meditating.");
                Stat.getMeditatingPlayers().remove(player.getUniqueId());

            }

            if (Stat.getRestingPlayers().contains(player.getUniqueId())) {
                player.sendMessage(ERROR_COLOR + "You have stopped resting.");
                Stat.getRestingPlayers().remove(player.getUniqueId());

            }

        }

        SpellCastEvent spellCastEvent = new SpellCastEvent(player, this);
        LostShardPlugin.plugin.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled())
            return false;

        if (!hasCastingRequirements(player))
            return false;

        switch (this.getType()) {
            case TELEPORT:

                if (!executeSpell(player))
                    return false;
                localBroadcast(player, this.getLatin());
                break;
            default:

                localBroadcast(player, this.getLatin());

                if (!executeSpell(player))
                    return false;
                break;
        }

        if (this instanceof SpellChanneleable) {
            SpellChanneleable channeleable = (SpellChanneleable) this;


            final UUID uuid = player.getUniqueId();

            Spell spell = this;



            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {

                    if(isCancelled())
                        return;

                    if (channeleable.hasMember(uuid)) {
                        if (player.isOnline()) {
                            player.sendMessage(ERROR_COLOR + "You fail to cast " + spell.getName() + ". You had " + channeleable.size() + "/" + channeleable.getRequired() + ".");
                        }
                        channeleable.removeMember(uuid);
                    }
                }
            };

            channeleable.addMember(player, runnable.runTaskLater(LostShardPlugin.plugin, (long) (channeleable.getGraceTimeSeconds() * 20)));


            if (channeleable.hasRequiredMembers(player.getLocation())) {
                channeleable.executeSuccessfulChannelSpell(player, channeleable.getMembers(player.getLocation()));
            } else {
                channeleable.executeFailedChannelSpell(player, channeleable.getMembers(player.getLocation()));
            }
        }


        Stat stat = Stat.wrap(player.getUniqueId());
        stat.setMana(stat.getMana() - this.getManaCost());
        if (player.getGameMode() != GameMode.CREATIVE)
            removeIngredients(player);
        if (getCooldown() != 0.0d)
            updateCooldown(player);

        if (this instanceof SpellToggleable) {

            SpellToggleable toggleable = (SpellToggleable) this;

            HashSet<UUID> drainSet = getManaDrainMap.get(toggleable);
            if (drainSet == null) {
                drainSet = new HashSet<>();
                getManaDrainMap.put(toggleable, drainSet);
            }

            drainSet.add(player.getUniqueId());
        }

        return true;
    }

    public static boolean isChanneling(Player player) {
        Spell[] channeleableSpells = getChanneleableSpells();
        for (Spell spell : channeleableSpells) {
            SpellChanneleable spellChanneleable = (SpellChanneleable) spell;
            if (spellChanneleable.hasMember(player.getUniqueId()))
                return true;
        }
        return false;
    }

    public static boolean removeChanneling(Player player) {
        Spell[] channeleableSpells = getChanneleableSpells();
        for (Spell spell : channeleableSpells) {
            SpellChanneleable spellChanneleable = (SpellChanneleable) spell;

           if(spellChanneleable.removeMember(player))
               return true;
        }
        return false;
    }

    public static void sendMessageOnChannelers(Player player, String message) {
        Spell[] channeleableSpells = getChanneleableSpells();
        for (Spell spell : channeleableSpells) {
            SpellChanneleable spellChanneleable = (SpellChanneleable) spell;

            if(spellChanneleable.hasMember(player))
            {

                for (Player onlineMember : spellChanneleable.getOnlineMembers(player.getLocation())) {
                    onlineMember.sendMessage(message);
                }

                return;
            }
        }
    }


    public static HashMap<SpellToggleable, HashSet<UUID>> getManaDrainMap() {
        return getManaDrainMap;
    }

    public static boolean removeDraining(SpellToggleable toggleable, UUID uuid) {
        if (!(toggleable instanceof SpellToggleable))
            return false;

        final HashSet<UUID> uuids = getManaDrainMap.get(toggleable);
        if (uuids == null)
            return false;

        return uuids.remove(uuid);
    }

    public static boolean hasDraining(SpellToggleable toggleable, UUID uuid) {
        if (!(toggleable instanceof SpellToggleable))
            return false;

        final HashSet<UUID> uuids = getManaDrainMap.get(toggleable);
        if (uuids == null)
            return false;

        if (!uuids.contains(uuid))
            return false;
        return true;
    }

    public boolean hasDraining(UUID uuid) {
        if (!(this instanceof SpellToggleable))
            return false;

        final HashSet<UUID> uuids = getManaDrainMap.get(this);
        if (uuids == null)
            return false;

        if (!uuids.contains(uuid))
            return false;
        return true;
    }

    public boolean removeDraining(UUID uuid) {
        if (!(this instanceof SpellToggleable))
            return false;

        final HashSet<UUID> uuids = getManaDrainMap.get(this);
        if (uuids == null)
            return false;

        return uuids.remove(uuid);
    }

}
