package me.toshiro.pride.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.toshiro.pride.Pride;

public class PrideCommand implements CommandExecutor {

    private Pride plugin;

    public PrideCommand(Pride plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Only _ToshiroCyMc can use these commands
        if (!player.getName().equals(Pride.ARCHBISHOP)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "Usage: /pride <bless|unbless|reload> [player]");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        if (subcommand.equals("bless")) {
            return handleBless(player, args);
        } else if (subcommand.equals("unbless")) {
            return handleUnbless(player, args);
        } else if (subcommand.equals("reload")) {
            return handleReload(player);
        } else {
            player.sendMessage(ChatColor.RED + "Unknown subcommand: " + subcommand);
            player.sendMessage(ChatColor.GOLD + "Usage: /pride <bless|unbless|reload> [player]");
            return true;
        }
    }

    private boolean handleBless(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /pride bless <player>");
            return true;
        }

        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + targetName);
            return true;
        }

        if (plugin.isCompanion(target)) {
            player.sendMessage(ChatColor.YELLOW + target.getName() + " is already a Companion!");
            return true;
        }

        plugin.blessCompanion(target);
        player.sendMessage(ChatColor.GREEN + "Blessed " + target.getName() + " as an Archbishop's Companion!");
        return true;
    }

    private boolean handleUnbless(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /pride unbless <player>");
            return true;
        }

        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + targetName);
            return true;
        }

        if (!plugin.isCompanion(target)) {
            player.sendMessage(ChatColor.YELLOW + target.getName() + " is not a Companion!");
            return true;
        }

        plugin.unbless(target);
        player.sendMessage(ChatColor.GREEN + "Removed Companion Mark from " + target.getName() + "!");
        return true;
    }

    private boolean handleReload(Player player) {
        plugin.reloadConfig();
        player.sendMessage(ChatColor.GREEN + "Pride plugin configuration reloaded!");
        return true;
    }
}
