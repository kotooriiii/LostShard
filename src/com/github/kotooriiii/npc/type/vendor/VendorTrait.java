package com.github.kotooriiii.npc.type.vendor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.npc.Skin;
import com.github.kotooriiii.plots.commands.VendorCommand;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.structure.FixedLinkedList;
import com.github.kotooriiii.util.HelperMethods;
import com.github.kotooriiii.util.ReflectionUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.DelegatePersistence;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.trait.SkinTrait;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;

import static com.github.kotooriiii.plots.commands.VendorCommand.df;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.

public class VendorTrait extends Trait {

    //Static
    private static final int socialDistance = 5;
    private static final DecimalFormat format = new DecimalFormat("0.00");


    // Name of vendor
    @Persist
    private String name = "";

    //Vendor location
    @Persist
    private Location vendorLocation = null;

    //Payment history
//    @Persist
//    @DelegatePersistence(FixedLinkedListStringPersister.class) // explicit delegation
    private FixedLinkedList<String> history = new FixedLinkedList<>(VendorNPC.getMaxHistory());

    //Balance in vendor
    @Persist
    private double balance = 0;

    //Residing plot
    private UUID plotUUID = null;
    private Plot plot = null;


    //Items to sell
    private ArrayList<VendorItemStack> vendorItems = new ArrayList<>();

    public VendorTrait() {
        super("VendorTrait");
    }


    public VendorTrait(String name, UUID plotUUID, Location vendorLocation) {
        super("VendorTrait");
        this.name = name;
        this.plotUUID = plotUUID;
        this.plot = LostShardPlugin.getPlotManager().wrap(plotUUID);
        this.vendorLocation = vendorLocation;
    }


    /*

        Slots:
             1:
                item:
                amount:
                totalPrice:
             2:
             3:
             4:
             5:

     */


    // Here you should load up any values you have previously saved (optional).
    // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
    // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
    // This is called BEFORE onSpawn, npc.getBukkitEntity() will return null.
    public void load(DataKey key) {

        ArrayList<VendorItemStack> vendorItemStacks = new ArrayList<>();

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            final int amt = key.getInt("slots." + i + ".amount", -1);
            if (amt == -1)
                break;

            final ItemStack itemStack = key.getRawUnchecked("slots." + i + ".itemstack");
            final double totalPrice = key.getDouble("slots." + i + ".totalPrice");

            vendorItemStacks.add(new VendorItemStack(itemStack, amt, totalPrice));
        }

        this.plotUUID = UUID.fromString(key.getString("plotUUID"));
        this.plot = LostShardPlugin.getPlotManager().wrap(plotUUID);

        final FixedLinkedList<String> strings = new FixedLinkedList<String>(VendorNPC.getMaxHistory());

        for(int i = 0; i < VendorNPC.getMaxHistory(); i++)
        {
            final String receivedKey = key.getString("history." + i, "ERR");

            if(receivedKey.equals("ERR"))
                break;
            strings.add(receivedKey);
        }

        this.history = strings;

        key.removeKey("history");
        key.removeKey("slots");
        key.removeKey("history");

        this.vendorItems = vendorItemStacks;
    }

    // Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
    public void save(DataKey key) {


        int index = 0;
        for (VendorItemStack vendorItemStack : vendorItems) {

            key.setRaw("slots." + index + ".itemstack", vendorItemStack.itemStack);
            key.setInt("slots." + index + ".amount", +vendorItemStack.amount);
            key.setDouble("slots." + index + ".totalPrice", +vendorItemStack.stackPrice);
            index++;
        }
        key.setString("plotUUID", plotUUID.toString());

        for(int i = 0; i < history.size(); i++)
        {
            key.setString("history." + i,  history.get(i));
        }
    }

    // An example event handler. All traits will be registered automatically as Bukkit Listeners.
    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event) {
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        if (!event.getNPC().equals(this.getNPC()))
            return;

        Player clicker = event.getClicker();

        clicker.performCommand("vendor list");
    }

    // An example event handler. All traits will be registered automatically as Bukkit Listeners.
    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCLeftClickEvent event) {
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        if (!event.getNPC().equals(this.getNPC()))
            return;

        Player clicker = event.getClicker();

        clicker.performCommand("vendor withdraw");
    }


