package me.luucka.teleportbow.command;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.Permissions;
import me.luucka.teleportbow.TeleportBow;
import me.luucka.teleportbow.setting.Settings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.luucka.teleportbow.util.Color.colorize;

public class TpBowCommand implements TabExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Settings.NO_CONSOLE);
			return true;
		}
		final Player player = (Player) sender;

		if (args.length == 0) {
			showUsage(player);
		} else {
			if ("give".equals(args[0])) {
				if (!player.hasPermission(Permissions.GIVE)) {
					player.sendMessage(Settings.NO_PERM);
					return true;
				}

				if (args.length == 1) {
					BowManager.giveBow(player);
				} else {
					Player targetPlayer = TeleportBow.getInstance().getServer().getPlayer(args[1]);
					if (targetPlayer == null) {
						player.sendMessage(Settings.PLAYER_NOT_FOUND.replace("{player}", args[1]));
						return true;
					}
					BowManager.giveBow(targetPlayer);
				}
			} else if ("reload".equals(args[0])) {
				if (!player.hasPermission(Permissions.RELOAD)) {
					player.sendMessage(Settings.NO_PERM);
					return true;
				}
				Settings.reload();
				player.sendMessage(Settings.RELOAD);
			} else {
				showUsage(player);
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> suggestions = new ArrayList<>();
		if (args.length == 1) {
			if (sender.hasPermission(Permissions.RELOAD)) {
				suggestions.add("reload");
			}
			if (sender.hasPermission(Permissions.GIVE)) {
				suggestions.add("give");
			}
		} else if (args.length == 2) {
			if (sender.hasPermission(Permissions.GIVE)) {
				suggestions.addAll(TeleportBow.getInstance().getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
			}
		}
		return suggestions;
	}

	private void showUsage(final Player player) {
		boolean hasGivePermission = player.hasPermission(Permissions.GIVE);
		boolean hasReloadPermission = player.hasPermission(Permissions.RELOAD);

		if (hasGivePermission || hasReloadPermission) {
			player.sendMessage(Settings.USAGE);
			if (hasGivePermission) {
				player.sendMessage(colorize("&7/tpbow give [player] - Give special bow to a player, or yourself"));
			}
			if (hasReloadPermission) {
				player.sendMessage(colorize("&7/tpbow reload - Reload the plugin"));
			}
		}
	}
}
