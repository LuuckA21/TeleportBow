package me.luucka.teleportbow.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Color {

	public static Component colorize(final String input) {
		return MiniMessage.miniMessage().deserialize(input);
	}

	public static List<Component> colorize(List<String> inputs) {
		return inputs.stream()
				.map(Color::colorize)
				.collect(Collectors.toList());
	}

}
