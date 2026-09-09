package me.toshiro.pride;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import me.toshiro.pride.commands.PrideCommand;
import me.toshiro.pride.listeners.PlayerDamageListener;
import me.toshiro.pride.listeners.PlayerInteractListener;
import me.toshiro.pride.listeners.HarvestListener;

public class Pride extends JavaPlugin {

    public static final String ARCHBISHOP = "_ToshiroCyMc";
    public static final NamespacedKey COMPANION_KEY = new NamespacedKey("pride", "companion");
    public static final NamespacedKey SCEPTER_KEY = new NamespacedKey("pride", "blessed_scepter");

    private static Pride instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Pride plugin enabled!");

        // Register commands
        getCommand("pride").setExecutor(new PrideCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new HarvestListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Pride plugin disabled!");
    }

    public static Pride getInstance() {
        return instance;
    }

    /**
     * Checks if a player is an active Companion
     */
    public boolean isCompanion(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.has(COMPANION_KEY, PersistentDataType.BYTE);
    }

    /**
     * Marks a player as an Archbishop's Companion
     */
    public void blessCompanion(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(COMPANION_KEY, PersistentDataType.BYTE, (byte) 1);
        
        player.sendMessage(ChatColor.GOLD + "You have received the Archbishop's Blessing!");
        player.sendMessage(ChatColor.GOLD + "You are now an Archbishop's Companion.");
        
        // Give Blessed Scepter
        ItemStack scepter = createBlessedScepter();
        player.getInventory().addItem(scepter);
    }

    /**
     * Removes the Companion Mark from a player
     */
    public void unblessi(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.remove(COMPANION_KEY);
        
        player.sendMessage(ChatColor.RED + "Your Archbishop's Blessing has been removed.");
    }

    /**
     * Creates a Blessed Scepter item
     */
    public ItemStack createBlessedScepter() {
        ItemStack scepter = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = scepter.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Blessed Scepter");
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(SCEPTER_KEY, PersistentDataType.BYTE, (byte) 1);
            scepter.setItemMeta(meta);
        }
        
        return scepter;
    }

    /**
     * Checks if an item is a Blessed Scepter
     */
    public boolean isBlessedScepter(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(SCEPTER_KEY, PersistentDataType.BYTE);
    }
}
