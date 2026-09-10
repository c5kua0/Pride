package me.toshiro.pride.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.toshiro.pride.Pride;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private Pride plugin;
    private Map<UUID, Long> archbishopMagicCooldown = new HashMap<>();
    private Map<UUID, Long> sacredBurstCooldown = new HashMap<>();

    public PlayerInteractListener(Pride plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if player is a Companion and holding Blessed Scepter
        if (!plugin.isCompanion(player) || !plugin.isBlessedScepter(item)) {
            return;
        }

        // Check if player is sneaking (Sacred Burst)
        if (player.isSneaking() && (event.getAction().toString().contains("RIGHT"))) {
            event.setCancelled(true);
            useSacredBurst(player);
            return;
        }

        // Check for Archbishop's Magic Blast (right-click)
        if (event.getAction().toString().contains("RIGHT")) {
            event.setCancelled(true);
            useArchbishopsMagicBlast(player);
        }
    }

    private void useArchbishopsMagicBlast(Player player) {
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldownTime = 5000; // 5 seconds

        // Check cooldown
        if (archbishopMagicCooldown.containsKey(playerUUID)) {
            long lastUse = archbishopMagicCooldown.get(playerUUID);
            if (currentTime - lastUse < cooldownTime) {
                long remainingTime = (cooldownTime - (currentTime - lastUse)) / 1000;
                player.sendMessage(ChatColor.YELLOW + "Archbishop's Magic Blast is on cooldown for " + remainingTime + " more seconds.");
                return;
            }
        }

        // Create magic blast effect
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();
        Location blastLocation = eyeLocation.clone();

        // Damage entities in the blast path
        for (int i = 0; i < 5; i++) {
            blastLocation.add(direction.clone().multiply(0.5));
            
            // Show particle effect
            player.getWorld().spawnParticle(Particle.SPELL, blastLocation, 5);
            
            // Check for entities hit
            for (LivingEntity entity : player.getWorld().getNearbyLivingEntities(blastLocation, 1.0)) {
                if (entity == player) continue;
                
                if (entity instanceof Player) {
                    Player targetPlayer = (Player) entity;
                    // Don't damage Archbishop or other Companions
                    if (targetPlayer.getName().equals(Pride.ARCHBISHOP) || plugin.isCompanion(targetPlayer)) {
                        continue;
                    }
                }

                // Only damage hostile mobs
                if (!isHostileMob(entity)) continue;

                // Apply damage
                entity.damage(4.0, player);
                
                // Knockback
                Vector knockback = entity.getLocation().toVector().subtract(blastLocation.toVector()).normalize().multiply(0.3);
                knockback.setY(0.1);
                entity.setVelocity(entity.getVelocity().add(knockback));
            }
        }

        // Update cooldown
        archbishopMagicCooldown.put(playerUUID, currentTime);

        player.sendMessage(ChatColor.BLUE + "Archbishop's Magic Blast cast!");
    }

    private void useSacredBurst(Player player) {
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldownTime = 15000; // 15 seconds

        // Check cooldown
        if (sacredBurstCooldown.containsKey(playerUUID)) {
            long lastUse = sacredBurstCooldown.get(playerUUID);
            if (currentTime - lastUse < cooldownTime) {
                long remainingTime = (cooldownTime - (currentTime - lastUse)) / 1000;
                player.sendMessage(ChatColor.YELLOW + "Sacred Burst is on cooldown for " + remainingTime + " more seconds.");
                return;
            }
        }

        // Create burst effect
        Location center = player.getLocation().add(0, 1, 0);
        double radius = 4.0;

        for (LivingEntity entity : player.getWorld().getNearbyLivingEntities(center, radius)) {
            if (entity == player) continue;
            if (entity instanceof Player) {
                Player targetPlayer = (Player) entity;
                // Don't damage Archbishop or other Companions
                if (targetPlayer.getName().equals(Pride.ARCHBISHOP) || plugin.isCompanion(targetPlayer)) {
                    continue;
                }
            }

            // Only damage hostile mobs
            if (!isHostileMob(entity)) continue;

            // Apply damage
            entity.damage(8.0, player);

            // Knock back
            Vector knockback = entity.getLocation().toVector().subtract(center.toVector()).normalize().multiply(0.5);
            knockback.setY(0.2);
            entity.setVelocity(entity.getVelocity().add(knockback));

            // Ignite
            entity.setFireTicks(60);
        }

        // Visual effect - magic particles around player
        for (int i = 0; i < 16; i++) {
            double angle = (Math.PI * 2 / 16) * i;
            Vector direction = new Vector(Math.cos(angle), 0.3, Math.sin(angle)).normalize();
            
            Location spawnLoc = center.clone().add(direction.multiply(1.5));
            player.getWorld().spawnParticle(Particle.SPELL_MOB, spawnLoc, 3);
        }

        // Update cooldown
        sacredBurstCooldown.put(playerUUID, currentTime);

        player.sendMessage(ChatColor.GOLD + "Sacred Burst unleashed!");
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
