package me.toshiro.pride.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.toshiro.pride.Pride;

public class PlayerDamageListener implements Listener {

    private Pride plugin;

    public PlayerDamageListener(Pride plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        LivingEntity damager = null;

        // Check if damager is a player or pet of a player
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else {
            return;
        }

        Player companionPlayer = (Player) damager;

        // Divine Flame - Passive ability (always active for Companions)
        if (plugin.isCompanion(companionPlayer) && event.getEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) event.getEntity();

            // Don't damage Archbishop or other Companions
            if (target instanceof Player) {
                Player targetPlayer = (Player) target;
                if (targetPlayer.getName().equals(Pride.ARCHBISHOP) || plugin.isCompanion(targetPlayer)) {
                    event.setCancelled(true);
                    return;
                }
            }

            // Only affects hostile mobs
            if (!isHostileMob(target)) {
                return;
            }

            // Divine Flame effect: chance to ignite and slight damage boost
            double randomChance = Math.random();
            if (randomChance < 0.25) { // 25% chance to ignite
                target.setFireTicks(80);
            }

            // Slight damage increase
            event.setDamage(event.getDamage() * 1.15);
        }
    }

    private boolean isHostileMob(LivingEntity entity) {
        String name = entity.getType().name();
        return name.contains("ZOMBIE") || name.contains("SKELETON") || name.contains("CREEPER") ||
               name.contains("SPIDER") || name.contains("ENDERMAN") || name.contains("WITCH") ||
               name.contains("GHAST") || name.contains("BLAZE") || name.contains("WITHER") ||
               name.contains("SLIME") || name.contains("CAVE_SPIDER") || name.contains("HUSK") ||
               name.contains("STRAY") || name.contains("DROWNED") || name.contains("PHANTOM") ||
               name.contains("GUARDIAN") || name.contains("ELDER_GUARDIAN") || name.contains("ELDER") ||
               name.contains("VINDICATOR") || name.contains("EVOKER") || name.contains("PILLAGER");
    }
}
