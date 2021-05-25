package me.luucka.teleportbow.commands;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.TeleportBow;
import me.luucka.teleportbow.utility.ChatUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CmdTpBow implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.message(TeleportBow.plugin().getConfig().getString("message.no-console")));
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            if (!player.hasPermission("tpbow.give")) {
                player.sendMessage(ChatUtils.message(TeleportBow.plugin().getConfig().getString("message.no-perm")));
                return true;
            }
            player.getInventory().setItem(TeleportBow.plugin().getConfig().getInt("bow.slot"), BowManager.createTpBow());
            player.getInventory().setItem(TeleportBow.plugin().getConfig().getInt("bow.arrow-slot"), new ItemStack(Material.ARROW, 1));
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
                if (!player.hasPermission("tpbow.reload")) {
                    player.sendMessage(ChatUtils.message(TeleportBow.plugin().getConfig().getString("message.no-perm")));
                    return true;
                }
                TeleportBow.plugin().reloadConfig();
                player.sendMessage(ChatUtils.message(TeleportBow.plugin().getConfig().getString("message.reload")));
                return true;
            }
        } else {
            player.sendMessage(ChatUtils.message("&cUsage: /tpbow [reload]"));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("reload");
            return suggestions;
        }
        return null;
    }
}
