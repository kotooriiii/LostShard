package com.github.kotooriiii.register_system;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.plots.PlotType;
import com.github.kotooriiii.plots.struct.FFAPlot;
import com.github.kotooriiii.plots.struct.Plot;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Gathering {

    private RegisterManager registerManager;
    private String armorLoadout;
    private String swordLoadout;
    private String bowLoadout;
    private String spellsLoadout;
    private GatheringType type;

    public Gathering(GatheringType type) {
        registerManager = new RegisterManager();
        this.armorLoadout = "";
        this.swordLoadout = "";
        this.bowLoadout = "";
        this.spellsLoadout = "";
        this.type = type;
    }


    public abstract void startGame();

    public abstract void endGame();

    public void sendIntroduction() {
        Bukkit.getOnlinePlayers().forEach(
                player -> {

                    player.sendMessage(ChatColor.GREEN + "Lostshard will be hosting a " + type.name() + " tournament in 30 minutes. Players are responsible for their own weapons, gear, and supplies.");

                    TextComponent component = new TextComponent("Hover over this message for info on required loadout.");

                    TextComponent loadout = new TextComponent("REQUIRED LOADOUT:\n");
                    loadout.addExtra("Armor: " + armorLoadout + "\n");
                    loadout.addExtra("Sword: " + swordLoadout + "\n");
                    loadout.addExtra("Bow: " + bowLoadout + "\n");
                    loadout.addExtra("Spells: " + spellsLoadout + "");
                    loadout.setColor(net.md_5.bungee.api.ChatColor.GREEN);


                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(loadout).create()));
                    component.setColor(net.md_5.bungee.api.ChatColor.GREEN);

                    player.spigot().sendMessage(component);

                    TextComponent component1 = new TextComponent("Type /join to join, or click here to join!");
                    component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join"));
                    component1.setColor(net.md_5.bungee.api.ChatColor.GREEN);

                    player.spigot().sendMessage(component1);

                }
        );

        new BukkitRunnable() {
            @Override
            public void run() {
                startGame();
            }
        }.runTaskLater(LostShardPlugin.plugin, 20 * 60 * 30);

    }

    public RegisterManager getRegisterManager() {
        return registerManager;
    }

    public String getArmorLoadout() {
        return armorLoadout;
    }

    public void setArmorLoadout(String armorLoadout) {
        this.armorLoadout = armorLoadout;
    }

    public String getSwordLoadout() {
        return swordLoadout;
    }

    public void setSwordLoadout(String swordLoadout) {
        this.swordLoadout = swordLoadout;
    }

    public String getBowLoadout() {
        return bowLoadout;
    }

    public void setBowLoadout(String bowLoadout) {
        this.bowLoadout = bowLoadout;
    }

    public String getSpellsLoadout() {
        return spellsLoadout;
    }

    public void setSpellsLoadout(String spellsLoadout) {
        this.spellsLoadout = spellsLoadout;
    }

    public GatheringType getType() {
        return type;
    }
}
