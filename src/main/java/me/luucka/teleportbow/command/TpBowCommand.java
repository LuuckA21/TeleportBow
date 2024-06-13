package me.luucka.teleportbow.command;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.Settings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.luucka.teleportbow.util.Color.colorize;

public class TpBowCommand implements TabExecutor {

	/*

	TODO: tpbow command
			/tpbow -> help with usage
			/tpbow give [player] -> without player give to me, otherwise selected player
			/tpbow reload -> reload the plugin

	 */

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(colorize(Settings.NO_CONSOLE));
			return true;
		}
		final Player player = (Player) sender;
		if (args.length == 0) {
			if (!player.hasPermission("tpbow.give")) {
				player.sendMessage(colorize(Settings.NO_PERM));
				return true;
			}
			BowManager.giveBow(player);
		} else {
			if (args[0].equalsIgnoreCase("reload")) {
				if (!player.hasPermission("tpbow.reload")) {
					player.sendMessage(colorize(Settings.NO_PERM));
					return true;
				}
				Settings.init();
				player.sendMessage(colorize(Settings.RELOAD));
			} else {
				player.sendMessage(colorize(Settings.USAGE));
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