//    // Called every tick
//    @Override
//    public void run() {
//        if (getNPC() == null)
//            return;
//        if (!getNPC().isSpawned())
//            return;
//    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.LEATHER_HELMET, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.LEATHER_CHESTPLATE, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.LEATHER_LEGGINGS, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.LEATHER_BOOTS, 1));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.WRITABLE_BOOK, 1));

        npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, Skin.VENDOR.getTexture());
        npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, Skin.VENDOR.getSignature());
        npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, Skin.VENDOR.getName());

        SkinTrait skinTrait = null;
        if (!npc.hasTrait(SkinTrait.class)) {
            skinTrait = new SkinTrait();
            npc.addTrait(skinTrait);
        } else {
            skinTrait = npc.getTrait(SkinTrait.class);
        }

        skinTrait.setSkinName(Skin.VENDOR.getName());
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

    public String getVendorName() {
        return name;
    }

    public void setVendorName(String name) {
        this.name = name;
    }

    public Location getVendorLocation() {
        return vendorLocation;
    }

    public void setVendorLocation(Location vendorLocation) {
        this.vendorLocation = vendorLocation;
        getNPC().teleport(vendorLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    //END OF BASIC GETTERS/SETTERS

    public boolean isSocialDistance(Location loc) {
        int xIntDiff = loc.getBlockX() - getNPC().getStoredLocation().getBlockX();
        int yIntDiff = loc.getBlockY() - getNPC().getStoredLocation().getBlockY();
        int zIntDiff = loc.getBlockZ() - getNPC().getStoredLocation().getBlockZ();
        return Math.abs(xIntDiff) <= socialDistance && Math.abs(yIntDiff) <= socialDistance && Math.abs(zIntDiff) <= socialDistance;
    }

    public static int getSocialDistance() {
        return socialDistance;
    }

    public void addEntry(String name, int qty, ItemStack itemStack, long millis) {

        final String materialName = HelperMethods.materialName(itemStack.getType());


        final String json = convertItemStackToJson(itemStack);

        if (!isStacked(name, qty,json, materialName, millis)) {
            history.add(name + " - " + qty + " <" + json + "> <" + HelperMethods.materialName(itemStack.getType()) + "> (" + millis + ")");

        }

    }

    private boolean isStacked(String playerNameNew, int qtyNew, String jsonNew, String materialName, long millis) {

        if (history.isEmpty())
            return false;


        String tab = history.get(history.size() - 1);

        String playerNameOld = tab.substring(0, tab.indexOf(" -"));
        int qtyOld = Integer.parseInt(tab.substring(tab.indexOf("-") + 2, tab.indexOf(" <")));
        String jsonOld = tab.substring(tab.indexOf("<") + 1, tab.indexOf(">"));

        JsonObject jsonObjNew = null;
        try {
            JsonElement root = new JsonParser().parse(jsonNew);
            jsonObjNew = root.getAsJsonObject();
            jsonObjNew.remove("Count");
            jsonObjNew.addProperty("Count", "1b");

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject jsonObjOld = null;
        try {
            JsonElement root = new JsonParser().parse(jsonOld);
            jsonObjOld = root.getAsJsonObject();
            jsonObjOld.remove("Count");
            jsonObjOld.addProperty("Count", "1b");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (playerNameOld.equals(playerNameNew) && jsonObjNew.toString().equals(jsonObjOld.toString())) {
            history.set(history.size() - 1, playerNameNew + " - " + (qtyOld + qtyNew) + " <" + jsonNew + "> <" + materialName + "> (" + millis + ")");
            return true;
        }
        return false;


    }

    public BaseComponent[] getPurchaseHistory() {

        TextComponent finalComponent = new TextComponent("-" + getPlotName() + "'s Vendor" + name + "-\n");
        finalComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);

        TextComponent balanceComponent = new TextComponent("Balance" + ChatColor.WHITE + ": " + ChatColor.GOLD + format.format(this.balance) + "\n");
        balanceComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        finalComponent.addExtra(balanceComponent);

        TextComponent purchasesComponent = new TextComponent("Purchases" + ChatColor.WHITE + ": \n");
        purchasesComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        finalComponent.addExtra(purchasesComponent);

        Stack<String> stack = new Stack<>();
        for (String tab : history) {
            stack.add(tab);
        }

        TextComponent ADDER = new TextComponent();

        while (!stack.isEmpty()) {

            String tab = stack.pop();

            long time = Long.parseLong(tab.substring(tab.lastIndexOf("(") + 1, tab.lastIndexOf(")")));

            final ZonedDateTime then = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("America/New_York"));

            //If more than 2 days,
            if (Math.abs(ChronoUnit.DAYS.between(then, ZonedDateTime.now())) > 2) {
                continue;
            }

            String timeLeft = HelperMethods.getTimeLeftShort(then) + " ago";

            TextComponent component = new TextComponent(tab.substring(0, tab.indexOf("-")));
            component.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            ADDER.addExtra(component);

            TextComponent dash = new TextComponent("- ");
            dash.setColor(net.md_5.bungee.api.ChatColor.WHITE);
            ADDER.addExtra(dash);

            TextComponent qty = new TextComponent(tab.substring(tab.indexOf("-") + 2, tab.indexOf(" <")));
            qty.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            qty.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Quantity purchased.").color(net.md_5.bungee.api.ChatColor.GOLD).create()));
            ADDER.addExtra(qty);

            TextComponent spaceComponent = new TextComponent(" ");
            spaceComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
            ADDER.addExtra(spaceComponent);

            final BaseComponent[] niceMeta = getNiceMeta(tab.substring(tab.indexOf("<") + 1, tab.indexOf(">")));

            TextComponent materialName = new TextComponent(tab.substring(tab.lastIndexOf("<") + 1, tab.lastIndexOf(">")));
            materialName.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            materialName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, niceMeta));
            ADDER.addExtra(materialName);

            ADDER.addExtra(spaceComponent);

            TextComponent timeComponent = new TextComponent("(" + timeLeft + ")");
            timeComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            ADDER.addExtra(timeComponent);

            ADDER.addExtra(new TextComponent("\n"));
        }

        final List<BaseComponent> extra = ADDER.getExtra();

        if (extra == null || extra.isEmpty()) {
            final TextComponent textComponent = new TextComponent("No history found within the last 48 hours.");
            textComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            return new BaseComponent[]{textComponent};
        }

        extra.remove(extra.size() - 1);

        ArrayList<BaseComponent> arrayList = new ArrayList();
        arrayList.add(finalComponent);
        arrayList.addAll(extra);


        return arrayList.toArray(new BaseComponent[arrayList.size()]);
    }

    public BaseComponent[] getListings() {

        final TextComponent completeComponent = new TextComponent("-" + getPlotName() + "'s Vendor " + name + "-\n" + "Hover over for more info\n");
        completeComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);

        final TextComponent ADDER = new TextComponent();

        for (VendorItemStack vendorItemStack : this.getVendorItems()) {
            final String materialName = HelperMethods.materialName(vendorItemStack.itemStack.getType());
            double totalPrice = vendorItemStack.getTotalPrice();

            TextComponent amountComponent = new TextComponent(vendorItemStack.amount + "x");
            amountComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            amountComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Total amount being sold.").color(net.md_5.bungee.api.ChatColor.GOLD).create()));

            ADDER.addExtra(amountComponent);
            ADDER.addExtra(new TextComponent(" "));

            TextComponent materialComponent = new TextComponent(materialName);
            materialComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            materialComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, getNiceMeta(vendorItemStack.itemStack)));

            ADDER.addExtra(materialComponent);
            TextComponent dashComponent = new TextComponent(" - ");
            dashComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            ADDER.addExtra(dashComponent);

            TextComponent priceComponent = new TextComponent(df.format(totalPrice) + "g");
            priceComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            priceComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                    "Individual (1x) Price: " + df.format(vendorItemStack.getIndividualPrice()) + "g\n" +
                            "Stack (" + vendorItemStack.getMaxStackSize() + "x)" + " Price: " + df.format(vendorItemStack.getStackPrice()) + "g\n" +
                            "Total (" + vendorItemStack.amount + "x)" + " Price: " + df.format(vendorItemStack.getTotalPrice()) + "g\n"
            ).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

            ADDER.addExtra(priceComponent);
            ADDER.addExtra(new TextComponent("\n"));
        }

        final TextComponent errComp = new TextComponent("No items are currently being sold.");
        errComp.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);


        final List<BaseComponent> extra = ADDER.getExtra();
        if (extra == null || extra.isEmpty()) {
            return new BaseComponent[]{errComp};
        }
        extra.remove(extra.size() - 1);

        ArrayList<BaseComponent> arrayList = new ArrayList();
        arrayList.add(completeComponent);
        arrayList.addAll(extra);


        return arrayList.toArray(new BaseComponent[arrayList.size()]);
    }

    public static BaseComponent[] getNiceMeta(ItemStack itemStack) {
        String itemJson = convertItemStackToJson(itemStack);

        return new BaseComponent[]{new TextComponent(itemJson)};
    }

    private BaseComponent[] getNiceMeta(String json) {
        return new BaseComponent[]{new TextComponent(json)};
    }


    public static String convertItemStackToJson(ItemStack itemStack) {
        // ItemStack methods to get a net.minecraft.server.ItemStack object for serialization
        Class<?> craftItemStackClazz = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
        Method asNMSCopyMethod = ReflectionUtil.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

        // NMS Method to serialize a net.minecraft.server.ItemStack to a valid Json string
        Class<?> nmsItemStackClazz = ReflectionUtil.getNMSClass("ItemStack");
        Class<?> nbtTagCompoundClazz = ReflectionUtil.getNMSClass("NBTTagCompound");
        Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
        Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
        Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
            return null;
        }

        // Return a string representation of the serialized object
        return itemAsJsonObject.toString();
    }

    public boolean canWithdraw() {
        return balance >= 1;
    }

    public double getBalance() {
        return balance;
    }

    public boolean withdraw() {
        //Gold ingots are 64 max stack.

        if (balance < 1)
            return false;

        int stacks = (int) balance / 64;
        int leftover = (int) balance % 64;

        while (stacks > 0) {
            throwMoney(new ItemStack(Material.GOLD_INGOT, 64));
            stacks--;
        }

        throwMoney(new ItemStack(Material.GOLD_INGOT, leftover));

        this.balance = this.balance - (int) this.balance;
        npc.getStoredLocation().getWorld().playSound(npc.getStoredLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 5.0f, 0.0f);
        npc.getStoredLocation().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, npc.getStoredLocation(), 6, 0.5, 0.5f, 0.5f);

        return true;
    }

    private void throwMoney(ItemStack itemStack) {
        npc.getStoredLocation().getWorld().dropItemNaturally(npc.getStoredLocation(), itemStack);
    }

    public void dieSomehow() {
        withdraw();

        for (VendorItemStack vendorItemStack : vendorItems) {

            int stacks = vendorItemStack.amount / vendorItemStack.itemStack.getType().getMaxStackSize();
            int leftover = vendorItemStack.amount % vendorItemStack.itemStack.getType().getMaxStackSize();

            while (stacks > 0) {
                vendorItemStack.itemStack.setAmount(vendorItemStack.itemStack.getType().getMaxStackSize());
                throwMoney(vendorItemStack.itemStack);
                stacks--;
            }
            if(leftover != 0) {
                vendorItemStack.itemStack.setAmount(leftover);
                throwMoney(vendorItemStack.itemStack);
            }
        }
        removeStockers();

        npc.getStoredLocation().getWorld().playSound(npc.getStoredLocation(), Sound.ENTITY_VILLAGER_DEATH, 5.0f, 0.0f);
        npc.getStoredLocation().getWorld().spawnParticle(Particle.SMOKE_NORMAL, npc.getStoredLocation(), 6, 0.5, 0.5f, 0.5f);
    }

    private void removeStockers() {

        final Set<Map.Entry<UUID, VendorTrait>> newListers = VendorCommand.getNewListingMap().entrySet();
        newListers.removeIf(next -> {

            boolean isRemoving = next.getValue().equals(this);
            if (isRemoving) {
                final Player player = Bukkit.getPlayer(next.getKey());
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "The vendor you were stocking died.");
                }
            }

            return next.getValue().equals(this);
        });

        final Set<Map.Entry<UUID, VendorTrait>> choiceListers = VendorCommand.getChoiceListingMap().entrySet();
        choiceListers.removeIf(next -> {

            boolean isRemoving = next.getValue().equals(this);
            if (isRemoving) {
                final Player player = Bukkit.getPlayer(next.getKey());
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "The vendor you were stocking died.");
                }
            }

            return next.getValue().equals(this);
        });
    }

    public ArrayList<VendorItemStack> getVendorItems() {
        return vendorItems;
    }

    public int emptySlots() {
        return VendorNPC.getMaxSlots() - vendorItems.size();
    }

    public int filledSlots() {
        return vendorItems.size();
    }

    public boolean isFull() {
        return VendorNPC.getMaxSlots() == vendorItems.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorTrait that = (VendorTrait) o;
        return Double.compare(that.balance, balance) == 0 &&
                Objects.equals(getVendorName(), that.getVendorName()) &&
                Objects.equals(getVendorLocation(), that.getVendorLocation()) &&
                Objects.equals(history, that.history) &&
                Objects.equals(getPlotName(), that.getPlotName()) &&
                Objects.equals(getVendorItems(), that.getVendorItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVendorName(), getVendorLocation(), history, balance, getPlotName(), getVendorItems());
    }

    public String getPlotName() {

        if(this.plot == null)
        {
            return "NULL";
        }

        return this.plot.getName();
    }


    public void deposit(double deposit) {
        this.balance += deposit;
    }


    //    public boolean stock(ItemStack itemStack, double totalPrice)
//    {
//        Iterator<VendorItemStack> itemStackIterator = vendorItems.iterator();
//        while (itemStackIterator.hasNext())
//        {
//            final VendorItemStack next = itemStackIterator.next();
//
//            if(next.itemStack.isSimilar(itemStack) && totalPrice == )
//            {
//
//            }
//        }
//    }
}
