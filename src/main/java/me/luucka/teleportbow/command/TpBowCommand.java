package me.luucka.teleportbow.command;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.Settings;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
			boolean hasGivePermission = player.hasPermission("tpbow.give");
			boolean hasReloadPermission = player.hasPermission("tpbow.reload");

			if (hasGivePermission || hasReloadPermission) {
				player.sendMessage(colorize(Settings.USAGE));
				if (hasGivePermission) {
					player.sendMessage(colorize("&7Usage: /tpbow give [player] - Give special bow to a player, or yourself"));
				}
				if (hasReloadPermission) {
					player.sendMessage(colorize("&7Usage: /tpbow reload - Reload the plugin"));
				}
			}
		} else {
			if ("give".equals(args[0])) {
				if (!player.hasPermission("tpbow.give")) {
					player.sendMessage(colorize(Settings.NO_PERM));
					return true;
				}

				if (args.length == 1) {
					BowManager.giveBow(player);
				} else {
					Player targetPlayer = TeleportBow.getInstance().getServer().getPlayer(args[1]);
					if (targetPlayer == null) {
						player.sendMessage(colorize(Settings.PLAYER_NOT_FOUND.replace("{player}", args[1])));
						return true;
					}
					BowManager.giveBow(targetPlayer);
				}
			} else if ("reload".equals(args[0])) {
				if (!player.hasPermission("tpbow.reload")) {
					player.sendMessage(colorize(Settings.NO_PERM));
					return true;
				}
				Settings.init();
				player.sendMessage(colorize(Settings.RELOAD));
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
			if (sender.hasPermission("tpbow.give")) {
				suggestions.add("give");
			}
		} else if (args.length == 2) {
			if (sender.hasPermission("tpbow.give")) {
				suggestions.addAll(TeleportBow.getInstance().getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
			}
		}
		return suggestions;
	}
}
