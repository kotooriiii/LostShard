package com.github.kotooriiii.sorcery.spells.drops;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.sorcery.spells.SorceryPlayer;
import com.github.kotooriiii.sorcery.spells.Spell;
import com.github.kotooriiii.util.HelperMethods;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class SpellMonsterDropListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onMonsterDrop(EntityDeathEvent event) {
        if (event.isCancelled())
            return;
        LivingEntity defenderEntity = event.getEntity();
        if (CitizensAPI.getNPCRegistry().isNPC(defenderEntity))
            return;

        EntityDamageEvent damagerCause = defenderEntity.getLastDamageCause();
        if (damagerCause == null || !(damagerCause instanceof EntityDamageByEntityEvent))
            return;


        EntityDamageByEntityEvent betterDamageCause = (EntityDamageByEntityEvent) damagerCause;

        Entity damagerEntity = betterDamageCause.getEntity();

        if (damagerEntity == null)
            return;

        Player attacker = HelperMethods.getPlayerDamagerONLY(defenderEntity, event.getEntity().getKiller());

        if (attacker == null)
            return;

         /*
        Event is not canceled
        Not an NPC
        Last damage event is dmg by entity
        attacker is not null
         */

        SorceryPlayer sorceryPlayer = LostShardPlugin.getSorceryManager().wrap(attacker.getUniqueId());
        for (Spell spell : Spell.getSpells()) {
            boolean isOwned = sorceryPlayer.hasSpell(spell.getType());

            if (isOwned)
                continue;
            final SpellMonsterDrop monsterDrop = spell.getMonsterDrop();
            for (EntityType type : monsterDrop.getTypes()) {
                if (type != defenderEntity.getType())
                    continue;

                if (Math.random() > monsterDrop.getChance())
                    continue;

                //LUCKY

                defenderEntity.getWorld().dropItem(defenderEntity.getLocation(), SpellDropScroll.getScrollPaper(attacker, spell));
                defenderEntity.getLocation().getWorld().playSound(defenderEntity.getLocation(), Sound.BLOCK_BELL_RESONATE, 10f, 4);
                spawnFireworks(defenderEntity.getLocation(), 1);
                break;
            }
        }
    }

    public static void spawnFireworks(Location location, int amount) {
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);

        fwm.addEffect(FireworkEffect.builder().withColor(Color.OLIVE).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }
}
