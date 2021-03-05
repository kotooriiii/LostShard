package com.github.kotooriiii.sorcery.spells.drops;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.commands.SpellbookCommand;
import com.github.kotooriiii.sorcery.spells.SorceryPlayer;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.sorcery.spells.SpellType;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import static com.github.kotooriiii.data.Maps.ERROR_COLOR;
import static com.github.kotooriiii.data.Maps.clanDisbandTimer;
import static com.github.kotooriiii.sorcery.commands.SpellbookCommand.BOOK_NAME;

public class SpellDropAddListener implements Listener {
    @EventHandler
    public void onInteractRightClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (CitizensAPI.getNPCRegistry().isNPC(player))
            return;
        final ItemStack item = event.getItem();
        if (item == null)
            return;
        final Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != action.RIGHT_CLICK_BLOCK)
            return;

        Spell spell = SpellDropScroll.getScrollSpell(player, item);
        if (spell == null)
            return;

        if (SpellDropScroll.isOwnedByAnotherPlayer(player, item)) {
            player.sendMessage(ERROR_COLOR + "You may only add a spell that belongs you.");
            return;
        }

        final SorceryPlayer sorceryPlayer = LostShardPlugin.getSorceryManager().wrap(player.getUniqueId());
        if (sorceryPlayer.hasSpell(spell.getType())) {
            player.sendMessage(ERROR_COLOR + "You already own this spell.");
            player.getInventory().setItemInMainHand(null);
            return;
        }

        sorceryPlayer.addSpell(spell.getType());
        player.sendMessage(ChatColor.GOLD + "You have successfully added \"" + spell.getName() + "\" to your spellbook. Type /spellbook to view.\n");
        player.getInventory().setItemInMainHand(null);
        player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation(), 100, 3, 3, 33);
        player.getWorld().playSound( player.getLocation(), Sound.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS,9.0f, 3f);


        /*

         */
    }

    @EventHandler
    public void onOpenBook(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (CitizensAPI.getNPCRegistry().isNPC(player))
            return;
        final ItemStack item = event.getItem();
        if (item == null)
            return;
        final Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != action.RIGHT_CLICK_BLOCK)
            return;
        if (item.getType() != Material.WRITTEN_BOOK)
            return;
        BookMeta originalMeta =(BookMeta) item.getItemMeta();
        String title = originalMeta.getTitle();

        if (!title.equals(BOOK_NAME))
            return;

        final ItemStack bookItemStack = SpellbookCommand.getBookItemStack(player);

        player.getInventory().setItemInMainHand(bookItemStack);
        player.getWorld().spawnParticle(Particle.WARPED_SPORE, player.getLocation(), 100, 3, 3, 33);
        player.getWorld().playSound( player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN,9.0f, 3f);
    }

    @EventHandler
    public void dropBook(PlayerDropItemEvent event)
    {
        if(event.getItemDrop() == null)
            return;

        final ItemStack itemStack = event.getItemDrop().getItemStack();

        if (!itemStack.getType().equals(Material.WRITTEN_BOOK))
            return;

        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

        String title = bookMeta.getTitle();
        if (!title.equals(BOOK_NAME))
            return;
        event.getItemDrop().remove();
    }
}
