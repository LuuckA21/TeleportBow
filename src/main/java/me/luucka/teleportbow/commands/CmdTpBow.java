package me.luucka.teleportbow.commands;

import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.luucka.teleportbow.utils.Color.colorize;

public class CmdTpBow implements TabExecutor {

    private final TeleportBow PLUGIN;

    public CmdTpBow(TeleportBow PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize(PLUGIN.getSettings().getNoConsole()));
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            if (!player.hasPermission("tpbow.give")) {
                player.sendMessage(colorize(PLUGIN.getSettings().getNoPerm()));
                return true;
            }
            player.getInventory().setItem(PLUGIN.getConfig().getInt("bow.slot"), PLUGIN.createTpBow());
            player.getInventory().setItem(PLUGIN.getConfig().getInt("bow.arrow-slot"), new ItemStack(Material.ARROW, 1));
        } else {
            if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                if (!player.hasPermission("tpbow.reload")) {
                    player.sendMessage(colorize(PLUGIN.getSettings().getNoPerm()));
                    return true;
                }
                PLUGIN.getSettings().reload();
                player.sendMessage(colorize(PLUGIN.getSettings().getReload()));
            }  else {
                player.sendMessage(colorize(PLUGIN.getSettings().getUsage()));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("tpbow.reload")) {
                suggestions.add("reload");
            }
            return suggestions;
        }
        return null;
    }
}
