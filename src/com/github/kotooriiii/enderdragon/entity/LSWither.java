package com.github.kotooriiii.enderdragon.entity;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.EntityEnderDragon;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityWither;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;

public class LSWither extends EntityWither {


    private boolean isHealingDragon = false;

    public LSWither(Location loc) {
        super(EntityTypes.WITHER, ((CraftWorld) loc.getWorld()).getHandle()); // Super the EntityPig Class

        this.setPosition(loc.getX(), loc.getY(), loc.getZ()); // Sets the location of the CustomPig when we spawn it
        this.setHealth(getMaxHealth()); // Sets to max health
        TextComponent textComponent = new TextComponent("Nickolov's Wither");
        textComponent.setColor(ChatColor.DARK_PURPLE);
        final IChatBaseComponent iChatBaseComponent = CraftChatMessage.fromStringOrNull(textComponent.toLegacyText(), true);
        this.setCustomName(iChatBaseComponent); // Sets custom name.
        this.setCustomNameVisible(true); // Makes the name visible to the player in-game
    }
    public  void setPos(double x, double y, double z)
    {
        this.setPosition(x,y,z); // Sets the location of the CustomPig when we spawn it
    }

    public boolean isHealingDragon()
    {
        return isHealingDragon;
    }

    public void setHealingDragon(boolean healingDragon) {
        isHealingDragon = healingDragon;
    }
}
