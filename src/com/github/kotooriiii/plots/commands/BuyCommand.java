package com.github.kotooriiii.plots.commands;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.bank.Bank;
import com.github.kotooriiii.npc.type.vendor.VendorItemStack;
import com.github.kotooriiii.npc.type.vendor.VendorNPC;
import com.github.kotooriiii.npc.type.vendor.VendorTrait;
import com.github.kotooriiii.plots.PlotManager;
import com.github.kotooriiii.plots.struct.PlayerPlot;
import com.github.kotooriiii.plots.struct.Plot;
import com.github.kotooriiii.sorcery.spells.type.circle9.GreedSpell;
import com.github.kotooriiii.status.StatusPlayer;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.util.HelperMethods.stringBuilder;

public class BuyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //If the player is sending this message
        if (sender instanceof Player) {
            Player playerSender = (Player) sender;
            //If the command is the "clan" command
            if (cmd.getName().equalsIgnoreCase("buy")) {
                playerSender.performCommand("vendor buy");
                return true;
            }

        }
        return false;
    }
}