package me.luucka.teleportbow.commands;

import me.luucka.teleportbow.Settings;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.luucka.teleportbow.utils.Color.colorize;

public class TpBowCommand implements TabExecutor {

    private final TeleportBow plugin;
    private final Settings settings;

    public TpBowCommand(TeleportBow plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize(settings.getNoConsole()));
            return true;
        }
        final Player player = (Player) sender;
        if (args.length == 0) {
            if (!player.hasPermission("tpbow.give")) {
                player.sendMessage(colorize(settings.getNoPerm()));
                return true;
            }
            plugin.giveBow(player);
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("tpbow.reload")) {
                    player.sendMessage(colorize(settings.getNoPerm()));
                    return true;
                }
                settings.reload();
                player.sendMessage(colorize(settings.getReload()));
            } else {
                player.sendMessage(colorize(settings.getUsage()));
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
        }
        return suggestions;
    }
}
