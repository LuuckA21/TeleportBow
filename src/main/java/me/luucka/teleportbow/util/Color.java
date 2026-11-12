package me.luucka.teleportbow.util;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Color {

	private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

	private Color() {
	}

	public static String colorize(final String input) {
		String text = input;
		if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_16)) {
			final Matcher matcher = HEX_PATTERN.matcher(text);
			// StringBuffer: the StringBuilder overload of appendReplacement needs Java 9
			final StringBuffer buffer = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(buffer, Matcher.quoteReplacement(ChatColor.of(matcher.group()).toString()));
			}
			matcher.appendTail(buffer);
			text = buffer.toString();
		}
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	public static List<String> colorize(final List<String> inputs) {
		return inputs.stream()
				.map(Color::colorize)
				.collect(Collectors.toList());
	}

}
