package me.luucka.teleportbow.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.setting.Settings;
import org.bukkit.entity.Player;

import java.util.List;

import static me.luucka.teleportbow.util.Color.colorize;

public class TpBowCommand {

	public static LiteralCommandNode<CommandSourceStack> build() {
		return Commands.literal("tpbow")
				.executes(ctx -> {
					if (!(ctx.getSource().getSender() instanceof Player player)) {
						ctx.getSource().getSender().sendMessage(colorize(Settings.NO_CONSOLE));
						return 1;
					}
					showUsage(player);
					return 1;
				})
				.then(Commands.literal("give")
						.requires(source -> source.getSender().hasPermission("tpbow.give"))
						.executes(ctx -> {
							if (!(ctx.getSource().getSender() instanceof Player player)) {
								ctx.getSource().getSender().sendMessage(colorize(Settings.NO_CONSOLE));
								return 1;
							}
							BowManager.giveBow(player);
							return 1;
						})
						.then(Commands.argument("player", ArgumentTypes.player())
								.executes(ctx -> {
									PlayerSelectorArgumentResolver resolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
									List<Player> players = resolver.resolve(ctx.getSource());
									if (players.isEmpty()) {
										ctx.getSource().getSender().sendMessage(colorize(Settings.PLAYER_NOT_FOUND.replace("{player}", "unknown")));
										return 1;
									}
									Player target = players.get(0);
									BowManager.giveBow(target);
									return 1;
								})
						)
				)
				.then(Commands.literal("reload")
						.requires(source -> source.getSender().hasPermission("tpbow.reload"))
						.executes(ctx -> {
							if (!(ctx.getSource().getSender() instanceof Player player)) {
								ctx.getSource().getSender().sendMessage(colorize(Settings.NO_CONSOLE));
								return 1;
							}
							Settings.reload();
							player.sendMessage(colorize(Settings.RELOAD));
							return 1;
						})
				)
				.build();

	}

	private static void showUsage(final Player player) {
		boolean hasGivePermission = player.hasPermission("tpbow.give");
		boolean hasReloadPermission = player.hasPermission("tpbow.reload");

		if (hasGivePermission || hasReloadPermission) {
			player.sendMessage(colorize(Settings.USAGE));
			if (hasGivePermission) {
				player.sendMessage(colorize("&7/tpbow give [player] - Give special bow to a player, or yourself"));
			}
			if (hasReloadPermission) {
				player.sendMessage(colorize("&7/tpbow reload - Reload the plugin"));
			}
		}
	}
}
