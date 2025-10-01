package me.luucka.teleportbow.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.luucka.teleportbow.util.Color.colorize;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Debug {

	private static final String PREFIX = "&7&l[ &5&lDEBUG &7&l] &r&7";

	public static void debug(final Player player, final String message) {
		player.sendMessage(colorize(PREFIX + message));
	}

	public static void debug(final String message) {
		Bukkit.getConsoleSender().sendMessage(colorize(PREFIX + message));
	}

}
