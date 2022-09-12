package me.luucka.teleportbow.commands;

import me.luucka.teleportbow.TeleportBow;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.luucka.teleportbow.utils.Color.colorize;

public class CmdTpBow implements TabExecutor {

    private final TeleportBow plugin;

    public CmdTpBow(TeleportBow plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize(plugin.getSettings().getNoConsole()));
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            if (!player.hasPermission("tpbow.give")) {
                player.sendMessage(colorize(plugin.getSettings().getNoPerm()));
                return true;
            }

            plugin.giveBow(player);
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("tpbow.reload")) {
                    player.sendMessage(colorize(plugin.getSettings().getNoPerm()));
                    return true;
                }
                plugin.getSettings().reload();
                player.sendMessage(colorize(plugin.getSettings().getReload()));
            }  else {
                player.sendMessage(colorize(plugin.getSettings().getUsage()));
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
