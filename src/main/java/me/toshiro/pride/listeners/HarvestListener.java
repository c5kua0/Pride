package me.toshiro.pride.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.toshiro.pride.Pride;

import java.util.ArrayList;
import java.util.List;

public class HarvestListener implements Listener {

    private Pride plugin;

    public HarvestListener(Pride plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Check if player is a Companion
        if (!plugin.isCompanion(player)) {
            return;
        }

        Block block = event.getBlock();
        Material blockType = block.getType();

        // Check if block is a harvestable crop
        if (!isHarvestableCrop(blockType)) {
            return;
        }

        // Get default drops
        List<ItemStack> drops = new ArrayList<>(block.getDrops());

        // Increase yield by approximately 25%
        List<ItemStack> newDrops = new ArrayList<>();
        for (ItemStack drop : drops) {
            ItemStack newDrop = drop.clone();
            // 25% chance to add an extra item to each drop stack
            if (Math.random() < 0.25) {
                newDrop.setAmount(newDrop.getAmount() + 1);
            }
            newDrops.add(newDrop);
        }

        // Clear default drops
        event.setDropItems(false);

        // Add modified drops
        for (ItemStack drop : newDrops) {
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }

    private boolean isHarvestableCrop(Material material) {
        return material == Material.WHEAT ||
               material == Material.CARROTS ||
               material == Material.POTATOES ||
               material == Material.BEETROOTS;
    }
}
