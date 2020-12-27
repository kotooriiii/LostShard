package com.github.kotooriiii.tutorial;

import com.github.kotooriiii.LostShardPlugin;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class THologramManager {

    List<Hologram> list;
    HashMap<UUID, Integer> map;


    public THologramManager() {
        list = new ArrayList<Hologram>(76 - 27);
        map = new HashMap<UUID, Integer>();
    }




    public void createDefaultHolograms() {
        createHologram(505, 67, 323, "You can use wands to cast spells!\nGrab a stick from the chest\nHold the stick in your hand\nType /bind teleport".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(519, 70.5f, 354, "To teleport, swing the stick in the direction you want to teleport.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(494, 66, 386, "Make it past the ravine to get to the next marker!".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(508, 68, 472, "Great job!".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(496, 67, 479, "LostShard features a variety of fully custom McMMO skills.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(485, 67, 481, "The Survivalism skills allows you to track mobs or players.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(467, 67, 481, "To track a mob or player, type /track (name).\nTrack the spider to get to the next marker!\nType: /track spider").getVisibilityManager().setVisibleByDefault(false);
        createHologram(445, 68, 504, "Type: /track spider".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(442, 76f, 554, "Type: /track spider".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(420, 69, 609, "Type: /track spider".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(364, 66.5f, 646, "Type: /track spider".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(292, 54f, 686, "Great job!".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(283, 50.5f, 695, "Mine 20 gold.\nMine 12 iron.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(286, 53, 690, "Wow this mine has a lot of gold and iron in it, probably a good idea to mine it all...\nGrab the pickaxe out of the chest.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(370, 40, 721, "Looks like a dead end.\nMine through and see if there’s anything on the other side.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(398, 39, 721, "Your pickaxe is low!\nYou can use the blacksmithy skill to repair it.\nHold it in your hand and type: /repair".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(401.5f, 39f, 720.5f, "You repaired your pickaxe!\nNow keep moving forward.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(426.5f, 39, 720.5f, "You’ve made it!\nNow make your way to Order straight ahead.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(472, 58.5f, 730, "Follow the path to get to the next marker.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(504, 68, 787, "Before you go in, you should craft some chain armor.\nChain armor is crafted like any other armor, just using cobblestone!\nCraft it at the crafting tables to your right.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(502, 67, 751, "Keep following the path!").getVisibilityManager().setVisibleByDefault(false);
        createHologram(502, 67, 796, "Good job! You’re ready to head to Order.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(588, 67, 804, "This is Order. It is where all the Lawful players spawn.\nOrder protects Lawful players with guards.\nIf you see a murderer, type: /guards.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(603, 67, 804, "Watch out! A murderer!".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        createHologram(615, 67, 801, "Good job! You killed the murderer using guards".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        createHologram(624, 67, 778, "Follow the path to get to the next marker.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(664.5f, 68.5f, 749.5f, "A player becomes a murderer when they kill 5 times.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(659.5f, 68.5f, 853f, "A player becomes a criminal when they hit another player.".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        createHologram(666, 67.5f, 860, "Follow the path and deposit all your gold at the banker!".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(692, 67.5f, 847, "Go inside the bank.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(712, 70, 867, "You found it!\nLet’s deposit all the gold you mined.\nType: /deposit (amount)".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        createHologram(712, 70.5f, 867, "To access your bank, type /bank.\nNo one else has access to your bank.".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        //PlotIntroChapter onBegin
        createHologram(707, 70.5f, 862, "You can’t stay here forever!\nGo outside and make a plot.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(502, 67, 751, "Follow the path to make a plot!").getVisibilityManager().setVisibleByDefault(false);
        createHologram(726, 67, 804, "Follow the path outside to make a plot.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(847, 66, 797, "No good spots yet! Keep following the path!".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(950f, 82.5f, 767f, "Up ahead to the left!".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        createHologram(977, 86.5f, 747, "This is a good spot for a plot.\nPlace your plot banner on the yellow wool to claim this area!".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        createHologram(977, 86, 747, "Good job!\nYou've claimed your first plot.\nLook in the chest and grab the feather and redstone.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(977, 86, 747, "Use the feather and redstone to cast a mark here.\nA mark is a place you can warp back to when you are away from your base.\nType: '/cast mark'".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        //createHologram(977, 86, 747, "Expand your plot by typing /plot expand.\nDo this multiple times to make your plot bigger!".split("\n")).getVisibilityManager().setVisibleByDefault(false);

       // createHologram(977, 86, 747, "Looks like you ran out of gold!\nLet’s get some more by capturing an event.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        //createHologram(977, 86, 757, "Before we leave, we should set a mark here so we can teleport back.\nTo set a mark, type: /cast mark.\nName the mark something easy to remember, like ‘home’.".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        createHologram(977, 85, 766, "Great job! Continue forward and finish the tutorial!".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(965, 97.5f, 887, "Keep following the path.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(903, 98, 956, "That’s a setback.\nLooks like the only way to keep moving forward is to jump...".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(887, 55, 979, "Ouch! That was a hard hit!\nMaybe we’ll find something on the way to heal us...".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(806, 50, 1027, "We’re getting close to something...".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(737, 51.5f, 1089, "Some melons! Break them to collect them!".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(727, 50.5f, 1087, "Melons can be instantly eaten by right-clicking them.\nThey instantly heal your hearts and hunger.\nThis can be very useful in combat, let’s keep moving.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(692, 50.5f, 1114, "Keep following the path!".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        createHologram(523, 58, 1149, "Kill the zombies!".split("\n")).getVisibilityManager().setVisibleByDefault(false);

        createHologram(515, 58, 1141, "Zombies drop rotten flesh which is instantly edible like melons.\nThey also drop feathers, which is a useful ingredient in casting spells.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(502, 58f, 1132, "Let’s continue to the event along the path.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(355, 73.5f, 962, "This way! Go inside!".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(395, 67, 972, "You’ve made it to Gorps!\nHead to the center to capture it.".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(322, 92, 929, "Stay on the platform without getting knocked off!").getVisibilityManager().setVisibleByDefault(false);
        createHologram(308, 92, 933, "Stay on the platform without getting knocked off!").getVisibilityManager().setVisibleByDefault(false);
        createHologram(308, 92, 915, "Stay on the platform without getting knocked off!").getVisibilityManager().setVisibleByDefault(false);
        createHologram(326, 92, 915, "Stay on the platform without getting knocked off!").getVisibilityManager().setVisibleByDefault(false);


        createHologram(508, 67, 326, "This hologram is blank intentionally.").getVisibilityManager().setVisibleByDefault(false);
        createHologram(325, 92, 925, "Congratulations! You have captured Gorps\nYou’ve been awarded 100 gold for your efforts!\nHead back to your plot by recalling to the mark you set there.\nType: '/cast recall'".split("\n")).getVisibilityManager().setVisibleByDefault(false);
        createHologram(977, 85, 749, "Great job! You recalled back successfully.\nYou’re all set for Lostshard!\nTime to take the skills you learned into the real server!\nLook around for the portal to get you there!".split("\n")).getVisibilityManager().setVisibleByDefault(false);


    }

    public void deconstructHolograms() {

        ImmutableList.copyOf(HologramsAPI.getHolograms(LostShardPlugin.plugin)).forEach(hologram -> {
            hologram.delete();
        });
        list.clear();
    }

    public Hologram createHologram(float x, float y, float z, String... args) {

        //Safe args
        ArrayList<String> list = new ArrayList<>();
        for (String line : args) {
            for (String splittedLine : line.split("\n")) {
                list.add(splittedLine);
            }
        }
        args = list.toArray(new String[list.size()]);


        Hologram hologram = HologramsAPI.createHologram(LostShardPlugin.plugin, new Location(LostShardPlugin.getTutorialManager().getTutorialWorld(), x + 0.5, y + 0.5, z + 0.5));
        for (String s : args) {
            hologram.appendTextLine(s);
        }

        this.list.add(hologram);
        return hologram;
    }

    public Hologram getHologram(String... lines) {

        //safe args
        ArrayList<String> list = new ArrayList<>();
        for (String line : lines) {
            for (String splittedLine : line.split("\n")) {
                list.add(splittedLine);
            }
        }

        lines = list.toArray(new String[list.size()]);


        Hologram foundHologram = null;
        for (Hologram h : HologramsAPI.getHolograms(LostShardPlugin.plugin)) {

            if (lines.length != h.size())
                continue;

            boolean exists = true;
            for (int i = 0; i < lines.length; i++) {

                if (!h.getLine(i).equals(lines[i])) {
                    exists = false;
                    continue;
                }
                exists = true;

            }

            if (exists)
                foundHologram = h;
        }

        return foundHologram;
    }

    public void deleteBefore(UUID uuid, int index) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return;

        int movingIndex = index;

        while (movingIndex >= 0 && list.get(movingIndex) != null && list.get(movingIndex).getVisibilityManager().isVisibleTo(player)) {
            list.get(movingIndex).getVisibilityManager().hideTo(player);
            movingIndex--;

        }
    }

    private void faceDirection(Player player, Location target) {
        Vector dir = target.clone().subtract(player.getEyeLocation()).toVector();
        Location loc = player.getLocation().setDirection(dir);
        player.teleport(loc);
    }


    public synchronized Hologram next(UUID uuid, boolean isDeletingAllHologramsBefore) {

        if (isDeletingAllHologramsBefore && map.get(uuid) != null) {
            int index = map.get(uuid);
            deleteBefore(uuid, index);
        }


        if (map.get(uuid) == null)
            map.put(uuid, -1);
        int index = map.get(uuid);
        map.put(uuid, ++index);

        Hologram next = list.get(index);
        if (next == null)
            return null;
        showHologram(next, uuid);

//        final Player player = Bukkit.getPlayer(uuid);
//        if (player != null) {
//            player.sendMessage("Currently on: " + next.getLine(0) + "...");
//
//        }

        return next;
    }

    public Hologram next(UUID uuid) {
        return next(uuid, true);
    }

    public Hologram getCurrent(UUID uuid) {
        if (map.get(uuid) == null)
            return null;
        return list.get(map.get(uuid));
    }

    public void showHologram(Hologram hologram, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            hologram.getVisibilityManager().showTo(player);
    }

    public void hideHologram(Hologram hologram, UUID uuid) {
        if (hologram == null)
            return;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            hologram.getVisibilityManager().hideTo(player);
    }

    public List<Hologram> getList() {
        return Lists.newCopyOnWriteArrayList(list);
    }

    public void clear(UUID uuid) {
        map.remove(uuid);
    }
}
